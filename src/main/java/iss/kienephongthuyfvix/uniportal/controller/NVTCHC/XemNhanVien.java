package iss.kienephongthuyfvix.uniportal.controller.NVTCHC;

import iss.kienephongthuyfvix.uniportal.dao.NhanVienDAO;
import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class XemNhanVien {

    @FXML private TableView<NhanVien> tableNhanVien;
    @FXML private TableColumn<NhanVien, String> colHoTen;
    @FXML private TableColumn<NhanVien, Void> colXem;
    @FXML private TableColumn<NhanVien, Void> colSua;
    @FXML private TableColumn<NhanVien, Void> colXoa;
    @FXML private TextField searchField;

    private ObservableList<NhanVien> masterData = FXCollections.observableArrayList();
    private FilteredList<NhanVien> filteredData;
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    @FXML
    public void initialize() {
        tableNhanVien.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colHoTen.setCellValueFactory(data -> data.getValue().hotenProperty());

        addButtonsToTable();
        loadAllNhanVien();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(nv -> {
                if (newValue == null || newValue.isBlank()) return true;
                return nv.getHoten().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

    private void addButtonsToTable() {
        addButton(colXem, "/iss/kienephongthuyfvix/uniportal/img/view.png", "Chi tiết", this::showChiTietNhanVien);
        addButton(colSua, "/iss/kienephongthuyfvix/uniportal/img/edit.png", "Sửa", this::editNhanVien);
        addButton(colXoa, "/iss/kienephongthuyfvix/uniportal/img/delete.png", "Xóa", this::deleteNhanVien);
    }

    private void addButton(TableColumn<NhanVien, Void> column, String iconPath, String tooltip, java.util.function.Consumer<NhanVien> handler) {
        column.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button();

            {
                ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream(iconPath))));
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                btn.setGraphic(imageView);
                btn.setStyle("-fx-background-color: transparent;");
                btn.setTooltip(new Tooltip(tooltip));
                btn.setOnAction(event -> {
                    NhanVien nv = getTableView().getItems().get(getIndex());
                    handler.accept(nv);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(btn));
            }
        });
    }

    private void loadAllNhanVien() {
        try {
            List<NhanVien> list = nhanVienDAO.getAllNhanVien();
            masterData.setAll(list);

            filteredData = new FilteredList<>(masterData, p -> true);
            tableNhanVien.setItems(filteredData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showChiTietNhanVien(NhanVien nv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVTCHC/xem-chi-tiet-nv.fxml"));
            Scene scene = new Scene(loader.load());

            XemChiTietNV controller = loader.getController();
            controller.setNhanVien(nv);

            Stage stage = new Stage();
            stage.setTitle("Chi tiết nhân viên");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void editNhanVien(NhanVien nv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVTCHC/sua-nhan-vien.fxml"));
            Scene scene = new Scene(loader.load());

            SuaNhanVien controller = loader.getController();
            controller.setNhanVien(nv);

            Stage stage = new Stage();
            stage.setTitle("Chỉnh sửa nhân viên");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteNhanVien(NhanVien nv) {
        // Hiển thị hộp thoại xác nhận trước khi xóa
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Xác nhận xóa");
        confirmAlert.setHeaderText("Bạn có chắc chắn muốn xóa nhân viên này?");
        confirmAlert.setContentText("Hành động này không thể hoàn tác.");

        // Nếu người dùng chọn "OK", tiến hành xóa nhân viên
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    boolean deleted = nhanVienDAO.deleteNhanVien(nv.getManv());
                    if (deleted) {
                        new Alert(Alert.AlertType.INFORMATION, "Xóa nhân viên thành công.").showAndWait();
                        loadAllNhanVien(); // Cập nhật lại danh sách sau khi xóa
                    } else {
                        new Alert(Alert.AlertType.ERROR, "Không thể xóa nhân viên.").showAndWait();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    new Alert(Alert.AlertType.ERROR, "Lỗi khi xóa nhân viên: " + e.getMessage()).showAndWait();
                }
            }
        });
    }
}
