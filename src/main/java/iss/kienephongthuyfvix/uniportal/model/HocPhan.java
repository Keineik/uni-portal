package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class HocPhan {
    private final StringProperty maHP;
    private final StringProperty tenHP;
    private final IntegerProperty soTC;
    private final IntegerProperty stLT;
    private final IntegerProperty stTH;
    private final StringProperty maDV;

    public HocPhan(String maHP, String tenHP, int soTC, int stLT, int stTH, String maDV) {
        this.maHP = new SimpleStringProperty(maHP);
        this.tenHP = new SimpleStringProperty(tenHP);
        this.soTC = new SimpleIntegerProperty(soTC);
        this.stLT = new SimpleIntegerProperty(stLT);
        this.stTH = new SimpleIntegerProperty(stTH);
        this.maDV = new SimpleStringProperty(maDV);
    }

    public static HocPhan fromResultSet(ResultSet rs) throws SQLException {
        return new HocPhan(
                rs.getString("MAHP"),
                rs.getString("TENHP"),
                rs.getInt("SOTC"),
                rs.getInt("STLT"),
                rs.getInt("STTH"),
                rs.getString("MADV")
        );
    }

    public StringProperty maHPProperty() { return maHP; }
    public StringProperty tenHPProperty() { return tenHP; }
    public IntegerProperty soTCProperty() { return soTC; }
    public IntegerProperty stLTProperty() { return stLT; }
    public IntegerProperty stTHProperty() { return stTH; }
    public StringProperty maDVProperty() { return maDV; }

    public String getMaHP() { return maHP.get(); }
    public String getTenHP() { return tenHP.get(); }
    public int getSoTC() { return soTC.get(); }
    public int getStLT() { return stLT.get(); }
    public int getStTH() { return stTH.get(); }
    public String getMaDV() { return maDV.get(); }

    public void setMaHP(String maHP) { this.maHP.set(maHP); }
    public void setTenHP(String tenHP) { this.tenHP.set(tenHP); }
    public void setSoTC(int soTC) { this.soTC.set(soTC); }
    public void setStLT(int stLT) { this.stLT.set(stLT); }
    public void setStTH(int stTH) { this.stTH.set(stTH); }
    public void setMaDV(String maDV) { this.maDV.set(maDV); }
}