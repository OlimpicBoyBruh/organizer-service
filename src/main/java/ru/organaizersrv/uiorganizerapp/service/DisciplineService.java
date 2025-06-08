package ru.organaizersrv.uiorganizerapp.service;

import java.util.List;

/**
 * Сервис для работы с дисциплинами
 */
public interface DisciplineService {
    /**
     * Получить все дисциплины
     * @return список всех дисциплин
     */
    List<String> getAllDisciplines();
    
    /**
     * Добавить новую дисциплину
     * @param disciplineName название дисциплины
     * @return true если добавление успешно, иначе false
     */
    boolean addDiscipline(String disciplineName);
    
    /**
     * Удалить дисциплину
     * @param disciplineName название дисциплины
     * @return true если удаление успешно, иначе false
     */
    boolean deleteDiscipline(String disciplineName);
    
    /**
     * Обновить название дисциплины
     * @param oldName старое название дисциплины
     * @param newName новое название дисциплины
     * @return true если обновление успешно, иначе false
     */
    boolean updateDiscipline(String oldName, String newName);
    
    /**
     * Проверить существование дисциплины
     * @param disciplineName название дисциплины
     * @return true если дисциплина существует, иначе false
     */
    boolean disciplineExists(String disciplineName);
    
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