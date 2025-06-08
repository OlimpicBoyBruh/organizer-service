package ru.organaizersrv.uiorganizerapp.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.organaizersrv.uiorganizerapp.dao.DisciplineDAO;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisciplineServiceImplTest {

    @Mock
    private DisciplineDAO disciplineDAO;

    private DisciplineServiceImpl disciplineService;

    @BeforeEach
    void setUp() {
        disciplineService = new DisciplineServiceImpl(disciplineDAO);
    }

    @Test
    void testGetAllDisciplines() {
        // Arrange
        List<String> expectedDisciplines = Arrays.asList("Mathematics", "Physics", "Computer Science");
        when(disciplineDAO.getAllDisciplines()).thenReturn(expectedDisciplines);

        // Act
        List<String> actualDisciplines = disciplineService.getAllDisciplines();

        // Assert
        assertEquals(expectedDisciplines, actualDisciplines);
        verify(disciplineDAO).getAllDisciplines();
    }

    @Test
    void testAddDiscipline_Success() {
        // Arrange
        String disciplineName = "Chemistry";
        when(disciplineDAO.addDiscipline(disciplineName)).thenReturn(true);

        // Act
        boolean result = disciplineService.addDiscipline(disciplineName);

        // Assert
        assertTrue(result);
        verify(disciplineDAO).addDiscipline(disciplineName);
    }

    @Test
    void testAddDiscipline_EmptyName() {
        // Act
        boolean result = disciplineService.addDiscipline("");

        // Assert
        assertFalse(result);
        verifyNoInteractions(disciplineDAO);
    }

    @Test
    void testAddDiscipline_NullName() {
        // Act
        boolean result = disciplineService.addDiscipline(null);

        // Assert
        assertFalse(result);
        verifyNoInteractions(disciplineDAO);
    }

    @Test
    void testDeleteDiscipline_Success() {
        // Arrange
        String disciplineName = "Biology";
        when(disciplineDAO.deleteDiscipline(disciplineName)).thenReturn(true);

        // Act
        boolean result = disciplineService.deleteDiscipline(disciplineName);

        // Assert
        assertTrue(result);
        verify(disciplineDAO).deleteDiscipline(disciplineName);
    }

    @Test
    void testDeleteDiscipline_EmptyName() {
        // Act
        boolean result = disciplineService.deleteDiscipline("");

        // Assert
        assertFalse(result);
        verifyNoInteractions(disciplineDAO);
    }

    @Test
    void testDisciplineExists_Success() {
        // Arrange
        String disciplineName = "Physics";
        when(disciplineDAO.disciplineExists(disciplineName)).thenReturn(true);

        // Act
        boolean result = disciplineService.disciplineExists(disciplineName);

        // Assert
        assertTrue(result);
        verify(disciplineDAO).disciplineExists(disciplineName);
    }

    @Test
    void testDisciplineExists_EmptyName() {
        // Act
        boolean result = disciplineService.disciplineExists("");

        // Assert
        assertFalse(result);
        verifyNoInteractions(disciplineDAO);
    }

    @Test
    void testChangeDataSource_DbSource() {
        // Act
        disciplineService.changeDataSource("db");

        // Assert
        assertEquals("db", disciplineService.getCurrentDataSource());
    }

    @Test
    void testChangeDataSource_FileSource() {
        // Act
        disciplineService.changeDataSource("file");

        // Assert
        assertEquals("file", disciplineService.getCurrentDataSource());
    }

    @Test
    void testChangeDataSource_ApiSource() {
        // Act
        disciplineService.changeDataSource("api");

        // Assert
        assertEquals("api", disciplineService.getCurrentDataSource());
    }

    @Test
    void testChangeDataSource_InvalidSource() {
        // Act
        disciplineService.changeDataSource("invalid");

        // Assert
        assertEquals("file", disciplineService.getCurrentDataSource());
    }
} 