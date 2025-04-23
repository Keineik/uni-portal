package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.DangKy;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DangKyDAO {
    public List<DangKy> getAllDangKyWithHocPhanDetails() throws SQLException {
        List<DangKy> dangKyList = new ArrayList<>();
        String query = "SELECT DANGKY.*, MOMON.HK, MOMON.NAM, HOCPHAN.* " +
                "FROM DANGKY JOIN MOMON ON DANGKY.MAMM = MOMON.MAMM" +
                " JOIN HOCPHAN ON MOMON.MAHP = HOCPHAN.MAHP";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                dangKyList.add(DangKy.fromResultSet(rs));
            }
        }
        return dangKyList;
    }

    public List<DangKy> getAllDangKyWithHocPhanDetailsNVPDT() throws SQLException {
        List<DangKy> dangKyList = new ArrayList<>();
        String query = "SELECT DANGKY.*, UV_NVPDT_MOMON.HK, UV_NVPDT_MOMON.NAM, HOCPHAN.* " +
                "FROM DANGKY JOIN UV_NVPDT_MOMON ON DANGKY.MAMM = UV_NVPDT_MOMON.MAMM" +
                " JOIN HOCPHAN ON UV_NVPDT_MOMON.MAHP = HOCPHAN.MAHP";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                dangKyList.add(DangKy.fromResultSet(rs));
            }
        }
        return dangKyList;
    }

    public void insertDangKy(String maSV, int maMM) throws SQLException {
        String query = "INSERT INTO DANGKY (MASV, MAMM, DIEMTH, DIEMQT, DIEMTHI, DIEMTK) VALUES (?, ?, NULL, NULL, NULL, NULL)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, maSV);
            pstmt.setInt(2, maMM);
            pstmt.executeUpdate();
        }
    }

    public void updateDiem(String maSV, Integer maMM, Double diemTH, Double diemQT, Double diemThi) throws SQLException {
        String query = "UPDATE DANGKY SET DIEMTH = ?, DIEMQT = ?, DIEMTHI = ?, DIEMTK = ? WHERE MASV = ? AND MAMM = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, diemTH);
            pstmt.setDouble(2, diemQT);
            pstmt.setDouble(3, diemThi);
            pstmt.setDouble(4, 0.3 * diemTH + 0.2 * diemQT + 0.5 * diemThi);
            pstmt.setString(5, maSV);
            pstmt.setInt(6, maMM);
            pstmt.executeUpdate();
        }
    }

    public void deleteDangKy(String maSV, int maMM) throws SQLException {
        String query = "DELETE FROM DANGKY WHERE MASV = ? AND MAMM = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, maSV);
            pstmt.setInt(2, maMM);
            pstmt.executeUpdate();
        }
    }

    public List<DangKy> getKetQua(String maSV) throws SQLException {
        List<DangKy> dangKyList = new ArrayList<>();
        String query = "SELECT DANGKY.*, MM.HK, MM.NAM, HOCPHAN.* " +
                "FROM DANGKY JOIN UV_SV_MOMON MM ON DANGKY.MAMM = MM.MAMM " +
                "JOIN HOCPHAN ON MM.MAHP = HOCPHAN.MAHP " +
                "WHERE DANGKY.DIEMTK IS NOT NULL AND DANGKY.MASV = ?";

        log.info("query: " + query);
        log.info("maSV: " + maSV);

        try (Connection conn = Database.getConnection()) {

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, maSV);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                dangKyList.add(DangKy.fromResultSet(rs));
            }
        }
        return dangKyList;
    }

    public List<SinhVien> getSinhVienByMaMM(int maMM) throws SQLException {
        List<SinhVien> list = new ArrayList<>();
        String query = "SELECT SINHVIEN.* " +
                "FROM DANGKY JOIN SINHVIEN ON DANGKY.MASV = SINHVIEN.MASV " +
                "WHERE DANGKY.MAMM = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, maMM);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                list.add(SinhVien.fromResultSet(rs));
            }
        }
        return list;
    }

}