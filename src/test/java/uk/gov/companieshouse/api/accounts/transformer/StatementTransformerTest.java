package uk.gov.companieshouse.api.accounts.transformer;


import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.StatementDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.StatementEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Statement;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class StatementTransformerTest {

    @InjectMocks
    private StatementTransformer statementTransformer;

    @Test
    @DisplayName("Tests statement transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        StatementEntity statementEntity = statementTransformer.transform(new Statement());

        assertNotNull(statementEntity);
        assertNull(statementEntity.getData().getEtag());
        assertNull(statementEntity.getData().getKind());
        assertEquals(new HashMap<>(), statementEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests statement transformer with populated object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {
        Statement statement = new Statement();
        statement.setEtag("etag");
        statement.setKind("kind");
        statement.setLinks(new HashMap<>());
        statement.setHasAgreedToLegalStatements(false);
        statement.setLegalStatements(getPopulatedMapStatement());

        StatementEntity statementEntity = statementTransformer.transform(statement);

        assertNotNull(statementEntity);
        assertEquals("etag", statementEntity.getData().getEtag());
        assertEquals("kind", statementEntity.getData().getKind());
        assertEquals(new HashMap<>(), statementEntity.getData().getLinks());
        assertFalse(statementEntity.getData().getHasAgreedToLegalStatements());
        assertEquals("def", statementEntity.getData().getLegalStatements().get("abc"));
        assertEquals("jkl", statementEntity.getData().getLegalStatements().get("ghi"));
    }

    @Test
    @DisplayName("Tests statement transformer with populated object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {
        StatementEntity statementEntity = new StatementEntity();
        StatementDataEntity statementDataEntity = new StatementDataEntity();
        statementDataEntity.setEtag("etag");
        statementDataEntity.setKind("kind");
        statementDataEntity.setLinks(new HashMap<>());
        statementDataEntity.setHasAgreedToLegalStatements(true);
        statementDataEntity.setLegalStatements(getPopulatedMapStatement());
        statementEntity.setData(statementDataEntity);

        Statement statement = statementTransformer.transform(statementEntity);

        assertNotNull(statement);
        assertEquals("etag", statement.getEtag());
        assertEquals("kind", statement.getKind());
        assertEquals(new HashMap<>(), statement.getLinks());
        assertTrue(statement.getHasAgreedToLegalStatements());
        assertEquals("def", statement.getLegalStatements().get("abc"));
        assertEquals("jkl", statement.getLegalStatements().get("ghi"));
    }

    private Map<String, String> getPopulatedMapStatement() {
        Map<String, String> result = new HashMap<>();
        result.put("abc", "def");
        result.put("ghi", "jkl");
        return result;
    }
}