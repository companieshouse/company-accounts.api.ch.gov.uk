package uk.gov.companieshouse.api.accounts.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.logging.api.LogContext;
import uk.gov.companieshouse.logging.api.LogUtil;
import uk.gov.companieshouse.logging.api.LoggerApi;

@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private LoggerApi accountsLogger;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        LogContext requestContext = new LogContext(requestPath(request),
                requestMethod(request), requestId(request), userId(request));
        Long startTime = System.currentTimeMillis();
        request.getSession().setAttribute(LogUtil.START_TIME_KEY.value(), startTime);
        accountsLogger.logStartOfRequestProcessing(requestContext);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
        Long startTime = (Long) request.getSession().getAttribute(LogUtil.START_TIME_KEY.value());
        long responseTime = System.currentTimeMillis() - startTime;
        LogContext requestContext = new LogContext(requestPath(request),
                requestMethod(request), requestId(request), userId(request));
        accountsLogger
                .logEndOfRequestProcessing(requestContext, response.getStatus(), responseTime);
    }


    private String requestMethod(HttpServletRequest request) {
        return request.getMethod();
    }

    private String requestId(HttpServletRequest request) {
        return request.getHeader(LogUtil.REQUEST_ID.value());
    }

    private String userId(HttpServletRequest request) {
        return request.getHeader(LogUtil.ERIC_IDENTITY.value());
    }

    private String requestPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
