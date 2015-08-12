package littleextracare.bifortis.com.data;

/**
 * Created by Company on 10/5/2015.
 * JSONParser class is responsible for fetching json
 * data from server by using GET and POST method
 * depends on request from UI.
 */
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;

import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.activities.LetsGoActivity;
import littleextracare.bifortis.com.model.WebApiManager;

@SuppressWarnings({"deprecation", "SameParameterValue", "TryWithIdenticalCatches", "StringConcatenationInsideStringBufferAppend"})
public class JSONParser extends AsyncTask<String, Void, String> {
    private static final String TAG = JSONParser.class.getName();

    private static InputStream inputStream = null;
    private static JSONObject jsonObject = null;
    private static String json = "";

    private String url;
    private final String method;
    private List<NameValuePair> params;
    private final LetsGoActivity letsGoActivity;

    public JSONParser(String url, String method,
                      List<NameValuePair> params, LetsGoActivity letsGoActivity) {
        this.url = url;
        this.method = method;
        this.params = params;
        this.letsGoActivity = letsGoActivity;
    }

    @Override
    protected String doInBackground(String... param) {
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
        } catch (UnsupportedEncodingException e) {
            Log.e(TAG, e.toString());
        } catch (ClientProtocolException e) {
            Log.e(TAG, e.toString());
        } catch (IOException e) {
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
        } catch (Exception e) {
            Log.e(TAG, "Error converting result " + e.toString());
        }

        // try parse the string to a JSON object
        try {
            jsonObject = new JSONObject(json);
        } catch (JSONException e) {
            Log.e(TAG, "Error parsing data " + e.toString());
        }

        if(jsonObject.has(SharedPrefConstants.tokenKey))
        try {
            String token = jsonObject.getString(SharedPrefConstants.tokenKey);
            SharedPref.setData(letsGoActivity, SharedPrefConstants.tokenValueKey, token);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
        Log.e(TAG, jsonObject.toString());
        return jsonObject.toString();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

    }


}