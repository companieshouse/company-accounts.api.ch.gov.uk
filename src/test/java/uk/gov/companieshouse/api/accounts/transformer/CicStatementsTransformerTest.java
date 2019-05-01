package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.CicStatementsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.ReportStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicStatements;
import uk.gov.companieshouse.api.accounts.model.rest.ReportStatements;

public class CicStatementsTransformerTest {

    private static final Map<String, String> LINKS = new HashMap<>();
    private static final Boolean HAS_COMPLETED_REPORT_STATEMENTS = true;
    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final String COMPANY_ACTIVITIES_AND_IMPACT = "companyActivitiesAndImpact";
    private static final String CONSULTATION_WITH_STAKEHOLDERS = "consultationWithStakeholders";
    private static final String DIRECTORS_REMUNERATION = "directorsRemuneration";
    private static final String TRANSFER_OF_ASSETS = "transferOfAssets";

    private CicStatementsTransformer transformer = new CicStatementsTransformer();

    @Test
    @DisplayName("REST to entity transform - fully populated object")
    public void testRestToEntityTransformerWithFullyPopulatedObject() {

        CicStatements cicStatements = new CicStatements();
        cicStatements.setHasCompletedReportStatements(HAS_COMPLETED_REPORT_STATEMENTS);
        cicStatements.setEtag(ETAG);
        cicStatements.setKind(KIND);
        cicStatements.setLinks(LINKS);

        ReportStatements reportStatements = new ReportStatements();
        reportStatements.setCompanyActivitiesAndImpact(COMPANY_ACTIVITIES_AND_IMPACT);
        reportStatements.setConsultationWithStakeholders(CONSULTATION_WITH_STAKEHOLDERS);
        reportStatements.setDirectorsRemuneration(DIRECTORS_REMUNERATION);
        reportStatements.setTransferOfAssets(TRANSFER_OF_ASSETS);
        cicStatements.setReportStatements(reportStatements);

        CicStatementsEntity cicStatementsEntity = transformer.transform(cicStatements);
        assertNotNull(cicStatementsEntity);

        CicStatementsDataEntity cicStatementsDataEntity = cicStatementsEntity.getData();
        assertNotNull(cicStatementsDataEntity);
        assertEquals(ETAG, cicStatementsDataEntity.getEtag());
        assertEquals(KIND, cicStatementsDataEntity.getKind());
        assertEquals(LINKS, cicStatementsDataEntity.getLinks());
        assertEquals(HAS_COMPLETED_REPORT_STATEMENTS, cicStatementsDataEntity.getHasCompletedReportStatements());

        ReportStatementsEntity reportStatementsEntity = cicStatementsDataEntity.getReportStatements();
        assertNotNull(reportStatementsEntity);
        assertEquals(COMPANY_ACTIVITIES_AND_IMPACT, reportStatementsEntity.getCompanyActivitiesAndImpact());
        assertEquals(CONSULTATION_WITH_STAKEHOLDERS, reportStatementsEntity.getConsultationWithStakeholders());
        assertEquals(DIRECTORS_REMUNERATION, reportStatementsEntity.getDirectorsRemuneration());
        assertEquals(TRANSFER_OF_ASSETS, reportStatementsEntity.getTransferOfAssets());
    }

    @Test
    @DisplayName("REST to entity transform - no report statements")
    public void testRestToEntityTransformerWithoutNestedReportStatements() {

        CicStatements cicStatements = new CicStatements();
        cicStatements.setHasCompletedReportStatements(HAS_COMPLETED_REPORT_STATEMENTS);
        cicStatements.setEtag(ETAG);
        cicStatements.setKind(KIND);
        cicStatements.setLinks(LINKS);

        CicStatementsEntity cicStatementsEntity = transformer.transform(cicStatements);
        assertNotNull(cicStatementsEntity);

        CicStatementsDataEntity cicStatementsDataEntity = cicStatementsEntity.getData();
        assertNotNull(cicStatementsDataEntity);
        assertEquals(ETAG, cicStatementsDataEntity.getEtag());
        assertEquals(KIND, cicStatementsDataEntity.getKind());
        assertEquals(LINKS, cicStatementsDataEntity.getLinks());
        assertEquals(HAS_COMPLETED_REPORT_STATEMENTS, cicStatementsDataEntity.getHasCompletedReportStatements());

        assertNull(cicStatementsDataEntity.getReportStatements());
    }

