package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.stocks.Stocks;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Component
public class StocksValidator extends BaseValidator implements CrossValidator<Stocks> {

    private static final String STOCKS_PATH = "$.stocks";
    private static final String CURRENT_PERIOD_PATH = STOCKS_PATH + ".current_period";
    private static final String PREVIOUS_PERIOD_PATH = STOCKS_PATH + ".previous_period";
    private static final String CURRENT_PERIOD_TOTAL_PATH = CURRENT_PERIOD_PATH + ".total";
    private static final String PREVIOUS_PERIOD_TOTAL_PATH = PREVIOUS_PERIOD_PATH + ".total";
    private static final String PREVIOUS_PERIOD_PAYMENTS_ON_ACCOUNT_PATH = PREVIOUS_PERIOD_PATH + ".payments_on_account";
    private static final String PREVIOUS_PERIOD_STOCKS_PATH = PREVIOUS_PERIOD_PATH + ".stocks";

    private CompanyService companyService;
    private CurrentPeriodService currentPeriodService;
    private PreviousPeriodService previousPeriodService;

    @Autowired
    public StocksValidator(CompanyService companyService, CurrentPeriodService currentPeriodService,
                           PreviousPeriodService previousPeriodService) {
        this.companyService = companyService;
        this.currentPeriodService = currentPeriodService;
        this.previousPeriodService = previousPeriodService;
    }

    public Errors validateStocks(@Valid Stocks stocks, Transaction transaction,
                                 String companyAccountsId,
                                 HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        if (stocks.getCurrentPeriod() != null) {
            validateCurrentPeriod(stocks, errors);
            crossValidateCurrentPeriod(errors, request, stocks, companyAccountsId);
        }

        if (stocks.getPreviousPeriod() != null) {

            try {
                if (companyService.isMultipleYearFiler(transaction)) {
                    validatePreviousPeriod(stocks, errors);
                    crossValidatePreviousPeriod(errors, request, stocks, companyAccountsId);
                } else {
                    validateInconsistentFiling(stocks, errors);
                }

            } catch (ServiceException e) {
                throw new DataException(e.getMessage(), e);
            }
        }
        return errors;
    }

    private void validateCurrentPeriod(Stocks stocks, Errors errors) {
        validateCurrentPeriodTotalPresent(stocks, errors);
        validateCurrentPeriodTotalCorrect(stocks, errors);
    }

    private void validatePreviousPeriod(Stocks stocks, Errors errors) {
        validatePreviousPeriodTotalPresent(stocks, errors);
        validatePreviousPeriodTotalCorrect(stocks, errors);
    }

