package iss.kienephongthuyfvix.uniportal.model;

import javafx.beans.property.*;
import lombok.Getter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class ThongBao {
    @Getter
    private final IntegerProperty maThongBao;
    @Getter
    private final StringProperty tieuDe;
    @Getter
    private final StringProperty noiDung;
    @Getter
    private final StringProperty linhVuc;
    @Getter
    private final StringProperty maCapBac;
    @Getter
    private final StringProperty maCoSo;
    @Getter
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
}
