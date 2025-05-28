package ru.organaizersrv.uiorganizerapp.dao.impl;

import ru.organaizersrv.uiorganizerapp.dao.MaterialDAO;
import ru.organaizersrv.uiorganizerapp.model.Attachment;
import ru.organaizersrv.uiorganizerapp.model.Material;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация MaterialDAO для работы с файловым хранилищем
 */
public class FileMaterialDAO implements MaterialDAO {
    private static final String DATA_DIRECTORY = System.getProperty("user.home") + File.separator + "organizer_data";
    private static final String MATERIALS_FILE = DATA_DIRECTORY + File.separator + "materials.csv";
    private static final String ATTACHMENTS_DIRECTORY = DATA_DIRECTORY + File.separator + "attachments";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    private List<Material> materials;
    private long nextId = 1;
    
    public FileMaterialDAO() {
        initialize();
    }
    
    private void initialize() {
        materials = new ArrayList<>();
        File dataDir = new File(DATA_DIRECTORY);
        if (!dataDir.exists()) {
            dataDir.mkdirs();
        }
        
        File attachmentsDir = new File(ATTACHMENTS_DIRECTORY);
        if (!attachmentsDir.exists()) {
            attachmentsDir.mkdirs();
        }
        
        loadMaterials();
    }
    
    private void loadMaterials() {
        File file = new File(MATERIALS_FILE);
        if (!file.exists()) {
            return;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 5) {
                    long id = Long.parseLong(parts[0]);
                    String name = parts[1];
                    LocalDate dateAdded = LocalDate.parse(parts[2], DATE_FORMATTER);
                    String tags = parts[3];
                    String disciplineName = parts[4];
                    
                    Material material = new Material(name, dateAdded, tags, disciplineName);
                    material.setId(id);
                    materials.add(material);
                    
                    if (id >= nextId) {
                        nextId = id + 1;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void saveMaterials() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(MATERIALS_FILE))) {
            for (Material material : materials) {
                writer.write(String.format("%d,%s,%s,%s,%s\n",
                        material.getId(),
                        material.getName(),
                        material.getDateAddedRaw().format(DATE_FORMATTER),
                        material.getTags(),
                        material.getDisciplineName()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public List<Material> getAllMaterials() {
        return new ArrayList<>(materials);
    }
    
    @Override
    public List<Material> getMaterialsByDiscipline(String disciplineName) {
        return materials.stream()
                .filter(m -> disciplineName.equals(m.getDisciplineName()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> searchByName(String name) {
        return materials.stream()
                .filter(m -> m.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> searchByDate(LocalDate date) {
        return materials.stream()
                .filter(m -> m.getDateAddedRaw().equals(date))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Material> searchByTag(String tag) {
        return materials.stream()
                .filter(m -> m.getTags().toLowerCase().contains(tag.toLowerCase()))
                .collect(Collectors.toList());
    }
    
    @Override
    public Material addMaterial(Material material) {
        material.setId(nextId++);
        materials.add(material);
        saveMaterials();
        return material;
    }
    
    @Override
    public Material updateMaterial(Material material) {
        for (int i = 0; i < materials.size(); i++) {
            if (materials.get(i).getId().equals(material.getId())) {
                materials.set(i, material);
                saveMaterials();
                return material;
            }
        }
        return null;
    }
    
    @Override
    public boolean deleteMaterial(Long id) {
        boolean removed = materials.removeIf(m -> m.getId().equals(id));
        if (removed) {
            saveMaterials();
        }
        return removed;
    }
    
    @Override
    public Material getMaterialById(Long id) {
        return materials.stream()
                .filter(m -> m.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
    
    // Методы для работы с вложениями
    public String saveAttachment(File file, String materialId) {
        try {
            File destDir = new File(ATTACHMENTS_DIRECTORY + File.separator + materialId);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            
            File destFile = new File(destDir, file.getName());
            try (FileInputStream in = new FileInputStream(file);
                 FileOutputStream out = new FileOutputStream(destFile)) {
                byte[] buffer = new byte[1024];
                int length;
                while ((length = in.read(buffer)) > 0) {
                    out.write(buffer, 0, length);
                }
            }
            
            return destFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}