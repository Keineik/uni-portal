package iss.kienephongthuyfvix.uniportal.controller.NVPDT;

import com.jfoenix.controls.JFXComboBox;
import iss.kienephongthuyfvix.uniportal.dao.HocPhanDAO;
import iss.kienephongthuyfvix.uniportal.dao.MoMonDAO;
import iss.kienephongthuyfvix.uniportal.model.HocPhan;
import iss.kienephongthuyfvix.uniportal.model.MoMon;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

public class MoMonDialog {

    private static final Logger log = LoggerFactory.getLogger(MoMonDialog.class);
    @FXML
    private TextField mammField;

    @FXML
    private JFXComboBox<HocPhan> hocphanCombo;

    @FXML
    private TextField magvField;

    @FXML
    private TextField hkField;

    @FXML
    private TextField namField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    private final MoMonDAO moMonDAO = new MoMonDAO();
    private MoMon moMon;
    private final HocPhanDAO hocPhanDAO = new HocPhanDAO();

    @FXML
    public void initialize() {
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());

        try {
            // Load list of HocPhan into the ComboBox
            List<HocPhan> hocPhanList = new HocPhanDAO().getAllHocPhan();
            hocphanCombo.setItems(FXCollections.observableArrayList(hocPhanList));

            // Set a custom StringConverter to display tenHP in the ComboBox
            hocphanCombo.setConverter(new javafx.util.StringConverter<>() {
                @Override
                public String toString(HocPhan hocPhan) {
                    return hocPhan == null ? "" : hocPhan.getMaHP() + "-" + hocPhan.getTenHP();
                }

                @Override
                public HocPhan fromString(String string) {
                    return null;
                }
            });
        } catch (SQLException e) {
            showErrorAlert("Error", "Failed to load HocPhan data: " + e.getMessage());
        }
    }

    public void setMoMon(MoMon moMon) throws SQLException {
        this.moMon = moMon;

        if (moMon != null) {
            mammField.setText(String.valueOf(moMon.getMamm()));
            HocPhan selectedHocPhan = hocphanCombo.getItems()
                    .stream()
                    .filter(hp -> hp.getMaHP().equals(moMon.getMahp()))
                    .findFirst()
                    .orElse(null);
            hocphanCombo.setValue(selectedHocPhan);
            magvField.setText(moMon.getMagv());
            hkField.setText(String.valueOf(moMon.getHk()));
            namField.setText(String.valueOf(moMon.getNam()));
        }
    }

    private void handleSave() {
        try {
            if (moMon == null) {
                // Insert new record
                moMon = new MoMon(
                        0,
                        hocphanCombo.getValue().getMaHP(),
                        magvField.getText(),
                        Integer.parseInt(hkField.getText()),
                        Integer.parseInt(namField.getText()),
                        "",
                        0,
                        0,
                        0
                );
                moMonDAO.insertMoMon(moMon);
                showInfoAlert("Success", "Môn học đã được thêm thành công!");
            } else {
                // Update existing record
                moMon.setMamm(Integer.parseInt(mammField.getText()));
                moMon.setMahp(hocphanCombo.getValue().getMaHP());
                moMon.setMagv(magvField.getText());
                moMon.setHk(Integer.parseInt(hkField.getText()));
                moMon.setNam(Integer.parseInt(namField.getText()));
                moMonDAO.updateMoMon(moMon);
                showInfoAlert("Success", "Môn học đã được cập nhật thành công!");
            }
            closeDialog();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Không thể lưu môn học: " + e.getMessage());
        } catch (NumberFormatException e) {
            showErrorAlert("Validation Error", "Vui lòng nhập đúng định dạng số cho các trường học kỳ và năm.");
        }
    }

    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}