    private void validateInconsistentFiling(Stocks stocks, Errors errors) {

        if (stocks.getPreviousPeriod().getPaymentsOnAccount() != null) {
            addInconsistentDataError(errors, PREVIOUS_PERIOD_PAYMENTS_ON_ACCOUNT_PATH);
        }

        if (stocks.getPreviousPeriod().getStocks() != null) {
            addInconsistentDataError(errors, PREVIOUS_PERIOD_STOCKS_PATH);
        }

        if (stocks.getPreviousPeriod().getTotal() != null) {
            addInconsistentDataError(errors, PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void validateCurrentPeriodTotalPresent(Stocks stocks, Errors errors) {
        if (stocks.getCurrentPeriod().getTotal() == null &&
                (stocks.getCurrentPeriod().getPaymentsOnAccount() != null ||
                        stocks.getCurrentPeriod().getStocks() != null)) {

            addError(errors, invalidNote, CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void validatePreviousPeriodTotalPresent(Stocks stocks, Errors errors) {
        if (stocks.getPreviousPeriod().getTotal() == null &&
                (stocks.getPreviousPeriod().getPaymentsOnAccount() != null ||
                        stocks.getPreviousPeriod().getStocks() != null)) {

            addError(errors, invalidNote, PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void validateCurrentPeriodTotalCorrect(Stocks stocks, Errors errors) {

        if (stocks.getCurrentPeriod().getTotal() == null) {
            return;
        }

        Long stocksAmount =
                Optional.ofNullable(stocks.getCurrentPeriod().getStocks()).orElse(0L);
        Long paymentsOnAccount =
                Optional.ofNullable(stocks.getCurrentPeriod().getPaymentsOnAccount()).orElse(0L);
        Long total = stocks.getCurrentPeriod().getTotal();
        Long sum = stocksAmount + paymentsOnAccount;

        validateAggregateTotal(total, sum,
                CURRENT_PERIOD_TOTAL_PATH, errors);
    }

    private void validatePreviousPeriodTotalCorrect(Stocks stocks, Errors errors) {

        if (stocks.getPreviousPeriod().getTotal() == null) {
            return;
        }

        Long stocksAmount =
                Optional.ofNullable(stocks.getPreviousPeriod().getStocks()).orElse(0L);
        Long paymentsOnAccount =
                Optional.ofNullable(stocks.getPreviousPeriod().getPaymentsOnAccount()).orElse(0L);
        Long total = stocks.getPreviousPeriod().getTotal();
        Long sum = stocksAmount + paymentsOnAccount;

        validateAggregateTotal(total, sum,
                PREVIOUS_PERIOD_TOTAL_PATH, errors);
    }

    @Override
    public Errors crossValidate(Errors errors, HttpServletRequest request,
                                String companyAccountsId, Stocks stocks)
            throws DataException {

        crossValidateCurrentPeriod(errors, request, stocks, companyAccountsId);
        crossValidatePreviousPeriod(errors, request, stocks, companyAccountsId);

        return errors;
    }

    private void crossValidateCurrentPeriod(Errors errors, HttpServletRequest request,
                                            Stocks stocks, String companyAccountsId)
            throws DataException {

        BalanceSheet currentPeriodBalanceSheet =
                getCurrentPeriodBalanceSheet(request, companyAccountsId);

        checkIfCurrentNoteIsNullAndBalanceSheetNot(errors, stocks, currentPeriodBalanceSheet);
        checkIfCurrentBalanceSheetIsNullAndNoteNot(errors, stocks, currentPeriodBalanceSheet);
        checkIfCurrentBalanceAndNoteValuesAreEqual(errors, stocks, currentPeriodBalanceSheet);
    }

    private void crossValidatePreviousPeriod(Errors errors, HttpServletRequest request,
                                             Stocks stocks, String companyAccountsId)
            throws DataException {

        BalanceSheet previousPeriodBalanceSheet =
                getPreviousPeriodBalanceSheet(request, companyAccountsId);

        checkIfPreviousNoteIsNullAndBalanceNot(errors, stocks, previousPeriodBalanceSheet);
        checkIsPreviousBalanceNullAndNoteNot(errors, stocks, previousPeriodBalanceSheet);
        checkIfPreviousBalanceAndNoteValuesAreEqual(errors, stocks, previousPeriodBalanceSheet);
    }

    private BalanceSheet getCurrentPeriodBalanceSheet(HttpServletRequest request,
                                                      String companyAccountsId) throws DataException {

        String currentPeriodId = currentPeriodService.generateID(companyAccountsId);

        ResponseObject<CurrentPeriod> currentPeriodResponseObject =
                currentPeriodService.findById(currentPeriodId, request);

        if (currentPeriodResponseObject != null && currentPeriodResponseObject.getData() != null) {
            return currentPeriodResponseObject.getData().getBalanceSheet();
        }

        return null;
    }


    private void checkIfCurrentBalanceAndNoteValuesAreEqual(Errors errors, Stocks stocks,
                                                            BalanceSheet currentPeriodBalanceSheet) {

        if (!(isCurrentPeriodBalanceSheetStocksNull(currentPeriodBalanceSheet))
                && !isStocksNoteCurrentTotalNull(stocks)
                && !(stocks.getCurrentPeriod().getTotal().equals(
                currentPeriodBalanceSheet.getCurrentAssets()
                        .getStocks()))) {

            addError(errors, currentBalanceSheetNotEqual, CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentBalanceSheetIsNullAndNoteNot(Errors errors, Stocks stocks,
                                                            BalanceSheet currentPeriodBalanceSheet) {
        if (isCurrentPeriodBalanceSheetStocksNull(currentPeriodBalanceSheet) &&
                !isStocksNoteCurrentTotalNull(stocks)) {

            addError(errors, currentBalanceSheetNotEqual, CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfCurrentNoteIsNullAndBalanceSheetNot(Errors errors, Stocks stocks,
                                                            BalanceSheet currentPeriodBalanceSheet) {
        if (isStocksNoteCurrentTotalNull(stocks) &&
                !isCurrentPeriodBalanceSheetStocksNull(currentPeriodBalanceSheet)) {

            addError(errors, currentBalanceSheetNotEqual, CURRENT_PERIOD_TOTAL_PATH);
        }
    }

    private boolean isStocksNoteCurrentTotalNull(Stocks stocks) {
        return stocks.getCurrentPeriod() == null ||
                stocks.getCurrentPeriod().getTotal() == null;
    }

    private boolean isCurrentPeriodBalanceSheetStocksNull(
            BalanceSheet currentPeriodBalanceSheet) {

        return currentPeriodBalanceSheet == null ||
                currentPeriodBalanceSheet.getCurrentAssets() == null ||
                currentPeriodBalanceSheet.getCurrentAssets().getStocks() == null;
    }


    private BalanceSheet getPreviousPeriodBalanceSheet(HttpServletRequest request,
                                                       String companyAccountsId) throws DataException {

        String currentPeriodId = previousPeriodService.generateID(companyAccountsId);

        ResponseObject<PreviousPeriod> previousPeriodResponseObject =
                previousPeriodService.findById(currentPeriodId, request);

        if (previousPeriodResponseObject != null && previousPeriodResponseObject.getData() != null) {
            return previousPeriodResponseObject.getData().getBalanceSheet();
        }

        return null;
    }

    private void checkIfPreviousNoteIsNullAndBalanceNot(Errors errors, Stocks stocks,
                                                        BalanceSheet previousPeriodBalanceSheet) {

        if (isStocksNotePreviousTotalNull(stocks) &&
                !isPreviousPeriodBalanceSheetStocksNull(previousPeriodBalanceSheet)) {

            addError(errors, previousBalanceSheetNotEqual, PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIsPreviousBalanceNullAndNoteNot(Errors errors, Stocks stocks,
                                                      BalanceSheet previousPeriodBalanceSheet) {

        if (isPreviousPeriodBalanceSheetStocksNull(previousPeriodBalanceSheet) &&
                (!isStocksNotePreviousTotalNull(stocks))) {

            addError(errors, previousBalanceSheetNotEqual, PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private void checkIfPreviousBalanceAndNoteValuesAreEqual(Errors errors, Stocks stocks,
                                                             BalanceSheet previousPeriodBalanceSheet) {

        if (!isPreviousPeriodBalanceSheetStocksNull(previousPeriodBalanceSheet)
                && !isStocksNotePreviousTotalNull(stocks)
                && !stocks.getPreviousPeriod().getTotal().equals(
                previousPeriodBalanceSheet.getCurrentAssets()
                        .getStocks())) {

            addError(errors, previousBalanceSheetNotEqual, PREVIOUS_PERIOD_TOTAL_PATH);
        }
    }

    private boolean isStocksNotePreviousTotalNull(Stocks stocks) {
        return stocks.getPreviousPeriod() == null ||
                stocks.getPreviousPeriod().getTotal() == null;
    }

    private boolean isPreviousPeriodBalanceSheetStocksNull(
            BalanceSheet previousPeriodBalanceSheet) {

        return previousPeriodBalanceSheet == null ||
                previousPeriodBalanceSheet.getCurrentAssets() == null ||
                previousPeriodBalanceSheet.getCurrentAssets().getStocks() == null;
    }
}
