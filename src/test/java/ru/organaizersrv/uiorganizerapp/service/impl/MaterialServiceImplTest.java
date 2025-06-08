package ru.organaizersrv.uiorganizerapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.organaizersrv.uiorganizerapp.dao.MaterialDAO;
import ru.organaizersrv.uiorganizerapp.model.Material;
import ru.organaizersrv.uiorganizerapp.model.Attachment;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MaterialServiceImplTest {

    @Mock
    private MaterialDAO materialDAO;

    private MaterialServiceImpl materialService;

    @BeforeEach
    void setUp() {
        materialService = new MaterialServiceImpl(materialDAO);
    }

    @Test
    void testGetAllMaterials() {
        // Arrange
        List<Material> expectedMaterials = Arrays.asList(
            createTestMaterial(1L, "Material 1", "Math"),
            createTestMaterial(2L, "Material 2", "Physics")
        );
        when(materialDAO.getAllMaterials()).thenReturn(expectedMaterials);

        // Act
        List<Material> actualMaterials = materialService.getAllMaterials();

        // Assert
        assertEquals(expectedMaterials, actualMaterials);
        verify(materialDAO).getAllMaterials();
    }

    @Test
    void testGetMaterialsByDiscipline() {
        // Arrange
        String disciplineName = "Math";
        List<Material> expectedMaterials = Collections.singletonList(
            createTestMaterial(1L, "Material 1", disciplineName)
        );
        when(materialDAO.getMaterialsByDiscipline(disciplineName)).thenReturn(expectedMaterials);

        // Act
        List<Material> actualMaterials = materialService.getMaterialsByDiscipline(disciplineName);

        // Assert
        assertEquals(expectedMaterials, actualMaterials);
        verify(materialDAO).getMaterialsByDiscipline(disciplineName);
    }

    @Test
    void testSearchByName() {
        // Arrange
        String name = "Math";
        List<Material> expectedMaterials = Collections.singletonList(
            createTestMaterial(1L, "Mathematics 101", "Math")
        );
        when(materialDAO.searchByName(name)).thenReturn(expectedMaterials);

        // Act
        List<Material> actualMaterials = materialService.searchByName(name);

        // Assert
        assertEquals(expectedMaterials, actualMaterials);
        verify(materialDAO).searchByName(name);
    }

    @Test
    void testSearchByTag() {
        // Arrange
        String tag = "exam";
        List<Material> expectedMaterials = Collections.singletonList(
            createTestMaterial(1L, "Exam Prep", "Math")
        );
        when(materialDAO.searchByTag(tag)).thenReturn(expectedMaterials);

        // Act
        List<Material> actualMaterials = materialService.searchByTag(tag);

        // Assert
        assertEquals(expectedMaterials, actualMaterials);
        verify(materialDAO).searchByTag(tag);
    }

    @Test
    void testAddMaterial() {
        // Arrange
        String name = "New Material";
        String tags = "test, example";
        String disciplineName = "Math";
        Material expectedMaterial = createTestMaterial(1L, name, disciplineName);
        when(materialDAO.addMaterial(any(Material.class))).thenReturn(expectedMaterial);

        // Act
        Material actualMaterial = materialService.addMaterial(name, tags, disciplineName);

        // Assert
        assertEquals(expectedMaterial, actualMaterial);
        verify(materialDAO).addMaterial(any(Material.class));
    }

    @Test
    void testUpdateMaterial() {
        // Arrange
        Material material = createTestMaterial(1L, "Updated Material", "Math");
        when(materialDAO.updateMaterial(material)).thenReturn(material);

        // Act
        Material actualMaterial = materialService.updateMaterial(material);

        // Assert
        assertEquals(material, actualMaterial);
        verify(materialDAO).updateMaterial(material);
    }

    @Test
    void testDeleteMaterial() {
        // Arrange
        Long id = 1L;
        when(materialDAO.deleteMaterial(id)).thenReturn(true);

        // Act
        boolean result = materialService.deleteMaterial(id);

        // Assert
        assertTrue(result);
        verify(materialDAO).deleteMaterial(id);
    }

    @Test
    void testGetMaterialById() {
        // Arrange
        Long id = 1L;
        Material expectedMaterial = createTestMaterial(id, "Material 1", "Math");
        when(materialDAO.getMaterialById(id)).thenReturn(expectedMaterial);

        // Act
        Material actualMaterial = materialService.getMaterialById(id);

        // Assert
        assertEquals(expectedMaterial, actualMaterial);
        verify(materialDAO).getMaterialById(id);
    }

    @Test
    void testChangeDataSource_DbSource() {
        // Act
        materialService.changeDataSource("db");

        // Assert
        assertEquals("db", materialService.getCurrentDataSource());
    }

    @Test
    void testChangeDataSource_FileSource() {
        // Act
        materialService.changeDataSource("file");

        // Assert
        assertEquals("file", materialService.getCurrentDataSource());
    }

    @Test
    void testChangeDataSource_ApiSource() {
        // Act
        materialService.changeDataSource("api");

        // Assert
        assertEquals("api", materialService.getCurrentDataSource());
    }

    private Material createTestMaterial(Long id, String name, String disciplineName) {
        Material material = new Material();
        material.setId(id);
        material.setName(name);
        material.setDisciplineName(disciplineName);
        return material;
    }
} 