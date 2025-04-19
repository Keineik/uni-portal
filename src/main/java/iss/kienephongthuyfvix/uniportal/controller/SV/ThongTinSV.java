package iss.kienephongthuyfvix.uniportal.controller.SV;

import iss.kienephongthuyfvix.uniportal.dao.SinhVienDAO;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.fxml.FXML;
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
        sinhVienDAO.updateDiaChiSdt(sinhVien.getMaSV(), diachiField.getText(), dtField.getText());
    }
}
