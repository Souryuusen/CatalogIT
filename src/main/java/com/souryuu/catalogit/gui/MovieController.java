package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.entity.Movie;
import com.souryuu.catalogit.entity.Writer;
import com.souryuu.catalogit.service.DirectorService;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.service.WriterService;
import com.souryuu.catalogit.utility.DialogUtility;
import com.souryuu.catalogit.utility.ScraperUtility;
import com.souryuu.imdbscrapper.entity.MovieData;
import com.souryuu.imdbscrapper.entity.ProductionDetailKeys;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.stream.Collectors;

@Component
@FxmlView("movie-view.fxml")
public class MovieController {

    private final FxWeaver fxWeaver;
    private final MovieService movieService;
    private final DirectorService directorService;
    private final WriterService writerService;

    //##################################################################################################################

    private Movie currentMovie;

    //##################################################################################################################
    @FXML AnchorPane root;
    @FXML AnchorPane detailPane;
    @FXML AnchorPane editDirectorRoot;
    @FXML AnchorPane editWriterRoot;

    @FXML HBox hDirectors;
    @FXML HBox hWriters;
    @FXML HBox hAddNewDirector;
    @FXML HBox hRemoveDirector;
    @FXML HBox hAddNewWriter;
    @FXML HBox hRemoveWriter;

    @FXML TextField textImdbLink;
    @FXML TextField textTitle;
    @FXML TextField textReleaseDate;
    @FXML TextField textRuntime;
    @FXML TextField textLanguage;
    @FXML TextField textCountryOfOrigin;
    @FXML TextField textCoverUrl;
    @FXML TextField textNewDirectorName;
    @FXML TextField textNewWriterName;

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

    @FXML ComboBox<String> comboRemoveDirector;
    @FXML ComboBox<String> comboRemoveWriter;

    @FXML ImageView viewCoverImage;

    @FXML TableView tableDirectors;
    @FXML TableView tableWriters;

    @FXML TableColumn<Director, Long> columnDirectorsID;
    @FXML TableColumn<Director, String> columnDirectorsName;
    @FXML TableColumn<Writer, Long> columnWritersID;
    @FXML TableColumn<Writer, String> columnWritersName;

    public MovieController(MovieService movieService, DirectorService directorService, WriterService writerService, FxWeaver fxWeaver) {
        this.movieService = movieService;
        this.directorService = directorService;
        this.writerService = writerService;
        this.fxWeaver = fxWeaver;
    }
    //##################################################################################################################

