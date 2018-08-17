package uk.gov.companieshouse.api.accounts.util.ixbrl.accountsbuilder;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import uk.gov.companieshouse.accountsDates.AccountsDates;
import uk.gov.companieshouse.api.accounts.model.ixbrl.Account;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.BalanceSheet;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.BalanceSheetStatements;
import uk.gov.companieshouse.api.accounts.model.ixbrl.balancesheet.CalledUpSharedCapitalNotPaid;
import uk.gov.companieshouse.api.accounts.model.ixbrl.company.Company;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.Notes;
import uk.gov.companieshouse.api.accounts.model.ixbrl.notes.PostBalanceSheetEvents;
import uk.gov.companieshouse.api.accounts.model.ixbrl.period.Period;

@Component
public class SmallFullAccountsBuilderImpl implements AccountsBuilder {
    
    @Autowired
    private AccountsDates accountsDates;

    @Override
    public Object buildAccount() throws IOException {
        return getSmallFullAccount();
    }

    /**
     * Builds the small full accounts model. Functionality needs to be change.
     */
    private Account getSmallFullAccount() {
        //TODO: REMOVE HARDCODED VALUES. Use functionality to retrieve the accounts information when built in the API.
        Account account = new Account(accountsDates);
        account.setPeriod(getAccountPeriod());
        account.setBalanceSheet(getBalanceSheet());
        account.setNotes(getNotes());
        account.setCompany(getCompany());
        account.setApprovalDate("2016-01-19T00:00:00.000Z");
        account.setApprovalName("Leah");
        
        return account;
    }

    private Company getCompany() {
        Company company = new Company();
        company.setCompanyName("MYRETON RENEWABLE ENERGY LIMITED");
        company.setCompanyNumber("SC344891");
        company.setJurisdiction(company.getCompanyNumber());

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
        Period period = new Period(accountsDates);
        period.setCurrentPeriodStartOn("2016-01-19T00:00:00.000Z");
        period.setCurrentPeriodEndsOn("2016-11-01T00:00:00.000Z");
        period.setPreviousPeriodStartOn("2017-01-31T00:00:00.000Z");
        period.setPreviousPeriodEndsOn("2017-12-31T00:00:00.000Z");
        period.setCurrentPeriodStartOnFormatted(period.getCurrentPeriodStartOn());
        period.setCurrentPeriodEndsOnFormatted(period.getCurrentPeriodEndsOn());
        period.setCurrentPeriodBSDate(period.getCurrentPeriodStartOn(), period.getCurrentPeriodEndsOn(), period.isSameYear(period.getCurrentPeriodStartOn(), period.getCurrentPeriodEndsOn()));
        period.setPreviousPeriodBSDate(period.getPreviousPeriodStartOn(), period.getPreviousPeriodEndsOn(), period.isSameYear(period.getCurrentPeriodStartOn(), period.getCurrentPeriodEndsOn()));

        return period;
    }

    /**
     * Build BalanceSheet model by using information from database
     */
    private BalanceSheet getBalanceSheet() {
        //TODO: remove hardcoded values for actual db call
        BalanceSheet balanceSheet = new BalanceSheet();
        
        BalanceSheetStatements statements = new BalanceSheetStatements();
        statements.setSection477("For the year ending 31 December 2016 the company was entitled to exemption under section 477 of the Companies Act 2006 relating to small companies.");
        statements.setAuditNotRequiredByMembers("The members have not required the company to obtain an audit in accordance with section 476 of the Companies Act 2006.");
        statements.setDirectorsResponsibility("The directors acknowledge their responsibilities for complying with the requirements of the Act with respect to accounting records and the preparation of accounts.");
        statements.setSmallCompaniesRegime("These accounts have been prepared and delivered in accordance with the provisions applicable to companies subject to the small companies regime.");
        balanceSheet.setBalanceSheetStatements(statements);
        
        CalledUpSharedCapitalNotPaid calledUpSharedCapitalNotPaid = new CalledUpSharedCapitalNotPaid();
        calledUpSharedCapitalNotPaid.setCurrentAmount(9);
        calledUpSharedCapitalNotPaid.setPreviousAmount(99);
        balanceSheet.setCalledUpSharedCapitalNotPaid(calledUpSharedCapitalNotPaid);
        
        balanceSheet.setCurrentPeriodDateFormatted("2017-01-01");
        balanceSheet.setPreviousPeriodDateFormatted("2017-01-01");

        return balanceSheet;
    }
}
