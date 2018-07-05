package uk.gov.companieshouse.api.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@SpringBootApplication
public class AccountsApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {

        // Returns the configured ${PORT} value in the `start.sh` script - passed in under `server.port`.
        // If no port is configured, return null
        Integer port = Integer.getInteger("server.port");

        if (port == null) {
            // When logging has been added, this print line will be replace
            System.out.println("Failed to start service, no port has been configured");
            System.exit(0);
        }

        SpringApplication.run(AccountsApplication.class, args);
    }
}