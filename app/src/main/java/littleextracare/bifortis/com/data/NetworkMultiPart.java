package littleextracare.bifortis.com.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

import littleextracare.bifortis.com.Constants.ApiConstants;
import littleextracare.bifortis.com.Constants.Constants;
import littleextracare.bifortis.com.model.WebApiManager;

public class NetworkMultiPart
{
    private static String TAG = NetworkMultiPart.class.getName();
    public void getdata(Context context, String url)
    {
        try {
            HttpPost poster = new HttpPost(url);
            poster.addHeader("X-Requested-With", "XMLHttpRequest");

            MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

            SharedPreferences sharedPref = context.getSharedPreferences("tokenKey", Context.MODE_PRIVATE);
            String token = sharedPref.getString(Constants.TOKEN, null);

            if(token != null)
            entity.addPart(ApiConstants.token_key, new StringBody(token));

            poster.setEntity(entity);

            WebApiManager.getInstance().execute(poster, new ResponseHandler<Object>() {
                public Object handleResponse(HttpResponse response) throws IOException {
                    HttpEntity respEntity = response.getEntity();
                    String responseString = EntityUtils.toString(respEntity);
                    // do something with the response string
                    Log.e(TAG + " response", responseString);
                    return responseString;
                }
            });
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }
}
