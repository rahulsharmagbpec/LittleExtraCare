package littleextracare.bifortis.com.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
public class VerifyPhoneNumberActivity extends AppCompatActivity {
    private  final String TAG = VerifyPhoneNumberActivity.class.getName();
    private String otp;
    private EditText verifyEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        otp = SharedPref.getData(this, SharedPrefConstants.otpKey);
        verifyEditText = (EditText) findViewById(R.id.textViewPhoneVerify);
        verifyEditText.setText(otp);
    }

    public void verifyButtonClicked(View v)
    {
        String token = SharedPref.getData(this, SharedPrefConstants.tokenValueKey);
        String phone = SharedPref.getData(this, SharedPrefConstants.phoneKey);
        if(verifyEditText.getText().length()>1)
        {
            List<NameValuePair> params= new ArrayList<>();
            params.add(new BasicNameValuePair(ApiConstants.phoneNumber_Key, phone));
            params.add(new BasicNameValuePair(ApiConstants.otp, otp));
            params.add(new BasicNameValuePair(ApiConstants.token_key, token));
            new JSONParser(Api.verifyOtp, "POST", params).execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please Enter 10 digit Number", Toast.LENGTH_SHORT).show();
        }
    }

    private void afterAsyncTask(String s) {
        try {
            JSONObject json = new JSONObject(s);
            if(json.getBoolean(SharedPrefConstants.otpCorrect))
            {
                Intent intent = new Intent(this, RegisterActivityWithFacebook.class);
                startActivity(intent);
                //finish();
            }
            else
            {
                Toast.makeText(getApplicationContext(), "OTP Expired", Toast.LENGTH_SHORT).show();
            }
        }
        catch(Exception e)
        {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressWarnings("deprecation")
    class JSONParser extends AsyncTask<String, Void, String> {

        String url;
        final String method;
        final List<NameValuePair> params;
        ProgressDialog progressDialog;

        public JSONParser(String url, String method,
                          List<NameValuePair> params) {
            this.url = url;
            this.method = method;
            this.params = params;
            progressDialog = new ProgressDialog(VerifyPhoneNumberActivity.this);
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