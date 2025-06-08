package ru.organaizersrv.uiorganizerapp.service;

import ru.organaizersrv.uiorganizerapp.model.Attachment;
import ru.organaizersrv.uiorganizerapp.model.Material;

import java.io.File;
import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для работы с учебными материалами
 */
public interface MaterialService {
    /**
     * Получить все материалы
     * @return список всех материалов
     */
    List<Material> getAllMaterials();
    
    /**
     * Получить материалы по дисциплине
     * @param disciplineName название дисциплины
     * @return список материалов по указанной дисциплине
     */
    List<Material> getMaterialsByDiscipline(String disciplineName);
    
    /**
     * Поиск материалов по названию
     * @param name часть названия для поиска
     * @return список найденных материалов
     */
    List<Material> searchByName(String name);
    
    /**
     * Поиск материалов по дате добавления
     * @param dateStr строка с датой в формате dd.MM.yyyy
     * @return список найденных материалов
     */
    List<Material> searchByDate(String dateStr);
    
    /**
     * Поиск материалов по тегу
     * @param tag тег для поиска
     * @return список найденных материалов
     */
    List<Material> searchByTag(String tag);
    
    /**
     * Комплексный поиск материалов по нескольким критериям
     * @param name часть названия для поиска (может быть пустым)
     * @param dateStr строка с датой в формате dd.MM.yyyy (может быть пустым)
     * @param tag тег для поиска (может быть пустым)
     * @return список найденных материалов
     */
    List<Material> searchMaterials(String name, String dateStr, String tag);
    
    /**
     * Добавить новый материал
     * @param name название материала
     * @param tags теги материала
     * @param disciplineName название дисциплины
     * @return добавленный материал
     */
    Material addMaterial(String name, String tags, String disciplineName);
    
    /**
     * Обновить существующий материал
     * @param material материал для обновления
     * @return обновленный материал
     */
    Material updateMaterial(Material material);
    
    /**
     * Удалить материал по ID
     * @param id идентификатор материала
     * @return true если удаление успешно, иначе false
     */
    boolean deleteMaterial(Long id);
    
    /**
     * Получить материал по ID
     * @param id идентификатор материала
     * @return найденный материал или null
     */
    Material getMaterialById(Long id);
    
    /**
     * Прикрепить файл к материалу
     * @param materialId идентификатор материала
     * @param file файл для прикрепления
     * @return обновленный материал с прикрепленным файлом
     */
    Material attachFile(Long materialId, File file);
    
    /**
     * Прикрепить ссылку к материалу
     * @param materialId идентификатор материала
     * @param linkName название ссылки
     * @param linkUrl URL ссылки
     * @return обновленный материал с прикрепленной ссылкой
     */
    Material attachLink(Long materialId, String linkName, String linkUrl);
    
    /**
     * Удалить вложение из материала
     * @param materialId идентификатор материала
     * @param attachmentId идентификатор вложения
     * @return обновленный материал без удаленного вложения
     */
    Material removeAttachment(Long materialId, Long attachmentId);
    
    /**
     * Получить вложение по ID
     * @param attachmentId идентификатор вложения
     * @return найденное вложение или null
     */
    Attachment getAttachmentById(Long attachmentId);
    
    /**
     * Изменить источник данных
     * @param sourceType тип источника данных ("file", "db", "api")
     */
    void changeDataSource(String sourceType);
    
    /**
     * Получить текущий тип источника данных
     * @return тип источника данных ("file", "db", "api")
     */
    String getCurrentDataSource();
}