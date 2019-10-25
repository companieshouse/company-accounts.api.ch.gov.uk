package uk.gov.companieshouse.api.accounts.validation;

import org.springframework.beans.factory.annotation.Value;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.GrossProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

public class ProfitLossValidator extends BaseValidator {

    private static final String GROSS_PROFIT_OR_LOSS = "$.gross_profit_or_loss";
    private static final String COST_OF_SALES = ".cost_of_sales";
    private static final String GROSS_TOTAL = ".gross_total";
    private static final String TURNOVER = ".turnover";

    @Value("${incorrect.total}")
    private String incorrectTotal;

    public Errors validateProfitLoss(@Valid ProfitAndLoss profitAndLoss, String companyAccountsId,
                                     HttpServletRequest request, Transaction transaction) throws DataException {
        return null;
    }

    private void validateGrossProfitTotal(GrossProfitOrLoss grossProfitOrLoss, Errors errors) {

        Long turnover = getTurnover(grossProfitOrLoss);
        Long costOfSales = getCostOfSales(grossProfitOrLoss);
        Long GrossProfitOrLossTotal = getGrossTotal(grossProfitOrLoss);

        Long Total = turnover + costOfSales;

        if (!Total.equals(GrossProfitOrLossTotal)) {
            addError(errors, incorrectTotal, GROSS_TOTAL);
        }



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


}
