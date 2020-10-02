package uk.gov.companieshouse.api.accounts.validation.transactionclosure;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.CompanyAccountLinkType;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CompanyAccount;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyAccountService;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.SmallFullService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.validation.BaseValidator;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Component
public class CurrentPeriodTnClosureValidator extends BaseValidator {

    private final CompanyAccountService companyAccountService;

    private final SmallFullService smallFullService;

    private final CurrentPeriodService currentPeriodService;

    private static final String SMALL_FULL_CURRENT_PERIOD_PATH = "$.small_full.current_period";
    private static final String SMALL_FULL_CURRENT_PERIOD_BALANCE_SHEET_PATH = SMALL_FULL_CURRENT_PERIOD_PATH + ".balance_sheet";

    @Autowired
    public CurrentPeriodTnClosureValidator(CompanyService companyService,
                                           CompanyAccountService companyAccountService,
                                           SmallFullService smallFullService,
                                           CurrentPeriodService currentPeriodService) {
        super(companyService);
        this.companyAccountService = companyAccountService;
        this.smallFullService = smallFullService;
        this.currentPeriodService = currentPeriodService;
    }

    public Errors isValid(String companyAccountsId, HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        Map<String, String> companyAccountLinks;
        Map<String, String> smallFullLinks;

        ResponseObject<CompanyAccount> companyAccountResponseObject = companyAccountService.findById(companyAccountsId, request);
        companyAccountLinks = companyAccountResponseObject.getData().getLinks();

        if (companyAccountLinks.get(CompanyAccountLinkType.SMALL_FULL.getLink()) != null) {
            smallFullLinks = smallFullService.find(companyAccountsId, request).getData().getLinks();

            if (smallFullLinks.get(SmallFullLinkType.CURRENT_PERIOD.getLink()) != null) {
                ResponseObject<CurrentPeriod> currentPeriodResponseObject = currentPeriodService.find(companyAccountsId, request);

                if (currentPeriodResponseObject.getStatus().equals(ResponseStatus.NOT_FOUND)) {
                    addError(errors, mandatoryElementMissing, SMALL_FULL_CURRENT_PERIOD_PATH);
                } else if (currentPeriodResponseObject.getData().getBalanceSheet() == null) {
                    addError(errors, mandatoryElementMissing, SMALL_FULL_CURRENT_PERIOD_BALANCE_SHEET_PATH);
                }
            } else {
                addError(errors, mandatoryElementMissing, SMALL_FULL_CURRENT_PERIOD_PATH);
            }
        }

        return errors;
    }
}
