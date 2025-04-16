package iss.kienephongthuyfvix.uniportal.controller.NVPDT;

import com.jfoenix.controls.JFXComboBox;
import iss.kienephongthuyfvix.uniportal.dao.DangKyDAO;
import iss.kienephongthuyfvix.uniportal.dao.MoMonDAO;
import iss.kienephongthuyfvix.uniportal.model.DangKy;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.util.List;

@Slf4j
public class DangKyDialog {

    @FXML
    private Button cancelButton;

    @FXML
    private TextField maSVField;

    @FXML
    private JFXComboBox<Pair<String, String>> hocPhanCombo;

    @FXML
    private Button saveButton;

    private final MoMonDAO moMonDAO = new MoMonDAO();
    private final DangKyDAO dangKyDAO = new DangKyDAO();

    private DangKy currentDangKy;

    public void setDangKy(DangKy dangKy) {
        this.currentDangKy = dangKy;

        if (dangKy != null) {
            maSVField.setText(dangKy.getMaSV());
            hocPhanCombo.setValue(new Pair<>(String.valueOf(dangKy.getMaMM()), dangKy.getTenHP()));
            maSVField.setEditable(false);
            maSVField.setStyle("-fx-background-color: transparent; -fx-text-fill: black;");
        }
    }

    @FXML
    public void initialize() {
        saveButton.setOnAction(e -> saveDangKy());
        cancelButton.setOnAction(e -> cancelAction());
        try {
            // Load list of HocPhan (MaHP and TenHP) into the ComboBox
            List<Pair<String, String>> hocPhanList = moMonDAO.getListHocPhan();
            hocPhanCombo.setItems(FXCollections.observableArrayList(hocPhanList));

            // Set a custom StringConverter to display TenHP in the ComboBox
            hocPhanCombo.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(Pair<String, String> pair) {
                    return pair == null ? "" : pair.getValue(); // Display TenHP
                }

                @Override
                public Pair<String, String> fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            showErrorAlert("Error", "Failed to load HocPhan data: " + e.getMessage());
        }
    }

    @FXML
    private void saveDangKy() {
        String maSV = maSVField.getText();
        Pair<String, String> selectedHocPhan = hocPhanCombo.getValue();

        if (maSV == null || maSV.isEmpty()) {
            showErrorAlert("Lỗi dữ liệu", "Mã sinh viên không thể trống.");
            return;
        }

        if (selectedHocPhan == null) {
            showErrorAlert("Lỗi dữ liệu", "Hãy chọn 1 học phần.");
            return;
        }

        try {
            if (currentDangKy == null) {
                log.info("Insert DangKy, MaSV: {}, MaHP: {}", maSV, selectedHocPhan.getKey());
                dangKyDAO.insertDangKy(maSV, Integer.parseInt(selectedHocPhan.getKey()));
                showInfoAlert("Thành công", "Đăng ký học phần thành công!");
            } else {
                log.info("Update DangKy, MaSV: {}, MaHP: {}", maSV, selectedHocPhan.getKey());
                dangKyDAO.deleteDangKy(maSV, currentDangKy.getMaMM());
                dangKyDAO.insertDangKy(maSV, Integer.parseInt(selectedHocPhan.getKey()));
                currentDangKy.setMaMM(Integer.parseInt(selectedHocPhan.getKey()));
                showInfoAlert("Thành công", "Cập nhật đăng ký học phần thành công!");
            }
            closeDialog();
        } catch (SQLException e) {
            showErrorAlert("Lỗi", "Failed to save registration: " + e.getMessage());
        }
    }

    @FXML
    private void cancelAction() {
        closeDialog();
    }

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

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}