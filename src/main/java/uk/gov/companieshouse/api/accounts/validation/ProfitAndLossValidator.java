package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.exception.ServiceException;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.GrossProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.OperatingProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.accounts.service.CompanyService;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Component
public class ProfitAndLossValidator extends BaseValidator {

    private static final String PROFIT_AND_LOSS = "$.profit_and_loss";
    private static final String GROSS_PROFIT_OR_LOSS = PROFIT_AND_LOSS + ".gross_profit_or_loss";
    private static final String COST_OF_SALES = GROSS_PROFIT_OR_LOSS + ".cost_of_sales";
    private static final String GROSS_TOTAL = GROSS_PROFIT_OR_LOSS + ".gross_total";
    private static final String TURNOVER = GROSS_PROFIT_OR_LOSS + ".turnover";
    private static final String OPERATING_PROFIT_OR_LOSS = PROFIT_AND_LOSS + ".operating_profit_or_loss";
    private static final String OPERATING_TOTAL = OPERATING_PROFIT_OR_LOSS + ".operating_total";

    private CompanyService companyService;

    @Value("${incorrect.total}")
    private String incorrectTotal;

    public Errors validateProfitLoss(@Valid ProfitAndLoss profitAndLoss, String companyAccountsId,
                                     HttpServletRequest request, Transaction transaction) throws DataException {

        Errors errors = new Errors();

            validateGrossProfitTotal(profitAndLoss.getGrossProfitOrLoss(), errors);
            validateOperatingTotal(profitAndLoss, errors);
            if(errors.hasErrors()) {
                return errors;
            }
            return errors;
    }

    private Long getTurnover(GrossProfitOrLoss grossProfitOrLoss) {

        return Optional.ofNullable(grossProfitOrLoss)
                .map(GrossProfitOrLoss::getTurnover)
                .orElse(0L);
    }

    private Long getCostOfSales(GrossProfitOrLoss grossProfitOrLoss) {

        return Optional.ofNullable(grossProfitOrLoss)
                .map(GrossProfitOrLoss::getCostOfSales)
                .orElse(0L);
    }

    private Long getGrossTotal(GrossProfitOrLoss grossProfitOrLoss) {

        return Optional.ofNullable(grossProfitOrLoss)
                .map(GrossProfitOrLoss::getGrossTotal)
                .orElse(0L);
    }

    private void validateGrossProfitTotal(GrossProfitOrLoss grossProfitOrLoss, Errors errors) {

        Long turnover = getTurnover(grossProfitOrLoss);
        Long costOfSales = getCostOfSales(grossProfitOrLoss);
        Long GrossProfitOrLossTotal = getGrossTotal(grossProfitOrLoss);

        Long total = turnover - costOfSales;

        if (!total.equals(GrossProfitOrLossTotal)) {
            addError(errors, incorrectTotal, GROSS_TOTAL);
        }
    }

    private Long getOtherOperatingIncome(OperatingProfitOrLoss operatingProfitOrLoss) {

        return Optional.ofNullable(operatingProfitOrLoss)
                .map(OperatingProfitOrLoss::getOtherOperatingIncome)
                .orElse(0L);
    }

    private Long getOperatingTotal(OperatingProfitOrLoss operatingProfitOrLoss) {

        return Optional.ofNullable(operatingProfitOrLoss)
                .map(OperatingProfitOrLoss::getOperatingTotal)
                .orElse(0L);
    }

    private Long getDistributionCosts(OperatingProfitOrLoss operatingProfitOrLoss) {

        return Optional.ofNullable(operatingProfitOrLoss)
                .map(OperatingProfitOrLoss::getDistributionCosts)
                .orElse(0L);
    }

    private Long getAdministrativeExpenses(OperatingProfitOrLoss operatingProfitOrLoss) {

        return Optional.ofNullable(operatingProfitOrLoss)
                .map(OperatingProfitOrLoss::getAdministrativeExpenses)
                .orElse(0L);
    }

    public void validateOperatingTotal(ProfitAndLoss profitAndLoss,  Errors errors) {

        Long administrativeExpenses = getAdministrativeExpenses(profitAndLoss.getOperatingProfitOrLoss());

        Long distributionCosts = getDistributionCosts(profitAndLoss.getOperatingProfitOrLoss());

        Long total = getOperatingTotal(profitAndLoss.getOperatingProfitOrLoss()) ;

        Long otherOperatingIncome = getOtherOperatingIncome(profitAndLoss.getOperatingProfitOrLoss());

        Long grossProfitOrLoss = profitAndLoss.getGrossProfitOrLoss().getGrossTotal();

        Long operatingTotal =  grossProfitOrLoss - distributionCosts - administrativeExpenses + otherOperatingIncome;

        if(!operatingTotal.equals(total)) {
            addError(errors, incorrectTotal, OPERATING_TOTAL);
        }
    }

    private boolean getIsMultipleYearFiler(Transaction transaction) throws DataException {
        try {
            return companyService.isMultipleYearFiler(transaction);
        } catch (ServiceException e) {
            throw new DataException(e.getMessage(), e);
        }
    }


}
