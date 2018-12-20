package uk.gov.companieshouse.api.accounts.validation;

import com.mongodb.MongoException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.notes.Debtors.Debtors;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.accounts.service.impl.CurrentPeriodService;
import uk.gov.companieshouse.api.accounts.service.impl.DebtorsService;
import uk.gov.companieshouse.api.accounts.service.impl.PreviousPeriodService;
import uk.gov.companieshouse.api.accounts.service.response.ResponseObject;
import uk.gov.companieshouse.api.accounts.transaction.Transaction;
import uk.gov.companieshouse.api.model.company.CompanyProfileApi;

@Component
public class DebtorsValidator extends BaseValidator implements CrossValidator {

    @Value("${invalid.note}")
    private String invalidNote;

    @Value("${inconsistent.data}")
    private String inconsistentData;

    @Value("${current.balancesheet.not.equal}")
    private String currentBalanceSheetNotEqual;

    @Value("${previous.balancesheet.not.equal}")
    private String previousBalanceSheetNotEqual;

    private static final String DEBTORS_PATH = "$.debtors";
    private static final String DEBTORS_PATH_PREVIOUS = DEBTORS_PATH + ".previous_period";
    private static final String CURRENT_TOTAL_PATH = DEBTORS_PATH + ".current_period.total";
    private static final String PREVIOUS_TOTAL_PATH = DEBTORS_PATH_PREVIOUS + ".total";
    private static final String PREVIOUS_TRADE_DEBTORS = DEBTORS_PATH_PREVIOUS + ".trade_debtors";
    private static final String PREVIOUS_PREPAYMENTS =
        DEBTORS_PATH_PREVIOUS + ".prepayments_and_accrued_income";
    private static final String PREVIOUS_OTHER_DEBTORS = DEBTORS_PATH_PREVIOUS + ".other_debtors";
    private static final String PREVIOUS_GREATER_THAN_ONE_YEAR =
        DEBTORS_PATH_PREVIOUS + ".greater_than_one_year";

    private CompanyService companyService;
    private DebtorsService debtorsService;

    @Autowired
    private CurrentPeriodService currentPeriodService;

    @Autowired
    private PreviousPeriodService previousPeriodService;;

    @Autowired
    public DebtorsValidator(CompanyService companyService, DebtorsService debtorsService) throws DataException {
        this.companyService = companyService;
        this.debtorsService = debtorsService;
    }

    public Errors validateDebtors(@Valid Debtors debtors, Transaction transaction,
                                  String companyAccountsId,
                                  HttpServletRequest request) throws DataException {

        Errors errors = new Errors();

        if (debtors != null) {

            crossValidate(errors, request, companyAccountsId);

            if (debtors.getCurrentPeriod() != null) {

                validateCurrentPeriodDebtors(errors, debtors);
            }

            if (debtors.getPreviousPeriod() != null) {

                if (isMultipleYearFiler(transaction)) {

                    validatePreviousPeriodDebtors(errors, debtors);

                } else {

                    validateInconsistentPeriodFiling(debtors, errors);
                }
            }

        }
        return errors;
    }

    private void validatePreviousPeriodDebtors(Errors errors, Debtors debtors) {

        validateRequiredPreviousPeriodTotalFieldNotNull(debtors, errors);
        validatePreviousPeriodTotalIsCorrect(debtors, errors);
    }

    private void addInconsistentDataError(Errors errors, String errorPath) {

        addError(errors, inconsistentData, errorPath);
    }

    private void validateCurrentPeriodDebtors(Errors errors, Debtors debtors) {

        validateRequiredCurrentPeriodTotalFieldNotNull(debtors, errors);
        validateCurrentPeriodTotalIsCorrect(debtors, errors);
    }

    private void validateRequiredCurrentPeriodTotalFieldNotNull(Debtors debtors, Errors errors) {

        if ((debtors.getCurrentPeriod().getTradeDebtors() != null ||
            debtors.getCurrentPeriod().getPrepaymentsAndAccruedIncome() != null ||
            debtors.getCurrentPeriod().getOtherDebtors() != null ||
            debtors.getCurrentPeriod().getGreaterThanOneYear() != null ||
            !debtors.getCurrentPeriod().getDetails().isEmpty()) &&
            debtors.getCurrentPeriod().getTotal() == null) {

            addError(errors, invalidNote, CURRENT_TOTAL_PATH);
        }
    }

    private void validateRequiredPreviousPeriodTotalFieldNotNull(Debtors debtors, Errors errors) {

        if ((debtors.getPreviousPeriod().getTradeDebtors() != null ||
            debtors.getPreviousPeriod().getPrepaymentsAndAccruedIncome() != null ||
            debtors.getPreviousPeriod().getOtherDebtors() != null ||
            debtors.getPreviousPeriod().getGreaterThanOneYear() != null) &&
            debtors.getPreviousPeriod().getTotal() == null) {

            addError(errors, invalidNote, PREVIOUS_TOTAL_PATH);
        }
    }

