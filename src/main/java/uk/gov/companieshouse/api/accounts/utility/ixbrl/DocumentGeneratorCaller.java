package uk.gov.companieshouse.api.accounts.utility.ixbrl;

import org.springframework.stereotype.Component;

/**
 * Class to call the document generator to obtain the ixbrl location.
 * Since the functionality has not been implemented yet, (STORY SFA-595), it returns an empty string.
 *
 * This class will change to call the new end point and it will return the s3 ixbrl location.
 */
@Component
public class DocumentGeneratorCaller {

    public String generateIxbrl() {
        return "";
    }
}
