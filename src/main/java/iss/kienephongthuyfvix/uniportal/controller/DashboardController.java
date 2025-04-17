package iss.kienephongthuyfvix.uniportal.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DashboardController {
    @FXML
    private VBox sidebarContainer;

    @FXML
    private StackPane contentArea;

    private List<JFXButton> sidebarButtons = new ArrayList<>();

    @FXML
    void initialize() {
        // Initialization code if needed
    }

    public void initializeRole(String role) {
        addSidebarButtons(role);
    }

    private void addSidebarButtons(String employeeType) {
        sidebarContainer.getChildren().clear();
        sidebarButtons.clear();
        switch (employeeType) {
            case "DBA":
                sidebarButtons.add(createButton("Quản lý user", "#QLUser"));
                sidebarButtons.add(createButton("Quản lý role", "#QLRole"));
                sidebarButtons.add(createButton("Cấp quyền", "#CapQuyen"));
                sidebarButtons.add(createButton("Phát thông báo", "#PhatTB"));
                break;

            case "Trưởng đơn vị":
                sidebarButtons.add(createButton("Thông tin cá nhân", "#ThongTinNhanVien"));
                break;

            case "Sinh viên":
                sidebarButtons.add(createButton("Thông tin cá nhân", "#ThongTinSinhVien"));
                sidebarButtons.add(createButton("Đăng ký học phần", "#DangKyHP"));
                sidebarButtons.add(createButton("Kết quả học tập", "#KetQuaHT"));
                break;

            case "Nhân viên Phòng Công tác Sinh viên":
                sidebarButtons.add(createButton("Thông tin cá nhân", "#ThongTinNhanVien"));
                break;

            case "Nhân viên Phòng Khảo thí":
                sidebarButtons.add(createButton("Thông tin cá nhân", "#ThongTinNhanVien"));
                break;

            case "Nhân viên Phòng Tổ chức Hành chính":
                sidebarButtons.add(createButton("Thông tin cá nhân", "#ThongTinNhanVien"));
                break;

            case "Nhân viên Phòng Đào tạo":
                sidebarButtons.add(createButton("Thông tin cá nhân", "#ThongTinNhanVien"));
                sidebarButtons.add(createButton("Quản lí sinh viên", "#PDT_QLSV"));
                sidebarButtons.add(createButton("Quản lí môn học", "#PDT_QLMH"));
                sidebarButtons.add(createButton("Quản lí đăng ký học phần", "#PDT_QLDK"));
                break;

            case "Giảng viên":
                sidebarButtons.add(createButton("Thông tin cá nhân", "#ThongTinNhanVien"));
                break;
        }
        sidebarContainer.getChildren().addAll(sidebarButtons);
    }

    private JFXButton createButton(String text, String action) {
        JFXButton button = new JFXButton(text);
        button.setAlignment(javafx.geometry.Pos.TOP_LEFT);
        button.setPrefHeight(32.0);
        button.setPrefWidth(174.0);
        button.getStyleClass().add("invisible-button");
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: WHITE");
        button.setOnAction(event -> {
            try {
                handleButtonAction(action, button);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        button.setFont(new Font("System Bold", 12.0));
        return button;
    }

    private void handleButtonAction(String action, JFXButton button) throws IOException {
        resetButtonStyles();
        button.setStyle("-fx-background-color: #77dbb8; -fx-background-radius: 10px; -fx-text-fill: WHITE");
        switch (action) {
            case "#QLUser":
                QuanLyUser(new ActionEvent());
                break;
            case "#QLRole":
                QuanLyRole(new ActionEvent());
                break;
            case "#ThongTinNhanVien":
                ThongTinNhanVien(new ActionEvent());
                break;
            case "#PDT_QLMH":
                PDT_QLMH(new ActionEvent());
                break;
            case "#PDT_QLSV":
                PDT_QLSV(new ActionEvent());
                break;
            case "#PhatTB":
                PhatThongBao(new ActionEvent());
                break;
            case "#CapQuyen":
                CapQuyen(new ActionEvent());
                break;
            case "#PDT_QLDK":
                PDT_QLDK(new ActionEvent());
                break;
            case "#ThongTinSinhVien":
                ThongTinSinhVien(new ActionEvent());
                break;
            case "#DangKyHP":
                // Handle DangKyHP action
                break;
            case "#KetQuaHT":
                // Handle KetQuaHT action
                break;
            // Add more cases for other actions
        }
    }

    private void resetButtonStyles() {
        for (JFXButton button : sidebarButtons) {
            button.setStyle("-fx-background-color: transparent; -fx-text-fill: WHITE");
        }
    }

    @FXML
    void QuanLyUser(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/quan-ly-user.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }

    @FXML
    void QuanLyRole(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/quan-ly-role.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }

    @FXML
    void CapQuyen(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/cap-quyen.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }

    @FXML
    void PhatThongBao(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/iss/kienephongthuyfvix/uniportal/DBA/phat-thong-bao.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }


    @FXML
    void ThongTinNhanVien(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVCB/thong-tin.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }

    @FXML
    void PDT_QLMH(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVPDT/ql-monhoc.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }

    @FXML
    void PDT_QLSV(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVPDT/ql-sinhvien.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }

    @FXML
    void PDT_QLDK(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVPDT/ql-dangky.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }

    @FXML
    void ThongTinSinhVien(ActionEvent event) throws IOException {
        Parent fxml = FXMLLoader.load(getClass().getResource("/iss/kienephongthuyfvix/uniportal/SV/thong-tin.fxml"));
        contentArea.getChildren().clear();
        contentArea.getChildren().add(fxml);
    }
}
