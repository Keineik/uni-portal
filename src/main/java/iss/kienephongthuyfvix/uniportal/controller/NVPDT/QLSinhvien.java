package iss.kienephongthuyfvix.uniportal.controller.NVPDT;

import iss.kienephongthuyfvix.uniportal.dao.SinhVienDAO;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.ComboBoxTableCell;

import java.sql.SQLException;
import java.util.Date;

public class QLSinhvien {

    @FXML
    private TableColumn<SinhVien, String> diaChiColumn;

    @FXML
    private TableColumn<SinhVien, String> hoTenColumn;

    @FXML
    private TableColumn<SinhVien, String> khoaColumn;

    @FXML
    private TableColumn<SinhVien, String> maSVColumn;

    @FXML
    private TableView<SinhVien> tableView;

    @FXML
    private TableColumn<SinhVien, Date> ngaySinhColumn;

    @FXML
    private TableColumn<SinhVien, String> dienThoaiColumn;

    @FXML
    private TableColumn<SinhVien, String> phaiColumn;

    @FXML
    private TextField searchBar;

    @FXML
    private TableColumn<SinhVien, String> tinhTrangColumn;

    private final ObservableList<SinhVien> sinhVienData = FXCollections.observableArrayList();

    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();

    @FXML
    private void initialize() {
        maSVColumn.setCellValueFactory(data -> data.getValue().maSVProperty());
        hoTenColumn.setCellValueFactory(data -> data.getValue().hoTenProperty());
        phaiColumn.setCellValueFactory(data -> data.getValue().phaiProperty());
        diaChiColumn.setCellValueFactory(data -> data.getValue().diaChiProperty());
        dienThoaiColumn.setCellValueFactory(data -> data.getValue().dienThoaiProperty());
        khoaColumn.setCellValueFactory(data -> data.getValue().khoaProperty());
        ngaySinhColumn.setCellValueFactory(data -> data.getValue().ngaySinhProperty());
        tinhTrangColumn.setCellValueFactory(data -> data.getValue().tinhTrangProperty());

        loadSinhVienData();

        addComboBoxToTinhTrangColumn();

        searchBar.textProperty().addListener((observable, oldValue, newValue) -> {
            filterSinhVienList(newValue);
        });
    }

    private void filterSinhVienList(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            tableView.setItems(sinhVienData);
            return;
        }

        ObservableList<SinhVien> filteredList = FXCollections.observableArrayList();
        for (SinhVien sinhVien : sinhVienData) {
            if (sinhVien.getMaSV().toLowerCase().contains(keyword.toLowerCase()) ||
                sinhVien.getHoTen().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(sinhVien);
            }
        }
        tableView.setItems(filteredList);
    }

    private void addComboBoxToTinhTrangColumn() {
        ObservableList<String> tinhTrangOptions = FXCollections.observableArrayList("Đang học", "Nghỉ học", "Bảo lưu", "Đã tốt nghiệp");
        tinhTrangColumn.setCellFactory(col -> new TableCell<>() {
            private final ComboBox<String> comboBox = new ComboBox<>(tinhTrangOptions);
            {
                comboBox.setOnAction(event -> {
                    SinhVien sinhVien = getTableView().getItems().get(getIndex());
                    String selectedValue = comboBox.getValue();
                    if (selectedValue != null) {
                        sinhVien.setTinhTrang(selectedValue);
                        updateTinhTrangInDatabase(sinhVien);
                    }
                });

                // Add a listener to change the ComboBox color based on the selection
                comboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        switch (newValue) {
                            case "Đang học":
                                comboBox.setStyle("-fx-background-color: lightgreen;");
                                break;
                            case "Nghỉ học":
                                comboBox.setStyle("-fx-background-color: lightcoral;");
                                break;
                            case "Bảo lưu":
                                comboBox.setStyle("-fx-background-color: lightblue;");
                                break;
                            case "Đã tốt nghiệp":
                                comboBox.setStyle("-fx-background-color: lightgoldenrodyellow;");
                                break;
                            default:
                                comboBox.setStyle(""); // Reset to default
                                break;
                        }
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    comboBox.setValue(item);
                    setGraphic(comboBox);
                }
            }
        });
    }

    private void loadSinhVienData() {
        sinhVienData.clear();
        try {
            sinhVienData.addAll(sinhVienDAO.getAllSinhVien());
            tableView.setItems(sinhVienData);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(sinhVienData);
    }

    private void updateTinhTrangInDatabase(SinhVien sinhVien) {
        try {
            sinhVienDAO.updateSinhVien(sinhVien);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
