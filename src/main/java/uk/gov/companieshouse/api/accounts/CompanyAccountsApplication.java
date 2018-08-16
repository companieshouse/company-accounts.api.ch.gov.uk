package uk.gov.companieshouse.api.accounts;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.gov.companieshouse.api.accounts.interceptor.CompanyAccountInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.LoggingInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.SmallFullInterceptor;
import uk.gov.companieshouse.api.accounts.interceptor.TransactionInterceptor;
import uk.gov.companieshouse.logging.Logger;
import uk.gov.companieshouse.logging.LoggerFactory;

@SpringBootApplication
public class CompanyAccountsApplication implements WebMvcConfigurer {

    public static final String APPLICATION_NAME_SPACE = "company-accounts.api.ch.gov.uk";
    private static final Logger LOGGER = LoggerFactory.getLogger(APPLICATION_NAME_SPACE);

    @Autowired
    private TransactionInterceptor transactionInterceptor;

    @Autowired
    private CompanyAccountInterceptor companyAccountInterceptor;

    @Autowired
    private SmallFullInterceptor smallFullInterceptor;

    @Autowired
    private LoggingInterceptor loggingInterceptor;

    public static void main(String[] args) {

        // Returns the configured ${PORT} value passed in under `server.port`.
        // If no port is configured, return null
        Integer port = Integer.getInteger("server.port");

        if (port == null) {
            LOGGER.error("Failed to start service, no port has been configured");
            System.exit(0);
        }

        SpringApplication.run(CompanyAccountsApplication.class, args);
    }

    @Bean
    @Primary
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return objectMapper;
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/**").excludePathPatterns("/healthcheck");
        registry.addInterceptor(transactionInterceptor)
                .addPathPatterns("/transactions/{transactionId}/**");
        registry.addInterceptor(companyAccountInterceptor)
                .addPathPatterns(
                        "/transactions/{transactionId}/company-accounts/{companyAccountId}**");
        registry.addInterceptor(smallFullInterceptor)
                .addPathPatterns(
                        "/transactions/{transactionId}/company-accounts/{companyAccountId}/small-full**");
    }
}