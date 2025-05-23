package iss.kienephongthuyfvix.uniportal.controller.NVCTSV;

import iss.kienephongthuyfvix.uniportal.dao.DonViDAO;
import iss.kienephongthuyfvix.uniportal.dao.SinhVienDAO;
import iss.kienephongthuyfvix.uniportal.model.DonVi;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class SinhVienDialog {

    @FXML
    private Button cancelButton;

    @FXML
    private TextField dChiField;

    @FXML
    private TextField dTField;

    @FXML
    private TextField hoTenField;

    @FXML
    private ComboBox<String> khoaCombo;

    @FXML
    private TextField maSVField;

    @FXML
    private DatePicker ngSinhPicker;

    @FXML
    private ComboBox<String> phaiCombo;

    @FXML
    private Button saveButton;

    @FXML
    private TextField tinhTrangField;

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final DonViDAO donViDAO = new DonViDAO();

    private boolean isEditMode = false;

    public void setSinhVien(SinhVien sinhVien) {
        this.isEditMode = true;

        // Populate fields with existing student data
        maSVField.setText(sinhVien.getMaSV());
        hoTenField.setText(sinhVien.getHoTen());
        phaiCombo.setValue(sinhVien.getPhai());
        dChiField.setText(sinhVien.getDiaChi());
        dTField.setText(sinhVien.getDienThoai());

        // Convert Date to LocalDate for DatePicker
        if (sinhVien.getNgaySinh() != null) {
            LocalDate localDate = new java.sql.Date(sinhVien.getNgaySinh().getTime()).toLocalDate();
            ngSinhPicker.setValue(localDate);
        }

        // Set combobox values
        khoaCombo.setValue(sinhVien.getKhoa());
        tinhTrangField.setText(sinhVien.getTinhTrang());

        // Disable student ID field in edit mode
        maSVField.setEditable(false);
        maSVField.setStyle("-fx-background-color: #f0f0f0; -fx-text-fill: #606060;");
    }

    @FXML
    public void initialize() {
        // Initialize ComboBox values
        initializeComboBoxes();

        // Set up button actions
        saveButton.setOnAction(e -> saveSinhVien());
        cancelButton.setOnAction(e -> closeDialog());
    }

    private void initializeComboBoxes() {
        // Set up gender options
        phaiCombo.setItems(FXCollections.observableArrayList("Nam", "Nữ"));

        // Load faculty options from a database
        try {
            List<DonVi> donViList = donViDAO.getAllDonVi();
            ObservableList<String> khoaItems = FXCollections.observableArrayList();

            for (DonVi donVi : donViList) {
                if ("Khoa".equals(donVi.getLoaiDV())) {
                    khoaItems.add(donVi.getMaDV());
                }
            }

            khoaCombo.setItems(khoaItems);
        } catch (SQLException e) {
            showErrorAlert("Lỗi", "Không thể tải dữ liệu khoa: " + e.getMessage());
        }
    }

    @FXML
    private void saveSinhVien() {
        // Validate input
        if (!validateInput()) {
            return;
        }

        try {
            // Get values from the form
            String maSV = maSVField.getText().trim();
            String hoTen = hoTenField.getText().trim();
            String phai = phaiCombo.getValue();
            LocalDate ngSinh = ngSinhPicker.getValue();
            String diaChi = dChiField.getText().trim();
            String dienThoai = dTField.getText().trim();
            String khoa = khoaCombo.getValue();
            String tinhTrang = tinhTrangField.getText();

            // Convert LocalDate to Date for a database
            Date ngaySinh = null;
            if (ngSinh != null) {
                ngaySinh = Date.from(ngSinh.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            if (isEditMode) {
                // Update existing student
                sinhVienDAO.updateSinhVien(new SinhVien(
                        maSV, hoTen, phai, ngaySinh, diaChi, dienThoai, khoa, tinhTrang, null));
                showInfoAlert("Cập nhật thông tin sinh viên thành công!");
            } else {
                // Add new student
                sinhVienDAO.insertSinhVien(new SinhVien(
                        null, hoTen, phai, ngaySinh, diaChi, dienThoai, khoa, tinhTrang, null));
                showInfoAlert("Thêm sinh viên mới thành công!");
            }

            closeDialog();
        } catch (SQLException e) {
            showErrorAlert("Lỗi cơ sở dữ liệu", "Không thể lưu thông tin sinh viên: " + e.getMessage());
        }
    }

    private boolean validateInput() {
        StringBuilder errorMessages = new StringBuilder();

        if (hoTenField.getText() == null || hoTenField.getText().trim().isEmpty()) {
            errorMessages.append("- Họ tên không được để trống\n");
        }

        if (phaiCombo.getValue() == null) {
            errorMessages.append("- Vui lòng chọn phái\n");
        }

        if (ngSinhPicker.getValue() == null) {
            errorMessages.append("- Ngày sinh không được để trống\n");
        }

        if (khoaCombo.getValue() == null) {
            errorMessages.append("- Vui lòng chọn khoa\n");
        }

        // Phone number validation - must be numeric
        String phone = dTField.getText().trim();
        if (!phone.isEmpty() && !phone.matches("\\d+")) {
            errorMessages.append("- Số điện thoại chỉ được chứa số\n");
        }

        if (!errorMessages.isEmpty()) {
            showErrorAlert("Lỗi dữ liệu", "Vui lòng kiểm tra các lỗi sau:\n" + errorMessages.toString());
            return false;
        }

        return true;
    }

    @FXML
    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Thành công");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}