    @Test
    @DisplayName("Entity to REST transform - fully populated object")
    public void testEntityToRestTransformerWithFullyPopulatedObject() {

        CicStatementsDataEntity cicStatementsDataEntity = new CicStatementsDataEntity();
        cicStatementsDataEntity.setEtag(ETAG);
        cicStatementsDataEntity.setKind(KIND);
        cicStatementsDataEntity.setLinks(LINKS);
        cicStatementsDataEntity.setHasCompletedReportStatements(HAS_COMPLETED_REPORT_STATEMENTS);

        ReportStatementsEntity reportStatementsEntity = new ReportStatementsEntity();
        reportStatementsEntity.setCompanyActivitiesAndImpact(COMPANY_ACTIVITIES_AND_IMPACT);
        reportStatementsEntity.setConsultationWithStakeholders(CONSULTATION_WITH_STAKEHOLDERS);
        reportStatementsEntity.setDirectorsRemuneration(DIRECTORS_REMUNERATION);
        reportStatementsEntity.setTransferOfAssets(TRANSFER_OF_ASSETS);
        cicStatementsDataEntity.setReportStatements(reportStatementsEntity);

        CicStatementsEntity cicStatementsEntity = new CicStatementsEntity();
        cicStatementsEntity.setData(cicStatementsDataEntity);

        CicStatements cicStatements = transformer.transform(cicStatementsEntity);

        assertNotNull(cicStatements);
        assertEquals(ETAG, cicStatements.getEtag());
        assertEquals(KIND, cicStatements.getKind());
        assertEquals(LINKS, cicStatements.getLinks());
        assertEquals(HAS_COMPLETED_REPORT_STATEMENTS, cicStatements.getHasCompletedReportStatements());

        ReportStatements reportStatements = cicStatements.getReportStatements();
        assertNotNull(reportStatements);
        assertEquals(COMPANY_ACTIVITIES_AND_IMPACT, reportStatements.getCompanyActivitiesAndImpact());
        assertEquals(CONSULTATION_WITH_STAKEHOLDERS, reportStatements.getConsultationWithStakeholders());
        assertEquals(DIRECTORS_REMUNERATION, reportStatements.getDirectorsRemuneration());
        assertEquals(TRANSFER_OF_ASSETS, reportStatements.getTransferOfAssets());
    }

    @Test
    @DisplayName("Entity to REST transform - no report statements")
    public void testEntityToRestTransformerWithoutNestedReportStatements() {

        CicStatementsDataEntity cicStatementsDataEntity = new CicStatementsDataEntity();
        cicStatementsDataEntity.setEtag(ETAG);
        cicStatementsDataEntity.setKind(KIND);
        cicStatementsDataEntity.setLinks(LINKS);
        cicStatementsDataEntity.setHasCompletedReportStatements(HAS_COMPLETED_REPORT_STATEMENTS);

        CicStatementsEntity cicStatementsEntity = new CicStatementsEntity();
        cicStatementsEntity.setData(cicStatementsDataEntity);

        CicStatements cicStatements = transformer.transform(cicStatementsEntity);

        assertNotNull(cicStatements);
        assertEquals(ETAG, cicStatements.getEtag());
        assertEquals(KIND, cicStatements.getKind());
        assertEquals(LINKS, cicStatements.getLinks());
        assertEquals(HAS_COMPLETED_REPORT_STATEMENTS, cicStatements.getHasCompletedReportStatements());

        assertNull(cicStatements.getReportStatements());
    }
}
