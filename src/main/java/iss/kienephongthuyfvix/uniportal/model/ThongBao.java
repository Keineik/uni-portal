package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.*;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

public class ThongBao {
    private final IntegerProperty maThongBao;
    private final StringProperty tieuDe;
    private final StringProperty noiDung;
    private final StringProperty linhVuc;
    private final StringProperty maCapBac;
    private final StringProperty maCoSo;
    private final ObjectProperty<Timestamp> ngayTao;

    public ThongBao(Integer maThongBao, String tieuDe, String noiDung, String linhVuc, String maCapBac, String maCoSo, Timestamp ngayTao) {
        this.maThongBao = new SimpleIntegerProperty(maThongBao);
        this.tieuDe = new SimpleStringProperty(tieuDe);
        this.noiDung = new SimpleStringProperty(noiDung);
        this.linhVuc = new SimpleStringProperty(linhVuc);
        this.maCapBac = new SimpleStringProperty(maCapBac);
        this.maCoSo = new SimpleStringProperty(maCoSo);
        this.ngayTao = new SimpleObjectProperty<>(ngayTao);
    }

    public static ThongBao fromResultSet(ResultSet rs) throws SQLException {
        return new ThongBao(
                rs.getInt("MATB"),
                rs.getString("TIEUDE"),
                rs.getString("NOIDUNG"),
                rs.getString("LINHVUC"),
                rs.getString("CAPBAC"),
                rs.getString("COSO"),
                rs.getTimestamp("NGAYTAO")
        );
    }

    public StringProperty getTieuDe() {
        return tieuDe;
    }

    public StringProperty getNoiDung() {
        return noiDung;
    }

    public StringProperty getLinhVuc() {
        return linhVuc;
    }

    public StringProperty getMaCapBac() {
        return maCapBac;
    }

    public StringProperty getMaCoSo() {
        return maCoSo;
    }

    public ObjectProperty<Timestamp> getNgayTao() {
        return ngayTao;
    }
}
