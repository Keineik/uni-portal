package iss.kienephongthuyfvix.uniportal.dao;

import iss.kienephongthuyfvix.uniportal.model.DonVi;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonViDAO {
    public List<DonVi> getAllDonVi() throws SQLException {
        List<DonVi> donViList = new ArrayList<>();
        String query = "SELECT * FROM DONVI";

        try (Connection conn = Database.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                donViList.add(DonVi.fromResultSet(rs));
            }
        }
        return donViList;
    }

    public void insertDonVi(DonVi donVi) throws SQLException {
        String query = "INSERT INTO DONVI (MADV, TENDV, LOAIDV, TRGDV) VALUES (?, ?, ?, ?)";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, donVi.getMaDV());
            pstmt.setString(2, donVi.getTenDV());
            pstmt.setString(3, donVi.getLoaiDV());
            pstmt.setString(4, donVi.getTrgDV());
            pstmt.executeUpdate();
        }
    }

    public void updateDonVi(DonVi donVi) throws SQLException {
        String query = "UPDATE DONVI SET TENDV = ?, LOAIDV = ?, TRGDV = ? WHERE MADV = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, donVi.getTenDV());
            pstmt.setString(2, donVi.getLoaiDV());
            pstmt.setString(3, donVi.getTrgDV());
            pstmt.setString(4, donVi.getMaDV());
            pstmt.executeUpdate();
        }
    }

    public void deleteDonVi(String maDV) throws SQLException {
        String query = "DELETE FROM DONVI WHERE MADV = ?";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, maDV);
            pstmt.executeUpdate();
        }
    }
}