package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class FileTransferToolImpl implements FileTransferTool {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);
    private static final String S3_BUCKET = "s3://";
    private static final String PATH_DELIMITER = "/";

    private final String awsAccessKeyId;
    private final String awsSecretAccessKey;
    private final String imageCloudProxyHost;
    private final Integer imageCloudProxyPort;

    private final AmazonS3Handler amazonS3Handler;
    private final EnvironmentReader environmentReader;

    @Autowired
    FileTransferToolImpl(EnvironmentReader environmentReader, AmazonS3Handler amazonS3Handler) {
        this.environmentReader = environmentReader;
        this.amazonS3Handler = amazonS3Handler;

        awsAccessKeyId = environmentReader.getMandatoryString("AWS_ACCESS_KEY_ID");
        awsSecretAccessKey = environmentReader.getMandatoryString("AWS_SECRET_ACCESS_KEY");
        imageCloudProxyHost = environmentReader.getOptionalString("IMAGE_CLOUD_PROXY_HOST");
        imageCloudProxyPort = environmentReader.getOptionalInteger("IMAGE_CLOUD_PROXY_PORT");
    }

    @Override
    public String downloadFileFromLocation(String fileLocation) {

        LOGGER.info(
            "FileTransferToolImpl: Start process to download file from location: " + fileLocation);

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
            AmazonS3 s3client = getAmazonS3Client();
            S3Object s3Object = getObjectInS3(fileLocation, s3client);

            return getConvertInputStringToString(s3Object.getObjectContent());

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
     * Get the AWS credentials passing all the S3 information: aws key, aws secred key, cloud proxy,
     * cloud port
     */
    private AmazonS3 getAmazonS3Client() {
        return amazonS3Handler.getAmazonS3(awsAccessKeyId, awsSecretAccessKey, imageCloudProxyHost,
            imageCloudProxyPort);
    }

    private S3Object getObjectInS3(String location, AmazonS3 s3client) {

        String locationWithoutS3 = location.replace(S3_BUCKET, "");

        String bucket = locationWithoutS3.split(PATH_DELIMITER)[0];
        String key = locationWithoutS3.replace(bucket + PATH_DELIMITER, "");

        return s3client.getObject(new GetObjectRequest(bucket, key));
    }

    private String getConvertInputStringToString(InputStream inputStream) throws IOException {
        byte[] byteArray = IOUtils.toByteArray(inputStream);
        return new String(byteArray);
    }

    private void logError(Exception exception, String errorKey, String errorMessageMessage) {
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("message", errorMessageMessage);
        LOGGER.error(errorKey, exception, logMap);
    }
}
