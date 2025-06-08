package ru.organaizersrv.uiorganizerapp.controller;

import javafx.collections.FXCollections;import javafx.collections.ObservableList;import javafx.fxml.FXML;import javafx.scene.control.*;import javafx.scene.layout.VBox;import javafx.stage.FileChooser;import ru.organaizersrv.uiorganizerapp.model.*;import ru.organaizersrv.uiorganizerapp.model.AttachmentType;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ru.organaizersrv.uiorganizerapp.service.DisciplineService;
import ru.organaizersrv.uiorganizerapp.service.MaterialService;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.nio.file.Files;

/**
 * Контроллер для пользовательского интерфейса органайзера учебных материалов
 */
@Component
@FxmlView("study_organizer.fxml")
public class OrganizerController {
    @FXML
    private TreeView<String> disciplineTree;

    @FXML
    private TextField nameSearchField;

    @FXML
    private TextField dateSearchField;
    
    @FXML
    private TextField tagSearchField;
    
    @FXML
    private TextField newDisciplineField;
    
    @FXML
    private TextArea materialDetails;
    
    @FXML
    private Label materialName;
    
    @FXML
    private Label materialDate;
    
    @FXML
    private Label materialTag;
    
    @FXML
    private ListView<Attachment> attachmentsList;
    
    @FXML
    private ComboBox<String> dataSourceComboBox;
    
    @FXML
    private ListView<Material> materialsList;
    
    private final DisciplineService disciplineService;
    private final MaterialService materialService;
    
    private Material currentMaterial;
    
    public OrganizerController(DisciplineService disciplineService, MaterialService materialService) {
        this.disciplineService = disciplineService;
        this.materialService = materialService;
    }
    
