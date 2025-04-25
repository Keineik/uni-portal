package iss.kienephongthuyfvix.uniportal.controller.NVTCHC;

import iss.kienephongthuyfvix.uniportal.dao.NhanVienDAO;
import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.sql.Date;
import java.time.LocalDate;

public class NhanVienDialog {

    @FXML private TextField txtMaNV, txtHoTen, txtLuong, txtPhuCap, txtSDT, txtMaDV;
    @FXML private ComboBox<String> cbPhai;
    @FXML private DatePicker dpNgaySinh;
    @FXML private Button btnLuu, btnHuy;
    @FXML private ComboBox<String> vaitroCombo;

    private NhanVien nhanVien;
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    @FXML
    public void initialize() {
        cbPhai.getItems().addAll("Nam", "Nữ", "Khác");

        btnLuu.setOnAction(e -> luuThongTin());
        btnHuy.setOnAction(e -> ((Stage) btnHuy.getScene().getWindow()).close());
        vaitroCombo.getItems().addAll("NVCB", "GV", "NV_PDT", "NV_TCHC", "NV_CTSV", "NV_PKT", "TRGDV");
    }

    public void setNhanVien(NhanVien nv) {
        this.nhanVien = nv;

        if (nv != null) {
            txtMaNV.setText(nv.getManv());
            txtHoTen.setText(nv.getHoten());
            cbPhai.setValue(nv.getPhai());
            dpNgaySinh.setValue(LocalDate.parse(nv.getNgsinh().toString()));
            txtLuong.setText(String.valueOf(nv.getLuong()));
            txtPhuCap.setText(String.valueOf(nv.getPhucap()));
            txtSDT.setText(nv.getDt());
            vaitroCombo.setValue(nv.getVaitro());
            txtMaDV.setText(nv.getMadv());
            txtMaNV.setEditable(false);
        } else {
            // Insert mode
            vaitroCombo.setValue("NVCB");
        }
    }

    private void luuThongTin() {
        try {
            if (nhanVien == null) {
                // Insert new employee
                NhanVien newNhanVien = new NhanVien(
                        null,
                        txtHoTen.getText(),
                        cbPhai.getValue(),
                        Date.valueOf(dpNgaySinh.getValue()),
                        Double.parseDouble(txtLuong.getText()),
                        Double.parseDouble(txtPhuCap.getText()),
                        txtSDT.getText(),
                        vaitroCombo.getValue(),
                        txtMaDV.getText()
                );

                nhanVienDAO.insertNhanVien(newNhanVien);

                new Alert(Alert.AlertType.INFORMATION, "Thêm nhân viên thành công.").showAndWait();
                ((Stage) btnLuu.getScene().getWindow()).close();
            } else {
                // Update existing employee
                nhanVien.setHoten(txtHoTen.getText());
                nhanVien.setPhai(cbPhai.getValue());
                nhanVien.setNgsinh(Date.valueOf(dpNgaySinh.getValue()));
                nhanVien.setLuong(Double.parseDouble(txtLuong.getText()));
                nhanVien.setPhucap(Double.parseDouble(txtPhuCap.getText()));
                nhanVien.setDt(txtSDT.getText());
                nhanVien.setVaitro(vaitroCombo.getValue());
                nhanVien.setMadv(txtMaDV.getText());

                nhanVienDAO.updateNhanVien(nhanVien);

                new Alert(Alert.AlertType.INFORMATION, "Cập nhật thành công.").showAndWait();
                ((Stage) btnLuu.getScene().getWindow()).close();
            }
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, "Lỗi:").showAndWait();
        }
    }
}
