package uk.gov.companieshouse.api.accounts.util.ixbrl;

import java.io.IOException;

/**
 * Interface to call the Document Render Service directly.
 *
 * This interface and its implementations need to be DELETED when the functionality to call the
 * Document Render Service is build in the Java SDK.
 **/
public interface IxbrlGenerator {

    /**
     * Returns the location of the ixbrl document that has been generated and stored in a bucket by the document
     * render service.
     *
     * @param documentGeneratorConnection - document generator settings. eg. URL, method, body, etc
     * @return the location of the generated ixbrl document (location and name)
     * @throws IOException
     */
    String generateIXBRL(DocumentGeneratorConnection documentGeneratorConnection)
        throws IOException;
}
