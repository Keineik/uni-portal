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

    public MoMon(int mamm, String mahp, String magv, int hk, int nam) {
        this.mamm = new SimpleIntegerProperty(mamm);
        this.mahp = new SimpleStringProperty(mahp);
        this.magv = new SimpleStringProperty(magv);
        this.hk = new SimpleIntegerProperty(hk);
        this.nam = new SimpleIntegerProperty(nam);
    }

    public static MoMon fromResultSet(ResultSet rs) throws SQLException {
        return new MoMon(
                rs.getInt("MAMM"),
                rs.getString("MAHP"),
                rs.getString("MAGV"),
                rs.getInt("HK"),
                rs.getInt("NAM")
        );
    }

    public IntegerProperty mammProperty() {
        return mamm;
    }

    public StringProperty mahpProperty() {
        return mahp;
    }

    public StringProperty magvProperty() {
        return magv;
    }

    public IntegerProperty hkProperty() {
        return hk;
    }

    public IntegerProperty namProperty() {
        return nam;
    }

    public int getMamm() {
        return mamm.get();
    }

    public String getMahp() {
        return mahp.get();
    }

    public String getMagv() {
        return magv.get();
    }

    public int getHk() {
        return hk.get();
    }

    public int getNam() {
        return nam.get();
    }

    public void setMamm(int mamm) {
        this.mamm.set(mamm);
    }

    public void setMahp(String mahp) {
        this.mahp.set(mahp);
    }

    public void setMagv(String magv) {
        this.magv.set(magv);
    }

    public void setHk(int hk) {
        this.hk.set(hk);
    }

    public void setNam(int nam) {
        this.nam.set(nam);
    }
}
