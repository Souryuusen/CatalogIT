package com.souryuu.catalogit.gui;

import com.souryuu.catalogit.service.ReviewService;
import javafx.fxml.FXML;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

@Component
@FxmlView("movie-add-review-view.fxml")
public class MovieAddReviewController {

    private final ReviewService reviewService;

    //##################################################################################################################

    //##################################################################################################################

    public MovieAddReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @FXML
    public void initialize() {

    }


}
