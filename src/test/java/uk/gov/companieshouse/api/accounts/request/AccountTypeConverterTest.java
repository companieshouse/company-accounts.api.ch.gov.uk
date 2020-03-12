package uk.gov.companieshouse.api.accounts.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.companieshouse.api.accounts.enumeration.AccountType;
import uk.gov.companieshouse.api.accounts.exception.InvalidPathParameterException;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountTypeConverterTest {

    private AccountType accountTypeWithSmallFull = AccountType.SMALL_FULL;
    private String OTHER_TEXT = "text";

    private Map<String, AccountType> ACCOUNT_TYPE_MAP = new HashMap<>();

    private AccountTypeConverter converter;

    @BeforeEach
    private void setup() {

        ACCOUNT_TYPE_MAP.put(accountTypeWithSmallFull.getType(), accountTypeWithSmallFull);
        converter = new AccountTypeConverter();
    }

    @Test
    @DisplayName("Get account type for the converter with the value from the map")
    void getAccountTypeForTheConverterSuccess() {

        assertEquals(accountTypeWithSmallFull,
                ACCOUNT_TYPE_MAP.get(accountTypeWithSmallFull.getType()));
        converter.setAsText(accountTypeWithSmallFull.getType());
        assertEquals(accountTypeWithSmallFull, converter.getValue());
    }

    @Test
    @DisplayName("Get account type for the converter throws invalid parameter exception")
    void getAccountTypeForTheConverterThrowsInvalidParameterException() {

        converter = new AccountTypeConverter();

        AccountType type = ACCOUNT_TYPE_MAP.get(OTHER_TEXT);
        converter.setValue(type);

        assertThrows(InvalidPathParameterException.class,
                () -> converter.setAsText(OTHER_TEXT));
        assertNull(converter.getValue());
    }
}