package iss.kienephongthuyfvix.uniportal.controller.DBA;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;

public class CapQuyen {
    @FXML private ToggleGroup granteeTypeGroup;
    @FXML private RadioButton userRadio;
    @FXML private RadioButton roleRadio;

    @FXML private CheckBox selectCheckBox;
    @FXML private CheckBox insertCheckBox;
    @FXML private CheckBox updateCheckBox;
    @FXML private CheckBox deleteCheckBox;
    @FXML private CheckBox executeCheckBox;

    @FXML private CheckBox selectGrantOption;
    @FXML private CheckBox insertGrantOption;
    @FXML private CheckBox updateGrantOption;
    @FXML private CheckBox deleteGrantOption;
    @FXML private CheckBox executeGrantOption;

    @FXML private AnchorPane selectColumnPermissionPane;
    @FXML private AnchorPane updateColumnPermissionPane;

    @FXML
    public void initialize() {
        userRadio.setToggleGroup(granteeTypeGroup);
        roleRadio.setToggleGroup(granteeTypeGroup);

        selectColumnPermissionPane.setVisible(false);
        updateColumnPermissionPane.setVisible(false);

        hideAllGrantOptions();

        bindGrantOptionVisibility(selectCheckBox, selectGrantOption);
        bindGrantOptionVisibility(insertCheckBox, insertGrantOption);
        bindGrantOptionVisibility(updateCheckBox, updateGrantOption);
        bindGrantOptionVisibility(deleteCheckBox, deleteGrantOption);
        bindGrantOptionVisibility(executeCheckBox, executeGrantOption);

        selectCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> updateColumnPermissionVisibility());
        updateCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> updateColumnPermissionVisibility());
    }

    private void bindGrantOptionVisibility(CheckBox mainCheckBox, CheckBox grantOptionCheckBox) {
        grantOptionCheckBox.setVisible(false);
        grantOptionCheckBox.managedProperty().bind(grantOptionCheckBox.visibleProperty());
        mainCheckBox.selectedProperty().addListener((obs, oldVal, newVal) -> grantOptionCheckBox.setVisible(newVal));
    }

    private void hideAllGrantOptions() {
        selectGrantOption.setVisible(false);
        insertGrantOption.setVisible(false);
        updateGrantOption.setVisible(false);
        deleteGrantOption.setVisible(false);
        executeGrantOption.setVisible(false);
    }

    private void updateColumnPermissionVisibility() {
        boolean show1 = selectCheckBox.isSelected();
        boolean show2 = updateCheckBox.isSelected();

        selectColumnPermissionPane.setVisible(show1);
        selectColumnPermissionPane.managedProperty().bind(selectColumnPermissionPane.visibleProperty());

        updateColumnPermissionPane.setVisible(show2);
        updateColumnPermissionPane.managedProperty().bind(updateColumnPermissionPane.visibleProperty());
    }
}
