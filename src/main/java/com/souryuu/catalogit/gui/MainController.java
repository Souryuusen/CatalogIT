package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.exception.ViewLoadException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@FxmlView("main-window-view.fxml")
public class MainController {

    private final FxWeaver fxWeaver;

    @FXML AnchorPane root;
    @FXML AnchorPane menuRoot;
    @FXML AnchorPane controlRoot;
    @FXML AnchorPane  contentRoot;

    @FXML Button btnAddNewMovie;
    @FXML Button btnViewAllMovies;

    public MainController(FxWeaver fxWeaver) {
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void onBtnAddNewMovieAction() {
        changeDisplayedContent(MovieController.class);
    }

    @FXML
    public void onBtnViewAllMoviesAction() {
        changeDisplayedContent(MovieListController.class);
    }

    @FXML
    public void onMenuItemCloseAction() {
        Platform.exit();
    }

    private void changeDisplayedContent(Class<?> clazz) {
        contentRoot.getChildren().clear();
        Optional<Node> viewOptional = fxWeaver.load(clazz).getView();
        if(viewOptional.isPresent()) {
            Node newContent = viewOptional.get();
            contentRoot.getChildren().setAll(newContent);
        }else {
            throw new ViewLoadException("Error Loading View From " + clazz.getName() + " !!");
        }
    }

}
