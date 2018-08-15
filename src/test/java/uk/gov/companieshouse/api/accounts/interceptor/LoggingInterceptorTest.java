package uk.gov.companieshouse.api.accounts.interceptor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.companieshouse.api.accounts.util.AccountsLogUtil.START_TIME_KEY;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.api.accounts.util.AccountsLogger;
import uk.gov.companieshouse.api.accounts.util.RequestContext;

@ExtendWith(MockitoExtension.class)
public class LoggingInterceptorTest {

    @Mock
    private AccountsLogger accountsLogger;

    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpSession session;

    @InjectMocks
    private LoggingInterceptor loggingInterceptor;

    @BeforeEach
    public void setUp() {
        when(httpServletRequest.getSession()).thenReturn(session);
    }

    @Test
    @DisplayName("Tests the interceptor logs the start of the request")
    public void preHandle() {
        loggingInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object());
        verify(session, times(1)).setAttribute(eq(START_TIME_KEY.value()), anyLong());
        verify(accountsLogger, times(1)).logStartOfRequestProcessing(any(RequestContext.class));
    }

    @Test
    @DisplayName("Tests the interceptor logs the end of the request")
    public void postHandle(){
        when(session.getAttribute(START_TIME_KEY.value())).thenReturn(System.currentTimeMillis());
        loggingInterceptor.postHandle(httpServletRequest, httpServletResponse, new Object(), new ModelAndView());
        verify(session, times(1)).getAttribute(eq(START_TIME_KEY.value()));
        verify(accountsLogger, times(1)).logEndOfRequestProcessing(any(RequestContext.class)
            ,anyInt(), anyLong());
    }
}
