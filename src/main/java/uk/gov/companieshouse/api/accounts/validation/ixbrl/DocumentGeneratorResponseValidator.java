package uk.gov.companieshouse.api.accounts.validation.ixbrl;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.DocumentGeneratorResponse;
import uk.gov.companieshouse.api.accounts.model.ixbrl.documentgenerator.Links;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class DocumentGeneratorResponseValidator {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String LOG_MESSAGE_KEY = "message";
    private static final String LOG_DOC_GENERATOR_RESPONSE_INVALID_MESSAGE_KEY =
        "FilingServiceImpl: Document generator response invalid";
    private static final String PERIOD_END_ON = "period_end_on";


    /**
     * Check if the document generator response contains the information needed to build the filing
     * model: Description, period end on (within description values) and the ixbrl location are
     * needed.
     *
     * @param response Contains the document generator response.
     * @return true if the response contains all the needed information.
     */
    public boolean isDocumentGeneratorResponseValid(DocumentGeneratorResponse response) {
        boolean isDocGeneratorResponseValid = true;

        LOGGER.info("DocumentGeneratorResponseValidator: validating document generator response");

        if (!isPeriodEndOnInDocGeneratorResponse(response)) {
            isDocGeneratorResponseValid = false;
        }

        if (!isDescriptionInDocGeneratorResponse(response)) {
            isDocGeneratorResponseValid = false;
        }

        if (!isIxbrlInDocGeneratorResponse(response)) {
            isDocGeneratorResponseValid = false;
        }

        LOGGER.info("DocumentGeneratorResponseValidator: validation has finished");

        return isDocGeneratorResponseValid;
    }

    /**
     * Checks if the document generator response contains the ixbrl location.
     *
     * @param response Contains the document generator response.
     * @return true when ixbrl location is not null nor blank.
     */
    private boolean isIxbrlInDocGeneratorResponse(DocumentGeneratorResponse response) {

        if (StringUtils.isBlank(getIxbrlLocationFromDocGeneratorResponse(response))) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put(LOG_MESSAGE_KEY,
                "The Ixbrl location has not been set in Document Generator Response");

            LOGGER.error(LOG_DOC_GENERATOR_RESPONSE_INVALID_MESSAGE_KEY, logMap);

            return false;
        }
        return true;
    }

    /**
     * Checks if the document generator response contains account's description.
     *
     * @param response Contains the document generator response.
     * @return true when the description not null nor blank.
     */
    private boolean isDescriptionInDocGeneratorResponse(DocumentGeneratorResponse response) {

        if (StringUtils.isBlank(response.getDescription())) {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put(LOG_MESSAGE_KEY,
                "The description has not been in the Document Generator Response");
            LOGGER.error(LOG_DOC_GENERATOR_RESPONSE_INVALID_MESSAGE_KEY, logMap);

            return false;
        }
        return true;
    }

    /**
     * Checks the document generator response contains the period end on within the description
     * values.
     *
     /Users/mcabrerahernandez/Documents/GITHUB/GitHub Commands.md     * @param response Contains the document generator response.
     * @return true when the description values contains the period_end_on key; and its value is not
     * null nor blank.
     */
    private boolean isPeriodEndOnInDocGeneratorResponse(DocumentGeneratorResponse response) {

        if (response.getDescriptionValues() == null ||
            !response.getDescriptionValues().containsKey(PERIOD_END_ON) ||
            StringUtils.isBlank(response.getDescriptionValues().get(PERIOD_END_ON))) {

            Map<String, Object> logMap = new HashMap<>();
            logMap.put(LOG_MESSAGE_KEY,
                "Period end on has not been set within the description_values in the Document Generator Response");

            LOGGER.error(LOG_DOC_GENERATOR_RESPONSE_INVALID_MESSAGE_KEY, logMap);
            return false;
        }

        return true;
    }

    /**
     * Get the ixbrl location stored in the document generator response values.
     *
     * @param response Contains the document generator response.
     * @return the location or null if ixbrl location has not been set.
     */
    private String getIxbrlLocationFromDocGeneratorResponse(DocumentGeneratorResponse response) {
        return Optional.of(response)
            .map(DocumentGeneratorResponse::getLinks)
            .map(Links::getLocation)
            .orElse(null);
    }
}
