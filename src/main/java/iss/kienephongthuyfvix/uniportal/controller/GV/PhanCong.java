package iss.kienephongthuyfvix.uniportal.controller.GV;

import iss.kienephongthuyfvix.uniportal.dao.MoMonDAO;
import iss.kienephongthuyfvix.uniportal.model.MoMon;
import iss.kienephongthuyfvix.uniportal.dao.DangKyDAO;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Callback;

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
    private final DangKyDAO dangKyDAO = new DangKyDAO(); // đảm bảo bạn đã tạo class này

    private final String maGV = "NV00000002"; // Thay bằng mã GV thực tế từ session đăng nhập

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
        List<MoMon> moMons = moMonDAO.getAllMoMon();
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
                            showSinhVienDialog(moMon.getMamm());
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
            }
        });
    }

    private void showSinhVienDialog(int maMM) {
        try {
            List<SinhVien> svList = dangKyDAO.getSinhVienByMaMM(maMM);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Danh sách sinh viên đăng ký");
            alert.setHeaderText("Lớp MAMM: " + maMM);

            StringBuilder content = new StringBuilder();
            for (SinhVien sv : svList) {
                content.append(sv.getMaSV()).append(" - ").append(sv.getHoTen()).append("\n");
            }

            if (svList.isEmpty()) {
                content.append("Chưa có sinh viên đăng ký.");
            }

            alert.setContentText(content.toString());
            alert.showAndWait();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
