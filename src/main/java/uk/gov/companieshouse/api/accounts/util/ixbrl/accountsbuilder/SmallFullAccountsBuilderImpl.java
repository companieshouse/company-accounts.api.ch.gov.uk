package uk.gov.companieshouse.api.accounts.util.ixbrl.accountsbuilder;

import java.io.IOException;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.api.accounts.model.ixbrl.Account;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.CalledUpSharedCapitalNotPaid;
import uk.gov.companieshouse.api.accounts.model.ixbrl.company.Company;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.Notes;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.PostBalanceSheetEvents;
import uk.gov.companieshouse.api.accounts.model.ixbrl.period.Period;

@Component
public class SmallFullAccountsBuilderImpl implements AccountsBuilder {

    @Override
    public Object buildAccount() throws IOException {
        return getSmallFullAccount();
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
