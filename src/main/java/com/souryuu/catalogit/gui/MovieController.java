package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.service.DirectorService;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.utility.DialogUtility;
import com.souryuu.catalogit.utility.ScraperUtility;
import com.souryuu.imdbscrapper.entity.MovieData;
import com.souryuu.imdbscrapper.entity.ProductionDetailKeys;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
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

    private final MovieService movieService;
    private final DirectorService directorService;

    //##################################################################################################################
    @FXML HBox hDirectors;

    @FXML TextField textImdbLink;
    @FXML TextField textTitle;
    @FXML TextField textWriters;
    @FXML TextField textReleaseDate;
    @FXML TextField textRuntime;
    @FXML TextField textLanguage;
    @FXML TextField textCountryOfOrigin;
    @FXML TextField textCoverUrl;

    @FXML Button btnScrapeData;
    @FXML Button btnLoadData;
    @FXML Button btnSaveData;

    @FXML ImageView viewCoverImage;

    public MovieController(MovieService movieService, DirectorService directorService) {
        this.movieService = movieService;
        this.directorService = directorService;
    }
    //##################################################################################################################

    @FXML
    public void initialize() {
        createBindings();
    }

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

    private void createBindings() {
        btnScrapeData.disableProperty().bind(Bindings.isEmpty(textImdbLink.textProperty()));
    }

    private void displayMovieCover(String url) {
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            viewCoverImage.setImage(SwingFXUtils.toFXImage(image, null));
            viewCoverImage.setPreserveRatio(true);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

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

    private void addWriters(List<String> writersToAdd) {

    }


}
