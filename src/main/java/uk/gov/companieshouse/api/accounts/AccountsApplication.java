package uk.gov.companieshouse.api.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@SpringBootApplication
public class AccountsApplication extends WebMvcConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger("company-accounts.api.ch.gov.uk");

    public static void main(String[] args) {

        // Returns the configured ${PORT} value passed in under `server.port`.
        // If no port is configured, return null
        Integer port = Integer.getInteger("server.port");

        if (port == null) {
            LOGGER.error("Failed to start service, no port has been configured");
            System.exit(0);
        }

        SpringApplication.run(AccountsApplication.class, args);
    }
}