package com.souryuu.catalogit.gui;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("main-window-view.fxml")
public class MainController {

    private final HostServices hostServices;
    private final FxWeaver fxWeaver;

//    @FXML
//    TextField keywordTextField;

    @FXML
    Button searchButton;

    @FXML
    AnchorPane root;

    public MainController(HostServices hostServices, FxWeaver fxWeaver) {
        this.hostServices = hostServices;
        this.fxWeaver = fxWeaver;
    }

//    @FXML
//    public void initialize() {
//        this.searchButton.setOnAction(action ->{
//            this.hostServices.showDocument("https://www.google.ca/search?q=" + this.keywordTextField.getText().trim());
//        });
//    }

//    @FXML
//    public void aboutMenuItemClicked() {
////        this.fxWeaver.loadController(AboutController.class).show();
////        this.fxWeaver.loadView(AboutController.class);
//        ((Stage)root.getScene().getWindow()).setScene(this.fxWeaver.loadView(AboutController.class).getScene());
//    }
}
