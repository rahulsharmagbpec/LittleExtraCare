package littleextracare.bifortis.com.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import littleextracare.bifortis.com.Constants.Constants;
import littleextracare.bifortis.com.Constants.ErrorConstants;
import littleextracare.bifortis.com.Constants.GCMConstants;
import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.WebApi.Api;
import littleextracare.bifortis.com.data.GetJsonFromServer;
import littleextracare.bifortis.com.data.InternetCheck;
import littleextracare.bifortis.com.data.SharedPref;



public class LetsGoActivity extends AppCompatActivity {
    private final String TAG = LetsGoActivity.class.getName();
    private GoogleCloudMessaging gcm;
    private String regid;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lets_go);

        SharedPref.setData(getApplicationContext(), SharedPrefConstants.tokenValueKey, null);

        InternetCheck internetCheck = new InternetCheck(this);
        if(internetCheck.isInternetAvailable())
        {
                List<NameValuePair> param = new ArrayList<>();
                Async jsonParser = new Async(Api.getTokenUrl, "GET", param);
                jsonParser.execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), ErrorConstants.internetError, Toast.LENGTH_SHORT).show();
        }
    }

    public void buttonClickedLetsGo(View v)
    {
        String id = SharedPref.getData(this, SharedPrefConstants.userId);
        String pass = SharedPref.getData(this, SharedPrefConstants.password);
        String token = SharedPref.getData(this, SharedPrefConstants.tokenValueKey);

        //if user is already Registered
        if(id != null && pass != null && token != null) {
            if (id.length() > 1 && pass.length() > 1) {
                SharedPref.setData(this, Constants.REQUESTING, "false");
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
        }
        // else new user
        else {
            if (token != null && regid != null) {
                Intent intent = new Intent(this, RegisterPhoneNumberActivity.class);
                startActivity(intent);
                //finish();
            } else {
                Toast.makeText(getApplicationContext(), ErrorConstants.serverError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Button click listener Just For Testing
    public void openPushNotification(View v)
    {
        Intent intent = new Intent(this, PushNotificationClickedActivity.class);
        startActivity(intent);
    }

    // function to call after successfully getting token from server
    private void afterAsync(String result)
    {
        try
        {
            JSONObject json = new JSONObject(result);
            String token = json.getString(SharedPrefConstants.tokenKey);
            SharedPref.setData(this, SharedPrefConstants.tokenValueKey, token);
            // Log.e(TAG, "saved to sharedPref: "+token);
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    // Inner class for getting CSRF token from server
    class Async extends AsyncTask<String, Void, String>
    {
        String url;
        String method;
        List<NameValuePair> params;
        ProgressDialog progressDialog;

        Async(String url, String method, List<NameValuePair> params)
        {
            this.url = url;
            this.method = method;
            this.params = params;
            progressDialog = new ProgressDialog(LetsGoActivity.this);
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle(Constants.progressText);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            GetJsonFromServer getJson = new GetJsonFromServer();
            String result = getJson.getJson(url, method, params);

            regid = SharedPref.getData(getApplicationContext(), SharedPrefConstants.gcmRegistrationKey);
            if(regid == null) {
                getRegId();
            }
            else
            {
                Log.e(TAG, regid);
            }
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if(s != null)
            {
                afterAsync(s);
            }
            else
            {
                Toast.makeText(getApplicationContext(), ErrorConstants.serverError, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // function to get "GCM REGISTRATION ID" from Google Server
    private void getRegId() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg;
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(LetsGoActivity.this);
                        //gcm = GoogleCloudMessaging.getInstance(this.co);
                    }
                    regid = gcm.register(GCMConstants.gcmKey);
                    if (regid.length() > 5) {
                        Log.e("gcm id", regid);
                    }
                    msg = "Device registered, registration ID=" + regid;
                    Log.e("gcm", msg);
                    SharedPref.setData(LetsGoActivity.this, SharedPrefConstants.gcmRegistrationKey, regid);

                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.e(TAG, ex.toString());
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {

            }
        }.execute(null, null, null);
    }
}