package iss.kienephongthuyfvix.uniportal.controller.DBA;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class PhatThongBao {

    @FXML private Button createAnnouncementButton;
    @FXML private TextField titleField;
    @FXML private TextArea contentArea;

    @FXML
    public void initialize() {
        setupAnnouncementButton();
    }

    private void sendAnnouncement() {
        String title = titleField.getText();
        String content = contentArea.getText();

    }
    private void openCreateAnnouncement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/tao-thong-bao.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Tạo Thông Báo");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupAnnouncementButton() {
        if (createAnnouncementButton != null) {
            createAnnouncementButton.setOnAction(e -> openCreateAnnouncement());
        }
    }
}
