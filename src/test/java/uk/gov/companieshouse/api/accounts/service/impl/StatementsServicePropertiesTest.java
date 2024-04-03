package uk.gov.companieshouse.api.accounts.service.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatementsServicePropertiesTest {
    @InjectMocks
    private StatementsServiceProperties statementsServiceProperties;

    @Test
    @DisplayName("Test Clone of Statements ")
    void testGetCloneOfStatements() {
        statementsServiceProperties.setStatements(new HashMap<>());
        assertNotNull(statementsServiceProperties.getCloneOfStatements());
    }

    @Test
    @DisplayName("Test annotations on the class")
    void testAnnotations() {
        assertTrue(this.statementsServiceProperties.getClass().isAnnotationPresent(ConfigurationProperties.class));
        assertTrue(this.statementsServiceProperties.getClass().isAnnotationPresent(Component.class));
        assertTrue(this.statementsServiceProperties.getClass().isAnnotationPresent(PropertySource.class));
    }
}
