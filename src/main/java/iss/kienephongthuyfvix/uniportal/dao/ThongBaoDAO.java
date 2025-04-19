package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.ThongBao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ThongBaoDAO {
    public void createThongBao(String tieuDe, String noiDung, String capBac, String linhVuc, String coSo) throws SQLException {
        String query = "{CALL USP_DBA_ThemThongBao(?, ?, ?, ?, ?)}";

        try (Connection conn = Database.getConnection();
             CallableStatement cstmt = conn.prepareCall(query)) {

            cstmt.setString(1, tieuDe);
            cstmt.setString(2, noiDung);
            cstmt.setString(3, capBac);
            cstmt.setString(4, linhVuc);
            cstmt.setString(5, coSo);

            cstmt.execute();
        }
    }

    public List<ThongBao> getAllThongBao() throws SQLException {
        String query = "SELECT * FROM THONGBAO ORDER BY NGAYTAO DESC";
        List<ThongBao> thongBaoList = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery();) {

            while (rs.next()) {
                ThongBao thongBao = ThongBao.fromResultSet(rs);
                thongBaoList.add(thongBao);
            }
        }
        return thongBaoList;
    }

    public ThongBao getThongBao(Integer maThongBao) throws SQLException {
        String query = "SELECT * FROM THONGBAO WHERE MATB = ?";
        ThongBao thongBao = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, maThongBao);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                thongBao = ThongBao.fromResultSet(rs);
            }
        }
        return thongBao;
    }

    public void deleteThongBao(Integer maThongBao) throws SQLException {
        String query = "DELETE FROM THONGBAO WHERE MATB = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, maThongBao);
            pstmt.executeUpdate();
        }
    }
}
