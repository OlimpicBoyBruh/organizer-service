package ru.organaizersrv.uiorganizerapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.organaizersrv.uiorganizerapp.dao.DisciplineDAO;

import java.util.List;

@Service
public class OrganizerService {
    private final DisciplineDAO disciplineDAO;

    @Autowired
    public OrganizerService(DisciplineDAO disciplineDAO) {
        this.disciplineDAO = disciplineDAO;
    }

    public List<String> getAllDisciplines() {
        return disciplineDAO.getAllDisciplines();
    }

    public boolean addDiscipline(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return disciplineDAO.addDiscipline(name.trim());
    }

    public boolean deleteDiscipline(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return disciplineDAO.deleteDiscipline(name);
    }
    
    public boolean disciplineExists(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return disciplineDAO.disciplineExists(name);
    }
} 