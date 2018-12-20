package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
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
class FileTransferToolTest {

    private static final String IXBRL_LOCATION = "s3://test-bucket/accounts/ixbrl-generated-name.html";
    private static final String IXBRL = getIxbrl();

    @Mock
    private HttpURLConnection httpURLConnectionMock;
    @Mock
    private HttpURLConnectionHandler httpURLConnectionHandlerMock;

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
        fileTransferTool = new FileTransferTool(httpURLConnectionHandlerMock);
    }

    @Test
    @DisplayName("File is downloaded form location successfully")
    void shouldDownloadFileSuccessfully() throws IOException {

        when(httpURLConnectionHandlerMock.openConnection(anyString()))
            .thenReturn(httpURLConnectionMock);

        when(httpURLConnectionMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);

        InputStream inputStreamResponse = new ByteArrayInputStream(IXBRL.getBytes());
        when(httpURLConnectionMock.getInputStream()).thenReturn(inputStreamResponse);

        assertNotNull(fileTransferTool.downloadFileFromPublicLocation(IXBRL_LOCATION));
        verifyHttpURLConnectionHandlerMockCalls();
        verifyHttpURLConnectionMockCalls();
        verify(httpURLConnectionMock, times(1)).getInputStream();
    }


    @Test
    @DisplayName("File is not downloaded as open connection throw exception")
    void shouldFailToDownloadFileAsOpenConnectionThrowException() throws IOException {

        when(httpURLConnectionHandlerMock.openConnection(anyString()))
            .thenThrow(IOException.class);

        assertNull(fileTransferTool.downloadFileFromPublicLocation(IXBRL_LOCATION));
        verifyHttpURLConnectionHandlerMockCalls();
    }

    @Test
    @DisplayName("File is not downloaded as wrong http code is returned")
    void shouldFailToDownloadFileAsWrongHttpCodeIsReturned() throws IOException {

        when(httpURLConnectionHandlerMock.openConnection(anyString()))
            .thenReturn(httpURLConnectionMock);

        when(httpURLConnectionMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NO_CONTENT);

        assertNull(fileTransferTool.downloadFileFromPublicLocation(IXBRL_LOCATION));
        verifyHttpURLConnectionHandlerMockCalls();
        verifyHttpURLConnectionMockCalls();
    }

    @Test
    @DisplayName("File is not downloaded as exception is thrown when getting response code")
    void shouldFailToDownloadFileAsExceptionThrownWhenGettingResponseCode() throws IOException {

        when(httpURLConnectionHandlerMock.openConnection(anyString()))
            .thenReturn(httpURLConnectionMock);

        when(httpURLConnectionMock.getResponseCode()).thenThrow(IOException.class);

        assertNull(fileTransferTool.downloadFileFromPublicLocation(IXBRL_LOCATION));
        verifyHttpURLConnectionHandlerMockCalls();
        verifyHttpURLConnectionMockCalls();
    }

    @Test
    @DisplayName("File is not downloaded as exception is thrown when trying to access to the file")
    void shouldFailToDownloadFileAsExceptionThrownWhenGettingInputStream() throws IOException {

        when(httpURLConnectionHandlerMock.openConnection(anyString()))
            .thenReturn(httpURLConnectionMock);

        when(httpURLConnectionMock.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(httpURLConnectionMock.getInputStream()).thenThrow(IOException.class);

        assertNull(fileTransferTool.downloadFileFromPublicLocation(IXBRL_LOCATION));
        verifyHttpURLConnectionHandlerMockCalls();
        verifyHttpURLConnectionMockCalls();
    }

    private void verifyHttpURLConnectionHandlerMockCalls() throws IOException {
        verify(httpURLConnectionHandlerMock, times(1)).openConnection(anyString());
    }

    private void verifyHttpURLConnectionMockCalls() throws IOException {
        verify(httpURLConnectionMock, times(1)).getResponseCode();
    }

}
