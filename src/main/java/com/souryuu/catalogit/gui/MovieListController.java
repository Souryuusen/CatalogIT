package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.entity.Review;
import com.souryuu.catalogit.entity.Writer;
import com.souryuu.catalogit.exception.ViewLoadException;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.utility.DialogUtility;
import com.souryuu.catalogit.utility.FXUtility;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import lombok.*;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
@FxmlView("show-all-movies-view.fxml")
public class MovieListController {

    private static final String BY_ID = "ID Filmu";
    private static final String BY_TITLE = "Tytuł Filmu";
    private static final String BY_LINK = "Link Filmu";


    private final MovieService movieService;
    private final FxWeaver fxWeaver;

    private Movie currentMovie;
    private List<Movie> currentMovieList;

    private Object firstRunInitLock;

    @FXML AnchorPane root;
    @FXML AnchorPane paneDetails;

    @FXML Button btnShowMovieDetails;
    @FXML Button btnShowReviewDetails;
    @FXML Button btnSearch;
    @FXML Button btnRefreshDatabase;

    @FXML ComboBox<String> comboSearchBy;

    @FXML TextField fieldCurrentMovieID;
    @FXML TextField fieldCurrentMovieTitle;
    @FXML TextField fieldCurrentMovieImdbUrl;
    @FXML TextField fieldCurrentMovieRuntime;
    @FXML TextField fieldCurrentMovieLanguage;
    @FXML TextField fieldCurrentMovieReleaseDate;
    @FXML TextField fieldCurrentMovieCountryOfOrigin;
    @FXML TextField fieldSearch;

    @FXML TextArea areaReviewBody;

    @FXML ImageView imageCover;

    @FXML TableView<MovieDTO> tableMovies;
    @FXML TableView<Director> tableDirectors;
    @FXML TableView<Writer> tableWriters;
    @FXML TableView<ReviewDTO> tableReviews;

    @FXML TableColumn<MovieDTO, Long> columnMovieID;
    @FXML TableColumn<MovieDTO, String> columnMovieTitle;
    @FXML TableColumn<MovieDTO, Integer> columnMovieReviewAmount;
    @FXML TableColumn<MovieDTO, String> columnMovieAverageRating;
    @FXML TableColumn<Director, Long> columnDirectorID;
    @FXML TableColumn<Director, String> columnDirectorName;
    @FXML TableColumn<Writer, Long> columnWriterID;
    @FXML TableColumn<Writer, String> columnWriterName;
    @FXML TableColumn<ReviewDTO, Long> columnReviewID;
    @FXML TableColumn<ReviewDTO, ZonedDateTime> columnReviewCreation;
    @FXML TableColumn<ReviewDTO, Double> columnReviewRating;



