package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DonVi {
    private final StringProperty maDV;
    private final StringProperty tenDV;
    private final StringProperty loaiDV;
    private final StringProperty trgDV;

    public DonVi(String maDV, String tenDV, String loaiDV, String trgDV) {
        this.maDV = new SimpleStringProperty(maDV);
        this.tenDV = new SimpleStringProperty(tenDV);
        this.loaiDV = new SimpleStringProperty(loaiDV);
        this.trgDV = new SimpleStringProperty(trgDV);
    }

    public static DonVi fromResultSet(ResultSet rs) throws SQLException {
        return new DonVi(
                rs.getString("MADV"),
                rs.getString("TENDV"),
                rs.getString("LOAIDV"),
                rs.getString("TRGDV")
        );
    }

    public StringProperty maDVProperty() { return maDV; }
    public StringProperty tenDVProperty() { return tenDV; }
    public StringProperty loaiDVProperty() { return loaiDV; }
    public StringProperty trgDVProperty() { return trgDV; }

    public String getMaDV() { return maDV.get(); }
    public String getTenDV() { return tenDV.get(); }
    public String getLoaiDV() { return loaiDV.get(); }
    public String getTrgDV() { return trgDV.get(); }

    public void setMaDV(String maDV) { this.maDV.set(maDV); }
    public void setTenDV(String tenDV) { this.tenDV.set(tenDV); }
    public void setLoaiDV(String loaiDV) { this.loaiDV.set(loaiDV); }
    public void setTrgDV(String trgDV) { this.trgDV.set(trgDV); }
}