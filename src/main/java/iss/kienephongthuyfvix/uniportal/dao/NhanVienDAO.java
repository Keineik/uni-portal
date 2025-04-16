package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.NhanVien;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {
    public List<NhanVien> getAllNhanVien() throws SQLException {
        List<NhanVien> nhanVienList = new ArrayList<>();
        String query = "SELECT * FROM NHANVIEN";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                nhanVienList.add(NhanVien.fromResultSet(rs));
            }
        }
        return nhanVienList;
    }

    public void insertNhanVien(NhanVien nhanVien) throws SQLException {
        String query = "INSERT INTO NHANVIEN (MANV, HOTEN, PHAI, NGSINH, LUONG, PHUCAP, DT, VAITRO, MADV) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nhanVien.getManv());
            pstmt.setString(2, nhanVien.getHoten());
            pstmt.setString(3, nhanVien.getPhai());
            pstmt.setDate(4, nhanVien.getNgsinh());
            pstmt.setDouble(5, nhanVien.getLuong());
            pstmt.setDouble(6, nhanVien.getPhucap());
            pstmt.setString(7, nhanVien.getDt());
            pstmt.setString(8, nhanVien.getVaitro());
            pstmt.setString(9, nhanVien.getMadv());
            pstmt.executeUpdate();
        }
    }

    public void updateNhanVien(NhanVien nhanVien) throws SQLException {
        String query = "UPDATE NHANVIEN SET HOTEN = ?, PHAI = ?, NGSINH = ?, LUONG = ?, PHUCAP = ?, DT = ?, VAITRO = ?, MADV = ? WHERE MANV = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, nhanVien.getHoten());
            pstmt.setString(2, nhanVien.getPhai());
            pstmt.setDate(3, nhanVien.getNgsinh());
            pstmt.setDouble(4, nhanVien.getLuong());
            pstmt.setDouble(5, nhanVien.getPhucap());
            pstmt.setString(6, nhanVien.getDt());
            pstmt.setString(7, nhanVien.getVaitro());
            pstmt.setString(8, nhanVien.getMadv());
            pstmt.setString(9, nhanVien.getManv());
            pstmt.executeUpdate();
        }
    }

    public void deleteNhanVien(String manv) throws SQLException {
        String query = "DELETE FROM NHANVIEN WHERE MANV = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, manv);
            pstmt.executeUpdate();
        }
    }
}