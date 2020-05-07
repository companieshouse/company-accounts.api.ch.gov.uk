package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.enumeration.AccountingNoteType;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.accountingpolicies.AccountingPoliciesDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.smallfull.notes.accountingpolicies.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.smallfull.notes.accountingpolicies.AccountingPolicies;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AccountingPoliciesTransformerTest {

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final String BASIS_OF_MEAS = "basis of measurement policy";
    private static final String TURNOVER = "turnover policy";
    private static final String TANGIBLE = "tangible policy";
    private static final String INTANGIBLE = "intangible policy";
    private static final String VALUATION = "valuation policy";
    private static final String OTHER = "other policies";

    private AccountingPoliciesTransformer accountingPoliciesTransformer = new AccountingPoliciesTransformer();

    @Test
    @DisplayName("Tests accountingPolicies  transformer with empty object which should result in null values")
    public void testTransformerWithEmptyObject() {
        AccountingPoliciesEntity accountingPoliciesEntity = accountingPoliciesTransformer
                .transform(new AccountingPolicies());

        assertNotNull(accountingPoliciesEntity);
        assertNull(accountingPoliciesEntity.getData().getEtag());
        assertEquals(new HashMap<>(), accountingPoliciesEntity.getData().getLinks());
    }

    @Test
    @DisplayName("Tests accountingPolicies transformer with populated Rest object and validates values returned")
    public void testRestToEntityTransformerWithPopulatedObject() {

        AccountingPolicies accountingPolicies = new AccountingPolicies();

        accountingPolicies.setEtag(ETAG);
        accountingPolicies.setKind(KIND);
        accountingPolicies.setBasisOfMeasurementAndPreparation(BASIS_OF_MEAS);
        accountingPolicies.setTurnoverPolicy(TURNOVER);
        accountingPolicies.setTangibleFixedAssetsDepreciationPolicy(TANGIBLE);
        accountingPolicies.setIntangibleFixedAssetsAmortisationPolicy(INTANGIBLE);
        accountingPolicies.setValuationInformationAndPolicy(VALUATION);
        accountingPolicies.setOtherAccountingPolicy(OTHER);
        accountingPolicies.setLinks(new HashMap<>());

        AccountingPoliciesEntity accountingPoliciesEntity = accountingPoliciesTransformer
                .transform(accountingPolicies);

        AccountingPoliciesDataEntity accountingPoliciesDataEntity = accountingPoliciesEntity.getData();

        assertNotNull(accountingPoliciesEntity);

        assertEquals(ETAG, accountingPoliciesDataEntity.getEtag());
        assertEquals(KIND, accountingPoliciesDataEntity.getKind());
        assertEquals(BASIS_OF_MEAS, accountingPoliciesDataEntity.getBasisOfMeasurementAndPreparation());
        assertEquals(TURNOVER, accountingPoliciesDataEntity.getTurnoverPolicy());
        assertEquals(TANGIBLE, accountingPoliciesDataEntity.getTangibleFixedAssetsDepreciationPolicy());
        assertEquals(INTANGIBLE, accountingPoliciesDataEntity.getIntangibleFixedAssetsAmortisationPolicy());
        assertEquals(VALUATION, accountingPoliciesDataEntity.getValuationInformationAndPolicy());
        assertEquals(OTHER, accountingPoliciesDataEntity.getOtherAccountingPolicy());
        assertEquals(new HashMap<>(), accountingPoliciesDataEntity.getLinks());
    }

    @Test
    @DisplayName("Tests accountingPolicies transformer with populated Entity object and validates values returned")
    public void testEntityToRestTransformerWithPopulatedObject() {

        AccountingPoliciesEntity accountingPoliciesEntity = new AccountingPoliciesEntity();
        AccountingPoliciesDataEntity accountingPoliciesDataEntity = new AccountingPoliciesDataEntity();

        accountingPoliciesDataEntity.setEtag(ETAG);
        accountingPoliciesDataEntity.setKind(KIND);
        accountingPoliciesDataEntity.setBasisOfMeasurementAndPreparation(BASIS_OF_MEAS);
        accountingPoliciesDataEntity.setTurnoverPolicy(TURNOVER);
        accountingPoliciesDataEntity.setTangibleFixedAssetsDepreciationPolicy(TANGIBLE);
        accountingPoliciesDataEntity.setIntangibleFixedAssetsAmortisationPolicy(INTANGIBLE);
        accountingPoliciesDataEntity.setValuationInformationAndPolicy(VALUATION);
        accountingPoliciesDataEntity.setOtherAccountingPolicy(OTHER);
        accountingPoliciesDataEntity.setLinks(new HashMap<>());
        accountingPoliciesEntity.setData(accountingPoliciesDataEntity);


        AccountingPolicies accountingPolicies = accountingPoliciesTransformer.transform(accountingPoliciesEntity);


        assertNotNull(accountingPolicies);
        assertEquals(ETAG, accountingPolicies.getEtag());
        assertEquals(KIND, accountingPolicies.getKind());
        assertEquals(BASIS_OF_MEAS, accountingPolicies.getBasisOfMeasurementAndPreparation());
        assertEquals(TURNOVER, accountingPolicies.getTurnoverPolicy());
        assertEquals(TANGIBLE, accountingPolicies.getTangibleFixedAssetsDepreciationPolicy());
        assertEquals(INTANGIBLE, accountingPolicies.getIntangibleFixedAssetsAmortisationPolicy());
        assertEquals(VALUATION, accountingPolicies.getValuationInformationAndPolicy());
        assertEquals(OTHER, accountingPolicies.getOtherAccountingPolicy());
        assertEquals(new HashMap<>(), accountingPolicies.getLinks());
    }

    @Test
    @DisplayName("Get accounting note type")
    void getAccountingNoteType() {

        assertEquals(AccountingNoteType.SMALL_FULL_ACCOUNTING_POLICIES,
                accountingPoliciesTransformer.getAccountingNoteType());
    }
}
