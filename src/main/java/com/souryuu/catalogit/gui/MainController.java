package com.souryuu.catalogit.gui;

import javafx.application.HostServices;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@FxmlView("main-window-view.fxml")
public class MainController {

    private final HostServices hostServices;
    private final FxWeaver fxWeaver;

    @FXML
    AnchorPane root, menuRoot, controlRoot, contentRoot;

    @FXML
    Button btnTest;

    @FXML
    SubScene contentSubScene;

//    @FXML
//    TextField keywordTextField;

    @FXML
    Button searchButton;


    public MainController(HostServices hostServices, FxWeaver fxWeaver) {
        this.hostServices = hostServices;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void onBtnTestAction() {
        changeDisplayedContent(MovieController.class);
    }

    private void changeDisplayedContent(Class clazz) {
        Node newContent = (Node) fxWeaver.load(clazz).getView().get();
        contentRoot.getChildren().setAll(newContent);
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
