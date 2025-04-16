package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

public class MoMon {
    private final IntegerProperty mamm;
    private final StringProperty mahp;
    private final StringProperty magv;
    private final IntegerProperty hk;
    private final IntegerProperty nam;

    private final StringProperty tenHP;
    private final IntegerProperty soTC;
    private final IntegerProperty stLT;
    private final IntegerProperty stTH;

    public MoMon(int mamm, String mahp, String magv, int hk, int nam, String tenHP, int soTC, int stLT, int stTH) {
        this.mamm = new SimpleIntegerProperty(mamm);
        this.mahp = new SimpleStringProperty(mahp);
        this.magv = new SimpleStringProperty(magv);
        this.hk = new SimpleIntegerProperty(hk);
        this.nam = new SimpleIntegerProperty(nam);

        this.tenHP = new SimpleStringProperty(tenHP);
        this.soTC = new SimpleIntegerProperty(soTC);
        this.stLT = new SimpleIntegerProperty(stLT);
        this.stTH = new SimpleIntegerProperty(stTH);
    }

    public static MoMon fromResultSet(ResultSet rs) throws SQLException {
        return new MoMon(
                rs.getInt("MAMM"),
                rs.getString("MAHP"),
                rs.getString("MAGV"),
                rs.getInt("HK"),
                rs.getInt("NAM"),
                rs.getString("TENHP"),
                rs.getInt("SOTC"),
                rs.getInt("STLT"),
                rs.getInt("STTH")
        );
    }

    public IntegerProperty mammProperty() {return mamm;}
    public StringProperty mahpProperty() {return mahp;}
    public StringProperty magvProperty() {return magv;}
    public IntegerProperty hkProperty() {return hk;}
    public IntegerProperty namProperty() {return nam;}
    public StringProperty tenHPProperty() {return tenHP;}
    public IntegerProperty soTCProperty() {return soTC;}
    public IntegerProperty stLTProperty() {return stLT;}
    public IntegerProperty stTHProperty() {return stTH;}

    public int getMamm() {return mamm.get();}
    public String getMahp() {return mahp.get();}
    public String getMagv() {return magv.get();}
    public int getHk() {return hk.get();}
    public int getNam() {return nam.get();}
    public String getTenHP() {return tenHP.get();}
    public int getSoTC() {return soTC.get();}
    public int getStLT() {return stLT.get();}
    public int getStTH() {return stTH.get();}

    public void setMamm(int mamm) {this.mamm.set(mamm);}
    public void setMahp(String mahp) {this.mahp.set(mahp);}
    public void setMagv(String magv) {this.magv.set(magv);}
    public void setHk(int hk) {this.hk.set(hk);}
    public void setNam(int nam) {this.nam.set(nam);}
    public void setTenHP(String tenHP) {this.tenHP.set(tenHP);}
    public void setSoTC(int soTC) {this.soTC.set(soTC);}
    public void setStLT(int stLT) {this.stLT.set(stLT);}
    public void setStTH(int stTH) {this.stTH.set(stTH);}
}
