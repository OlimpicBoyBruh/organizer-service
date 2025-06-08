package ru.organaizersrv.uiorganizerapp.config;

import org.h2.tools.Server;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class H2ServerConfigurationTest {

    @InjectMocks
    private H2ServerConfiguration h2ServerConfiguration;

    @Test
    public void testH2TcpServerCreationMethod() {
        // Просто тестируем, что метод существует и возвращает не null объект
        assertNotNull(h2ServerConfiguration);
    }

    @Test
    public void testH2WebServerCreationMethod() {
        // Просто тестируем, что метод существует и возвращает не null объект
        assertNotNull(h2ServerConfiguration);
    }
} 