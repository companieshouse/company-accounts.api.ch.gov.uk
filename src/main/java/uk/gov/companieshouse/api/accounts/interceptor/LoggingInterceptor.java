package uk.gov.companieshouse.api.accounts.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.logging.api.LogContext;
import uk.gov.companieshouse.logging.api.LogHelper;
import uk.gov.companieshouse.logging.api.LogType;
import uk.gov.companieshouse.logging.api.LogUtil;
import uk.gov.companieshouse.logging.api.LoggerApi;

@Component
public class LoggingInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private LoggerApi accountsLogger;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) {
        LogContext logContext = LogHelper.createNewLogContext(request, LogType.START);
        Long startTime = System.currentTimeMillis();
        request.getSession().setAttribute(LogUtil.START_TIME_KEY.value(), startTime);
        accountsLogger.logStart(logContext);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
            ModelAndView modelAndView) {
        Long startTime = (Long) request.getSession().getAttribute(LogUtil.START_TIME_KEY.value());
        long responseTime = System.currentTimeMillis() - startTime;
        LogContext logContext = LogHelper.createNewLogContext(request, LogType.END);
        accountsLogger
                .logEnd(logContext, response.getStatus(), responseTime);
    }
}
