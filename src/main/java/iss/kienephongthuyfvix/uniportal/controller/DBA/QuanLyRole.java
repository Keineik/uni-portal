package iss.kienephongthuyfvix.uniportal.controller.DBA;

import com.jfoenix.controls.JFXCheckBox;
import iss.kienephongthuyfvix.uniportal.dao.Database;
import iss.kienephongthuyfvix.uniportal.dao.ObjectDao;
import iss.kienephongthuyfvix.uniportal.dao.RoleDao;
import iss.kienephongthuyfvix.uniportal.model.Privilege;
import iss.kienephongthuyfvix.uniportal.model.Role;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
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
    public void initialize() {
        if (roleListView != null) {
            roleNameColumn.setCellValueFactory(new PropertyValueFactory<>("roleName"));
            addActionColumn();
            try {
                loadRoleList();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (searchField != null) {
            searchField.setOnKeyReleased(event -> {
                System.out.println("Key released: " + event.getText());
            });
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
    private void openEditRoleDialog(Role role) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/edit-role.fxml"));
            Parent root = loader.load();

            TextField roleNameField = (TextField) root.lookup("#roleNameField");
            Button saveButton = (Button) root.lookup("#saveButton");
            Button cancelButton = (Button) root.lookup("#cancelButton");

            roleNameField.setText(role.getRoleName());

            saveButton.setOnAction(e -> {
                role.setRoleName(roleNameField.getText());

                try {
                    roleDao.updateRole(role);
                    loadRoleList();
                    Stage stage = (Stage) root.getScene().getWindow();
                    stage.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating role: " + ex.getMessage());
                    alert.showAndWait();
                }
            });

            cancelButton.setOnAction(e -> {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chỉnh sửa Role");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error loading the edit role dialog: " + e.getMessage());
            e.printStackTrace();
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
