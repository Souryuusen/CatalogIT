package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.database.Genre;
import com.souryuu.catalogit.entity.database.Movie;
import com.souryuu.catalogit.entity.database.Review;
import com.souryuu.catalogit.exception.ViewLoadException;
import com.souryuu.catalogit.service.MovieService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@FxmlView("main-window-view.fxml")
public class MainController {

    private final FxWeaver fxWeaver;
    private final MovieService movieService;

    @FXML AnchorPane root;
    @FXML AnchorPane menuRoot;
    @FXML AnchorPane controlRoot;
    @FXML AnchorPane  contentRoot;

    @FXML Button btnAddNewMovie;
    @FXML Button btnViewAllMovies;

    public MainController(FxWeaver fxWeaver, MovieService movieService) {
        this.fxWeaver = fxWeaver;
        this.movieService = movieService;
    }

    @FXML
    public void onBtnExportToTxtAction() {
        Set<Movie> fetchedMovies = movieService.getAllMoviesWithInitialization();
        var sortedMovies = fetchedMovies.stream().sorted(new Movie.MovieIdComparator()).collect(Collectors.toList());

        StringBuilder sb = new StringBuilder();
        for(Movie m : sortedMovies) {
            if (sb.length() != 0) {
                sb.append("\n===== ===== ===== ===== =====\n");
            }
            sb.append(m.createExportSummary());
        }
        File f = new File("C:\\Users\\grzeg\\OneDrive\\tempfile.txt");
        try {
            Files.writeString(f.getAbsoluteFile().toPath(), sb.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

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
