package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodDebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.DebtorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.PreviousPeriodDebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriodDebtors;
import uk.gov.companieshouse.api.accounts.model.rest.Debtors;
import uk.gov.companieshouse.api.accounts.model.rest.PreviousPeriodDebtors;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DebtorsTransformerTest {

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final String DETAILS = "debtors note text";
    private static final Long GREATER_THAT_ONE_YEAR = 1L;
    private static final Long OTHER_DEBTORS = 2L;
    private static final Long PREPAYMENTS = 3L;
    private static final Long TRADE_DEBTORS = 4L;
    private static final Long TOTAL = 10L;

    private static final Long GREATER_THAT_ONE_YEAR_PREVIOUS = 2L;
    private static final Long OTHER_DEBTORS_PREVIOUS = 4L;
    private static final Long PREPAYMENTS_PREVIOUS = 6L;
    private static final Long TRADE_DEBTORS_PREVIOUS = 8L;
    private static final Long TOTAL_PREVIOUS = 20L;

    private DebtorsTransformer debtorsTransformer = new DebtorsTransformer();

    @Test
    @DisplayName("Tests debtors transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {

        DebtorsEntity debtorsEntity = debtorsTransformer.transform(new Debtors());

        assertNotNull(debtorsEntity);
        assertNull(debtorsEntity.getData().getEtag());
        assertEquals(new HashMap<>(), debtorsEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests debtors transformer with empty previous period")
    public void testTransformerWithEmptyPreviousPeriod() {

        Debtors debtors = new Debtors();
        CurrentPeriodDebtors currentPeriodDebtors = new CurrentPeriodDebtors();

        debtors.setEtag(ETAG);
        debtors.setKind(KIND);
        debtors.setLinks(new HashMap<>());

        currentPeriodDebtors.setDetails(DETAILS);
        currentPeriodDebtors.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR);
        currentPeriodDebtors.setOtherDebtors(OTHER_DEBTORS);
        currentPeriodDebtors.setPrepaymentsAndAccruedIncome(PREPAYMENTS);
        currentPeriodDebtors.setTradeDebtors(TRADE_DEBTORS);
        currentPeriodDebtors.setTotal(TOTAL);

        debtors.setCurrentPeriodDebtors(currentPeriodDebtors);

        DebtorsEntity debtorsEntity = debtorsTransformer.transform(debtors);

        assertNotNull(debtorsEntity);
        assertNull(debtorsEntity.getData().getPreviousPeriodDebtorsEntity());
        assertEquals(new HashMap<>(), debtorsEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests debtors transformer with populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        Debtors debtors = new Debtors();
        CurrentPeriodDebtors currentPeriodDebtors = new CurrentPeriodDebtors();
        PreviousPeriodDebtors previousPeriodDebtors = new PreviousPeriodDebtors();

        debtors.setEtag(ETAG);
        debtors.setKind(KIND);
        debtors.setLinks(new HashMap<>());

        currentPeriodDebtors.setDetails(DETAILS);
        currentPeriodDebtors.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR);
        currentPeriodDebtors.setOtherDebtors(OTHER_DEBTORS);
        currentPeriodDebtors.setPrepaymentsAndAccruedIncome(PREPAYMENTS);
        currentPeriodDebtors.setTradeDebtors(TRADE_DEBTORS);
        currentPeriodDebtors.setTotal(TOTAL);

        previousPeriodDebtors.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR_PREVIOUS);
        previousPeriodDebtors.setOtherDebtors(OTHER_DEBTORS_PREVIOUS);
        previousPeriodDebtors.setPrepaymentsAndAccruedIncome(PREPAYMENTS_PREVIOUS);
        previousPeriodDebtors.setTradeDebtors(TRADE_DEBTORS_PREVIOUS);
        previousPeriodDebtors.setTotal(TOTAL_PREVIOUS);

        debtors.setCurrentPeriodDebtors(currentPeriodDebtors);
        debtors.setPreviousPeriodDebtors(previousPeriodDebtors);

        DebtorsEntity debtorsEntity = debtorsTransformer.transform(debtors);
        DebtorsDataEntity debtorsDataEntity = debtorsEntity.getData();

        assertEquals(ETAG, debtorsDataEntity.getEtag());
        assertEquals(KIND, debtorsDataEntity.getKind());
        assertEquals(new HashMap<>(), debtorsDataEntity.getLinks());

        assertEquals(DETAILS, debtorsDataEntity.getCurrentPeriodDebtorsEntity().getDetails());
        assertEquals(GREATER_THAT_ONE_YEAR, debtorsDataEntity.getCurrentPeriodDebtorsEntity().getGreaterThanOneYear());
        assertEquals(OTHER_DEBTORS, debtorsDataEntity.getCurrentPeriodDebtorsEntity().getOtherDebtors());
        assertEquals(PREPAYMENTS, debtorsDataEntity.getCurrentPeriodDebtorsEntity().getPrepaymentsAndAccruedIncome());
        assertEquals(TRADE_DEBTORS, debtorsDataEntity.getCurrentPeriodDebtorsEntity().getTradeDebtors());
        assertEquals(TOTAL, debtorsDataEntity.getCurrentPeriodDebtorsEntity().getTotal());

        assertEquals(GREATER_THAT_ONE_YEAR_PREVIOUS, debtorsDataEntity.getPreviousPeriodDebtorsEntity().getGreaterThanOneYear());
        assertEquals(OTHER_DEBTORS_PREVIOUS, debtorsDataEntity.getPreviousPeriodDebtorsEntity().getOtherDebtors());
        assertEquals(PREPAYMENTS_PREVIOUS, debtorsDataEntity.getPreviousPeriodDebtorsEntity().getPrepaymentsAndAccruedIncome());
        assertEquals(TRADE_DEBTORS_PREVIOUS, debtorsDataEntity.getPreviousPeriodDebtorsEntity().getTradeDebtors());
        assertEquals(TOTAL_PREVIOUS, debtorsDataEntity.getPreviousPeriodDebtorsEntity().getTotal());

    }

    @Test
    @DisplayName("Tests debtors transformer with populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {

        DebtorsEntity debtorsEntity = new DebtorsEntity();
        DebtorsDataEntity debtorsDataEntity = new DebtorsDataEntity();
        CurrentPeriodDebtorsEntity currentPeriodDebtorsEntity = new CurrentPeriodDebtorsEntity();
        PreviousPeriodDebtorsEntity previousPeriodDebtorsEntity = new PreviousPeriodDebtorsEntity();

        debtorsDataEntity.setEtag(ETAG);
        debtorsDataEntity.setKind(KIND);
        debtorsDataEntity.setLinks(new HashMap<>());

        currentPeriodDebtorsEntity.setDetails(DETAILS);
        currentPeriodDebtorsEntity.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR);
        currentPeriodDebtorsEntity.setOtherDebtors(OTHER_DEBTORS);
        currentPeriodDebtorsEntity.setPrepaymentsAndAccruedIncome(PREPAYMENTS);
        currentPeriodDebtorsEntity.setTradeDebtors(TRADE_DEBTORS);
        currentPeriodDebtorsEntity.setTotal(TOTAL);

        previousPeriodDebtorsEntity.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR_PREVIOUS);
        previousPeriodDebtorsEntity.setOtherDebtors(OTHER_DEBTORS_PREVIOUS);
        previousPeriodDebtorsEntity.setPrepaymentsAndAccruedIncome(PREPAYMENTS_PREVIOUS);
        previousPeriodDebtorsEntity.setTradeDebtors(TRADE_DEBTORS_PREVIOUS);
        previousPeriodDebtorsEntity.setTotal(TOTAL_PREVIOUS);

        debtorsDataEntity.setPreviousPeriodDebtorsEntity(previousPeriodDebtorsEntity);
        debtorsDataEntity.setCurrentPeriodDebtorsEntity(currentPeriodDebtorsEntity);
        debtorsEntity.setData(debtorsDataEntity);
        Debtors debtors = debtorsTransformer.transform(debtorsEntity);

        assertNotNull(debtors);
        assertEquals(KIND, debtors.getKind());
        assertEquals(ETAG, debtors.getEtag());
        assertEquals(new HashMap<>(), debtors.getLinks());

        assertEquals(DETAILS, debtors.getCurrentPeriodDebtors().getDetails());
        assertEquals(GREATER_THAT_ONE_YEAR, debtors.getCurrentPeriodDebtors().getGreaterThanOneYear());
        assertEquals(OTHER_DEBTORS, debtors.getCurrentPeriodDebtors().getOtherDebtors());
        assertEquals(PREPAYMENTS, debtors.getCurrentPeriodDebtors().getPrepaymentsAndAccruedIncome());
        assertEquals(TRADE_DEBTORS, debtors.getCurrentPeriodDebtors().getTradeDebtors());
        assertEquals(TOTAL, debtors.getCurrentPeriodDebtors().getTotal());

        assertEquals(GREATER_THAT_ONE_YEAR_PREVIOUS, debtors.getPreviousPeriodDebtors().getGreaterThanOneYear());
        assertEquals(OTHER_DEBTORS_PREVIOUS, debtors.getPreviousPeriodDebtors().getOtherDebtors());
        assertEquals(PREPAYMENTS_PREVIOUS, debtors.getPreviousPeriodDebtors().getPrepaymentsAndAccruedIncome());
        assertEquals(TRADE_DEBTORS_PREVIOUS, debtors.getPreviousPeriodDebtors().getTradeDebtors());
        assertEquals(TOTAL_PREVIOUS, debtors.getPreviousPeriodDebtors().getTotal());
    }
}
