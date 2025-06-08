package ru.organaizersrv.uiorganizerapp;

import org.springframework.context.ConfigurableApplicationContext;
import ru.organaizersrv.uiorganizerapp.service.DisciplineService;
import ru.organaizersrv.uiorganizerapp.service.MaterialService;
import ru.organaizersrv.uiorganizerapp.util.TestDataGenerator;

/**
 * Класс для ручного запуска генерации тестовых данных
 */
public class TestDataGeneratorRunner {

    /**
     * Точка входа для генерации тестовых данных
     */
    public static void main(String[] args) {
        // Создаем Spring контекст
        ConfigurableApplicationContext context = UiOrganizerAppApplication.createSpringApplicationBuilder()
                .run(args);
        
        try {
            System.out.println("Начинаем генерацию тестовых данных...");
            
            // Получаем сервисы из контекста
            DisciplineService disciplineService = context.getBean(DisciplineService.class);
            MaterialService materialService = context.getBean(MaterialService.class);
            
            // Создаем генератор и запускаем генерацию данных
            TestDataGenerator generator = new TestDataGenerator(disciplineService, materialService);
            generator.generateTestData();
            
            System.out.println("Генерация тестовых данных завершена успешно!");
        } catch (Exception e) {
            System.err.println("Ошибка при генерации тестовых данных: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Закрываем контекст Spring
            context.close();
        }
    }
} 