package ru.organaizersrv.uiorganizerapp.dao.impl;

import ru.organaizersrv.uiorganizerapp.dao.DisciplineDAO;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Реализация DisciplineDAO для работы с внешним REST API
 * Примечание: Это имитация работы с API, так как реального API нет
 */
public class ApiDisciplineDAO implements DisciplineDAO {
    private static final String API_BASE_URL = "https://api.example.com/disciplines";
    
    // Локальный кэш дисциплин для имитации работы с API
    private List<String> disciplineCache;
    
    public ApiDisciplineDAO() {
        // Инициализируем кэш с тестовыми данными
        disciplineCache = new ArrayList<>(Arrays.asList("Java", "Python", "C#", "JavaScript", "REST API"));
    }
    
    @Override
    public List<String> getAllDisciplines() {
        // Имитация GET запроса к API
        try {
            // В реальном приложении здесь был бы HTTP запрос
            // String response = sendGetRequest(API_BASE_URL);
            // ... обработка JSON ответа
            
            // Возвращаем данные из кэша
            return new ArrayList<>(disciplineCache);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    @Override
    public boolean addDiscipline(String disciplineName) {
        // Имитация POST запроса к API
        try {
            // В реальном приложении здесь был бы HTTP запрос
            // String jsonData = "{\"name\":\"" + disciplineName + "\"}";
            // String response = sendPostRequest(API_BASE_URL, jsonData);
            // ... обработка ответа
            
            if (disciplineExists(disciplineName)) {
                return false;
            }
            
            disciplineCache.add(disciplineName);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean deleteDiscipline(String disciplineName) {
        // Имитация DELETE запроса к API
        try {
            // В реальном приложении здесь был бы HTTP запрос
            // URL url = new URL(API_BASE_URL + "/" + URLEncoder.encode(disciplineName, "UTF-8"));
            // HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            // conn.setRequestMethod("DELETE");
            // ... обработка ответа
            
            return disciplineCache.remove(disciplineName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public boolean disciplineExists(String disciplineName) {
        // Имитация GET запроса к API для проверки существования
        try {
            // В реальном приложении здесь был бы HTTP запрос
            // URL url = new URL(API_BASE_URL + "/" + URLEncoder.encode(disciplineName, "UTF-8"));
            // ... обработка ответа
            
            return disciplineCache.contains(disciplineName);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Вспомогательные методы для работы с HTTP
    
    private String sendGetRequest(String urlStr) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        
        if (conn.getResponseCode() != 200) {
            throw new IOException("HTTP Error: " + conn.getResponseCode());
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        
        return response.toString();
    }
    
    private String sendPostRequest(String urlStr, String jsonData) throws IOException {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setDoOutput(true);
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = jsonData.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        if (conn.getResponseCode() != 201 && conn.getResponseCode() != 200) {
            throw new IOException("HTTP Error: " + conn.getResponseCode());
        }
        
        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();
        
        return response.toString();
    }
}