package iss.kienephongthuyfvix.uniportal.controller.DBA;

import com.jfoenix.controls.JFXCheckBox;
import iss.kienephongthuyfvix.uniportal.dao.Database;
import iss.kienephongthuyfvix.uniportal.dao.ObjectDao;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CapQuyen {

    private Connection connection;
    private ObjectDao objectDao;

    @FXML private ToggleGroup granteeTypeGroup;
    @FXML private RadioButton userRadio;
    @FXML private RadioButton roleRadio;

    @FXML private JFXCheckBox selectCheckBox;
    @FXML private JFXCheckBox insertCheckBox;
    @FXML private JFXCheckBox updateCheckBox;
    @FXML private JFXCheckBox deleteCheckBox;
    @FXML private JFXCheckBox executeCheckBox;

    @FXML private JFXCheckBox selectGrantOption;
    @FXML private JFXCheckBox insertGrantOption;
    @FXML private JFXCheckBox updateGrantOption;
    @FXML private JFXCheckBox deleteGrantOption;
    @FXML private JFXCheckBox executeGrantOption;

    @FXML private AnchorPane updateColumnPermissionPane;

    @FXML private ChoiceBox<String> granteeChoiceBox;
    @FXML private ChoiceBox<String> objectTypeChoiceBox;
    @FXML private ChoiceBox<String> objectNameChoiceBox;

    @FXML
    public void initialize() {
        try {
            connection = Database.getConnection();
            objectDao = new ObjectDao(connection); // Initialize ObjectDao
        } catch (SQLException e) {
            showAlert("Lỗi kết nối", "Không thể kết nối tới CSDL: " + e.getMessage(), Alert.AlertType.ERROR);
            return;
        }

        userRadio.setToggleGroup(granteeTypeGroup);
        roleRadio.setToggleGroup(granteeTypeGroup);

        updateColumnPermissionPane.setVisible(false);
        updateColumnPermissionPane.managedProperty().bind(updateColumnPermissionPane.visibleProperty());

        hideAllGrantOptions();

        updateCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> updateColumnPermissionVisibility());

        objectNameChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (updateCheckBox.isSelected()) {
                loadColumnsForTable(newVal);
            }
        });

        loadDataIntoChoiceBoxes();

        userRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                hideAllGrantOptions();
                loadDataIntoChoiceBoxes();

                bindGrantOptionVisibility(selectCheckBox, selectGrantOption);
                bindGrantOptionVisibility(insertCheckBox, insertGrantOption);
                bindGrantOptionVisibility(updateCheckBox, updateGrantOption);
                bindGrantOptionVisibility(deleteCheckBox, deleteGrantOption);
                bindGrantOptionVisibility(executeCheckBox, executeGrantOption);
            }
        });

        roleRadio.selectedProperty().addListener((observable, oldValue, newValue) -> {
            hideAllGrantOptions();
            if (newValue) {
                loadDataIntoChoiceBoxes();
                hideAllGrantOptions();
            }
        });

        objectTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean isProcedureOrFunction = "PROCEDURE".equalsIgnoreCase(newVal) || "FUNCTION".equalsIgnoreCase(newVal);
            executeCheckBox.setVisible(isProcedureOrFunction);
            executeCheckBox.setManaged(isProcedureOrFunction);

            if (!isProcedureOrFunction) {
                executeCheckBox.setSelected(false);
                executeGrantOption.setVisible(false);
            }
        });
    }

    private void bindGrantOptionVisibility(JFXCheckBox mainCheckBox, JFXCheckBox grantOptionCheckBox) {
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
        executeCheckBox.setVisible(false);
        executeCheckBox.setManaged(false);
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

            String grantee = granteeChoiceBox.getValue();
            String objectName = objectNameChoiceBox.getValue();

            if (grantee == null || grantee.isEmpty() || objectName == null || objectName.isEmpty()) {
                showAlert("Lỗi", "Vui lòng chọn đầy đủ thông tin cấp quyền!", Alert.AlertType.ERROR);
                return;
            }

            List<String> normalPermissions = new ArrayList<>();
            List<String> grantOptionPermissions = new ArrayList<>();

            if (selectCheckBox.isSelected()) {
                if (selectGrantOption.isSelected()) {
                    grantOptionPermissions.add("SELECT");
                } else {
                    normalPermissions.add("SELECT");
                }
            }

            // INSERT
            if (insertCheckBox.isSelected()) {
                if (insertGrantOption.isSelected()) {
                    grantOptionPermissions.add("INSERT");
                } else {
                    normalPermissions.add("INSERT");
                }
            }

            // UPDATE
            if (updateCheckBox.isSelected()) {
                List<String> updateColumns = getSelectedColumns(updateColumnPermissionPane);
                if (!updateColumns.isEmpty()) {
                    String grantSQL = buildUpdateColumnGrantSQL(updateColumns, objectName, grantee, updateGrantOption.isSelected());
                    try (PreparedStatement stmt = connection.prepareStatement(grantSQL)) {
                        stmt.executeUpdate();
                        System.out.println("Executing SQL: " + grantSQL);
                    }
                } else {
                    if (updateGrantOption.isSelected()) {
                        grantOptionPermissions.add("UPDATE");
                    } else {
                        normalPermissions.add("UPDATE");
                    }
                }
            }

            // DELETE
            if (deleteCheckBox.isSelected()) {
                if (deleteGrantOption.isSelected()) {
                    grantOptionPermissions.add("DELETE");
                } else {
                    normalPermissions.add("DELETE");
                }
            }

            // EXECUTE
            if (executeCheckBox.isSelected()) {
                if (executeGrantOption.isSelected()) {
                    grantOptionPermissions.add("EXECUTE");
                } else {
                    normalPermissions.add("EXECUTE");
                }
            }

            if (normalPermissions.isEmpty() && grantOptionPermissions.isEmpty()) {
                showAlert("Thông báo", "Vui lòng chọn ít nhất một quyền!", Alert.AlertType.WARNING);
                return;
            }

            if (!normalPermissions.isEmpty()) {
                String grantSQL = buildGrantSQL(normalPermissions, objectName, grantee, false);
                try (PreparedStatement stmt = connection.prepareStatement(grantSQL)) {
                    stmt.executeUpdate();
                    System.out.println("Executing SQL: " + grantSQL);
                }
            }

            if (!grantOptionPermissions.isEmpty()) {
                String grantSQL = buildGrantSQL(grantOptionPermissions, objectName, grantee, true);
                try (PreparedStatement stmt = connection.prepareStatement(grantSQL)) {
                    stmt.executeUpdate();
                    System.out.println("Executing SQL: " + grantSQL);
                }
            }

            showAlert("Cấp quyền thành công", "Đã cấp quyền cho " + grantee, Alert.AlertType.INFORMATION);
            handleCancel(); // clear the form after successful grant

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi khi cấp quyền", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private String buildGrantSQL(List<String> permissions, String objectName, String grantee, boolean withGrantOption) {
        String sql = "GRANT " + String.join(", ", permissions)
                + " ON " + objectName
                + " TO " + grantee;
        if (withGrantOption) {
            sql += " WITH GRANT OPTION";
        }
        return sql;
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


    private void loadDataIntoChoiceBoxes() {
        try {
            List<String> grantees = objectDao.getGrantees(userRadio.isSelected());
            granteeChoiceBox.getItems().clear();
            granteeChoiceBox.getItems().addAll(grantees);

            objectTypeChoiceBox.getItems().clear();
            objectTypeChoiceBox.getItems().addAll(objectDao.getObjectTypes());

            objectTypeChoiceBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
                loadObjectNames(newVal);
            });

        } catch (SQLException e) {
            showAlert("Lỗi khi tải dữ liệu", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadObjectNames(String objectType) {
        try {
            List<String> objectNames = objectDao.getObjectNames(objectType);
            objectNameChoiceBox.getItems().clear();
            objectNameChoiceBox.getItems().addAll(objectNames);
        } catch (SQLException e) {
            showAlert("Lỗi khi tải tên đối tượng", e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadColumnsForTable(String tableName) {
        updateColumnPermissionPane.getChildren().clear();

        try {
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

    private String buildUpdateColumnGrantSQL(List<String> columns, String objectName, String grantee, boolean withGrantOption) {
        String cols = String.join(", ", columns);
        StringBuilder sql = new StringBuilder();
        sql.append("GRANT UPDATE (").append(cols).append(") ON ").append(objectName)
                .append(" TO ").append(grantee);
        if (withGrantOption) {
            sql.append(" WITH GRANT OPTION");
        }
        return sql.toString();
    }


    @FXML
    public void handleCancel() {
        // Clear checkboxes
        selectCheckBox.setSelected(false);
        insertCheckBox.setSelected(false);
        updateCheckBox.setSelected(false);
        deleteCheckBox.setSelected(false);
        executeCheckBox.setSelected(false);

        // Clear WITH GRANT OPTION checkboxes
        selectGrantOption.setSelected(false);
        insertGrantOption.setSelected(false);
        updateGrantOption.setSelected(false);
        deleteGrantOption.setSelected(false);
        executeGrantOption.setSelected(false);

        // Clear choiceboxes
        granteeChoiceBox.getSelectionModel().clearSelection();
        objectTypeChoiceBox.getSelectionModel().clearSelection();
        objectNameChoiceBox.getSelectionModel().clearSelection();

        // Clear radio buttons
        granteeTypeGroup.selectToggle(null);

        // Clear update column permission pane
        updateColumnPermissionPane.setVisible(false);
        updateColumnPermissionPane.getChildren().clear();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}