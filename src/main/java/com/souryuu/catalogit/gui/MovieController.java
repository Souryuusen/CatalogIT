package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.application.MovieData;
import com.souryuu.catalogit.entity.database.*;
import com.souryuu.catalogit.entity.database.Writer;
import com.souryuu.catalogit.exception.ImdbLinkParsingException;
import com.souryuu.catalogit.service.*;
import com.souryuu.catalogit.utility.DialogUtility;
import com.souryuu.catalogit.utility.ExternalFileParser;
import com.souryuu.catalogit.utility.FXUtility;
import com.souryuu.catalogit.utility.ScraperUtility;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@FxmlView("movie-add-new-view.fxml")
public class MovieController {

    private final FxWeaver fxWeaver;
    private final MovieService movieService;
    private final DirectorService directorService;
    private final WriterService writerService;
    private final ReviewService reviewService;
    private final GenreService genreService;
    private final TagService tagService;

    //##################################################################################################################

    private Movie currentMovie;
    private final HashSet<Review> currentReviews = new HashSet<>();
    private Thread loadingThread;

    //##################################################################################################################
    @FXML AnchorPane root;
    @FXML AnchorPane editDirectorRoot;
    @FXML AnchorPane editWriterRoot;
    @FXML AnchorPane addReviewRoot;
    @FXML AnchorPane editReviewRoot;

    @FXML HBox hAddNewDirector;
    @FXML HBox hRemoveDirector;

    @FXML Label labelEditedReviewIndex;
    @FXML Label labelEditedReviewCreation;

    @FXML TextField textImdbLink;
    @FXML TextField textTitle;
    @FXML TextField textReleaseDate;
    @FXML TextField textRuntime;
    @FXML TextField textLanguage;
    @FXML TextField textCountryOfOrigin;
    @FXML TextField textCoverUrl;
    @FXML TextField textNewDirectorName;
    @FXML TextField textNewWriterName;

    @FXML TextArea areaReviewBody;
    @FXML TextArea areaEditedReviewBody;

    @FXML Button btnScrapeData;
    @FXML Button btnLoadData;
    @FXML Button btnSaveData;
    @FXML Button btnAddNewDirector;
    @FXML Button btnRemoveDirector;
    @FXML Button btnAddNewWriter;
    @FXML Button btnRemoveWriter;
    @FXML Button btnSearchDatabase;

    @FXML ComboBox<String> comboRemoveDirector;
    @FXML ComboBox<String> comboRemoveWriter;

    @FXML Slider sliderReviewRating;
    @FXML Slider sliderEditedReviewRating;

    @FXML ImageView viewCoverImage;

    @FXML TableView<Director> tableAddedDirectors;
    @FXML TableView<Writer> tableAddedWriters;

    @FXML TableColumn<Director, Long> columnAddedDirectorsID;
    @FXML TableColumn<Director, String> columnAddedDirectorsName;
    @FXML TableColumn<Writer, Long> columnAddedWritersID;
    @FXML TableColumn<Writer, String> columnAddedWritersName;

    public MovieController(MovieService movieService, DirectorService directorService, WriterService writerService, FxWeaver fxWeaver, ReviewService reviewService, GenreService genreService, TagService tagService) {
        this.movieService = movieService;
        this.directorService = directorService;
        this.writerService = writerService;
        this.fxWeaver = fxWeaver;
        this.reviewService = reviewService;
        this.genreService = genreService;
        this.tagService = tagService;
    }
    //##################################################################################################################

    @FXML
    public void initialize() {
        // Logic For Bindings And Listeners
        createBindings();
        attachListeners();
        // Logic For Tables Initialization & Configuration
        initializeAddMovieTables();
    }

