package ru.organaizersrv.uiorganizerapp;

import ru.organaizersrv.uiorganizerapp.model.Attachment;
import ru.organaizersrv.uiorganizerapp.model.AttachmentType;
import ru.organaizersrv.uiorganizerapp.model.Material;
import ru.organaizersrv.uiorganizerapp.service.DisciplineService;
import ru.organaizersrv.uiorganizerapp.service.MaterialService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Основной класс для создания тестовых данных приложения:
 * - создает 5 учебных дисциплин
 * - генерирует PDF файлы
 * - добавляет материалы в приложение
 */
@Component
public class CreateSampleDataApp implements ApplicationRunner {

    // Названия 5 учебных дисциплин
    private static final String[] DISCIPLINES = {
        "Программирование на Java",
        "Базы данных",
        "Алгоритмы и структуры данных",
        "Компьютерные сети",
        "Машинное обучение"
    };
    
    // Используем каталог пользователя для хранения файлов
    private static final String USER_HOME = System.getProperty("user.home");
    private static final String DATA_DIR = USER_HOME + File.separator + "organizer_data";
    private static final String PDF_DIR = DATA_DIR + File.separator + "pdfs";
    private static final String DISCIPLINES_FILE = DATA_DIR + File.separator + "disciplines.csv";
    
    private static final Random random = new Random();
    
    private DisciplineService disciplineService;
    private MaterialService materialService;

    // Конструктор для использования Spring Boot (будет автоматически внедрять зависимости)
    @Autowired
    public CreateSampleDataApp(DisciplineService disciplineService, MaterialService materialService) {
        this.disciplineService = disciplineService;
        this.materialService = materialService;
    }
    
    // Пустой конструктор для запуска как отдельное приложение
    public CreateSampleDataApp() {
    }

    // Метод для запуска через Spring Boot
    @Override
    public void run(ApplicationArguments args) {
        // Проверяем аргумент командной строки для генерации данных
        if (args.containsOption("generate-data") && 
            args.getOptionValues("generate-data").contains("true")) {
            System.out.println("Запущен процесс генерации тестовых данных...");
            generateTestData();
        }
    }

