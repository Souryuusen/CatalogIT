package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.entity.Director;
import com.souryuu.catalogit.service.DirectorService;
import com.souryuu.catalogit.service.MovieService;
import com.souryuu.catalogit.utility.ScraperUtility;
import com.souryuu.imdbscrapper.MovieDataExtractor;
import com.souryuu.imdbscrapper.entity.MovieData;
import com.souryuu.imdbscrapper.entity.ProductionDetailKeys;
import javafx.beans.binding.Bindings;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

@Component
@FxmlView("movie-view.fxml")
public class MovieController {

    private final MovieService movieService;
    private final DirectorService directorService;

    //##################################################################################################################

    @FXML TextField textImdbLink;
    @FXML TextField textTitle;
    @FXML TextField textDirectors;
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
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML TextFlow flowTest;

    private void addDirectors(List<String> directorsToAdd) {
        flowTest.getChildren().clear();
        if(directorsToAdd != null && directorsToAdd.size() > 0) {
            Text directorText;
            Text commaText = new Text(", ");
            commaText.setStyle("-fx-fill: BLACK;-fx-font-weight:normal;");
            for(int i = 0; i < directorsToAdd.size(); i++) {
                String directorName = ScraperUtility.formatToCamelCase(directorsToAdd.get(i));
                Director director = new Director(directorName);
                directorText = new Text();
                directorText.setText(directorName);

                System.out.println(director);
                if(directorService.directorExistInDB(director)) {
                    directorText.setStyle("-fx-fill: GREEN;-fx-font-weight:normal;");
                } else {
                    directorText.setStyle("-fx-fill: RED;-fx-font-weight:bold;");
                }
                flowTest.getChildren().add(directorText);
                if(i != directorsToAdd.size()-1) flowTest.getChildren().add(commaText);
            }
        }
    }

    private void addWriters(List<String> writersToAdd) {

    }

}
