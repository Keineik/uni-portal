package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.*;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NhanVien {
    private final StringProperty manv;
    private final StringProperty hoten;
    private final StringProperty phai;
    private final ObjectProperty<Date> ngsinh;
    private final DoubleProperty luong;
    private final DoubleProperty phucap;
    private final StringProperty dt;
    private final StringProperty vaitro;
    private final StringProperty madv;

    public NhanVien(String manv, String hoten, String phai, Date ngsinh, double luong, double phucap, String dt, String vaitro, String madv) {
        this.manv = new SimpleStringProperty(manv);
        this.hoten = new SimpleStringProperty(hoten);
        this.phai = new SimpleStringProperty(phai);
        this.ngsinh = new SimpleObjectProperty<>(ngsinh);
        this.luong = new SimpleDoubleProperty(luong);
        this.phucap = new SimpleDoubleProperty(phucap);
        this.dt = new SimpleStringProperty(dt);
        this.vaitro = new SimpleStringProperty(vaitro);
        this.madv = new SimpleStringProperty(madv);
    }

    public static NhanVien fromResultSet(ResultSet rs) throws SQLException {
        var meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();

        boolean hasLuong = false, hasPhuCap = false;

        for (int i = 1; i <= columnCount; i++) {
            String columnName = meta.getColumnName(i);
            if (columnName.equalsIgnoreCase("LUONG")) hasLuong = true;
            if (columnName.equalsIgnoreCase("PHUCAP")) hasPhuCap = true;
        }

        double luong = hasLuong ? rs.getDouble("LUONG") : Double.NaN;
        double phucap = hasPhuCap ? rs.getDouble("PHUCAP") : Double.NaN;
        return new NhanVien(
                rs.getString("MANV"),
                rs.getString("HOTEN"),
                rs.getString("PHAI"),
                rs.getDate("NGSINH"),
                luong,
                phucap,
                rs.getString("DT"),
                rs.getString("VAITRO"),
                rs.getString("MADV")
        );
    }

    public StringProperty manvProperty() { return manv; }
    public StringProperty hotenProperty() { return hoten; }
    public StringProperty phaiProperty() { return phai; }
    public ObjectProperty<Date> ngsinhProperty() { return ngsinh; }
    public DoubleProperty luongProperty() { return luong; }
    public DoubleProperty phucapProperty() { return phucap; }
    public StringProperty dtProperty() { return dt; }
    public StringProperty vaitroProperty() { return vaitro; }
    public StringProperty madvProperty() { return madv; }

    public String getManv() { return manv.get(); }
    public String getHoten() { return hoten.get(); }
    public String getPhai() { return phai.get(); }
    public Date getNgsinh() { return ngsinh.get(); }
    public double getLuong() { return luong.get(); }
    public double getPhucap() { return phucap.get(); }
    public String getDt() { return dt.get(); }
    public String getVaitro() { return vaitro.get(); }
    public String getMadv() { return madv.get(); }

    public void setManv(String manv) { this.manv.set(manv); }
    public void setHoten(String hoten) { this.hoten.set(hoten); }
    public void setPhai(String phai) { this.phai.set(phai); }
    public void setNgsinh(Date ngsinh) { this.ngsinh.set(ngsinh); }
    public void setLuong(double luong) { this.luong.set(luong); }
    public void setPhucap(double phucap) { this.phucap.set(phucap); }
    public void setDt(String dt) { this.dt.set(dt); }
    public void setVaitro(String vaitro) { this.vaitro.set(vaitro); }
    public void setMadv(String madv) { this.madv.set(madv); }
}