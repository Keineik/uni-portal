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
        String query = "SELECT UV_NVPDT_MOMON.*, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH" +
                " FROM UV_NVPDT_MOMON JOIN HOCPHAN ON UV_NVPDT_MOMON.MAHP = HOCPHAN.MAHP";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                moMonList.add(MoMon.fromResultSet(rs));
            }
        }
        return moMonList;
    }

    public List<MoMon> getAllMoMonGV() throws SQLException {
        List<MoMon> moMonList = new ArrayList<>();
        String query = "SELECT UV_GV_MOMON.*, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH" +
                " FROM UV_GV_MOMON JOIN HOCPHAN ON UV_GV_MOMON.MAHP = HOCPHAN.MAHP";

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
        String query = "INSERT INTO UV_NVPDT_MOMON (MAHP, MAGV, HK, NAM) VALUES (?, ?, ?, ?)";

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
        String query = "UPDATE UV_NVPDT_MOMON SET MAHP = ?, MAGV = ?, HK = ?, NAM = ? WHERE MAMM = ?";

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
        String query = "DELETE FROM UV_NVPDT_MOMON WHERE MAMM = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, mamm);
            pstmt.executeUpdate();
        }
    }

    public List<Pair<String, String>> getListHocPhan() throws SQLException {
        List<Pair<String, String>> hocPhanList = new ArrayList<>();
        String query = "SELECT MAMM, TENHP FROM HOCPHAN JOIN UV_NVPDT_MOMON ON HOCPHAN.MAHP = UV_NVPDT_MOMON.MAHP";

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
        String query = "SELECT UV_SV_MOMON.*, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH " +
                "FROM DANGKY JOIN UV_SV_MOMON ON DANGKY.MAMM = UV_SV_MOMON.MAMM" +
                " JOIN HOCPHAN ON UV_SV_MOMON.MAHP = HOCPHAN.MAHP";
        List<MoMon> monDDKList = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                monDDKList.add(MoMon.fromResultSet(rs));
            }
        }
        return monDDKList;
    }

    public List<MoMon> getAllChuaDangKy(String maSV) throws SQLException {
        String query = "SELECT UV_SV_MOMON.*, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH " +
                        "FROM HOCPHAN JOIN UV_SV_MOMON ON UV_SV_MOMON.MAHP = HOCPHAN.MAHP " +
                        "MINUS " +
                        "SELECT UV_SV_MOMON.*, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH " +
                        "FROM DANGKY JOIN UV_SV_MOMON ON DANGKY.MAMM = UV_SV_MOMON.MAMM " +
                        "JOIN HOCPHAN ON UV_SV_MOMON.MAHP = HOCPHAN.MAHP ";
        List<MoMon> monCDKList = new ArrayList<>();

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                monCDKList.add(MoMon.fromResultSet(rs));
            }
        }
        return monCDKList;
    }
}