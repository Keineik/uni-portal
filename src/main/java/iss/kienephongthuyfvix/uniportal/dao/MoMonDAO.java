package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.DangKy;
import iss.kienephongthuyfvix.uniportal.model.MoMon;
import javafx.util.Pair;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MoMonDAO {

    public List<MoMon> getAllMoMon() throws SQLException {
        List<MoMon> moMonList = new ArrayList<>();
        String query = "SELECT MOMON.*, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH" +
                " FROM MOMON JOIN HOCPHAN ON MOMON.MAHP = HOCPHAN.MAHP";

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
        String query = "INSERT INTO MOMON (MAHP, MAGV, HK, NAM) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, moMon.getMahp());
            pstmt.setString(2, moMon.getMagv());
            pstmt.setInt(3, moMon.getHk());
            pstmt.setInt(4, moMon.getNam());
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
            pstmt.setInt(5, moMon.getMamm());
            pstmt.executeUpdate();
        }
    }

    public void deleteMoMon(int mamm) throws SQLException {
        String query = "DELETE FROM MOMON WHERE MAMM = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, mamm);
            pstmt.executeUpdate();
        }
    }

    public List<Pair<String, String>> getListHocPhan() throws SQLException {
        List<Pair<String, String>> hocPhanList = new ArrayList<>();
        String query = "SELECT MAMM, TENHP FROM HOCPHAN JOIN MOMON ON HOCPHAN.MAHP = MOMON.MAHP";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                hocPhanList.add(
                        new Pair<>(rs.getString("MAMM"), rs.getString("TENHP"))
                );
            }
        }
        return hocPhanList;
    }

    public List<MoMon> getAllDaDangKy(String maSV) throws SQLException {
        String query = "SELECT DANGKY.*, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH " +
                "FROM DANGKY JOIN MOMON ON DANGKY.MAMM = MOMON.MAMM" +
                " JOIN HOCPHAN ON MOMON.MAHP = HOCPHAN.MAHP" +
                " WHERE DANGKY.MASV = ?";
        List<MoMon> dangKyList = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, maSV);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                dangKyList.add(MoMon.fromResultSet(rs));
            }
        }
        return dangKyList;
    }

    public List<MoMon> getAllChuaDangKy(String maSV) throws SQLException {
        String query = "SELECT DANGKY.*, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH " +
                "FROM DANGKY JOIN MOMON ON DANGKY.MAMM = MOMON.MAMM" +
                " JOIN HOCPHAN ON MOMON.MAHP = HOCPHAN.MAHP" +
                " WHERE DANGKY.MASV = ?";
        List<MoMon> dangKyList = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, maSV);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                dangKyList.add(MoMon.fromResultSet(rs));
            }
        }
        return dangKyList;
    }
}