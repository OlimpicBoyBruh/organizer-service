package ru.organaizersrv.uiorganizerapp.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.organaizersrv.uiorganizerapp.service.DisciplineService;
import ru.organaizersrv.uiorganizerapp.service.MaterialService;
import ru.organaizersrv.uiorganizerapp.util.TestDataGenerator;

/**
 * Инициализация данных при запуске приложения
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private final DisciplineService disciplineService;
    private final MaterialService materialService;

    @Autowired
    public DataInitializer(DisciplineService disciplineService, MaterialService materialService) {
        this.disciplineService = disciplineService;
        this.materialService = materialService;
    }

    @Override
    public void run(String... args) {
        // Всегда генерируем тестовые данные при запуске
        try {
            TestDataGenerator dataGenerator = new TestDataGenerator(disciplineService, materialService);
            dataGenerator.generateTestData();
            System.out.println("Тестовые данные успешно сгенерированы");
        } catch (Exception e) {
            System.err.println("Ошибка при генерации тестовых данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 