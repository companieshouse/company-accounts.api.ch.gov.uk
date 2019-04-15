package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CicReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;

public class CicReportTransformerTest {

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final Map<String, String> LINKS = new HashMap<>();

    private CicReportTransformer transformer = new CicReportTransformer();

    @Test
    @DisplayName("Rest to entity transform")
    void restToEntityTransform() {

        CicReport cicReport = new CicReport();
        cicReport.setEtag(ETAG);
        cicReport.setKind(KIND);
        cicReport.setLinks(LINKS);

        CicReportEntity cicReportEntity = transformer.transform(cicReport);
        assertNotNull(cicReportEntity);

        CicReportDataEntity cicReportDataEntity = cicReportEntity.getData();
        assertNotNull(cicReportDataEntity);
        assertEquals(ETAG, cicReportDataEntity.getEtag());
        assertEquals(KIND, cicReportDataEntity.getKind());
        assertEquals(LINKS, cicReportDataEntity.getLinks());
    }

    @Test
    @DisplayName("Entity to rest transform")
    void entityToRestTransform() {

        CicReportDataEntity cicReportDataEntity = new CicReportDataEntity();
        cicReportDataEntity.setEtag(ETAG);
        cicReportDataEntity.setKind(KIND);
        cicReportDataEntity.setLinks(LINKS);

        CicReportEntity cicReportEntity = new CicReportEntity();
        cicReportEntity.setData(cicReportDataEntity);

        CicReport cicReport = transformer.transform(cicReportEntity);
        assertNotNull(cicReport);
        assertEquals(ETAG, cicReport.getEtag());
        assertEquals(KIND, cicReport.getKind());
        assertEquals(LINKS, cicReport.getLinks());
    }
}
