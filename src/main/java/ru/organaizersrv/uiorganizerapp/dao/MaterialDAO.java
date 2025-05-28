package ru.organaizersrv.uiorganizerapp.dao;

import ru.organaizersrv.uiorganizerapp.model.Material;

import java.time.LocalDate;
import java.util.List;

/**
 * Интерфейс для доступа к данным материалов
 */
public interface MaterialDAO {
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
     * @param date дата добавления
     * @return список найденных материалов
     */
    List<Material> searchByDate(LocalDate date);
    
    /**
     * Поиск материалов по тегу
     * @param tag тег для поиска
     * @return список найденных материалов
     */
    List<Material> searchByTag(String tag);
    
    /**
     * Добавить новый материал
     * @param material материал для добавления
     * @return добавленный материал с присвоенным ID
     */
    Material addMaterial(Material material);
    
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
}