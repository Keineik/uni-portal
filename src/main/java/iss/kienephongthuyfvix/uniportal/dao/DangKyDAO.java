package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.DangKy;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DangKyDAO {
    public List<DangKy> getAllDangKy() throws SQLException {
        List<DangKy> dangKyList = new ArrayList<>();
        String query = "SELECT * FROM DANGKY";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                dangKyList.add(DangKy.fromResultSet(rs));
            }
        }
        return dangKyList;
    }

    public void insertDangKy(DangKy dangKy) throws SQLException {
        String query = "INSERT INTO DANGKY (MASV, MAMM, DIEMTH, DIEMQT, DIEMTHI, DIEMTK) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, dangKy.getMaSV());
            pstmt.setInt(2, dangKy.getMaMM());
            pstmt.setDouble(3, dangKy.getDiemTH());
            pstmt.setDouble(4, dangKy.getDiemQT());
            pstmt.setDouble(5, dangKy.getDiemThi());
            pstmt.setDouble(6, dangKy.getDiemTK());
            pstmt.executeUpdate();
        }
    }

    public void updateDangKy(DangKy dangKy) throws SQLException {
        String query = "UPDATE DANGKY SET DIEMTH = ?, DIEMQT = ?, DIEMTHI = ?, DIEMTK = ? WHERE MASV = ? AND MAMM = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setDouble(1, dangKy.getDiemTH());
            pstmt.setDouble(2, dangKy.getDiemQT());
            pstmt.setDouble(3, dangKy.getDiemThi());
            pstmt.setDouble(4, dangKy.getDiemTK());
            pstmt.setString(5, dangKy.getMaSV());
            pstmt.setInt(6, dangKy.getMaMM());
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