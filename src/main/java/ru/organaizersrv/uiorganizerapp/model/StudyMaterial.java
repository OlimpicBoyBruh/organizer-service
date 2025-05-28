package ru.organaizersrv.uiorganizerapp.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StudyMaterial implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String details;
    private LocalDateTime dateAdded;
    private String tag;
    private List<Attachment> attachments;

    public StudyMaterial(String name, String details, String tag) {
        this.name = name;
        this.details = details;
        this.tag = tag;
        this.dateAdded = LocalDateTime.now();
        this.attachments = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getDateAdded() {
        return dateAdded;
    }

    public void setDateAdded(LocalDateTime dateAdded) {
        this.dateAdded = dateAdded;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }
} 