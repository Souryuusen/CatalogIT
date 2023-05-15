package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.entity.Review;
import com.souryuu.catalogit.entity.Writer;
import com.souryuu.catalogit.service.DirectorService;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.service.ReviewService;
import com.souryuu.catalogit.service.WriterService;
import com.souryuu.catalogit.utility.FXUtility;
import javafx.collections.FXCollections;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import lombok.*;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Component
@FxmlView("show-all-movies-view.fxml")
public class MovieListController {

    private final MovieService movieService;
    private final ReviewService reviewService;
    private final DirectorService directorService;
    private final WriterService writerService;
    private final FxWeaver fxWeaver;

    private Movie currentMovie;

    @FXML AnchorPane paneDetails;

    @FXML Button btnShowMovieDetails;
    @FXML Button btnShowReviewDetails;

    @FXML TextField fieldCurrentMovieID;
    @FXML TextField fieldCurrentMovieTitle;
    @FXML TextField fieldCurrentMovieImdbUrl;
    @FXML TextField fieldCurrentMovieRuntime;
    @FXML TextField fieldCurrentMovieLanguage;
    @FXML TextField fieldCurrentMovieReleaseDate;
    @FXML TextField fieldCurrentMovieCountryOfOrigin;

    @FXML ImageView imageCover;

    @FXML TableView tableMovies;
    @FXML TableView tableDirectors;
    @FXML TableView tableWriters;

    @FXML TableColumn columnMovieID;
    @FXML TableColumn columnMovieTitle;
    @FXML TableColumn columnMovieReviewAmount;
    @FXML TableColumn columnMovieAverageRating;
    @FXML TableColumn columnDirectorID;
    @FXML TableColumn columnDirectorName;
    @FXML TableColumn columnWriterID;
    @FXML TableColumn columnWriterName;

    public MovieListController(MovieService movieService, ReviewService reviewService, DirectorService directorService, WriterService writerService, FxWeaver fxWeaver) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.directorService = directorService;
        this.writerService = writerService;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void initialize() {
        fillTableWithMovieData(movieService.findAllWithReviews());
    }

    @FXML
    public void onBtnSearchAction() {

    }

    @FXML
    public void onBtnRefreshDatabaseAction() {

    }

    @FXML
    public void onBtnShowMovieDetailsAction() {
        changeDetailsPane(MovieListMovieDetailsController.class);
        if(currentMovie != null) {
            showMovieData(currentMovie);
        }
    }

    @FXML
    public void onBtnShowReviewDetailsAction() {
        changeDetailsPane(MovieListReviewDetailsController.class);
    }

    //TODO: Add errorImage For Situation When Can't Display Proper Cover (blank image or "NO IMG")
    private void showMovieData(Movie m) {
        // Display Movie Cover
        FXUtility.changeImageViewContent(imageCover, m.getCoverUrl(), null);
        // Fill Movie Specs Along With Generated ID
        fieldCurrentMovieID.setText(String.valueOf(m.getMovieID()));
        fieldCurrentMovieTitle.setText(m.getTitle().trim());
        // Fill Technical Specs Of Current Movie
        fieldCurrentMovieImdbUrl.setText(m.getImdbUrl().trim());
        fieldCurrentMovieRuntime.setText(m.getRuntime().trim());
        fieldCurrentMovieLanguage.setText(m.getLanguage().trim());
        fieldCurrentMovieReleaseDate.setText(m.getReleaseDate().trim());
        fieldCurrentMovieCountryOfOrigin.setText(m.getCountryOfOrigin().trim());
        // Fill Writers And Directors Tables
        fillTableWithDirectorData(m.getDirectors().stream().toList());
        fillTableWithWriterData(m.getWriters().stream().toList());
    }

    private void changeDetailsPane(Class controllerClass) {
        paneDetails.getChildren().clear();
        AnchorPane newContent = (AnchorPane) fxWeaver.load(controllerClass).getView().get();
        paneDetails.getChildren().addAll(newContent.getChildren());
    }

