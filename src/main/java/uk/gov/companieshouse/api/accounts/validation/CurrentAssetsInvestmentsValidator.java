package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.currentassetsinvestments.CurrentAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;

@Component
public class CurrentAssetsInvestmentsValidator extends BaseValidator implements NoteValidator<CurrentAssetsInvestments> {

    private static final String CURRENT_ASSETS_DETAILS_PATH = "$.current_assets_investments.details";

    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;

    @Autowired
    public CurrentAssetsInvestmentsValidator(CurrentPeriodService currentPeriodService,
        PreviousPeriodService previousPeriodService) {
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }


    @Override
    public Errors validateSubmission(CurrentAssetsInvestments currentAssetsInvestments, Transaction transaction,
                                     String companyAccountsId, HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
            companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
            companyAccountsId);

        if ((!hasCurrentBalanceSheetInvestmentsValue(currentPeriodBalanceSheet) &&
            !hasPreviousBalanceSheetInvestmentsValue(previousPeriodBalanceSheet)) &&
            currentAssetsInvestments.getDetails() == null) {
            addEmptyResourceError(errors, CURRENT_ASSETS_DETAILS_PATH);
        }
        if(currentPeriodBalanceSheet!=null || previousPeriodBalanceSheet!=null) {
            if ((!hasCurrentBalanceSheetInvestmentsValue(currentPeriodBalanceSheet) &&
                    !hasPreviousBalanceSheetInvestmentsValue(previousPeriodBalanceSheet)) &&
                    currentAssetsInvestments.getDetails() != null) {
                addError(errors, unexpectedData, CURRENT_ASSETS_DETAILS_PATH);
            }
        }

        if ((hasCurrentBalanceSheetInvestmentsValue(currentPeriodBalanceSheet) ||
            hasPreviousBalanceSheetInvestmentsValue(previousPeriodBalanceSheet)) &&
            currentAssetsInvestments.getDetails() == null) {
            addError(errors, mandatoryElementMissing, CURRENT_ASSETS_DETAILS_PATH);
        }
        return errors;
    }

    private boolean hasPreviousBalanceSheetInvestmentsValue(BalanceSheet previousPeriodBalanceSheet) {
        return previousPeriodBalanceSheet != null && previousPeriodBalanceSheet.getCurrentAssets() != null
            && previousPeriodBalanceSheet.getCurrentAssets().getInvestments() != null;
    }

    private boolean hasCurrentBalanceSheetInvestmentsValue(BalanceSheet currentPeriodBalanceSheet) {
        return currentPeriodBalanceSheet !=null && currentPeriodBalanceSheet.getCurrentAssets()!= null
            && currentPeriodBalanceSheet.getCurrentAssets().getInvestments() !=null;
    }

    private BalanceSheet getCurrentPeriodBalanceSheet(HttpServletRequest request,
        String companyAccountsId) throws DataException {

        ResponseObject<CurrentPeriod> currentPeriodResponseObject;

        currentPeriodResponseObject = currentPeriodService.find(companyAccountsId, request);

        if (currentPeriodResponseObject != null && currentPeriodResponseObject.getData() != null) {
            return currentPeriodResponseObject.getData().getBalanceSheet();
        } else {
            return null;
        }
    }

    private BalanceSheet getPreviousPeriodBalanceSheet(
        HttpServletRequest request, String companyAccountsId) throws DataException {

        ResponseObject<uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod> previousPeriodResponseObject;

        previousPeriodResponseObject = previousPeriodService.find(companyAccountsId, request);

        if (previousPeriodResponseObject != null && previousPeriodResponseObject.getData() != null) {
            return previousPeriodResponseObject.getData().getBalanceSheet();
        } else {
            return null;
        }
    }

    @Override
    public AccountingNoteType getAccountingNoteType() {
        return AccountingNoteType.SMALL_FULL_CURRENT_ASSETS_INVESTMENTS;
    }

}
