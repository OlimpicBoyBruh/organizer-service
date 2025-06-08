package ru.organaizersrv.uiorganizerapp.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Material {
    private Long id;
    private String name;
    private LocalDate dateAdded;
    private String tags;
    private String disciplineName;
    private List<Attachment> attachments;

    // Конструкторы
    public Material() {
        this.attachments = new ArrayList<>();
    }

    public Material(String name, LocalDate dateAdded, String tags) {
        this();
        this.name = name;
        this.dateAdded = dateAdded;
        this.tags = tags;
    }

    public Material(String name, LocalDate dateAdded, String tags, String disciplineName) {
        this(name, dateAdded, tags);
        this.disciplineName = disciplineName;
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

    public LocalDate getDateAddedRaw() {
        return dateAdded;
    }

    public String getDateAdded() {
        // Форматируем дату в строку
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return dateAdded.format(formatter);
    }

    public void setDateAdded(LocalDate dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getDisciplineName() {
        return disciplineName;
    }

    public void setDisciplineName(String disciplineName) {
        this.disciplineName = disciplineName;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public void addAttachment(Attachment attachment) {
        this.attachments.add(attachment);
    }

    public void removeAttachment(Attachment attachment) {
        this.attachments.remove(attachment);
    }

    @Override
    public String toString() {
        return "Название: " + name + ", Дата добавления: " + getDateAdded() + ", Теги: " + tags;
    }
}