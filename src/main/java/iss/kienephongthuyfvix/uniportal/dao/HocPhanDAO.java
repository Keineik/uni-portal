package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.HocPhan;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HocPhanDAO {
    public List<HocPhan> getAllHocPhan() throws SQLException {
        List<HocPhan> hocPhanList = new ArrayList<>();
        String query = "SELECT * FROM HOCPHAN";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                hocPhanList.add(HocPhan.fromResultSet(rs));
            }
        }
        return hocPhanList;
    }

    public HocPhan getHocPhan(String hocPhan) throws SQLException {
        String query = "SELECT * FROM HOCPHAN WHERE MAHP = ?";
        HocPhan hp = null;

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, hocPhan);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                hp = HocPhan.fromResultSet(rs);
            }
        }

        return hp;
    }

    public void insertHocPhan(HocPhan hocPhan) throws SQLException {
        String query = "INSERT INTO HOCPHAN (MAHP, TENHP, SOTC, STLT, STTH, MADV) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, hocPhan.getMaHP());
            pstmt.setString(2, hocPhan.getTenHP());
            pstmt.setInt(3, hocPhan.getSoTC());
            pstmt.setInt(4, hocPhan.getStLT());
            pstmt.setInt(5, hocPhan.getStTH());
            pstmt.setString(6, hocPhan.getMaDV());
            pstmt.executeUpdate();
        }
    }

    public void updateHocPhan(HocPhan hocPhan) throws SQLException {
        String query = "UPDATE HOCPHAN SET TENHP = ?, SOTC = ?, STLT = ?, STTH = ?, MADV = ? WHERE MAHP = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, hocPhan.getTenHP());
            pstmt.setInt(2, hocPhan.getSoTC());
            pstmt.setInt(3, hocPhan.getStLT());
            pstmt.setInt(4, hocPhan.getStTH());
            pstmt.setString(5, hocPhan.getMaDV());
            pstmt.setString(6, hocPhan.getMaHP());
            pstmt.executeUpdate();
        }
    }

    public void deleteHocPhan(String maHP) throws SQLException {
        String query = "DELETE FROM HOCPHAN WHERE MAHP = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, maHP);
            pstmt.executeUpdate();
        }
    }
}