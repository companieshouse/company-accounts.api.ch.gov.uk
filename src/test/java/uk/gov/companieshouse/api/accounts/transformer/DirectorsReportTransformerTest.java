package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.DirectorsReport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DirectorsReportTransformerTest {

    private static final String ADDITIONAL_INFORMATION = "Additional info";
    private static final String COMPANY_POLICY = "company policy on disabled employees";
    private static final String POLITICAL_AND_CHARITABLE_DONATIONS = "political and charitable donations";
    private static final String PRINCIPAL_ACTIVITIES = "principal activities";

    private DirectorsReportTransformer transformer = new DirectorsReportTransformer();

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntity() {

        DirectorsReport directorsReport = new DirectorsReport();
        directorsReport.setAdditionalInformation(ADDITIONAL_INFORMATION);
        directorsReport.setCompanyPolicyOnDisabledEmployees(COMPANY_POLICY);
        directorsReport.setPoliticalAndCharitableDonations(POLITICAL_AND_CHARITABLE_DONATIONS);
        directorsReport.setPrincipalActivities(PRINCIPAL_ACTIVITIES);

        DirectorsReportEntity directorsReportEntity = transformer.transform(directorsReport);

        assertNotNull(directorsReportEntity);
        assertNotNull(directorsReportEntity.getData());
        assertEquals(ADDITIONAL_INFORMATION, directorsReportEntity.getData().getAdditionalInformation());
        assertEquals(COMPANY_POLICY, directorsReportEntity.getData().getCompanyPolicyOnDisabledEmployees());
        assertEquals(POLITICAL_AND_CHARITABLE_DONATIONS, directorsReportEntity.getData().getPoliticalAndCharitableDonations());
        assertEquals(PRINCIPAL_ACTIVITIES, directorsReportEntity.getData().getPrincipalActivities());
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRest() {
        DirectorsReportDataEntity directorsReportDataEntity = new DirectorsReportDataEntity();
        directorsReportDataEntity.setAdditionalInformation(ADDITIONAL_INFORMATION);
        directorsReportDataEntity.setCompanyPolicyOnDisabledEmployees(COMPANY_POLICY);
        directorsReportDataEntity.setPoliticalAndCharitableDonations(POLITICAL_AND_CHARITABLE_DONATIONS);
        directorsReportDataEntity.setPrincipalActivities(PRINCIPAL_ACTIVITIES);

        DirectorsReportEntity directorsReportEntity = new DirectorsReportEntity();
        directorsReportEntity.setData(directorsReportDataEntity);

        DirectorsReport directorsReport = transformer.transform(directorsReportEntity);

        assertNotNull(directorsReport);
        assertEquals(ADDITIONAL_INFORMATION, directorsReport.getAdditionalInformation());
        assertEquals(COMPANY_POLICY, directorsReport.getCompanyPolicyOnDisabledEmployees());
        assertEquals(POLITICAL_AND_CHARITABLE_DONATIONS, directorsReport.getPoliticalAndCharitableDonations());
        assertEquals(PRINCIPAL_ACTIVITIES, directorsReport.getPrincipalActivities());
    }
}
