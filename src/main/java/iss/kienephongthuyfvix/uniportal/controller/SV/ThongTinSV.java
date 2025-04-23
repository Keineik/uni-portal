package iss.kienephongthuyfvix.uniportal.controller.SV;

import iss.kienephongthuyfvix.uniportal.dao.SinhVienDAO;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class ThongTinSV {

    @FXML
    private TextField diachiField;

    @FXML
    private TextField dtField;

    @FXML
    private TextField hotenField;

    @FXML
    private TextField khoaField;

    @FXML
    private TextField masvField;

    @FXML
    private TextField ngsinhField;

    @FXML
    private TextField phaiField;

    @FXML
    private TextField tinhtrangField;

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private SinhVien sinhVien;

    @FXML
    private void initialize() throws SQLException {
        sinhVien = sinhVienDAO.getCurrentSinhVien();
        masvField.setText(sinhVien.getMaSV());
        hotenField.setText(sinhVien.getHoTen());
        ngsinhField.setText(sinhVien.getNgaySinh().toString());
        phaiField.setText(sinhVien.getPhai());
        diachiField.setText(sinhVien.getDiaChi());
        dtField.setText(sinhVien.getDienThoai());
        khoaField.setText(sinhVien.getKhoa());
        tinhtrangField.setText(sinhVien.getTinhTrang());
    }

    @FXML
    private void saveChanges() throws SQLException {
        try {
            sinhVienDAO.updateDiaChiSdt(sinhVien.getMaSV(), diachiField.getText(), dtField.getText());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Changes saved successfully!");
            alert.showAndWait();
        } catch (SQLException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Không thể cập nhật số điện thoại");
            alert.setContentText("Số điện thoại đã tồn tại, vui lòng kiểm tra lại!");
            alert.showAndWait();
            throw e;
        }
    }
}
