package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.entity.Writer;
import com.souryuu.catalogit.entity.Review;
import com.souryuu.catalogit.exception.ViewLoadException;
import com.souryuu.catalogit.service.DirectorService;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.service.ReviewService;
import com.souryuu.catalogit.service.WriterService;
import com.souryuu.catalogit.utility.DialogUtility;
import com.souryuu.catalogit.utility.ExternalFileParser;
import com.souryuu.catalogit.utility.FXUtility;
import com.souryuu.catalogit.utility.ScraperUtility;
import com.souryuu.imdbscrapper.entity.MovieData;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import lombok.SneakyThrows;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Component
@FxmlView("movie-view.fxml")
public class MovieController {

    private final FxWeaver fxWeaver;
    private final MovieService movieService;
    private final DirectorService directorService;
    private final WriterService writerService;
    private final ReviewService reviewService;

    //##################################################################################################################

    private Movie currentMovie;
    private final HashSet<Review> currentReviews = new HashSet<>();
    private final IntegerProperty currentReviewIndexProperty = new SimpleIntegerProperty(0);

    private Thread loadingThread;

    //##################################################################################################################
    @FXML AnchorPane root;
    @FXML AnchorPane detailPane;
    @FXML AnchorPane editDirectorRoot;
    @FXML AnchorPane editWriterRoot;
    @FXML AnchorPane addReviewRoot;
    @FXML AnchorPane editReviewRoot;

    @FXML HBox hDirectors;
    @FXML HBox hWriters;
    @FXML HBox hAddNewDirector;
    @FXML HBox hRemoveDirector;
    @FXML HBox hAddNewWriter;
    @FXML HBox hRemoveWriter;

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
    @FXML Button btnEditDirector;
    @FXML Button btnEditWriter;
    @FXML Button btnAddNewReview;
    @FXML Button btnAddNewDirector;
    @FXML Button btnRemoveDirector;
    @FXML Button btnAddNewWriter;
    @FXML Button btnRemoveWriter;
    @FXML Button btnAddReview;
    @FXML Button btnEditReview;
    @FXML Button btnEditedReviewPrevious;
    @FXML Button btnEditedReviewSave;
    @FXML Button btnEditedReviewNext;

    @FXML ComboBox<String> comboRemoveDirector;
    @FXML ComboBox<String> comboRemoveWriter;

    @FXML Slider sliderReviewRating;
    @FXML Slider sliderEditedReviewRating;

    @FXML ImageView viewCoverImage;

    @FXML TableView<Director> tableDirectors;
    @FXML TableView<Writer> tableWriters;

    @FXML TableColumn<Director, Long> columnDirectorsID;
    @FXML TableColumn<Director, String> columnDirectorsName;
    @FXML TableColumn<Writer, Long> columnWritersID;
    @FXML TableColumn<Writer, String> columnWritersName;

    public MovieController(MovieService movieService, DirectorService directorService, WriterService writerService, FxWeaver fxWeaver, ReviewService reviewService) {
        this.movieService = movieService;
        this.directorService = directorService;
        this.writerService = writerService;
        this.fxWeaver = fxWeaver;
        this.reviewService = reviewService;
    }
    //##################################################################################################################

    @FXML
    public void initialize() {
        createBindings();
        attachListeners();
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
            // Clear Detail Pane
            detailPane.getChildren().clear();
            // Verification If Movie With This URL Exists In DB
            currentMovie = movieService.getMovieByImdbUrl(movieImdbLink);
            if(currentMovie != null && currentMovie.getImdbUrl().equalsIgnoreCase(movieImdbLink)) {
                // Fetch All Fields From DB
                currentMovie = movieService.getMovieByImdbUrlWithInitialization(currentMovie.getImdbUrl());
                currentReviews.addAll(currentMovie.getReviews());
                addDirectors(currentMovie.getDirectors().stream().map(Director::getName).toList());
                addWriters(currentMovie.getWriters().stream().map(Writer::getName).toList());
            } else {
                MovieData scrapedData = ScraperUtility.scrapeData(movieImdbLink);
                currentMovie = new Movie(movieImdbLink, scrapedData);
                currentReviews.clear();
                addDirectors(scrapedData.getDirectors());
                addWriters(scrapedData.getWriters());
            }
            textTitle.setText(currentMovie.getTitle());
            textCoverUrl.setText(currentMovie.getCoverUrl());
            textRuntime.setText(currentMovie.getRuntime());
            textReleaseDate.setText(currentMovie.getReleaseDate());
            textCountryOfOrigin.setText(currentMovie.getCountryOfOrigin());
            textLanguage.setText(currentMovie.getLanguage());
            FXUtility.changeImageViewContent(viewCoverImage, currentMovie.getCoverUrl(), null);
            currentMovie.setWriters(obtainCurrentWriters());
            currentMovie.setDirectors(obtainCurrentDirectors());
        }
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @SneakyThrows
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

