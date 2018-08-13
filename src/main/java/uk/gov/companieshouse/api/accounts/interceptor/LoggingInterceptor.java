package uk.gov.companieshouse.api.accounts.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.util.AccountsLogUtil;
import uk.gov.companieshouse.api.accounts.util.AccountsLogger;
import uk.gov.companieshouse.api.accounts.util.AccountsLoggerImpl;
import uk.gov.companieshouse.api.accounts.util.RequestContext;

public class LoggingInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private AccountsLogger accountsLogger;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) {
        RequestContext requestContext = new RequestContext(requestPath(request),
            request.getMethod(), requestId(request), userId(request));
        request.getSession().setAttribute("START_TIME", System.currentTimeMillis());
        accountsLogger.logStartOfRequestProcessing(requestContext);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
        ModelAndView modelAndView) {
        long startTime = Long.valueOf((String) request.getSession().getAttribute("START_TIME"));
        long responseTime = System.currentTimeMillis() - startTime;
        RequestContext requestContext = new RequestContext(requestPath(request),
            request.getMethod(), requestId(request), userId(request));
        accountsLogger.logEndOfRequestProcessing(requestContext,response.getStatus(), responseTime);
    }


    private String requestId(HttpServletRequest request) {
        return request.getHeader(AccountsLogUtil.REQUEST_ID.value());
    }

    private String userId(HttpServletRequest request) {
        return request.getHeader(AccountsLogUtil.ERIC_IDENTITY.value());
    }

    private String requestPath(HttpServletRequest request) {
        return request.getRequestURI();
    }
}
