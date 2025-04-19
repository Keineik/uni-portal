package iss.kienephongthuyfvix.uniportal.controller.DBA;

import iss.kienephongthuyfvix.uniportal.dao.ObjectDao;
import iss.kienephongthuyfvix.uniportal.dao.RoleDao;
import iss.kienephongthuyfvix.uniportal.model.Privilege;
import iss.kienephongthuyfvix.uniportal.model.Role;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;



public class QuanLyRole {
    private ObservableList<Privilege> privilegeList = FXCollections.observableArrayList();

    @FXML
    private TextField searchField;

    @FXML
    private TableView<Role> roleListView;

    @FXML
    private TableColumn<Role, String> roleNameColumn;

    @FXML
    private TableColumn<Role, Void> actionColumn;

    @FXML
    private Button createRoleButton;

    private final RoleDao roleDao = new RoleDao();
    private final ObservableList<Role> roleObservableList = FXCollections.observableArrayList();
    private ObjectDao objectDao;


    @FXML
    public void initialize() throws SQLException {
        if (roleListView != null) {
            roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
            addActionColumn();
            loadRoleList();

            Tooltip tooltip = new Tooltip("Double click để xem chi tiết");
            Tooltip.install(roleListView, tooltip);

            tooltip.setShowDelay(Duration.millis(10));
            tooltip.setHideDelay(Duration.millis(300));

            roleListView.setRowFactory(tv -> {
                TableRow<Role> row = new TableRow<>();
                row.setOnMouseClicked(event -> {
                    if (event.getClickCount() == 2 && !row.isEmpty()) {
                        Role selectedRole = row.getItem();
                        showPrivilegesForRole(selectedRole);
                    }
                });
                return row;
            });

        }

        if (searchField != null) {
            searchField.setOnKeyReleased(this::handleSearch);
        } else {
            System.err.println("searchField is not initialized.");
        }

        if (createRoleButton != null) {
            createRoleButton.setOnAction(event -> openCreateRole());
        }
    }

    private void addActionColumn() {
        actionColumn.setCellFactory(new Callback<TableColumn<Role, Void>, TableCell<Role, Void>>() {
            @Override
            public TableCell<Role, Void> call(final TableColumn<Role, Void> param) {
                return new TableCell<Role, Void>() {
                    private final Button editButton = new Button();
                    private final Button deleteButton = new Button();
                    private final HBox actionBox = new HBox(2, editButton, deleteButton);

                    {
                        editButton.setGraphic(new FontIcon("fas-pen"));
                        editButton.getStyleClass().add("action-button");
                        editButton.setOnAction(e -> {
//                            System.out.println("Danh sách Role trong bảng:");
//                            getTableView().getItems().forEach(System.out::println);

                            Role role = getTableView().getItems().get(getIndex());
                            openEditRoleDialog(role);
                        });

                        deleteButton.setGraphic(new FontIcon("fas-trash"));
                        deleteButton.getStyleClass().add("action-button");
                        deleteButton.setOnAction(e -> {
                            Role role = getTableView().getItems().get(getIndex());
                            handleDeleteRole(role);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (getIndex() >= 0 && getIndex() < getTableView().getItems().size()) {
                            setGraphic(actionBox);
                        } else {
                            setGraphic(null);
                        }
                    }
                };
            }
        });
    }

    private void showPrivilegesForRole(Role role) {
        try {
            List<Privilege> privileges = roleDao.getPrivilegesByRole(role.getRoleName());
            if (privileges.isEmpty()) {
                showAlert("Thông báo", "Role này không có quyền nào.", Alert.AlertType.INFORMATION);
                return;
            }

            showAllPrivilegesPopup(privileges, role.getRoleName());
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Lỗi", "Không thể tải danh sách quyền: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAllPrivilegesPopup(List<Privilege> privileges, String roleName) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(roleListView.getScene().getWindow());

        TableView<Privilege> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Privilege, String> objectCol = new TableColumn<>("Object");
        objectCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getObject()));

        TableColumn<Privilege, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));

        TableColumn<Privilege, String> privilegesCol = new TableColumn<>("Privileges");
        privilegesCol.setCellValueFactory(data -> new SimpleStringProperty(String.join(", ", data.getValue().getPrivileges())));

        TableColumn<Privilege, String> updateColsCol = new TableColumn<>("Update Columns");
        updateColsCol.setCellValueFactory(data -> new SimpleStringProperty(String.join(", ", data.getValue().getUpdateColumns())));

        table.getColumns().addAll(objectCol, typeCol, privilegesCol, updateColsCol);
        table.setItems(FXCollections.observableArrayList(privileges));

        VBox vbox = new VBox(10, table);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 700, 400);
        popupStage.setScene(scene);
        popupStage.setTitle("Chi tiết quyền của role: " + roleName);
        popupStage.show();
    }


    @FXML
    public void openCreateRole() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/create-role.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Create Role");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            // Reload the role list after the dialog is closed
            loadRoleList();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error opening the Create Role dialog: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadRoleList() throws SQLException {
        List<Role> roles = roleDao.getAllRoles();
        roleObservableList.setAll(roles);
        roleListView.setItems(roleObservableList);
    }

    private void handleSearch(KeyEvent event) {
        String searchText = searchField.getText().toLowerCase();
        ObservableList<Role> filteredRoles = FXCollections.observableArrayList();

        for (Role role : roleObservableList) {
            if (role.getRoleName().toLowerCase().contains(searchText)) {
                filteredRoles.add(role);
            }
        }

        roleListView.setItems(filteredRoles);
    }


    private void handleDeleteRole(Role role) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Xác nhận xóa");
        confirmationDialog.setHeaderText("Bạn có chắc muốn xóa role này?");
        confirmationDialog.setContentText("Role: " + role.getRoleName());

        ButtonType yesButton = new ButtonType("Có", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Không", ButtonBar.ButtonData.NO);

        confirmationDialog.getButtonTypes().setAll(yesButton, noButton);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    roleDao.deleteRole(role);
                    loadRoleList();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting role: " + e.getMessage());
                    alert.showAndWait();
                }
            } else {
                System.out.println("Hủy xóa role: " + role.getRoleName());
            }
        });
    }

    public void openEditRoleDialog(Role roleToEdit) {
//        System.out.println("roleToEdit: " + (roleToEdit == null ? "null" : roleToEdit.getRoleName()));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/edit-role.fxml"));
            Parent root = loader.load();

            EditRole controller = loader.getController();
//            System.out.println("Controller instance: " + loader.getController());
            controller.setRoleToEdit(roleToEdit);

            Stage stage = new Stage();
            stage.setTitle(roleToEdit == null ? "Create Role" : "Edit Role");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.showAndWait();

            loadRoleList();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Error opening the Create Role dialog: " + e.getMessage(), Alert.AlertType.ERROR);
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
