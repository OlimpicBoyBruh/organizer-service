package ru.organaizersrv.uiorganizerapp.dao.impl;

import ru.organaizersrv.uiorganizerapp.dao.DisciplineDAO;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация DisciplineDAO для работы с файловым хранилищем
 */
public class FileDisciplineDAO implements DisciplineDAO {
    private static final String DATA_DIRECTORY = System.getProperty("user.home") + File.separator + "organizer_data";
    private static final String DISCIPLINES_FILE = DATA_DIRECTORY + File.separator + "disciplines.csv";
    
    private List<String> disciplines;
    
    public FileDisciplineDAO() {
        initialize();
    }
    
    private void initialize() {
        disciplines = new ArrayList<>();
        File dataDir = new File(DATA_DIRECTORY);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        loadDisciplines();
    }
    
    private void loadDisciplines() {
        File file = new File(DISCIPLINES_FILE);
        if (!file.exists()) {
            // Добавляем несколько дисциплин по умолчанию
            disciplines.add("Java");
            disciplines.add("Python");
            disciplines.add("C#");
            disciplines.add("JavaScript");
            saveDisciplines();
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                disciplines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveDisciplines() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DISCIPLINES_FILE))) {
            for (String discipline : disciplines) {
                writer.write(discipline + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<String> getAllDisciplines() {
        return new ArrayList<>(disciplines);
    }
    
    @Override
    public boolean addDiscipline(String disciplineName) {
        if (disciplineExists(disciplineName)) {
            return false;
        }
        
        disciplines.add(disciplineName);
        saveDisciplines();
        return true;
    }
    
    @Override
    public boolean deleteDiscipline(String disciplineName) {
        boolean removed = disciplines.remove(disciplineName);
        if (removed) {
            saveDisciplines();
        }
        return removed;
    }
    
    @Override
    public boolean disciplineExists(String disciplineName) {
        return disciplines.contains(disciplineName);
    }
}