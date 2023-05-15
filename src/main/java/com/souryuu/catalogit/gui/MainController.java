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

    private final FxWeaver fxWeaver;

    @FXML
    AnchorPane root, menuRoot, controlRoot, contentRoot;

    @FXML
    Button btnAddNewMovie;
    @FXML Button btnViewAllMovies;

    public MainController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void onBtnAddNewMovieAction() {
        changeDisplayedContent(MovieController.class);
    }

    public void onBtnViewAllMoviesAction() {
        changeDisplayedContent(MovieListController.class);
    }

    private void changeDisplayedContent(Class clazz) {
        contentRoot.getChildren().clear();
        Node newContent = (Node) fxWeaver.load(clazz).getView().get();
        contentRoot.getChildren().setAll(newContent);
    }

}
