package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.MoMon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoMonDAO {

    public List<MoMon> getAllMoMon() throws SQLException {
        List<MoMon> moMonList = new ArrayList<>();
        String query = "SELECT * FROM MOMON";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                moMonList.add(MoMon.fromResultSet(rs));
            }
        }
        return moMonList;
    }

    public void insertMoMon(MoMon moMon) throws SQLException {
        String query = "INSERT INTO MOMON (MAMM, MAHP, MAGV, HK, NAM) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, moMon.getMamm());
            pstmt.setString(2, moMon.getMahp());
            pstmt.setString(3, moMon.getMagv());
            pstmt.setInt(4, moMon.getHk());
            pstmt.setInt(5, moMon.getNam());
            pstmt.executeUpdate();
        }
    }

    public void updateMoMon(MoMon moMon) throws SQLException {
        String query = "UPDATE MOMON SET MAHP = ?, MAGV = ?, HK = ?, NAM = ? WHERE MAMM = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, moMon.getMahp());
            pstmt.setString(2, moMon.getMagv());
            pstmt.setInt(3, moMon.getHk());
            pstmt.setInt(4, moMon.getNam());
            pstmt.setString(5, moMon.getMamm());
            pstmt.executeUpdate();
        }
    }

    public void deleteMoMon(String mamm) throws SQLException {
        String query = "DELETE FROM MOMON WHERE MAMM = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, mamm);
            pstmt.executeUpdate();
        }
    }
}