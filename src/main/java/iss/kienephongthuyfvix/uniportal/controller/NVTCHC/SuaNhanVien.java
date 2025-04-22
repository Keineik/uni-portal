package iss.kienephongthuyfvix.uniportal.controller.NVTCHC;

import iss.kienephongthuyfvix.uniportal.dao.NhanVienDAO;
import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class SuaNhanVien {

    @FXML private TextField txtMaNV, txtHoTen, txtLuong, txtPhuCap, txtSDT, txtVaiTro, txtMaDV;
    @FXML private ComboBox<String> cbPhai;
    @FXML private DatePicker dpNgaySinh;
    @FXML private Button btnLuu, btnHuy;

    private NhanVien nhanVien;
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    @FXML
    public void initialize() {
        cbPhai.getItems().addAll("Nam", "Nữ", "Khác");

        btnLuu.setOnAction(e -> luuThongTin());
        btnHuy.setOnAction(e -> ((Stage) btnHuy.getScene().getWindow()).close());
    }

    public void setNhanVien(NhanVien nv) {
        this.nhanVien = nv;
        txtMaNV.setText(nv.getManv());
        txtHoTen.setText(nv.getHoten());
        cbPhai.setValue(nv.getPhai());
        dpNgaySinh.setValue(LocalDate.parse(nv.getNgsinh().toString()));
        txtLuong.setText(String.valueOf(nv.getLuong()));
        txtPhuCap.setText(String.valueOf(nv.getPhucap()));
        txtSDT.setText(nv.getDt());
        txtVaiTro.setText(nv.getVaitro());
        txtMaDV.setText(nv.getMadv());
    }

    private void luuThongTin() {
        try {
            nhanVien.setHoten(txtHoTen.getText());
            nhanVien.setPhai(cbPhai.getValue());
            nhanVien.setNgsinh(Date.valueOf(dpNgaySinh.getValue()));
            nhanVien.setLuong(Double.parseDouble(txtLuong.getText()));
            nhanVien.setPhucap(Double.parseDouble(txtPhuCap.getText()));
            nhanVien.setDt(txtSDT.getText());
            nhanVien.setVaitro(txtVaiTro.getText());
            nhanVien.setMadv(txtMaDV.getText());

            boolean updated = nhanVienDAO.updateNhanVien(nhanVien);

            if (updated) {
                new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công.").showAndWait();
                ((Stage) btnLuu.getScene().getWindow()).close();
            } else {
                new Alert(Alert.AlertType.ERROR, "Không thể cập nhật nhân viên.").showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Lỗi khi lưu thông tin.").showAndWait();
        }
    }
}
