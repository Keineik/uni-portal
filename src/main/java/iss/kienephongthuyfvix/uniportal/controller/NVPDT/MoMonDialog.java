package iss.kienephongthuyfvix.uniportal.controller.NVPDT;

import iss.kienephongthuyfvix.uniportal.model.MoMon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import lombok.Getter;

public class MoMonDialog {

    @FXML
    private TextField mammField;

    @FXML
    private TextField mahpField;

    @FXML
    private TextField magvField;

    @FXML
    private TextField hkField;

    @FXML
    private TextField namField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @Getter
    private MoMon moMon;
    private boolean isSaveClicked = false;

    @FXML
    public void initialize() {
        saveButton.setOnAction(e -> handleSave());
        cancelButton.setOnAction(e -> handleCancel());
    }

    public void setMoMon(MoMon moMon) {
        this.moMon = moMon;

        if (moMon != null) {
            mammField.setText(String.valueOf(moMon.getMamm()));
            mahpField.setText(moMon.getMahp());
            magvField.setText(moMon.getMagv());
            hkField.setText(String.valueOf(moMon.getHk()));
            namField.setText(String.valueOf(moMon.getNam()));
        }
    }

    public boolean isSaveClicked() {
        return isSaveClicked;
    }

    private void handleSave() {
        if (moMon == null) {
            moMon = new MoMon(
                    Integer.parseInt(mammField.getText()),
                    mahpField.getText(),
                    magvField.getText(),
                    Integer.parseInt(hkField.getText()),
                    Integer.parseInt(namField.getText())
            );
        } else {
            moMon.setMamm(Integer.parseInt(mammField.getText()));
            moMon.setMahp(mahpField.getText());
            moMon.setMagv(magvField.getText());
            moMon.setHk(Integer.parseInt(hkField.getText()));
            moMon.setNam(Integer.parseInt(namField.getText()));
        }

        isSaveClicked = true;
        closeDialog();
    }

    private void handleCancel() {
        closeDialog();
    }

    private void closeDialog() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

}