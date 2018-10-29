package uk.gov.companieshouse.api.accounts.transformer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.AccountingPoliciesEntity;
import uk.gov.companieshouse.api.accounts.model.rest.AccountingPolicies;

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
        accountingPolicies.setTangibleFixedAssetsDeprecationPolicy(TANGIBLE);
        accountingPolicies.setIntangibleFixedAssetsDeprecationPolicy(INTANGIBLE);
        accountingPolicies.setValuationInformationAndPolicy(VALUATION);
        accountingPolicies.setOtherAccountingPolicies(OTHER);
        accountingPolicies.setLinks(new HashMap<>());

        AccountingPoliciesEntity accountingPoliciesEntity = accountingPoliciesTransformer
                .transform(accountingPolicies);

        AccountingPoliciesDataEntity accountingPoliciesDataEntity = accountingPoliciesEntity.getData();

        assertNotNull(accountingPoliciesEntity);

        assertEquals(ETAG, accountingPoliciesDataEntity.getEtag());
        assertEquals(KIND, accountingPoliciesDataEntity.getKind());
        assertEquals(BASIS_OF_MEAS, accountingPoliciesDataEntity.getBasisOfMeasurementAndPreparation());
        assertEquals(TURNOVER, accountingPoliciesDataEntity.getTurnoverPolicy());
        assertEquals(TANGIBLE, accountingPoliciesDataEntity.getTangibleFixedAssetsDeprecationPolicy());
        assertEquals(INTANGIBLE, accountingPoliciesDataEntity.getIntangibleFixedAssetsDeprecationPolicy());
        assertEquals(VALUATION, accountingPoliciesDataEntity.getValuationInformationAndPolicy());
        assertEquals(OTHER, accountingPoliciesDataEntity.getOtherAccountingPolicies());
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
        accountingPoliciesDataEntity.setTangibleFixedAssetsDeprecationPolicy(TANGIBLE);
        accountingPoliciesDataEntity.setIntangibleFixedAssetsDeprecationPolicy(INTANGIBLE);
        accountingPoliciesDataEntity.setValuationInformationAndPolicy(VALUATION);
        accountingPoliciesDataEntity.setOtherAccountingPolicies(OTHER);
        accountingPoliciesDataEntity.setLinks(new HashMap<>());
        accountingPoliciesEntity.setData(accountingPoliciesDataEntity);


        AccountingPolicies accountingPolicies = accountingPoliciesTransformer.transform(accountingPoliciesEntity);


        assertNotNull(accountingPolicies);
        assertEquals(ETAG, accountingPolicies.getEtag());
        assertEquals(KIND, accountingPolicies.getKind());
        assertEquals(BASIS_OF_MEAS, accountingPolicies.getBasisOfMeasurementAndPreparation());
        assertEquals(TURNOVER, accountingPolicies.getTurnoverPolicy());
        assertEquals(TANGIBLE, accountingPolicies.getTangibleFixedAssetsDeprecationPolicy());
        assertEquals(INTANGIBLE, accountingPolicies.getIntangibleFixedAssetsDeprecationPolicy());
        assertEquals(VALUATION, accountingPolicies.getValuationInformationAndPolicy());
        assertEquals(OTHER, accountingPolicies.getOtherAccountingPolicies());
        assertEquals(new HashMap<>(), accountingPolicies.getLinks());
    }
}
