package iss.kienephongthuyfvix.uniportal.controller.NVPDT;

import iss.kienephongthuyfvix.uniportal.dao.DangKyDAO;
import iss.kienephongthuyfvix.uniportal.model.DangKy;
import javafx.beans.property.SimpleIntegerProperty;
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

public class QLDangKy {

    @FXML
    private TableColumn<DangKy, Void> actionsColumn;

    @FXML
    private TableView<DangKy> dangKyListView;

    @FXML
    private TableColumn<DangKy, String> maHPColumn;

    @FXML
    private TableColumn<DangKy, String> maSVColumn;

    @FXML
    private TextField searchBar;

    @FXML
    private TableColumn<DangKy, Integer> soTCColumn;

    @FXML
    private TableColumn<DangKy, Integer> stltColumn;

    @FXML
    private TableColumn<DangKy, Integer> stthColumn;

    @FXML
    private TableColumn<DangKy, String> tenHPColumn;

    private final ObservableList<DangKy> dangKyData = FXCollections.observableArrayList();
    private final DangKyDAO dangKyDAO = new DangKyDAO();

    @FXML
    public void initialize() {
        // Initialize table columns
        maSVColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaSV()));
        maHPColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaMM() + ""));
        tenHPColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTenHP()));
        soTCColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSoTC()).asObject());
        stltColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStLT()).asObject());
        stthColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getStTH()).asObject());

        // Load data into the table
        loadDangKyData();

        // Add action buttons to the action column
        addActionButtons();

        // Set up search functionality
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> filterTable(newValue));
    }

    private void loadDangKyData() {
        dangKyData.clear();
        try {
            dangKyData.addAll(dangKyDAO.getAllDangKyWithHocPhanDetailsNVPDT());
            dangKyListView.setItems(dangKyData);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading data from the database: " + e.getMessage());
        }
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            dangKyListView.setItems(dangKyData);
            return;
        }

        ObservableList<DangKy> filteredList = FXCollections.observableArrayList();
        for (DangKy dangKy : dangKyData) {
            if (dangKy.getMaSV().toLowerCase().contains(keyword.toLowerCase()) ||
                    dangKy.getTenHP().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(dangKy);
            }
        }
        dangKyListView.setItems(filteredList);
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
                    DangKy dangKy = getTableView().getItems().get(getIndex());
                    openDangKyDialog(dangKy);
                });

                // Delete button
                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconColor(Color.BLACK);
                deleteIcon.setIconSize(14);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setOnAction(event -> {
                    DangKy dangKy = getTableView().getItems().get(getIndex());
                    deleteDangKy(dangKy);
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

    private void deleteDangKy(DangKy dangKy) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Xác nhận xóa");
        confirmationDialog.setHeaderText("Bạn có chắc muốn xóa đăng ký này?");
        confirmationDialog.setContentText("Mã sinh viên: " + dangKy.getMaSV() + ", Mã học phần: " + dangKy.getMaMM());

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        confirmationDialog.getButtonTypes().setAll(yesButton, noButton);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    dangKyDAO.deleteDangKy(dangKy.getMaSV(), dangKy.getMaMM());
                    dangKyData.remove(dangKy);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error deleting data from the database: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    void themDangKy(ActionEvent event) {
        openDangKyDialog(null);
    }

    private void openDangKyDialog(DangKy dangKy) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVPDT/dang-ky-dialog.fxml"));
            Parent root = loader.load();

            DangKyDialog controller = loader.getController();
            controller.setDangKy(dangKy);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(dangKy == null ? "Thêm Đăng Ký" : "Chỉnh Sửa Đăng Ký");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            // Reload data after dialog is closed
            loadDangKyData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}