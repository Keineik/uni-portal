package iss.kienephongthuyfvix.uniportal.controller.DBA;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;

public class QuanLyRole {

    @FXML
    private Button createRoleButton;

    @FXML
    private TextField searchField;

    @FXML
    private TableView<RoleModel> roleListView;

    @FXML
    private TableColumn<RoleModel, String> roleNameColumn;

    @FXML
    private TableColumn<RoleModel, Object> actionColumn;

    private ObservableList<RoleModel> roleData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
//        setupCreateRoleButton();
        roleNameColumn.setCellValueFactory(cellData -> cellData.getValue().roleNameProperty());

        roleListView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        roleNameColumn.setMaxWidth(1f * Integer.MAX_VALUE * 90);
        actionColumn.setMaxWidth(1f * Integer.MAX_VALUE * 10);

        loadRoleData();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterRoleList(newValue);
        });

        addActionButtons();
    }

    private void loadRoleData() {
        roleData.clear();
        roleData.addAll(
                new RoleModel("ROLE_GIANGVIEN"),
                new RoleModel("ROLE_SINHVIEN"),
                new RoleModel("ROLE_NV_PDT")
        );
        roleListView.setItems(roleData);
    }

    private void filterRoleList(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            roleListView.setItems(roleData);
            addActionButtons();
            return;
        }

        ObservableList<RoleModel> filteredList = FXCollections.observableArrayList();
        for (RoleModel role : roleData) {
            if (role.getRoleName().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(role);
            }
        }

        roleListView.setItems(filteredList);
        addActionButtons();
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox actionBox = new HBox(5, editButton, deleteButton);

            {
                // Icon edit
                FontIcon editIcon = new FontIcon("fas-pen");
                editIcon.setIconColor(Color.BLACK);
                editIcon.setIconSize(14);
                editButton.setGraphic(editIcon);
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editButton.setOnAction(event -> {
                    RoleModel role = getTableView().getItems().get(getIndex());
                    System.out.println("Sửa role: " + role.getRoleName());
                    // TODO: mở hộp thoại chỉnh sửa
                });

                // Icon delete
                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconColor(Color.BLACK);
                deleteIcon.setIconSize(14);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setOnAction(event -> {
                    RoleModel role = getTableView().getItems().get(getIndex());
                    deleteRole(role);
                });

                actionBox.setStyle("-fx-alignment: CENTER;");
            }

            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void deleteRole(RoleModel role) {
        roleData.remove(role);
        roleListView.setItems(roleData);
        System.out.println("Đã xoá role: " + role.getRoleName());

    }

    private void setupCreateRoleButton() {
        if (createRoleButton != null) {
            createRoleButton.setOnAction(e -> onCreateRole());
        }
    }

    @FXML
    void onCreateRole() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/create-role.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Tạo Role Mới");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static class RoleModel {
        private final StringProperty roleName;

        public RoleModel(String roleName) {
            this.roleName = new SimpleStringProperty(roleName);
        }

        public String getRoleName() {
            return roleName.get();
        }

        public StringProperty roleNameProperty() {
            return roleName;
        }
    }
}
