package iss.kienephongthuyfvix.uniportal.controller.TDV;

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
    @FXML private Label lblDonVi;
    @FXML private TextField searchField;

    private ObservableList<NhanVien> masterData = FXCollections.observableArrayList();
    private FilteredList<NhanVien> filteredData;
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    @FXML
    public void initialize() {
        tableNhanVien.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colHoTen.setCellValueFactory(data -> data.getValue().hotenProperty());

        addViewButtonToTable();
        loadNhanVienByCurrentDonVi();

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(nv -> {
                if (newValue == null || newValue.isBlank()) return true;
                return nv.getHoten().toLowerCase().contains(newValue.toLowerCase());
            });
        });
    }

    private void addViewButtonToTable() {
        colXem.setCellFactory(param -> new TableCell<>() {
            final Button btn = new Button();

            {
                ImageView imageView = new ImageView(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/iss/kienephongthuyfvix/uniportal/img/view.png"))));
                imageView.setFitWidth(16);
                imageView.setFitHeight(16);
                btn.setGraphic(imageView);
                btn.setStyle("-fx-background-color: transparent;");
                btn.setOnAction(event -> {
                    NhanVien nv = getTableView().getItems().get(getIndex());
                    showChiTietNhanVien(nv);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : new HBox(btn));
            }
        });
    }

    private void loadNhanVienByCurrentDonVi() {
        try {
            List<NhanVien> list = nhanVienDAO.TRGDV_getAllNhanVien();
            masterData.setAll(list);

            filteredData = new FilteredList<>(masterData, p -> true);
            tableNhanVien.setItems(filteredData);

            if (!list.isEmpty()) {
                lblDonVi.setText("Đơn vị: " + list.getFirst().getMadv());
            } else {
                lblDonVi.setText("Không có nhân viên");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void showChiTietNhanVien(NhanVien nv) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/TDV/xem-chi-tiet-nv.fxml"));
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
}
