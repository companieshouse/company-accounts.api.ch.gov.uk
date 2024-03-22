package uk.gov.companieshouse.api.accounts.validation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

import java.lang.reflect.Method;

import jakarta.xml.bind.annotation.XmlElement;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DataTest {

    @InjectMocks
    private Data data;

    @BeforeEach
    void setup () {
        data.setBalanceSheetDate("BalanceSheetData");
        data.setAccountsType("CompanyAccounts");
        data.setCompaniesHouseRegisteredNumber("CompaniesHouseRegisteredNumber");
    }

    @Test
    @DisplayName("Test Get BalanceSheet data")
    void testGetBalanceSheetDate() {
        assertEquals("BalanceSheetData",data.getBalanceSheetDate());
    }

    @Test
    @DisplayName("Test Get AccountsType data")
    void testGetAccountsType() {
        assertEquals("CompanyAccounts",data.getAccountsType());
    }

    @Test
    @DisplayName("Test Get CompaniesHouseRegisteredNumber")
    void testGetCompaniesHouseRegisteredNumber() {
        assertEquals("CompaniesHouseRegisteredNumber",data.getCompaniesHouseRegisteredNumber());
    }

    @Test
    @DisplayName("Test If Get Methods have the appropriate annotation")
    void testHasMethodsWithAnnotation() {
        Class<Data> dataClass = Data.class;
        Method[] methods = dataClass.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().startsWith("get")) {
            assertNotNull("Method :"+m.getName() + " does not have annotation XmlElement",m.getAnnotation(XmlElement.class));
            }
        }
    }

}
