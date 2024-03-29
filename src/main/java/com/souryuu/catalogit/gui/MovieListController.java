package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.DTO.MovieDTO;
import com.souryuu.catalogit.entity.DTO.ReviewDTO;
import com.souryuu.catalogit.entity.database.Director;
import com.souryuu.catalogit.entity.database.Movie;
import com.souryuu.catalogit.entity.database.Review;
import com.souryuu.catalogit.entity.database.Writer;
import com.souryuu.catalogit.exception.ViewLoadException;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.service.ReviewService;
import com.souryuu.catalogit.utility.DialogUtility;
import com.souryuu.catalogit.utility.FXUtility;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
    private final ReviewService reviewService;
    private final FxWeaver fxWeaver;

    private final HostServices hostServices;

    private Movie currentMovie;
    private List<Movie> currentMovieList;

    private Object firstRunInitLock;

    @FXML AnchorPane root;
    @FXML AnchorPane paneDetails;

    @FXML HBox hSearchHeaderField;

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
    @FXML TableColumn<MovieDTO, Number> columnMovieAverageRating;
    @FXML TableColumn<MovieDTO, ZonedDateTime> columnMovieAddedDate;
    @FXML TableColumn<Director, Long> columnDirectorID;
    @FXML TableColumn<Director, String> columnDirectorName;
    @FXML TableColumn<Writer, Long> columnWriterID;
    @FXML TableColumn<Writer, String> columnWriterName;
    @FXML TableColumn<ReviewDTO, Long> columnReviewID;
    @FXML TableColumn<ReviewDTO, ZonedDateTime> columnReviewCreation;
    @FXML TableColumn<ReviewDTO, Double> columnReviewRating;



    public MovieListController(MovieService movieService, ReviewService reviewService, FxWeaver fxWeaver, HostServices hostServices) {
        this.movieService = movieService;
        this.reviewService = reviewService;
        this.fxWeaver = fxWeaver;
        this.hostServices = hostServices;
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
        //btnShowMovieDetails.disableProperty().bind(currentMovieNullProperty);
        //btnShowReviewDetails.disableProperty().bind(currentMovieNullProperty);
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
            fieldCurrentMovieImdbUrl.setOnMouseClicked(mouseEvent -> {
                if(mouseEvent.getClickCount() == 2) {
                    this.hostServices.showDocument(currentMovie.getImdbUrl());
                }
            });
            showMovieData(currentMovie);
        }
    }

    @FXML
    public void onBtnShowReviewDetailsAction() {
        if(currentMovie != null && Hibernate.isInitialized(currentMovie.getReviews()) && currentMovie.getReviews() != null && currentMovie.getReviews().size() > 0){
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
        if(m != null && m.getMovieID() != 0) {
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
        } else {
            // Fill Movie Specs Along With Generated ID
            fieldCurrentMovieID.setText("");
            fieldCurrentMovieTitle.setText("");
            // Fill Technical Specs Of Current Movie
            fieldCurrentMovieImdbUrl.setText("");
            fieldCurrentMovieRuntime.setText("");
            fieldCurrentMovieLanguage.setText("");
            fieldCurrentMovieReleaseDate.setText("");
            fieldCurrentMovieCountryOfOrigin.setText("");
            // Fill Writers And Directors Tables
            fillTableWithDirectorData(List.of());
            fillTableWithWriterData(List.of());
        }
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
            tableMovies.scrollTo(tableMovies.getItems().size()-1);
        }
    }

    private void initializeMoviesTableProperties() {
        columnMovieID.setCellValueFactory(new PropertyValueFactory<>("movieID"));
        columnMovieID.setSortType(TableColumn.SortType.ASCENDING);
        columnMovieTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        columnMovieReviewAmount.setCellValueFactory(new PropertyValueFactory<>("reviewsCount"));
        columnMovieAverageRating.setCellValueFactory(new PropertyValueFactory<>("avgRating"));
        columnMovieAverageRating.setCellFactory(tc -> new TableCell<MovieDTO, Number>() {
            @Override
            protected void updateItem(Number averageRating, boolean empty) {
                if (empty) {
                    setText("");
                } else {
                    if(averageRating != null && averageRating.doubleValue() >= 0) {
                        setText(String.format("%.2f", averageRating.doubleValue()));
                    } else {
                        setText("-");
                    }
                }
            }
        });
        columnMovieAddedDate.setCellValueFactory(new PropertyValueFactory<>("creationDate"));
        columnMovieAddedDate.setCellFactory(tc -> new TableCell<MovieDTO, ZonedDateTime>() {
            @Override
            protected void updateItem(ZonedDateTime creationDate, boolean empty) {
                if (empty) {
                    setText("");
                } else {
                    if(creationDate != null) {
                        setText(creationDate.format(DateTimeFormatter.ofPattern("HH:mm:ss dd.MM.yyyy")));
                    } else {
                        setText("-");
                    }
                }
            }
        });
        // Opening Movie Details After Double-Click
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
        // Removing Selected Movie From DB
        tableMovies.setOnKeyReleased(keyEvent -> {
            if(keyEvent.getCode() == KeyCode.DELETE) {
                MovieDTO movieDTO = tableMovies.getSelectionModel().getSelectedItem();
                if (movieDTO != null) {
                    long movieID = movieDTO.getMovieID();
                    if(movieService.existsById(movieID)) {
                        boolean isDeleted;
                        if(movieDTO.getReviewsCount() != 0) {
                            Movie m = movieService.getMovieWithInitialization(movieID);
                            for(Review r : m.getReviews()) {
                                reviewService.deleteByReviewID(r.getReviewID());
                            }
                            isDeleted = movieService.deleteMovieById(m.getMovieID());
                        } else {
                            isDeleted = movieService.deleteMovieById(movieID);
                        }
                        if(isDeleted) {
                            onBtnRefreshDatabaseAction();
                        }
                    }
                    if(currentMovie != null && currentMovie.getMovieID() == movieID) {
                        currentMovie = new Movie();
                        Platform.runLater(() -> {
                            showMovieData(currentMovie);
                            paneDetails.getChildren().clear();
                        });
                    }
                }
            }
        });

    }

    private void fillTableWithDirectorData(List<Director> directorList) {
        // Validate Argument
        if(directorList == null) {
            throw new IllegalArgumentException("Collection To Display Can't Be Null!");
        }
        initializeDirectorsTableProperties();
        if(directorList.size() == 0) {
            tableDirectors.getItems().clear();
        } else {
            tableDirectors.getItems().clear();
            //Fill Table With MovieDTO Data
            tableDirectors.getItems().addAll(FXCollections.observableList(directorList));
            tableDirectors.getSortOrder().add(columnDirectorID);
            tableDirectors.sort();
        }
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
        if(writerList == null) {
            throw new IllegalArgumentException("Collection To Display Can't Be Null Or Empty!");
        }
        if(writerList.size() == 0) {
            initializeWritersTableProperties();
            tableWriters.getItems().clear();
        } else {
            // Initialize Table Parameters
            initializeWritersTableProperties();
            tableWriters.getItems().clear();
            //Fill Table With MovieDTO Data
            tableWriters.getItems().addAll(FXCollections.observableList(writerList));
            tableWriters.getSortOrder().add(columnWriterID);
            tableWriters.sort();
        }
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
        tableReviews.getItems().addAll(FXCollections.observableList(reviewDTOList));
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




}
