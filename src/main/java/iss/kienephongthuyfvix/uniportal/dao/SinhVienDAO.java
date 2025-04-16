package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.SinhVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SinhVienDAO {

    public List<SinhVien> getAllSinhVien() throws SQLException {
        List<SinhVien> sinhVienList = new ArrayList<>();
        String query = "SELECT * FROM SINHVIEN";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                sinhVienList.add(SinhVien.fromResultSet(rs));
            }
        }
        return sinhVienList;
    }

    public void insertSinhVien(SinhVien sinhVien) throws SQLException {
        String query = "INSERT INTO SINHVIEN VALUES (NULL, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sinhVien.getHoTen());
            pstmt.setString(2, sinhVien.getPhai());
            pstmt.setDate(3, new java.sql.Date(sinhVien.getNgaySinh().getTime()));
            pstmt.setString(4, sinhVien.getDiaChi());
            pstmt.setString(5, sinhVien.getDienThoai());
            pstmt.setString(6, sinhVien.getKhoa());
            pstmt.setString(7, sinhVien.getTinhTrang());
            pstmt.executeUpdate();
        }
    }

    public void updateSinhVien(SinhVien sinhVien) throws SQLException {
        String query = "UPDATE SINHVIEN SET HOTEN = ?, PHAI = ?, NGSINH = ?, DCHI = ?, DT = ?, KHOA = ?, TINHTRANG = ? WHERE MASV = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, sinhVien.getHoTen());
            pstmt.setString(2, sinhVien.getPhai());
            pstmt.setDate(3, new java.sql.Date(sinhVien.getNgaySinh().getTime()));
            pstmt.setString(4, sinhVien.getDiaChi());
            pstmt.setString(5, sinhVien.getDienThoai());
            pstmt.setString(6, sinhVien.getKhoa());
            pstmt.setString(7, sinhVien.getTinhTrang());
            pstmt.setString(8, sinhVien.getMaSV());
            pstmt.executeUpdate();
        }
    }

    public void deleteSinhVien(String maSV) throws SQLException {
        String query = "DELETE FROM SINHVIEN WHERE MASV = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, maSV);
            pstmt.executeUpdate();
        }
    }

    public void updateTinhTrang(String maSV, String tinhTrang) throws SQLException {
        String query = "UPDATE SINHVIEN SET TINHTRANG = ? WHERE MASV = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, tinhTrang);
            pstmt.setString(2, maSV);
            pstmt.executeUpdate();
        }
    }
}