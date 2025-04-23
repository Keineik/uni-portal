package iss.kienephongthuyfvix.uniportal.controller.DBA;

import iss.kienephongthuyfvix.uniportal.dao.ThongBaoDAO;
import iss.kienephongthuyfvix.uniportal.model.ThongBao;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Slf4j
public class PhatThongBao {

    @FXML
    private ComboBox<String> capbacCombo;

    @FXML
    private ComboBox<String> cosoCombo;

    @FXML
    private ComboBox<String> linhVucCombo;

    @FXML
    private Button createAnnouncementButton;

    @FXML
    private VBox notiListBox;

    private final ObservableList<ThongBao> thongBaoData = FXCollections.observableArrayList();
    private final ThongBaoDAO thongBaoDAO = new ThongBaoDAO();

    @FXML
    private void initialize() {
        cosoCombo.getItems().addAll("Tất cả", "CS1", "CS2");
        cosoCombo.setValue("Tất cả");
        cosoCombo.setOnAction(event -> filterNotifications());
        capbacCombo.getItems().addAll("Tất cả", "TRGDV", "NV", "SV");
        capbacCombo.setValue("Tất cả");
        capbacCombo.setOnAction(event -> filterNotifications());
        linhVucCombo.getItems().addAll("Tất cả", "TOAN", "LY", "HOA", "HC");
        linhVucCombo.setValue("Tất cả");
        linhVucCombo.setOnAction(event -> filterNotifications());

        createAnnouncementButton.setOnAction(event -> moTaoThongBao());

        try {
            loadData();
        } catch (SQLException e) {
            ;;log.error("Error loading data: ", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load notifications");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
        showThongBao(thongBaoData);
    }

    private String formatTimestamp(Timestamp timestamp) {
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd/MM/yyyy");
        return dateTime.format(formatter);
    }

    @FXML
    private void loadData() throws SQLException {
        thongBaoData.setAll(thongBaoDAO.getAllThongBao());
    }

    @FXML
    private void showThongBao(ObservableList<ThongBao> thongBaoData) {
        notiListBox.getChildren().clear();

        for (ThongBao thongBao : thongBaoData) {
            VBox notiBox = new VBox();
            notiBox.setPrefHeight(76.0);
            notiBox.setPrefWidth(852.0);
            notiBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #dee2e6; -fx-border-radius: 8;");

            BorderPane headerPane = new BorderPane();
            headerPane.setPrefHeight(200.0);
            headerPane.setPrefWidth(200.0);

            Label titleLabel = new Label(thongBao.getTieuDe().get());
            titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            BorderPane.setAlignment(titleLabel, javafx.geometry.Pos.CENTER);
            headerPane.setLeft(titleLabel);

            Label dateLabel = new Label(formatTimestamp(thongBao.getNgayTao().get()));
            dateLabel.setStyle("-fx-text-fill: gray;");
            BorderPane.setAlignment(dateLabel, javafx.geometry.Pos.CENTER);
            headerPane.setRight(dateLabel);

            Label recipientLabel = new Label("Gửi đến: " + thongBao.getMaCapBac().get() + ", " + thongBao.getLinhVuc().get());
            Label contentLabel = new Label(getShortContent(thongBao.getNoiDung().get()));

            notiBox.setOnMouseEntered(event -> {
                notiBox.setStyle("-fx-background-color: #18c27b; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #dee2e6; -fx-border-radius: 8;");
                notiBox.setCursor(javafx.scene.Cursor.HAND);

                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: white;");
                dateLabel.setStyle("-fx-text-fill: white;");
                recipientLabel.setStyle("-fx-text-fill: white;");
                contentLabel.setStyle("-fx-text-fill: white;");
            });

            notiBox.setOnMouseExited(event -> {
                notiBox.setStyle("-fx-background-color: white; -fx-padding: 10; -fx-background-radius: 8; -fx-border-color: #dee2e6; -fx-border-radius: 8;");
                notiBox.setCursor(javafx.scene.Cursor.DEFAULT);

                titleLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
                dateLabel.setStyle("-fx-text-fill: gray;");
                recipientLabel.setStyle("-fx-text-fill: black;");
                contentLabel.setStyle("-fx-text-fill: black;");
            });

            notiBox.getChildren().addAll(headerPane, recipientLabel, contentLabel);

            notiBox.setOnMouseClicked(event -> showNotificationDetails(thongBao));

            notiListBox.getChildren().add(notiBox);
        }
    }

    private String getShortContent(String content) {
        return content.length() > 50 ? content.substring(0, 50) + "..." : content;
    }

    private void showNotificationDetails(ThongBao thongBao) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Chi tiết thông báo");
        alert.setHeaderText(thongBao.getTieuDe().get());
        alert.setContentText(
                "Nội dung: " + thongBao.getNoiDung().get() + "\n" +
                        "Lĩnh vực: " + thongBao.getLinhVuc().get() + "\n" +
                        "Cấp bậc: " + thongBao.getMaCapBac().get() + "\n" +
                        "Cơ sở: " + thongBao.getMaCoSo().get() + "\n" +
                        "Ngày tạo: " + thongBao.getNgayTao().get()
        );
        alert.showAndWait();
    }

    private void moTaoThongBao() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/tao-thong-bao.fxml"));
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Tạo thông báo");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);

            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            loadData();
            filterNotifications();
            showThongBao(thongBaoData);
        } catch (IOException e) {
            log.error("Error loading the create notification dialog: ", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load create notification dialog");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } catch (SQLException e) {
            log.error("Error loading data: ", e);
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to load data");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void filterNotifications() {
        String selectedCapBac = capbacCombo.getValue();
        String selectedCoSo = cosoCombo.getValue();
        String selectedLinhVuc = linhVucCombo.getValue();

        FilteredList<ThongBao> filteredData = new FilteredList<>(thongBaoData, b -> true);

        filteredData.setPredicate(thongBao -> {
            boolean matchesCapBac = selectedCapBac.equals("Tất cả") || thongBao.getMaCapBac().get().equals(selectedCapBac);
            boolean matchesCoSo = selectedCoSo.equals("Tất cả") || thongBao.getMaCoSo().get().equals(selectedCoSo);
            boolean matchesLinhVuc = selectedLinhVuc.equals("Tất cả") || thongBao.getLinhVuc().get().equals(selectedLinhVuc);

            return matchesCapBac && matchesCoSo && matchesLinhVuc;
        });

        showThongBao(filteredData);
    }
}
