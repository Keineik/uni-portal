package iss.kienephongthuyfvix.uniportal.controller.NVCB;

import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class ThongTin {

    @FXML
    private TextField manvField;
    @FXML
    private TextField hotenField;
    @FXML
    private TextField phaiField;
    @FXML
    private TextField ngsinhField;
    @FXML
    private TextField luongField;
    @FXML
    private TextField phucapField;
    @FXML
    private TextField dtField;
    @FXML
    private TextField vaitroField;
    @FXML
    private TextField madvField;

    public void setNhanVien(NhanVien nhanVien) {
        manvField.setText(nhanVien.getManv());
        hotenField.setText(nhanVien.getHoten());
        phaiField.setText(nhanVien.getPhai());
        ngsinhField.setText(nhanVien.getNgsinh());
        luongField.setText(String.valueOf(nhanVien.getLuong()));
        phucapField.setText(String.valueOf(nhanVien.getPhucap()));
        dtField.setText(nhanVien.getDt());
        vaitroField.setText(nhanVien.getVaitro());
        madvField.setText(nhanVien.getMadv());
    }
}