package ru.organaizersrv.uiorganizerapp;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.context.ConfigurableApplicationContext;
import ru.organaizersrv.uiorganizerapp.event.StageReadyEvent;

import java.io.IOException;

public class OrganizerApplication extends Application {

    private ConfigurableApplicationContext applicationContext;
    private String[] args;

    @Override
    public void init() {
        args = getParameters().getRaw().toArray(new String[0]);
        applicationContext = UiOrganizerAppApplication.createSpringApplicationBuilder()
                .run(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        applicationContext.publishEvent(new StageReadyEvent(primaryStage));
    }

    @Override
    public void stop() {
        applicationContext.close();
        Platform.exit();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}