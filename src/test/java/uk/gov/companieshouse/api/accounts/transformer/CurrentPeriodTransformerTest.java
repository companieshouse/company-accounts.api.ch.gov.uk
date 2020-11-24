package uk.gov.companieshouse.api.accounts.transformer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.model.entity.BalanceSheetEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodDataEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentPeriodEntity;
import uk.gov.companieshouse.api.accounts.model.entity.FixedAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.MembersFundsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.OtherLiabilitiesOrAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CurrentAssetsEntity;
import uk.gov.companieshouse.api.accounts.model.entity.CapitalAndReservesEntity;

import uk.gov.companieshouse.api.accounts.model.rest.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentPeriod;
import uk.gov.companieshouse.api.accounts.model.rest.FixedAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CurrentAssets;
import uk.gov.companieshouse.api.accounts.model.rest.CapitalAndReserves;
import uk.gov.companieshouse.api.accounts.model.rest.MembersFunds;
import uk.gov.companieshouse.api.accounts.model.rest.OtherLiabilitiesOrAssets;

@ExtendWith(MockitoExtension.class)
@TestInstance(Lifecycle.PER_CLASS)
class CurrentPeriodTransformerTest {

    private static final String ETAG = "etag";
    private static final String KIND = "kind";
    private static final Map<String, String> LINKS = new HashMap<>();

    private static final Long CALLED_UP_SHARE_CAPITAL_NOT_PAID = 1L;

    private static final Long CAPITAL_AND_RESERVES_CALLED_UP_SHARE_CAPITAL = 11L;
    private static final Long CAPITAL_AND_RESERVES_OTHER_RESERVES = 12L;
    private static final Long CAPITAL_AND_RESERVES_PROFIT_AND_LOSS = 13L;
    private static final Long CAPITAL_AND_RESERVES_SHARE_PREMIUM_ACCOUNT = 14L;
    private static final Long CAPITAL_AND_RESERVES_TOTAL_SHAREHOLDERS_FUNDS = 15L;

    private static final Long CURRENT_ASSETS_CASH_AT_BANK_AND_IN_HAND = 21L;
    private static final Long CURRENT_ASSETS_DEBTORS = 22L;
    private static final Long CURRENT_ASSETS_STOCKS = 23L;
    private static final Long CURRENT_ASSETS_TOTAL = 24L;

    private static final Long FIXED_ASSETS_TANGIBLE = 31L;
    private static final Long FIXED_ASSETS_TOTAL = 32L;

    private static final Long OTHER_LIABILITIES_ACCRUALS_AND_DEFERRED_INCOME = 41L;
    private static final Long OTHER_LIABILITIES_CREDITORS_AFTER = 42L;
    private static final Long OTHER_LIABILITIES_CREDITORS_WITHIN = 43L;
    private static final Long OTHER_LIABILITIES_NET_CURRENT_ASSETS = 44L;
    private static final Long OTHER_LIABILITIES_PREPAYMENTS_AND_ACCRUED_INCOME = 45L;
    private static final Long OTHER_LIABILITIES_PROVISION_FOR_LIABILITIES = 46L;
    private static final Long OTHER_LIABILITIES_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES = 47L;
    private static final Long OTHER_LIABILITIES_TOTAL_NET_ASSETS = 48L;

    private static final Long MEMBERS_FUNDS_PROFIT_AND_LOSS_ACCOUNT = 51L;
    private static final Long MEMBERS_FUNDS_TOTAL = 52L;

    private CurrentPeriodTransformer currentPeriodTransformer = new CurrentPeriodTransformer();

    @Test
    @DisplayName("Rest to entity transform - current period without balance sheet")
    void restToEntityEmptyCurrentPeriod() {

        CurrentPeriod currentPeriod = setUpBaseCurrentPeriodRestObject();

        CurrentPeriodEntity currentPeriodEntity =
                currentPeriodTransformer.transform(currentPeriod);

        assertNotNull(currentPeriodEntity);
        assertBaseFieldsMapped(currentPeriodEntity.getData());
        assertNull(currentPeriodEntity.getData().getBalanceSheetEntity());
    }

    @Test
    @DisplayName("Rest to entity transform - current period with called up share capital not paid")
    void restToEntityCurrentPeriodWithCalledUpShareCapitalNotPaid() {

        CurrentPeriod currentPeriod = setUpBaseCurrentPeriodRestObject();

        BalanceSheet balanceSheet = new BalanceSheet();
        balanceSheet.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID);

        currentPeriod.setBalanceSheet(balanceSheet);

