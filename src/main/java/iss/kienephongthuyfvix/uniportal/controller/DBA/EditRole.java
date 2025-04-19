package iss.kienephongthuyfvix.uniportal.controller.DBA;

import com.jfoenix.controls.JFXCheckBox;
import iss.kienephongthuyfvix.uniportal.dao.Database;
import iss.kienephongthuyfvix.uniportal.dao.ObjectDao;
import iss.kienephongthuyfvix.uniportal.dao.RoleDao;
import iss.kienephongthuyfvix.uniportal.model.Privilege;
import iss.kienephongthuyfvix.uniportal.model.Role;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class EditRole {

    private Connection connection;
    private RoleDao roleDao;
    private ObjectDao objectDao;
    private Role roleToEdit;

    @FXML private TextField roleNameField;
    @FXML private CheckBox selectCheckBox;
    @FXML private CheckBox insertCheckBox;
    @FXML private CheckBox updateCheckBox;
    @FXML private CheckBox deleteCheckBox;
    @FXML private CheckBox executeCheckBox;
    @FXML private AnchorPane updateColumnPermissionPane;
    @FXML private ChoiceBox<String> objectTypeChoiceBox;
    @FXML private ChoiceBox<String> objectNameChoiceBox;
    @FXML private TableView<Privilege> privilegesTable;
    @FXML private TableColumn<Privilege, String> objectColumn;
    @FXML private TableColumn<Privilege, String> typeColumn;
    @FXML private TableColumn<Privilege, String> privilegesColumn;
    @FXML private TableColumn<Privilege, String> actionsColumn;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ObservableList<Privilege> privilegeList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            connection = Database.getConnection();
            roleDao = new RoleDao();
            objectDao = new ObjectDao(connection);
        } catch (SQLException e) {
            showAlert("Error", "Could not connect to database: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        // Set actions for buttons
        saveButton.setOnAction(event -> saveRole());
        cancelButton.setOnAction(event -> closeDialog());

        executeCheckBox.setVisible(false);
        executeCheckBox.setManaged(false);

        updateColumnPermissionPane.setVisible(false);
        updateColumnPermissionPane.managedProperty().bind(updateColumnPermissionPane.visibleProperty());


        updateCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> updateColumnPermissionVisibility());

        objectNameChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (updateCheckBox.isSelected()) {
                loadColumnsForTable(newVal);
            }
        });

        loadDataIntoChoiceBoxes();

        objectTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isProcedureOrFunction = "PROCEDURE".equalsIgnoreCase(newVal) || "FUNCTION".equalsIgnoreCase(newVal);
            selectCheckBox.setVisible(!isProcedureOrFunction);
            insertCheckBox.setVisible(!isProcedureOrFunction);
            updateCheckBox.setVisible(!isProcedureOrFunction);
            deleteCheckBox.setVisible(!isProcedureOrFunction);

            executeCheckBox.setVisible(isProcedureOrFunction);
            executeCheckBox.setManaged(isProcedureOrFunction);

            if (!isProcedureOrFunction) {
                executeCheckBox.setSelected(false);
            }
        });


        // Set up table columns for privileges
        objectColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getObject()));
        typeColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));
        privilegesColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDisplayPrivileges()));
        actionsColumn.setCellFactory(column -> new TableCell<Privilege, String>() {
            private final Button deleteButton = new Button();

            {
                FontIcon trashIcon = new FontIcon("fas-trash");
                trashIcon.setIconSize(12);
                deleteButton.setGraphic(trashIcon);
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                // Handle delete button click
                deleteButton.setOnAction(event -> {
                    Privilege privilege = getTableView().getItems().get(getIndex());

                    Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
                    confirmDialog.setTitle("Xác nhận xóa quyền");
                    confirmDialog.setHeaderText("Bạn có chắc chắn muốn REVOKE quyền này?");
                    confirmDialog.setContentText(privilege.toString());

                    Optional<ButtonType> result = confirmDialog.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            revokePrivilegeFromDB(privilege);

                            privilegeList.remove(privilege);
                            privilegesTable.setItems(FXCollections.observableArrayList(privilegeList));
                            System.out.println("Đã REVOKE và xóa quyền: " + privilege);
                        } catch (SQLException e) {
                            showAlert("Lỗi", "Không thể revoke quyền: " + e.getMessage(), Alert.AlertType.ERROR);
                        }
                    } else {
                        System.out.println("Hủy revoke quyền.");
                    }
                });

            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= privilegeList.size()) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });
        Tooltip tooltip = new Tooltip("Double click để xem chi tiết");
        Tooltip.install(privilegesTable, tooltip);

        tooltip.setShowDelay(Duration.millis(10));
        tooltip.setHideDelay(Duration.millis(300));
        // Add double-click event listener to open popup with privilege details
        privilegesTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {  // Double-click on a row
                Privilege selectedPrivilege = privilegesTable.getSelectionModel().getSelectedItem();
                if (selectedPrivilege != null) {
                    showDetailPopup(selectedPrivilege);  // Show popup with details
                }
            }
        });
    }

    private void loadRoleData() {
        if (roleToEdit != null){
            String roleName = roleToEdit.getRoleName();
            roleNameField.setText(roleName);

            // Load privileges for the role
            try {
                List<Privilege> privileges = roleDao.getPrivilegesByRole(roleName);
                privilegeList.addAll(privileges);
                privilegesTable.setItems(FXCollections.observableArrayList(privilegeList));
            } catch (SQLException e) {
                showAlert("Error", "Could not load role privileges: " + e.getMessage(), Alert.AlertType.ERROR);
            }

            // Load available object types and names
            loadObjectData();
        }

    }

    private void loadObjectData() {
        objectTypeChoiceBox.getItems().clear();
        objectTypeChoiceBox.getItems().addAll(objectDao.getObjectTypes());

        objectTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadObjectNames(newVal);
        });
    }

    private void loadObjectNames(String objectType) {
        try {
            List<String> objectNames = objectDao.getObjectNames(objectType);
            objectNameChoiceBox.getItems().clear();
            objectNameChoiceBox.getItems().addAll(objectNames);
        } catch (SQLException e) {
            showAlert("Error", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void addPrivilege() {
        String object = objectNameChoiceBox.getValue();
        String type = objectTypeChoiceBox.getValue();
        List<String> privileges = new ArrayList<>();
        List<String> grantOptions = new ArrayList<>();
        List<String> updateColumns = new ArrayList<>();

        // Validate that object and type are not null or empty
        if (object == null || object.isBlank() || type == null || type.isBlank()) {
            showAlert("Lỗi", "Hãy chọn đối tượng và loại đối tượng trước khi thêm quyền.", Alert.AlertType.ERROR);
            return;
        }

        // Check SELECT permission and grant option
        if (selectCheckBox.isSelected()) {
            privileges.add("SELECT");
        }

        // Check INSERT permission and grant option
        if (insertCheckBox.isSelected()) {
            privileges.add("INSERT");
        }

        // Check UPDATE permission and grant option
        if (updateCheckBox.isSelected()) {
            updateColumns = getSelectedColumns(updateColumnPermissionPane);
            System.out.println("Selected columns for UPDATE: " + updateColumns);

            privileges.add("UPDATE");
        }

        // Check DELETE permission and grant option
        if (deleteCheckBox.isSelected()) {
            privileges.add("DELETE");
        }

        // Check EXECUTE permission and grant option
        if (executeCheckBox.isVisible() && executeCheckBox.isSelected()) {
            privileges.add("EXECUTE");
        }

        // Show warning if no privilege is selected
        if (privileges.isEmpty()) {
            showAlert("Thông báo", "Hãy chọn ít nhất một quyền.", Alert.AlertType.WARNING);
            return;
        }

        // Create a Privilege object and add it to the privilegeList
        Privilege privilege = new Privilege(object, type, privileges, grantOptions, updateColumns);
        privilegeList.add(privilege);

        privilegesTable.setItems(FXCollections.observableArrayList(privilegeList));
        clearPrivilegeSelections();
    }

    private List<String> getSelectedColumns(AnchorPane pane) {
        List<String> selected = new ArrayList<>();
        collectSelectedColumns(pane, selected);
        return selected;
    }

    private void collectSelectedColumns(javafx.scene.Parent parent, List<String> selected) {
        for (javafx.scene.Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof JFXCheckBox checkBox && checkBox.isSelected()) {
                selected.add(checkBox.getText());
            } else if (node instanceof javafx.scene.Parent childParent) {
                collectSelectedColumns(childParent, selected);
            }
        }
    }

    private void clearPrivilegeSelections() {
        objectNameChoiceBox.setValue(null);
        objectTypeChoiceBox.setValue(null);

        selectCheckBox.setSelected(false);

        insertCheckBox.setSelected(false);

        updateCheckBox.setSelected(false);

        for (Node node : updateColumnPermissionPane.getChildren()) {
            if (node instanceof JFXCheckBox cb) {
                cb.setSelected(false);
            }
        }

        deleteCheckBox.setSelected(false);

        executeCheckBox.setSelected(false);

        updateColumnPermissionPane.setVisible(false);
    }

    private void updateColumnPermissionVisibility() {
        boolean showUpdate = updateCheckBox.isSelected();
        updateColumnPermissionPane.setVisible(showUpdate);

        if (showUpdate) {
            String selectedTable = objectNameChoiceBox.getValue();
            if (selectedTable != null && !selectedTable.isEmpty()) {
                loadColumnsForTable(selectedTable);
            }
        }
    }


    private void loadColumnsForTable(String tableName) {
        updateColumnPermissionPane.getChildren().clear();

        try {
            if (tableName == null || tableName.trim().isEmpty()) {
                System.out.println("Table name is null or empty");
                return;
            }
            List<String> columns = objectDao.getTableColumns(tableName);

            Label updateLabel = new Label("Phân quyền UPDATE theo cột");
            updateLabel.setLayoutX(10);
            updateLabel.setLayoutY(9);
            updateLabel.setStyle("-fx-text-fill: #0000008c; -fx-font-weight: bold;");
            updateColumnPermissionPane.getChildren().add(updateLabel);

            GridPane gridPane = new GridPane();
            gridPane.setLayoutX(16);
            gridPane.setLayoutY(33);
            gridPane.setHgap(20); // Khoảng cách ngang giữa các cột
            gridPane.setVgap(10); // Khoảng cách dọc giữa các hàng

            int column = 0;
            int row = 0;

            for (String col : columns) {
                JFXCheckBox updateBox = new JFXCheckBox(col);
                gridPane.add(updateBox, column, row);

                column++;
                if (column >= 3) {
                    column = 0;
                    row++;
                }
            }

            updateColumnPermissionPane.getChildren().add(gridPane);

        } catch (SQLException e) {
            showAlert("Lỗi khi tải danh sách cột", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadDataIntoChoiceBoxes() {
        objectTypeChoiceBox.getItems().clear();
        objectTypeChoiceBox.getItems().addAll(objectDao.getObjectTypes());

        objectTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            loadObjectNames(newVal);
        });

    }

    @FXML
    private void saveRole() {
        String roleName = roleNameField.getText().trim();

        // Ensure role name is not empty
        if (roleName.isEmpty()) {
            showAlert("Error", "Role name cannot be empty", Alert.AlertType.ERROR);
            return;
        }

        // Try to establish a connection and handle exceptions
        try (Connection conn = Database.getConnection()) {

            // Escape the role name for SQL query
            String escapedRoleName = "\"" + roleToEdit.getRoleName().toUpperCase() + "\"";

            for (Privilege privilege : privilegeList) {
                String objectName = privilege.getObject();

                // ====== CẤP QUYỀN MỚI (GRANT) ======
                grantNewPrivileges(conn, privilege, escapedRoleName, objectName);
            }

            // Success message
            showAlert("Success", "Chỉnh sửa role thành công!", Alert.AlertType.INFORMATION);
            closeDialog();

        } catch (SQLException e) {
            showAlert("Error", "Error updating role: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    private void grantNewPrivileges(Connection conn, Privilege privilege, String escapedRoleName, String objectName) throws SQLException {
        // Grant new privileges that were not previously granted
        for (String priv : privilege.getPrivileges()) {
            if (privilege.getOriginalPrivileges() != null &&
                    privilege.getOriginalPrivileges().contains(priv)) {
                // Skip if the privilege was already granted
                continue;
            }

            String columns = "";
            if ("UPDATE".equalsIgnoreCase(priv) && privilege.getUpdateColumns() != null && !privilege.getUpdateColumns().isEmpty()) {
                columns = "(" + String.join(", ", privilege.getUpdateColumns()) + ")";
            }

            boolean withGrant = privilege.getWithGrantOption() != null && privilege.getWithGrantOption().contains(priv);

            String grantSQL = "GRANT " + priv +
                    (columns.isEmpty() ? "" : " " + columns) +
                    " ON " + objectName +
                    " TO " + escapedRoleName +
                    (withGrant ? " WITH GRANT OPTION" : "");

            System.out.println("GRANT SQL: " + grantSQL);

            try (PreparedStatement grantStmt = conn.prepareStatement(grantSQL)) {
                grantStmt.execute();
            }
        }
    }

    private void revokePrivilegeFromDB(Privilege privilege) throws SQLException {
        String privilegeStr = privilege.getPrivileges().stream().map(String::toUpperCase).collect(Collectors.joining(", "));
        String sql = String.format(
                "REVOKE %s ON %s FROM %s",
                privilegeStr,
                privilege.getType().equalsIgnoreCase("TABLE") || privilege.getType().equalsIgnoreCase("VIEW") ?
                        privilege.getObject() :
                        privilege.getType() + " " + privilege.getObject(),
                roleNameField.getText().trim()
        );

        System.out.println("Executing SQL: " + sql);
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate(sql);
        }
    }


    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showDetailPopup(Privilege privilege) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(saveButton.getScene().getWindow());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        grid.add(createLabel("Object:"), 0, 0);
        grid.add(new Label(privilege.getObject()), 1, 0);

        grid.add(createLabel("Type:"), 0, 1);
        grid.add(new Label(privilege.getType()), 1, 1);

        grid.add(createLabel("Privileges:"), 0, 2);
        grid.add(new Label(String.join(", ", privilege.getPrivileges())), 1, 2);

        grid.add(createLabel("Update Columns:"), 0, 3);
        grid.add(new Label(String.join(", ", privilege.getUpdateColumns())), 1, 3);

        Scene popupScene = new Scene(grid, 350, 250);
        popupStage.setScene(popupScene);
        popupStage.setTitle("Chi tiết quyền");
        popupStage.show();
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-font-weight: bold;");
        return label;
    }

    public void setRoleToEdit(Role role) {
        this.roleToEdit = role;
        loadRoleData();
    }
}