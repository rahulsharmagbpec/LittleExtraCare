package littleextracare.bifortis.com.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import littleextracare.bifortis.com.Constants.ApiConstants;
import littleextracare.bifortis.com.Constants.Constants;
import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.WebApi.Api;
import littleextracare.bifortis.com.data.GetJsonFromServer;
import littleextracare.bifortis.com.data.SharedPref;

@SuppressWarnings("deprecation")
public class RegisterPhoneNumberActivity extends AppCompatActivity {
    private static final String TAG = RegisterPhoneNumberActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_phone_number);
    }

    public void nextButtonClicked(View v)
    {
        EditText phoneEditText = (EditText) findViewById(R.id.textViewPhone);
        Constants.phoneNumber = phoneEditText.getText().toString().trim();
        int length = Constants.phoneNumber.length();
        if(length > 9) {
            SharedPref.setData(this, SharedPrefConstants.phoneKey, Constants.phoneNumber);
            SharedPreferences sharedPref = getSharedPreferences(SharedPrefConstants.tokenValueKey,
                                                                    Context.MODE_PRIVATE);
            String token = sharedPref.getString(Constants.TOKEN, null);

            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair(ApiConstants.token_key, token));
            params.add(new BasicNameValuePair(ApiConstants.phoneNumber_Key, Constants.phoneNumber));

            JSONParser jsonParser = new JSONParser(Api.getPhoneOtp, "post", params);
            jsonParser.execute();
        }
        else{
            Toast.makeText(getApplicationContext(), "Please Enter 10 digit Number", Toast.LENGTH_SHORT).show();
        }
    }

    //this function will run after completing the network operation
    private void afterAsyncTask(String s)
    {
        try {
            JSONObject json = new JSONObject(s);
            if (json.has(SharedPrefConstants.otpKey)) {

                String value = json.getString(SharedPrefConstants.otpKey);
                SharedPref.setData(this, SharedPrefConstants.otpKey, value);

                Intent intent = new Intent(this, VerifyPhoneNumberActivity.class);
                startActivity(intent);
                //finish();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    public void careGiverClicked(View v)
    {
        Intent intent = new Intent(this, PromoCodeActivity.class);
        startActivity(intent);
    }





    class JSONParser extends AsyncTask<String, Void, String> {

        private final String TAG = JSONParser.class.getName();

        String url;
        final String method;
        final List<NameValuePair> params;
        ProgressDialog progressDialog;

        public JSONParser(String url, String method, List<NameValuePair> params) {
            this.url = url;
            this.method = method;
            this.params = params;
            progressDialog = new ProgressDialog(RegisterPhoneNumberActivity.this);
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
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            afterAsyncTask(s);
        }
    }
}
