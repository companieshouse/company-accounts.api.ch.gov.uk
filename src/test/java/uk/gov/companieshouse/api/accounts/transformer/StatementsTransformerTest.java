package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.StatementsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.StatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.Statements;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class StatementsTransformerTest {
    private static final String ADDITIONAL_INFORMATION = "additionalInfo";
    private static final String COMPANY_POLICY_ON_DISABLED_EMPLOYEES = "companyPolicy";
    private static final String POLITICAL_AND_CHARITABLE_DONATIONS = "politicalDonations";
    private static final String PRINCIPAL_ACTIVITIES = "principalActivities";

    private final StatementsTransformer transformer = new StatementsTransformer();

    @Test
    @DisplayName("Transform REST object to entity")
    void restToEntity() {
        Statements statements = new Statements();

        statements.setAdditionalInformation(ADDITIONAL_INFORMATION);
        statements.setCompanyPolicyOnDisabledEmployees(COMPANY_POLICY_ON_DISABLED_EMPLOYEES);
        statements.setPoliticalAndCharitableDonations(POLITICAL_AND_CHARITABLE_DONATIONS);
        statements.setPrincipalActivities(PRINCIPAL_ACTIVITIES);

        StatementsEntity statementsEntity = transformer.transform(statements);

        assertNotNull(statementsEntity);
        assertNotNull(statementsEntity.getData());
        assertEquals(ADDITIONAL_INFORMATION, statementsEntity.getData().getAdditionalInformation());
        assertEquals(COMPANY_POLICY_ON_DISABLED_EMPLOYEES, statementsEntity.
                getData().getCompanyPolicyOnDisabledEmployees());
        assertEquals(POLITICAL_AND_CHARITABLE_DONATIONS, statementsEntity.
                getData().getPoliticalAndCharitableDonations());
        assertEquals(PRINCIPAL_ACTIVITIES, statementsEntity.
                getData().getPrincipalActivities());
    }

    @Test
    @DisplayName("Transform entity to REST object")
    void entityToRest() {
        StatementsDataEntity statementsDataEntity = new StatementsDataEntity();

        statementsDataEntity.setAdditionalInformation(ADDITIONAL_INFORMATION);
        statementsDataEntity.setCompanyPolicyOnDisabledEmployees(COMPANY_POLICY_ON_DISABLED_EMPLOYEES);
        statementsDataEntity.setPoliticalAndCharitableDonations(POLITICAL_AND_CHARITABLE_DONATIONS);
        statementsDataEntity.setPrincipalActivities(PRINCIPAL_ACTIVITIES);

        StatementsEntity statementsEntity = new StatementsEntity();
        statementsEntity.setData(statementsDataEntity);

        Statements statements = transformer.transform(statementsEntity);

        assertNotNull(statements);
        assertEquals(ADDITIONAL_INFORMATION, statements.getAdditionalInformation());
        assertEquals(COMPANY_POLICY_ON_DISABLED_EMPLOYEES, statements.getCompanyPolicyOnDisabledEmployees());
        assertEquals(POLITICAL_AND_CHARITABLE_DONATIONS, statements.getPoliticalAndCharitableDonations());
        assertEquals(PRINCIPAL_ACTIVITIES, statements.getPrincipalActivities());
    }
}