    public static void main(String[] args) {
        try {
            CreateSampleDataApp app = new CreateSampleDataApp();
            
            // Создаем директории
            app.createDirectories();
            
            // Создаем файл дисциплин
            app.createDisciplinesFile();
            
            // Создаем PDF файлы
            List<File> pdfFiles = app.generateSamplePdfs();
            
            System.out.println("Данные успешно созданы в " + DATA_DIR);
            System.out.println("Теперь запустите приложение с помощью: mvn spring-boot:run или mvn javafx:run");
            
        } catch (Exception e) {
            System.err.println("Ошибка при создании данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Метод для использования внутри приложения - создает тестовые данные с использованием сервисов
     */
    public void generateTestData() {
        try {
            // Создаем директории
            createDirectories();
            
            // Создаем PDF файлы
            List<File> pdfFiles = generateSamplePdfs();
            
            if (pdfFiles.isEmpty()) {
                System.err.println("Не удалось создать PDF файлы. Генерация тестовых данных остановлена.");
                return;
            }
            
            // Создаем дисциплины в базе данных
            createDisciplinesInDb();
            
            // Добавляем материалы с прикрепленными PDF файлами
            createMaterialsWithAttachments(pdfFiles);
            
            System.out.println("Генерация тестовых данных успешно завершена!");
            
        } catch (Exception e) {
            System.err.println("Ошибка при создании тестовых данных: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createDirectories() throws IOException {
        // Создаем основную директорию для данных
        Path dataPath = Paths.get(DATA_DIR);
        if (!Files.exists(dataPath)) {
            Files.createDirectories(dataPath);
            System.out.println("Создана директория: " + dataPath);
        }
        
        // Создаем директорию для PDF файлов
        Path pdfPath = Paths.get(PDF_DIR);
        if (!Files.exists(pdfPath)) {
            Files.createDirectories(pdfPath);
            System.out.println("Создана директория: " + pdfPath);
        }
    }
    
    private void createDisciplinesFile() throws IOException {
        Path disciplinesPath = Paths.get(DISCIPLINES_FILE);
        List<String> lines = new ArrayList<>();
        
        for (String discipline : DISCIPLINES) {
            lines.add(discipline);
            System.out.println("Добавлена дисциплина: " + discipline);
        }
        
        Files.write(disciplinesPath, lines);
        System.out.println("Файл дисциплин сохранен: " + disciplinesPath);
    }
    
    /**
     * Создает дисциплины в базе данных приложения
     */
    private void createDisciplinesInDb() {
        for (String discipline : DISCIPLINES) {
            disciplineService.addDiscipline(discipline);
            System.out.println("Добавлена дисциплина в БД: " + discipline);
        }
    }
    
    /**
     * Генерирует набор PDF файлов для учебных материалов
     * @return список созданных файлов
     */
    private List<File> generateSamplePdfs() {
        try {
            // Создаем директорию для файлов, если её нет
            Path resourceDir = Paths.get(PDF_DIR);
            if (!Files.exists(resourceDir)) {
                Files.createDirectories(resourceDir);
            }
            
            System.out.println("Создаем PDF файлы в каталоге: " + resourceDir.toAbsolutePath());
            
            List<File> pdfFiles = new ArrayList<>();
            
            // PDF для Java
            pdfFiles.add(createPdf("java_lecture_1.pdf", "Лекция 1 - Введение в Java"));
            pdfFiles.add(createPdf("java_lecture_2.pdf", "Лекция 2 - Основы ООП"));
            pdfFiles.add(createPdf("java_practice_1.pdf", "Практика 1 - Базовые конструкции"));
            pdfFiles.add(createPdf("java_homework_1.pdf", "Домашнее задание 1"));
            pdfFiles.add(createPdf("java_exam.pdf", "Материалы для экзамена"));
            
            // PDF для баз данных
            pdfFiles.add(createPdf("db_lecture_1.pdf", "Лекция 1 - Введение в базы данных"));
            pdfFiles.add(createPdf("db_lecture_2.pdf", "Лекция 2 - SQL"));
            pdfFiles.add(createPdf("db_practice_1.pdf", "Практика 1 - Создание таблиц"));
            pdfFiles.add(createPdf("db_homework_1.pdf", "Лабораторная работа 1"));
            pdfFiles.add(createPdf("db_exam.pdf", "Вопросы к экзамену"));
            
            // PDF для алгоритмов
            pdfFiles.add(createPdf("alg_lecture_1.pdf", "Лекция 1 - Алгоритмическая сложность"));
            pdfFiles.add(createPdf("alg_lecture_2.pdf", "Лекция 2 - Сортировки"));
            pdfFiles.add(createPdf("alg_practice_1.pdf", "Практика 1 - Реализация алгоритмов"));
            pdfFiles.add(createPdf("alg_homework_1.pdf", "Задача на рекурсию"));
            pdfFiles.add(createPdf("alg_exam.pdf", "Материалы для подготовки к экзамену"));
            
            // PDF для сетей
            pdfFiles.add(createPdf("net_lecture_1.pdf", "Лекция 1 - Модель OSI"));
            pdfFiles.add(createPdf("net_lecture_2.pdf", "Лекция 2 - Протоколы TCP/IP"));
            pdfFiles.add(createPdf("net_practice_1.pdf", "Практика 1 - Настройка сети"));
            pdfFiles.add(createPdf("net_homework_1.pdf", "Задание по сетевым протоколам"));
            pdfFiles.add(createPdf("net_exam.pdf", "Билеты к экзамену"));
            
            // PDF для машинного обучения
            pdfFiles.add(createPdf("ml_lecture_1.pdf", "Лекция 1 - Введение в машинное обучение"));
            pdfFiles.add(createPdf("ml_lecture_2.pdf", "Лекция 2 - Линейная регрессия"));
            pdfFiles.add(createPdf("ml_practice_1.pdf", "Практика 1 - Обработка данных"));
            pdfFiles.add(createPdf("ml_homework_1.pdf", "Лабораторная работа по классификации"));
            pdfFiles.add(createPdf("ml_exam.pdf", "Вопросы к экзамену"));
            
            System.out.println("Создано " + pdfFiles.size() + " PDF файлов в " + resourceDir.toAbsolutePath());
            return pdfFiles;
            
        } catch (IOException e) {
            System.err.println("Ошибка при создании PDF файлов: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Создает простой PDF файл
     * @param fileName имя файла
     * @param content содержимое (будет отображаться как заголовок)
     * @return созданный файл
     */
    private File createPdf(String fileName, String content) throws IOException {
        File file = new File(PDF_DIR, fileName);
        
        System.out.println("Создаем файл: " + file.getAbsolutePath());
        
        // Очень простой PDF файл (заголовок с минимальной структурой PDF)
        String pdfContent = 
            "%PDF-1.4\n" +
            "1 0 obj\n" +
            "<< /Type /Catalog /Pages 2 0 R >>\n" +
            "endobj\n" +
            "2 0 obj\n" +
            "<< /Type /Pages /Kids [3 0 R] /Count 1 >>\n" +
            "endobj\n" +
            "3 0 obj\n" +
            "<< /Type /Page /Parent 2 0 R /Resources 4 0 R /MediaBox [0 0 595 842] /Contents 5 0 R >>\n" +
            "endobj\n" +
            "4 0 obj\n" +
            "<< /Font << /F1 6 0 R >> >>\n" +
            "endobj\n" +
            "5 0 obj\n" +
            "<< /Length 68 >>\n" +
            "stream\n" +
            "BT\n" +
            "/F1 24 Tf\n" +
            "100 700 Td\n" +
            "(" + content + ") Tj\n" +
            "ET\n" +
            "endstream\n" +
            "endobj\n" +
            "6 0 obj\n" +
            "<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>\n" +
            "endobj\n" +
            "xref\n" +
            "0 7\n" +
            "0000000000 65535 f\n" +
            "0000000010 00000 n\n" +
            "0000000059 00000 n\n" +
            "0000000118 00000 n\n" +
            "0000000217 00000 n\n" +
            "0000000262 00000 n\n" +
            "0000000380 00000 n\n" +
            "trailer\n" +
            "<< /Size 7 /Root 1 0 R >>\n" +
            "startxref\n" +
            "447\n" +
            "%%EOF";
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(pdfContent.getBytes());
        }
        
        return file;
    }
    
    private void createMaterialsWithAttachments(List<File> pdfFiles) {
        // Группируем PDF файлы по дисциплинам (на основе префикса файла)
        Map<String, List<File>> filesByDiscipline = new HashMap<>();
        
        // Проверяем, что у нас достаточно файлов
        if (pdfFiles.size() < 25) {
            System.err.println("Недостаточно PDF файлов: " + pdfFiles.size() + ". Ожидается не менее 25.");
            return;
        }
        
        try {
            filesByDiscipline.put("Программирование на Java", pdfFiles.subList(0, 5));
            filesByDiscipline.put("Базы данных", pdfFiles.subList(5, 10));
            filesByDiscipline.put("Алгоритмы и структуры данных", pdfFiles.subList(10, 15));
            filesByDiscipline.put("Компьютерные сети", pdfFiles.subList(15, 20));
            filesByDiscipline.put("Машинное обучение", pdfFiles.subList(20, 25));
            
            int totalMaterialsCreated = 0;
            
            // Для каждой дисциплины создаем материалы с вложениями
            for (Map.Entry<String, List<File>> entry : filesByDiscipline.entrySet()) {
                String discipline = entry.getKey();
                List<File> files = entry.getValue();
                
                System.out.println("Добавляем материалы для дисциплины: " + discipline);
                
                for (int i = 0; i < files.size(); i++) {
                    try {
                        File file = files.get(i);
                        
                        // Название и теги
                        String fileName = file.getName();
                        String materialName = fileName.substring(0, fileName.lastIndexOf('.')).replace('_', ' ');
                        
                        // Переводим первую букву в верхний регистр для красоты
                        materialName = materialName.substring(0, 1).toUpperCase() + materialName.substring(1);
                        
                        String tags = generateTags(discipline);
                        
                        // Случайная дата за последние 60 дней
                        LocalDate date = LocalDate.now().minusDays(random.nextInt(60));
                        
                        System.out.println("  Создаем материал: " + materialName);
                        
                        // Создаем материал
                        Material material = new Material(materialName, date, tags, discipline);
                        Material savedMaterial = materialService.addMaterial(material.getName(), material.getTags(), material.getDisciplineName());
                        
                        if (savedMaterial != null) {
                            System.out.println("  - Материал создан с ID: " + savedMaterial.getId());
                            
                            // Прикрепляем PDF файл
                            System.out.println("  - Прикрепляем файл: " + file.getAbsolutePath());
                            Material updatedMaterial = materialService.attachFile(savedMaterial.getId(), file);
                            
                            if (updatedMaterial != null) {
                                System.out.println("  - Файл успешно прикреплен");
                                totalMaterialsCreated++;
                                
                                // Добавляем ссылку для разнообразия
                                materialService.attachLink(savedMaterial.getId(), 
                                        "Дополнительные материалы к " + materialName, 
                                        "https://example.org/study/" + discipline.toLowerCase().replaceAll("\\s+", "-") + "/" + i);
                            } else {
                                System.err.println("  - Ошибка при прикреплении файла");
                            }
                        } else {
                            System.err.println("  - Ошибка при создании материала");
                        }
                    } catch (Exception e) {
                        System.err.println("Ошибка при обработке материала: " + e.getMessage());
                        e.printStackTrace();
                    }
                }
            }
            
            System.out.println("Успешно создано " + totalMaterialsCreated + " материалов с прикрепленными файлами");
        } catch (Exception e) {
            System.err.println("Ошибка при создании материалов: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String generateTags(String discipline) {
        String[] commonTags = {
                "важное", "экзамен", "зачет", "основы", "продвинутый уровень",
                "самостоятельная работа", "презентация", "лекция"
        };
        
        // Берем 1-3 случайных тега
        int tagCount = 1 + random.nextInt(3);
        StringBuilder tags = new StringBuilder();
        
        for (int i = 0; i < tagCount; i++) {
            if (i > 0) {
                tags.append(", ");
            }
            tags.append(commonTags[random.nextInt(commonTags.length)]);
        }
        
        return tags.toString();
    }
} 