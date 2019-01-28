package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.http.client.methods.HttpGet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class FileTransferToolImplTest {

    private static final String IXBRL_LOCATION = "s3://test-bucket_name/accounts/ixbrl-generated-name.html";
    private static final String IXBRL = getIxbrl();

    @Mock
    private AmazonS3 amazonS3Mock;
    @Mock
    private S3Object s3ObjectMock;

    private FileTransferTool fileTransferTool;

    private static String getIxbrl() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
            + "<html xmlns:ixt2=\"http://www.xbrl.org/inlineXBRL/transformation/2011-07-31\">\n"
            + "  <head>\n"
            + "    <meta content=\"application/xhtml+xml; charset=UTF-8\" http-equiv=\"content-type\" />\n"
            + "    <title>\n"
            + "            TEST COMPANY\n"
            + "        </title>\n"
            + "  <body xml:lang=\"en\">\n"
            + "    <div class=\"accounts-body \">\n"
            + "      <div id=\"your-account-type\" class=\"wholedoc\">\n"
            + "      </div>\n"
            + "    </div>\n"
            + "   </body>\n"
            + "</html>\n";
    }

    @BeforeEach
    void setBeforeEach() {
        fileTransferTool = new FileTransferToolImpl(amazonS3Mock);
    }

    @Test
    @DisplayName("File is downloaded form location successfully")
    void shouldDownloadFileSuccessfully() {

        when(amazonS3Mock.getObject(any())).thenReturn(s3ObjectMock);

        S3ObjectInputStream s3ObjectInputStream = createS3InputStream();
        when(s3ObjectMock.getObjectContent()).thenReturn(s3ObjectInputStream);

        assertNotNull(fileTransferTool.downloadFileFromLocation(IXBRL_LOCATION));
        verifyAmazonS3MockCall();
        verifyS3ObjectMockCall();
    }

    @Test
    @DisplayName("File not downloaded. SdkClientException thrown when getting the S3Object")
    void shouldFailToDownloadAsSdkClientExceptionThrownWhenGettingS3Object() {

        when(amazonS3Mock.getObject(any())).thenThrow(SdkClientException.class);

        assertNull(fileTransferTool.downloadFileFromLocation(IXBRL_LOCATION));
        verifyAmazonS3MockCall();
    }

    @Test
    @DisplayName("File not downloaded. SdkClientException thrown when getting S3Object content, ixbrl")
    void shouldFailToDownloadAsSdkClientExceptionThrownWhenGettingS3ObjectContent() {

        when(amazonS3Mock.getObject(any())).thenReturn(s3ObjectMock);

        when(s3ObjectMock.getObjectContent()).thenThrow(SdkClientException.class);

        assertNull(fileTransferTool.downloadFileFromLocation(IXBRL_LOCATION));
        verifyAmazonS3MockCall();
        verifyS3ObjectMockCall();
    }

    private S3ObjectInputStream createS3InputStream() {
        InputStream inputStreamResponse = new ByteArrayInputStream(IXBRL.getBytes());
        return new S3ObjectInputStream(inputStreamResponse, new HttpGet());
    }

    private void verifyS3ObjectMockCall() {
        verify(s3ObjectMock, times(1)).getObjectContent();
    }

    private void verifyAmazonS3MockCall() {
        verify(amazonS3Mock, times(1)).getObject(any());
    }
}
