package ru.organaizersrv.uiorganizerapp.util;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import ru.organaizersrv.uiorganizerapp.model.Attachment;
import ru.organaizersrv.uiorganizerapp.model.AttachmentType;
import ru.organaizersrv.uiorganizerapp.model.Material;
import ru.organaizersrv.uiorganizerapp.service.DisciplineService;
import ru.organaizersrv.uiorganizerapp.service.MaterialService;

/**
 * Генератор тестовых данных для приложения
 */
public class TestDataGenerator {

    private final DisciplineService disciplineService;
    private final MaterialService materialService;
    private final String dataDir = "src/main/resources/attachments";
    
    public TestDataGenerator(DisciplineService disciplineService, MaterialService materialService) {
        this.disciplineService = disciplineService;
        this.materialService = materialService;
    }

    /**
     * Генерирует тестовые данные
     */
    public void generateTestData() {
        try {
            createDataDirectory();
            
            List<String> disciplines = generateDisciplines();
            generateMaterialsAndFiles(disciplines);
            
            System.out.println("Тестовые данные успешно сгенерированы");
        } catch (Exception e) {
            System.err.println("Ошибка при генерации тестовых данных: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Создает директорию для тестовых данных
     */
    private void createDataDirectory() throws Exception {
        Path dir = Paths.get(dataDir);
        if (!Files.exists(dir)) {
            Files.createDirectories(dir);
            System.out.println("Создана директория для тестовых данных: " + dir.toAbsolutePath());
        }
    }

    /**
     * Генерирует список дисциплин
     */
    private List<String> generateDisciplines() {
        List<String> disciplines = new ArrayList<>();
        
        // Создаем 5 дисциплин
        disciplines.add("Математика");
        disciplines.add("Физика");
        disciplines.add("Информатика");
        disciplines.add("История");
        disciplines.add("Английский язык");
        
        // Добавляем дисциплины в сервис
        for (String discipline : disciplines) {
            disciplineService.addDiscipline(discipline);
            System.out.println("Добавлена дисциплина: " + discipline);
        }
        
        return disciplines;
    }

    /**
     * Генерирует материалы и файлы для дисциплин
     */
    private void generateMaterialsAndFiles(List<String> disciplines) throws Exception {
        for (int i = 0; i < disciplines.size(); i++) {
            String discipline = disciplines.get(i);
            
            // Создаем материал для дисциплины
            Material material = new Material(
                    "Материал по " + discipline, 
                    LocalDate.now(), 
                    "тема" + (i + 1),
                    discipline // disciplineName параметр
            );
            
            // Создаем PDF файл
            String fileName = discipline.toLowerCase().replace(" ", "_") + "_материал.pdf";
            String filePath = createPdfFile(fileName, discipline);
            
            // Добавляем вложение к материалу
            Attachment attachment = new Attachment();
            attachment.setName(fileName);
            attachment.setPath(filePath);
            attachment.setType(AttachmentType.PDF);
            
            List<Attachment> attachments = new ArrayList<>();
            attachments.add(attachment);
            material.setAttachments(attachments);
            
            // Сохраняем материал
            materialService.addMaterial(material.getName(), material.getTags(), material.getDisciplineName());
            System.out.println("Создан материал для дисциплины " + discipline + " с PDF файлом: " + fileName);
        }
    }

    /**
     * Транслитерирует русский текст в латиницу
     */
    private String transliterate(String text) {
        Map<Character, String> cyrillicToLatin = new HashMap<>();
        
        // Русские буквы в латиницу
        cyrillicToLatin.put('а', "a");
        cyrillicToLatin.put('б', "b");
        cyrillicToLatin.put('в', "v");
        cyrillicToLatin.put('г', "g");
        cyrillicToLatin.put('д', "d");
        cyrillicToLatin.put('е', "e");
        cyrillicToLatin.put('ё', "yo");
        cyrillicToLatin.put('ж', "zh");
        cyrillicToLatin.put('з', "z");
        cyrillicToLatin.put('и', "i");
        cyrillicToLatin.put('й', "y");
        cyrillicToLatin.put('к', "k");
        cyrillicToLatin.put('л', "l");
        cyrillicToLatin.put('м', "m");
        cyrillicToLatin.put('н', "n");
        cyrillicToLatin.put('о', "o");
        cyrillicToLatin.put('п', "p");
        cyrillicToLatin.put('р', "r");
        cyrillicToLatin.put('с', "s");
        cyrillicToLatin.put('т', "t");
        cyrillicToLatin.put('у', "u");
        cyrillicToLatin.put('ф', "f");
        cyrillicToLatin.put('х', "kh");
        cyrillicToLatin.put('ц', "ts");
        cyrillicToLatin.put('ч', "ch");
        cyrillicToLatin.put('ш', "sh");
        cyrillicToLatin.put('щ', "sch");
        cyrillicToLatin.put('ъ', "");
        cyrillicToLatin.put('ы', "y");
        cyrillicToLatin.put('ь', "");
        cyrillicToLatin.put('э', "e");
        cyrillicToLatin.put('ю', "yu");
        cyrillicToLatin.put('я', "ya");
        
        // Прописные буквы
        cyrillicToLatin.put('А', "A");
        cyrillicToLatin.put('Б', "B");
        cyrillicToLatin.put('В', "V");
        cyrillicToLatin.put('Г', "G");
        cyrillicToLatin.put('Д', "D");
        cyrillicToLatin.put('Е', "E");
        cyrillicToLatin.put('Ё', "Yo");
        cyrillicToLatin.put('Ж', "Zh");
        cyrillicToLatin.put('З', "Z");
        cyrillicToLatin.put('И', "I");
        cyrillicToLatin.put('Й', "Y");
        cyrillicToLatin.put('К', "K");
        cyrillicToLatin.put('Л', "L");
        cyrillicToLatin.put('М', "M");
        cyrillicToLatin.put('Н', "N");
        cyrillicToLatin.put('О', "O");
        cyrillicToLatin.put('П', "P");
        cyrillicToLatin.put('Р', "R");
        cyrillicToLatin.put('С', "S");
        cyrillicToLatin.put('Т', "T");
        cyrillicToLatin.put('У', "U");
        cyrillicToLatin.put('Ф', "F");
        cyrillicToLatin.put('Х', "Kh");
        cyrillicToLatin.put('Ц', "Ts");
        cyrillicToLatin.put('Ч', "Ch");
        cyrillicToLatin.put('Ш', "Sh");
        cyrillicToLatin.put('Щ', "Sch");
        cyrillicToLatin.put('Ъ', "");
        cyrillicToLatin.put('Ы', "Y");
        cyrillicToLatin.put('Ь', "");
        cyrillicToLatin.put('Э', "E");
        cyrillicToLatin.put('Ю', "Yu");
        cyrillicToLatin.put('Я', "Ya");
        
        StringBuilder result = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (cyrillicToLatin.containsKey(c)) {
                result.append(cyrillicToLatin.get(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }

    /**
     * Создает PDF файл с заданным именем и содержимым
     */
    private String createPdfFile(String fileName, String discipline) throws Exception {
        String filePath = dataDir + File.separator + fileName;
        
        // Создаем PDF документ
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        
        // Транслитерируем название дисциплины для совместимости с PDType1Font
        String transliteratedDiscipline = transliterate(discipline);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(100, 700);
            contentStream.showText("Study Material: " + transliteratedDiscipline);
            
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(0, -30);
            contentStream.showText("Content Summary:");
            
            contentStream.newLineAtOffset(0, -20);
            
            // Добавляем разное содержимое в зависимости от дисциплины (используя транслитерацию)
            switch (discipline) {
                case "Математика":
                    contentStream.showText("1. Introduction to Mathematical Analysis");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("2. Differential Calculus");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("3. Integral Calculus");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("4. Series and Sequences");
                    break;
                    
                case "Физика":
                    contentStream.showText("1. Mechanics");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("2. Electromagnetism");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("3. Optics");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("4. Quantum Physics");
                    break;
                    
                case "Информатика":
                    contentStream.showText("1. Algorithms and Data Structures");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("2. Object-Oriented Programming");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("3. Databases");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("4. Computer Networks");
                    break;
                    
                case "История":
                    contentStream.showText("1. Ancient History");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("2. Medieval Times");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("3. Modern Era");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("4. Contemporary History");
                    break;
                    
                case "Английский язык":
                    contentStream.showText("1. Grammar");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("2. Vocabulary");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("3. Speaking Practice");
                    contentStream.newLineAtOffset(0, -15);
                    contentStream.showText("4. Writing");
                    break;
                    
                default:
                    contentStream.showText("Main topics of the discipline");
            }
            
            contentStream.newLineAtOffset(0, -40);
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 11);
            contentStream.showText("This file was created automatically for testing purposes.");
            contentStream.newLineAtOffset(0, -15);
            contentStream.showText("Created on: " + LocalDate.now());
            
            contentStream.endText();
        }
        
        // Сохраняем документ и закрываем его, чтобы избежать утечки ресурсов
        document.save(filePath);
        document.close();
        
        return filePath;
    }
} 