package iss.kienephongthuyfvix.uniportal.controller.NVPKT;

import iss.kienephongthuyfvix.uniportal.dao.DangKyDAO;
import iss.kienephongthuyfvix.uniportal.model.DangKy;
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

public class QLDiem {

    @FXML
    private TableColumn<DangKy, Void> actionsColumn;

    @FXML
    private TableView<DangKy> dangKyListView;

    @FXML
    private TableColumn<DangKy, Double> diemCKColumn;

    @FXML
    private TableColumn<DangKy, Double> diemQTColumn;

    @FXML
    private TableColumn<DangKy, Double> diemTHColumn;

    @FXML
    private TableColumn<DangKy, Double> diemTKColumn;

    @FXML
    private ComboBox<String> hkCombo;

    @FXML
    private TableColumn<DangKy, String> maHPColumn;

    @FXML
    private TextField maHPSearch;

    @FXML
    private TableColumn<DangKy, Integer> maMMColumn;

    @FXML
    private TableColumn<DangKy, String> maSVColumn;

    @FXML
    private TextField maSVSearch;

    @FXML
    private ComboBox<String> namCombo;

    @FXML
    private TableColumn<DangKy, String> tenHPColumn;

    private final ObservableList<DangKy> dangKyData = FXCollections.observableArrayList();
    private final DangKyDAO dangKyDAO = new DangKyDAO();

    @FXML
    public void initialize() {
        // Initialize table columns
        maSVColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getMaSV()));
        maMMColumn.setCellValueFactory(data -> data.getValue().maMMProperty().asObject());
        maHPColumn.setCellValueFactory(data -> data.getValue().maHPProperty());
        tenHPColumn.setCellValueFactory(data -> data.getValue().tenHPProperty());
        diemTHColumn.setCellValueFactory(data -> data.getValue().diemTHProperty().asObject());
        diemQTColumn.setCellValueFactory(data -> data.getValue().diemQTProperty().asObject());
        diemCKColumn.setCellValueFactory(data -> data.getValue().diemThiProperty().asObject());
        diemTKColumn.setCellValueFactory(data -> data.getValue().diemTKProperty().asObject());

        // Load data into the table
        loadDangKyData();

        // Add action buttons to the action column
        addActionButtons();

        // Set up filtering
        setupFilters();
    }

    private void loadDangKyData() {
        dangKyData.clear();
        try {
            dangKyData.addAll(dangKyDAO.getAllDangKyWithHocPhanDetails());
            dangKyListView.setItems(dangKyData);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading data from the database: " + e.getMessage());
        }
    }

    private void setupFilters() {
        hkCombo.getItems().addAll("Học kì", "1", "2", "3");
        hkCombo.setValue("Học kì");
        namCombo.getItems().addAll("Năm", "2022", "2023", "2024", "2025");
        namCombo.setValue("Năm");

        maSVSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        maHPSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        hkCombo.valueProperty().addListener((observable, oldValue, newValue) -> filterTable());
        namCombo.valueProperty().addListener((observable, oldValue, newValue) -> filterTable());
    }

    private void filterTable() {
        String maSVKeyword = maSVSearch.getText().toLowerCase();
        String maHPKeyword = maHPSearch.getText().toLowerCase();
        String selectedHK = hkCombo.getValue();
        String selectedNam = namCombo.getValue();

        ObservableList<DangKy> filteredList = FXCollections.observableArrayList();
        for (DangKy dangKy : dangKyData) {
            boolean matchesMaSV = dangKy.getMaSV().toLowerCase().contains(maSVKeyword);
            boolean matchesMaHP = dangKy.getMaHP().toLowerCase().contains(maHPKeyword);
            boolean matchesHK = selectedHK == null || selectedHK.equals("Học kì") || String.valueOf(dangKy.getHk()).equals(selectedHK);
            boolean matchesNam = selectedNam == null || selectedNam.equals("Năm") || String.valueOf(dangKy.getNam()).equals(selectedNam);

            if (matchesMaSV && matchesMaHP && matchesHK && matchesNam) {
                filteredList.add(dangKy);
            }
        }
        dangKyListView.setItems(filteredList);
    }

    private void addActionButtons() {
        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button();
            private final HBox actionBox = new HBox(5, editButton);

            {
                // Edit button
                FontIcon editIcon = new FontIcon("fas-pen");
                editIcon.setIconColor(Color.BLACK);
                editIcon.setIconSize(14);
                editButton.setGraphic(editIcon);
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editButton.setOnAction(event -> {
                    DangKy dangKy = getTableView().getItems().get(getIndex());
                    openDiemDialog(dangKy);
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

    private void openDiemDialog(DangKy dangKy) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVPKT/diem-dialog.fxml"));
            Parent root = loader.load();

            DiemDialog controller = loader.getController();
            controller.setDangKy(dangKy);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Chỉnh Sửa Điểm");
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