package iss.kienephongthuyfvix.uniportal.controller.TDV;

import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class XemChiTietNV {

    @FXML private Label lblMaNV, lblHoTen, lblPhai, lblNgaySinh, lblSDT, lblVaiTro, lblMaDV;

    public void setNhanVien(NhanVien nv) {
        lblMaNV.setText(nv.getManv());
        lblHoTen.setText(nv.getHoten());
        lblPhai.setText(nv.getPhai());
        lblNgaySinh.setText(nv.getNgsinh().toString());
        lblSDT.setText(nv.getDt());
        lblVaiTro.setText(nv.getVaitro());
        lblMaDV.setText(nv.getMadv());
    }
}
