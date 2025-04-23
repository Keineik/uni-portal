package iss.kienephongthuyfvix.uniportal.controller.NVCB;

import iss.kienephongthuyfvix.uniportal.dao.NhanVienDAO;
import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class ThongTin {

    @FXML
    private TextField manvField;
    @FXML
    private TextField hotenField;
    @FXML
    private TextField phaiField;
    @FXML
    private TextField ngsinhField;
    @FXML
    private TextField luongField;
    @FXML
    private TextField phucapField;
    @FXML
    private TextField dtField;
    @FXML
    private TextField vaitroField;
    @FXML
    private TextField madvField;

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private NhanVien nhanVien;

    @FXML
    private void initialize() throws SQLException {
        nhanVien = nhanVienDAO.getCurrentNhanVien();
        manvField.setText(nhanVien.getManv());
        hotenField.setText(nhanVien.getHoten());
        phaiField.setText(nhanVien.getPhai());
        ngsinhField.setText(nhanVien.getNgsinh().toString());
        luongField.setText(String.valueOf(nhanVien.getLuong()));
        phucapField.setText(String.valueOf(nhanVien.getPhucap()));
        dtField.setText(nhanVien.getDt());
        vaitroField.setText(nhanVien.getVaitro());
        madvField.setText(nhanVien.getMadv());
    }

    @FXML
    private void saveChanges() throws SQLException {
        try {
            nhanVienDAO.updateSDTforNVCB(dtField.getText());

            // Show success alert
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText(null);
            alert.setContentText("Changes saved successfully!");
            alert.showAndWait();
        } catch (SQLException e) {
            // Show error alert
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to Save Changes");
            alert.setContentText("An error occurred while saving changes: " + e.getMessage());
            alert.showAndWait();
            throw e;
        }
    }
}