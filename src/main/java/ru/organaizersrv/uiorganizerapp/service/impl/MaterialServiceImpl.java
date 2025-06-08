package ru.organaizersrv.uiorganizerapp.service.impl;

import org.springframework.stereotype.Service;
import ru.organaizersrv.uiorganizerapp.dao.MaterialDAO;
import ru.organaizersrv.uiorganizerapp.dao.impl.ApiMaterialDAO;
import ru.organaizersrv.uiorganizerapp.dao.impl.DbMaterialDAO;
import ru.organaizersrv.uiorganizerapp.dao.impl.FileMaterialDAO;
import ru.organaizersrv.uiorganizerapp.model.Attachment;
import ru.organaizersrv.uiorganizerapp.model.AttachmentType;
import ru.organaizersrv.uiorganizerapp.model.Material;
import ru.organaizersrv.uiorganizerapp.service.MaterialService;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса для работы с учебными материалами
 */
public class MaterialServiceImpl implements MaterialService {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    
    private MaterialDAO materialDAO;
    private String currentDataSource; // "file", "db", "api"

    public MaterialServiceImpl(MaterialDAO materialDAO) {
        this.materialDAO = materialDAO;
        // По умолчанию используем файловое хранилище, можно изменить на "db" или "api"
        // this.currentDataSource = "file"; // Или другой источник по умолчанию
        // Для Spring Boot лучше управлять этим через конфигурацию или установить явно
        this.currentDataSource = "db"; // Установим "db" как источник по умолчанию для примера
    }
    
    @Override
    public List<Material> getAllMaterials() {
        return materialDAO.getAllMaterials();
    }
    
    @Override
    public List<Material> getMaterialsByDiscipline(String disciplineName) {
        if (disciplineName == null || disciplineName.isEmpty()) {
            return new ArrayList<>();
        }
        return materialDAO.getMaterialsByDiscipline(disciplineName);
    }
    
    @Override
    public List<Material> searchByName(String name) {
        if (name == null || name.isEmpty()) {
            return getAllMaterials();
        }
        return materialDAO.searchByName(name);
    }
    
