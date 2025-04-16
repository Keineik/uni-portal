package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.DangKy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DangKyDAO {
    public List<DangKy> getAllDangKyWithHocPhanDetails() throws SQLException {
        List<DangKy> dangKyList = new ArrayList<>();
        String query = "SELECT DANGKY.*, HOCPHAN.TENHP, HOCPHAN.SOTC, HOCPHAN.STLT, HOCPHAN.STTH " +
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

    public void insertDangKy(String maSV, int maMM) throws SQLException {
        String query = "INSERT INTO DANGKY (MASV, MAMM, DIEMTH, DIEMQT, DIEMTHI, DIEMTK) VALUES (?, ?, NULL, NULL, NULL, NULL)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, maSV);
            pstmt.setInt(2, maMM);
            pstmt.executeUpdate();
        }
    }

    public void updateDiem(DangKy dangKy) throws SQLException {
        String query = "UPDATE DANGKY SET DIEMTH = ?, DIEMQT = ?, DIEMTHI = ? WHERE MASV = ? AND MAMM = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, dangKy.getDiemTH());
            pstmt.setDouble(2, dangKy.getDiemQT());
            pstmt.setDouble(3, dangKy.getDiemThi());
            pstmt.setString(4, dangKy.getMaSV());
            pstmt.setInt(5, dangKy.getMaMM());
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
}