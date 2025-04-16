package iss.kienephongthuyfvix.uniportal.controller.NVPDT;

import iss.kienephongthuyfvix.uniportal.dao.MoMonDAO;
import iss.kienephongthuyfvix.uniportal.model.MoMon;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.sql.SQLException;

public class QLMonhoc {

    @FXML
    private TextField searchBar;

    @FXML
    private TableColumn<MoMon, Void> actionColumn;

    @FXML
    private TableColumn<MoMon, Integer> hkColumn;

    @FXML
    private TableColumn<MoMon, String> maGVColumn;

    @FXML
    private TableColumn<MoMon, String> maHPColumn;

    @FXML
    private TableColumn<MoMon, Integer> maMMColumn;

    @FXML
    private TableColumn<MoMon, Integer> namColumn;

    @FXML
    private TableView<MoMon> moMonListView;

    private final ObservableList<MoMon> moMonData = FXCollections.observableArrayList();

    private final MoMonDAO moMonDAO = new MoMonDAO();

    @FXML
    public void initialize() {
        // Initialize table columns
        maMMColumn.setCellValueFactory(data -> data.getValue().mammProperty().asObject());
        maHPColumn.setCellValueFactory(data -> data.getValue().mahpProperty());
        maGVColumn.setCellValueFactory(data -> data.getValue().magvProperty());
        hkColumn.setCellValueFactory(data -> data.getValue().hkProperty().asObject());
        namColumn.setCellValueFactory(data -> data.getValue().namProperty().asObject());

        // Load data into the table
        loadMoMonData();

        // Add action buttons to the action column
        addActionButtons();

        // Set up search functionality
        searchBar.textProperty().addListener((observable, oldValue, newValue) -> filterTable(newValue));
    }

    private void loadMoMonData() {
        moMonData.clear();
        try {
            moMonData.addAll(moMonDAO.getAllMoMon());
            moMonListView.setItems(moMonData);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error loading data from the database: " + e.getMessage());
        }
    }

    private void saveMoMonToDatabase(MoMon moMon) {
        try {
            if (moMonData.contains(moMon)) {
                moMonDAO.updateMoMon(moMon);
            } else {
                moMonDAO.insertMoMon(moMon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error saving data to the database: " + e.getMessage());
        }
    }

    private void filterTable(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            moMonListView.setItems(moMonData);
            return;
        }

        ObservableList<MoMon> filteredList = FXCollections.observableArrayList();
        for (MoMon moMon : moMonData) {
            if (String.valueOf(moMon.getMamm()).contains(keyword.toLowerCase()) ||
                    moMon.getMahp().toLowerCase().contains(keyword.toLowerCase()) ||
                    moMon.getMagv().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(moMon);
            }
        }
        moMonListView.setItems(filteredList);
    }

    private void addActionButtons() {
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button();
            private final Button deleteButton = new Button();
            private final HBox actionBox = new HBox(5, editButton, deleteButton);

            {
                // Edit button
                FontIcon editIcon = new FontIcon("fas-pen");
                editIcon.setIconColor(Color.BLACK);
                editIcon.setIconSize(14);
                editButton.setGraphic(editIcon);
                editButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                editButton.setOnAction(event -> {
                    MoMon moMon = getTableView().getItems().get(getIndex());
                    openMoMonDialog(moMon);
                });

                // Delete button
                FontIcon deleteIcon = new FontIcon("fas-trash");
                deleteIcon.setIconColor(Color.BLACK);
                deleteIcon.setIconSize(14);
                deleteButton.setGraphic(deleteIcon);
                deleteButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand;");
                deleteButton.setOnAction(event -> {
                    MoMon moMon = getTableView().getItems().get(getIndex());
                    deleteMoMon(moMon);
                });

                actionBox.setStyle("-fx-alignment: CENTER;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(actionBox);
                }
            }
        });
    }

    private void deleteMoMon(MoMon moMon) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationDialog.setTitle("Xác nhận xóa");
        confirmationDialog.setHeaderText("Bạn có chắc muốn xóa môn này?");
        confirmationDialog.setContentText("Mã mở môn: " + moMon.getMamm());

        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noButton = new ButtonType("No", ButtonBar.ButtonData.NO);

        confirmationDialog.getButtonTypes().setAll(yesButton, noButton);

        confirmationDialog.showAndWait().ifPresent(response -> {
            if (response == yesButton) {
                try {
                    moMonDAO.deleteMoMon(moMon.getMamm());
                    moMonData.remove(moMon);
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Error deleting data from the database: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    void themMonHoc(ActionEvent event) {
        openMoMonDialog(null);
    }

    private void openMoMonDialog(MoMon moMon) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/iss/kienephongthuyfvix/uniportal/NVPDT/mo-mon-dialog.fxml"));
            Parent root = loader.load();

            MoMonDialog controller = loader.getController();
            controller.setMoMon(moMon);

            Stage dialogStage = new Stage();
            dialogStage.setTitle(moMon == null ? "Thêm Môn Học" : "Chỉnh Sửa Môn Học");
            dialogStage.setScene(new Scene(root));
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setResizable(false);
            dialogStage.showAndWait();

            if (controller.isSaveClicked()) {
                saveMoMonToDatabase(controller.getMoMon());
                if (moMon == null) {
                    moMonData.add(controller.getMoMon());
                } else {
                    moMonListView.refresh();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}