    @FXML
    public void initialize() {
        setupDataSourceComboBox();
        setupDisciplineTree();
        setupAttachmentsList();
        setupMaterialsList();
        
        // Настройка обработчика выбора дисциплины
        disciplineTree.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.getValue().equals("Дисциплины")) {
                loadMaterialsForDiscipline(newValue.getValue());
            }
        });
    }
    
    private void setupDataSourceComboBox() {
        dataSourceComboBox.getItems().addAll("Файловая система", "База данных", "API");
        
        // Устанавливаем значение по умолчанию в зависимости от текущего источника
        String currentSource = disciplineService.getCurrentDataSource();
        if (currentSource != null) {
            switch (currentSource) {
                case "file":
                    dataSourceComboBox.setValue("Файловая система");
                    break;
                case "db":
                    dataSourceComboBox.setValue("База данных");
                    break;
                case "api":
                    dataSourceComboBox.setValue("API");
                    break;
                default:
                    dataSourceComboBox.setValue("Файловая система");
            }
        } else {
            dataSourceComboBox.setValue("Файловая система");
        }
        
        // Обработчик изменения источника данных
        dataSourceComboBox.setOnAction(e -> {
            String selected = dataSourceComboBox.getValue();
            String sourceType = "file"; // По умолчанию
            
            switch (selected) {
                case "Файловая система":
                    sourceType = "file";
                    break;
                case "База данных":
                    sourceType = "db";
                    break;
                case "API":
                    sourceType = "api";
                    break;
            }
            
            // Изменяем источник данных в обоих сервисах
            disciplineService.changeDataSource(sourceType);
            materialService.changeDataSource(sourceType);
            
            // Обновляем отображение
            refreshDisciplineTree();
            clearMaterialDetails();
            materialDetails.setText("Источник данных изменен на: " + selected);
        });
    }
    
    private void setupDisciplineTree() {
        TreeItem<String> root = new TreeItem<>("Дисциплины");
        disciplineTree.setRoot(root);
        refreshDisciplineTree();
    }
    
    private void setupAttachmentsList() {
        attachmentsList.setCellFactory(lv -> new ListCell<Attachment>() {
            @Override
            protected void updateItem(Attachment item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? null : item.getName());
            }
        });
    }
    
    private void setupMaterialsList() {
        // Настраиваем отображение имени материала в списке
        materialsList.setCellFactory(lv -> new ListCell<Material>() {
            @Override
            protected void updateItem(Material item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getName());
                }
            }
        });
        
        // Настраиваем обработчик выбора материала
        materialsList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                displayMaterial(newValue);
            }
        });
    }
    
    private void loadDisciplines() {
        List<String> disciplines = disciplineService.getAllDisciplines();
        TreeItem<String> rootItem = new TreeItem<>("Дисциплины");
        rootItem.setExpanded(true);
        
        for (String discipline : disciplines) {
            TreeItem<String> disciplineItem = new TreeItem<>(discipline);
            rootItem.getChildren().add(disciplineItem);
        }
        
        disciplineTree.setRoot(rootItem);
        disciplineTree.setShowRoot(true);
    }
    
    private void loadMaterialsForDiscipline(String disciplineName) {
        List<Material> materials = materialService.getMaterialsByDiscipline(disciplineName);
        
        // Обновляем список материалов
        ObservableList<Material> materialObservableList = FXCollections.observableArrayList(materials);
        materialsList.setItems(materialObservableList);
        
        if (materials.isEmpty()) {
            clearMaterialDetails();
            materialDetails.setText("Нет материалов для дисциплины " + disciplineName);
        } else {
            // Выбираем первый материал автоматически
            materialsList.getSelectionModel().selectFirst();
            
            // Формируем общую информацию о количестве материалов
            StringBuilder materialsText = new StringBuilder();
            materialsText.append("Всего материалов по дисциплине \"")
                    .append(disciplineName)
                    .append("\": ")
                    .append(materials.size())
                    .append("\n\n");
            
            materialsText.append("Выберите материал из списка слева для просмотра деталей");
            
            materialDetails.setText(materialsText.toString());
        }
    }
    
    private void displayMaterial(Material material) {
        if (material == null) {
            clearMaterialDetails();
            return;
        }
        
        currentMaterial = material;
        materialName.setText("Название: " + material.getName());
        materialDate.setText("Дата: " + material.getDateAdded());
        materialTag.setText("Теги: " + material.getTags());
        
        // Отображаем вложения
        ObservableList<Attachment> attachments = FXCollections.observableArrayList(material.getAttachments());
        attachmentsList.setItems(attachments);
    }
    
    private void clearMaterialDetails() {
        currentMaterial = null;
        materialName.setText("");
        materialDate.setText("");
        materialTag.setText("");
        attachmentsList.setItems(FXCollections.observableArrayList());
    }
    
    @FXML
    private void searchMaterials() {
        String nameQuery = nameSearchField.getText().trim();
        String dateQuery = dateSearchField.getText().trim();
        String tagQuery = tagSearchField.getText().trim();
        
        List<Material> results = materialService.searchMaterials(nameQuery, dateQuery, tagQuery);
        
        // Обновляем список материалов результатами поиска
        ObservableList<Material> materialObservableList = FXCollections.observableArrayList(results);
        materialsList.setItems(materialObservableList);
        
        if (results.isEmpty()) {
            clearMaterialDetails();
            materialDetails.setText("Поиск не дал результатов");
        } else {
            // Выбираем первый найденный материал
            materialsList.getSelectionModel().selectFirst();
            
            // Формируем информацию о результатах поиска
            StringBuilder resultsText = new StringBuilder();
            resultsText.append("Результаты поиска: найдено ")
                    .append(results.size())
                    .append(" материалов")
                    .append("\n\n");
            
            resultsText.append("Поисковый запрос:\n");
            if (!nameQuery.isEmpty()) resultsText.append("- Название: ").append(nameQuery).append("\n");
            if (!dateQuery.isEmpty()) resultsText.append("- Дата: ").append(dateQuery).append("\n");
            if (!tagQuery.isEmpty()) resultsText.append("- Тег: ").append(tagQuery).append("\n");
            
            resultsText.append("\nВыберите материал из списка слева для просмотра деталей");
            
            materialDetails.setText(resultsText.toString());
        }
    }
    
    @FXML
    private void addDiscipline() {
        String newDiscipline = newDisciplineField.getText().trim();
        if (!newDiscipline.isEmpty()) {
            boolean added = disciplineService.addDiscipline(newDiscipline);
            if (added) {
                loadDisciplines();
                newDisciplineField.clear();
                materialDetails.setText("Дисциплина \"" + newDiscipline + "\" успешно добавлена");
            } else {
                materialDetails.setText("Не удалось добавить дисциплину \"" + newDiscipline + "\". Возможно, она уже существует.");
            }
        }
    }
    
    @FXML
    private void updateDiscipline() {
        TreeItem<String> selectedItem = disciplineTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.getValue().equals("Дисциплины")) {
            showAlert("Ошибка", "Не выбрана дисциплина", "Пожалуйста, выберите дисциплину для обновления.");
            return;
        }
        
        String oldDisciplineName = selectedItem.getValue();
        
        TextInputDialog dialog = new TextInputDialog(oldDisciplineName);
        dialog.setTitle("Обновить дисциплину");
        dialog.setHeaderText("Введите новое название для дисциплины \"" + oldDisciplineName + "\"");
        dialog.setContentText("Новое название:");
        
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent() && !result.get().trim().isEmpty()) {
            String newDisciplineName = result.get().trim();
            
            if (newDisciplineName.equals(oldDisciplineName)) {
                // Если имя не изменилось, ничего не делаем
                return;
            }
            
            boolean updated = disciplineService.updateDiscipline(oldDisciplineName, newDisciplineName);
            if (updated) {
                loadDisciplines();
                materialDetails.setText("Дисциплина \"" + oldDisciplineName + "\" успешно переименована в \"" + newDisciplineName + "\"");
            } else {
                showAlert("Ошибка", "Не удалось обновить дисциплину", 
                         "Возможно, дисциплина с таким названием уже существует или произошла другая ошибка.");
            }
        }
    }
    
    @FXML
    private void attachFile() {
        if (currentMaterial == null) {
            showAlert("Ошибка", "Не выбран материал", "Пожалуйста, выберите материал для прикрепления файла.");
            return;
        }
        
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf"),
                new FileChooser.ExtensionFilter("Microsoft Word", "*.doc", "*.docx"),
                new FileChooser.ExtensionFilter("Microsoft PowerPoint", "*.ppt", "*.pptx"),
                new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        
        File selectedFile = fileChooser.showOpenDialog(null);
        
        if (selectedFile != null) {
            try {
                // Копируем файл в директорию ресурсов
                String resourceDir = "src/main/resources/attachments";
                File resourcesDir = new File(resourceDir);
                if (!resourcesDir.exists()) {
                    resourcesDir.mkdirs();
                }
                
                String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(resourcesDir, newFileName);
                
                Files.copy(selectedFile.toPath(), destFile.toPath(), 
                           java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                
                System.out.println("Файл скопирован в: " + destFile.getAbsolutePath());
                
                // Прикрепляем скопированный файл
                Material updatedMaterial = materialService.attachFile(currentMaterial.getId(), destFile);
                if (updatedMaterial != null) {
                    displayMaterial(updatedMaterial);
                    materialDetails.setText("Файл \"" + selectedFile.getName() + "\" успешно прикреплен");
                } else {
                    showAlert("Ошибка", "Не удалось прикрепить файл", "Произошла ошибка при прикреплении файла.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Ошибка", "Не удалось скопировать файл", 
                         "Произошла ошибка при копировании файла: " + e.getMessage());
            }
        }
    }
    
    @FXML
    private void attachLink() {
        if (currentMaterial == null) {
            showAlert("Ошибка", "Не выбран материал", "Пожалуйста, выберите материал для прикрепления ссылки.");
            return;
        }
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Прикрепить ссылку");
        dialog.setHeaderText("Введите название и URL ссылки");
        dialog.setContentText("Название:");
        
        Optional<String> nameResult = dialog.showAndWait();
        if (nameResult.isPresent() && !nameResult.get().trim().isEmpty()) {
            String linkName = nameResult.get().trim();
            
            dialog = new TextInputDialog("http://");
            dialog.setTitle("Прикрепить ссылку");
            dialog.setHeaderText("Введите URL ссылки");
            dialog.setContentText("URL:");
            
            Optional<String> urlResult = dialog.showAndWait();
            if (urlResult.isPresent() && !urlResult.get().trim().isEmpty()) {
                String linkUrl = urlResult.get().trim();
                
                Material updatedMaterial = materialService.attachLink(currentMaterial.getId(), linkName, linkUrl);
                if (updatedMaterial != null) {
                    displayMaterial(updatedMaterial);
                    materialDetails.setText("Ссылка \"" + linkName + "\" успешно прикреплена");
                } else {
                    showAlert("Ошибка", "Не удалось прикрепить ссылку", "Произошла ошибка при прикреплении ссылки.");
                }
            }
        }
    }
    
    @FXML
    private void addMaterial() {
        TreeItem<String> selectedItem = disciplineTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.getValue().equals("Дисциплины")) {
            showAlert("Ошибка", "Не выбрана дисциплина", "Пожалуйста, выберите дисциплину для добавления материала.");
            return;
        }
        
        String disciplineName = selectedItem.getValue();
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Добавить материал");
        dialog.setHeaderText("Добавление нового материала для дисциплины \"" + disciplineName + "\"");
        dialog.setContentText("Название материала:");
        
        Optional<String> nameResult = dialog.showAndWait();
        if (nameResult.isPresent() && !nameResult.get().trim().isEmpty()) {
            String materialName = nameResult.get().trim();
            
            dialog = new TextInputDialog();
            dialog.setTitle("Добавить материал");
            dialog.setHeaderText("Введите теги для материала (через запятую)");
            dialog.setContentText("Теги:");
            
            Optional<String> tagsResult = dialog.showAndWait();
            String tags = tagsResult.map(String::trim).orElse("");
            
            // Создаем материал
            Material newMaterial = materialService.addMaterial(materialName, tags, disciplineName);
            
            if (newMaterial != null) {
                // Предлагаем прикрепить файл
                Alert filePrompt = new Alert(Alert.AlertType.CONFIRMATION);
                filePrompt.setTitle("Прикрепить файл");
                filePrompt.setHeaderText("Хотите прикрепить файл к материалу?");
                filePrompt.setContentText("Нажмите 'OK' чтобы выбрать файл для прикрепления");
                
                Optional<ButtonType> result = filePrompt.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    // Выбираем файл
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Выберите файл для материала");
                    fileChooser.getExtensionFilters().addAll(
                            new FileChooser.ExtensionFilter("Все поддерживаемые форматы", "*.pdf", "*.doc", "*.docx", "*.ppt", "*.pptx", "*.txt", "*.zip"),
                            new FileChooser.ExtensionFilter("PDF файлы", "*.pdf"),
                            new FileChooser.ExtensionFilter("Microsoft Word", "*.doc", "*.docx"),
                            new FileChooser.ExtensionFilter("Microsoft PowerPoint", "*.ppt", "*.pptx"),
                            new FileChooser.ExtensionFilter("Текстовые файлы", "*.txt"),
                            new FileChooser.ExtensionFilter("Архивы", "*.zip"),
                            new FileChooser.ExtensionFilter("Все файлы", "*.*")
                    );
                    
                    File selectedFile = fileChooser.showOpenDialog(null);
                    if (selectedFile != null) {
                        // Копируем файл в директорию ресурсов
                        try {
                            String resourceDir = "src/main/resources/attachments";
                            File resourcesDir = new File(resourceDir);
                            if (!resourcesDir.exists()) {
                                resourcesDir.mkdirs();
                            }
                            
                            String newFileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                            File destFile = new File(resourcesDir, newFileName);
                            
                            Files.copy(selectedFile.toPath(), destFile.toPath(), 
                                      java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                            
                            System.out.println("Файл скопирован в: " + destFile.getAbsolutePath());
                            
                            // Прикрепляем файл к материалу
                            currentMaterial = materialService.attachFile(newMaterial.getId(), destFile);
                            if (currentMaterial != null) {
                                materialDetails.setText("Материал \"" + materialName + "\" успешно добавлен с файлом: " + selectedFile.getName());
                            } else {
                                materialDetails.setText("Материал \"" + materialName + "\" добавлен, но прикрепить файл не удалось");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            materialDetails.setText("Материал \"" + materialName + "\" добавлен, но при копировании файла произошла ошибка: " + e.getMessage());
                        }
                    } else {
                        materialDetails.setText("Материал \"" + materialName + "\" успешно добавлен (файл не выбран)");
                    }
                } else {
                    materialDetails.setText("Материал \"" + materialName + "\" успешно добавлен");
                }
                
                // Обновляем интерфейс
                loadMaterialsForDiscipline(disciplineName);
                displayMaterial(newMaterial);
            } else {
                showAlert("Ошибка", "Не удалось добавить материал", "Произошла ошибка при добавлении материала.");
            }
        }
    }
    
    @FXML
    private void deleteFile() {
        Attachment selectedAttachment = attachmentsList.getSelectionModel().getSelectedItem();
        if (currentMaterial == null || selectedAttachment == null) {
            showAlert("Ошибка", "Не выбрано вложение", "Пожалуйста, выберите вложение для удаления.");
            return;
        }
        
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Подтверждение удаления");
        alert.setHeaderText("Удаление вложения");
        alert.setContentText("Вы уверены, что хотите удалить вложение \"" + selectedAttachment.getName() + "\"?");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Material updatedMaterial = materialService.removeAttachment(currentMaterial.getId(), selectedAttachment.getId());
            if (updatedMaterial != null) {
                displayMaterial(updatedMaterial);
                materialDetails.setText("Вложение \"" + selectedAttachment.getName() + "\" успешно удалено");
            } else {
                showAlert("Ошибка", "Не удалось удалить вложение", "Произошла ошибка при удалении вложения.");
            }
        }
    }
    
    @FXML
    private void downloadFile() {
        openSelectedAttachment();
    }
    
    private void openSelectedAttachment() {
        Attachment selectedAttachment = attachmentsList.getSelectionModel().getSelectedItem();
        if (selectedAttachment == null) {
            showAlert("Ошибка", "Не выбрано вложение", "Пожалуйста, выберите вложение для открытия.");
            return;
        }
        
        try {
            if (selectedAttachment.getType() == AttachmentType.LINK) {
                // Для ссылок показываем URL в сообщении
                showAlert("Ссылка", selectedAttachment.getName(), "URL: " + selectedAttachment.getPath());
                
                // Пытаемся открыть ссылку в браузере
                try {
                    java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                    if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                        desktop.browse(new URI(selectedAttachment.getPath()));
                    }
                } catch (Exception e) {
                    System.out.println("Не удалось открыть ссылку в браузере: " + e.getMessage());
                    // Ошибку не показываем пользователю, так как URL уже отображен
                }
            } else {
                // Для файлов пытаемся открыть их
                File file = new File(selectedAttachment.getPath());
                
                // Если файл не существует по указанному пути, проверяем его в classpath ресурсах
                if (!file.exists()) {
                    // Пробуем получить файл из resources
                    String resourcePath = "attachments/" + file.getName();
                    file = new File(getClass().getClassLoader().getResource(resourcePath).getFile());
                }
                
                if (file.exists()) {
                    // Показываем информацию о файле
                    showAlert("Информация", "Открытие файла: " + selectedAttachment.getName(), 
                            "Файл будет открыт в соответствующем приложении");
                    
                    // Открываем файл
                    try {
                        ProcessBuilder pb = new ProcessBuilder();
                        pb.command("cmd.exe", "/c", file.getAbsolutePath());
                        pb.start();
                    } catch (Exception e) {
                        System.err.println("Ошибка при открытии файла через процесс: " + e.getMessage());
                        e.printStackTrace();
                        
                        // Пробуем через Desktop API
                        try {
                            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                            if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                                desktop.open(file);
                            }
                        } catch (Exception ex) {
                            System.err.println("Не удалось открыть файл через Desktop API: " + ex.getMessage());
                            ex.printStackTrace();
                            throw ex;  // перебрасываем исключение для показа пользователю
                        }
                    }
                } else {
                    showAlert("Ошибка", "Файл не найден", "Файл \"" + selectedAttachment.getPath() + "\" не существует.");
                }
            }
            
            // Добавляем информацию в materialDetails
            materialDetails.appendText("\n\nПросмотр вложения: " + selectedAttachment.getName());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть вложение", "Произошла ошибка при открытии вложения: " + e.getMessage());
        }
    }
    
    private void refreshDisciplineTree() {
        TreeItem<String> root = disciplineTree.getRoot();
        root.getChildren().clear();
        
        List<String> disciplines = disciplineService.getAllDisciplines();
        for (String disciplineName : disciplines) {
            TreeItem<String> item = new TreeItem<>(disciplineName);
            root.getChildren().add(item);
        }
    }
    
    private void showAlert(String title, String header, String content) {
        Alert.AlertType type = Alert.AlertType.ERROR;
        
        // Определяем тип оповещения в зависимости от заголовка
        if (title.equals("Информация") || title.equals("Ссылка")) {
            type = Alert.AlertType.INFORMATION;
        } else if (title.equals("Подтверждение")) {
            type = Alert.AlertType.CONFIRMATION;
        } else if (title.equals("Предупреждение")) {
            type = Alert.AlertType.WARNING;
        }
        
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}