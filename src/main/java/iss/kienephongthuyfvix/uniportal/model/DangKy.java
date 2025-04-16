package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DangKy {
    private final StringProperty maSV;
    private final IntegerProperty maMM;
    private final DoubleProperty diemTH;
    private final DoubleProperty diemQT;
    private final DoubleProperty diemThi;
    private final DoubleProperty diemTK;

    public DangKy(String maSV, int maMM, Double diemTH, Double diemQT, Double diemThi, Double diemTK) {
        this.maSV = new SimpleStringProperty(maSV);
        this.maMM = new SimpleIntegerProperty(maMM);
        this.diemTH = new SimpleDoubleProperty(diemTH != null ? diemTH : 0);
        this.diemQT = new SimpleDoubleProperty(diemQT != null ? diemQT : 0);
        this.diemThi = new SimpleDoubleProperty(diemThi != null ? diemThi : 0);
        this.diemTK = new SimpleDoubleProperty(diemTK != null ? diemTK : 0);
    }

    public static DangKy fromResultSet(ResultSet rs) throws SQLException {
        return new DangKy(
                rs.getString("MASV"),
                rs.getInt("MAMM"),
                rs.getObject("DIEMTH", Double.class),
                rs.getObject("DIEMQT", Double.class),
                rs.getObject("DIEMTHI", Double.class),
                rs.getObject("DIEMTK", Double.class)
        );
    }

    public StringProperty maSVProperty() { return maSV; }
    public IntegerProperty maMMProperty() { return maMM; }
    public DoubleProperty diemTHProperty() { return diemTH; }
    public DoubleProperty diemQTProperty() { return diemQT; }
    public DoubleProperty diemThiProperty() { return diemThi; }
    public DoubleProperty diemTKProperty() { return diemTK; }

    public String getMaSV() { return maSV.get(); }
    public int getMaMM() { return maMM.get(); }
    public double getDiemTH() { return diemTH.get(); }
    public double getDiemQT() { return diemQT.get(); }
    public double getDiemThi() { return diemThi.get(); }
    public double getDiemTK() { return diemTK.get(); }

    public void setMaSV(String maSV) { this.maSV.set(maSV); }
    public void setMaMM(int maMM) { this.maMM.set(maMM); }
    public void setDiemTH(double diemTH) { this.diemTH.set(diemTH); }
    public void setDiemQT(double diemQT) { this.diemQT.set(diemQT); }
    public void setDiemThi(double diemThi) { this.diemThi.set(diemThi); }
    public void setDiemTK(double diemTK) { this.diemTK.set(diemTK); }
}