    @FXML
    public void initialize() {
        createBindings();
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnScrapeDataAction() {
        if(textImdbLink.getText().trim().length() > 0) {
            // Clear Detail Pane
            detailPane.getChildren().clear();
            // Verification If Movie With This URL Exists In DB
            Movie currentMovie = movieService.getMovieByImdbUrl(textImdbLink.getText().trim().toLowerCase());
            if(currentMovie != null && currentMovie.getImdbUrl().equalsIgnoreCase(textImdbLink.getText())) {
                System.out.println("debug out");
            } else {
                MovieData scrapedData = ScraperUtility.scrapeData(textImdbLink.getText().trim());
                textTitle.setText(scrapedData.getTitle());
                textCoverUrl.setText(scrapedData.getCoverURL());
                textRuntime.setText(scrapedData.getRuntime());
                textReleaseDate.setText(scrapedData.getProductionDetails().get(ProductionDetailKeys.RELEASE_DATE));
                textCountryOfOrigin.setText(scrapedData.getProductionDetails().get(ProductionDetailKeys.COUNTRY_OF_ORIGIN));
                textLanguage.setText(scrapedData.getProductionDetails().get(ProductionDetailKeys.LANGUAGE));
                displayMovieCover(scrapedData.getCoverURL());
                addDirectors(scrapedData.getDirectors());
                addWriters(scrapedData.getWriters());
            }








        }
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnLoadDataAction() {
        //TODO: Add Method Implementation
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnSaveDataAction() {
        //TODO: Add Method Implementation
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
        currentDirectors.stream().sorted().forEach(d -> currentDirectorNames.add(d.getName()));
        // Setting New Content To Display On detailsPane
        detailPane.getChildren().clear();
        Node newContent = fxWeaver.load(MovieEditDirectorController.class).getView().get();
        detailPane.getChildren().setAll(newContent);
        // Setting Of Elements Bindings
        btnAddNewDirector.disableProperty().bind(Bindings.isEmpty(textNewDirectorName.textProperty()));
        btnRemoveDirector.disableProperty().bind(Bindings.isNull(comboRemoveDirector.valueProperty()));
        // Filling ComboBox Data From
        comboRemoveDirector.setItems(FXCollections.observableList(currentDirectorNames));
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
            addedDirectors.stream().forEach(d -> addedDirectorNames.add(d.getName()));
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
                List<String> addedDirectorNames = currentDirectors.stream().map(d -> d.getName()).collect(Collectors.toList());
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
        currentWriters.stream().sorted().forEach(d -> currentWritersNames.add(d.getName()));
        // Setting New Content To Display On detailsPane
        detailPane.getChildren().clear();
        Node newContent = fxWeaver.load(MovieEditWriterController.class).getView().get();
        detailPane.getChildren().setAll(newContent);
        // Setting Of Elements Bindings
        btnAddNewWriter.disableProperty().bind(Bindings.isEmpty(textNewWriterName.textProperty()));
        btnRemoveWriter.disableProperty().bind(Bindings.isNull(comboRemoveWriter.valueProperty()));
        // Filling ComboBox Data From
        comboRemoveWriter.setItems(FXCollections.observableList(currentWritersNames));
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
        if(textNewWriterName.getText().trim().length() > 0) {
            // Retrieve New Director Name
            String newWriterName = ScraperUtility.formatToCamelCase(textNewWriterName.getText().trim());
            // Retrieve HashSet Of All Added Directors
            HashSet<Writer> addedWriters = (HashSet<Writer>) obtainCurrentWriters();
            addedWriters.add(new Writer(newWriterName));
            // Creating Names List To Transform Into Labels
            ArrayList<String> addedWritersNames = new ArrayList<>(addedWriters.size() + 1);
            addedWriters.stream().forEach(w -> addedWritersNames.add(w.getName()));
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
                List<String> addedWritersNames = currentWriters.stream().map(w -> w.getName()).collect(Collectors.toList());
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
        Node newContent = fxWeaver.load(MovieAddReviewController.class).getView().get();
        detailPane.getChildren().setAll(newContent);
    }

    /**
     * @author Grzegorz Lach
     * TODO: Add Verification Of IMDB Link Corectness To Binding (URL Containing "imdb.com" Part)
     */
    private void createBindings() {
        btnScrapeData.disableProperty().bind(Bindings.isEmpty(textImdbLink.textProperty()));
    }

    /**
     * @since v0.0.1
     * @author Grzegorz Lach
     * @param url String Containing Web Link For Cover Image For Movie
     */
    private void displayMovieCover(String url) {
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            viewCoverImage.setImage(SwingFXUtils.toFXImage(image, null));
            viewCoverImage.setPreserveRatio(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
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
                if(directorService.directorExistInDB(director)) {
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
                if(writerService.writerExistInDB(writer)) {
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
            if(e != null && e instanceof Label) {
                Label label = (Label) e;
                if(!label.getText().trim().equalsIgnoreCase(",")) {
                    Director parsedDirector = new Director(label.getText());
                    if(!obtainedSet.contains(parsedDirector)) obtainedSet.add(parsedDirector);
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
            if(e != null && e instanceof Label) {
                Label label = (Label) e;
                if(!label.getText().trim().equalsIgnoreCase(",")) {
                    Writer parsedWriter = new Writer(label.getText());
                    if(!obtainedSet.contains(parsedWriter)) obtainedSet.add(parsedWriter);
                }
            }
        }
        return obtainedSet;
    }

}
