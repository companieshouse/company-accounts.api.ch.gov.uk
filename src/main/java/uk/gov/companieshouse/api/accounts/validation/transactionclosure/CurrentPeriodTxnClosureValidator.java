package uk.gov.companieshouse.api.accounts.validation.transactionclosure;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.validation.BaseValidator;

@Component
public class CurrentPeriodTxnClosureValidator extends BaseValidator {

    private final CurrentPeriodService currentPeriodService;

    private static final String SMALL_FULL_CURRENT_PERIOD_PATH = "$.small_full.current_period";
    private static final String SMALL_FULL_CURRENT_PERIOD_BALANCE_SHEET_PATH = SMALL_FULL_CURRENT_PERIOD_PATH + ".balance_sheet";

    @Autowired
    public CurrentPeriodTxnClosureValidator(CompanyService companyService,
                                           CurrentPeriodService currentPeriodService) {
        super(companyService);
        this.currentPeriodService = currentPeriodService;
    }

    public Errors isValid(String companyAccountsId, SmallFull smallFull, HttpServletRequest request,
            Errors errors) throws DataException {

        if (smallFull.getLinks().get(SmallFullLinkType.CURRENT_PERIOD.getLink()) != null) {
            ResponseObject<CurrentPeriod> currentPeriodResponseObject =
                    currentPeriodService.find(companyAccountsId, request);

            if (currentPeriodResponseObject.getStatus().equals(ResponseStatus.NOT_FOUND)) {
                addError(errors, mandatoryElementMissing, SMALL_FULL_CURRENT_PERIOD_PATH);
            } else if (currentPeriodResponseObject.getData().getBalanceSheet() == null) {
                addError(errors, mandatoryElementMissing,
                        SMALL_FULL_CURRENT_PERIOD_BALANCE_SHEET_PATH);
            }
        } else {
            addError(errors, mandatoryElementMissing, SMALL_FULL_CURRENT_PERIOD_PATH);
        }
        
        return errors;
    }
}
