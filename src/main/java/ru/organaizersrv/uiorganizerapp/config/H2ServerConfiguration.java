package ru.organaizersrv.uiorganizerapp.config;

import org.h2.tools.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.sql.SQLException;

@Configuration
@Profile("test") // Активировать только для тестов
public class H2ServerConfiguration {

    /**
     * Start H2 database as TCP server so it can be accessed from external tools
     * like DBeaver or IntelliJ database tools via localhost:9091
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2TcpServer() throws SQLException {
        // Не будет загружен при обычном запуске, только при тестах
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9091");
    }

    /**
     * Start H2 web console for database access via browser
     * Available at http://localhost:8083
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2WebServer() throws SQLException {
        // Не будет загружен при обычном запуске, только при тестах
        return Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083");
    }
} 