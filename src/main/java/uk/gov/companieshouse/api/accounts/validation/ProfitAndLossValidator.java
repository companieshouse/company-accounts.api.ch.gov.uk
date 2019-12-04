package uk.gov.companieshouse.api.accounts.validation;

import java.util.Objects;
import java.util.stream.Stream;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.exception.DataException;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.GrossProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.OperatingProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossForFinancialYear;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossBeforeTax;
import uk.gov.companieshouse.api.accounts.model.validation.Errors;
import uk.gov.companieshouse.api.model.transaction.Transaction;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Optional;

@Component
public class ProfitAndLossValidator extends BaseValidator {

    private static final String PROFIT_AND_LOSS = "$.profit_and_loss";
    private static final String GROSS_PROFIT_OR_LOSS = PROFIT_AND_LOSS + ".gross_profit_or_loss";
    private static final String GROSS_TOTAL = GROSS_PROFIT_OR_LOSS + ".gross_total";
    private static final String OPERATING_PROFIT_OR_LOSS = PROFIT_AND_LOSS + ".operating_profit_or_loss";
    private static final String OPERATING_TOTAL = OPERATING_PROFIT_OR_LOSS + ".operating_total";
    private static final String PROFIT_OR_LOSS_BEFORE_TAX = PROFIT_AND_LOSS + ".profit_or_loss_before_tax";
    private static final String TOTAL_PROFIT_OR_LOSS_BEFORE_TAX = PROFIT_OR_LOSS_BEFORE_TAX + ".total_profit_or_loss_before_tax";
    private static final String PROFIT_OR_LOSS_FOR_FINANCIAL_YEAR = PROFIT_AND_LOSS + ".profit_or_loss_for_financial_year";
    private static final String TOTAL_PROFIT_OR_FOR_FINANCIAL_YEAR = PROFIT_OR_LOSS_FOR_FINANCIAL_YEAR + ".total_profit_or_loss_for_financial_year";

    public Errors validateProfitLoss(@Valid ProfitAndLoss profitAndLoss, String companyAccountsId,
                                     HttpServletRequest request, Transaction transaction) throws DataException {

        Errors errors = new Errors();

        verifyProfitAndLossNotEmpty(profitAndLoss, errors);
        if (errors.hasErrors()) {
            return errors;
        }

        validateGrossProfitTotal(profitAndLoss.getGrossProfitOrLoss(), errors);
        validateOperatingTotal(profitAndLoss, errors);
        validateProfitOrLossBeforeTax(profitAndLoss,errors);
        validateProfitOrLossForFinancialYear(profitAndLoss, errors);

        return errors;
    }

