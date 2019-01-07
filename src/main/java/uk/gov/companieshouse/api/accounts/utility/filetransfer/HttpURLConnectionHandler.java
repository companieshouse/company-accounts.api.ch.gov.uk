package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import java.io.IOException;
import java.net.HttpURLConnection;

public interface HttpURLConnectionHandler {

    /**
     * Create HttpURLConnection using the url file location provided.
     *
     * @param urlFileLocation - location of the file to be downloaded
     * @return - {@link HttpURLConnection}
     * @throws IOException
     */
    HttpURLConnection openConnection(String urlFileLocation) throws IOException;
}