    /**
     * TODO: Add errorImage same As In MovieListController
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnScrapeDataAction() {
        if(textImdbLink.getText().trim().length() > 0 && (loadingThread == null || !loadingThread.isAlive())) {
            String movieImdbLink = textImdbLink.getText().trim().toLowerCase();
            // Cleaning Of Tables
            tableAddedDirectors.getItems().clear();
            tableAddedWriters.getItems().clear();
            // Verification If Movie With This URL Exists In DB
            MovieData scrapedData = ScraperUtility.scrapeData(movieImdbLink);
            currentMovie = new Movie(movieImdbLink, scrapedData);
            currentReviews.clear();
            scrapedData.getDirectors().ifPresent(this::addDirectors);
            scrapedData.getWriters().ifPresent(this::addWriters);
            // Display Movie Data
            displayObtainedMovieInformation();
        }
    }

    @FXML
    public void onBtnSearchDatabaseAction() {
        if(loadingThread == null || !loadingThread.isAlive()) {
            if(textImdbLink.getText().trim().length() > 0) {
                String movieImdbLink = textImdbLink.getText().trim().toLowerCase();
                if(Movie.validateImdbLink(movieImdbLink)) {
                    currentMovie = movieService.getMovieByImdbUrlWithInitialization(movieImdbLink);
                } else {
                    // TODO: Add error dialog for "Invalid IMDB URL"
                }
            } else if (textTitle.getText().trim().length() > 0) {
                String movieTitle = textTitle.getText().trim().toLowerCase();
                currentMovie = movieService.getMovieByTitleWithInitialization(movieTitle);
            } else {
                // TODO: Add error dialog for "Insufficient Data For Search Operation"
            }
            if(currentMovie != null) {
                currentReviews.addAll(currentMovie.getReviews());
                addDirectors(currentMovie.getDirectors().stream().map(Director::getName).toList());
                addWriters(currentMovie.getWriters().stream().map(Writer::getName).toList());
                // Display Movie Data
                displayObtainedMovieInformation();
            } else {
                //TODO: Add error dialog for "Error During Database Search Operation"
            }
        } else {
            //TODO: Add error dialog for "Unable To Start DB Search Operation"
        }
    }

    private void displayObtainedMovieInformation() {
        // Display Movie Data
        textTitle.setText(currentMovie.getTitle());
        textCoverUrl.setText(currentMovie.getCoverUrl());
        textRuntime.setText(currentMovie.getRuntime());
        textReleaseDate.setText(currentMovie.getReleaseDate());
        textCountryOfOrigin.setText(currentMovie.getCountryOfOrigin());
        textLanguage.setText(currentMovie.getLanguage());
        FXUtility.changeImageViewContent(viewCoverImage, currentMovie.getCoverUrl(), true);
        currentMovie.setWriters(obtainCurrentWriters());
        currentMovie.setDirectors(obtainCurrentDirectors());
        displayAddedMovieDirectors();
        displayAddedMovieWriters();
    }


    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnLoadDataAction() {
        ExternalFileParser efp = new ExternalFileParser();

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File f = fileChooser.showOpenDialog(root.getScene().getWindow());

        // TODO: Externalize creation of runnable task with refactoring of the code
        if(f != null && f.exists() && f.isFile()) {
            loadingThread = new Thread(() -> {
                btnLoadData.setDisable(true);
                try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f), StandardCharsets.UTF_8))) {
                    for (String line : br.lines().toList()) {
                        List<String> lineData = efp.parseMovieLine(line);

                        String imdbUrl = "";
                        boolean wasWatched = false;
                        if (lineData.size() == 4) {
                            wasWatched = true;
                            imdbUrl = lineData.get(2).trim();
                        } else {
                            wasWatched = false;
                            for (String data : lineData) {
                                if (Movie.validateImdbLink(data)) {
                                    imdbUrl = data.trim();
                                }
                            }
                        }
                        if (imdbUrl.equalsIgnoreCase("")) {
                            throw new ImdbLinkParsingException(line);
                        } else {
                            if (movieService.getMovieByImdbUrl(imdbUrl) == null) {
                                // Temp Collections
                                HashSet<Director> directorsToAdd = new HashSet<>();
                                HashSet<Writer> writersToAdd = new HashSet<>();
                                HashSet<Tag> tagsToAdd = new HashSet<>();
                                HashSet<Genre> genresToAdd = new HashSet<>();
                                // Scrape Movie Data
                                MovieData scrapedData = ScraperUtility.scrapeData(imdbUrl);
                                Movie movieToAdd = new Movie(imdbUrl, scrapedData);
                                // Verification Of Scraped Directors Data
                                scrapedData.getDirectors()
                                        .ifPresent(list ->
                                                list.stream()
                                                        .map(ScraperUtility::formatToCamelCase)
                                                        .map(directorName -> directorService.findOrCreateNew(directorName))
                                                        .forEach(director -> directorsToAdd.add(director)));
                                movieToAdd.setDirectors(directorsToAdd);
                                // Verification Of Scraped Writers Data
                                scrapedData.getWriters()
                                        .ifPresent(list ->
                                                list.stream()
                                                        .map(ScraperUtility::formatToCamelCase)
                                                        .map(writerName -> writerService.findOrCreateNew(writerName))
                                                        .forEach(writer -> writersToAdd.add(writer)));
                                movieToAdd.setWriters(writersToAdd);
                                // Verification Of Scraped Genres Data
                                scrapedData.getGenres()
                                        .ifPresent(list ->
                                                list.stream()
                                                        .map(ScraperUtility::formatToCamelCase)
                                                        .map(genreName -> genreService.findOrCreateNew(genreName))
                                                        .forEach(genre -> genresToAdd.add(genre)));
                                movieToAdd.setGenres(genresToAdd);
                                // Verification Of Scraped Tags Data
                                scrapedData.getTags()
                                        .ifPresent(list ->
                                                list.stream()
                                                        .map(ScraperUtility::formatToCamelCase)
                                                        .map(tagName -> tagService.findOrCreateNew(tagName))
                                                        .forEach(tag -> tagsToAdd.add(tag)));
                                movieToAdd.setTags(tagsToAdd);
                                // Saving Of Scraped Movie
                                movieService.save(movieToAdd);
                                // Creation Of Review Data
                                if (wasWatched) {
                                    double rating = Double.parseDouble(lineData.get(1).trim());
                                    rating *= 10.0;
                                    Review r = new Review((int) rating, lineData.get(3).trim(), ZonedDateTime.now(), movieToAdd);
                                    reviewService.save(r);
                                }
                                // Refresh Displayed Information
                                updateDisplayedInformation(movieToAdd);
                            } else {
                                // TODO: Add logic for movie for read already existing in db...
                                System.out.println("Movie Already Exists In Database!!");
                            }
                            System.out.println("Movie With Link:\t" + imdbUrl + " Already Exists In Database!");
                        }
                    }
                } catch (IOException ex) {
                    // TODO: Add exception handling...
                } finally {
                    btnLoadData.setDisable(false);
                }
            });
            loadingThread.start();
        } else {
            //TODO: Add logic for handling invalid file selection
            System.out.println("Wrong File Selected!!");
        }
    }

    private void updateDisplayedInformation(Movie movie) {
        Platform.runLater(() -> {
            textImdbLink.setText(movie.getImdbUrl());
            textTitle.setText(movie.getTitle());
            textLanguage.setText(movie.getLanguage());
            textCoverUrl.setText(movie.getCoverUrl());
            textRuntime.setText(movie.getRuntime());
            textCountryOfOrigin.setText(movie.getCountryOfOrigin());
            textReleaseDate.setText(movie.getReleaseDate());
            addDirectors(movie.getDirectors().stream().map(Director::getName).toList());
            addWriters(movie.getWriters().stream().map(Writer::getName).toList());
            FXUtility.changeImageViewContent(viewCoverImage, movie.getCoverUrl(), true);
        });
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnSaveDataAction() {
        //TODO: Refactor Method
        if(currentMovie != null && currentMovie.getImdbUrl().length() > 0) {
            if(movieService.existsById(currentMovie.getMovieID())) {

            }
            currentMovie.setDirectors(obtainCurrentDirectors());
            currentMovie.setWriters(obtainCurrentWriters());
            Set<Genre> genres = currentMovie.getGenres();
            Set<Genre> genreToAdd = new HashSet<>(currentMovie.getGenres().size());
            for(Genre g : genres) {
                if(g.getGenreID() != 0) {
                    genreToAdd.add(g);
                } else {
                    if(genreService.existsByGenreName(g.getGenreName())) {
                        genreToAdd.add(genreService.findGenreByGenreName(g.getGenreName()));
                    } else {
                        genreToAdd.add(genreService.save(g));
                    }
                }
            }
            Set<Tag> tags = currentMovie.getTags();
            Set<Tag> tagToAdd = new HashSet<>(currentMovie.getTags().size());
            for(Tag t : tags) {
                if(t.getTagID() != 0) {
                    tagToAdd.add(t);
                } else {
                    if(tagService.existsByTagName(t.getTagName())) {
                        tagToAdd.add(tagService.findTagByTagName(t.getTagName()));
                    } else {
                        tagToAdd.add(tagService.save(t));
                    }
                }
            }
            currentMovie.setGenres(genreToAdd);
            currentMovie.setTags(tagToAdd);
            movieService.save(currentMovie);
//            currentMovie.setReviews(obtainCurrentReviews());
            movieService.save(currentMovie);
        }
    }

    private void initializeAddMovieTables() {
        // Initialize Tables Properties
        columnAddedDirectorsID.setCellValueFactory(new PropertyValueFactory<>("directorID"));
        columnAddedDirectorsID.setSortType(TableColumn.SortType.ASCENDING);
        columnAddedDirectorsName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Table Row Clicked Listener
        tableAddedDirectors.setRowFactory(rf -> {
            TableRow<Director> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    // TODO: Add Logic To Display Information About Director
                }
            });
            return row ;
        });
        // Initialize Tables Properties
        columnAddedWritersID.setCellValueFactory(new PropertyValueFactory<>("writerID"));
        columnAddedWritersID.setSortType(TableColumn.SortType.ASCENDING);
        columnAddedWritersName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Table Row Clicked Listener
        tableAddedWriters.setRowFactory(rf -> {
            TableRow<Writer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    // TODO: Add Logic To Display Information About Writer
                }
            });
            return row ;
        });
    }

    private void displayAddedMovieDirectors() {
        // Parsing Current Directors List
        HashSet<Director> currentDirectors = (HashSet<Director>)obtainCurrentDirectors();
        // Creating List Of Directors Names
        List<String> currentDirectorNames = new ArrayList<>(currentDirectors.size());
        currentDirectors.forEach(d -> currentDirectorNames.add(d.getName()));
        // Filling ComboBox Data From
        comboRemoveDirector.setItems(FXCollections.observableList(currentDirectorNames).sorted());
        // Filling TableView With Database Entries
        tableAddedDirectors.getItems().clear();
//        tableAddedDirectors.setItems(FXCollections.observableList(directorService.findDirectorsOfMovie(currentMovie)));
        tableAddedDirectors.setItems(FXCollections.observableList(new ArrayList<>(currentMovie.getDirectors())));
        // Sorting Table By Director ID Field
        tableAddedDirectors.getSortOrder().add(columnAddedDirectorsID);
        tableAddedDirectors.sort();
    }

    private void displayAddedMovieWriters() {
        // Parsing Current Writers List
        HashSet<Writer> currentWriters = (HashSet<Writer>)obtainCurrentWriters();
        // Creating List Of Writers Names
        List<String> currentWritersNames = new ArrayList<>(currentWriters.size());
        currentWriters.forEach(w -> currentWritersNames.add(w.getName()));
        // Filling ComboBox Data From
        comboRemoveWriter.setItems(FXCollections.observableList(currentWritersNames).sorted());
        // Filling TableView With Database Entries
        tableAddedWriters.getItems().clear();
//        tableAddedWriters.setItems(FXCollections.observableList(writerService.findWritersOfMovie(currentMovie)));
        tableAddedWriters.setItems(FXCollections.observableList(new ArrayList<>(currentMovie.getWriters())));
        // Sorting Table By Director ID Field
        tableAddedWriters.getSortOrder().add(columnAddedWritersID);
        tableAddedWriters.sort();
    }

    @FXML
    public void onBtnAddNewDirectorAction() {
        if(textNewDirectorName.getText().trim().length() > 0) {
            // Retrieve New Director Name
            String newDirectorName = ScraperUtility.formatToCamelCase(textNewDirectorName.getText().trim());
            // Retrieve HashSet Of All Added Directors
            HashSet<Director> addedDirectors = (HashSet<Director>) obtainCurrentDirectors();
            addedDirectors.add(new Director(newDirectorName));
            // Creating Names List To Transform Into Labels
            ArrayList<String> addedDirectorNames = new ArrayList<>(addedDirectors.size() + 1);
            addedDirectors.forEach(d -> addedDirectorNames.add(d.getName()));
            // Updating Directors
            addDirectors(addedDirectorNames);
            // Updating Remove ComboBox
            comboRemoveDirector.setItems(FXCollections.observableList(addedDirectorNames));
            // Clearing Text Fields...
            textNewDirectorName.setText("");
            // Refreshing TableView With Database Entries
            tableAddedDirectors.getItems().clear();
            tableAddedDirectors.setItems(FXCollections.observableList(directorService.findAll()));
            // Sorting Table By Director ID Field
            tableAddedDirectors.getSortOrder().add(columnAddedDirectorsID);
            tableAddedDirectors.sort();
        }
    }

    @FXML
    public void onBtnRemoveDirectorAction() {
        if(comboRemoveDirector.getValue() != null && !comboRemoveDirector.getValue().trim().equalsIgnoreCase("")) {
            Director directorToRemove = new Director(comboRemoveDirector.getSelectionModel().getSelectedItem());
            // Parsing Current Directors List
            HashSet<Director> currentDirectors = (HashSet<Director>)obtainCurrentDirectors();
            // Remove Director From TableView
            if(tableAddedDirectors.getItems().contains(directorToRemove)) {
                tableAddedDirectors.getItems().remove(directorToRemove);
            }
            boolean removed = currentDirectors.remove(directorToRemove);
            if(removed) {
                List<String> addedDirectorNames = currentDirectors.stream().map(Director::getName).collect(Collectors.toList());
                // Updating Directors
                addDirectors(addedDirectorNames);
                // Updating Remove ComboBox
                comboRemoveDirector.setItems(FXCollections.observableList(addedDirectorNames));
            }
        }
    }

    @FXML
    public void onBtnAddNewWriterAction() {
        String content = textNewWriterName.getText().trim();
        if(content.length() > 0) {
            // Retrieve New Director Name
            String newWriterName = ScraperUtility.formatToCamelCase(content);
            // Retrieve HashSet Of All Added Directors
            HashSet<Writer> addedWriters = (HashSet<Writer>) obtainCurrentWriters();
            addedWriters.add(new Writer(newWriterName));
            // Creating Names List To Transform Into Labels
            ArrayList<String> addedWritersNames = new ArrayList<>(addedWriters.size() + 1);
            addedWriters.forEach(w -> addedWritersNames.add(w.getName()));
            // Updating Directors
            addWriters(addedWritersNames);
            // Updating Remove ComboBox
            comboRemoveWriter.setItems(FXCollections.observableList(addedWritersNames));
            // Clearing Text Fields...
            textNewWriterName.setText("");
            // Refreshing TableView With Database Entries
            tableAddedWriters.getItems().clear();
            tableAddedWriters.setItems(FXCollections.observableList(writerService.findAll()));
            // Sorting Table By Director ID Field
            tableAddedWriters.getSortOrder().add(columnAddedWritersID);
            tableAddedWriters.sort();
        } else {
            DialogUtility.createErrorDialog("Nie można dodać wpisu!", "Brak danych scenarzysty!!");
        }
    }

    @FXML
    public void onBtnRemoveWriterAction() {
        if(comboRemoveWriter.getValue() != null && !comboRemoveWriter.getValue().trim().equalsIgnoreCase("")) {
            Writer writerToRemove = new Writer(comboRemoveWriter.getSelectionModel().getSelectedItem());
            // Parsing Current Directors List
            HashSet<Writer> currentWriters = (HashSet<Writer>)obtainCurrentWriters();
            // Remove Writer From TableView
            boolean removed = false;
            for(Writer w : currentWriters) {
                if (w.getName().equalsIgnoreCase(writerToRemove.getName())) {
                    removed = currentWriters.remove(w);
                }
            }
            if(removed) {
                List<String> addedWritersNames = currentWriters.stream().map(Writer::getName).collect(Collectors.toList());
                // Updating Directors
                addWriters(addedWritersNames);
                // Updating Remove ComboBox
                comboRemoveWriter.setItems(FXCollections.observableList(addedWritersNames));
            }
        }
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
//    @FXML
//    public void onBtnAddNewReviewAction() {
//        // Setting New Content To Display On detailsPane
//        detailPane.getChildren().clear();
//        Optional<Node> viewOptional = fxWeaver.load(MovieAddReviewController.class).getView();
//        if(viewOptional.isPresent()) {
//            Node newContent = viewOptional.get();
//            detailPane.getChildren().setAll(newContent);
//            System.out.println(detailPane.styleProperty().getValue());
//        } else {
//            throw new ViewLoadException("Error Loading View From MovieAddReviewController Class!!");
//        }
//    }

//    @FXML
//    public void onBtnAddReviewAction() {
//        if(currentMovie != null && areaReviewBody.getText().trim().length() > 0) {
//            int rating = (int)(sliderReviewRating.getValue()*10);
//            String reviewBody = areaReviewBody.getText().trim();
//            ZonedDateTime now = ZonedDateTime.now();
//            Review newReview = new Review(rating, reviewBody, now, currentMovie);
//            currentReviews.add(newReview);
//            sliderReviewRating.setValue(5);
//            areaReviewBody.clear();
//            detailPane.getChildren().clear();
//        }
//    }

//    @FXML
//    public void onBtnEditReviewAction() {
//        // Setting New Content To Display On detailsPane
//        detailPane.getChildren().clear();
//        Optional<Node> viewOptional = fxWeaver.load(MovieEditReviewController.class).getView();
//        if(viewOptional.isPresent()) {
//            Node newContent = viewOptional.get();
//            detailPane.getChildren().setAll(newContent);
//        } else {
//            throw new ViewLoadException("Error Loading View From MovieEditReviewController Class!!");
//        }
//        // Creating Bindings For Navigation Buttons
//        btnEditedReviewPrevious.disableProperty().bind(currentReviewIndexProperty.isEqualTo(0));
//        btnEditedReviewNext.disableProperty().bind(currentReviewIndexProperty.lessThan(currentReviews.size()-1).not());
//        labelEditedReviewIndex.textProperty().bind(Bindings.convert(currentReviewIndexProperty.add(1)));
//        // Setting Current Index To 0
//        this.currentReviewIndexProperty.setValue(0);
//        // Set Fields To Values From Review At Index 0
//        refreshDisplayedEditedReview();
//    }

//    @FXML
//    public void onBtnEditedReviewPreviousAction() {
//        if(currentReviewIndexProperty.getValue() > 0) {
//            currentReviewIndexProperty.setValue(currentReviewIndexProperty.getValue()-1);
//            refreshDisplayedEditedReview();
//        }
//    }

//    @FXML
//    public void onBtnEditedReviewSaveAction() {
//        Review editedReview = currentReviews.stream().toList().get(currentReviewIndexProperty.getValue());
//        if(sliderEditedReviewRating.getValue() == 0 && areaEditedReviewBody.getText().equalsIgnoreCase("")) {
//            currentReviews.remove(editedReview);
//            if(editedReview.getReviewID() != 0) {
//                reviewService.deleteByReviewID(editedReview.getReviewID());
//            }
//        } else {
//            editedReview.setRating((int)(sliderEditedReviewRating.getValue()*10));
//            editedReview.setReview(areaEditedReviewBody.getText());
//            if(editedReview.getReviewID() != 0) {
//                reviewService.save(editedReview);
//            }
//        }
//    }

//    @FXML
//    public void onBtnEditedReviewNextAction() {
//        if(currentReviewIndexProperty.getValue() < currentReviews.size()) {
//            currentReviewIndexProperty.setValue(currentReviewIndexProperty.getValue() + 1);
//            refreshDisplayedEditedReview();
//        }
//    }

//    private void refreshDisplayedEditedReview() {
//        // Set Fields To Values From Review At Current Index
//        if(currentReviews.size() > 0) {
//            Review review = currentReviews.stream().toList().get(currentReviewIndexProperty.getValue());
//            sliderEditedReviewRating.setValue(review.getRating() / 10.0);
//            areaEditedReviewBody.setText(review.getReview());
//            ZonedDateTime currentReviewDate = review.getCreationData();
//            String currentDate = currentReviewDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
//            labelEditedReviewCreation.setText(currentDate);
//        }
//    }

    /**
     * @author Grzegorz Lach
     */
    private void createBindings() {
        btnScrapeData.disableProperty().bind(Bindings.isEmpty(textImdbLink.textProperty()).or(new BooleanBinding() {
            {
                super.bind(textImdbLink.textProperty());
            }
            @Override
            protected boolean computeValue() {
                String toCheck = "https://www.imdb.com/title/";
                return !textImdbLink.textProperty().getValue().toLowerCase().contains(toCheck) ||
                        !(textImdbLink.textProperty().getValue().length() > toCheck.length());
            }
        }));
        btnSearchDatabase.disableProperty().bind(Bindings.isEmpty(textImdbLink.textProperty()).or(new BooleanBinding() {
            {
                super.bind(textImdbLink.textProperty());
            }
            @Override
            protected boolean computeValue() {
                String toCheck = "https://www.imdb.com/title/";
                return !textImdbLink.textProperty().getValue().toLowerCase().contains(toCheck) ||
                        !(textImdbLink.textProperty().getValue().length() > toCheck.length());
            }
        }).and(Bindings.isEmpty(textTitle.textProperty())));
        // Setting Of Elements Bindings For Current Movie List
        btnAddNewDirector.disableProperty().bind(Bindings.isEmpty(textNewDirectorName.textProperty()));
        btnRemoveDirector.disableProperty().bind(Bindings.isNull(comboRemoveDirector.valueProperty()));
    }

