package ru.organaizersrv.uiorganizerapp;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.organaizersrv.uiorganizerapp"})
public class UiOrganizerAppApplication {
    // Этот класс используется только как контейнер конфигурации для Spring Boot
    // Фактический запуск происходит через OrganizerApplication или OrganizerLauncher
    
    // Метод для создания SpringApplicationBuilder
    public static SpringApplicationBuilder createSpringApplicationBuilder() {
        return new SpringApplicationBuilder(UiOrganizerAppApplication.class)
                .web(WebApplicationType.NONE);
    }
}