    private void fillTableWithMovieData(List<Movie> movieList) {
        // Validate Argument
        if(movieList == null || movieList.size() == 0) throw new IllegalArgumentException("Collection To Display Can't Be Null Or Empty!");
        // Initialize Table Parameters
        initializeMoviesTableProperties();
        tableMovies.getItems().clear();
        // Converting Movie Objects To MovieDTO Objects
        ArrayList<MovieDTO> movieDTOArrayList = new ArrayList<>(movieList.size());
        movieDTOArrayList.addAll(movieList.stream().map(m -> new MovieDTO(m)).toList());
        //Fill Table With MovieDTO Data
        tableMovies.setItems(FXCollections.observableList(movieDTOArrayList));
        tableMovies.getSortOrder().add(columnMovieID);
        tableMovies.sort();
    }

    private void initializeMoviesTableProperties() {
        columnMovieID.setCellValueFactory(new PropertyValueFactory<>("movieID"));
        columnMovieID.setSortType(TableColumn.SortType.ASCENDING);
        columnMovieTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnMovieReviewAmount.setCellValueFactory(new PropertyValueFactory<>("reviewsCount"));
        columnMovieAverageRating.setCellValueFactory(new PropertyValueFactory<>("averageRating"));
        // Table Row Clicked Listener
        tableMovies.setRowFactory(rf -> {
            TableRow<MovieDTO> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
                    currentMovie = movieService.getMovieWithInitialization(row.getItem().getMovieID());
                    onBtnShowMovieDetailsAction();
                }
            });
            return row;
        });
    }

    private void fillTableWithDirectorData(List<Director> directorList) {
        // Validate Argument
        if(directorList == null || directorList.size() == 0) throw new IllegalArgumentException("Collection To Display Can't Be Null Or Empty!");
        // Initialize Table Parameters
        initializeDirectorsTableProperties();
        tableDirectors.getItems().clear();
        //Fill Table With MovieDTO Data
        tableDirectors.setItems(FXCollections.observableList(directorList));
        tableDirectors.getSortOrder().add(columnDirectorID);
        tableDirectors.sort();
    }

    private void initializeDirectorsTableProperties() {
        columnDirectorID.setCellValueFactory(new PropertyValueFactory<>("directorID"));
        columnMovieID.setSortType(TableColumn.SortType.ASCENDING);
        columnDirectorName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Table Row Clicked Listener
        tableDirectors.setRowFactory(rf -> {
            TableRow<Director> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                // TODO: Implement Functionality To Display Pop-Up With Director Informations...
            });
            return row;
        });
    }

    private void fillTableWithWriterData(List<Writer> writerList) {
        // Validate Argument
        if(writerList == null || writerList.size() == 0) throw new IllegalArgumentException("Collection To Display Can't Be Null Or Empty!");
        // Initialize Table Parameters
        initializeWritersTableProperties();
        tableWriters.getItems().clear();
        //Fill Table With MovieDTO Data
        tableWriters.setItems(FXCollections.observableList(writerList));
        tableWriters.getSortOrder().add(columnWriterID);
        tableWriters.sort();
    }

    private void initializeWritersTableProperties() {
        columnWriterID.setCellValueFactory(new PropertyValueFactory<>("writerID"));
        columnWriterID.setSortType(TableColumn.SortType.ASCENDING);
        columnWriterName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Table Row Clicked Listener
        tableDirectors.setRowFactory(rf -> {
            TableRow<Director> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                // TODO: Implement Functionality To Display Pop-Up With Writer Informations...
            });
            return row;
        });
    }


    @Data
    public class MovieDTO {
        private int reviewsCount;
        private long movieID;
        private String averageRating;
        private String title;
        private String imdbUrl;
        private String coverUrl;
        private String runtime;
        private String language;
        private String releaseDate;
        private String countryOfOrigin;

        public MovieDTO(Movie m) {
            if(m != null && m.getMovieID() != 0 && Hibernate.isInitialized(m.getReviews())) {
                setMovieID(m.getMovieID());
                setTitle(m.getTitle());
                setImdbUrl(m.getImdbUrl());
                setCoverUrl(m.getCoverUrl());
                setRuntime(m.getRuntime());
                setLanguage(m.getLanguage());
                setReleaseDate(m.getReleaseDate());
                setCountryOfOrigin(m.getCountryOfOrigin());
                setReviewsCount(m.getReviews().size());
                // Calculating Average Rating From Reviews
                double avg = 0;
                if (m.getReviews().size() > 0) {
                    for (Review r : m.getReviews()) avg += r.getRating();
                    avg /= m.getReviews().size();
                    avg /= 10;
                    setAverageRating(String.format("%.2f", avg));
                } else {
                    setAverageRating("-");
                }
            }
        }
    }
}
