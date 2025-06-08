package ru.organaizersrv.uiorganizerapp.dao;

import java.util.List;

/**
 * Интерфейс для доступа к данным дисциплин
 */
public interface DisciplineDAO {
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
     * Удалить дисциплину по названию
     * @param name название дисциплины
     * @return true если удаление успешно, иначе false
     */
    boolean deleteDiscipline(String name);
    
    /**
     * Проверить существование дисциплины
     * @param disciplineName название дисциплины
     * @return true если дисциплина существует, иначе false
     */
    boolean disciplineExists(String disciplineName);
}