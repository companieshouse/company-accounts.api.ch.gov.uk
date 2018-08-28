package uk.gov.companieshouse.api.accounts.util.ixbrl.accountsbuilder.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.security.NoSuchAlgorithmException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.CompanyAccountsApplication;
import uk.gov.companieshouse.api.accounts.model.entity.SmallFullEntity;
import uk.gov.companieshouse.api.accounts.model.ixbrl.Account;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.CalledUpSharedCapitalNotPaid;
import uk.gov.companieshouse.api.accounts.model.ixbrl.company.Company;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.Notes;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.PostBalanceSheetEvents;
import uk.gov.companieshouse.api.accounts.model.ixbrl.period.Period;
import uk.gov.companieshouse.api.accounts.model.rest.SmallFull;
import uk.gov.companieshouse.api.accounts.service.SmallFullService;
import uk.gov.companieshouse.api.accounts.transformer.SmallFullTransformer;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@Component
public class SmallFullAccountsHelperImpl implements AccountsHelper<SmallFull, Account> {

    private static final Logger LOGGER = LoggerFactory
        .getLogger(CompanyAccountsApplication.APPLICATION_NAME_SPACE);

    @Autowired
    private SmallFullService smallFullService;

    @Autowired
    private SmallFullTransformer smallFullTransformer;

    @Override
    public SmallFull getAccountTypeInformation(String accountId) throws NoSuchAlgorithmException {
        String smallFullId = smallFullService.generateID(accountId);

        SmallFullEntity smallFullEntity = null;
        try {
            smallFullEntity = smallFullService.findById(smallFullId);
        } catch (DataAccessException dae) {
            LOGGER.error(dae);
        }

        return (smallFullEntity != null ? smallFullTransformer.transform(smallFullEntity) : null);
    }

    @Override
    public Account buildAccount(SmallFull account) {
        return getSmallFullAccount();
    }


    @Override
    public String getAccountsJsonFormat(String accountId)
        throws NoSuchAlgorithmException, JsonProcessingException {

        SmallFull smallFull = getAccountTypeInformation(accountId);
        /*if (smallFull == null) {
            return null;
        }
        */
        Account accountModel = buildAccount(smallFull);
        if (accountModel == null) {
            return null;
        }

        return convertToJson(accountModel);
    }


    /**
     * Generates the json for account type passed in.
     *
     * @param account - Account type: small full, abridged, etc.
     * @return The account marshaled to JSON and converted to String.
     * @throws JsonProcessingException
     */
    public String convertToJson(Account account) throws JsonProcessingException {

        JSONObject accountsRequestBody = new JSONObject();
        accountsRequestBody.put("small_full_account", convertObjectToJson(account));
        return accountsRequestBody.toString();
    }

    /**
     * Marshall object to json.
     *
     * @param obj - object is being processed. e.g. Account object
     * @return {@link JSONObject}
     * @throws JsonProcessingException
     */
    private <T> JSONObject convertObjectToJson(T obj) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return new JSONObject(objectMapper.writeValueAsString(obj));
    }

    /**
     * Get Account information and build Account by calling the API.
     */
    //TODO: REMOVE HARDCODED VALUES. Use functionality to retrieve the accounts information when built in the API. (STORY SFA-574)
    private Account getSmallFullAccount() {
        Account account = new Account();
        account.setPeriod(getAccountPeriod());
        account.setBalanceSheet(getBalanceSheet());
        account.setNotes(getNotes());
        account.setCompany(getCompany());

        return account;
    }

    private Company getCompany() {
        Company company = new Company();
        company.setCompanyName("TEST COMPANY LIMITED");
        company.setCompanyNumber("12345678");

        return company;
    }

    private Notes getNotes() {
        Notes notes = new Notes();
        PostBalanceSheetEvents postBalanceSheetEvents = new PostBalanceSheetEvents();
        postBalanceSheetEvents.setCurrentPeriodDateFormatted("16 December 2017");
        postBalanceSheetEvents.setPostBalanceSheetEventsInfo("test post balance note");
        notes.setPostBalanceSheetEvents(postBalanceSheetEvents);

        return notes;
    }

    private Period getAccountPeriod() {
        Period period = new Period();
        period.setCurrentPeriodStartOn("2017-05-01");
        period.setCurrentPeriodEndsOn("2018-05-01");
        period.setPreviousPeriodStartOn("2016-12-01");
        period.setPreviousPeriodEndsOn("2016-01-01");

        return period;
    }

    /**
     * Build BalanceSheet model by using information from database
     */
    private BalanceSheet getBalanceSheet() {
        //TODO: remove hardcoded values for actual db call
        BalanceSheet balanceSheet = new BalanceSheet();

        CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid = new CalledUpSharedCapitalNotPaid();
        calledUpSharedCapitalNotPaid.setCurrentAmount(9);
        calledUpSharedCapitalNotPaid.setPreviousAmount(99);

        balanceSheet.setCalledUpSharedCapitalNotPaid(calledUpSharedCapitalNotPaid);
        balanceSheet.setCurrentPeriodDateFormatted("16 December 2017");
        balanceSheet.setPreviousPeriodDateFormatted("16 December 2017");

        return balanceSheet;
    }
}
