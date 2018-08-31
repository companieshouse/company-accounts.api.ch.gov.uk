package uk.gov.companieshouse.api.accounts.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.accounts.transaction.TransactionStatus;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
public class OpenTransactionInterceptorTest {

    @InjectMocks
    private OpenTransactionInterceptor transactionInterceptor;

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private HttpServletResponse httpServletResponseMock;

    @Test
    @DisplayName("Tests the interceptor with an existing transaction that is open - happy path")
    void testPreHandleWithOpenTransaction() {

        when(httpServletRequestMock.getAttribute(anyString()))
            .thenReturn(createDummyTransaction(true));

        assertTrue(transactionInterceptor
            .preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
    }

    @Test
    @DisplayName("Tests the interceptor failure when transaction is closed")
    void testPreHandleWithClosedTransaction() {

        when(httpServletRequestMock.getAttribute(anyString()))
            .thenReturn(createDummyTransaction(false));

        assertFalse(transactionInterceptor
            .preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
    }

    @Test
    @DisplayName("Tests the interceptor failure when transaction has not been set")
    void testPreHandleWithTransactionNotBeingSet() {

        when(httpServletRequestMock.getAttribute(anyString()))
            .thenReturn(null);

        assertFalse(transactionInterceptor
            .preHandle(httpServletRequestMock, httpServletResponseMock, new Object()));
    }

    /**
     * creates an open or closed dummy transaction depending on the boolean passed into method
     *
     * @param isOpen - true = open, false - closed
     * @return {@link Transaction} with the desired transaction status
     */
    private Transaction createDummyTransaction(boolean isOpen) {
        Transaction transaction = new Transaction();

        transaction.setStatus(
            isOpen ? TransactionStatus.OPEN.getStatus() : TransactionStatus.CLOSED.getStatus());

        return transaction;
    }
}