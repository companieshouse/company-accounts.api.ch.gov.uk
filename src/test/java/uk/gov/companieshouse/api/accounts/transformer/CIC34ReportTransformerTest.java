package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.CIC34ReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CIC34ReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CIC34Report;

public class CIC34ReportTransformerTest {

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final String COMPANY_ACTIVITIES_AND_IMPACT = "companyActivitiesAndImpact";
    private static final String CONSULTATION_WITH_STAKEHOLDERS = "consultationWithStakeholders";
    private static final String DIRECTORS_REMUNERATION = "directorsRemuneration";
    private static final String TRANSFER_OF_ASSETS = "transferOfAssets";

    private CIC34ReportTransformer transformer = new CIC34ReportTransformer();

    @Test
    @DisplayName("Tests the CIC34 report transformer maps a populated REST object to a database entity")
    public void testRestToEntityTransformerWithPopulatedObject() {

        CIC34Report cic34Report = new CIC34Report();

        cic34Report.setEtag(ETAG);
        cic34Report.setKind(KIND);
        cic34Report.setCompanyActivitiesAndImpact(COMPANY_ACTIVITIES_AND_IMPACT);
        cic34Report.setConsultationWithStakeholders(CONSULTATION_WITH_STAKEHOLDERS);
        cic34Report.setDirectorsRemuneration(DIRECTORS_REMUNERATION);
        cic34Report.setTransferOfAssets(TRANSFER_OF_ASSETS);
        cic34Report.setLinks(new HashMap<>());

        CIC34ReportEntity cic34ReportEntity = transformer.transform(cic34Report);
        assertNotNull(cic34ReportEntity);

        CIC34ReportDataEntity cic34ReportDataEntity = cic34ReportEntity.getData();

        assertEquals(ETAG, cic34ReportDataEntity.getEtag());
        assertEquals(KIND, cic34ReportDataEntity.getKind());
        assertEquals(COMPANY_ACTIVITIES_AND_IMPACT, cic34ReportDataEntity.getCompanyActivitiesAndImpact());
        assertEquals(CONSULTATION_WITH_STAKEHOLDERS, cic34ReportDataEntity.getConsultationWithStakeholders());
        assertEquals(DIRECTORS_REMUNERATION, cic34ReportDataEntity.getDirectorsRemuneration());
        assertEquals(TRANSFER_OF_ASSETS, cic34ReportDataEntity.getTransferOfAssets());
        assertEquals(new HashMap<>(), cic34ReportDataEntity.getLinks());
    }

    @Test
    @DisplayName("Tests the CIC34 report transformer maps a populated database entity to a REST object")
    public void testEntityToRestTransformerWithPopulatedObject() {

        CIC34ReportEntity cic34ReportEntity = new CIC34ReportEntity();
        CIC34ReportDataEntity cic34ReportDataEntity = new CIC34ReportDataEntity();

        cic34ReportDataEntity.setEtag(ETAG);
        cic34ReportDataEntity.setKind(KIND);
        cic34ReportDataEntity.setCompanyActivitiesAndImpact(COMPANY_ACTIVITIES_AND_IMPACT);
        cic34ReportDataEntity.setConsultationWithStakeholders(CONSULTATION_WITH_STAKEHOLDERS);
        cic34ReportDataEntity.setDirectorsRemuneration(DIRECTORS_REMUNERATION);
        cic34ReportDataEntity.setTransferOfAssets(TRANSFER_OF_ASSETS);
        cic34ReportDataEntity.setLinks(new HashMap<>());
        cic34ReportEntity.setData(cic34ReportDataEntity);

        CIC34Report cic34Report = transformer.transform(cic34ReportEntity);

        assertNotNull(cic34Report);
        assertEquals(ETAG, cic34Report.getEtag());
        assertEquals(KIND, cic34Report.getKind());
        assertEquals(COMPANY_ACTIVITIES_AND_IMPACT, cic34Report.getCompanyActivitiesAndImpact());
        assertEquals(CONSULTATION_WITH_STAKEHOLDERS, cic34Report.getConsultationWithStakeholders());
        assertEquals(DIRECTORS_REMUNERATION, cic34Report.getDirectorsRemuneration());
        assertEquals(TRANSFER_OF_ASSETS, cic34Report.getTransferOfAssets());
        assertEquals(new HashMap<>(), cic34Report.getLinks());
    }
}
