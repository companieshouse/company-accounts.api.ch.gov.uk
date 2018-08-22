package uk.gov.companieshouse.api.accounts.util.ixbrl.ixbrlgenerator;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class IxbrlGeneratorImpl implements IxbrlGenerator {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String LOG_ERROR_KEY = "error";
    private static final String LOG_MESSAGE_KEY = "message";

    /**
     * {@inheritDoc}
     *
     * @return
     * @throws IOException
     */
    @Override
    public String generateIXBRL(DocumentGeneratorConnection generatorConnection)
        throws IOException {

        return callDocumentRenderService(generatorConnection);
    }


    /**
     * Generates the ixbrl by calling the document render service.
     *
     * @param generatorConnection - the settings to call the document render service
     * @return The location where the service has stored the generated ixbrl.
     * @throws IOException
     */
    private String callDocumentRenderService(DocumentGeneratorConnection generatorConnection)
        throws IOException {

        String requestBody = generatorConnection.getRequestBody();
        if (requestBody != null) {
            HttpURLConnection connection = createConnectionForAccount(generatorConnection);
            try {
                try (DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream())) {
                    out.write(requestBody.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }

                int responseCode = connection.getResponseCode();
                if (connection.getResponseCode() == HttpURLConnection.HTTP_CREATED) {
                    return connection.getHeaderField("Location");
                } else {
                    logRenderServiceErrorResponse(generatorConnection, responseCode);
                }
            } finally {
                connection.disconnect();
            }
        } else {
            Map<String, Object> logMap = new HashMap<>();
            logMap.put(LOG_ERROR_KEY, "Document render service request body empty");
            logMap.put(LOG_MESSAGE_KEY,
                "Request Body is empty. The Document Render Service cannot be called with a empty request body");
            LOGGER.error("Document render service request body empty", logMap);
        }

        return null;
    }

    /**
     * Set the http connection information needed to call the document render service .
     *
     * @param generatorConnection - the settings to call the document render service
     * @return {@link HttpURLConnection}
     * @throws IOException
     */
    private HttpURLConnection createConnectionForAccount(DocumentGeneratorConnection generatorConnection)
        throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(
            generatorConnection.getServiceURL())
            .openConnection();

        connection.setRequestMethod(generatorConnection.getRequestMethod());
        connection
            .setRequestProperty("Authorization", generatorConnection.getAuthorizationProperty());
        connection.setRequestProperty("assetId", generatorConnection.getAssetId());
        connection.setRequestProperty("Content-Type", generatorConnection.getContentType());
        connection.setRequestProperty("Accept", generatorConnection.getAcceptType());
        connection.setRequestProperty("Location", generatorConnection.getLocation());
        connection.setRequestProperty("templateName", generatorConnection.getTemplateName());
        connection.setDoOutput(true);

        return connection;
    }

    /**
     * Log the error code returned from the document render service
     *
     * @param responseCode
     * @param connection
     */
    private void logRenderServiceErrorResponse(DocumentGeneratorConnection connection,
        int responseCode) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put(LOG_ERROR_KEY, "Error response received from document render service");
        logMap.put(LOG_MESSAGE_KEY, "Response code: " + responseCode);
        logMap.put("asset-id", connection.getAssetId());
        logMap.put("content-type", connection.getContentType());
        logMap.put("accept-type", connection.getAcceptType());
        logMap.put("location", connection.getLocation());
        logMap.put("template-name", connection.getTemplateName());

        LOGGER.error("Error response received from document render service", logMap);
    }
}