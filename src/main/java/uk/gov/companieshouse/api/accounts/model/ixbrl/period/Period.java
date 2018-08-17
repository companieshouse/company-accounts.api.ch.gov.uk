package uk.gov.companieshouse.api.accounts.model.ixbrl.period;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;

import uk.gov.companieshouse.accountsDates.AccountsDates;

public class Period {

    @JsonProperty("current_period_start_on")
    private String currentPeriodStartOn;
    @JsonProperty("current_period_end_on")
    private String currentPeriodEndsOn;
    @JsonProperty("previous_period_start_on")
    private String previousPeriodStartOn;
    @JsonProperty("previous_period_end_on")
    private String previousPeriodEndsOn;

    @JsonProperty("current_period_start_on_formatted")
    private String currentPeriodStartOnFormatted;
    @JsonProperty("current_period_end_on_formatted")
    private String currentPeriodEndsOnFormatted;

    @JsonProperty("current_period_bs_date")
    private String currentPeriodBSDate;
    @JsonProperty("previous_period_bs_date")
    private String previousPeriodBSDate;

    private AccountsDates accountsDates;

    public Period(AccountsDates accountsDates) {

        this.accountsDates = accountsDates;

    }

    public String getCurrentPeriodBSDate() {
        return currentPeriodBSDate;
    }

    public void setCurrentPeriodBSDate(String currentPeriodStartOn, String currentPeriodEndsOn, boolean isSameYear) {
        currentPeriodBSDate = accountsDates.generateBalanceSheetHeading(currentPeriodStartOn, currentPeriodEndsOn,
                isSameYear(currentPeriodStartOn, currentPeriodEndsOn));
    }

    public String getPreviousPeriodBSDate() {
        return previousPeriodBSDate;
    }

    public void setPreviousPeriodBSDate(String previousPeriodStartOn, String previousPeriodEndsOn, boolean isSameYear) {
        previousPeriodBSDate = accountsDates.generateBalanceSheetHeading(previousPeriodStartOn, previousPeriodEndsOn,
                isSameYear(currentPeriodStartOn, currentPeriodEndsOn));
    }

    public String getCurrentPeriodStartOnFormatted() {
        return currentPeriodStartOnFormatted;
    }

    public String getCurrentPeriodEndsOnFormatted() {
        return currentPeriodEndsOnFormatted;
    }

    public void setCurrentPeriodEndsOnFormatted(String currentPeriodEndsOnFormatted) {
        LocalDate date = accountsDates.convertStringToDate(currentPeriodEndsOnFormatted);
        this.currentPeriodEndsOnFormatted = accountsDates.convertLocalDateToDisplayDate(date);
    }

    public void setCurrentPeriodStartOnFormatted(String currentPeriodStartOnFormatted) {
        LocalDate date = accountsDates.convertStringToDate(currentPeriodStartOnFormatted);
        this.currentPeriodStartOnFormatted = accountsDates.convertLocalDateToDisplayDate(date);
    }

    public String getCurrentPeriodStartOn() {
        return currentPeriodStartOn;
    }

    public void setCurrentPeriodStartOn(String currentPeriodStartOn) {
        LocalDate localDate = accountsDates.getLocalDatefromDateTimeString(currentPeriodStartOn);
        this.currentPeriodStartOn = accountsDates.convertDateToString(localDate);
    }

    public String getCurrentPeriodEndsOn() {
        return currentPeriodEndsOn;
    }

    public void setCurrentPeriodEndsOn(String currentPeriodEndsOn) {
        LocalDate localDate = accountsDates.getLocalDatefromDateTimeString(currentPeriodEndsOn);
        this.currentPeriodEndsOn = accountsDates.convertDateToString(localDate);
    }

    public String getPreviousPeriodStartOn() {
        return previousPeriodStartOn;
    }

    public void setPreviousPeriodStartOn(String previousPeriodStartOn) {
        this.previousPeriodStartOn = accountsDates
                .convertDateToString(accountsDates.getLocalDatefromDateTimeString(previousPeriodStartOn));
        ;
    }

    public String getPreviousPeriodEndsOn() {
        return previousPeriodEndsOn;
    }

    public void setPreviousPeriodEndsOn(String previousPeriodEndsOn) {
        LocalDate localDate = accountsDates.getLocalDatefromDateTimeString(previousPeriodEndsOn);
        this.previousPeriodEndsOn = accountsDates.convertDateToString(localDate);
    }

    public boolean isSameYear(String date1, String date2) {
        return accountsDates.isSameYear(accountsDates.convertStringToDate(date1),
                accountsDates.convertStringToDate(date2));
    }
}
