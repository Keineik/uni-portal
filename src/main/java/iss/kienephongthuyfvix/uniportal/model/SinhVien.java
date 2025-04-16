package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

public class SinhVien {
    private final StringProperty maSV;
    private final StringProperty hoTen;
    private final StringProperty phai;
    private final ObjectProperty<Date> ngaySinh;
    private final StringProperty diaChi;
    private final StringProperty dienThoai;
    private final StringProperty khoa;
    private final StringProperty tinhTrang;

    public SinhVien(String maSV, String hoTen, String phai, Date ngaySinh, String diaChi, String dienThoai, String khoa, String tinhTrang) {
        this.maSV = new SimpleStringProperty(maSV);
        this.hoTen = new SimpleStringProperty(hoTen);
        this.phai = new SimpleStringProperty(phai);
        this.ngaySinh = new SimpleObjectProperty<>(ngaySinh);
        this.diaChi = new SimpleStringProperty(diaChi);
        this.dienThoai = new SimpleStringProperty(dienThoai);
        this.khoa = new SimpleStringProperty(khoa);
        this.tinhTrang = new SimpleStringProperty(tinhTrang);
    }

    public static SinhVien fromResultSet(ResultSet rs) throws SQLException {
        return new SinhVien(
                rs.getString("MASV"),
                rs.getString("HOTEN"),
                rs.getString("PHAI"),
                rs.getDate("NGSINH"),
                rs.getString("DCHI"),
                rs.getString("DT"),
                rs.getString("KHOA"),
                rs.getString("TINHTRANG")
        );
    }

    public StringProperty maSVProperty() { return maSV; }
    public StringProperty hoTenProperty() { return hoTen; }
    public StringProperty phaiProperty() { return phai; }
    public ObjectProperty<Date> ngaySinhProperty() { return ngaySinh; }
    public StringProperty diaChiProperty() { return diaChi; }
    public StringProperty dienThoaiProperty() { return dienThoai; }
    public StringProperty khoaProperty() { return khoa; }
    public StringProperty tinhTrangProperty() { return tinhTrang; }

    public String getMaSV() { return maSV.get(); }
    public String getHoTen() { return hoTen.get(); }
    public String getPhai() { return phai.get(); }
    public Date getNgaySinh() { return ngaySinh.get(); }
    public String getDiaChi() { return diaChi.get(); }
    public String getDienThoai() { return dienThoai.get(); }
    public String getKhoa() { return khoa.get(); }
    public String getTinhTrang() { return tinhTrang.get(); }

    public void setMaSV(String maSV) { this.maSV.set(maSV); }
    public void setHoTen(String hoTen) { this.hoTen.set(hoTen); }
    public void setPhai(String phai) { this.phai.set(phai); }
    public void setNgaySinh(Date ngaySinh) { this.ngaySinh.set(ngaySinh); }
    public void setDiaChi(String diaChi) { this.diaChi.set(diaChi); }
    public void setDienThoai(String dienThoai) { this.dienThoai.set(dienThoai); }
    public void setKhoa(String khoa) { this.khoa.set(khoa); }
    public void setTinhTrang(String tinhTrang) { this.tinhTrang.set(tinhTrang); }
}