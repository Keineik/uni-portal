package iss.kienephongthuyfvix.uniportal.controller.NVTCHC;

import iss.kienephongthuyfvix.uniportal.dao.NhanVienDAO;
import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;

public class QLNhanVien {

    @FXML
    private TableView<NhanVien> tableNhanVien;

    @FXML
    private TableColumn<NhanVien, String> manvColumn, hotenColumn, phaiColumn, ngsinhColumn, dienthoaiColumn, vaitroColumn, madvColumn;

    @FXML
    private TableColumn<NhanVien, Void> actionsColumn;

    @FXML
    private TextField searchField;

    @FXML
    private Button themnvButton;

    @FXML
    private TableColumn<NhanVien, Double> luongColumn, phucapColumn;

    private final ObservableList<NhanVien> nhanVienData = FXCollections.observableArrayList();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    @FXML
    public void initialize() {
        // Initialize table columns
        manvColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getManv()));
        hotenColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getHoten()));
        phaiColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPhai()));
        ngsinhColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNgsinh().toString()));
        dienthoaiColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDt()));
        vaitroColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getVaitro()));
        madvColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMadv()));
        luongColumn.setCellValueFactory(data -> data.getValue().luongProperty().asObject());
        phucapColumn.setCellValueFactory(data -> data.getValue().phucapProperty().asObject());

        // Load data into the table
        loadNhanVienData();

        themnvButton.setOnAction(this::themNhanVien);

        // Add action buttons to the action column
        addActionButtons();

        // Set up search functionality
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterTable(newValue));
    }

    private void loadNhanVienData() {
        nhanVienData.clear();
        try {
            nhanVienData.addAll(nhanVienDAO.getAllNhanVien());
            tableNhanVien.setItems(nhanVienData);
        } catch (SQLException e) {
            System.out.println("Error loading data from the database: " + e.getMessage());
        }
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            tableNhanVien.setItems(nhanVienData);
            return;
        }

        ObservableList<NhanVien> filteredList = FXCollections.observableArrayList();
        for (NhanVien nv : nhanVienData) {
            if (nv.getHoten().toLowerCase().contains(keyword.toLowerCase()) ||
                    nv.getManv().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(nv);
            }
        }
        tableNhanVien.setItems(filteredList);
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox actionBox = new HBox(5, editButton, deleteButton);

            {
                // Edit button
                FontIcon editIcon = new FontIcon("fas-pen");
                editIcon.setIconColor(Color.BLACK);
                editIcon.setIconSize(14);
                editButton.setGraphic(editIcon);
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editButton.setOnAction(event -> {
                    NhanVien nv = getTableView().getItems().get(getIndex());
                    openNhanVienDialog(nv);
                });

                // Delete button
                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconColor(Color.BLACK);
                deleteIcon.setIconSize(14);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setOnAction(event -> {
                    NhanVien nv = getTableView().getItems().get(getIndex());
                    deleteNhanVien(nv);
                });

                actionBox.setStyle("-fx-alignment: CENTER;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void deleteNhanVien(NhanVien nv) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Xác nhận xóa");
        confirmationDialog.setHeaderText("Bạn có chắc muốn xóa nhân viên này?");
        confirmationDialog.setContentText("Mã nhân viên: " + nv.getManv() + ", Họ tên: " + nv.getHoten());

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        confirmationDialog.getButtonTypes().setAll(yesButton, noButton);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    nhanVienDAO.deleteNhanVien(nv.getManv());
                    nhanVienData.remove(nv);
                } catch (SQLException e) {
                    System.out.println("Error deleting data from the database: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    void themNhanVien(ActionEvent event) {
        openNhanVienDialog(null);
    }

    private void openNhanVienDialog(NhanVien nv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVTCHC/nhan-vien-dialog.fxml"));
            Parent root = loader.load();

            NhanVienDialog controller = loader.getController();
            controller.setNhanVien(nv);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(nv == null ? "Thêm Nhân Viên" : "Chỉnh Sửa Nhân Viên");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Reload data after the dialog is closed
            loadNhanVienData();
        } catch (IOException e) {
            Alert errorDialog = new Alert(Alert.AlertType.ERROR);
            errorDialog.setTitle("Lỗi");
            errorDialog.setHeaderText("Không thể mở cửa sổ chỉnh sửa nhân viên");
            errorDialog.setContentText("Lỗi: " + e.getMessage());
            errorDialog.showAndWait();
        }
    }

}