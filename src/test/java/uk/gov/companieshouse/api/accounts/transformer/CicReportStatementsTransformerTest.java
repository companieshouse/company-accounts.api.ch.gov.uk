package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportStatementsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.ReportStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportStatementsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicReportStatements;
import uk.gov.companieshouse.api.accounts.model.rest.ReportStatements;

public class CicReportStatementsTransformerTest {

    private static final Map<String, String> LINKS = new HashMap<>();
    private static final Boolean HAS_COMPLETED_REPORT_STATEMENTS = true;
    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final String COMPANY_ACTIVITIES_AND_IMPACT = "companyActivitiesAndImpact";
    private static final String CONSULTATION_WITH_STAKEHOLDERS = "consultationWithStakeholders";
    private static final String DIRECTORS_REMUNERATION = "directorsRemuneration";
    private static final String TRANSFER_OF_ASSETS = "transferOfAssets";

    private CicReportStatementsTransformer transformer = new CicReportStatementsTransformer();

    @Test
    @DisplayName("REST to entity transform - fully populated object")
    public void testRestToEntityTransformerWithFullyPopulatedObject() {

        CicReportStatements cicReportStatements = new CicReportStatements();
        cicReportStatements.setHasCompletedReportStatements(HAS_COMPLETED_REPORT_STATEMENTS);
        cicReportStatements.setEtag(ETAG);
        cicReportStatements.setKind(KIND);
        cicReportStatements.setLinks(LINKS);

        ReportStatements reportStatements = new ReportStatements();
        reportStatements.setCompanyActivitiesAndImpact(COMPANY_ACTIVITIES_AND_IMPACT);
        reportStatements.setConsultationWithStakeholders(CONSULTATION_WITH_STAKEHOLDERS);
        reportStatements.setDirectorsRemuneration(DIRECTORS_REMUNERATION);
        reportStatements.setTransferOfAssets(TRANSFER_OF_ASSETS);
        cicReportStatements.setReportStatements(reportStatements);

        CicReportStatementsEntity cicReportStatementsEntity = transformer.transform(cicReportStatements);
        assertNotNull(cicReportStatementsEntity);

        CicReportStatementsDataEntity cicReportStatementsDataEntity = cicReportStatementsEntity.getData();
        assertNotNull(cicReportStatementsDataEntity);
        assertEquals(ETAG, cicReportStatementsDataEntity.getEtag());
        assertEquals(KIND, cicReportStatementsDataEntity.getKind());
        assertEquals(LINKS, cicReportStatementsDataEntity.getLinks());
        assertEquals(HAS_COMPLETED_REPORT_STATEMENTS, cicReportStatementsDataEntity.getHasCompletedReportStatements());

        ReportStatementsEntity reportStatementsEntity = cicReportStatementsDataEntity.getReportStatements();
        assertNotNull(reportStatementsEntity);
        assertEquals(COMPANY_ACTIVITIES_AND_IMPACT, reportStatementsEntity.getCompanyActivitiesAndImpact());
        assertEquals(CONSULTATION_WITH_STAKEHOLDERS, reportStatementsEntity.getConsultationWithStakeholders());
        assertEquals(DIRECTORS_REMUNERATION, reportStatementsEntity.getDirectorsRemuneration());
        assertEquals(TRANSFER_OF_ASSETS, reportStatementsEntity.getTransferOfAssets());
    }

    @Test
    @DisplayName("REST to entity transform - no report statements")
    public void testRestToEntityTransformerWithoutNestedReportStatements() {

        CicReportStatements cicReportStatements = new CicReportStatements();
        cicReportStatements.setHasCompletedReportStatements(HAS_COMPLETED_REPORT_STATEMENTS);
        cicReportStatements.setEtag(ETAG);
        cicReportStatements.setKind(KIND);
        cicReportStatements.setLinks(LINKS);

        CicReportStatementsEntity cicReportStatementsEntity = transformer.transform(cicReportStatements);
        assertNotNull(cicReportStatementsEntity);

        CicReportStatementsDataEntity cicReportStatementsDataEntity = cicReportStatementsEntity.getData();
        assertNotNull(cicReportStatementsDataEntity);
        assertEquals(ETAG, cicReportStatementsDataEntity.getEtag());
        assertEquals(KIND, cicReportStatementsDataEntity.getKind());
        assertEquals(LINKS, cicReportStatementsDataEntity.getLinks());
        assertEquals(HAS_COMPLETED_REPORT_STATEMENTS, cicReportStatementsDataEntity.getHasCompletedReportStatements());

        assertNull(cicReportStatementsDataEntity.getReportStatements());
    }

