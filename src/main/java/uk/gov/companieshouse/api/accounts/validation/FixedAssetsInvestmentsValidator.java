package uk.gov.companieshouse.api.accounts.validation;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.fixedassetsinvestments.FixedAssetsInvestments;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.model.transaction.Transaction;

@Component
public class FixedAssetsInvestmentsValidator extends BaseValidator implements NoteValidator<FixedAssetsInvestments> {

    private static final String FIXED_ASSETS_DETAILS_PATH = "$.fixed_assets_investments.details";

    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;

    @Autowired
    public FixedAssetsInvestmentsValidator(CompanyService companyService, CurrentPeriodService currentPeriodService,
            PreviousPeriodService previousPeriodService) {
        super(companyService);
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    @Override
    public Errors validateSubmission(FixedAssetsInvestments fixedAssetsNote,
                                     Transaction transaction,
                                     String companyAccountsId,
                                     HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        BalanceSheet currentPeriodBalanceSheet = getCurrentPeriodBalanceSheet(request,
                companyAccountsId);
        BalanceSheet previousPeriodBalanceSheet = getPreviousPeriodBalanceSheet(request,
                companyAccountsId);

        if ((!hasCurrentBalanceSheetInvestmentsValue(currentPeriodBalanceSheet) &&
            !hasPreviousBalanceSheetInvestmentsValue(previousPeriodBalanceSheet)) &&
                fixedAssetsNote.getDetails() == null) {
            addEmptyResourceError(errors, FIXED_ASSETS_DETAILS_PATH);
        }
        if(currentPeriodBalanceSheet!=null || previousPeriodBalanceSheet!=null) {
            if ((!hasCurrentBalanceSheetInvestmentsValue(currentPeriodBalanceSheet) &&
                    !hasPreviousBalanceSheetInvestmentsValue(previousPeriodBalanceSheet)) &&
                    fixedAssetsNote.getDetails() != null) {
                addError(errors, unexpectedData, FIXED_ASSETS_DETAILS_PATH);
            }
        }

        if ((hasCurrentBalanceSheetInvestmentsValue(currentPeriodBalanceSheet) ||
                hasPreviousBalanceSheetInvestmentsValue(previousPeriodBalanceSheet)) &&
                fixedAssetsNote.getDetails() == null) {
            addError(errors, mandatoryElementMissing, FIXED_ASSETS_DETAILS_PATH);
        }
        return errors;
    }

    @Override
    public AccountingNoteType getAccountingNoteType() {

        return AccountingNoteType.SMALL_FULL_FIXED_ASSETS_INVESTMENTS;
    }

    private boolean hasPreviousBalanceSheetInvestmentsValue(BalanceSheet previousPeriodBalanceSheet) {
        return previousPeriodBalanceSheet != null && previousPeriodBalanceSheet.getFixedAssets() != null && previousPeriodBalanceSheet.getFixedAssets().getInvestments() != null;
    }

    private boolean hasCurrentBalanceSheetInvestmentsValue(BalanceSheet currentPeriodBalanceSheet) {
        return currentPeriodBalanceSheet !=null && currentPeriodBalanceSheet.getFixedAssets()!= null && currentPeriodBalanceSheet.getFixedAssets().getInvestments() !=null;
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
}