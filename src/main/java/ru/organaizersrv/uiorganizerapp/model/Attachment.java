package ru.organaizersrv.uiorganizerapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Attachment implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private Long id;
    private String name;
    private String path;
    private AttachmentType type;
    private LocalDateTime createdAt;
    private Long materialId;

    // Конструкторы
    public Attachment() {
        this.createdAt = LocalDateTime.now();
    }

    public Attachment(String name, String path, AttachmentType type) {
        this();
        this.name = name;
        this.path = path;
        this.type = type;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public AttachmentType getType() {
        return type;
    }

    public void setType(AttachmentType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getMaterialId() {
        return materialId;
    }

    public void setMaterialId(Long materialId) {
        this.materialId = materialId;
    }

    @Override
    public String toString() {
        return name;
    }
}