package uk.gov.companieshouse.api.accounts.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class AccountsResourceFilter implements Filter {

    private static final Pattern ACCOUNTS_RESOURCE_REGEX = Pattern.compile("^/transactions/[^/]*/company-accounts/[^/]+/([^/]+)/([^/]+)(/[^/]+)?$");

    private static final List<String> SUBMISSION_METHODS = Arrays.asList("POST", "PUT");

    private static final String ACCOUNTS_RESOURCE_PACKAGE = "accountsResourcePackage";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
            FilterChain chain) throws IOException, ServletException {

        // Add request wrapper so the body can be read multiple times
        AccountsResourceRequestWrapper multiReadRequest = new AccountsResourceRequestWrapper((HttpServletRequest) request);

        chain.doFilter(multiReadRequest, response);
    }

    @Override
    public void destroy() {

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
                    j.put(ACCOUNTS_RESOURCE_PACKAGE,
                            (matcher.group(3) != null ?
                                    getPackageNameIncludingGroupThree(matcher) :
                                    getPackageNameExcludingGroupThree(matcher)));
                }

                // Cache the body for later reading
                body = j.toString();
            }
        }

        private String getPackageNameExcludingGroupThree(Matcher matcher) {

            return "." + getPackage(matcher.group(1)) +
                    "." + getPackage(matcher.group(2)) +
                    "." + getClassName(matcher.group(2));
        }

        private String getPackageNameIncludingGroupThree(Matcher matcher) {

            return "." + getPackage(matcher.group(1)) +
                    "." + getPackage(matcher.group(2)) +
                    "." + getPackage(matcher.group(3).replace("/", "")) +
                    "." + getClassName(matcher.group(3).replace("/", ""));
        }

        private String getPackage(String input) {

            return input.replace("-", "").toLowerCase();
        }

        private String getClassName(String input) {

            return WordUtils.capitalizeFully(input.replace("-", " ")).replace(" ", "");
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {

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

                public int read() throws IOException {
                    return byteArrayInputStream.read();
                }
            };
        }

        @Override
        public BufferedReader getReader() throws IOException {
            return new BufferedReader(new InputStreamReader(this.getInputStream()));
        }
    }
}