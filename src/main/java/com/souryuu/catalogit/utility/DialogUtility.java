package com.souryuu.catalogit.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class DialogUtility {

    public static Optional<ButtonType> createConfirmationDialog(String title, String message, String affirmativeAction, String negativeAction) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.getButtonTypes().clear();
        alert.setTitle(title);
        alert.setContentText(message);
        alert.getButtonTypes().add(new ButtonType(affirmativeAction));
        alert.getButtonTypes().add(new ButtonType(negativeAction));
        return alert.showAndWait();
    }

    public static void createErrorDialog(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.getButtonTypes().clear();
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.getButtonTypes().add(new ButtonType("OK"));
    }

}
