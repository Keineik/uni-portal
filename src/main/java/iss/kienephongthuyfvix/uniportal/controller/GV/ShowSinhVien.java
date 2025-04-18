package iss.kienephongthuyfvix.uniportal.controller.GV;

import iss.kienephongthuyfvix.uniportal.dao.SinhVienDAO;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.List;

public class ShowSinhVien {

    @FXML
    private TableView<iss.kienephongthuyfvix.uniportal.model.SinhVien> tableSinhVien;

    @FXML
    private TableColumn<iss.kienephongthuyfvix.uniportal.model.SinhVien, String> colMaSV;

    @FXML
    private TableColumn<iss.kienephongthuyfvix.uniportal.model.SinhVien, String> colHoTen;

    @FXML
    private TableColumn<iss.kienephongthuyfvix.uniportal.model.SinhVien, String> colPhai;

    @FXML
    private TableColumn<iss.kienephongthuyfvix.uniportal.model.SinhVien, java.util.Date> colNgaySinh;

    @FXML
    private TableColumn<iss.kienephongthuyfvix.uniportal.model.SinhVien, String> colDiaChi;

    @FXML
    private TableColumn<iss.kienephongthuyfvix.uniportal.model.SinhVien, String> colDienThoai;

    @FXML
    private TableColumn<iss.kienephongthuyfvix.uniportal.model.SinhVien, String> colKhoa;

    @FXML
    private TableColumn<iss.kienephongthuyfvix.uniportal.model.SinhVien, String> colTinhTrang;

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();

    private String maKhoa;

    public void setMaKhoa(String maKhoa) {
        this.maKhoa = maKhoa;
        loadSinhVienTheoKhoa(); // Gọi hàm lọc ngay sau khi set
    }

    private void loadSinhVienTheoKhoa() {
        try {
            List<iss.kienephongthuyfvix.uniportal.model.SinhVien> list = sinhVienDAO.getSinhVienByKhoa(maKhoa);
            ObservableList<iss.kienephongthuyfvix.uniportal.model.SinhVien> data = FXCollections.observableArrayList(list);
            tableSinhVien.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void initialize() {
        tableSinhVien.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        colMaSV.setCellValueFactory(new PropertyValueFactory<>("maSV"));
        colHoTen.setCellValueFactory(new PropertyValueFactory<>("hoTen"));
        colPhai.setCellValueFactory(new PropertyValueFactory<>("phai"));
        colNgaySinh.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));
        colDienThoai.setCellValueFactory(new PropertyValueFactory<>("dienThoai"));
        colKhoa.setCellValueFactory(new PropertyValueFactory<>("khoa"));
        colTinhTrang.setCellValueFactory(new PropertyValueFactory<>("tinhTrang"));
    }
    public void loadSinhVienByMaMM(int maMM) {
        try {
            List<iss.kienephongthuyfvix.uniportal.model.SinhVien> list = sinhVienDAO.getSinhVienByMaMM(maMM);
            ObservableList<iss.kienephongthuyfvix.uniportal.model.SinhVien> data = FXCollections.observableArrayList(list);
            tableSinhVien.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
