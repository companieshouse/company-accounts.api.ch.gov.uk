package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionManager;
import uk.gov.companieshouse.api.accounts.transaction.TransactionStatus;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class TransactionInterceptorTest {

    @InjectMocks
    private TransactionInterceptor transactionInterceptor;

    @Mock
    private TransactionManager transactionManagerMock;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private HttpServletResponse httpServletResponseMock;

    @Mock
    private HttpSession httpSessionMock;

    @BeforeEach
    void setUp() {
        Map<String, String> pathVariables = new HashMap<>();
        pathVariables.put("transactionId", "5555");

        when(httpServletRequestMock.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(pathVariables);
        when(httpServletRequestMock.getHeader("X-Request-Id")).thenReturn("1111");

        httpServletResponseMock.setContentType("text/html");
    }

    @Test
    @DisplayName("Tests the interceptor with an existing transaction that is open")
    void testPreHandleWithOpenTransaction() {
        when(transactionManagerMock.getTransaction(anyString(), anyString()))
                .thenReturn(createDummyTransaction(true));

        when(httpServletRequestMock.getSession()).thenReturn(httpSessionMock);

        assertTrue(transactionInterceptor.preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
    }

    @Test
    @DisplayName("Tests the interceptor with an existing transaction that is closed")
    void testPreHandleWithClosedTransaction() {
        when(transactionManagerMock.getTransaction(anyString(), anyString()))
                .thenReturn(createDummyTransaction(false));

        when(httpServletRequestMock.getSession()).thenReturn(httpSessionMock);

        assertFalse(transactionInterceptor.preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
    }

    @Test
    @DisplayName("Tests the interceptor with a non-existing transaction")
    void testPreHandleWithNonExistingTransaction() {
        when(transactionManagerMock.getTransaction(anyString(), anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        assertFalse(transactionInterceptor.preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
        verify(httpServletResponseMock).setStatus(HttpStatus.NOT_FOUND.value());
    }

    /**
     * creates an open or closed dummy transaction depending on the boolean passed into method
     *
     * @param isOpen - true = open, false - closed
     * @return ResponseEntity<> with the desired transaction
     */
    private ResponseEntity<Transaction> createDummyTransaction(boolean isOpen) {
        Transaction transaction = new Transaction();

        transaction.setStatus(isOpen ? TransactionStatus.OPEN.getStatus() : TransactionStatus.CLOSED.getStatus());

        return new ResponseEntity<>(transaction, HttpStatus.OK);
    }
}