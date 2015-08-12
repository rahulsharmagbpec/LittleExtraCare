package littleextracare.bifortis.com.data;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import littleextracare.bifortis.com.model.WebApiManager;

public class GetJsonFromServer
{
    private final String TAG = GetJsonFromServer.class.getName();
    private InputStream inputStream;
    private String json;

    public GetJsonFromServer()
    {
    }

    @SuppressWarnings("deprecation")
    public String getJson(String url, String method, List<NameValuePair> params)
    {
        try {
            // if condition is for POST method
            if(method.equalsIgnoreCase("POST")){
                // request method inputStream POST
                // defaultHttpClient

                HttpPost httpPost = new HttpPost(url);
                httpPost.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse httpResponse = WebApiManager.getInstance().execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            }
            //else condition is for GET method
            else if(method.equalsIgnoreCase("GET"))
            {
                // request method inputStream GET
                String paramString = URLEncodedUtils.format(params, "utf-8");
                url += "?" + paramString;
                HttpGet httpGet = new HttpGet(url);

                HttpResponse httpResponse = WebApiManager.getInstance().execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                inputStream = httpEntity.getContent();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    inputStream, "iso-8859-1"), 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            inputStream.close();
            json = stringBuilder.toString();
            Log.e(TAG, json);
        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }
        return json;
    }
}
