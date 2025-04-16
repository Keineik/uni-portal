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
    private Button saveButton;

    @FXML
    private TextField tinhtrangField;

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final String maSV = "SV00000001"; // TODO: Please change this

    @FXML
    private void initialize() throws SQLException {
        SinhVien sinhVien = sinhVienDAO.getSinhVienByMaSV(maSV);
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
        sinhVienDAO.updateDiaChiSdt(maSV, diachiField.getText(), dtField.getText());
    }
}
