package iss.kienephongthuyfvix.uniportal.controller.GV;

import iss.kienephongthuyfvix.uniportal.dao.NhanVienDAO;
import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;
import java.util.Optional;

public class ThongTinGV {

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

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    // Gọi hàm này từ controller chính hoặc khi cần truyền mã nhân viên
    public void loadThongTin(String maNV) {
        try {
            Optional<NhanVien> nv = nhanVienDAO.getAllNhanVien()
                    .stream()
                    .filter(n -> n.getManv().equals(maNV))
                    .findFirst();

            nv.ifPresent(nhanVien -> {
                manvField.setText(nhanVien.getManv());
                hotenField.setText(nhanVien.getHoten());
                phaiField.setText(nhanVien.getPhai());
                ngsinhField.setText(nhanVien.getNgsinh().toString());
                luongField.setText(String.valueOf(nhanVien.getLuong()));
                phucapField.setText(String.valueOf(nhanVien.getPhucap()));
                dtField.setText(nhanVien.getDt());
                vaitroField.setText(nhanVien.getVaitro());
                madvField.setText(nhanVien.getMadv());
            });
        } catch (SQLException e) {
            System.err.println("Lỗi khi tải thông tin nhân viên: " + e.getMessage());
        }
    }
}
