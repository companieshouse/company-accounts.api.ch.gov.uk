package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class FileTransferTool {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private final HttpURLConnectionHandler httpURLConnectionHandler;

    @Autowired
    FileTransferTool(HttpURLConnectionHandler httpURLConnectionHandler) {
        this.httpURLConnectionHandler = httpURLConnectionHandler;
    }

    /**
     * It will download a file from a public location using a httpUrlConnection. The
     * httpUrlConnection will use proxy settings if they have been set in the environment
     * variables.
     *
     * @param fileLocation - Contains the public location of the file.
     * @return {@link String} containing the downloaded file. Return null if file not downloaded.
     */
    public String downloadFileFromPublicLocation(String fileLocation) {

        LOGGER.info("FileTransferTool: Start process to download file from location");

        String downloadedFile = null;

        HttpURLConnection httpURLConnection = openAndSetHttpUrlConnection(fileLocation);

        if (httpURLConnection != null) {
            downloadedFile = downloadFileUsingHttpUrlConnection(httpURLConnection);
        }

        LOGGER.info("FileTransferTool: Process to download file has finished");

        return downloadedFile;
    }

    private HttpURLConnection openAndSetHttpUrlConnection(String fileLocation) {

        try {
            HttpURLConnection httpConn = httpURLConnectionHandler.openConnection(fileLocation);
            httpConn.setRequestMethod("GET");

            LOGGER.info(
                "FileTransferTool: openAndSetHttpUrlConnection has successfully set a HttpURLConnection");

            return httpConn;
        } catch (IOException ex) {
            logError(ex,
                "FileTransfer: opening connection has failed",
                "Fail to download file as error occurred when opening connection for location: "
                    + fileLocation);
        }

        return null;
    }

    /**
     * Downloads the file from a public location when the correct http code is returned.
     *
     * @param httpURLConnection
     * @return {@link String} containing the downloaded file.
     */
    private String downloadFileUsingHttpUrlConnection(HttpURLConnection httpURLConnection) {

        String downloadedFile = null;

        try {
            int responseCode = httpURLConnection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {

                try (InputStream response = httpURLConnection.getInputStream()) {
                    downloadedFile = new String(IOUtils.toByteArray(response));
                    LOGGER.info(
                        "FileTransferTool: downloadFileUsingHttpUrlConnection has successfully download the file's content");
                }

            } else {
                logError(null,
                    "FileTransfer: wrong response code",
                    "Fail to download file as wrong response code has been returned: "
                        + responseCode);
            }

        } catch (IOException ex) {
            logError(ex,
                "FileTransfer: Exception thrown when downloading file",
                "Fail to download file as exception thrown when getting the response code or downloading the file");
        } finally {
            httpURLConnection.disconnect();
        }

        return downloadedFile;
    }

    private void logError(IOException exception, String errorKey, String errorMessageMessage) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("message", errorMessageMessage);
        LOGGER.error(errorKey, exception, logMap);
    }
}