    private void attachListeners() {
        textImdbLink.textProperty().addListener((observable, oldValue, newValue) -> {
            if(Movie.validateImdbLink(newValue)) {
                clearTextFields();
                textImdbLink.setText(newValue);
                currentMovie = new Movie(newValue);
            } else {
                System.out.println("Invalid IMDB Link While Attaching Listeners!!");
                // TODO: Add logic for handling invalid IMDB link
            }
        });
        textTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            if(currentMovie != null) currentMovie.setTitle(newValue);
        });
        textReleaseDate.textProperty().addListener((observable, oldValue, newValue) -> {
            if(currentMovie != null) currentMovie.setReleaseDate(newValue);
        });
        textRuntime.textProperty().addListener((observable, oldValue, newValue) -> {
            if(currentMovie != null) currentMovie.setRuntime(newValue);
        });
        textLanguage.textProperty().addListener((observable, oldValue, newValue) -> {
            if(currentMovie != null) currentMovie.setLanguage(newValue);
        });
        textCountryOfOrigin.textProperty().addListener((observable, oldValue, newValue) -> {
            if(currentMovie != null) currentMovie.setCountryOfOrigin(newValue);
        });
        textCoverUrl.textProperty().addListener((observable, oldValue, newValue) -> {
            if(currentMovie != null) currentMovie.setCoverUrl(newValue);
        });
    }

    /**
     * @since v0.0.1
     * @author Grzegorz Lach
     * @param directorsToAdd List Of Directors Scrapped From Web To Be Added
     * TODO: Externalize Strings Containing Style Data And Dialog Creation Data
     */
    protected void addDirectors(List<String> directorsToAdd) {
        if(directorsToAdd != null && directorsToAdd.size() > 0) {
            tableAddedDirectors.getItems().clear();
            for(int i = 0; i < directorsToAdd.size(); i++) {
                String directorName = ScraperUtility.formatToCamelCase(directorsToAdd.get(i));
                Director director = new Director(directorName);
                if(directorService.existsByNameIgnoreCase(director.getName())) {
                    director = directorService.getDirectorByNameEqualsIgnoreCase(directorName);
                } else {
                    String dialogTitle = "Director Does Not Exist In Database";
                    String dialogMessage = "The Obtained Director " + directorName + " Does Not Exist In Database. Do You Want To Create New Director In Database?";
                    String dialogYes = "Create";
                    String dialogNo = "Skip";
                    Optional<ButtonType> answer = DialogUtility.createConfirmationDialog(dialogTitle, dialogMessage, dialogYes, dialogNo);
                    if(answer.isPresent()) {
                        if (answer.get().getText().equalsIgnoreCase(dialogYes)) {
                            director = directorService.findOrCreateNew(directorName);
                        }
                    }
                }
                tableAddedDirectors.getItems().add(director);
            }
        } else {
            tableAddedDirectors.getItems().clear();
        }
    }

    /**
     * Method used for adding new labels representing directors in proper color:<br>
     *      - Red   - Director Does Not Exist In Database<br>
     *      - Green - Director Exist In Database<br>
     * @since v0.0.1
     * @author Grzegorz Lach
     * @param writersToAdd List Of Writers Scrapped From Web To Be Added
     * TODO: Externalize Strings containing style data and dialog creation data
     */
    protected void addWriters(List<String> writersToAdd) {
        if(writersToAdd != null && writersToAdd.size() > 0) {
            tableAddedWriters.getItems().clear();
            for(int i = 0; i < writersToAdd.size(); i++) {
                String writerName = ScraperUtility.formatToCamelCase(writersToAdd.get(i));
                Writer writer = new Writer(writerName);
                if(writerService.existsByNameIgnoreCase(writer.getName())) {
                    writer = writerService.getWriterByNameEqualsIgnoreCase(writerName);
                } else {
                    String dialogTitle = "Writer Does Not Exist In Database";
                    String dialogMessage = "The Obtained Writer " + writerName + " Does Not Exist In Database. Do You Want To Create New Writer In Database?";
                    String dialogYes = "Create";
                    String dialogNo = "Skip";
                    Optional<ButtonType> answer = DialogUtility.createConfirmationDialog(dialogTitle, dialogMessage, dialogYes, dialogNo);
                    if(answer.isPresent()) {
                        if (answer.get().getText().equalsIgnoreCase(dialogYes)) {
                            writer = writerService.findOrCreateNew(writerName);
                        }
                    }
                }
                tableAddedWriters.getItems().add(writer);
            }
        } else {
            tableAddedWriters.getItems().clear();
        }
    }

    /**
     * Function For Returning Currently Added Directors (Given As Labels!)
     * @since v0.0.1
     * @author Grzegorz Lach
     * @return Set Of Director Objects Representing Currently Added Directors
     */
    private Set<Director> obtainCurrentDirectors() {
        Set<Director> returnSet;
        if(tableAddedDirectors.getItems().size() > 0) {
            returnSet = tableAddedDirectors.getItems().stream().collect(Collectors.toSet());
        } else {
            returnSet = new HashSet<>();
        }
        return returnSet;
    }

    private Set<Writer> obtainCurrentWriters() {
        Set<Writer> returnSet;
        if(tableAddedWriters.getItems().size() > 0) {
            returnSet = tableAddedWriters.getItems().stream().collect(Collectors.toSet());
        } else {
            returnSet = new HashSet<>();
        }
        return returnSet;
    }

//    private Set<Review> obtainCurrentReviews() {
//        //TODO: Add Logic For Obtaining Current Reviews For Selected Movie
//        return currentReviews;
//    }

    private void clearTextFields() {
        textRuntime.clear();
        textTitle.clear();
        textLanguage.clear();
        textCoverUrl.clear();
        textCountryOfOrigin.clear();
        textReleaseDate.clear();
        addWriters(List.of());
        addDirectors(List.of());
        viewCoverImage.setImage(null);
    }
}
