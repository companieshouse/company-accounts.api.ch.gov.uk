package uk.gov.companieshouse.api.accounts.interceptor;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.util.LogContext;
import uk.gov.companieshouse.logging.util.LogContextProperties;

@ExtendWith(MockitoExtension.class)
public class LoggingInterceptorTest {

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HttpSession session;

    @InjectMocks
    private LoggingInterceptor loggingInterceptor;

    private ByteArrayOutputStream out;
    private PrintStream defaultOut;

    @BeforeEach
    public void setUp() {
        when(httpServletRequest.getSession()).thenReturn(session);
        out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));
    }

    @Test
    @DisplayName("Tests the interceptor logs the start of the request")
    public void preHandle() {
        loggingInterceptor.preHandle(httpServletRequest, httpServletResponse, new Object());
        verify(session, times(1)).setAttribute(eq(LogContextProperties.START_TIME_KEY.value()), anyLong());
        String data = this.getOutputJson().toString();
        assertThat(data, containsString(LogContextProperties.START_OF_REQUEST_MSG.value()));
        assertThat(data, containsString( "\"event\":\"info\""));
    }

    @Test
    @DisplayName("Tests the interceptor logs the end of the request")
    public void postHandle() {
        long startTime = System.currentTimeMillis();
        when(session.getAttribute(LogContextProperties.START_TIME_KEY.value()))
                .thenReturn(startTime);
        when(httpServletResponse.getStatus()).thenReturn(200);

        loggingInterceptor.postHandle(httpServletRequest, httpServletResponse, new Object(),
                new ModelAndView());
        verify(session, times(1)).getAttribute(LogContextProperties.START_TIME_KEY.value());
        String data = this.getOutputJson().toString();
        assertThat(data, containsString(LogContextProperties.END_OF_REQUEST_MSG.value()));
        assertThat(data, containsString( "\"event\":\"info\""));
        assertThat(data, containsString( "duration"));
        assertThat(data, containsString( "status\":200"));
    }

    private JSONObject getOutputJson() {
        String output = out.toString();
        return new JSONObject(output);
    }
}
