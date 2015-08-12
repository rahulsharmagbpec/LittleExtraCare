package littleextracare.bifortis.com.model;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

public class WebApiManager extends DefaultHttpClient {
    private static WebApiManager wsm = null;

    public static WebApiManager getInstance() {
        if (wsm == null) {
            wsm = new WebApiManager();
        }
        return wsm;
    }

    private final HttpClient httpClient;

    private WebApiManager() {
        httpClient = new DefaultHttpClient();
    }
}
