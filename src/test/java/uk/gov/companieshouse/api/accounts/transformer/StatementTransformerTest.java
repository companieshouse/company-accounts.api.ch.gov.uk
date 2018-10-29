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

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final String STATEMENT_KEY_1 = "statement1";
    private static final String STATEMENT_KEY_2 = "statement2";
    private static final String STATEMENT_DESCRIPTION_FOR_KEY_1 = "Statement description for key 1";
    private static final String STATEMENT_DESCRIPTION_FOR_KEY_2 = "Statement description for key 2";

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
        statement.setEtag(ETAG);
        statement.setKind(KIND);
        statement.setLinks(new HashMap<>());
        statement.setHasAgreedToLegalStatements(false);
        statement.setLegalStatements(getPopulatedMapStatement());

        StatementEntity statementEntity = statementTransformer.transform(statement);

        assertNotNull(statementEntity);
        assertEquals(ETAG, statementEntity.getData().getEtag());
        assertEquals(KIND, statementEntity.getData().getKind());
        assertEquals(new HashMap<>(), statementEntity.getData().getLinks());
        assertFalse(statementEntity.getData().getHasAgreedToLegalStatements());
        assertEquals(STATEMENT_DESCRIPTION_FOR_KEY_1,
            statementEntity.getData().getLegalStatements().get(STATEMENT_KEY_1));

        assertEquals(STATEMENT_DESCRIPTION_FOR_KEY_2,
            statementEntity.getData().getLegalStatements().get(STATEMENT_KEY_2));
    }

    @Test
    @DisplayName("Tests statement transformer with populated object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {
        StatementEntity statementEntity = new StatementEntity();
        StatementDataEntity statementDataEntity = new StatementDataEntity();
        statementDataEntity.setEtag(ETAG);
        statementDataEntity.setKind(KIND);
        statementDataEntity.setLinks(new HashMap<>());
        statementDataEntity.setHasAgreedToLegalStatements(true);
        statementDataEntity.setLegalStatements(getPopulatedMapStatement());
        statementEntity.setData(statementDataEntity);

        Statement statement = statementTransformer.transform(statementEntity);

        assertNotNull(statement);
        assertEquals(ETAG, statement.getEtag());
        assertEquals(KIND, statement.getKind());
        assertEquals(new HashMap<>(), statement.getLinks());
        assertTrue(statement.getHasAgreedToLegalStatements());
        assertEquals(STATEMENT_DESCRIPTION_FOR_KEY_1,
            statement.getLegalStatements().get(STATEMENT_KEY_1));

        assertEquals(STATEMENT_DESCRIPTION_FOR_KEY_2,
            statement.getLegalStatements().get(STATEMENT_KEY_2));
    }

    private Map<String, String> getPopulatedMapStatement() {
        Map<String, String> result = new HashMap<>();
        result.put(STATEMENT_KEY_1, STATEMENT_DESCRIPTION_FOR_KEY_1);
        result.put(STATEMENT_KEY_2, STATEMENT_DESCRIPTION_FOR_KEY_2);
        return result;
    }
}