    private void validateCurrentPeriodTotalIsCorrect(Debtors debtors, Errors errors) {

        if (debtors.getCurrentPeriod().getTotal() != null) {
            Long traderDebtors =
                Optional.ofNullable(debtors.getCurrentPeriod().getTradeDebtors()).orElse(0L);
            Long prepayments =
                Optional.ofNullable(debtors.getCurrentPeriod().getPrepaymentsAndAccruedIncome())
                        .orElse(0L);
            Long otherDebtors =
                Optional.ofNullable(debtors.getCurrentPeriod().getOtherDebtors()).orElse(0L);
            Long moreThanOneYear =
                Optional.ofNullable(debtors.getCurrentPeriod().getGreaterThanOneYear()).orElse(0L);

            Long total = debtors.getCurrentPeriod().getTotal();

            Long sum = traderDebtors + prepayments + otherDebtors + moreThanOneYear;

            validateAggregateTotal(total, sum, CURRENT_TOTAL_PATH, errors);
        }
    }

    private void validatePreviousPeriodTotalIsCorrect(Debtors debtors, Errors errors) {

        if (debtors.getPreviousPeriod().getTotal() != null) {
            Long traderDebtors =
                Optional.ofNullable(debtors.getPreviousPeriod().getTradeDebtors()).orElse(0L);
            Long prepayments =
                Optional.ofNullable(debtors.getPreviousPeriod().getPrepaymentsAndAccruedIncome())
                        .orElse(0L);
            Long otherDebtors =
                Optional.ofNullable(debtors.getPreviousPeriod().getOtherDebtors()).orElse(0L);
            Long moreThanOneYear =
                Optional.ofNullable(debtors.getPreviousPeriod().getGreaterThanOneYear()).orElse(0L);

            Long total = debtors.getPreviousPeriod().getTotal();

            Long sum = traderDebtors + prepayments + otherDebtors + moreThanOneYear;

            validateAggregateTotal(total, sum, PREVIOUS_TOTAL_PATH, errors);
        }
    }

    private boolean isMultipleYearFiler(Transaction transaction) throws DataException {

        try {
            CompanyProfileApi companyProfile =
                companyService.getCompanyProfile(transaction.getCompanyNumber());
            return (companyProfile != null && companyProfile.getAccounts() != null &&
                companyProfile.getAccounts().getLastAccounts() != null &&
                companyProfile.getAccounts().getLastAccounts().getPeriodStartOn() != null);

        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }
    }

    private void validateInconsistentPeriodFiling(Debtors debtors, Errors errors) {

        if (debtors.getPreviousPeriod().getTradeDebtors() != null) {
            addInconsistentDataError(errors, PREVIOUS_TRADE_DEBTORS);
        }

        if (debtors.getPreviousPeriod().getGreaterThanOneYear() != null) {
            addInconsistentDataError(errors, PREVIOUS_GREATER_THAN_ONE_YEAR);
        }

        if (debtors.getPreviousPeriod().getPrepaymentsAndAccruedIncome() != null) {
            addInconsistentDataError(errors, PREVIOUS_PREPAYMENTS);
        }

        if (debtors.getPreviousPeriod().getOtherDebtors() != null) {
            addInconsistentDataError(errors, PREVIOUS_OTHER_DEBTORS);
        }

        if (debtors.getPreviousPeriod().getTotal() != null) {
            addInconsistentDataError(errors, PREVIOUS_TOTAL_PATH);
        }
    }


    @Override
    public Errors crossValidate(Errors errors, HttpServletRequest request,
                                String CompanyAccountsId) throws DataException {

        String currentPeriodId = currentPeriodService.generateID(CompanyAccountsId);

        // Get Debtors resource
        ResponseObject<Debtors> debtorsResponseObject;

        try {
            debtorsResponseObject = debtorsService.findById(currentPeriodId, request);
        } catch (MongoException e) {
            throw new DataException(e.getMessage(), e);
        }

        Long currentDebtorsTotalValue =
            debtorsResponseObject.getData().getCurrentPeriod().getTotal();
        Long previousDebtorsValue = debtorsResponseObject.getData().getPreviousPeriod().getTotal();

        // get current period object
        ResponseObject<CurrentPeriod> currentPeriodResponseObject;

        try {
            currentPeriodResponseObject = currentPeriodService.findById(currentPeriodId, request);
        } catch (MongoException e) {
            throw new DataException(e.getMessage(), e);
        }

        Long balanceSheetDebtorsCurrent =
            currentPeriodResponseObject.getData().getBalanceSheet().getCurrentAssets().getDebtors();

        if (currentDebtorsTotalValue != balanceSheetDebtorsCurrent) {
            addError(errors, currentBalanceSheetNotEqual, CURRENT_TOTAL_PATH);

        }

//            // get previous period object
//            ResponseObject<PreviousPeriod> periodResponseObject;
//
//            try {
//                periodResponseObject = previousPeriodService.findById(currentPeriodId, request);
//            } catch (MongoException e) {
//                throw new DataException(e.getMessage(), e);
//            }
//            Long balanceSheetDebtorsPrevious =
//                periodResponseObject.getData().getBalanceSheet().getCurrentAssets().getDebtors();
//
//
//            if (previousDebtorsValue != balanceSheetDebtorsPrevious) {
//                addError(errors, previousBalanceSheetNotEqual, PREVIOUS_TOTAL_PATH);
//
//            }
        return errors;
    }
}
