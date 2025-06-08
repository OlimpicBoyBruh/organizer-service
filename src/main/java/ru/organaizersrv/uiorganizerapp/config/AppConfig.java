package ru.organaizersrv.uiorganizerapp.config;

import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.spring.SpringFxWeaver;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import ru.organaizersrv.uiorganizerapp.dao.DisciplineDAO;
import ru.organaizersrv.uiorganizerapp.dao.MaterialDAO;
import ru.organaizersrv.uiorganizerapp.dao.impl.FileDisciplineDAO;
import ru.organaizersrv.uiorganizerapp.dao.impl.FileMaterialDAO;
import ru.organaizersrv.uiorganizerapp.service.DisciplineService;
import ru.organaizersrv.uiorganizerapp.service.MaterialService;
import ru.organaizersrv.uiorganizerapp.service.impl.DisciplineServiceImpl;
import ru.organaizersrv.uiorganizerapp.service.impl.MaterialServiceImpl;

@Configuration
public class AppConfig {

    @Bean
    @Primary
    public DisciplineDAO disciplineDAO() {
        return new FileDisciplineDAO();
    }

    @Bean
    @Primary
    public MaterialDAO materialDAO() {
        return new FileMaterialDAO();
    }

    @Bean
    @Primary
    public DisciplineService disciplineService() {
        return new DisciplineServiceImpl(disciplineDAO());
    }

    @Bean
    @Primary
    public MaterialService materialService() {
        return new MaterialServiceImpl(materialDAO());
    }
    
    @Bean
    public FxWeaver fxWeaver(ConfigurableApplicationContext applicationContext) {
        return new SpringFxWeaver(applicationContext);
    }
} 