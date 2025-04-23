package iss.kienephongthuyfvix.uniportal.controller.SV;

import iss.kienephongthuyfvix.uniportal.dao.DangKyDAO;
import iss.kienephongthuyfvix.uniportal.dao.SinhVienDAO;
import iss.kienephongthuyfvix.uniportal.model.DangKy;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class KetQuaHT {

    @FXML
    private TableColumn<DangKy, Double> diemckColumn;

    @FXML
    private TableColumn<DangKy, Double> diemqtColumn;

    @FXML
    private TextField diemtbField;

    @FXML
    private TableColumn<DangKy, Double> diemthColumn;

    @FXML
    private TableColumn<DangKy, Double> diemtkColumn;

    @FXML
    private TableColumn<DangKy, String> hkColumn;

    @FXML
    private ComboBox<String> hkCombo;

    @FXML
    private TableColumn<DangKy, String> mahpColumn;

    @FXML
    private TableView<DangKy> ketquaListView;

    @FXML
    private TableColumn<DangKy, Integer> sotcColumn;

    @FXML
    private TableColumn<DangKy, String> tenhpColumn;

    @FXML
    private final ObservableList<DangKy> ketquaData = FXCollections.observableArrayList();
    private final DangKyDAO dangKyDAO = new DangKyDAO();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private SinhVien sinhVien;
    private Double diemTBTatCa = 0.0;

    @FXML
    private void initialize() {
        try {
            sinhVien = sinhVienDAO.getCurrentSinhVien();
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        // Initialize columns
        mahpColumn.setCellValueFactory(data -> data.getValue().maHPProperty());
        tenhpColumn.setCellValueFactory(data -> data.getValue().tenHPProperty());
        hkColumn.setCellValueFactory(data -> new SimpleStringProperty("HK" + data.getValue().getHk()  + " " + data.getValue().getNam()));
        sotcColumn.setCellValueFactory(data -> data.getValue().soTCProperty().asObject());
        diemqtColumn.setCellValueFactory(data -> data.getValue().diemQTProperty().asObject());
        diemckColumn.setCellValueFactory(data -> data.getValue().diemThiProperty().asObject());
        diemthColumn.setCellValueFactory(data -> data.getValue().diemTHProperty().asObject());
        diemtkColumn.setCellValueFactory(data -> data.getValue().diemTKProperty().asObject());

        loadData();

        hkCombo.getItems().add("--Tất cả học kỳ--");
        hkCombo.getItems().addAll(
                ketquaData.stream()
                        .map(dangKy -> "HK" + dangKy.getHk() + " " + dangKy.getNam())
                        .distinct()
                        .toList()
        );
        hkCombo.setValue("--Tất cả học kỳ--");

        int totalCredits = 0;
        for (DangKy dangKy : ketquaData) {
            totalCredits += dangKy.getSoTC();
            diemTBTatCa += dangKy.getDiemTK() * dangKy.getSoTC();
        }
        if (totalCredits > 0) {
            diemTBTatCa /= totalCredits;
        }
        diemtbField.setText("ĐTB: " + String.format("%.2f", diemTBTatCa));

        hkCombo.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                filterTable(newValue);
            }
        });
    }

    private void loadData() {
        try {
            ketquaData.setAll(dangKyDAO.getKetQua(sinhVien.getMaSV()));
            ketquaListView.setItems(ketquaData);
            diemtbField.setText("ĐTB: " + String.format("%.2f", diemTBTatCa));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void filterTable(String hk) {
        if (hk.equals("--Tất cả học kỳ--")) {
            ketquaListView.setItems(ketquaData);
        } else {
            ObservableList<DangKy> filteredData = FXCollections.observableArrayList();
            Double diemTB = 0.0;
            int totalCredits = 0;
            for (DangKy dangKy : ketquaData) {
                String formattedHK = "HK" + dangKy.getHk() + " " + dangKy.getNam();
                if (formattedHK.equals(hk)) {
                    filteredData.add(dangKy);
                    totalCredits += dangKy.getSoTC();
                    diemTB += dangKy.getDiemTK() * dangKy.getSoTC();
                }
            }
            ketquaListView.setItems(filteredData);
            if (totalCredits > 0) {
                diemTB /= totalCredits;
                diemtbField.setText(String.format("%.2f", diemTB));
            } else {
                diemtbField.setText("0.00");
            }
        }
    }
}
