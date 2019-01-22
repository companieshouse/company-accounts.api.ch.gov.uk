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
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class FileTransferToolImpl implements FileTransferTool {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    private static final String S3_BUCKET = "s3://";
    private static final String PATH_DELIMITER = "/";

    private final AmazonS3 amazonS3;

    @Autowired
    FileTransferToolImpl(AmazonS3 amazonS3) {
        this.amazonS3 = amazonS3;
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
            S3Object s3Object = getObjectInS3(fileLocation);

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
     * It will get the S3Object by using the location information: bucket name and the location of the
     * file within the bucket(key).
     *
     * @param location
     * @return
     */
    private S3Object getObjectInS3(String location) {

        String locationWithoutS3 = location.replace(S3_BUCKET, "");

        String bucket = locationWithoutS3.split(PATH_DELIMITER)[0];
        String key = locationWithoutS3.replace(bucket + PATH_DELIMITER, "");

        return amazonS3.getObject(new GetObjectRequest(bucket, key));
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
