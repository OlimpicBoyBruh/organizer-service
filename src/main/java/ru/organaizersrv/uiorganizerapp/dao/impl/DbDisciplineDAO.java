package ru.organaizersrv.uiorganizerapp.dao.impl;

import org.springframework.stereotype.Repository;
import ru.organaizersrv.uiorganizerapp.dao.DisciplineDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация DisciplineDAO для работы с базой данных H2
 */
@Repository
public class DbDisciplineDAO implements DisciplineDAO {
    private static final String DB_URL = "jdbc:h2:~/organizer_db;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    public DbDisciplineDAO() {
        initialize();
    }
    
    private void initialize() {
        try {
            Class.forName("org.h2.Driver");
            try (Connection conn = getConnection()) {
                createTablesIfNotExist(conn);
                insertDefaultDisciplines(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    private void createTablesIfNotExist(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS disciplines (" +
                    "name VARCHAR(255) PRIMARY KEY);");
        }
    }
    
    private void insertDefaultDisciplines(Connection conn) throws SQLException {
        // Проверяем, есть ли уже записи в таблице
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM disciplines")) {
            if (rs.next() && rs.getInt(1) == 0) {
                // Если таблица пуста, добавляем дисциплины по умолчанию
                String[] defaultDisciplines = {"Java", "Python", "C#", "JavaScript"};
                for (String discipline : defaultDisciplines) {
                    try (PreparedStatement pstmt = conn.prepareStatement(
                            "INSERT INTO disciplines (name) VALUES (?)")) {
                        pstmt.setString(1, discipline);
                        pstmt.executeUpdate();
                    }
                }
            }
        }
    }
    
    @Override
    public List<String> getAllDisciplines() {
        List<String> disciplines = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT name FROM disciplines ORDER BY name")) {
            
            while (rs.next()) {
                disciplines.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return disciplines;
    }
    
    @Override
    public boolean addDiscipline(String disciplineName) {
        if (disciplineExists(disciplineName)) {
            return false;
        }
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO disciplines (name) VALUES (?)")) {
            
            pstmt.setString(1, disciplineName);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteDiscipline(String disciplineName) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "DELETE FROM disciplines WHERE name = ?")) {
            
            pstmt.setString(1, disciplineName);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean disciplineExists(String disciplineName) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "SELECT 1 FROM disciplines WHERE name = ?")) {
            
            pstmt.setString(1, disciplineName);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}