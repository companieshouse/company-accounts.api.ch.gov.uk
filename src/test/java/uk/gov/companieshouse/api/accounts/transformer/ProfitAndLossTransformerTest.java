package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.GrossProfitOrLossEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.OperatingProfitOrLossEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitAndLossDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitAndLossEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitOrLossBeforeTaxEntity;
import uk.gov.companieshouse.api.accounts.model.entity.profitloss.ProfitOrLossForFinancialYearEntity;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.GrossProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.OperatingProfitOrLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitAndLoss;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossBeforeTax;
import uk.gov.companieshouse.api.accounts.model.rest.profitloss.ProfitOrLossForFinancialYear;

class ProfitAndLossTransformerTest {
    private static final Long TURNOVER = 1L;
    private static final Long COST_OF_SALES = 2L;
    private static final Long GROSS_TOTAL = 3L;

    private static final Long DISTRIBUTION_COSTS = 4L;
    private static final Long ADMIN_EXPENSES = 5L;
    private static final Long OTHER_OPERATING_INCOME = 6L;
    private static final Long OPERATING_TOTAL = 7L;

    private static final Long INTEREST_RECEIVABLE = 8L;
    private static final Long INTEREST_PAYABLE = 9L;
    private static final Long TOTAL_BEFORE_TAX = 10L;

    private static final Long TAX = 11L;
    private static final Long TOTAL_FOR_YEAR = 12L;

    private final ProfitAndLossTransformer transformer = new ProfitAndLossTransformer();

    @Test
    @DisplayName("Tests rest to entity transform for empty rest object")
    void testRestToEntityTransformWithEmptyObject() {
        ProfitAndLossEntity entity = transformer.transform(new ProfitAndLoss());

        assertNotNull(entity);
        assertNotNull(entity.getData());
        assertNull(entity.getData().getGrossProfitOrLoss());
        assertNull(entity.getData().getOperatingProfitOrLoss());
        assertNull(entity.getData().getProfitOrLossBeforeTax());
        assertNull(entity.getData().getProfitOrLossForFinancialYear());
    }

    @Test
    @DisplayName("Tests rest to entity transform for full rest object")
    void testRestToEntityTransformWithFullyPopulatedObject() {
        ProfitAndLoss profitAndLoss = new ProfitAndLoss();

        GrossProfitOrLoss grossProfitOrLoss = new GrossProfitOrLoss();
        grossProfitOrLoss.setTurnover(TURNOVER);
        grossProfitOrLoss.setCostOfSales(COST_OF_SALES);
        grossProfitOrLoss.setGrossTotal(GROSS_TOTAL);
        profitAndLoss.setGrossProfitOrLoss(grossProfitOrLoss);

        OperatingProfitOrLoss operatingProfitOrLoss = new OperatingProfitOrLoss();
        operatingProfitOrLoss.setDistributionCosts(DISTRIBUTION_COSTS);
        operatingProfitOrLoss.setAdministrativeExpenses(ADMIN_EXPENSES);
        operatingProfitOrLoss.setOtherOperatingIncome(OTHER_OPERATING_INCOME);
        operatingProfitOrLoss.setOperatingTotal(OPERATING_TOTAL);
        profitAndLoss.setOperatingProfitOrLoss(operatingProfitOrLoss);

        ProfitOrLossBeforeTax profitOrLossBeforeTax = new ProfitOrLossBeforeTax();
        profitOrLossBeforeTax.setInterestReceivableAndSimilarIncome(INTEREST_RECEIVABLE);
        profitOrLossBeforeTax.setInterestPayableAndSimilarCharges(INTEREST_PAYABLE);
        profitOrLossBeforeTax.setTotalProfitOrLossBeforeTax(TOTAL_BEFORE_TAX);
        profitAndLoss.setProfitOrLossBeforeTax(profitOrLossBeforeTax);

        ProfitOrLossForFinancialYear profitOrLossForFinancialYear = new ProfitOrLossForFinancialYear();
        profitOrLossForFinancialYear.setTax(TAX);
        profitOrLossForFinancialYear.setTotalProfitOrLossForFinancialYear(TOTAL_FOR_YEAR);
        profitAndLoss.setProfitOrLossForFinancialYear(profitOrLossForFinancialYear);

        ProfitAndLossEntity entity = transformer.transform(profitAndLoss);

        assertNotNull(entity);
        assertNotNull(entity.getData());

        assertNotNull(entity.getData().getGrossProfitOrLoss());
        assertEquals(TURNOVER, entity.getData().getGrossProfitOrLoss().getTurnover());
        assertEquals(COST_OF_SALES, entity.getData().getGrossProfitOrLoss().getCostOfSales());
        assertEquals(GROSS_TOTAL, entity.getData().getGrossProfitOrLoss().getGrossTotal());

        assertNotNull(entity.getData().getOperatingProfitOrLoss());
        assertEquals(DISTRIBUTION_COSTS, entity.getData().getOperatingProfitOrLoss().getDistributionCosts());
        assertEquals(ADMIN_EXPENSES, entity.getData().getOperatingProfitOrLoss().getAdministrativeExpenses());
        assertEquals(OTHER_OPERATING_INCOME, entity.getData().getOperatingProfitOrLoss().getOtherOperatingIncome());
        assertEquals(OPERATING_TOTAL, entity.getData().getOperatingProfitOrLoss().getOperatingTotal());

        assertNotNull(entity.getData().getProfitOrLossBeforeTax());
        assertEquals(INTEREST_RECEIVABLE, entity.getData().getProfitOrLossBeforeTax().getInterestReceivableAndSimilarIncome());
        assertEquals(INTEREST_PAYABLE, entity.getData().getProfitOrLossBeforeTax().getInterestPayableAndSimilarCharges());
        assertEquals(TOTAL_BEFORE_TAX, entity.getData().getProfitOrLossBeforeTax().getTotalProfitOrLossBeforeTax());

        assertNotNull(entity.getData().getProfitOrLossForFinancialYear());
        assertEquals(TAX, entity.getData().getProfitOrLossForFinancialYear().getTax());
        assertEquals(TOTAL_FOR_YEAR, entity.getData().getProfitOrLossForFinancialYear().getTotalProfitOrLossForFinancialYear());
    }

