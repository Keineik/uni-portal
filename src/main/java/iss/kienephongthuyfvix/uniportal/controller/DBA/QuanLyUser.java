package iss.kienephongthuyfvix.uniportal.controller.DBA;

import iss.kienephongthuyfvix.uniportal.dao.UserDao;
import iss.kienephongthuyfvix.uniportal.model.Privilege;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;
import iss.kienephongthuyfvix.uniportal.model.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

public class QuanLyUser {

    @FXML private TableView<User> userListView;
    @FXML private TableColumn<User, String> userNameColumn;
    @FXML private TableColumn<User, String> roleColumn;
    @FXML private TableColumn<User, Void> actionColumn;
    @FXML private Button createUserButton;

    @FXML private TextField usernameField;
    @FXML private ListView<String> multiRoleListView;
    @FXML private Button createButton;
    @FXML private Button cancelButton;
    @FXML private TextField searchField;

    private ObservableList<User> masterUserList = FXCollections.observableArrayList();
    private FilteredList<User> filteredUsers;
    private UserDao userDao;

    public QuanLyUser() {
        this.userDao = new UserDao();
    }

    @FXML
    public void initialize() {
        initUserTableIfAvailable();
        Tooltip tooltip = new Tooltip("Double click để xem và thu hồi quyền");
        Tooltip.install(userListView, tooltip);

        tooltip.setShowDelay(Duration.millis(10));
        tooltip.setHideDelay(Duration.millis(300));

        setupCreateUserButton();
        setupCreateUserForm();
        if (searchField != null) {
            searchField.textProperty().addListener((observable, oldValue, newValue) -> {
                String lowerCaseFilter = newValue.toLowerCase();

                filteredUsers.setPredicate(user -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }

                    return user.getUsername().toLowerCase().contains(lowerCaseFilter)
                            || user.getRoles().stream().anyMatch(role -> role.toLowerCase().contains(lowerCaseFilter));
                });
            });

        }
    }

    private void initUserTableIfAvailable() {
        if ( userNameColumn != null && roleColumn != null && userListView != null) {
            userNameColumn.setCellValueFactory(data -> data.getValue().usernameProperty());
            roleColumn.setCellValueFactory(data -> {
                String roles = String.join(", ", data.getValue().getRoles());
                return new SimpleStringProperty(roles);
            });

            addActionColumn();

            // Fetch users from database
            try {
                List<User> users = userDao.getAllUsers();
                users.sort(Comparator.comparing(User::getUsername));
                masterUserList.setAll(users);
            } catch (SQLException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error fetching users from database: " + e.getMessage());
                alert.showAndWait();
            }

            filteredUsers = new FilteredList<>(masterUserList, p -> true);
            userListView.setItems(filteredUsers);

//            userListView.setRowFactory(tv -> {
//                TableRow<User> row = new TableRow<>();
//                Tooltip tooltip = new Tooltip("Double click để xem chi tiết");
//                row.setTooltip(tooltip);
//                return row;
//            });


            userListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // Double click
                    User selectedUser = userListView.getSelectionModel().getSelectedItem();
                    if (selectedUser != null) {
                        try {
                            showAllPrivilegesForUser(selectedUser.getUsername());
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

        }
    }

    private void setupCreateUserButton() {
        if (createUserButton != null) {
            createUserButton.setOnAction(e -> openCreateUserDialog());
        }
    }

    private void setupCreateUserForm() {
        if ( multiRoleListView != null && createButton != null && cancelButton != null) {
            multiRoleListView.setItems(FXCollections.observableArrayList("RL_NVCB","RL_GV" ,"RL_NV_PDT" ,"RL_NV_PKT" ,"RL_NV_TCHC" ,"RL_NV_CTSV" ,"RL_TRGDV" ,"RL_SV"));

            cancelButton.setOnAction(e -> {
                Stage stage = (Stage) cancelButton.getScene().getWindow();
                stage.close();
            });
        }
    }

    private void addActionColumn() {
        actionColumn.setCellFactory(new Callback<>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<>() {
                    private final Button editButton = new Button();
                    private final Button deleteButton = new Button();
                    private final HBox actionBox = new HBox(2, editButton, deleteButton);

                    {
                        editButton.setGraphic(new FontIcon("fas-pen"));
                        editButton.getStyleClass().add("action-button");
                        editButton.setOnAction(e -> {
                            User user = getTableView().getItems().get(getIndex());
                            openEditUserDialog(user);
                        });

                        deleteButton.setGraphic(new FontIcon("fas-trash"));
                        deleteButton.getStyleClass().add("action-button");
                        deleteButton.setOnAction(e -> {
                            User user = getTableView().getItems().get(getIndex());
                            deleteUser(user);
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

    private void openCreateUserDialog() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/create-user.fxml"));
            Parent root = loader.load();

            TextField usernameField = (TextField) root.lookup("#usernameField");
            ListView<String> multiRoleListView = (ListView<String>) root.lookup("#multiRoleListView");
            Button createButton = (Button) root.lookup("#createButton");
            Button cancelButton = (Button) root.lookup("#cancelButton");

            ObservableList<String> allRoles = FXCollections.observableArrayList("RL_NVCB","RL_GV" ,"RL_NV_PDT" ,"RL_NV_PKT" ,"RL_NV_TCHC" ,"RL_NV_CTSV" ,"RL_TRGDV" ,"RL_SV");
            multiRoleListView.setItems(allRoles);

            Map<String, BooleanProperty> selectedRoleMap = new HashMap<>();
            for (String role : allRoles) {
                selectedRoleMap.put(role, new SimpleBooleanProperty(false));
            }

            multiRoleListView.setCellFactory(CheckBoxListCell.forListView(role -> selectedRoleMap.get(role)));

            createButton.setOnAction(e -> {
                String username = usernameField.getText();

                List<String> selectedRoles = selectedRoleMap.entrySet().stream()
                        .filter(entry -> entry.getValue().get())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList());

                System.out.println("Tạo user: " + username + " với các role: " + selectedRoles);

                if (username.isEmpty() || selectedRoles.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.WARNING, "Vui lòng nhập đầy đủ thông tin và chọn ít nhất một role.");
                    alert.showAndWait();
                    return;
                }

                ObservableList<User> userList = userListView.getItems();
                if (userList == null) {
                    userList = FXCollections.observableArrayList();
                    userListView.setItems(userList);
                }

                User newUser = new User(username, String.join(",", selectedRoles));
                try {
                    userDao.createUser(newUser);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }

                masterUserList.add(newUser);
//                filteredUsers.setAll(masterUserList);

                Stage stage = (Stage) createButton.getScene().getWindow();
                showAlert("Thành công", "Tạo user thành công", Alert.AlertType.INFORMATION);
                stage.close();
            });

            cancelButton.setOnAction(e -> {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Tạo User Mới");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Lỗi khi mở hộp thoại tạo user: " + e.getMessage());
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


    private void openEditUserDialog(User user) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/edit-user.fxml"));
            Parent root = loader.load();

            TextField usernameField = (TextField) root.lookup("#usernameField");
            Button saveButton = (Button) root.lookup("#saveButton");
            Button cancelButton = (Button) root.lookup("#cancelButton");
            ListView<String> roleListView = (ListView<String>) root.lookup("#roleListView");
            ChoiceBox<String> roleChoiceBox = (ChoiceBox<String>) root.lookup("#roleChoiceBox");
            PasswordField passwordField = (PasswordField) root.lookup("#passwordField");
            Button addRoleButton = (Button) root.lookup("#addRoleButton");

            usernameField.setText(user.getUsername());

            ObservableList<String> allRoles = FXCollections.observableArrayList("RL_NVCB","RL_GV" ,"RL_NV_PDT" ,"RL_NV_PKT" ,"RL_NV_TCHC" ,"RL_NV_CTSV" ,"RL_TRGDV" ,"RL_SV");
            roleChoiceBox.setItems(allRoles);
            roleChoiceBox.setValue(null);

            ObservableList<String> currentRoles = FXCollections.observableArrayList(user.getRoles());
            roleListView.setItems(currentRoles);

            roleListView.setCellFactory(lv -> new ListCell<>() {
                private final HBox content = new HBox(10);
                private final Label roleLabel = new Label();
                private final FontIcon trashIcon = new FontIcon("fas-trash");
                private final Button deleteButton = new Button();

                {
                    trashIcon.setIconSize(12);
                    trashIcon.setIconColor(Color.BLACK);

                    deleteButton.setGraphic(trashIcon);
                    deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");

                    deleteButton.setOnAction(event -> {
                        String role = getItem();
                        if (role != null) {
                            currentRoles.remove(role);
                        }
                    });

                    HBox.setHgrow(roleLabel, Priority.ALWAYS);
                    roleLabel.setMaxWidth(Double.MAX_VALUE);

                    content.getChildren().addAll(roleLabel, deleteButton);
                    content.setAlignment(Pos.CENTER_LEFT);
                }
//                @Override
//                protected void updateItem(String role, boolean empty) {
//                    super.updateItem(role, empty);
//                    if (empty || role == null) {
//                        setGraphic(null);
//                    } else {
//                        roleLabel.setText(String.join(", ", currentRoles));
//                        setGraphic(content);
//                    }
//                }

                @Override
                protected void updateItem(String role, boolean empty) {
                    super.updateItem(role, empty);
                    if (empty || role == null) {
                        setGraphic(null);
                    } else {
                        roleLabel.setText(role);
                        setGraphic(content);
                    }
                }
            });

            addRoleButton.setOnAction(e -> {
                String selectedRole = roleChoiceBox.getValue();
                if (selectedRole == null) {
                    showAlert("Cảnh báo", "Vui lòng chọn một role trước khi thêm!", Alert.AlertType.WARNING);
                    return;
                }
                if (!currentRoles.contains(selectedRole)) {
                    currentRoles.add(selectedRole);
                    System.out.println("Role added: " + selectedRole);
                }
            });

            saveButton.setOnAction(e -> {
                user.setUsername(usernameField.getText());
                user.getRoles().setAll(currentRoles);
                System.out.println("Saving user: " + user.getUsername());
                System.out.println("Roles to save: " + currentRoles);

                try {
                    userDao.updateUser(user);

                    String newPassword = passwordField.getText();
                    if (newPassword != null && !newPassword.isBlank()) {
                        userDao.changePassword(user.getUsername(), newPassword);
                        System.out.println("Password changed for user: " + user.getUsername());
                    }
                    userListView.refresh();
                    Stage stage = (Stage) root.getScene().getWindow();
                    showAlert("Thành công", "Chỉnh sửa user thành công", Alert.AlertType.INFORMATION);
                    stage.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error updating user: " + ex.getMessage());
                    alert.showAndWait();
                }
            });

            cancelButton.setOnAction(e -> {
                Stage stage = (Stage) root.getScene().getWindow();
                stage.close();
            });

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chỉnh sửa User");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

        } catch (IOException e) {
            System.err.println("Error loading the edit user dialog: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAllPrivilegesForUser(String username) throws SQLException {
        UserDao userDao = new UserDao();
        List<Privilege> privileges = userDao.getPrivilegesByUser(username);

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.WINDOW_MODAL);
        popupStage.initOwner(userListView.getScene().getWindow());

        TableView<Privilege> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Column: Object
        TableColumn<Privilege, String> objectCol = new TableColumn<>("Object");
        objectCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getObject()));

        // Column: Type
        TableColumn<Privilege, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getType()));

        // Column: Privileges
        TableColumn<Privilege, String> privilegesCol = new TableColumn<>("Privileges");
        privilegesCol.setCellValueFactory(data -> new SimpleStringProperty(
                String.join(", ", Optional.ofNullable(data.getValue().getPrivileges()).orElse(Collections.emptyList()))
        ));

        // Column: Update Columns
        TableColumn<Privilege, String> updateColsCol = new TableColumn<>("Update Columns");
        updateColsCol.setCellValueFactory(data -> new SimpleStringProperty(
                String.join(", ", Optional.ofNullable(data.getValue().getUpdateColumns()).orElse(Collections.emptyList()))
        ));

        // Column: Grantee (new column)
        TableColumn<Privilege, String> granteeCol = new TableColumn<>("Grantee");
        granteeCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGrantee()));

        // Column: Action (Revoke button)
        TableColumn<Privilege, Void> actionCol = new TableColumn<>("Action");
        actionCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Revoke");

            {
                deleteButton.setOnAction(event -> {
                    Privilege privilege = getTableView().getItems().get(getIndex());
                    try {
                        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
                        confirmAlert.setTitle("Xác nhận thu hồi");
                        confirmAlert.setHeaderText("Bạn có chắc chắn muốn thu hồi quyền này?");
                        confirmAlert.setContentText("Quyền: " + privilege.getPrivileges());

                        Optional<ButtonType> result = confirmAlert.showAndWait();
                        if (result.isPresent() && result.get() == ButtonType.OK) {
                            userDao.revokePrivilege(username, privilege);
                            getTableView().getItems().remove(privilege);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Lỗi khi thu hồi quyền: " + e.getMessage());
                        alert.showAndWait();
                    }
                });

            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Privilege privilege = getTableView().getItems().get(getIndex());

                    // Enable "Revoke" button if the grantee matches the username
                    if (privilege.getGrantee().equalsIgnoreCase(username)) {
                        deleteButton.setDisable(false);
                        deleteButton.setTooltip(null);
                        setGraphic(deleteButton);
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });

        // Add columns to the table
        table.getColumns().addAll(objectCol, typeCol, privilegesCol, updateColsCol, granteeCol, actionCol);

        // Populate the table with privileges
        table.setItems(FXCollections.observableArrayList(privileges));

        // Create layout and set scene
        VBox vbox = new VBox(10, table);
        vbox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(vbox, 750, 450);
        popupStage.setScene(scene);
        popupStage.setTitle("Privileges for user: " + username);
        popupStage.show();
    }


    private void deleteUser(User user) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Xác nhận xóa");
        confirmationDialog.setHeaderText("Bạn có chắc muốn xóa người dùng này?");
        confirmationDialog.setContentText("Username: " + user.getUsername());

        ButtonType yesButton = new ButtonType("Có", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("Không", ButtonBar.ButtonData.NO);

        confirmationDialog.getButtonTypes().setAll(yesButton, noButton);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    userDao.deleteUser(user);
                    masterUserList.remove(user);
                    System.out.println("Đã xóa user: " + user.getUsername());
                    showAlert("Thành công", "Xóa user thành công", Alert.AlertType.INFORMATION);
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR, "Error deleting user: " + ex.getMessage());
                    alert.showAndWait();
                }
            } else {
                System.out.println("Hủy xóa user: " + user.getUsername());
            }
        });
    }

}