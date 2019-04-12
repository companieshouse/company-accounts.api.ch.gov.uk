package uk.gov.companieshouse.api.accounts.interceptor;

import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CicReport;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.service.impl.CicReportService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

public class CicReportInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private CicReportService cicReportService;

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
            ResponseObject<CicReport> responseObject = cicReportService.find(companyAccountId, request);

            if (!responseObject.getStatus().equals(ResponseStatus.FOUND)) {

                LoggingHelper.logInfo(
                        companyAccountId, transaction, "Cic report not found", request);

                response.setStatus(HttpStatus.NOT_FOUND.value());
                return false;
            }

            CompanyAccount companyAccount = (CompanyAccount) request
                    .getAttribute(AttributeName.COMPANY_ACCOUNT.getValue());
            String companyAccountsCicReportLink =
                    companyAccount.getLinks().get(CompanyAccountLinkType.CIC_REPORT.getLink());

            CicReport cicReport = responseObject.getData();
            String cicReportSelfLink = cicReport.getLinks().get(BasicLinkType.SELF.getLink());

            if (!cicReportSelfLink.equals(companyAccountsCicReportLink)) {

                LoggingHelper.logInfo(
                        companyAccountId, transaction, "Cic report link not present in company accounts resource", request);

                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return false;
            }

            request.setAttribute(AttributeName.CIC_REPORT.getValue(), cicReport);
            return true;

        } catch (DataException e) {

            LoggingHelper.logException(
                    companyAccountId, transaction, "Failed to retrieve cic report resource", e, request);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return false;
        }
    }
}
