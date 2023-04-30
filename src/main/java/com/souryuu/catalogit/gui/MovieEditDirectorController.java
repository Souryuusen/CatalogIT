package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.service.DirectorService;
import javafx.fxml.FXML;
import javafx.scene.layout.AnchorPane;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("movie-edit-director-view.fxml")
public class MovieEditDirectorController {

    private final DirectorService directorService;

    //##################################################################################################################
    @FXML AnchorPane root;
    //##################################################################################################################

    public MovieEditDirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @FXML
    public void initialize() {

    }
}
