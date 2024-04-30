package uk.gov.companieshouse.api.accounts.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.gov.companieshouse.api.accounts.AttributeName;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.BasicLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.relatedpartytransactions.RelatedPartyTransactions;
import uk.gov.companieshouse.api.accounts.service.impl.RelatedPartyTransactionsServiceImpl;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.utility.LoggingHelper;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class RelatedPartyTransactionsInterceptor implements HandlerInterceptor {

    @Autowired
    private RelatedPartyTransactionsServiceImpl relatedPartyTransactionsService;

    private static final String COMPANY_ACCOUNTS_ID_PATH_VARIABLE = "companyAccountId";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        Transaction transaction = (Transaction) request.getAttribute(AttributeName.TRANSACTION.getValue());

        Map<String, String> pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        String companyAccountId = pathVariables.get(COMPANY_ACCOUNTS_ID_PATH_VARIABLE);

        try {
            ResponseObject<RelatedPartyTransactions> responseObject = relatedPartyTransactionsService
                    .find(companyAccountId, request);

            if (!responseObject.getStatus().equals(ResponseStatus.FOUND)) {
                LoggingHelper.logInfo(companyAccountId, transaction,
                        "Related party transaction not found", request);

                response.setStatus(HttpStatus.NOT_FOUND.value());
                return false;
            }

            SmallFull smallFull = (SmallFull) request.getAttribute(AttributeName.SMALLFULL.getValue());

            String smallFullRelatedPartyTransactionsLink =
                    smallFull.getLinks().get(SmallFullLinkType.RELATED_PARTY_TRANSACTIONS.getLink());

            RelatedPartyTransactions relatedPartyTransactions = responseObject.getData();
            String relatedPartyTransactionsSelfLink = relatedPartyTransactions.getLinks()
                    .get(BasicLinkType.SELF.getLink());

            if (!relatedPartyTransactionsSelfLink.equals(smallFullRelatedPartyTransactionsLink)) {
                LoggingHelper.logInfo(companyAccountId, transaction,
                        "Related party transactions link not present in small full resource", request);

                response.setStatus(HttpStatus.BAD_REQUEST.value());
                return false;
            }

            request.setAttribute(AttributeName.RELATED_PARTY_TRANSACTIONS.getValue(), relatedPartyTransactions);
            return true;

        } catch (DataException e) {

            LoggingHelper.logException(companyAccountId, transaction,
                    "Failed to retrieve related party transactions resource", e, request);

            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            return false;
        }
    }
}
