package iss.kienephongthuyfvix.uniportal.controller.SV;

import iss.kienephongthuyfvix.uniportal.dao.DangKyDAO;
import iss.kienephongthuyfvix.uniportal.dao.MoMonDAO;
import iss.kienephongthuyfvix.uniportal.dao.SinhVienDAO;
import iss.kienephongthuyfvix.uniportal.model.MoMon;
import iss.kienephongthuyfvix.uniportal.model.SinhVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import org.kordamp.ikonli.javafx.FontIcon;

import java.sql.SQLException;

public class DangKyHP {

    @FXML
    private TableView<MoMon> momonDKListView;

    @FXML
    private TableView<MoMon> momonListView;

    @FXML
    private TableColumn<MoMon, String> mahpDKColumn, tenhpDKColumn;

    @FXML
    private TableColumn<MoMon, Integer> mammDKColumn, mammColumn;

    @FXML
    private TableColumn<MoMon, String> mahpColumn, tenhpColumn;

    @FXML
    private TableColumn<MoMon, Integer> stltColumn, stthColumn, sotcColumn;

    @FXML
    private TableColumn<MoMon, Integer> stltDKColumn, stthDKColumn, sotcDKColumn;

    @FXML
    private TableColumn<MoMon, Void> actionsDKColumn, actionsColumn;

    private final ObservableList<MoMon> daDangKyData = FXCollections.observableArrayList();
    private final ObservableList<MoMon> chuaDangKyData = FXCollections.observableArrayList();
    private final MoMonDAO moMonDAO = new MoMonDAO();
    private final DangKyDAO dangKyDAO = new DangKyDAO();
    private SinhVien sinhVien;
    private SinhVienDAO sinhVienDAO = new SinhVienDAO();

    @FXML
    public void initialize() {
        try {
            sinhVien = sinhVienDAO.getCurrentSinhVien();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Unable to load student data: " + e.getMessage());
            return;
        }
        // Initialize columns for DaDangKy
        mammDKColumn.setCellValueFactory(data -> data.getValue().mammProperty().asObject());
        mahpDKColumn.setCellValueFactory(data -> data.getValue().mahpProperty());
        tenhpDKColumn.setCellValueFactory(data -> data.getValue().tenHPProperty());
        sotcDKColumn.setCellValueFactory(data -> data.getValue().soTCProperty().asObject());
        stltDKColumn.setCellValueFactory(data -> data.getValue().stLTProperty().asObject());
        stthDKColumn.setCellValueFactory(data -> data.getValue().stTHProperty().asObject());

        // Initialize columns for ChuaDangKy
        mammColumn.setCellValueFactory(data -> data.getValue().mammProperty().asObject());
        mahpColumn.setCellValueFactory(data -> data.getValue().mahpProperty());
        tenhpColumn.setCellValueFactory(data -> data.getValue().tenHPProperty());
        sotcColumn.setCellValueFactory(data -> data.getValue().soTCProperty().asObject());
        stltColumn.setCellValueFactory(data -> data.getValue().stLTProperty().asObject());
        stthColumn.setCellValueFactory(data -> data.getValue().stTHProperty().asObject());

        loadData();

        addActionButtons();
    }

    private void loadData() {
        try {
            daDangKyData.setAll(moMonDAO.getAllDaDangKy(sinhVien.getMaSV()));
            chuaDangKyData.setAll(moMonDAO.getAllChuaDangKy(sinhVien.getMaSV()));

            momonDKListView.setItems(daDangKyData);
            momonListView.setItems(chuaDangKyData);
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Unable to load data: " + e.getMessage());
        }
    }

    private void addActionButtons() {
        actionsDKColumn.setCellFactory(col -> new TableCell<>() {
            private final Button removeButton = new Button();

            {
                FontIcon removeIcon = new FontIcon("fas-trash");
                removeIcon.setIconColor(Color.RED);
                removeIcon.setIconSize(14);
                removeButton.setGraphic(removeIcon);
                removeButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                removeButton.setOnAction(event -> {
                    MoMon moMon = getTableView().getItems().get(getIndex());
                    removeDangKy(moMon);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(removeButton);
                }
            }
        });

        actionsColumn.setCellFactory(col -> new TableCell<>() {
            private final Button addButton = new Button();

            {
                FontIcon addIcon = new FontIcon("fas-plus");
                addIcon.setIconColor(Color.GREEN);
                addIcon.setIconSize(14);
                addButton.setGraphic(addIcon);
                addButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                addButton.setOnAction(event -> {
                    MoMon moMon = getTableView().getItems().get(getIndex());
                    addDangKy(moMon);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(addButton);
                }
            }
        });
    }

    private void removeDangKy(MoMon moMon) {
        try {
            dangKyDAO.deleteDangKy(sinhVien.getMaSV(), moMon.getMamm());
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Unable to remove registration: " + e.getMessage());
        }
    }

    private void addDangKy(MoMon moMon) {
        try {
            dangKyDAO.insertDangKy(sinhVien.getMaSV(), moMon.getMamm());
            loadData();
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Database Error", "Unable to add registration: " + e.getMessage());
        }
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}