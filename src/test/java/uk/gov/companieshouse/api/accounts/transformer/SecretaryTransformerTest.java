package uk.gov.companieshouse.api.accounts.transformer;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.SecretaryDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.SecretaryEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Secretary;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class SecretaryTransformerTest {
    private static final String NAME = "name";

    private final SecretaryTransformer transformer = new SecretaryTransformer();

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntity() {
        Secretary secretary = new Secretary();
        secretary.setName(NAME);

        SecretaryEntity secretaryEntity = transformer.transform(secretary);

        assertNotNull(secretaryEntity);
        assertNotNull(secretaryEntity.getData());
        assertEquals(NAME, secretaryEntity.getData().getName());
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRest() {
        SecretaryDataEntity secretaryDataEntity = new SecretaryDataEntity();
        secretaryDataEntity.setName(NAME);

        SecretaryEntity secretaryEntity = new SecretaryEntity();
        secretaryEntity.setData(secretaryDataEntity);

        Secretary secretary = transformer.transform(secretaryEntity);

        assertNotNull(secretary);
        assertEquals(NAME, secretary.getName());
    }
}
