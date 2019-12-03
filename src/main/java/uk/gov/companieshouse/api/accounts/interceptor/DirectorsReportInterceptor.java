package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.directorsreport.DirectorsReport;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.DirectorsReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class DirectorsReportInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private DirectorsReportService directorsReportService;

    private static final String COMPANY_ACCOUNTS_ID_PATH_VARIABLE = "companyAccountId";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        Transaction transaction = (Transaction) request
                .getAttribute(AttributeName.TRANSACTION.getValue());

        Map<String, String> pathVariables = (Map) request
                .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        String companyAccountId = pathVariables.get(COMPANY_ACCOUNTS_ID_PATH_VARIABLE);

        try {
            ResponseObject<DirectorsReport> responseObject = directorsReportService.find(companyAccountId, request);

            if (!responseObject.getStatus().equals(ResponseStatus.FOUND)) {

                LoggingHelper.logInfo(
                        companyAccountId, transaction, "Directors report not found", request);

                response.setStatus(HttpStatus.NOT_FOUND.value());
                return false;
            }

            SmallFull smallFull = (SmallFull) request
                    .getAttribute(AttributeName.SMALLFULL.getValue());

            String smallFullDirectorsLink =
                    smallFull.getLinks().get(SmallFullLinkType.DIRECTORS_REPORT.getLink());

            DirectorsReport directorsReport = responseObject.getData();
            String directorsReportSelfLink = directorsReport.getLinks().get(BasicLinkType.SELF.getLink());

            if (!directorsReportSelfLink.equals(smallFullDirectorsLink)) {

                LoggingHelper.logInfo(
                        companyAccountId, transaction, "Directors report link not present in small full resource", request);

                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return false;
            }

            request.setAttribute(AttributeName.DIRECTORS_REPORT.getValue(), directorsReport);
            return true;

        } catch (DataException e) {

            LoggingHelper.logException(
                    companyAccountId, transaction, "Failed to retrieve directors report resource", e, request);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return false;
        }
    }
}
