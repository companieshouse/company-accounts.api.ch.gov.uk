package uk.gov.companieshouse.api.accounts.validation;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "data")
public class Data {

    private String balanceSheetDate;

    private String accountsType;

    private String companiesHouseRegisteredNumber;

    @XmlElement(name = "BalanceSheetDate")
    public String getBalanceSheetDate() {
        return balanceSheetDate;
    }

    public void setBalanceSheetDate(String balanceSheetDate) {
        this.balanceSheetDate = balanceSheetDate;
    }

    @XmlElement(name = "AccountsType")
    public String getAccountsType() {
        return accountsType;
    }

    public void setAccountsType(String accountsType) {
        this.accountsType = accountsType;
    }

    @XmlElement(name = "CompaniesHouseRegisteredNumber")
    public String getCompaniesHouseRegisteredNumber() {
        return companiesHouseRegisteredNumber;
    }

    public void setCompaniesHouseRegisteredNumber(String companiesHouseRegisteredNumber) {
        this.companiesHouseRegisteredNumber = companiesHouseRegisteredNumber;
    }

    @Override
    public String toString() {
        return "Data [balanceSheetDate=" + balanceSheetDate + ", accountsType=" + accountsType
            + ", companiesHouseRegisteredNumber=" + companiesHouseRegisteredNumber + "]";
    }
}