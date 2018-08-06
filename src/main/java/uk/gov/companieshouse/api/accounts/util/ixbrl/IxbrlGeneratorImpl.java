package uk.gov.companieshouse.api.accounts.util.ixbrl;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;

@Component
public class IxbrlGeneratorImpl implements IxbrlGenerator {

    /**
     * {@inheritDoc}
     * @return
     * @throws IOException
     */
    @Override
    public  String generateIXBRL(DocumentGeneratorConnection generatorConnection) throws IOException {

        return callDocumentRenderService(generatorConnection);
    }


    /**
     * Get the connection information needed to call the service that generates the ixbrl.
     *
     * @return {@link HttpURLConnection}
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
     * Generates the ixbrl by calling the document render service.
     *
     * @return The location where the service has stored the generated ixbrl.
     */
    private String callDocumentRenderService(DocumentGeneratorConnection generatorConnection)
        throws IOException {
        HttpURLConnection connection = createConnectionForAccount(generatorConnection);

        String requestBody = generatorConnection.getRequestBody();
        try {
            if (requestBody != null) {
                try (DataOutputStream out = new DataOutputStream(
                    connection.getOutputStream())) {
                    out.write(requestBody.getBytes(StandardCharsets.UTF_8));
                    out.flush();
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_CREATED) {
                return connection.getHeaderField("Location");
            }
        } finally {
            connection.disconnect();
        }

        return null;
    }

}