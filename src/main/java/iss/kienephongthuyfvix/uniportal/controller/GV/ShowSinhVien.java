package iss.kienephongthuyfvix.uniportal.controller.GV;

import iss.kienephongthuyfvix.uniportal.dao.DonViDAO;
import iss.kienephongthuyfvix.uniportal.dao.MoMonDAO;
import iss.kienephongthuyfvix.uniportal.dao.NhanVienDAO;
import iss.kienephongthuyfvix.uniportal.dao.SinhVienDAO;
import iss.kienephongthuyfvix.uniportal.model.DonVi;
import iss.kienephongthuyfvix.uniportal.model.MoMon;
import iss.kienephongthuyfvix.uniportal.model.NhanVien;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
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

    @FXML
    private Label titleLabel;

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();

    private String maKhoa;
    private final DonViDAO donViDAO = new DonViDAO();
    private final MoMonDAO moMonDAO = new MoMonDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private NhanVien nhanVien;

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

        try {
            nhanVien = nhanVienDAO.getCurrentNhanVien();
            maKhoa = nhanVien.getMadv();
            loadSinhVienTheoKhoa();
        } catch (SQLException e) {
        }
    }

    public void loadSinhVienTheoKhoa() {
        try {
            List<SinhVien> list = sinhVienDAO.getSinhVienByKhoa(maKhoa);
            ObservableList<SinhVien> data = FXCollections.observableArrayList(list);
            DonVi donVi = donViDAO.getDonVi(maKhoa);
            titleLabel.setText("Danh sách sinh viên khoa " + donVi.getTenDV());
            tableSinhVien.setItems(data);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void loadSinhVienByMaMM(int maMM) {
        try {
            List<SinhVien> list = sinhVienDAO.getSinhVienByMaMM(maMM);
            ObservableList<SinhVien> data = FXCollections.observableArrayList(list);
            tableSinhVien.setItems(data);
            MoMon moMon = moMonDAO.getMoMonById(maMM);
            titleLabel.setText("Danh sách lớp môn " + moMon.getMahp() + "-" + moMon.getTenHP());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
