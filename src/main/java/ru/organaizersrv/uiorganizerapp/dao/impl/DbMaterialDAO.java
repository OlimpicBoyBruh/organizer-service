package ru.organaizersrv.uiorganizerapp.dao.impl;

import org.springframework.stereotype.Repository;
import ru.organaizersrv.uiorganizerapp.dao.MaterialDAO;
import ru.organaizersrv.uiorganizerapp.model.Attachment;
import ru.organaizersrv.uiorganizerapp.model.AttachmentType;
import ru.organaizersrv.uiorganizerapp.model.Material;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Реализация MaterialDAO для работы с базой данных H2
 */
@Repository
public class DbMaterialDAO implements MaterialDAO {
    private static final String DB_URL = "jdbc:h2:~/organizer_db;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    public DbMaterialDAO() {
        initialize();
    }
    
    private void initialize() {
        try {
            Class.forName("org.h2.Driver");
            try (Connection conn = getConnection()) {
                createTablesIfNotExist(conn);
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
            // Создаем таблицу материалов
            stmt.execute("CREATE TABLE IF NOT EXISTS materials (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "date_added DATE NOT NULL, " +
                    "tags VARCHAR(255), " +
                    "discipline_name VARCHAR(255) NOT NULL);");
            
            // Создаем таблицу вложений
            stmt.execute("CREATE TABLE IF NOT EXISTS attachments (" +
                    "id BIGINT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(255) NOT NULL, " +
                    "path VARCHAR(1024) NOT NULL, " +
                    "type VARCHAR(50) NOT NULL, " +
                    "created_at TIMESTAMP NOT NULL, " +
                    "material_id BIGINT NOT NULL, " +
                    "FOREIGN KEY (material_id) REFERENCES materials(id) ON DELETE CASCADE);");
        }
    }
    
    @Override
    public List<Material> getAllMaterials() {
        List<Material> materials = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM materials")) {
            
            while (rs.next()) {
                Material material = extractMaterialFromResultSet(rs);
                loadAttachments(conn, material);
                materials.add(material);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }
    
    @Override
    public List<Material> getMaterialsByDiscipline(String disciplineName) {
        List<Material> materials = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM materials WHERE discipline_name = ?")) {
            
            pstmt.setString(1, disciplineName);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = extractMaterialFromResultSet(rs);
                    loadAttachments(conn, material);
                    materials.add(material);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }
    
    @Override
    public List<Material> searchByName(String name) {
        List<Material> materials = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM materials WHERE LOWER(name) LIKE ?")) {
            
            pstmt.setString(1, "%" + name.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = extractMaterialFromResultSet(rs);
                    loadAttachments(conn, material);
                    materials.add(material);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }
    
    @Override
    public List<Material> searchByDate(LocalDate date) {
        List<Material> materials = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM materials WHERE date_added = ?")) {
            
            pstmt.setDate(1, Date.valueOf(date));
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = extractMaterialFromResultSet(rs);
                    loadAttachments(conn, material);
                    materials.add(material);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }
    
    @Override
    public List<Material> searchByTag(String tag) {
        List<Material> materials = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM materials WHERE LOWER(tags) LIKE ?")) {
            
            pstmt.setString(1, "%" + tag.toLowerCase() + "%");
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Material material = extractMaterialFromResultSet(rs);
                    loadAttachments(conn, material);
                    materials.add(material);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return materials;
    }
    
    @Override
    public Material addMaterial(Material material) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO materials (name, date_added, tags, discipline_name) VALUES (?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, material.getName());
            pstmt.setDate(2, Date.valueOf(material.getDateAddedRaw()));
            pstmt.setString(3, material.getTags());
            pstmt.setString(4, material.getDisciplineName());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Не удалось создать материал, ни одна строка не была добавлена.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    material.setId(generatedKeys.getLong(1));
                    
                    // Сохраняем вложения, если они есть
                    for (Attachment attachment : material.getAttachments()) {
                        saveAttachment(conn, attachment, material.getId());
                    }
                } else {
                    throw new SQLException("Не удалось создать материал, не получен ID.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return material;
    }
    
    @Override
    public Material updateMaterial(Material material) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "UPDATE materials SET name = ?, date_added = ?, tags = ?, discipline_name = ? WHERE id = ?")) {
            
            pstmt.setString(1, material.getName());
            pstmt.setDate(2, Date.valueOf(material.getDateAddedRaw()));
            pstmt.setString(3, material.getTags());
            pstmt.setString(4, material.getDisciplineName());
            pstmt.setLong(5, material.getId());
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                // Обновляем вложения
                // Сначала удаляем все существующие
                try (PreparedStatement deleteStmt = conn.prepareStatement("DELETE FROM attachments WHERE material_id = ?")) {
                    deleteStmt.setLong(1, material.getId());
                    deleteStmt.executeUpdate();
                }
                
                // Затем добавляем все заново
                for (Attachment attachment : material.getAttachments()) {
                    saveAttachment(conn, attachment, material.getId());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return material;
    }
    
    @Override
    public boolean deleteMaterial(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM materials WHERE id = ?")) {
            
            pstmt.setLong(1, id);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Material getMaterialById(Long id) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM materials WHERE id = ?")) {
            
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Material material = extractMaterialFromResultSet(rs);
                    loadAttachments(conn, material);
                    return material;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private Material extractMaterialFromResultSet(ResultSet rs) throws SQLException {
        Material material = new Material();
        material.setId(rs.getLong("id"));
        material.setName(rs.getString("name"));
        material.setDateAdded(rs.getDate("date_added").toLocalDate());
        material.setTags(rs.getString("tags"));
        material.setDisciplineName(rs.getString("discipline_name"));
        return material;
    }
    
    private void loadAttachments(Connection conn, Material material) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM attachments WHERE material_id = ?")) {
            pstmt.setLong(1, material.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Attachment attachment = new Attachment();
                    attachment.setId(rs.getLong("id"));
                    attachment.setName(rs.getString("name"));
                    attachment.setPath(rs.getString("path"));
                    attachment.setType(AttachmentType.valueOf(rs.getString("type")));
                    attachment.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    attachment.setMaterialId(rs.getLong("material_id"));
                    material.addAttachment(attachment);
                }
            }
        }
    }
    
    private void saveAttachment(Connection conn, Attachment attachment, Long materialId) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO attachments (name, path, type, created_at, material_id) VALUES (?, ?, ?, ?, ?)",
                Statement.RETURN_GENERATED_KEYS)) {
            
            pstmt.setString(1, attachment.getName());
            pstmt.setString(2, attachment.getPath());
            pstmt.setString(3, attachment.getType().name());
            pstmt.setTimestamp(4, Timestamp.valueOf(attachment.getCreatedAt()));
            pstmt.setLong(5, materialId);
            
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows == 0) {
                throw new SQLException("Не удалось создать вложение, ни одна строка не была добавлена.");
            }
            
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    attachment.setId(generatedKeys.getLong(1));
                    attachment.setMaterialId(materialId);
                } else {
                    throw new SQLException("Не удалось создать вложение, не получен ID.");
                }
            }
        }
    }
}