package iss.kienephongthuyfvix.uniportal.controller.NVPKT;

import iss.kienephongthuyfvix.uniportal.dao.DangKyDAO;
import iss.kienephongthuyfvix.uniportal.model.DangKy;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

@Slf4j
public class DiemDialog {

    @FXML
    private Button cancelButton;

    @FXML
    private TextField diemCKField;

    @FXML
    private TextField diemQTField;

    @FXML
    private TextField diemTHField;

    @FXML
    private TextField diemTKField;

    @FXML
    private TextField hkField;

    @FXML
    private TextField maHPField;

    @FXML
    private TextField maMMField;

    @FXML
    private TextField maSVField;

    @FXML
    private TextField namField;

    @FXML
    private Button saveButton;

    private DangKy currentDangKy;

    private final DangKyDAO dangKyDAO = new DangKyDAO();

    @FXML
    public void initialize() {
        addFieldListeners();
        calculateTK();

        saveButton.setOnAction(event -> saveChanges());
        cancelButton.setOnAction(event -> closeDialog());
    }

    public void setDangKy(DangKy dangKy) {
        this.currentDangKy = dangKy;

        if (dangKy != null) {
            maSVField.setText(dangKy.getMaSV());
            maMMField.setText(String.valueOf(dangKy.getMaMM()));
            maHPField.setText(dangKy.getMaHP());
            hkField.setText(String.valueOf(dangKy.getHk()));
            namField.setText(String.valueOf(dangKy.getNam()));
            diemTHField.setText(String.valueOf(dangKy.getDiemTH()));
            diemQTField.setText(String.valueOf(dangKy.getDiemQT()));
            diemCKField.setText(String.valueOf(dangKy.getDiemThi()));
            diemTKField.setText(String.format("%.2f", dangKy.getDiemTK()));
        }
    }

    private void addFieldListeners() {
        ChangeListener<String> listener = (observable, oldValue, newValue) -> calculateTK();
        diemTHField.textProperty().addListener(listener);
        diemQTField.textProperty().addListener(listener);
        diemCKField.textProperty().addListener(listener);
    }

    private void calculateTK() {
        try {
            double diemTH = diemTHField.getText() != null ? Double.parseDouble(diemTHField.getText()) : 0;
            double diemQT = diemQTField.getText() != null ? Double.parseDouble(diemQTField.getText()) : 0;
            double diemCK = diemCKField.getText() != null ? Double.parseDouble(diemCKField.getText()) : 0;

            double diemTK = 0.3 * diemTH + 0.2 * diemQT + 0.5 * diemCK;
            diemTKField.setText(String.valueOf(diemTK));
        } catch (NumberFormatException e) {
            // Clear diemTKField if input is invalid
            diemTKField.clear();
        }
    }

    private void saveChanges() {
        if (currentDangKy != null) {
            try {
                double diemTH = Double.parseDouble(diemTHField.getText());
                double diemQT = Double.parseDouble(diemQTField.getText());
                double diemCK = Double.parseDouble(diemCKField.getText());
                double diemTK = Double.parseDouble(diemTKField.getText());

                currentDangKy.setDiemTH(diemTH);
                currentDangKy.setDiemQT(diemQT);
                currentDangKy.setDiemThi(diemCK);
                currentDangKy.setDiemTK(diemTK);

                dangKyDAO.updateDiem(currentDangKy.getMaSV(),
                        currentDangKy.getMaMM(),
                        diemTH,
                        diemQT,
                        diemCK
                );

                showInfoAlert("Success", "Changes have been saved successfully!");
            } catch (NumberFormatException e) {
                showErrorAlert("Invalid Input", "Please enter valid numeric values for Điểm TH, QT, and CK.");
            } catch (SQLException e) {
                log.error("Failed to update diem: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private void closeDialog() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        closeDialog();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}