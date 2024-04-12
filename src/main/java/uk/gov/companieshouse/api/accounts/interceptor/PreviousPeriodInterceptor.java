package uk.gov.companieshouse.api.accounts.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HandlerMapping;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.model.transaction.Transaction;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PreviousPeriodInterceptor implements HandlerInterceptor {

        private static final Logger LOGGER = LoggerFactory.getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

        @Autowired
        private PreviousPeriodService previousPeriodService;

        @Override
        @SuppressWarnings("unchecked")
        public boolean preHandle(HttpServletRequest request,
                                 @NonNull HttpServletResponse response,
                                 @NonNull Object handler) throws NoSuchAlgorithmException {

            if (request.getMethod().equalsIgnoreCase("POST") && request.getRequestURI()
                    .endsWith("previous-period")) {
                return true;
            }

            String requestId = request.getHeader("X-Request-Id");

            final Map<String, Object> debugMap = new HashMap<>();

            debugMap.put("request_method", request.getMethod());
            debugMap.put("request_id", requestId);

            Transaction transaction = (Transaction) request
                    .getAttribute(AttributeName.TRANSACTION.getValue());
            if (transaction == null) {
                LOGGER.errorRequest(request,
                        "PreviousPeriodInterceptor error: No transaction in request session", debugMap);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return false;
            }

            Map<String, String> pathVariables = (Map) request
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            String companyAccountId = pathVariables.get("companyAccountId");

            debugMap.put("transaction_id", transaction.getId());
            debugMap.put("transaction_company_number", transaction.getCompanyNumber());
            debugMap.put("path_variables", pathVariables);

            SmallFull smallFull = (SmallFull) request.getAttribute(AttributeName.SMALLFULL.getValue());

            if (smallFull == null) {
                LOGGER.errorRequest(request,
                        "PreviousPeriodInterceptor error: No company account in request session", debugMap);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return false;
            }

            ResponseObject<PreviousPeriod> responseObject;
            try {
                responseObject = previousPeriodService.find(companyAccountId, request);
            } catch (DataException de) {
                LOGGER.errorRequest(request, de, debugMap);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return false;
            }

            if (!responseObject.getStatus().equals(ResponseStatus.FOUND)) {
                LOGGER.debugRequest(request,
                        "PreviousPeriodInterceptor error: Failed to retrieve a previous period resource", debugMap);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return false;
            }

            PreviousPeriod previousPeriod = responseObject.getData();

            String smallFullLink = smallFull.getLinks().get(SmallFullLinkType.PREVIOUS_PERIOD.getLink());
            String previousPeriodSelf = previousPeriod.getLinks().get(BasicLinkType.SELF.getLink());
            if (!smallFullLink.equals(previousPeriodSelf)) {
                LOGGER.debugRequest(request,
                        "PreviousPeriodInterceptor error: The PreviousPeriod self link does not exist in the SmallFull links",
                        debugMap);
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return false;
            }

            request.setAttribute(AttributeName.PREVIOUS_PERIOD.getValue(), previousPeriod);
            return true;

        }
}
