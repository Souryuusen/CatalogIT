package com.souryuu.catalogit.utility;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class FXUtility {

    public static void changeImageViewContent(ImageView view, String url, boolean useErrorImage) {
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            view.setImage(SwingFXUtils.toFXImage(image, null));
        } catch (IOException ex) {
            if(useErrorImage) {
                BufferedImage errorImage;
                try {
                    URL errorImageURL = FXUtility.class.getResource("/static/images/NO_IMG.png");
                    if(errorImageURL != null) {
                        errorImage = ImageIO.read(errorImageURL);
                        view.setImage(SwingFXUtils.toFXImage(errorImage, null));
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        view.setPreserveRatio(true);
    }

}
