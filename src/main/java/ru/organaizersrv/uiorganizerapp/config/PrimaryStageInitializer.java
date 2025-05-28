package ru.organaizersrv.uiorganizerapp.config;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import ru.organaizersrv.uiorganizerapp.controller.OrganizerController;
import ru.organaizersrv.uiorganizerapp.event.StageReadyEvent;

import java.io.IOException;

@Component
public class PrimaryStageInitializer implements ApplicationListener<StageReadyEvent> {

    private final FxWeaver fxWeaver;
    private final ApplicationContext applicationContext;

    @Autowired
    public PrimaryStageInitializer(FxWeaver fxWeaver, ApplicationContext applicationContext) {
        this.fxWeaver = fxWeaver;
        this.applicationContext = applicationContext;
    }

    @Override
    public void onApplicationEvent(StageReadyEvent event) {
        try {
            Stage stage = event.getStage();
            
            // Сначала пробуем загрузить через FxWeaver
            Parent root = fxWeaver.loadView(OrganizerController.class);
            
            // Если не получилось, загружаем напрямую через FXMLLoader
            if (root == null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ru/organaizersrv/uiorganizerapp/study_organizer.fxml"));
                
                // Устанавливаем контроллер из Spring контекста
                loader.setControllerFactory(applicationContext::getBean);
                
                root = loader.load();
            }
            
            Scene scene = new Scene(root, 800, 600);
            stage.setScene(scene);
            stage.setTitle("Органайзер учебных материалов");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}