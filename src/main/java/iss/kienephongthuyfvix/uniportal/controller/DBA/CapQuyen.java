package iss.kienephongthuyfvix.uniportal.controller.DBA;

import iss.kienephongthuyfvix.uniportal.dao.Database;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CapQuyen {

    private Connection connection;

    @FXML private ToggleGroup granteeTypeGroup;
    @FXML private RadioButton userRadio;
    @FXML private RadioButton roleRadio;

    @FXML private CheckBox selectCheckBox;
    @FXML private CheckBox insertCheckBox;
    @FXML private CheckBox updateCheckBox;
    @FXML private CheckBox deleteCheckBox;
    @FXML private CheckBox executeCheckBox;

    @FXML private CheckBox selectGrantOption;
    @FXML private CheckBox insertGrantOption;
    @FXML private CheckBox updateGrantOption;
    @FXML private CheckBox deleteGrantOption;
    @FXML private CheckBox executeGrantOption;

    @FXML private AnchorPane updateColumnPermissionPane;

    @FXML private ChoiceBox<String> granteeChoiceBox;
    @FXML private ChoiceBox<String> objectTypeChoiceBox;
    @FXML private ChoiceBox<String> objectNameChoiceBox;
    @FXML private Button grantButton;

    @FXML
    public void initialize() {

        try {
            connection = Database.getConnection();
        } catch (SQLException e) {
            showAlert("Lỗi kết nối", "Không thể kết nối tới CSDL: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        userRadio.setToggleGroup(granteeTypeGroup);
        roleRadio.setToggleGroup(granteeTypeGroup);

        updateColumnPermissionPane.setVisible(false);
        updateColumnPermissionPane.managedProperty().bind(updateColumnPermissionPane.visibleProperty());

        hideAllGrantOptions();

        bindGrantOptionVisibility(selectCheckBox, selectGrantOption);
        bindGrantOptionVisibility(insertCheckBox, insertGrantOption);
        bindGrantOptionVisibility(updateCheckBox, updateGrantOption);
        bindGrantOptionVisibility(deleteCheckBox, deleteGrantOption);
        bindGrantOptionVisibility(executeCheckBox, executeGrantOption);

        updateCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> updateColumnPermissionVisibility());

        objectNameChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (updateCheckBox.isSelected()) {
                loadColumnsForTable(newVal);
            }
        });

        loadDataIntoChoiceBoxes();

        userRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) loadDataIntoChoiceBoxes();
        });

        roleRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) loadDataIntoChoiceBoxes();
        });
    }

    private void bindGrantOptionVisibility(CheckBox mainCheckBox, CheckBox grantOptionCheckBox) {
        grantOptionCheckBox.setVisible(false);
        grantOptionCheckBox.managedProperty().bind(grantOptionCheckBox.visibleProperty());
        mainCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> grantOptionCheckBox.setVisible(newVal));
    }

    private void hideAllGrantOptions() {
        selectGrantOption.setVisible(false);
        insertGrantOption.setVisible(false);
        updateGrantOption.setVisible(false);
        deleteGrantOption.setVisible(false);
        executeGrantOption.setVisible(false);
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

    @FXML
    public void handleGrant() {
        try {
            if (connection == null) {
                showAlert("Lỗi kết nối", "Không thể kết nối đến cơ sở dữ liệu.", Alert.AlertType.ERROR);
                return;
            }

            Statement stmt = connection.createStatement();

            String grantee = granteeChoiceBox.getValue();
            String objectType = objectTypeChoiceBox.getValue();
            String objectName = objectNameChoiceBox.getValue();

            String fullObjectName = objectName;

            if (grantee == null || grantee.isEmpty() || objectName == null || objectName.isEmpty()) {
                showAlert("Lỗi", "Vui lòng chọn đầy đủ thông tin cấp quyền!", Alert.AlertType.ERROR);
                return;
            }

            List<String> permissionClauses = new ArrayList<>();
            boolean hasGrantOption = false;

            if (selectCheckBox.isSelected()) {
                permissionClauses.add("SELECT");
                if (selectGrantOption.isSelected()) hasGrantOption = true;
            }

            if (insertCheckBox.isSelected()) {
                permissionClauses.add("INSERT");
                if (insertGrantOption.isSelected()) hasGrantOption = true;
            }

            if (updateCheckBox.isSelected()) {
                List<String> updateColumns = getSelectedColumns(updateColumnPermissionPane);
                if (!updateColumns.isEmpty()) {
                    permissionClauses.add("UPDATE (" + String.join(", ", updateColumns) + ")");
                } else {
                    permissionClauses.add("UPDATE");
                }
                if (updateGrantOption.isSelected()) hasGrantOption = true;
            }

            if (deleteCheckBox.isSelected()) {
                permissionClauses.add("DELETE");
                if (deleteGrantOption.isSelected()) hasGrantOption = true;
            }

            if (executeCheckBox.isSelected()) {
                permissionClauses.add("EXECUTE");
                if (executeGrantOption.isSelected()) hasGrantOption = true;
            }

            if (permissionClauses.isEmpty()) {
                showAlert("Thông báo", "Vui lòng chọn ít nhất một quyền!", Alert.AlertType.WARNING);
                return;
            }

            String baseQuery = "GRANT " + String.join(", ", permissionClauses)
                    + " ON " + fullObjectName
                    + " TO " + grantee;

            if (hasGrantOption) {
                baseQuery += " WITH GRANT OPTION";
            }

            System.out.println("Executing SQL: " + baseQuery);
            stmt.executeUpdate(baseQuery);

            showAlert("Cấp quyền thành công", "Đã cấp quyền cho " + grantee, Alert.AlertType.INFORMATION);
        } catch (SQLException e) {
            showAlert("Lỗi khi cấp quyền", e.getMessage(), Alert.AlertType.ERROR);
        }
    }


    private List<String> getSelectedColumns(AnchorPane pane) {
        List<String> selected = new ArrayList<>();
        for (var node : pane.getChildren()) {
            if (node instanceof CheckBox checkBox && checkBox.isSelected()) {
                selected.add(checkBox.getText());
            }
        }
        return selected;
    }

    private void loadDataIntoChoiceBoxes() {
        try {
            Statement stmt = connection.createStatement();

            granteeChoiceBox.getItems().clear();
            if (userRadio.isSelected()) {
                String query = "SELECT DISTINCT GRANTEE FROM DBA_ROLE_PRIVS WHERE GRANTED_ROLE LIKE 'RL_%'";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    granteeChoiceBox.getItems().add(rs.getString("GRANTEE"));
                }
            } else if (roleRadio.isSelected()) {
                String query = "SELECT DISTINCT GRANTED_ROLE FROM DBA_ROLE_PRIVS WHERE GRANTED_ROLE LIKE 'RL_%'";
                ResultSet rs = stmt.executeQuery(query);
                while (rs.next()) {
                    granteeChoiceBox.getItems().add(rs.getString("GRANTED_ROLE"));
                }
            }

            objectTypeChoiceBox.getItems().clear();
            objectTypeChoiceBox.getItems().addAll("TABLE", "VIEW", "PROCEDURE", "FUNCTION");

            objectTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                loadObjectNames(newVal, stmt);
            });

        } catch (SQLException e) {
            showAlert("Lỗi khi tải dữ liệu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadObjectNames(String objectType, Statement stmt) {
        try {
            String query = "SELECT OBJECT_NAME FROM ALL_OBJECTS WHERE OWNER = 'QLDAIHOC' AND OBJECT_TYPE = ?";
            PreparedStatement ps = stmt.getConnection().prepareStatement(query);
            ps.setString(1, objectType);
            ResultSet rs = ps.executeQuery();

            objectNameChoiceBox.getItems().clear();
            while (rs.next()) {
                objectNameChoiceBox.getItems().add(rs.getString("OBJECT_NAME"));
            }
        } catch (SQLException e) {
            showAlert("Lỗi khi tải tên đối tượng", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadColumnsForTable(String tableName) {
        updateColumnPermissionPane.getChildren().clear();

        try (Connection connection = Database.getConnection()) {
            String query = "SELECT COLUMN_NAME FROM ALL_TAB_COLUMNS WHERE TABLE_NAME = ? AND OWNER = 'QLDAIHOC'";
            PreparedStatement ps = connection.prepareStatement(query);
            ps.setString(1, tableName.toUpperCase());
            ResultSet rs = ps.executeQuery();

            int y = 33;

            Label updateLabel = new Label("Phân quyền UPDATE theo cột");
            updateLabel.setLayoutX(10);
            updateLabel.setLayoutY(9);
            updateLabel.setStyle("-fx-text-fill: #0000008c; -fx-font-weight: bold;");
            updateColumnPermissionPane.getChildren().add(updateLabel);

            while (rs.next()) {
                String col = rs.getString("COLUMN_NAME");

                CheckBox updateBox = new CheckBox(col);
                updateBox.setLayoutX(16);
                updateBox.setLayoutY(y);
                updateColumnPermissionPane.getChildren().add(updateBox);

                y += 30;
            }

        } catch (SQLException e) {
            showAlert("Lỗi khi tải danh sách cột", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}