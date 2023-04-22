package com.souryuu.catalogit.event;

import com.souryuu.catalogit.gui.MainController;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;

@Component
public class StageReadyEventListener implements ApplicationListener<StageReadyEvent> {

    private final ApplicationContext context;
    private final String applicationTitle;
    private final String labelText;

    public StageReadyEventListener(ApplicationContext context,
                                   @Value("${app.title}") String applicationTitle,
                                   @Value("${app.testlabel}") String labelText) {
        this.context = context;
        this.applicationTitle = applicationTitle;
        this.labelText = labelText;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        Stage stage = event.getStage();
        FxWeaver fxWeaver = context.getBean(FxWeaver.class);
        Parent root = fxWeaver.loadView(MainController.class);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle(this.applicationTitle);
        stage.setResizable(false);
        stage.show();
    }
}