    @Test
    @DisplayName("Tests entity to rest transform for empty entity")
    void testEntityToRestTransformWithEmptyObject() {
        ProfitAndLossEntity entity = new ProfitAndLossEntity();
        entity.setData(new ProfitAndLossDataEntity());

        ProfitAndLoss rest = transformer.transform(entity);

        assertNotNull(rest);
        assertNull(rest.getGrossProfitOrLoss());
        assertNull(rest.getOperatingProfitOrLoss());
        assertNull(rest.getProfitOrLossBeforeTax());
        assertNull(rest.getProfitOrLossForFinancialYear());
    }

    @Test
    @DisplayName("Tests entity to rest transform for full entity")
    void testEntityToRestTransformWithFullyPopulatedObject() {
        ProfitAndLossDataEntity dataEntity = new ProfitAndLossDataEntity();

        GrossProfitOrLossEntity grossProfitOrLoss = new GrossProfitOrLossEntity();
        grossProfitOrLoss.setTurnover(TURNOVER);
        grossProfitOrLoss.setCostOfSales(COST_OF_SALES);
        grossProfitOrLoss.setGrossTotal(GROSS_TOTAL);
        dataEntity.setGrossProfitOrLoss(grossProfitOrLoss);

        OperatingProfitOrLossEntity operatingProfitOrLoss = new OperatingProfitOrLossEntity();
        operatingProfitOrLoss.setDistributionCosts(DISTRIBUTION_COSTS);
        operatingProfitOrLoss.setAdministrativeExpenses(ADMIN_EXPENSES);
        operatingProfitOrLoss.setOtherOperatingIncome(OTHER_OPERATING_INCOME);
        operatingProfitOrLoss.setOperatingTotal(OPERATING_TOTAL);
        dataEntity.setOperatingProfitOrLoss(operatingProfitOrLoss);

        ProfitOrLossBeforeTaxEntity profitOrLossBeforeTax = new ProfitOrLossBeforeTaxEntity();
        profitOrLossBeforeTax.setInterestReceivableAndSimilarIncome(INTEREST_RECEIVABLE);
        profitOrLossBeforeTax.setInterestPayableAndSimilarCharges(INTEREST_PAYABLE);
        profitOrLossBeforeTax.setTotalProfitOrLossBeforeTax(TOTAL_BEFORE_TAX);
        dataEntity.setProfitOrLossBeforeTax(profitOrLossBeforeTax);

        ProfitOrLossForFinancialYearEntity profitOrLossForFinancialYear = new ProfitOrLossForFinancialYearEntity();
        profitOrLossForFinancialYear.setTax(TAX);
        profitOrLossForFinancialYear.setTotalProfitOrLossForFinancialYear(TOTAL_FOR_YEAR);
        dataEntity.setProfitOrLossForFinancialYear(profitOrLossForFinancialYear);

        ProfitAndLossEntity entity = new ProfitAndLossEntity();
        entity.setData(dataEntity);

        ProfitAndLoss rest = transformer.transform(entity);

        assertNotNull(rest);

        assertNotNull(rest.getGrossProfitOrLoss());
        assertEquals(TURNOVER, rest.getGrossProfitOrLoss().getTurnover());
        assertEquals(COST_OF_SALES, rest.getGrossProfitOrLoss().getCostOfSales());
        assertEquals(GROSS_TOTAL, rest.getGrossProfitOrLoss().getGrossTotal());

        assertNotNull(rest.getOperatingProfitOrLoss());
        assertEquals(DISTRIBUTION_COSTS, rest.getOperatingProfitOrLoss().getDistributionCosts());
        assertEquals(ADMIN_EXPENSES, rest.getOperatingProfitOrLoss().getAdministrativeExpenses());
        assertEquals(OTHER_OPERATING_INCOME, rest.getOperatingProfitOrLoss().getOtherOperatingIncome());
        assertEquals(OPERATING_TOTAL, rest.getOperatingProfitOrLoss().getOperatingTotal());

        assertNotNull(rest.getProfitOrLossBeforeTax());
        assertEquals(INTEREST_RECEIVABLE, rest.getProfitOrLossBeforeTax().getInterestReceivableAndSimilarIncome());
        assertEquals(INTEREST_PAYABLE, rest.getProfitOrLossBeforeTax().getInterestPayableAndSimilarCharges());
        assertEquals(TOTAL_BEFORE_TAX, rest.getProfitOrLossBeforeTax().getTotalProfitOrLossBeforeTax());

        assertNotNull(rest.getProfitOrLossForFinancialYear());
        assertEquals(TAX, rest.getProfitOrLossForFinancialYear().getTax());
        assertEquals(TOTAL_FOR_YEAR, rest.getProfitOrLossForFinancialYear().getTotalProfitOrLossForFinancialYear());
    }
}
