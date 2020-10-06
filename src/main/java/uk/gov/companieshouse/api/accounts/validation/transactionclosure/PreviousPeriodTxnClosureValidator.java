package uk.gov.companieshouse.api.accounts.validation.transactionclosure;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.links.SmallFullLinkType;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.service.response.ResponseStatus;
import uk.gov.companieshouse.api.accounts.validation.BaseValidator;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class PreviousPeriodTxnClosureValidator extends BaseValidator {

    private final PreviousPeriodService previousPeriodService;

    private static final String SMALL_FULL_PREVIOUS_PERIOD_PATH = "$.small_full.previous_period";
    private static final String SMALL_FULL_PREVIOUS_PERIOD_BALANCE_SHEET_PATH = SMALL_FULL_PREVIOUS_PERIOD_PATH + ".balance_sheet";

    @Autowired
    public PreviousPeriodTxnClosureValidator(CompanyService companyService,
                                           PreviousPeriodService previousPeriodService) {
        super(companyService);
        this.previousPeriodService = previousPeriodService;
    }

    public Errors isValid(String companyAccountsId, SmallFull smallFull,
            Transaction transaction, HttpServletRequest request, Errors errors) throws DataException {

        if (getIsMultipleYearFiler(transaction)) {
            if(smallFull.getLinks().get(SmallFullLinkType.PREVIOUS_PERIOD.getLink()) != null) {
                ResponseObject<PreviousPeriod> previousPeriodResponseObject =
                        previousPeriodService.find(companyAccountsId, request);
    
                if (previousPeriodResponseObject.getStatus().equals(ResponseStatus.NOT_FOUND)) {
                    addError(errors, mandatoryElementMissing, SMALL_FULL_PREVIOUS_PERIOD_PATH);
                } else if (previousPeriodResponseObject.getData().getBalanceSheet() == null) {
                    addError(errors, mandatoryElementMissing,
                            SMALL_FULL_PREVIOUS_PERIOD_BALANCE_SHEET_PATH);
                }
            } else {
                addError(errors, mandatoryElementMissing, SMALL_FULL_PREVIOUS_PERIOD_PATH);
            }
        }

        return errors;
    }
}