    @Override
    public List<Material> searchByDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return getAllMaterials();
        }
        
        try {
            LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
            return materialDAO.searchByDate(date);
        } catch (DateTimeParseException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public List<Material> searchByTag(String tag) {
        if (tag == null || tag.isEmpty()) {
            return getAllMaterials();
        }
        return materialDAO.searchByTag(tag);
    }
    
    @Override
    public List<Material> searchMaterials(String name, String dateStr, String tag) {
        // Если все параметры пустые, возвращаем все материалы
        if ((name == null || name.isEmpty()) && 
            (dateStr == null || dateStr.isEmpty()) && 
            (tag == null || tag.isEmpty())) {
            return getAllMaterials();
        }
        
        List<Material> allMaterials = getAllMaterials();
        List<Material> result = new ArrayList<>(allMaterials);
        
        // Фильтруем по имени
        if (name != null && !name.isEmpty()) {
            result = result.stream()
                    .filter(m -> m.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        // Фильтруем по дате
        if (dateStr != null && !dateStr.isEmpty()) {
            try {
                LocalDate date = LocalDate.parse(dateStr, DATE_FORMATTER);
                result = result.stream()
                        .filter(m -> m.getDateAddedRaw().equals(date))
                        .collect(Collectors.toList());
            } catch (DateTimeParseException e) {
                e.printStackTrace();
            }
        }
        
        // Фильтруем по тегу
        if (tag != null && !tag.isEmpty()) {
            result = result.stream()
                    .filter(m -> m.getTags().toLowerCase().contains(tag.toLowerCase()))
                    .collect(Collectors.toList());
        }
        
        return result;
    }
    
    @Override
    public Material addMaterial(String name, String tags, String disciplineName) {
        if (name == null || name.isEmpty() || disciplineName == null || disciplineName.isEmpty()) {
            return null;
        }
        
        Material material = new Material(name, LocalDate.now(), tags, disciplineName);
        return materialDAO.addMaterial(material);
    }
    
    @Override
    public Material updateMaterial(Material material) {
        if (material == null || material.getId() == null) {
            return null;
        }
        return materialDAO.updateMaterial(material);
    }
    
    @Override
    public boolean deleteMaterial(Long id) {
        if (id == null) {
            return false;
        }
        return materialDAO.deleteMaterial(id);
    }
    
    @Override
    public Material getMaterialById(Long id) {
        if (id == null) {
            return null;
        }
        return materialDAO.getMaterialById(id);
    }
    
    @Override
    public Material attachFile(Long materialId, File file) {
        if (materialId == null || file == null || !file.exists()) {
            return null;
        }
        
        Material material = getMaterialById(materialId);
        if (material == null) {
            return null;
        }
        
        // Определяем тип вложения
        AttachmentType type = AttachmentType.OTHER;
        if (file.getName().toLowerCase().endsWith(".pdf")) {
            type = AttachmentType.PDF;
        }
        
        // Сохраняем файл и создаем вложение
        String path = null;
        if (materialDAO instanceof FileMaterialDAO) {
            path = ((FileMaterialDAO) materialDAO).saveAttachment(file, materialId.toString());
        } else {
            // Для других типов DAO просто сохраняем путь к файлу
            path = file.getAbsolutePath();
        }
        
        if (path != null) {
            Attachment attachment = new Attachment(file.getName(), path, type);
            attachment.setMaterialId(materialId);
            material.addAttachment(attachment);
            return updateMaterial(material);
        }
        
        return null;
    }
    
    @Override
    public Material attachLink(Long materialId, String linkName, String linkUrl) {
        if (materialId == null || linkName == null || linkName.isEmpty() || 
            linkUrl == null || linkUrl.isEmpty()) {
            return null;
        }
        
        Material material = getMaterialById(materialId);
        if (material == null) {
            return null;
        }
        
        // Создаем вложение типа LINK
        Attachment attachment = new Attachment(linkName, linkUrl, AttachmentType.LINK);
        attachment.setMaterialId(materialId);
        material.addAttachment(attachment);
        
        return updateMaterial(material);
    }
    
    @Override
    public Material removeAttachment(Long materialId, Long attachmentId) {
        if (materialId == null || attachmentId == null) {
            return null;
        }
        
        Material material = getMaterialById(materialId);
        if (material == null) {
            return null;
        }
        
        // Находим и удаляем вложение
        material.getAttachments().removeIf(a -> a.getId() != null && a.getId().equals(attachmentId));
        
        return updateMaterial(material);
    }
    
    @Override
    public Attachment getAttachmentById(Long attachmentId) {
        if (attachmentId == null) {
            return null;
        }
        
        // Ищем вложение во всех материалах
        for (Material material : getAllMaterials()) {
            for (Attachment attachment : material.getAttachments()) {
                if (attachment.getId() != null && attachment.getId().equals(attachmentId)) {
                    return attachment;
                }
            }
        }
        
        return null;
    }
    
    @Override
    public void changeDataSource(String sourceType) {
        if (sourceType == null || sourceType.isEmpty()) {
            return;
        }
        
        // Если текущий источник данных совпадает с запрошенным, ничего не делаем
        if (sourceType.equals(currentDataSource)) {
            return;
        }
        
        switch (sourceType.toLowerCase()) {
            case "file":
                materialDAO = new FileMaterialDAO();
                currentDataSource = "file";
                break;
            case "db":
                materialDAO = new DbMaterialDAO();
                currentDataSource = "db";
                break;
            case "api":
                materialDAO = new ApiMaterialDAO();
                currentDataSource = "api";
                break;
            default:
                // Если тип не распознан, используем файловое хранилище
                materialDAO = new FileMaterialDAO();
                currentDataSource = "file";
                break;
        }
    }
    
    @Override
    public String getCurrentDataSource() {
        return currentDataSource;
    }
}