                        if (lineData.size() != 4) continue;

                        String imdbUrl = lineData.get(2).trim();
                        if (movieService.getMovieByImdbUrl(imdbUrl) == null) {
                            MovieData scrapedData = ScraperUtility.scrapeData(imdbUrl);
                            Movie movieToAdd = new Movie(imdbUrl, scrapedData);
                            HashSet<Director> directorsToAdd = new HashSet<>();
                            HashSet<Writer> writersToAdd = new HashSet<>();
                            for (String directorName : scrapedData.getDirectors()) {
                                String n = ScraperUtility.formatToCamelCase(directorName);
                                if (!directorService.existsByNameIgnoreCase(ScraperUtility.formatToCamelCase(directorName))) {
                                    Director d = new Director(n);
                                    directorService.save(d);
                                    directorsToAdd.add(d);
                                } else {
                                    directorsToAdd.add(directorService.getDirectorByNameEqualsIgnoreCase(n));
                                }
                            }
                            movieToAdd.setDirectors(directorsToAdd);
                            for (String writerName : scrapedData.getWriters()) {
                                String n = ScraperUtility.formatToCamelCase(writerName);
                                if (!writerService.existsByNameIgnoreCase(n)) {
                                    Writer w = new Writer(n);
                                    writerService.save(w);
                                    writersToAdd.add(w);
                                } else {
                                    writersToAdd.add(writerService.getWriterByNameEqualsIgnoreCase(n));
                                }
                            }
                            movieToAdd.setWriters(writersToAdd);
                            movieService.save(movieToAdd);
                            System.out.println(movieToAdd);
                            // Review
                            double rating = Double.parseDouble(lineData.get(1).trim());
                            rating *= 10.0;
                            Review r = new Review((int) rating, lineData.get(3).trim(), ZonedDateTime.now(), movieToAdd);
                            reviewService.save(r);
                            System.out.println(r);
                            Platform.runLater(() -> {
                                textImdbLink.setText(movieToAdd.getImdbUrl());
                                textTitle.setText(movieToAdd.getTitle());
                                textLanguage.setText(movieToAdd.getLanguage());
                                textCoverUrl.setText(movieToAdd.getCoverUrl());
                                textRuntime.setText(movieToAdd.getRuntime());
                                textCountryOfOrigin.setText(movieToAdd.getCountryOfOrigin());
                                textReleaseDate.setText(movieToAdd.getReleaseDate());
                                addDirectors(movieToAdd.getDirectors().stream().map(Director::getName).toList());
                                addWriters(movieToAdd.getWriters().stream().map(Writer::getName).toList());
                                FXUtility.changeImageViewContent(viewCoverImage, movieToAdd.getCoverUrl(), null);
                            });
                        } else {
                            // TODO: Add logic for movie for read already existing in db...
                            System.out.println("Movie Already Exists In Database!!");
                        }
                        System.out.println("Movie With Link:\t" + imdbUrl + " Already Exists In Database!");
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

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnSaveDataAction() {
        //TODO: Refactor Method
        if(currentMovie != null && currentMovie.getImdbUrl().length() > 0) {
            currentMovie.setDirectors(obtainCurrentDirectors());
            currentMovie.setWriters(obtainCurrentWriters());
            movieService.save(currentMovie);
            currentMovie.setReviews(obtainCurrentReviews());
            movieService.save(currentMovie);
        }
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnEditDirectorAction() {
        // Parsing Current Directors List
        HashSet<Director> currentDirectors = (HashSet<Director>)obtainCurrentDirectors();
        // Creating List Of Directors Names
        List<String> currentDirectorNames = new ArrayList<>(currentDirectors.size());
        currentDirectors.forEach(d -> currentDirectorNames.add(d.getName()));
        // Setting New Content To Display On detailsPane
        detailPane.getChildren().clear();
        Optional<Node> viewOptional = fxWeaver.load(MovieEditDirectorController.class).getView();
        if(viewOptional.isPresent()) {
            Node newContent = viewOptional.get();
            detailPane.getChildren().setAll(newContent);
        } else {
            throw new ViewLoadException("Cannot Load View From MovieEditDirectorController Class!!");
        }
        // Setting Of Elements Bindings
        btnAddNewDirector.disableProperty().bind(Bindings.isEmpty(textNewDirectorName.textProperty()));
        btnRemoveDirector.disableProperty().bind(Bindings.isNull(comboRemoveDirector.valueProperty()));
        // Filling ComboBox Data From
        comboRemoveDirector.setItems(FXCollections.observableList(currentDirectorNames).sorted());
        // Initialize Tables Properties
        columnDirectorsID.setCellValueFactory(new PropertyValueFactory<>("directorID"));
        columnDirectorsID.setSortType(TableColumn.SortType.ASCENDING);
        columnDirectorsName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Table Row Clicked Listener
        tableDirectors.setRowFactory(rf -> {
            TableRow<Director> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Director rowData = row.getItem();
                    textNewDirectorName.setText(rowData.getName());
                }
            });
            return row ;
        });
        // Filling TableView With Database Entries
        tableDirectors.getItems().clear();
        tableDirectors.setItems(FXCollections.observableList(directorService.findAll()));
        // Sorting Table By Director ID Field
        tableDirectors.getSortOrder().add(columnDirectorsID);
        tableDirectors.sort();
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
            tableDirectors.getItems().clear();
            tableDirectors.setItems(FXCollections.observableList(directorService.findAll()));
            // Sorting Table By Director ID Field
            tableDirectors.getSortOrder().add(columnDirectorsID);
            tableDirectors.sort();
        }
    }

    @FXML
    public void onBtnRemoveDirectorAction() {
        if(comboRemoveDirector.getValue() != null && !comboRemoveDirector.getValue().trim().equalsIgnoreCase("")) {
            Director directorToRemove = new Director(comboRemoveDirector.getSelectionModel().getSelectedItem());
            // Parsing Current Directors List
            HashSet<Director> currentDirectors = (HashSet<Director>)obtainCurrentDirectors();
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

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnEditWriterAction() {
        // Parsing Current Directors List
        HashSet<Writer> currentWriters = (HashSet<Writer>)obtainCurrentWriters();
        // Creating List Of Directors Names
        List<String> currentWritersNames = new ArrayList<>(currentWriters.size());
        currentWriters.forEach(d -> currentWritersNames.add(d.getName()));
        // Setting New Content To Display On detailsPane
        detailPane.getChildren().clear();
        Optional<Node> viewOptional = fxWeaver.load(MovieEditWriterController.class).getView();
        if(viewOptional.isPresent()) {
            Node newContent = viewOptional.get();
            detailPane.getChildren().setAll(newContent);
        } else {
            throw new ViewLoadException("Error Loading View From MovieEditWriterController Class!!");
        }
        // Setting Of Elements Bindings
        btnAddNewWriter.disableProperty().bind(Bindings.isEmpty(textNewWriterName.textProperty()));
        btnRemoveWriter.disableProperty().bind(Bindings.isNull(comboRemoveWriter.valueProperty()));
        // Filling ComboBox Data From
        comboRemoveWriter.setItems(FXCollections.observableList(currentWritersNames).sorted());
        // Initialize Tables Properties
        columnWritersID.setCellValueFactory(new PropertyValueFactory<>("writerID"));
        columnWritersID.setSortType(TableColumn.SortType.ASCENDING);
        columnWritersName.setCellValueFactory(new PropertyValueFactory<>("name"));
        // Table Row Clicked Listener
        tableWriters.setRowFactory(rf -> {
            TableRow<Writer> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    Writer rowData = row.getItem();
                    textNewWriterName.setText(rowData.getName());
                }
            });
            return row ;
        });
        // Filling TableView With Database Entries
        tableWriters.getItems().clear();
        tableWriters.setItems(FXCollections.observableList(writerService.findAll()));
        // Sorting Table By Director ID Field
        tableWriters.getSortOrder().add(columnWritersID);
        tableWriters.sort();
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
            tableWriters.getItems().clear();
            tableWriters.setItems(FXCollections.observableList(writerService.findAll()));
            // Sorting Table By Director ID Field
            tableWriters.getSortOrder().add(columnWritersID);
            tableWriters.sort();
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
            boolean removed = currentWriters.remove(writerToRemove);
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
    @FXML
    public void onBtnAddNewReviewAction() {
        // Setting New Content To Display On detailsPane
        detailPane.getChildren().clear();
        Optional<Node> viewOptional = fxWeaver.load(MovieAddReviewController.class).getView();
        if(viewOptional.isPresent()) {
            Node newContent = viewOptional.get();
            detailPane.getChildren().setAll(newContent);
        } else {
            throw new ViewLoadException("Error Loading View From MovieAddReviewController Class!!");
        }
    }

    @FXML
    public void onBtnAddReviewAction() {
        if(currentMovie != null && areaReviewBody.getText().trim().length() > 0) {
            int rating = (int)(sliderReviewRating.getValue()*10);
            String reviewBody = areaReviewBody.getText().trim();
            ZonedDateTime now = ZonedDateTime.now();
            Review newReview = new Review(rating, reviewBody, now, currentMovie);
            currentReviews.add(newReview);
            sliderReviewRating.setValue(5);
            areaReviewBody.clear();
            detailPane.getChildren().clear();
        }
    }

    @FXML
    public void onBtnEditReviewAction() {
        // Setting New Content To Display On detailsPane
        detailPane.getChildren().clear();
        Optional<Node> viewOptional = fxWeaver.load(MovieEditReviewController.class).getView();
        if(viewOptional.isPresent()) {
            Node newContent = viewOptional.get();
            detailPane.getChildren().setAll(newContent);
        } else {
            throw new ViewLoadException("Error Loading View From MovieEditReviewController Class!!");
        }
        // Creating Bindings For Navigation Buttons
        btnEditedReviewPrevious.disableProperty().bind(currentReviewIndexProperty.isEqualTo(0));
        btnEditedReviewNext.disableProperty().bind(currentReviewIndexProperty.lessThan(currentReviews.size()-1).not());
        labelEditedReviewIndex.textProperty().bind(Bindings.convert(currentReviewIndexProperty.add(1)));
        // Setting Current Index To 0
        this.currentReviewIndexProperty.setValue(0);
        // Set Fields To Values From Review At Index 0
        refreshDisplayedEditedReview();
    }

    @FXML
    public void onBtnEditedReviewPreviousAction() {
        if(currentReviewIndexProperty.getValue() > 0) {
            currentReviewIndexProperty.setValue(currentReviewIndexProperty.getValue()-1);
            refreshDisplayedEditedReview();
        }
    }

    @FXML
    public void onBtnEditedReviewSaveAction() {
        Review editedReview = currentReviews.stream().toList().get(currentReviewIndexProperty.getValue());
        if(sliderEditedReviewRating.getValue() == 0 && areaEditedReviewBody.getText().equalsIgnoreCase("")) {
            currentReviews.remove(editedReview);
            if(editedReview.getReviewID() != 0) {
                reviewService.deleteByReviewID(editedReview.getReviewID());
            }
        } else {
            editedReview.setRating((int)(sliderEditedReviewRating.getValue()*10));
            editedReview.setReview(areaEditedReviewBody.getText());
            if(editedReview.getReviewID() != 0) {
                reviewService.save(editedReview);
            }
        }
    }

    @FXML
    public void onBtnEditedReviewNextAction() {
        if(currentReviewIndexProperty.getValue() < currentReviews.size()) {
            currentReviewIndexProperty.setValue(currentReviewIndexProperty.getValue() + 1);
            refreshDisplayedEditedReview();
        }
    }

    private void refreshDisplayedEditedReview() {
        // Set Fields To Values From Review At Current Index
        if(currentReviews.size() > 0) {
            Review review = currentReviews.stream().toList().get(currentReviewIndexProperty.getValue());
            sliderEditedReviewRating.setValue(review.getRating() / 10.0);
            areaEditedReviewBody.setText(review.getReview());
            ZonedDateTime currentReviewDate = review.getCreationData();
            String currentDate = currentReviewDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            labelEditedReviewCreation.setText(currentDate);
        }
    }

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
        String styleExist = "-fx-background-color: white; -fx-max-width: 300; -fx-text-fill: GREEN;-fx-font-weight:normal;";
        String styleNotExist = "-fx-background-color: white; -fx-max-width: 300; -fx-text-fill: RED;-fx-font-weight:bold;";
        hDirectors.getChildren().clear();
        if(directorsToAdd != null && directorsToAdd.size() > 0) {
            Label directorLabel;
            for(int i = 0; i < directorsToAdd.size(); i++) {
                String directorName = ScraperUtility.formatToCamelCase(directorsToAdd.get(i));
                Director director = new Director(directorName);
                directorLabel = new Label();
                if(directorService.existsByNameIgnoreCase(director.getName())) {
                    directorLabel.setStyle(styleExist);
                } else {
                    String dialogTitle = "Director Does Not Exist In Database";
                    String dialogMessage = "The Obtained Director " + director.getName() + " Does Not Exist In Database. Do You Want To Create New Director In Database?";
                    String dialogYes = "Create";
                    String dialogNo = "Skip";
                    Optional<ButtonType> answer = DialogUtility.createConfirmationDialog(dialogTitle, dialogMessage, dialogYes, dialogNo);
                    if(answer.isPresent()) {
                        if (answer.get().getText().equalsIgnoreCase(dialogYes)) {
                            directorService.save(director);
                            directorLabel.setStyle(styleExist);
                        } else {
                            directorLabel.setStyle(styleNotExist);
                        }
                    }
                }
                directorLabel.setText(directorName);
                directorLabel.setAlignment(Pos.CENTER_LEFT);
                hDirectors.getChildren().add(directorLabel);
                if(i != directorsToAdd.size()-1) hDirectors.getChildren().add(new Label(", "));
            }
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
        String styleExist = "-fx-background-color: white; -fx-max-width: 300; -fx-text-fill: GREEN;-fx-font-weight:normal;";
        String styleNotExist = "-fx-background-color: white; -fx-max-width: 300; -fx-text-fill: RED;-fx-font-weight:bold;";
        hWriters.getChildren().clear();
        if(writersToAdd != null && writersToAdd.size() > 0) {
            Label writerLabel;
            for(int i = 0; i < writersToAdd.size(); i++) {
                String writerName = ScraperUtility.formatToCamelCase(writersToAdd.get(i));
                Writer writer = new Writer(writerName);
                writerLabel = new Label();
                if(writerService.existsByNameIgnoreCase(writer.getName())) {
                    writerLabel.setStyle(styleExist);
                } else {
                    String dialogTitle = "Writer Does Not Exist In Database";
                    String dialogMessage = "The Obtained Writer " + writer.getName() + " Does Not Exist In Database. Do You Want To Create New Writer In Database?";
                    String dialogYes = "Create";
                    String dialogNo = "Skip";
                    Optional<ButtonType> answer = DialogUtility.createConfirmationDialog(dialogTitle, dialogMessage, dialogYes, dialogNo);
                    if(answer.isPresent()) {
                        if (answer.get().getText().equalsIgnoreCase(dialogYes)) {
                            writerService.save(writer);
                            writerLabel.setStyle(styleExist);
                        } else {
                            writerLabel.setStyle(styleNotExist);
                        }
                    }
                }
                writerLabel.setText(writerName);
                writerLabel.setAlignment(Pos.CENTER_LEFT);
                hWriters.getChildren().add(writerLabel);
                if(i != writersToAdd.size()-1) hWriters.getChildren().add(new Label(", "));
            }
        }
    }

    /**
     * Function For Returning Currently Added Directors (Given As Labels!)
     * @since v0.0.1
     * @author Grzegorz Lach
     * @return Set Of Director Objects Representing Currently Added Directors
     */
    private Set<Director> obtainCurrentDirectors() {
        // Returned Set
        HashSet<Director> obtainedSet = new HashSet<>();
        // Parsing Labels To Directors
        ObservableList<Node> elements = hDirectors.getChildren();
        for(Node e : elements) {
            if(e instanceof Label) {
                String content = ((Label) e).getText().trim();
                if(!content.equalsIgnoreCase(",")) {
                    // Verify If Director Already Exists In Database
                    if (directorService.existsByNameIgnoreCase(content)) {
                        Director fetchedDirector = directorService.getDirectorByNameEqualsIgnoreCase(content);
                        obtainedSet.add(fetchedDirector);
                    } else {
                        Director createdDirector = new Director(content);
                        obtainedSet.add(createdDirector);
                    }
                }
            }
        }
        return obtainedSet;
    }

    private Set<Writer> obtainCurrentWriters() {
        // Returned Set
        HashSet<Writer> obtainedSet = new HashSet<>();
        // Parsing Labels To Directors
        ObservableList<Node> elements = hWriters.getChildren();
        for(Node e : elements) {
            if(e instanceof Label label) {
                String content = label.getText().trim();
                if(!content.equalsIgnoreCase(",")) {
                    // Verify If Writer Exists In Database
                    if(writerService.existsByNameIgnoreCase(content)) {
                        Writer fetchedWriter = writerService.getWriterByNameEqualsIgnoreCase(content);
                        obtainedSet.add(fetchedWriter);
                    } else {
                        Writer createdWriter = new Writer(content);
                        obtainedSet.add(createdWriter);
                    }
                }
            }
        }
        return obtainedSet;
    }

    private Set<Review> obtainCurrentReviews() {
        //TODO: Add Logic For Obtaining Current Reviews For Selected Movie
        return currentReviews;
    }

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
