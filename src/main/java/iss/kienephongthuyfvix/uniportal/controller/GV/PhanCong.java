package iss.kienephongthuyfvix.uniportal.controller.GV;

import iss.kienephongthuyfvix.uniportal.dao.MoMonDAO;
import iss.kienephongthuyfvix.uniportal.model.MoMon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

public class PhanCong {

    @FXML
    private TableView<MoMon> tablePhanCong;

    @FXML
    private TableColumn<MoMon, Integer> colMaMM;

    @FXML
    private TableColumn<MoMon, String> colTenHP;

    @FXML
    private TableColumn<MoMon, Integer> colHK;

    @FXML
    private TableColumn<MoMon, Integer> colNam;

    @FXML
    private TableColumn<MoMon, Void> colXem;

    private final MoMonDAO moMonDAO = new MoMonDAO();
    private final String maGV = "NV00000002"; // Giả lập mã giảng viên

    @FXML
    public void initialize() {
        tablePhanCong.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        try {
            loadPhanCong();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadPhanCong() throws SQLException {
        List<MoMon> moMons = moMonDAO.getAllMoMonGV();
        ObservableList<MoMon> data = FXCollections.observableArrayList();

        for (MoMon m : moMons) {
            if (m.getMagv().equals(maGV)) {
                data.add(m);
            }
        }

        colMaMM.setCellValueFactory(new PropertyValueFactory<>("mamm"));
        colTenHP.setCellValueFactory(new PropertyValueFactory<>("mahp"));
        colHK.setCellValueFactory(new PropertyValueFactory<>("hk"));
        colNam.setCellValueFactory(new PropertyValueFactory<>("nam"));

        addXemButton();

        tablePhanCong.setItems(data);
    }

    private void addXemButton() {
        colXem.setCellFactory(new Callback<>() {
            @Override
            public TableCell<MoMon, Void> call(final TableColumn<MoMon, Void> param) {
                return new TableCell<>() {
                    private final Button btn = new Button();

                    {
                        ImageView icon = new ImageView(new Image(getClass().getResourceAsStream("/iss/kienephongthuyfvix/uniportal/img/view.png")));
                        icon.setFitHeight(16);
                        icon.setFitWidth(16);
                        btn.setGraphic(icon);
                        btn.setStyle("-fx-background-color: transparent;");
                        btn.setOnAction(e -> {
                            MoMon moMon = getTableView().getItems().get(getIndex());
                            openSinhVienWindow(moMon.getMamm());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        setGraphic(empty ? null : btn);
                    }
                };
            }
        });
    }

    private void openSinhVienWindow(int maMM) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/GV/show-sinh-vien.fxml"));
            Parent root = loader.load();

            ShowSinhVien controller = loader.getController();
            controller.loadSinhVienByMaMM(maMM);

            Stage stage = new Stage();
            stage.setTitle("Danh sách sinh viên đăng ký - MAMM: " + maMM);
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
