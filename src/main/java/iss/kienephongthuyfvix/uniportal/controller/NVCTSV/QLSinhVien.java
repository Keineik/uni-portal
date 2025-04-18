package iss.kienephongthuyfvix.uniportal.controller.NVCTSV;

import iss.kienephongthuyfvix.uniportal.dao.DonViDAO;
import iss.kienephongthuyfvix.uniportal.dao.SinhVienDAO;
import iss.kienephongthuyfvix.uniportal.model.DonVi;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

public class QLSinhVien {

    @FXML
    private TableColumn<SinhVien, Void> actionsColumn;

    @FXML
    private Button addButton;

    @FXML
    private TableColumn<SinhVien, String> coSoColumn;

    @FXML
    private TableColumn<SinhVien, String> dChiColumn;

    @FXML
    private TableColumn<SinhVien, String> dTColumn;

    @FXML
    private TableColumn<SinhVien, String> hoTenColumn;

    @FXML
    private TextField hoTenSearch;

    @FXML
    private TableColumn<SinhVien, String> khoaColumn;

    @FXML
    private ComboBox<String> khoaCombo;

    @FXML
    private TableColumn<SinhVien, String> maSVColumn;

    @FXML
    private TextField maSVSearch;

    @FXML
    private TableColumn<SinhVien, Date> ngSinhColumn;

    @FXML
    private TableColumn<SinhVien, String> phaiColumn;

    @FXML
    private TableView<SinhVien> sinhvienListView;

    @FXML
    private TableColumn<SinhVien, String> tinhTrangColumn;

    private final ObservableList<SinhVien> sinhVienData = FXCollections.observableArrayList();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final DonViDAO donViDAO = new DonViDAO();

    @FXML
    public void initialize() {
        // Initialize table columns
        maSVColumn.setCellValueFactory(data -> data.getValue().maSVProperty());
        hoTenColumn.setCellValueFactory(data -> data.getValue().hoTenProperty());
        ngSinhColumn.setCellValueFactory(data -> data.getValue().ngaySinhProperty());
        phaiColumn.setCellValueFactory(data -> data.getValue().phaiProperty());
        dChiColumn.setCellValueFactory(data -> data.getValue().diaChiProperty());
        dTColumn.setCellValueFactory(data -> data.getValue().dienThoaiProperty());
        khoaColumn.setCellValueFactory(data -> data.getValue().khoaProperty());
        tinhTrangColumn.setCellValueFactory(data -> data.getValue().tinhTrangProperty());
        // coSoColumn.setCellValueFactory(data -> data.getValue().coSoProperty());

        // Load data into the table
        loadSinhVienData();

        // Add action buttons to the actions column
        addActionButtons();

        // Set up filtering
        setupFilters();

        // Set up add button handler
        addButton.setOnAction(event -> openSinhVienDialog(null));
    }

    private void loadSinhVienData() {
        sinhVienData.clear();
        try {
            sinhVienData.addAll(sinhVienDAO.getAllSinhVien());
            sinhvienListView.setItems(sinhVienData);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading data from the database: " + e.getMessage());
        }
    }

    private void setupFilters() {
        // Initialize khoa combo box
        try {
            khoaCombo.getItems().add("Tất cả khoa");
            khoaCombo.getItems().addAll(donViDAO.getAllDonVi().stream()
                    .filter(donVi -> {return Objects.equals(donVi.getLoaiDV(), "Khoa");})
                    .map(DonVi::getMaDV)
                    .toList());
            khoaCombo.setValue("Tất cả khoa");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Set up listeners for search fields
        maSVSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        hoTenSearch.textProperty().addListener((observable, oldValue, newValue) -> filterTable());
        khoaCombo.valueProperty().addListener((observable, oldValue, newValue) -> filterTable());
    }

    private void filterTable() {
        String maSVKeyword = maSVSearch.getText().toLowerCase();
        String hoTenKeyword = hoTenSearch.getText().toLowerCase();
        String selectedKhoa = khoaCombo.getValue();

        ObservableList<SinhVien> filteredList = FXCollections.observableArrayList();
        for (SinhVien sinhVien : sinhVienData) {
            boolean matchesMaSV = sinhVien.getMaSV().toLowerCase().contains(maSVKeyword);
            boolean matchesHoTen = sinhVien.getHoTen().toLowerCase().contains(hoTenKeyword);
            boolean matchesKhoa = selectedKhoa == null || selectedKhoa.equals("Tất cả khoa") || sinhVien.getKhoa().equals(selectedKhoa);

            if (matchesMaSV && matchesHoTen && matchesKhoa) {
                filteredList.add(sinhVien);
            }
        }
        sinhvienListView.setItems(filteredList);
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
                    SinhVien sinhVien = getTableView().getItems().get(getIndex());
                    openSinhVienDialog(sinhVien);
                });

                // Delete button
                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconColor(Color.RED);
                deleteIcon.setIconSize(14);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setOnAction(event -> {
                    SinhVien sinhVien = getTableView().getItems().get(getIndex());
                    deleteSinhVien(sinhVien);
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

    private void openSinhVienDialog(SinhVien sinhVien) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVCTSV/sinhvien-dialog.fxml"));
            Parent root = loader.load();

            SinhVienDialog controller = loader.getController();
            if (sinhVien != null) {
                controller.setSinhVien(sinhVien);
            }

            Stage dialogStage = new Stage();
            dialogStage.setTitle(sinhVien == null ? "Thêm Sinh Viên" : "Chỉnh Sửa Sinh Viên");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            loadSinhVienData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteSinhVien(SinhVien sinhVien) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setHeaderText("Xóa sinh viên");
        confirmDialog.setContentText("Bạn có chắc chắn muốn xóa sinh viên " + sinhVien.getHoTen() + "?");

        confirmDialog.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    sinhVienDAO.deleteSinhVien(sinhVien.getMaSV());
                    loadSinhVienData(); // Refresh
                } catch (SQLException e) {
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                    errorAlert.setTitle("Lỗi");
                    errorAlert.setHeaderText("Lỗi khi xóa sinh viên");
                    errorAlert.setContentText("Không thể xóa sinh viên. Lỗi: " + e.getMessage());
                    errorAlert.showAndWait();
                    e.printStackTrace();
                }
            }
        });
    }
}