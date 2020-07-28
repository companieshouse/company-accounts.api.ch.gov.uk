package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoansToDirectorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoansToDirectorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.LoansToDirectors;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoansToDirectorsTransformerTest {

    private static final Map<String, String> LOANS = new HashMap<>();

    private static final String LOAN_ID = "loanId";
    private static final String LOAN_LINK = "loanLink";

    private LoansToDirectorsTransformer transformer = new LoansToDirectorsTransformer();

    @BeforeEach
    private void setup() {
        LOANS.put(LOAN_ID, LOAN_LINK);
    }

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntity() {

        LoansToDirectors loansToDirectors = new LoansToDirectors();
        loansToDirectors.setLoans(LOANS);

        LoansToDirectorsEntity loansToDirectorsEntity = transformer.transform(loansToDirectors);

        assertNotNull(loansToDirectorsEntity);
        assertNotNull(loansToDirectorsEntity.getData());
        assertNotNull(loansToDirectorsEntity.getData().getLoans());
        assertEquals(LOAN_LINK, loansToDirectorsEntity.getData().getLoans().get(LOAN_ID));
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRest() {
        LoansToDirectorsDataEntity loansToDirectorsDataEntity = new LoansToDirectorsDataEntity();
        loansToDirectorsDataEntity.setLoans(LOANS);

        LoansToDirectorsEntity loansToDirectorsEntity = new LoansToDirectorsEntity();
        loansToDirectorsEntity.setData(loansToDirectorsDataEntity);

        LoansToDirectors loansToDirectors = transformer.transform(loansToDirectorsEntity);

        assertNotNull(loansToDirectors);
        assertNotNull(loansToDirectors.getLoans());
        assertEquals(LOAN_LINK, loansToDirectors.getLoans().get(LOAN_ID));
    }
}
