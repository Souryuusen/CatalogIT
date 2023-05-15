package com.souryuu.catalogit.utility;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

public class FXUtility {

    public static void changeImageViewContent(ImageView view, String url, BufferedImage errorImage) {
        try {
            BufferedImage image = ImageIO.read(new URL(url));
            view.setImage(SwingFXUtils.toFXImage(image, null));
        } catch (IOException ex) {
            view.setImage(SwingFXUtils.toFXImage(errorImage, null));
        }
        view.setPreserveRatio(true);
    }

}
