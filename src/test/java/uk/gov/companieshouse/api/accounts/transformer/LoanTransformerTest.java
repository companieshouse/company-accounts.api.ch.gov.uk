package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanBreakdownResourceEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.LoanEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.Loan;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LoanTransformerTest {

    @Mock
    private LoanBreakdownResourceEntity loanBreakdownResourceEntity;

    private static final String DIRECTOR_NAME = "name";
    private static final String DESCRIPTION = "description";

    private static final String LOAN_ID = "loanId";
    private static final String LOAN_LINK = "loanLink";

    private LoanTransformer transformer = new LoanTransformer();

    @BeforeEach
    private void setup() {
        LoanBreakdownResourceEntity loanBreakdownResourceEntity = new LoanBreakdownResourceEntity();
        loanBreakdownResourceEntity.setAdvancesCreditsMade(1L);
        loanBreakdownResourceEntity.setAdvancesCreditsRepaid(1L);
        loanBreakdownResourceEntity.setBalanceAtPeriodEnd(1L);
        loanBreakdownResourceEntity.setBalanceAtPeriodStart(1L);
    }

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntity() {

        Loan loan = new Loan();
        loan.setDirectorName(DIRECTOR_NAME);
        loan.setDescription(DESCRIPTION);
        loan.setBreakdown(loanBreakdownResourceEntity);


        LoanEntity loanEntity = transformer.transform(loan);

        assertNotNull(loanEntity);
        assertNotNull(loanEntity.getData());
        assertNotNull(loanEntity.getData().getDirectorName());
        assertNotNull(loanEntity.getData().getDescription());
        assertNotNull(loanEntity.getData().getBreakdown());
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRest() {

        Loan loan = transformer.transform(getLoanEntity());

        assertNotNull(loan);
        assertRestFieldsSet(loan);
    }

    @Test
    @DisplayName("Transform entity array to rest object array")
    void entityArrayToRestArray() {

        LoanEntity[] entities = new LoanEntity[]{getLoanEntity(), getLoanEntity()};

        Loan[] loans = transformer.transform(entities);

        assertNotNull(loans);
        assertEquals(2, loans.length);
        assertRestFieldsSet(loans[0]);
        assertRestFieldsSet(loans[1]);
    }

    private LoanEntity getLoanEntity() {

        LoanDataEntity loanDataEntity = new LoanDataEntity();
        loanDataEntity.setDirectorName(DIRECTOR_NAME);
        loanDataEntity.setDescription(DESCRIPTION);
        loanDataEntity.setBreakdown(loanBreakdownResourceEntity);

        LoanEntity loanEntity = new LoanEntity();
        loanEntity.setData(loanDataEntity);

        return loanEntity;
    }

    private void assertRestFieldsSet(Loan loan) {
        assertEquals(DIRECTOR_NAME, loan.getDirectorName());
        assertEquals(DESCRIPTION, loan.getDescription());
        assertEquals(loanBreakdownResourceEntity, loan.getBreakdown());
    }
}