    private void verifyProfitAndLossNotEmpty(ProfitAndLoss profitAndLoss, Errors errors) {

        if (Stream.of(profitAndLoss.getGrossProfitOrLoss(),
                      profitAndLoss.getOperatingProfitOrLoss(),
                      profitAndLoss.getProfitOrLossBeforeTax(),
                      profitAndLoss.getProfitOrLossForFinancialYear()).allMatch(Objects::isNull)) {

            addError(errors, valueRequired, TOTAL_PROFIT_OR_FOR_FINANCIAL_YEAR);
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

    private void validateGrossProfitTotal(GrossProfitOrLoss grossProfitOrLoss, Errors errors) {

        Long turnover = getTurnover(grossProfitOrLoss);
        Long costOfSales = getCostOfSales(grossProfitOrLoss);
        Long grossProfitOrLossTotal = getGrossTotal(grossProfitOrLoss);

        Long total = turnover - costOfSales;

        if (!total.equals(grossProfitOrLossTotal)) {
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

    private void validateOperatingTotal(ProfitAndLoss profitAndLoss,  Errors errors) {

        Long administrativeExpenses = getAdministrativeExpenses(profitAndLoss.getOperatingProfitOrLoss());

        Long distributionCosts = getDistributionCosts(profitAndLoss.getOperatingProfitOrLoss());

        Long operatingProfitAndLossTotal = getOperatingTotal(profitAndLoss.getOperatingProfitOrLoss()) ;

        Long otherOperatingIncome = getOtherOperatingIncome(profitAndLoss.getOperatingProfitOrLoss());

        Long grossProfitOrLoss = getGrossTotal(profitAndLoss.getGrossProfitOrLoss());

        Long total =  grossProfitOrLoss - distributionCosts - administrativeExpenses + otherOperatingIncome;

        if (!total.equals(operatingProfitAndLossTotal)) {
            addError(errors, incorrectTotal, OPERATING_TOTAL);
        }
    }

    private long getInterestReceivableAndSimilarIncome(ProfitOrLossBeforeTax profitOrLossBeforeTax) {
        return Optional.ofNullable(profitOrLossBeforeTax)
                .map(ProfitOrLossBeforeTax::getInterestReceivableAndSimilarIncome)
                .orElse(0L);
    }

    private long getInterestPayableAndSimilarCharges(ProfitOrLossBeforeTax profitOrLossBeforeTax) {
        return Optional.ofNullable(profitOrLossBeforeTax)
                .map(ProfitOrLossBeforeTax::getInterestPayableAndSimilarCharges)
                .orElse(0L);
    }

    private long getTotalProfitOrLossBeforeTax(ProfitOrLossBeforeTax profitOrLossBeforeTax) {
        return Optional.ofNullable(profitOrLossBeforeTax)
                .map(ProfitOrLossBeforeTax::getTotalProfitOrLossBeforeTax)
                .orElse(0L);
    }

    private long getTax(ProfitOrLossForFinancialYear profitOrLossForFinancialYear) {
        return Optional.ofNullable(profitOrLossForFinancialYear)
                .map(ProfitOrLossForFinancialYear::getTax)
                .orElse(0L);
    }

    private long getTotalProfitOrLossForFinancialYear(ProfitOrLossForFinancialYear profitOrLossForFinancialYear) {
        return Optional.ofNullable(profitOrLossForFinancialYear)
                .map(ProfitOrLossForFinancialYear::getTotalProfitOrLossForFinancialYear)
                .orElse(0L);
    }

    private void validateProfitOrLossBeforeTax(ProfitAndLoss profitAndLoss,  Errors errors) {

        Long operatingProfitAndLossTotal = getOperatingTotal(profitAndLoss.getOperatingProfitOrLoss()) ;

        Long interestPayableAndSimilarCharges = getInterestPayableAndSimilarCharges(profitAndLoss.getProfitOrLossBeforeTax());

        Long interestReceivableAndSimilarIncome = getInterestReceivableAndSimilarIncome(profitAndLoss.getProfitOrLossBeforeTax());

        Long totalProfitOrLossBeforeTax = getTotalProfitOrLossBeforeTax(profitAndLoss.getProfitOrLossBeforeTax());

        Long total = operatingProfitAndLossTotal + interestReceivableAndSimilarIncome - interestPayableAndSimilarCharges;

        if (!total.equals(totalProfitOrLossBeforeTax)) {
            addError(errors, incorrectTotal, TOTAL_PROFIT_OR_LOSS_BEFORE_TAX);
        }

    }

    private void validateProfitOrLossForFinancialYear(ProfitAndLoss profitAndLoss, Errors errors) {

        Long totalProfitOrLossBeforeTax = getTotalProfitOrLossBeforeTax(
                profitAndLoss.getProfitOrLossBeforeTax());

        Long tax = getTax(profitAndLoss.getProfitOrLossForFinancialYear());

        Long totalProfitOrLossForFinancialYear = getTotalProfitOrLossForFinancialYear(
                profitAndLoss.getProfitOrLossForFinancialYear());

        Long total = totalProfitOrLossBeforeTax - tax;

        if (!total.equals(totalProfitOrLossForFinancialYear)) {
            addError(errors, incorrectTotal, TOTAL_PROFIT_OR_FOR_FINANCIAL_YEAR);
        }

    }
}
