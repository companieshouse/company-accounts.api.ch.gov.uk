package uk.gov.companieshouse.api.accounts.utility.filetransfer;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.companieshouse.environment.EnvironmentReader;

@Component
public class HttpURLConnectionHandlerImpl implements HttpURLConnectionHandler {

    private static final String PROXY_ADDRESS_ENV_VAR = "HTTP_URL_CONNECTION_PROXY_ADDR";
    private static final String PROXY_PORT_ENV_VAR = "HTTP_URL_CONNECTION_PROXY_PORT";
    private final String connectionHandlerProxyAddress;
    private final Integer connectionHandlerProxyPort;
    private final EnvironmentReader environmentReader;

    @Autowired
    public HttpURLConnectionHandlerImpl(EnvironmentReader environmentReader) {

        this.environmentReader = environmentReader;

        connectionHandlerProxyAddress = environmentReader.getOptionalString(PROXY_ADDRESS_ENV_VAR);
        connectionHandlerProxyPort = environmentReader.getOptionalInteger(PROXY_PORT_ENV_VAR);
    }

    /**
     * Create HttpURLConnection using the url file location provided and it will add proxy settings
     * if the environment variables for the proxy have been set.
     *
     * @param urlFileLocation - location of the file to be downloaded
     * @return - {@link HttpURLConnection} with or without proxy settings.
     * @throws IOException
     */
    @Override
    public HttpURLConnection openConnection(String urlFileLocation) throws IOException {

        URL url = new URL(urlFileLocation);

        if (StringUtils.isNotBlank(connectionHandlerProxyAddress) &&
            connectionHandlerProxyPort != null) {

            InetSocketAddress inetSocketAddress =
                new InetSocketAddress(connectionHandlerProxyAddress, connectionHandlerProxyPort);

            Proxy proxy = new Proxy(Proxy.Type.HTTP, inetSocketAddress);

            return (HttpURLConnection) url.openConnection(proxy);

        } else {
            return (HttpURLConnection) url.openConnection();
        }
    }
}
