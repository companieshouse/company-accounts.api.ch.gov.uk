package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.AdditionalInformationDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.loanstodirectors.AdditionalInformationEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.loanstodirectors.AdditionalInformation;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoansToDirectorsAdditionalInformationTransformerTest {

    private static final String DETAILS = "details";

    private LoansToDirectorsAdditionalInformationTransformer transformer =
            new LoansToDirectorsAdditionalInformationTransformer();

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
