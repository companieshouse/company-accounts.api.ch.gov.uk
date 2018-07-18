package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @BeforeEach
    public void setUp() {
        Map<String, String> pathVariables = ImmutableMap.of("transactionId", "5555");

        when(httpServletRequest.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE))
                .thenReturn(pathVariables);
        when(httpServletRequest.getHeader("X-Request-Id")).thenReturn("1111");

        httpServletResponse.setContentType("text/html");
    }

    @Test
    @DisplayName("Tests the interceptor with an existing transaction that is open")
    public void testPreHandleWithOpenTransaction() {
        when(transactionManagerMock.getTransaction(anyString(), anyString()))
                .thenReturn(createOpenDummyTransaction(true));

        boolean result = transactionInterceptor
                .preHandle(httpServletRequest, httpServletResponse, new Object());

        assertTrue(result);
    }

    @Test
    @DisplayName("Tests the interceptor with an existing transaction that is closed")
    public void testPreHandleWithClosedTransaction() {
        when(transactionManagerMock.getTransaction(anyString(), anyString()))
                .thenReturn(createOpenDummyTransaction(false));

        boolean result = transactionInterceptor
                .preHandle(httpServletRequest, httpServletResponse, new Object());

        assertFalse(result);
    }

    @Test
    @DisplayName("Tests the interceptor with a non-existing transaction")
    public void testPreHandleWithNonExistingTransaction() {
        when(transactionManagerMock.getTransaction(anyString(), anyString()))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        boolean result = transactionInterceptor
                .preHandle(httpServletRequest, httpServletResponse, new Object());

        assertFalse(result);
    }

    /**
     * creates an open or closed dummy transaction depending on the boolean passed into method
     *
     * @param isOpen - true = open, false - closed
     * @return ResponseEntity<> with the desired transaction
     */
    private ResponseEntity<Transaction> createOpenDummyTransaction(boolean isOpen) {
        Transaction openTransaction = new Transaction();

        if (isOpen) {
            openTransaction.setStatus(TransactionStatus.OPEN.getStatus());
        } else {
            openTransaction.setStatus(TransactionStatus.CLOSED.getStatus());
        }

        return new ResponseEntity<>(openTransaction, HttpStatus.OK);
    }
}