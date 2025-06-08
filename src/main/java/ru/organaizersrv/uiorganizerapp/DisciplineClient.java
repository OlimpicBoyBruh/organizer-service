package ru.organaizersrv.uiorganizerapp;


import java.util.ArrayList;
import java.util.List;


public class DisciplineClient {

    private static List<String> disciplines;

    public DisciplineClient() {
        disciplines = new ArrayList<>();
        disciplines.add("Java");
        disciplines.add("Python");
        disciplines.add("C#");
        disciplines.add("JavaScript");
    }

    public List<String> getDisciplines() {
        return disciplines;
    }

    public void addDiscipline(String newDiscipline) {
        disciplines.add(newDiscipline);
    }
}