        CurrentPeriodEntity currentPeriodEntity =
                currentPeriodTransformer.transform(currentPeriod);

        assertNotNull(currentPeriodEntity);
        assertBaseFieldsMapped(currentPeriodEntity.getData());

        BalanceSheetEntity balanceSheetEntity = currentPeriodEntity.getData().getBalanceSheetEntity();
        assertNotNull(balanceSheetEntity);

        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID, balanceSheetEntity.getCalledUpShareCapitalNotPaid());

        assertNull(balanceSheetEntity.getFixedAssets());
        assertNull(balanceSheetEntity.getOtherLiabilitiesOrAssetsEntity());
        assertNull(balanceSheetEntity.getCurrentAssets());
        assertNull(balanceSheetEntity.getCapitalAndReservesEntity());
        assertNull(balanceSheetEntity.getMembersFundsEntity());
    }

    @Test
    @DisplayName("Rest to entity transform - current period with fixed assets")
    void restToEntityCurrentPeriodWithFixedAssets() {

        CurrentPeriod currentPeriod = setUpBaseCurrentPeriodRestObject();

        BalanceSheet balanceSheet = new BalanceSheet();
        setUpFixedAssetsOnBalanceSheet(balanceSheet);

        currentPeriod.setBalanceSheet(balanceSheet);

        CurrentPeriodEntity currentPeriodEntity =
                currentPeriodTransformer.transform(currentPeriod);

        assertNotNull(currentPeriodEntity);
        assertBaseFieldsMapped(currentPeriodEntity.getData());

        BalanceSheetEntity balanceSheetEntity = currentPeriodEntity.getData().getBalanceSheetEntity();
        assertNotNull(balanceSheetEntity);

        assertFixedAssetsMapped(balanceSheetEntity.getFixedAssets());

        assertNull(balanceSheetEntity.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheetEntity.getOtherLiabilitiesOrAssetsEntity());
        assertNull(balanceSheetEntity.getCurrentAssets());
        assertNull(balanceSheetEntity.getCapitalAndReservesEntity());
        assertNull(balanceSheetEntity.getMembersFundsEntity());
    }

    @Test
    @DisplayName("Rest to entity transform - current period with other liabilities or assets")
    void restToEntityCurrentPeriodWithOtherLiabilitiesOrAssets() {

        CurrentPeriod currentPeriod = setUpBaseCurrentPeriodRestObject();

        BalanceSheet balanceSheet = new BalanceSheet();
        setUpOtherLiabilitiesOrAssetsOnBalanceSheet(balanceSheet);

        currentPeriod.setBalanceSheet(balanceSheet);

        CurrentPeriodEntity currentPeriodEntity =
                currentPeriodTransformer.transform(currentPeriod);

        assertNotNull(currentPeriodEntity);
        assertBaseFieldsMapped(currentPeriodEntity.getData());

        BalanceSheetEntity balanceSheetEntity = currentPeriodEntity.getData().getBalanceSheetEntity();
        assertNotNull(balanceSheetEntity);

        assertOtherLiabilitiesOrAssetsMapped(balanceSheetEntity.getOtherLiabilitiesOrAssetsEntity());

        assertNull(balanceSheetEntity.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheetEntity.getFixedAssets());
        assertNull(balanceSheetEntity.getCurrentAssets());
        assertNull(balanceSheetEntity.getCapitalAndReservesEntity());
        assertNull(balanceSheetEntity.getMembersFundsEntity());
    }

    @Test
    @DisplayName("Rest to entity transform - current period with current assets")
    void restToEntityCurrentPeriodWithCurrentAssets() {

        CurrentPeriod currentPeriod = setUpBaseCurrentPeriodRestObject();

        BalanceSheet balanceSheet = new BalanceSheet();
        setUpCurrentAssetsOnBalanceSheet(balanceSheet);

        currentPeriod.setBalanceSheet(balanceSheet);

        CurrentPeriodEntity currentPeriodEntity =
                currentPeriodTransformer.transform(currentPeriod);

        assertNotNull(currentPeriodEntity);
        assertBaseFieldsMapped(currentPeriodEntity.getData());

        BalanceSheetEntity balanceSheetEntity = currentPeriodEntity.getData().getBalanceSheetEntity();
        assertNotNull(balanceSheetEntity);

        assertCurrentAssetsMapped(balanceSheetEntity.getCurrentAssets());

        assertNull(balanceSheetEntity.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheetEntity.getFixedAssets());
        assertNull(balanceSheetEntity.getOtherLiabilitiesOrAssetsEntity());
        assertNull(balanceSheetEntity.getCapitalAndReservesEntity());
        assertNull(balanceSheetEntity.getMembersFundsEntity());
    }

    @Test
    @DisplayName("Rest to entity transform - current period with capital and reserves")
    void restToEntityCurrentPeriodWithCapitalAndReserves() {

        CurrentPeriod currentPeriod = setUpBaseCurrentPeriodRestObject();

        BalanceSheet balanceSheet = new BalanceSheet();
        setUpCapitalAndReservesOnBalanceSheet(balanceSheet);

        currentPeriod.setBalanceSheet(balanceSheet);

        CurrentPeriodEntity currentPeriodEntity =
                currentPeriodTransformer.transform(currentPeriod);

        assertNotNull(currentPeriodEntity);
        assertBaseFieldsMapped(currentPeriodEntity.getData());

        BalanceSheetEntity balanceSheetEntity = currentPeriodEntity.getData().getBalanceSheetEntity();
        assertNotNull(balanceSheetEntity);

        assertCapitalAndReservesMapped(balanceSheetEntity.getCapitalAndReservesEntity());

        assertNull(balanceSheetEntity.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheetEntity.getFixedAssets());
        assertNull(balanceSheetEntity.getOtherLiabilitiesOrAssetsEntity());
        assertNull(balanceSheetEntity.getCurrentAssets());
        assertNull(balanceSheetEntity.getMembersFundsEntity());
    }

    @Test
    @DisplayName("Rest to entity transform - current period with members' funds")
    void restToEntityCurrentPeriodWithMembersFunds() {

        CurrentPeriod currentPeriod = setUpBaseCurrentPeriodRestObject();

        BalanceSheet balanceSheet = new BalanceSheet();
        setUpMembersFundsOnBalanceSheet(balanceSheet);

        currentPeriod.setBalanceSheet(balanceSheet);

        CurrentPeriodEntity currentPeriodEntity =
                currentPeriodTransformer.transform(currentPeriod);

        assertNotNull(currentPeriodEntity);
        assertBaseFieldsMapped(currentPeriodEntity.getData());

        BalanceSheetEntity balanceSheetEntity = currentPeriodEntity.getData().getBalanceSheetEntity();
        assertNotNull(balanceSheetEntity);

        assertMembersFundsMapped(balanceSheetEntity.getMembersFundsEntity());

        assertNull(balanceSheetEntity.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheetEntity.getFixedAssets());
        assertNull(balanceSheetEntity.getOtherLiabilitiesOrAssetsEntity());
        assertNull(balanceSheetEntity.getCurrentAssets());
        assertNull(balanceSheetEntity.getCapitalAndReservesEntity());
    }

    @Test
    @DisplayName("Entity to rest transform - current period without balance sheet")
    void entityToRestEmptyCurrentPeriod() {

        CurrentPeriodEntity currentPeriodEntity = setUpBaseCurrentPeriodEntity();

        CurrentPeriod currentPeriod =
                currentPeriodTransformer.transform(currentPeriodEntity);

        assertNotNull(currentPeriod);
        assertBaseFieldsMapped(currentPeriod);
        assertNull(currentPeriod.getBalanceSheet());
    }

    @Test
    @DisplayName("Entity to rest transform - current period with called up share capital not paid")
    void entityToRestCurrentPeriodWithCalledUpShareCapitalNotPaid() {

        CurrentPeriodEntity currentPeriodEntity = setUpBaseCurrentPeriodEntity();

        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        balanceSheetEntity.setCalledUpShareCapitalNotPaid(CALLED_UP_SHARE_CAPITAL_NOT_PAID);

        currentPeriodEntity.getData().setBalanceSheetEntity(balanceSheetEntity);

        CurrentPeriod currentPeriod =
                currentPeriodTransformer.transform(currentPeriodEntity);

        assertNotNull(currentPeriod);
        assertBaseFieldsMapped(currentPeriod);

        BalanceSheet balanceSheet = currentPeriod.getBalanceSheet();
        assertNotNull(balanceSheet);

        assertEquals(CALLED_UP_SHARE_CAPITAL_NOT_PAID, balanceSheet.getCalledUpShareCapitalNotPaid());

        assertNull(balanceSheet.getFixedAssets());
        assertNull(balanceSheet.getOtherLiabilitiesOrAssets());
        assertNull(balanceSheet.getCurrentAssets());
        assertNull(balanceSheet.getCapitalAndReserves());
        assertNull(balanceSheet.getMembersFunds());
    }

    @Test
    @DisplayName("Entity to rest transform - current period with fixed assets")
    void entityToRestCurrentPeriodWithFixedAssets() {

        CurrentPeriodEntity currentPeriodEntity = setUpBaseCurrentPeriodEntity();

        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        setUpFixedAssetsOnBalanceSheet(balanceSheetEntity);

        currentPeriodEntity.getData().setBalanceSheetEntity(balanceSheetEntity);

        CurrentPeriod currentPeriod =
                currentPeriodTransformer.transform(currentPeriodEntity);

        assertNotNull(currentPeriod);
        assertBaseFieldsMapped(currentPeriod);

        BalanceSheet balanceSheet = currentPeriod.getBalanceSheet();
        assertNotNull(balanceSheet);

        assertFixedAssetsMapped(balanceSheet.getFixedAssets());

        assertNull(balanceSheet.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheet.getOtherLiabilitiesOrAssets());
        assertNull(balanceSheet.getCurrentAssets());
        assertNull(balanceSheet.getCapitalAndReserves());
        assertNull(balanceSheet.getMembersFunds());
    }

    @Test
    @DisplayName("Entity to rest transform - current period with other liabilities or assets")
    void entityToRestCurrentPeriodWithOtherLiabilitiesOrAssets() {

        CurrentPeriodEntity currentPeriodEntity = setUpBaseCurrentPeriodEntity();

        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        setUpOtherLiabilitiesOrAssetsOnBalanceSheet(balanceSheetEntity);

        currentPeriodEntity.getData().setBalanceSheetEntity(balanceSheetEntity);

        CurrentPeriod currentPeriod =
                currentPeriodTransformer.transform(currentPeriodEntity);

        assertNotNull(currentPeriod);
        assertBaseFieldsMapped(currentPeriod);

        BalanceSheet balanceSheet = currentPeriod.getBalanceSheet();
        assertNotNull(balanceSheet);

        assertOtherLiabilitiesOrAssetsMapped(balanceSheet.getOtherLiabilitiesOrAssets());

        assertNull(balanceSheet.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheet.getFixedAssets());
        assertNull(balanceSheet.getCurrentAssets());
        assertNull(balanceSheet.getCapitalAndReserves());
        assertNull(balanceSheet.getMembersFunds());
    }


    @Test
    @DisplayName("Entity to rest transform - current period with current assets")
    void entityToRestCurrentPeriodWithCurrentAssets() {

        CurrentPeriodEntity currentPeriodEntity = setUpBaseCurrentPeriodEntity();

        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        setUpCurrentAssetsOnBalanceSheet(balanceSheetEntity);

        currentPeriodEntity.getData().setBalanceSheetEntity(balanceSheetEntity);

        CurrentPeriod currentPeriod =
                currentPeriodTransformer.transform(currentPeriodEntity);

        assertNotNull(currentPeriod);
        assertBaseFieldsMapped(currentPeriod);

        BalanceSheet balanceSheet = currentPeriod.getBalanceSheet();
        assertNotNull(balanceSheet);

        assertCurrentAssetsMapped(balanceSheet.getCurrentAssets());

        assertNull(balanceSheet.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheet.getFixedAssets());
        assertNull(balanceSheet.getOtherLiabilitiesOrAssets());
        assertNull(balanceSheet.getCapitalAndReserves());
        assertNull(balanceSheet.getMembersFunds());
    }

    @Test
    @DisplayName("Entity to rest transform - current period with capital and reserves")
    void entityToRestCurrentPeriodWithCapitalAndReserves() {

        CurrentPeriodEntity currentPeriodEntity = setUpBaseCurrentPeriodEntity();

        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        setUpCapitalAndReservesOnBalanceSheet(balanceSheetEntity);

        currentPeriodEntity.getData().setBalanceSheetEntity(balanceSheetEntity);

        CurrentPeriod currentPeriod =
                currentPeriodTransformer.transform(currentPeriodEntity);

        assertNotNull(currentPeriod);
        assertBaseFieldsMapped(currentPeriod);

        BalanceSheet balanceSheet = currentPeriod.getBalanceSheet();
        assertNotNull(balanceSheet);

        assertCapitalAndReservesMapped(balanceSheet.getCapitalAndReserves());

        assertNull(balanceSheet.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheet.getFixedAssets());
        assertNull(balanceSheet.getOtherLiabilitiesOrAssets());
        assertNull(balanceSheet.getCurrentAssets());
        assertNull(balanceSheet.getMembersFunds());
    }

    @Test
    @DisplayName("Entity to rest transform - current period with members' funds")
    void entityToRestCurrentPeriodWithMembersFunds() {

        CurrentPeriodEntity currentPeriodEntity = setUpBaseCurrentPeriodEntity();

        BalanceSheetEntity balanceSheetEntity = new BalanceSheetEntity();
        setUpMembersFundsOnBalanceSheet(balanceSheetEntity);

        currentPeriodEntity.getData().setBalanceSheetEntity(balanceSheetEntity);

        CurrentPeriod currentPeriod =
                currentPeriodTransformer.transform(currentPeriodEntity);

        assertNotNull(currentPeriod);
        assertBaseFieldsMapped(currentPeriod);

        BalanceSheet balanceSheet = currentPeriod.getBalanceSheet();
        assertNotNull(balanceSheet);

        assertMembersFundsMapped(balanceSheet.getMembersFunds());

        assertNull(balanceSheet.getCalledUpShareCapitalNotPaid());
        assertNull(balanceSheet.getFixedAssets());
        assertNull(balanceSheet.getOtherLiabilitiesOrAssets());
        assertNull(balanceSheet.getCurrentAssets());
        assertNull(balanceSheet.getCapitalAndReserves());
    }

    private CurrentPeriod setUpBaseCurrentPeriodRestObject() {

        CurrentPeriod currentPeriod = new CurrentPeriod();
        currentPeriod.setEtag(ETAG);
        currentPeriod.setKind(KIND);
        currentPeriod.setLinks(LINKS);

        return currentPeriod;
    }

    private CurrentPeriodEntity setUpBaseCurrentPeriodEntity() {

        CurrentPeriodDataEntity currentPeriodDataEntity = new CurrentPeriodDataEntity();
        currentPeriodDataEntity.setEtag(ETAG);
        currentPeriodDataEntity.setKind(KIND);
        currentPeriodDataEntity.setLinks(LINKS);

        CurrentPeriodEntity currentPeriodEntity = new CurrentPeriodEntity();
        currentPeriodEntity.setData(currentPeriodDataEntity);

        return currentPeriodEntity;
    }

    private void setUpFixedAssetsOnBalanceSheet(BalanceSheet balanceSheet) {

        FixedAssets fixedAssets = new FixedAssets();
        fixedAssets.setTangible(FIXED_ASSETS_TANGIBLE);
        fixedAssets.setTotal(FIXED_ASSETS_TOTAL);

        balanceSheet.setFixedAssets(fixedAssets);
    }

    private void setUpFixedAssetsOnBalanceSheet(BalanceSheetEntity balanceSheet) {

        FixedAssetsEntity fixedAssets = new FixedAssetsEntity();
        fixedAssets.setTangible(FIXED_ASSETS_TANGIBLE);
        fixedAssets.setTotal(FIXED_ASSETS_TOTAL);

        balanceSheet.setFixedAssets(fixedAssets);
    }

    private void setUpOtherLiabilitiesOrAssetsOnBalanceSheet(BalanceSheet balanceSheet) {

        OtherLiabilitiesOrAssets otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssets();
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(OTHER_LIABILITIES_ACCRUALS_AND_DEFERRED_INCOME);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(OTHER_LIABILITIES_CREDITORS_AFTER);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(OTHER_LIABILITIES_CREDITORS_WITHIN);
        otherLiabilitiesOrAssets.setNetCurrentAssets(OTHER_LIABILITIES_NET_CURRENT_ASSETS);
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(OTHER_LIABILITIES_PREPAYMENTS_AND_ACCRUED_INCOME);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(OTHER_LIABILITIES_PROVISION_FOR_LIABILITIES);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(OTHER_LIABILITIES_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES);
        otherLiabilitiesOrAssets.setTotalNetAssets(OTHER_LIABILITIES_TOTAL_NET_ASSETS);

        balanceSheet.setOtherLiabilitiesOrAssets(otherLiabilitiesOrAssets);
    }

    private void setUpOtherLiabilitiesOrAssetsOnBalanceSheet(BalanceSheetEntity balanceSheet) {

        OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssets = new OtherLiabilitiesOrAssetsEntity();
        otherLiabilitiesOrAssets.setAccrualsAndDeferredIncome(OTHER_LIABILITIES_ACCRUALS_AND_DEFERRED_INCOME);
        otherLiabilitiesOrAssets.setCreditorsAfterOneYear(OTHER_LIABILITIES_CREDITORS_AFTER);
        otherLiabilitiesOrAssets.setCreditorsDueWithinOneYear(OTHER_LIABILITIES_CREDITORS_WITHIN);
        otherLiabilitiesOrAssets.setNetCurrentAssets(OTHER_LIABILITIES_NET_CURRENT_ASSETS);
        otherLiabilitiesOrAssets.setPrepaymentsAndAccruedIncome(OTHER_LIABILITIES_PREPAYMENTS_AND_ACCRUED_INCOME);
        otherLiabilitiesOrAssets.setProvisionForLiabilities(OTHER_LIABILITIES_PROVISION_FOR_LIABILITIES);
        otherLiabilitiesOrAssets.setTotalAssetsLessCurrentLiabilities(OTHER_LIABILITIES_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES);
        otherLiabilitiesOrAssets.setTotalNetAssets(OTHER_LIABILITIES_TOTAL_NET_ASSETS);

        balanceSheet.setOtherLiabilitiesOrAssetsEntity(otherLiabilitiesOrAssets);
    }

    private void setUpCurrentAssetsOnBalanceSheet(BalanceSheet balanceSheet) {

        CurrentAssets currentAssets = new CurrentAssets();
        currentAssets.setCashAtBankAndInHand(CURRENT_ASSETS_CASH_AT_BANK_AND_IN_HAND);
        currentAssets.setDebtors(CURRENT_ASSETS_DEBTORS);
        currentAssets.setStocks(CURRENT_ASSETS_STOCKS);
        currentAssets.setTotal(CURRENT_ASSETS_TOTAL);

        balanceSheet.setCurrentAssets(currentAssets);
    }

    private void setUpCurrentAssetsOnBalanceSheet(BalanceSheetEntity balanceSheet) {

        CurrentAssetsEntity currentAssets = new CurrentAssetsEntity();
        currentAssets.setCashAtBankAndInHand(CURRENT_ASSETS_CASH_AT_BANK_AND_IN_HAND);
        currentAssets.setDebtors(CURRENT_ASSETS_DEBTORS);
        currentAssets.setStocks(CURRENT_ASSETS_STOCKS);
        currentAssets.setTotal(CURRENT_ASSETS_TOTAL);

        balanceSheet.setCurrentAssets(currentAssets);
    }

    private void setUpCapitalAndReservesOnBalanceSheet(BalanceSheet balanceSheet) {

        CapitalAndReserves capitalAndReserves = new CapitalAndReserves();
        capitalAndReserves.setCalledUpShareCapital(CAPITAL_AND_RESERVES_CALLED_UP_SHARE_CAPITAL);
        capitalAndReserves.setOtherReserves(CAPITAL_AND_RESERVES_OTHER_RESERVES);
        capitalAndReserves.setProfitAndLoss(CAPITAL_AND_RESERVES_PROFIT_AND_LOSS);
        capitalAndReserves.setSharePremiumAccount(CAPITAL_AND_RESERVES_SHARE_PREMIUM_ACCOUNT);
        capitalAndReserves.setTotalShareholdersFunds(CAPITAL_AND_RESERVES_TOTAL_SHAREHOLDERS_FUNDS);

        balanceSheet.setCapitalAndReserves(capitalAndReserves);
    }

    private void setUpCapitalAndReservesOnBalanceSheet(BalanceSheetEntity balanceSheet) {

        CapitalAndReservesEntity capitalAndReserves = new CapitalAndReservesEntity();
        capitalAndReserves.setCalledUpShareCapital(CAPITAL_AND_RESERVES_CALLED_UP_SHARE_CAPITAL);
        capitalAndReserves.setOtherReserves(CAPITAL_AND_RESERVES_OTHER_RESERVES);
        capitalAndReserves.setProfitAndLoss(CAPITAL_AND_RESERVES_PROFIT_AND_LOSS);
        capitalAndReserves.setSharePremiumAccount(CAPITAL_AND_RESERVES_SHARE_PREMIUM_ACCOUNT);
        capitalAndReserves.setTotalShareholdersFunds(CAPITAL_AND_RESERVES_TOTAL_SHAREHOLDERS_FUNDS);

        balanceSheet.setCapitalAndReservesEntity(capitalAndReserves);
    }

    private void setUpMembersFundsOnBalanceSheet(BalanceSheet balanceSheet) {

        MembersFunds membersFunds = new MembersFunds();
        membersFunds.setProfitAndLossAccount(MEMBERS_FUNDS_PROFIT_AND_LOSS_ACCOUNT);
        membersFunds.setTotalMembersFunds(MEMBERS_FUNDS_TOTAL);

        balanceSheet.setMembersFunds(membersFunds);
    }

    private void setUpMembersFundsOnBalanceSheet(BalanceSheetEntity balanceSheet) {

        MembersFundsEntity membersFunds = new MembersFundsEntity();
        membersFunds.setProfitAndLossAccount(MEMBERS_FUNDS_PROFIT_AND_LOSS_ACCOUNT);
        membersFunds.setTotalMembersFunds(MEMBERS_FUNDS_TOTAL);

        balanceSheet.setMembersFundsEntity(membersFunds);
    }

    private void assertBaseFieldsMapped(CurrentPeriodDataEntity currentPeriodDataEntity) {

        assertNotNull(currentPeriodDataEntity);
        assertEquals(ETAG, currentPeriodDataEntity.getEtag());
        assertEquals(KIND, currentPeriodDataEntity.getKind());
        assertEquals(LINKS, currentPeriodDataEntity.getLinks());
    }

    private void assertBaseFieldsMapped(CurrentPeriod currentPeriod) {

        assertEquals(ETAG, currentPeriod.getEtag());
        assertEquals(KIND, currentPeriod.getKind());
        assertEquals(LINKS, currentPeriod.getLinks());
    }

    private void assertFixedAssetsMapped(FixedAssetsEntity fixedAssetsEntity) {

        assertNotNull(fixedAssetsEntity);
        assertEquals(FIXED_ASSETS_TANGIBLE, fixedAssetsEntity.getTangible());
        assertEquals(FIXED_ASSETS_TOTAL, fixedAssetsEntity.getTotal());
    }

    private void assertFixedAssetsMapped(FixedAssets fixedAssets) {

        assertNotNull(fixedAssets);
        assertEquals(FIXED_ASSETS_TANGIBLE, fixedAssets.getTangible());
        assertEquals(FIXED_ASSETS_TOTAL, fixedAssets.getTotal());
    }

    private void assertOtherLiabilitiesOrAssetsMapped(OtherLiabilitiesOrAssetsEntity otherLiabilitiesOrAssetsEntity) {

        assertNotNull(otherLiabilitiesOrAssetsEntity);
        assertEquals(OTHER_LIABILITIES_ACCRUALS_AND_DEFERRED_INCOME, otherLiabilitiesOrAssetsEntity.getAccrualsAndDeferredIncome());
        assertEquals(OTHER_LIABILITIES_CREDITORS_AFTER, otherLiabilitiesOrAssetsEntity.getCreditorsAfterOneYear());
        assertEquals(OTHER_LIABILITIES_CREDITORS_WITHIN, otherLiabilitiesOrAssetsEntity.getCreditorsDueWithinOneYear());
        assertEquals(OTHER_LIABILITIES_NET_CURRENT_ASSETS, otherLiabilitiesOrAssetsEntity.getNetCurrentAssets());
        assertEquals(OTHER_LIABILITIES_PREPAYMENTS_AND_ACCRUED_INCOME, otherLiabilitiesOrAssetsEntity.getPrepaymentsAndAccruedIncome());
        assertEquals(OTHER_LIABILITIES_PROVISION_FOR_LIABILITIES, otherLiabilitiesOrAssetsEntity.getProvisionForLiabilities());
        assertEquals(OTHER_LIABILITIES_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES, otherLiabilitiesOrAssetsEntity.getTotalAssetsLessCurrentLiabilities());
        assertEquals(OTHER_LIABILITIES_TOTAL_NET_ASSETS, otherLiabilitiesOrAssetsEntity.getTotalNetAssets());
    }

    private void assertOtherLiabilitiesOrAssetsMapped(OtherLiabilitiesOrAssets otherLiabilitiesOrAssets) {

        assertNotNull(otherLiabilitiesOrAssets);
        assertEquals(OTHER_LIABILITIES_ACCRUALS_AND_DEFERRED_INCOME, otherLiabilitiesOrAssets.getAccrualsAndDeferredIncome());
        assertEquals(OTHER_LIABILITIES_CREDITORS_AFTER, otherLiabilitiesOrAssets.getCreditorsAfterOneYear());
        assertEquals(OTHER_LIABILITIES_CREDITORS_WITHIN, otherLiabilitiesOrAssets.getCreditorsDueWithinOneYear());
        assertEquals(OTHER_LIABILITIES_NET_CURRENT_ASSETS, otherLiabilitiesOrAssets.getNetCurrentAssets());
        assertEquals(OTHER_LIABILITIES_PREPAYMENTS_AND_ACCRUED_INCOME, otherLiabilitiesOrAssets.getPrepaymentsAndAccruedIncome());
        assertEquals(OTHER_LIABILITIES_PROVISION_FOR_LIABILITIES, otherLiabilitiesOrAssets.getProvisionForLiabilities());
        assertEquals(OTHER_LIABILITIES_TOTAL_ASSETS_LESS_CURRENT_LIABILITIES, otherLiabilitiesOrAssets.getTotalAssetsLessCurrentLiabilities());
        assertEquals(OTHER_LIABILITIES_TOTAL_NET_ASSETS, otherLiabilitiesOrAssets.getTotalNetAssets());
    }

    private void assertCurrentAssetsMapped(CurrentAssetsEntity currentAssetsEntity) {

        assertNotNull(currentAssetsEntity);
        assertEquals(CURRENT_ASSETS_CASH_AT_BANK_AND_IN_HAND, currentAssetsEntity.getCashAtBankAndInHand());
        assertEquals(CURRENT_ASSETS_DEBTORS, currentAssetsEntity.getDebtors());
        assertEquals(CURRENT_ASSETS_STOCKS, currentAssetsEntity.getStocks());
        assertEquals(CURRENT_ASSETS_TOTAL, currentAssetsEntity.getTotal());
    }

    private void assertCurrentAssetsMapped(CurrentAssets currentAssets) {

        assertNotNull(currentAssets);
        assertEquals(CURRENT_ASSETS_CASH_AT_BANK_AND_IN_HAND, currentAssets.getCashAtBankAndInHand());
        assertEquals(CURRENT_ASSETS_DEBTORS, currentAssets.getDebtors());
        assertEquals(CURRENT_ASSETS_STOCKS, currentAssets.getStocks());
        assertEquals(CURRENT_ASSETS_TOTAL, currentAssets.getTotal());
    }

    private void assertCapitalAndReservesMapped(CapitalAndReservesEntity capitalAndReservesEntity) {

        assertNotNull(capitalAndReservesEntity);
        assertEquals(CAPITAL_AND_RESERVES_CALLED_UP_SHARE_CAPITAL, capitalAndReservesEntity.getCalledUpShareCapital());
        assertEquals(CAPITAL_AND_RESERVES_OTHER_RESERVES, capitalAndReservesEntity.getOtherReserves());
        assertEquals(CAPITAL_AND_RESERVES_PROFIT_AND_LOSS, capitalAndReservesEntity.getProfitAndLoss());
        assertEquals(CAPITAL_AND_RESERVES_SHARE_PREMIUM_ACCOUNT, capitalAndReservesEntity.getSharePremiumAccount());
        assertEquals(CAPITAL_AND_RESERVES_TOTAL_SHAREHOLDERS_FUNDS, capitalAndReservesEntity.getTotalShareholdersFunds());
    }

    private void assertCapitalAndReservesMapped(CapitalAndReserves capitalAndReserves) {

        assertNotNull(capitalAndReserves);
        assertEquals(CAPITAL_AND_RESERVES_CALLED_UP_SHARE_CAPITAL, capitalAndReserves.getCalledUpShareCapital());
        assertEquals(CAPITAL_AND_RESERVES_OTHER_RESERVES, capitalAndReserves.getOtherReserves());
        assertEquals(CAPITAL_AND_RESERVES_PROFIT_AND_LOSS, capitalAndReserves.getProfitAndLoss());
        assertEquals(CAPITAL_AND_RESERVES_SHARE_PREMIUM_ACCOUNT, capitalAndReserves.getSharePremiumAccount());
        assertEquals(CAPITAL_AND_RESERVES_TOTAL_SHAREHOLDERS_FUNDS, capitalAndReserves.getTotalShareholdersFunds());
    }

    private void assertMembersFundsMapped(MembersFundsEntity membersFundsEntity) {

        assertNotNull(membersFundsEntity);
        assertEquals(MEMBERS_FUNDS_PROFIT_AND_LOSS_ACCOUNT, membersFundsEntity.getProfitAndLossAccount());
        assertEquals(MEMBERS_FUNDS_TOTAL, membersFundsEntity.getTotalMembersFunds());
    }

    private void assertMembersFundsMapped(MembersFunds membersFunds) {

        assertNotNull(membersFunds);
        assertEquals(MEMBERS_FUNDS_PROFIT_AND_LOSS_ACCOUNT, membersFunds.getProfitAndLossAccount());
        assertEquals(MEMBERS_FUNDS_TOTAL, membersFunds.getTotalMembersFunds());
    }
}

