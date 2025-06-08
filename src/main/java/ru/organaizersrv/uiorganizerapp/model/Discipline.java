package ru.organaizersrv.uiorganizerapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Discipline implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private List<StudyMaterial> materials;

    public Discipline() {
        this.materials = new ArrayList<>();
    }

    public Discipline(String name) {
        this();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<StudyMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<StudyMaterial> materials) {
        this.materials = materials;
    }

    @Override
    public String toString() {
        return name;
    }
} 