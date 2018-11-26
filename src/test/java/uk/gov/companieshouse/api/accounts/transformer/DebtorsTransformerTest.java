package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.DebtorsDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.DebtorsEntity;
import uk.gov.companieshouse.api.accounts.model.rest.Debtors;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class DebtorsTransformerTest {

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final String DETAILS = "debtors note text";
    private static final Long GREATER_THAT_ONE_YEAR= 1L;
    private static final Long OTHER_DEBTORS = 2L;
    private static final Long PREPAYMENTS = 3L;
    private static final Long TRADE_DEBTORS = 4L;
    private static final Long TOTAL = 10L;


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
    @DisplayName("Tests debtors transformer with populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        Debtors debtors = new Debtors();

        debtors.setEtag(ETAG);
        debtors.setKind(KIND);
        debtors.setDetails(DETAILS);
        debtors.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR);
        debtors.setOtherDebtors(OTHER_DEBTORS);
        debtors.setPrepaymentsAndAccruedIncome(PREPAYMENTS);
        debtors.setTradeDebtors(TRADE_DEBTORS);
        debtors.setTotal(TOTAL);
        debtors.setLinks(new HashMap<>());

        DebtorsEntity debtorsEntity = debtorsTransformer.transform(debtors);
        DebtorsDataEntity debtorsDataEntity = debtorsEntity.getData();

        assertEquals(ETAG, debtorsDataEntity.getEtag());
        assertEquals(KIND, debtorsDataEntity.getKind());
        assertEquals(DETAILS, debtorsDataEntity.getDetails());
        assertEquals(GREATER_THAT_ONE_YEAR, debtorsDataEntity.getGreaterThanOneYear());
        assertEquals(OTHER_DEBTORS, debtorsDataEntity.getOtherDebtors());
        assertEquals(PREPAYMENTS, debtorsDataEntity.getPrepaymentsAndAccruedIncome());
        assertEquals(TRADE_DEBTORS, debtorsDataEntity.getTradeDebtors());
        assertEquals(TOTAL, debtorsDataEntity.getTotal());
        assertEquals(new HashMap<>(), debtorsDataEntity.getLinks());

    }

    @Test
    @DisplayName("Tests debtors transformer with populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {

       DebtorsEntity debtorsEntity = new DebtorsEntity();
       DebtorsDataEntity debtorsDataEntity = new DebtorsDataEntity();

       debtorsDataEntity.setEtag(ETAG);
       debtorsDataEntity.setKind(KIND);
       debtorsDataEntity.setDetails(DETAILS);
       debtorsDataEntity.setGreaterThanOneYear(GREATER_THAT_ONE_YEAR);
       debtorsDataEntity.setOtherDebtors(OTHER_DEBTORS);
       debtorsDataEntity.setPrepaymentsAndAccruedIncome(PREPAYMENTS);
       debtorsDataEntity.setTradeDebtors(TRADE_DEBTORS);
       debtorsDataEntity.setTotal(TOTAL);
       debtorsDataEntity.setLinks(new HashMap<>());

       debtorsEntity.setData(debtorsDataEntity);
       Debtors debtors = debtorsTransformer.transform(debtorsEntity);

       assertNotNull(debtors);
       assertEquals(KIND, debtors.getKind());
       assertEquals(ETAG, debtors.getEtag());
       assertEquals(DETAILS, debtors.getDetails());
       assertEquals(GREATER_THAT_ONE_YEAR, debtors.getGreaterThanOneYear());
       assertEquals(OTHER_DEBTORS, debtors.getOtherDebtors());
       assertEquals(PREPAYMENTS, debtors.getPrepaymentsAndAccruedIncome());
       assertEquals(TRADE_DEBTORS, debtors.getTradeDebtors());
       assertEquals(TOTAL, debtors.getTotal());
       assertEquals(new HashMap<>(), debtors.getLinks());

    }
}
