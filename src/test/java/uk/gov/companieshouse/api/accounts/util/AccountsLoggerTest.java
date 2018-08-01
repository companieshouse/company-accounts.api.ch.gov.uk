package uk.gov.companieshouse.api.accounts.util;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.internal.util.reflection.Fields;
import org.mockito.internal.util.reflection.InstanceField;
import uk.gov.companieshouse.api.accounts.util.logging.AccountsLoggerImpl;
import uk.gov.companieshouse.logging.StructuredLogger;

public class AccountsLoggerTest {

  private AccountsLoggerImpl accountsLogger;

  @BeforeEach
  void setUp() {
    List<String> resources = new ArrayList<>();
    resources.add("transactionId");
    resources.add("accountId");
    accountsLogger = new AccountsLoggerImpl("requestId",
        "userId", resources);
  }

  @Test
  public void loggerUsingStructuredLogging() {
    List<InstanceField> fields = Fields.allDeclaredFieldsOf(accountsLogger).instanceFields()
        .stream()
        .filter(field -> field.name().equals("LOGGER")).collect(toList());
    assertTrue(!fields.isEmpty());
    assertTrue(fields.size() > 0);
    InstanceField field = fields.get(0);
    assertTrue(field.read() instanceof StructuredLogger);
    StructuredLogger logger = (StructuredLogger) field.read();
    assertNotNull(logger);
  }
}
