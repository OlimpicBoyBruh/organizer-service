package ru.organaizersrv.uiorganizerapp.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class MaterialTest {

    @Test
    void testMaterialConstructor() {
        // Test the default constructor
        Material material1 = new Material();
        assertNotNull(material1.getAttachments());
        assertTrue(material1.getAttachments().isEmpty());
        
        // Test constructor with name, date, and tags
        LocalDate now = LocalDate.now();
        Material material2 = new Material("Test Material", now, "test, example");
        assertEquals("Test Material", material2.getName());
        assertEquals(now, material2.getDateAddedRaw());
        assertEquals("test, example", material2.getTags());
        assertNotNull(material2.getAttachments());
        
        // Test constructor with name, date, tags, and discipline
        Material material3 = new Material("Test Material", now, "test, example", "Math");
        assertEquals("Math", material3.getDisciplineName());
    }
    
    @Test
    void testSettersAndGetters() {
        Material material = new Material();
        
        // Test id
        material.setId(1L);
        assertEquals(1L, material.getId());
        
        // Test name
        material.setName("Updated Name");
        assertEquals("Updated Name", material.getName());
        
        // Test date
        LocalDate testDate = LocalDate.of(2023, 5, 15);
        material.setDateAdded(testDate);
        assertEquals(testDate, material.getDateAddedRaw());
        assertEquals("15.05.2023", material.getDateAdded());
        
        // Test tags
        material.setTags("new, tags");
        assertEquals("new, tags", material.getTags());
        
        // Test discipline
        material.setDisciplineName("Physics");
        assertEquals("Physics", material.getDisciplineName());
    }
    
    @Test
    void testAttachmentManagement() {
        Material material = new Material();
        Attachment attachment1 = new Attachment();
        attachment1.setId(1L);
        attachment1.setName("File 1");
        
        Attachment attachment2 = new Attachment();
        attachment2.setId(2L);
        attachment2.setName("Link 1");
        
        // Test adding attachments
        material.addAttachment(attachment1);
        assertEquals(1, material.getAttachments().size());
        
        material.addAttachment(attachment2);
        assertEquals(2, material.getAttachments().size());
        
        // Test removing attachments
        material.removeAttachment(attachment1);
        assertEquals(1, material.getAttachments().size());
        assertEquals(attachment2, material.getAttachments().get(0));
        
        // Test setting attachments directly
        List<Attachment> attachments = new ArrayList<>();
        attachments.add(attachment1);
        material.setAttachments(attachments);
        assertEquals(1, material.getAttachments().size());
        assertEquals(attachment1, material.getAttachments().get(0));
    }
    
    @Test
    void testToString() {
        LocalDate testDate = LocalDate.of(2023, 5, 15);
        Material material = new Material("Test Material", testDate, "test, example", "Math");
        
        String expected = "Название: Test Material, Дата добавления: 15.05.2023, Теги: test, example";
        assertEquals(expected, material.toString());
    }
} 