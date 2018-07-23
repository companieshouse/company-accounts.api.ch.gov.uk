package uk.gov.companieshouse.api.accounts.utility.logging;

import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;
import uk.gov.companieshouse.environment.impl.EnvironmentReaderImpl;

@Component
public class ApiLogging {

  private boolean methodTraceEnabled;
  private boolean performanceStatsEnabled;

  public boolean isMethodTraceEnabled() {
    return methodTraceEnabled;
  }

  public boolean isPerformanceStatsEnabled() {
    return performanceStatsEnabled;
  }

  /**
   * Constructs API Logging config using environment variables.
   */
  public ApiLogging() {
    EnvironmentReader reader = new EnvironmentReaderImpl();
    this.methodTraceEnabled = reader.getOptionalBoolean("METHOD_TRACE_ENABLED");
    this.performanceStatsEnabled = reader.getOptionalBoolean("PERFORMANCE_STATS_ENABLED");
  }

}
