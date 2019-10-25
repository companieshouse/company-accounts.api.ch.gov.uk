package uk.gov.companieshouse.api.accounts.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class CurrentPeriodInterceptor extends HandlerInterceptorAdapter {

    private static  final Logger LOGGER = LoggerFactory.getLogger(
            CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private CurrentPeriodService currentPeriodService;

    @Override
    @SuppressWarnings("unchecked")
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response
            , Object handler) throws NoSuchAlgorithmException {

        if(request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI()
        .endsWith("current-period")) {
            return true;
        }

        String requestId = request.getHeader("X-Request-Id");

        final Map<String, Object> debugMap = new HashMap<>();

        debugMap.put("request_method", request.getMethod());
        debugMap.put("request_id", requestId);

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());
        if (transaction == null) {
            debugMap.put("message",
                    "CurrentPeriodInterceptor error: No transaction in request session");
            LOGGER.errorRequest(request, null, debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        Map<String, String> pathVariables = (Map) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String companyAccountId = pathVariables.get("companyAccountId");

        debugMap.put("transaction_id", transaction.getId());
        debugMap.put("transaction_company_number", transaction.getCompanyNumber());
        debugMap.put("path_variables", pathVariables);

        SmallFull smallFull = (SmallFull) request
                .getAttribute(AttributeName.SMALLFULL.getValue());

        if (smallFull == null) {
            debugMap.put("message",
                    "CurrentPeriodInterceptor error: No company account in request session");
            LOGGER.errorRequest(request, null, debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        ResponseObject<CurrentPeriod> responseObject;
        try {
            responseObject = currentPeriodService.find(companyAccountId, request);
        } catch (DataException de) {
            LOGGER.errorRequest(request, de, debugMap);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return false;
        }

        if (!responseObject.getStatus().equals(ResponseStatus.FOUND)) {
            LOGGER.debugRequest(request,
                    "CurrentPeriodInterceptor error: Failed to retrieve a current period resource",
                    debugMap);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return false;
        }

        CurrentPeriod currentPeriod= responseObject.getData();

        String smallFullLink = smallFull.getLinks()
                .get(SmallFullLinkType.CURRENT_PERIOD.getLink());
        String currentPeriodSelf = currentPeriod.getLinks().get(BasicLinkType.SELF.getLink());
        if (!smallFullLink.equals(currentPeriodSelf)) {
            LOGGER.debugRequest(request,
                    "CurrentPeriodInterceptor error: The CurrentPeriod self link does not exist in the SmallFull links",
                    debugMap);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return false;
        }

        request.setAttribute(AttributeName.CURRENT_PERIOD.getValue(), currentPeriod);
        return true;

    }
}