    public MovieListController(MovieService movieService, FxWeaver fxWeaver) {
        this.movieService = movieService;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void initialize() {
        if(firstRunInitLock == null) {
            onBtnRefreshDatabaseAction();
            firstRunInitLock = new Object();
        }
        comboSearchBy.getSelectionModel().select(0);
        createBindings();
        attachListeners();
    }

    private void createBindings() {
        BooleanProperty currentMovieNullProperty = new SimpleBooleanProperty(currentMovie == null);
        btnShowMovieDetails.disableProperty().bind(currentMovieNullProperty);
        btnShowReviewDetails.disableProperty().bind(currentMovieNullProperty);
    }

    private void attachListeners() {
        fieldSearch.setOnKeyReleased(event -> {
            if(event.getCode() == KeyCode.ENTER) {
                onBtnSearchAction();
            }
        });
    }

    @FXML
    public void onBtnSearchAction() {
        if(fieldSearch.getText().trim().equalsIgnoreCase("")) {
            currentMovieList = movieService.findAllWithReviews();
            fillTableWithMovieData();
        } else {
            String selection = comboSearchBy.getValue();
            switch (selection) {
                case BY_ID -> searchByID();
                case BY_LINK -> searchByLink();
                case BY_TITLE -> searchByTitle();
                default -> invalidSearchCriteria();
            }
        }
    }

    private void searchByID() {
        if(fieldSearch.getText().length() > 0) {
            String valueString = fieldSearch.getText().trim();
            try {
                long idCriteria = Long.parseLong(valueString);
                currentMovieList = movieService.findAllByIdWithReviews(idCriteria);
            } catch (NumberFormatException ex) {
//                ex.printStackTrace();
                DialogUtility.createErrorDialog("Niepoprawna Wartość ID", "Wprowadzona wartość: " +
                        fieldSearch.getText() + " nie jest poprawną wartością dla pola ID!");
            } finally {
                fillTableWithMovieData();
            }
        } else {
            DialogUtility.createErrorDialog("Niepoprawna Wartość ID", "Pole ID nie może być puste!");
        }
    }

    private void searchByLink() {
        if(fieldSearch.getText().length() > 0) {
            String urlCriteria = fieldSearch.getText().trim();
            currentMovieList = movieService.findAllByUrlContainsWithReviews(urlCriteria);
            fillTableWithMovieData();
        } else {
            DialogUtility.createErrorDialog("Niepoprawna Wartość URL", "Pole linku url nie może być puste!");
        }
    }

    private void searchByTitle() {
        if(fieldSearch.getText().length() > 0) {
            String titleCriteria = fieldSearch.getText().trim();
            currentMovieList = movieService.findAllByTitleContainingWithReviews(titleCriteria);
            fillTableWithMovieData();
        } else {
            DialogUtility.createErrorDialog("Niepoprawna Wartość Pola Tytułu", "Pole tytułu nie może być puste!");
        }
    }

    private void invalidSearchCriteria() {
        DialogUtility.createErrorDialog("Niepoprawne Kryterium Wyszukiwania", "Wykryto niepoprawny wybór kryterium wyszukiwania!");
    }

    @FXML
    public void onBtnRefreshDatabaseAction() {
        currentMovieList = movieService.findAllWithReviews();
        fillTableWithMovieData();
    }

    @FXML
    public void onBtnShowMovieDetailsAction() {
        if(currentMovie != null) {
            changeDetailsPane(MovieListMovieDetailsController.class);
            imageCover.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getClickCount() == 2 && currentMovie.getImdbUrl() != null && currentMovie.getImdbUrl().length() > 0) {
                    // TODO: Add Dialog Creation Method
                }
            });
            showMovieData(currentMovie);
        }
    }

    @FXML
    public void onBtnShowReviewDetailsAction() {
        if(currentMovie != null && Hibernate.isInitialized(currentMovie.getReviews()) && currentMovie.getReviews().size() > 0){
            changeDetailsPane(MovieListReviewDetailsController.class);
            showMovieReviewData(currentMovie);
        } else {
            if (currentMovie != null) {
                DialogUtility.createErrorDialog("Brak Recenzji Dla Filmu!", "W bazie danych dla filmu " + currentMovie.getTitle() + " nie znaleziono recenzji!");
            }
        }
    }

    private void showMovieData(Movie m) {
        // Display Movie Cover
        FXUtility.changeImageViewContent(imageCover, m.getCoverUrl(), true);
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

    private void showMovieReviewData(Movie m) {
        fillTableWithReviewData(m.getReviews().stream().toList());
    }

    private void changeDetailsPane(Class<?> controllerClass) {
        paneDetails.getChildren().clear();
        Optional<Node> viewOptional = fxWeaver.load(controllerClass).getView();
        if (viewOptional.isPresent()) {
            AnchorPane newContent = (AnchorPane) viewOptional.get();
            paneDetails.getChildren().addAll(newContent.getChildren());
        } else {
            throw new ViewLoadException("Error Loading View From " + controllerClass.getName() + " !!");
        }
    }

    private void fillTableWithMovieData() {
        // Validate Argument
        if(currentMovieList == null || currentMovieList.size() == 0) {
            tableMovies.getItems().clear();
            paneDetails.getChildren().clear();
            currentMovie = null;
        } else {
            // Initialize Table Parameters
            initializeMoviesTableProperties();
            tableMovies.getItems().clear();
            // Converting Movie Objects To MovieDTO Objects
            ArrayList<MovieDTO> movieDTOArrayList = new ArrayList<>(currentMovieList.size());
            movieDTOArrayList.addAll(currentMovieList.stream().map(MovieDTO::new).toList());
            //Fill Table With MovieDTO Data
            tableMovies.setItems(FXCollections.observableList(movieDTOArrayList));
            tableMovies.getSortOrder().add(columnMovieID);
            tableMovies.sort();
        }
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

    private void fillTableWithReviewData(List<Review> reviewList) {
        // Validate Argument
        if(reviewList == null || reviewList.size() == 0) throw new IllegalArgumentException("Collection To Display Can't Be Null Or Empty!");
        // Initialize Table Parameters
        initializeReviewsTableProperties();
        tableReviews.getItems().clear();
        // Map Reviews To ReviewDTOs Objects
        List<ReviewDTO> reviewDTOList = reviewList.stream().map(ReviewDTO::new).toList();
        //Fill Table With MovieDTO Data
        tableReviews.setItems(FXCollections.observableList(reviewDTOList));
        tableReviews.getSortOrder().add(columnReviewID);
        tableReviews.sort();
    }

    private void initializeReviewsTableProperties() {
        columnReviewID.setCellValueFactory(new PropertyValueFactory<>("reviewID"));
        columnReviewID.setSortType(TableColumn.SortType.ASCENDING);
        columnReviewCreation.setCellValueFactory(new PropertyValueFactory<>("formattedCreationDate"));
        columnReviewRating.setCellValueFactory(new PropertyValueFactory<>("convertedRating"));
        // Table Row Clicked Listener
        tableReviews.setRowFactory(rf -> {
            TableRow<ReviewDTO> row = new TableRow<>();
            row.setOnMouseClicked(mouseEvent -> {
                if (mouseEvent.getClickCount() == 2 && !row.isEmpty()) {
                    areaReviewBody.setText(row.getItem().getReviewBody());
                }
            });
            return row;
        });
    }

    @Data
    public static class MovieDTO {
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

    @Data
    public static class ReviewDTO {

        private double convertedRating;
        private long reviewID;
        private String formattedCreationDate;
        private String reviewBody;

        public ReviewDTO(Review review) {
            setReviewID(review.getReviewID());
            setConvertedRating(review.getRating()/10.0);
            setFormattedCreationDate(review.getCreationData().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
            setReviewBody(review.getReview());
        }

    }
}