    @Test
    @DisplayName("Entity to REST transform - fully populated object")
    public void testEntityToRestTransformerWithFullyPopulatedObject() {

        CicReportStatementsDataEntity cicReportStatementsDataEntity = new CicReportStatementsDataEntity();
        cicReportStatementsDataEntity.setEtag(ETAG);
        cicReportStatementsDataEntity.setKind(KIND);
        cicReportStatementsDataEntity.setLinks(LINKS);
        cicReportStatementsDataEntity.setHasCompletedReportStatements(HAS_COMPLETED_REPORT_STATEMENTS);

        ReportStatementsEntity reportStatementsEntity = new ReportStatementsEntity();
        reportStatementsEntity.setCompanyActivitiesAndImpact(COMPANY_ACTIVITIES_AND_IMPACT);
        reportStatementsEntity.setConsultationWithStakeholders(CONSULTATION_WITH_STAKEHOLDERS);
        reportStatementsEntity.setDirectorsRemuneration(DIRECTORS_REMUNERATION);
        reportStatementsEntity.setTransferOfAssets(TRANSFER_OF_ASSETS);
        cicReportStatementsDataEntity.setReportStatements(reportStatementsEntity);

        CicReportStatementsEntity cicReportStatementsEntity = new CicReportStatementsEntity();
        cicReportStatementsEntity.setData(cicReportStatementsDataEntity);

        CicReportStatements cicReportStatements = transformer.transform(cicReportStatementsEntity);

        assertNotNull(cicReportStatements);
        assertEquals(ETAG, cicReportStatements.getEtag());
        assertEquals(KIND, cicReportStatements.getKind());
        assertEquals(LINKS, cicReportStatements.getLinks());
        assertEquals(HAS_COMPLETED_REPORT_STATEMENTS, cicReportStatements.getHasCompletedReportStatements());

        ReportStatements reportStatements = cicReportStatements.getReportStatements();
        assertNotNull(reportStatements);
        assertEquals(COMPANY_ACTIVITIES_AND_IMPACT, reportStatements.getCompanyActivitiesAndImpact());
        assertEquals(CONSULTATION_WITH_STAKEHOLDERS, reportStatements.getConsultationWithStakeholders());
        assertEquals(DIRECTORS_REMUNERATION, reportStatements.getDirectorsRemuneration());
        assertEquals(TRANSFER_OF_ASSETS, reportStatements.getTransferOfAssets());
    }

    @Test
    @DisplayName("Entity to REST transform - no report statements")
    public void testEntityToRestTransformerWithoutNestedReportStatements() {

        CicReportStatementsDataEntity cicReportStatementsDataEntity = new CicReportStatementsDataEntity();
        cicReportStatementsDataEntity.setEtag(ETAG);
        cicReportStatementsDataEntity.setKind(KIND);
        cicReportStatementsDataEntity.setLinks(LINKS);
        cicReportStatementsDataEntity.setHasCompletedReportStatements(HAS_COMPLETED_REPORT_STATEMENTS);

        CicReportStatementsEntity cicReportStatementsEntity = new CicReportStatementsEntity();
        cicReportStatementsEntity.setData(cicReportStatementsDataEntity);

        CicReportStatements cicReportStatements = transformer.transform(cicReportStatementsEntity);

        assertNotNull(cicReportStatements);
        assertEquals(ETAG, cicReportStatements.getEtag());
        assertEquals(KIND, cicReportStatements.getKind());
        assertEquals(LINKS, cicReportStatements.getLinks());
        assertEquals(HAS_COMPLETED_REPORT_STATEMENTS, cicReportStatements.getHasCompletedReportStatements());

        assertNull(cicReportStatements.getReportStatements());
    }
}
