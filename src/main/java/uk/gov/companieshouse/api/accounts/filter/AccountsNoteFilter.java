package uk.gov.companieshouse.api.accounts.filter;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import uk.gov.companieshouse.api.accounts.utility.PackageResolver;

@Component
public class AccountsNoteFilter implements Filter {

    private static final Pattern ACCOUNTS_RESOURCE_REGEX = Pattern.compile("^/transactions/[^/]*/company-accounts/[^/]+/([^/]+)/notes/([^/]+)$");

    private static final List<String> SUBMISSION_METHODS = Arrays.asList("POST", "PUT");

    private static final String ACCOUNTS_RESOURCE_PACKAGE = "accounts_resource_package";

    @Autowired
    private PackageResolver packageResolver;

    @Override
    public void init(FilterConfig filterConfig) {
        // Default impl
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Add request wrapper so the body can be read multiple times
        AccountsResourceRequestWrapper multiReadRequest =
                new AccountsResourceRequestWrapper((HttpServletRequest) request);

        chain.doFilter(multiReadRequest, response);
    }

    @Override
    public void destroy() {
        // Default impl
    }

    private class AccountsResourceRequestWrapper extends HttpServletRequestWrapper {

        private String body;

        private AccountsResourceRequestWrapper(HttpServletRequest request) throws IOException {
            super(request);

            // If submission request
            if (SUBMISSION_METHODS.contains(request.getMethod())) {

                // Read request body to json object
                JSONObject j = new JSONObject(IOUtils.toString(request.getReader()));

                // For requests of a given format, append an accounts resource package to the body
                Matcher matcher = ACCOUNTS_RESOURCE_REGEX.matcher(request.getRequestURI());
                if (matcher.find()) {
                    j.put(ACCOUNTS_RESOURCE_PACKAGE, packageResolver.getNotePackage(matcher.group(1), matcher.group(2)));
                }

                // Cache the body for later reading
                body = j.toString();
            }
        }

        @Override
        public ServletInputStream getInputStream() {

            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body.getBytes());

            return new ServletInputStream() {

                @Override
                public boolean isFinished() {
                    return byteArrayInputStream.available() == 0;
                }

                @Override
                public boolean isReady() {
                    return true;
                }

                @Override
                public void setReadListener(ReadListener readListener) {
                    throw new UnsupportedOperationException();
                }

                public int read() {
                    return byteArrayInputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }
    }
}
