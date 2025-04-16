package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.*;

import javax.lang.model.type.NullType;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DangKy {
    private final StringProperty maSV;
    private final IntegerProperty maMM;
    private final DoubleProperty diemTH;
    private final DoubleProperty diemQT;
    private final DoubleProperty diemThi;
    private final DoubleProperty diemTK;

    private final StringProperty tenHP;
    private final IntegerProperty soTC;
    private final IntegerProperty stLT;
    private final IntegerProperty stTH;

    public DangKy(String maSV, int maMM, Double diemTH, Double diemQT, Double diemThi, Double diemTK,
                  String tenHP, int soTC, int stLT, int stTH) {
        this.maSV = new SimpleStringProperty(maSV);
        this.maMM = new SimpleIntegerProperty(maMM);
        this.diemTH = new SimpleDoubleProperty(diemTH != null ? diemTH : 0);
        this.diemQT = new SimpleDoubleProperty(diemQT != null ? diemQT : 0);
        this.diemThi = new SimpleDoubleProperty(diemThi != null ? diemThi : 0);
        this.diemTK = new SimpleDoubleProperty(diemTK != null ? diemTK : 0);

        // Initialize new fields
        this.tenHP = new SimpleStringProperty(tenHP);
        this.soTC = new SimpleIntegerProperty(soTC);
        this.stLT = new SimpleIntegerProperty(stLT);
        this.stTH = new SimpleIntegerProperty(stTH);
    }

    public static DangKy fromResultSet(ResultSet rs) throws SQLException {
        return new DangKy(
                rs.getString("MASV"),
                rs.getInt("MAMM"),
                rs.getObject("DIEMTH", Double.class),
                rs.getObject("DIEMQT", Double.class),
                rs.getObject("DIEMTHI", Double.class),
                rs.getObject("DIEMTK", Double.class),
                rs.getString("TENHP"),
                rs.getInt("SOTC"),
                rs.getInt("STLT"),
                rs.getInt("STTH")
        );
    }

    public StringProperty maSVProperty() { return maSV; }
    public IntegerProperty maMMProperty() { return maMM; }
    public DoubleProperty diemTHProperty() { return diemTH; }
    public DoubleProperty diemQTProperty() { return diemQT; }
    public DoubleProperty diemThiProperty() { return diemThi; }
    public DoubleProperty diemTKProperty() { return diemTK; }
    public StringProperty tenHPProperty() { return tenHP; }
    public IntegerProperty soTCProperty() { return soTC; }
    public IntegerProperty stLTProperty() { return stLT; }
    public IntegerProperty stTHProperty() { return stTH; }

    public String getMaSV() { return maSV.get(); }
    public int getMaMM() { return maMM.get(); }
    public double getDiemTH() { return diemTH.get(); }
    public double getDiemQT() { return diemQT.get(); }
    public double getDiemThi() { return diemThi.get(); }
    public double getDiemTK() { return diemTK.get(); }
    public String getTenHP() { return tenHP.get(); }
    public int getSoTC() { return soTC.get(); }
    public int getStLT() { return stLT.get(); }
    public int getStTH() { return stTH.get(); }

    public void setMaSV(String maSV) { this.maSV.set(maSV); }
    public void setMaMM(int maMM) { this.maMM.set(maMM); }
    public void setDiemTH(double diemTH) { this.diemTH.set(diemTH); }
    public void setDiemQT(double diemQT) { this.diemQT.set(diemQT); }
    public void setDiemThi(double diemThi) { this.diemThi.set(diemThi); }
    public void setDiemTK(double diemTK) { this.diemTK.set(diemTK); }
    public void setTenHP(String tenHP) { this.tenHP.set(tenHP); }
    public void setSoTC(int soTC) { this.soTC.set(soTC); }
    public void setStLT(int stLT) { this.stLT.set(stLT); }
    public void setStTH(int stTH) { this.stTH.set(stTH); }
}