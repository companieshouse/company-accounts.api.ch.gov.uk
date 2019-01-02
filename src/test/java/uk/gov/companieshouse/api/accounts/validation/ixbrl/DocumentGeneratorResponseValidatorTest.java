package uk.gov.companieshouse.api.accounts.validation.ixbrl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorResponse;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.Links;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class DocumentGeneratorResponseValidatorTest {

    private static final String IXBRL_LOCATION = "http://test/ixbrl_bucket_location";
    private static final String PERIOD_END_ON_KEY = "period_end_on";
    private static final String PERIOD_END_ON_VALUE = "2018-01-01";
    private static final String ACCOUNT_DESCRIPTION = "Small full accounts made up to 18 January 2018";

    private DocumentGeneratorResponse documentGeneratorResponse;

    @InjectMocks
    private DocumentGeneratorResponseValidator docGeneratorResponseValidator;

    @BeforeEach
    void setUpBeforeEach() {
        documentGeneratorResponse = new DocumentGeneratorResponse();
    }

    @Test
    @DisplayName("Document generator response validation is successful")
    void shouldPassValidation() {

        documentGeneratorResponse.setLinks(createIxbrlLink(IXBRL_LOCATION));
        documentGeneratorResponse.setDescription(ACCOUNT_DESCRIPTION);
        documentGeneratorResponse
            .setDescriptionValues(createDescriptionValues(PERIOD_END_ON_KEY, PERIOD_END_ON_VALUE));

        assertTrue(docGeneratorResponseValidator
            .isDocumentGeneratorResponseValid(documentGeneratorResponse));
    }

    @Test
    @DisplayName("Document generator response validation fails when Ixbrl link is not set ")
    void shouldFailValidationAsIxbrlLocationNotSet() {

        documentGeneratorResponse.setDescription(ACCOUNT_DESCRIPTION);
        documentGeneratorResponse
            .setDescriptionValues(createDescriptionValues(PERIOD_END_ON_KEY, PERIOD_END_ON_VALUE));

        assertFalse(docGeneratorResponseValidator
            .isDocumentGeneratorResponseValid(documentGeneratorResponse));
    }

    @Test
    @DisplayName("Document generator response validation fails when Ixbrl link value is null")
    void shouldFailValidationIxbrlLocationIsNull() {

        documentGeneratorResponse.setLinks(createIxbrlLink(null));
        documentGeneratorResponse.setDescription(ACCOUNT_DESCRIPTION);
        documentGeneratorResponse
            .setDescriptionValues(createDescriptionValues(PERIOD_END_ON_KEY, PERIOD_END_ON_VALUE));

        assertFalse(docGeneratorResponseValidator
            .isDocumentGeneratorResponseValid(documentGeneratorResponse));
    }

    @Test
    @DisplayName("Document generator response validation fails when description is not")
    void shouldFailValidationAsDescriptionNotSetInDocGeneratorResponse() {

        documentGeneratorResponse.setLinks(createIxbrlLink(IXBRL_LOCATION));
        documentGeneratorResponse
            .setDescriptionValues(createDescriptionValues(PERIOD_END_ON_KEY, PERIOD_END_ON_VALUE));

        assertFalse(docGeneratorResponseValidator
            .isDocumentGeneratorResponseValid(documentGeneratorResponse));
    }

    @Test
    @DisplayName("Document generator response validation fails when period end on is not set")
    void shouldFailValidationAsPeriodEndOnNotSetInDocGeneratorResponse() {

        documentGeneratorResponse.setLinks(createIxbrlLink(IXBRL_LOCATION));
        documentGeneratorResponse.setDescription(ACCOUNT_DESCRIPTION);

        assertFalse(docGeneratorResponseValidator
            .isDocumentGeneratorResponseValid(documentGeneratorResponse));
    }

    @Test
    @DisplayName("Document generator response validation fails when period end on is null")
    void shouldFailValidationAsPeriodEndOnValueIsNullInDocGeneratorResponse() {

        documentGeneratorResponse.setLinks(createIxbrlLink(IXBRL_LOCATION));
        documentGeneratorResponse.setDescription(ACCOUNT_DESCRIPTION);
        documentGeneratorResponse
            .setDescriptionValues(createDescriptionValues(PERIOD_END_ON_KEY, null));

        assertFalse(docGeneratorResponseValidator
            .isDocumentGeneratorResponseValid(documentGeneratorResponse));
    }

    @Test
    @DisplayName("Document generator response validation fails when period end key does not exist")
    void shouldFailValidationAsPeriodEndOnKeyIsNotInDocGeneratorResponse() {

        documentGeneratorResponse.setLinks(createIxbrlLink(IXBRL_LOCATION));
        documentGeneratorResponse.setDescription(ACCOUNT_DESCRIPTION);
        documentGeneratorResponse
            .setDescriptionValues(createDescriptionValues("wrong_key", PERIOD_END_ON_VALUE));

        assertFalse(docGeneratorResponseValidator
            .isDocumentGeneratorResponseValid(documentGeneratorResponse));
    }

    /**
     * Create links object containing the ixbrl location.
     *
     * @param ixbrlLocation Ixbrl location to be set in the links
     * @return
     */
    private Links createIxbrlLink(String ixbrlLocation) {

        Links links = new Links();
        links.setLocation(ixbrlLocation);
        return links;
    }

    /**
     * Create the description values map with the key and value passed in
     *
     * @param keyId key name to use it in the map
     * @param keyValue key value to use in the map
     * @return
     */
    private Map<String, String> createDescriptionValues(String keyId, String keyValue) {

        Map<String, String> descriptionValues = new HashMap<>();
        descriptionValues.put(keyId, keyValue);
        return descriptionValues;
    }
}
