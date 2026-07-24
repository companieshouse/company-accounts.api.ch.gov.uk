package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
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
    private S3Client s3ClientMock;
    @Mock
    private ResponseInputStream<GetObjectResponse> s3ObjectMock;

    private FileTransferTool fileTransferTool;

    private static String getIxbrl() {
        return """
                <?xml version="1.0" encoding="UTF-8"?><html xmlns:ixt2="http://www.xbrl.org/inlineXBRL/transformation/2011-07-31">
                  <head>
                    <meta content="application/xhtml+xml; charset=UTF-8" http-equiv="content-type" />
                    <title>
                            TEST COMPANY
                        </title>
                  <body xml:lang="en">
                    <div class="accounts-body ">
                      <div id="your-account-type" class="wholedoc">
                      </div>
                    </div>
                   </body>
                </html>
                """;
    }

    @BeforeEach
    void setBeforeEach() {
        fileTransferTool = new FileTransferToolImpl(s3ClientMock);
    }

    @Test
    @DisplayName("File is downloaded form location successfully")
    void shouldDownloadFileSuccessfully() throws IOException {
        when(s3ClientMock.getObject(any(GetObjectRequest.class))).thenReturn(s3ObjectMock);

        when(s3ObjectMock.readAllBytes()).thenReturn(IXBRL.getBytes());

        assertNotNull(fileTransferTool.downloadFileFromLocation(IXBRL_LOCATION));
        verifyS3ClientMockCall();
        verifyS3ObjectMockCall();
    }

    @Test
    @DisplayName("File not downloaded. SdkClientException thrown when getting the S3Object")
    void shouldFailToDownloadAsSdkClientExceptionThrownWhenGettingS3Object() {
        when(s3ClientMock.getObject(any(GetObjectRequest.class))).thenThrow(SdkClientException.class);

        assertNull(fileTransferTool.downloadFileFromLocation(IXBRL_LOCATION));
        verifyS3ClientMockCall();
    }

    @Test
    @DisplayName("File not downloaded. SdkClientException thrown when getting S3Object content, ixbrl")
    void shouldFailToDownloadAsSdkClientExceptionThrownWhenGettingS3ObjectContent() throws IOException {
        when(s3ClientMock.getObject(any(GetObjectRequest.class))).thenReturn(s3ObjectMock);

        when(s3ObjectMock.readAllBytes()).thenThrow(IOException.class);

        assertNull(fileTransferTool.downloadFileFromLocation(IXBRL_LOCATION));
        verifyS3ClientMockCall();
        verifyS3ObjectMockCall();
    }

    private void verifyS3ObjectMockCall() throws IOException {
        verify(s3ObjectMock, times(1)).readAllBytes();
    }

    private void verifyS3ClientMockCall() {
        verify(s3ClientMock, times(1)).getObject(any(GetObjectRequest.class));
    }
}
