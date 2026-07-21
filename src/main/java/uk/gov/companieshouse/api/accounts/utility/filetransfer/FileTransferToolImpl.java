package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class FileTransferToolImpl implements FileTransferTool {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String S3_BUCKET = "s3://";
    private static final String PATH_DELIMITER = "/";

    private final S3Client s3Client;

    @Autowired
    FileTransferToolImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String downloadFileFromLocation(String fileLocation) {
        LOGGER.info("FileTransferToolImpl: Start process to download file from location: " + fileLocation);

        String downloadedFile = null;

        if (StringUtils.startsWithIgnoreCase(fileLocation, S3_BUCKET)) {
            downloadedFile = downloadFileFromS3(fileLocation);
        } else {
            logError(null, "FileTransferImpl: Invalid file location",
                "The file cannot be downloaded as it is not stored in a private S3 location: "
                    + fileLocation);
        }

        LOGGER.info("FileTransferToolImpl: Process to download file has finished");

        return downloadedFile;
    }

    private String downloadFileFromS3(String fileLocation) {
        try {
            byte[] s3ObjectBytes = getObjectInS3(fileLocation).readAllBytes();
            return new String(s3ObjectBytes);
        } catch (SdkClientException sdkEx) {
            logError(sdkEx,
                "FileTransferImpl: SdkClientException thrown when downloading file from S3",
                "Fail to download file as S3 location cannot be accessed: " + fileLocation);
        } catch (IOException ex) {
            logError(ex,
                "FileTransferImpl: IOException thrown when trying to convert file",
                "Fail to convert file from InputString to String");
        }

        return null;
    }

    /**
     * It will get the S3Object by using the location information: bucket name and the location of the
     * file within the bucket(key).
     *
     * @param location - location
     */
    private ResponseInputStream<GetObjectResponse> getObjectInS3(String location) {
        String locationWithoutS3 = location.replace(S3_BUCKET, "");

        String bucket = locationWithoutS3.split(PATH_DELIMITER)[0];
        String key = locationWithoutS3.replace(bucket + PATH_DELIMITER, "");
        return s3Client.getObject(GetObjectRequest.builder().bucket(bucket).key(key).build());
    }

    private void logError(Exception exception, String errorKey, String errorMessageMessage) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("message", errorMessageMessage);
        LOGGER.error(errorKey, exception, logMap);
    }
}
