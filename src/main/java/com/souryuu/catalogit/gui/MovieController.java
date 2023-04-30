package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.entity.Writer;
import com.souryuu.catalogit.service.DirectorService;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.service.WriterService;
import com.souryuu.catalogit.utility.DialogUtility;
import com.souryuu.catalogit.utility.ScraperUtility;
import com.souryuu.imdbscrapper.entity.MovieData;
import com.souryuu.imdbscrapper.entity.ProductionDetailKeys;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;

@Component
@FxmlView("movie-view.fxml")
public class MovieController {

    private final FxWeaver fxWeaver;
    private final MovieService movieService;
    private final DirectorService directorService;
    private final WriterService writerService;

    //##################################################################################################################
    @FXML AnchorPane root;
    @FXML AnchorPane detailPane;

    @FXML HBox hDirectors;
    @FXML HBox hWriters;

    @FXML TextField textImdbLink;
    @FXML TextField textTitle;
    @FXML TextField textReleaseDate;
    @FXML TextField textRuntime;
    @FXML TextField textLanguage;
    @FXML TextField textCountryOfOrigin;
    @FXML TextField textCoverUrl;

    @FXML Button btnScrapeData;
    @FXML Button btnLoadData;
    @FXML Button btnSaveData;
    @FXML Button btnEditDirector;
    @FXML Button btnEditWriter;
    @FXML Button btnAddNewReview;

    @FXML ImageView viewCoverImage;

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
        detailPane.getChildren().clear();
        Node newContent = fxWeaver.load(MovieEditDirectorController.class).getView().get();
        detailPane.getChildren().setAll(newContent);
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnEditWriterAction() {
        //TODO: Add Method Implementation
    }

    /**
     * @author Grzegorz Lach
     * @since v0.0.1
     */
    @FXML
    public void onBtnAddNewReviewAction() {
        //TODO: Add Method Implementation
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
    private void addDirectors(List<String> directorsToAdd) {
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
    private void addWriters(List<String> writersToAdd) {
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


}
