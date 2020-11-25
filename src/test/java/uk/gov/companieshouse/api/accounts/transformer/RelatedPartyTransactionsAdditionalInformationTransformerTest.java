package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.AdditionalInformationDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.relatedpartytransactions.AdditionalInformationEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.AdditionalInformation;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RelatedPartyTransactionsAdditionalInformationTransformerTest {

    private static final String DETAILS = "details";

    private RelatedPartyTransactionsAdditionalInformationTransformer transformer =
            new RelatedPartyTransactionsAdditionalInformationTransformer();

    @Test
    @DisplayName("Transform rest object to entity")
    void restToEntity() {

        AdditionalInformation additionalInformation = new AdditionalInformation();
        additionalInformation.setDetails(DETAILS);

        AdditionalInformationEntity additionalInformationEntity = transformer.transform(additionalInformation);

        assertNotNull(additionalInformationEntity);
        assertNotNull(additionalInformationEntity.getData());
        assertEquals(DETAILS, additionalInformationEntity.getData().getDetails());
    }

    @Test
    @DisplayName("Transform entity to rest object")
    void entityToRest() {

        AdditionalInformationDataEntity additionalInformationDataEntity = new AdditionalInformationDataEntity();
        additionalInformationDataEntity.setDetails(DETAILS);

        AdditionalInformationEntity additionalInformationEntity = new AdditionalInformationEntity();
        additionalInformationEntity.setData(additionalInformationDataEntity);

        AdditionalInformation additionalInformation = transformer.transform(additionalInformationEntity);

        assertNotNull(additionalInformation);
        assertEquals(DETAILS, additionalInformation.getDetails());
    }
}