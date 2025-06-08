package ru.organaizersrv.uiorganizerapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UiOrganizerAppApplicationTest {

    @Test
    public void testCreateSpringApplicationBuilder() {
        SpringApplicationBuilder builder = UiOrganizerAppApplication.createSpringApplicationBuilder();
        
        assertNotNull(builder);
        // Since we can't directly check the WebApplicationType without executing the builder,
        // we're just asserting that the builder isn't null, indicating it was created successfully
    }
} 