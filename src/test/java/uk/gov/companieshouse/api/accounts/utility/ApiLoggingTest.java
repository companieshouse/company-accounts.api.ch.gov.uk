package uk.gov.companieshouse.api.accounts.utility;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.gov.companieshouse.api.accounts.utility.TestEnvironmentSetupHelper.METHOD_TRACE_ENABLED_KEY;
import static uk.gov.companieshouse.api.accounts.utility.TestEnvironmentSetupHelper.PERFORMANCE_STATS_ENABLED_KEY;

import org.junit.jupiter.api.Test;
import uk.gov.companieshouse.api.accounts.utility.logging.ApiLogging;

public class ApiLoggingTest {

  /**
   * Test that the apiLogging.methodTraceEnabled defaults to False
   */
  @Test
  public void methodTraceNotEnabled() {
    ApiLogging apiLogging = new ApiLogging();
    assertTrue(!apiLogging.isMethodTraceEnabled());
  }


  /**
   * Test that the apiLogging.methodTraceEnabled enabled when METHOD_TRACE_ENABLED is true
   */
  @Test
  public void methodTraceEnabled() {
    TestEnvironmentSetupHelper.setEnvironmentVariable(METHOD_TRACE_ENABLED_KEY, "true");
    ApiLogging apiLogging = new ApiLogging();
    assertTrue(apiLogging.isMethodTraceEnabled());
  }

  /**
   * Test that the apiLogging.performanceStatEnabled defaults to False
   */
  @Test
  public void perfStatsDisabled() {
    ApiLogging apiLogging = new ApiLogging();
    assertTrue(!apiLogging.isPerformanceStatsEnabled());
  }

  /**
   * Test that the apiLogging.performanceStatEnabled enabled when PERFORMANCE_STATS_ENABLED is true
   */
  @Test
  public void perfStatsEnabled() {
    TestEnvironmentSetupHelper.setEnvironmentVariable(PERFORMANCE_STATS_ENABLED_KEY, "true");
    ApiLogging apiLogging = new ApiLogging();
    assertTrue(apiLogging.isPerformanceStatsEnabled());
  }

}
