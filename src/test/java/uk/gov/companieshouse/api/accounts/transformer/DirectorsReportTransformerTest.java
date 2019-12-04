package uk.gov.companieshouse.api.accounts.transformer;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.directorsreport.DirectorsReportEntity;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class DirectorsReportTransformerTest {

    private static final Map<String, String> DIRECTORS = new HashMap<>();

    private static final String DIRECTOR_ID = "directorId";
    private static final String DIRECTOR_LINK = "directorLink";

    private DirectorsReportTransformer transformer = new DirectorsReportTransformer();

    @BeforeEach
    private void setup() {
        DIRECTORS.put(DIRECTOR_ID, DIRECTOR_LINK);
    }

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntity() {

        DirectorsReport directorsReport = new DirectorsReport();
        directorsReport.setDirectors(DIRECTORS);

        DirectorsReportEntity directorsReportEntity = transformer.transform(directorsReport);

        assertNotNull(directorsReportEntity);
        assertNotNull(directorsReportEntity.getData());
        assertNotNull(directorsReportEntity.getData().getDirectors());
        assertEquals(DIRECTOR_LINK, directorsReportEntity.getData().getDirectors().get(DIRECTOR_ID));
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRest() {
        DirectorsReportDataEntity directorsReportDataEntity = new DirectorsReportDataEntity();
        directorsReportDataEntity.setDirectors(DIRECTORS);

        DirectorsReportEntity directorsReportEntity = new DirectorsReportEntity();
        directorsReportEntity.setData(directorsReportDataEntity);

        DirectorsReport directorsReport = transformer.transform(directorsReportEntity);

        assertNotNull(directorsReport);
        assertNotNull(directorsReport.getDirectors());
        assertEquals(DIRECTOR_LINK, directorsReport.getDirectors().get(DIRECTOR_ID));
    }
}
