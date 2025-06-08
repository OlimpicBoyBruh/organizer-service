package ru.organaizersrv.uiorganizerapp.service.impl;

import org.springframework.stereotype.Service;
import ru.organaizersrv.uiorganizerapp.dao.DisciplineDAO;
import ru.organaizersrv.uiorganizerapp.dao.impl.ApiDisciplineDAO;
import ru.organaizersrv.uiorganizerapp.dao.impl.DbDisciplineDAO;
import ru.organaizersrv.uiorganizerapp.dao.impl.FileDisciplineDAO;
import ru.organaizersrv.uiorganizerapp.service.DisciplineService;

import java.util.List;

/**
 * Реализация сервиса для работы с дисциплинами
 */
@Service
public class DisciplineServiceImpl implements DisciplineService {
    private DisciplineDAO disciplineDAO;
    private String currentDataSource;

    public DisciplineServiceImpl(DisciplineDAO disciplineDAO) {
        this.disciplineDAO = disciplineDAO;
    }
    
    @Override
    public List<String> getAllDisciplines() {
        return disciplineDAO.getAllDisciplines();
    }
    
    @Override
    public boolean addDiscipline(String disciplineName) {
        if (disciplineName == null || disciplineName.trim().isEmpty()) {
            return false;
        }
        return disciplineDAO.addDiscipline(disciplineName.trim());
    }
    
    @Override
    public boolean deleteDiscipline(String disciplineName) {
        if (disciplineName == null || disciplineName.trim().isEmpty()) {
            return false;
        }
        return disciplineDAO.deleteDiscipline(disciplineName);
    }
    
    @Override
    public boolean updateDiscipline(String oldName, String newName) {
        if (oldName == null || oldName.trim().isEmpty() || 
            newName == null || newName.trim().isEmpty()) {
            return false;
        }
        
        // Проверяем существование старой дисциплины
        if (!disciplineDAO.disciplineExists(oldName)) {
            return false;
        }
        
        // Проверяем, что новое имя еще не используется
        if (disciplineDAO.disciplineExists(newName) && !oldName.equals(newName)) {
            return false;
        }
        
        // Сначала добавляем новую дисциплину
        boolean added = disciplineDAO.addDiscipline(newName.trim());
        if (!added) {
            return false;
        }
        
        // Если это одна и та же дисциплина, считаем что обновление успешно
        if (oldName.equals(newName)) {
            return true;
        }
        
        // Затем удаляем старую дисциплину
        return disciplineDAO.deleteDiscipline(oldName);
    }
    
    @Override
    public boolean disciplineExists(String disciplineName) {
        if (disciplineName == null || disciplineName.trim().isEmpty()) {
            return false;
        }
        return disciplineDAO.disciplineExists(disciplineName);
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
                disciplineDAO = new FileDisciplineDAO();
                currentDataSource = "file";
                break;
            case "db":
                disciplineDAO = new DbDisciplineDAO();
                currentDataSource = "db";
                break;
            case "api":
                disciplineDAO = new ApiDisciplineDAO();
                currentDataSource = "api";
                break;
            default:
                // Если тип не распознан, используем файловое хранилище
                disciplineDAO = new FileDisciplineDAO();
                currentDataSource = "file";
                break;
        }
    }
    
    @Override
    public String getCurrentDataSource() {
        return currentDataSource;
    }
}