package iss.kienephongthuyfvix.uniportal.controller.NVTCHC;

import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class XemChiTietNV {

    @FXML
    private Label lblMaNV, lblHoTen, lblPhai, lblNgaySinh, lblLuong, lblPhuCap, lblSDT, lblVaiTro, lblMaDV;

    public void setNhanVien(NhanVien nv) {
        lblMaNV.setText(nv.getManv());
        lblHoTen.setText(nv.getHoten());
        lblPhai.setText(nv.getPhai());
        lblNgaySinh.setText(nv.getNgsinh().toString());
        lblLuong.setText(String.valueOf(nv.getLuong()));
        lblPhuCap.setText(String.valueOf(nv.getPhucap()));
        lblSDT.setText(nv.getDt());
        lblVaiTro.setText(nv.getVaitro());
        lblMaDV.setText(nv.getMadv());
    }
}