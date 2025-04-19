package iss.kienephongthuyfvix.uniportal.controller.DBA;

import com.jfoenix.controls.JFXCheckBox;
import iss.kienephongthuyfvix.uniportal.dao.ThongBaoDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class TaoThongBao {

    @FXML
    private TextArea contentArea;

    @FXML
    private JFXCheckBox coso1Check;

    @FXML
    private JFXCheckBox coso2Check;

    @FXML
    private JFXCheckBox nvCheck;

    @FXML
    private JFXCheckBox svCheck;

    @FXML
    private TextField titleField;

    @FXML
    private JFXCheckBox trgdvCheck;

    @FXML
    private JFXCheckBox hcCheck;

    @FXML
    private JFXCheckBox hoaCheck;

    @FXML
    private JFXCheckBox lyCheck;

    @FXML
    private JFXCheckBox toanCheck;

    @FXML
    private Button submitButton;

    @FXML
    private Button cancelButton;

    private final ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

    @FXML
    private void initialize() {
        trgdvCheck.setOnAction(e -> handleCapBacSelection(trgdvCheck));
        nvCheck.setOnAction(e -> handleCapBacSelection(nvCheck));
        svCheck.setOnAction(e -> handleCapBacSelection(svCheck));

        toanCheck.setOnAction(e -> handleLinhVucSelection(toanCheck));
        lyCheck.setOnAction(e -> handleLinhVucSelection(lyCheck));
        hoaCheck.setOnAction(e -> handleLinhVucSelection(hoaCheck));
        hcCheck.setOnAction(e -> handleLinhVucSelection(hcCheck));

        submitButton.setOnAction(e -> handleSubmit());

        cancelButton.setOnAction(e -> handleCancel());
    }

    private void handleCapBacSelection(JFXCheckBox selectedCheckBox) {
        if (selectedCheckBox.isSelected()) {
            trgdvCheck.setSelected(selectedCheckBox == trgdvCheck);
            nvCheck.setSelected(selectedCheckBox == nvCheck);
            svCheck.setSelected(selectedCheckBox == svCheck);
        }
    }

    private void handleLinhVucSelection(JFXCheckBox selectedCheckBox) {
        if (selectedCheckBox.isSelected()) {
            toanCheck.setSelected(selectedCheckBox == toanCheck);
            lyCheck.setSelected(selectedCheckBox == lyCheck);
            hoaCheck.setSelected(selectedCheckBox == hoaCheck);
            hcCheck.setSelected(selectedCheckBox == hcCheck);
        }
    }

    private void handleSubmit() {
        try {
            if (titleField.getText().trim().isEmpty()) {
                showErrorAlert("Validation Error", "Tiêu đề không được để trống.");
                return;
            }
            if (contentArea.getText().trim().isEmpty()) {
                showErrorAlert("Validation Error", "Nội dung không được để trống.");
                return;
            }
            if (!coso1Check.isSelected() && !coso2Check.isSelected()) {
                showErrorAlert("Validation Error", "Phải chọn ít nhất một cơ sở.");
                return;
            }
            if (!trgdvCheck.isSelected() && !nvCheck.isSelected() && !svCheck.isSelected()) {
                showErrorAlert("Validation Error", "Phải chọn một cấp bậc.");
                return;
            }

            if (!toanCheck.isSelected() && !lyCheck.isSelected() && !hoaCheck.isSelected() && !hcCheck.isSelected()) {
                showErrorAlert("Validation Error", "Phải chọn ít nhất một lĩnh vực.");
                return;
            }

            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String coSo = (coso1Check.isSelected() ? "Cơ sở 1" : "") + (coso2Check.isSelected() ? ",Cơ sở 2" : "");
            String capBac = trgdvCheck.isSelected() ? "Trưởng đơn vị" : nvCheck.isSelected() ? "Nhân viên" : "Sinh viên";
            String linhVuc = (toanCheck.isSelected() ? "Khoa Toán" : "") + (lyCheck.isSelected() ? ",Khoa Vật lý" : "") +
                    (hoaCheck.isSelected() ? ",Khoa Hóa học" : "") + (hcCheck.isSelected() ? ",Đơn vị hành chính" : "");

            thongBaoDAO.createThongBao(title, content, capBac, linhVuc, coSo);
            showInfoAlert("Success", "Thông báo đã được tạo thành công!");
            handleCancel();
        } catch (SQLException e) {
            showErrorAlert("Database Error", "Không thể lưu thông báo: " + e.getMessage());
        }
    }

    private void handleCancel() {
        cancelButton.getScene().getWindow().hide();
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