package littleextracare.bifortis.com.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import littleextracare.bifortis.com.Constants.ApiConstants;
import littleextracare.bifortis.com.Constants.Constants;
import littleextracare.bifortis.com.Constants.ErrorConstants;
import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.WebApi.Api;
import littleextracare.bifortis.com.data.GetJsonFromServer;
import littleextracare.bifortis.com.data.SharedPref;


public class PushNotificationClickedActivity extends AppCompatActivity {

    private static final String TAG = PushNotificationClickedActivity.class.getName();
    private boolean checkTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
        setContentView(R.layout.activity_push_notification_clicked);

        Button acceptButton = (Button) findViewById(R.id.button20);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptToServer acceptToServer = new AcceptToServer(ApiConstants.ACCEPTED);
                acceptToServer.execute();
            }
        });

        Button cancelButton = (Button) findViewById(R.id.button21);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AcceptToServer acceptToServer = new AcceptToServer(ApiConstants.REJECTED);
                acceptToServer.execute();
                finish();
            }
        });
    }

    private void afterAsyncTask() {
        Intent intent = new Intent(PushNotificationClickedActivity.this, MainActivity.class);
        startActivity(intent);
    }

    //void tempFunctionForTest()
    //{
    //    Intent intent = new Intent(PushNotificationClickedActivity.this, CareGiverSideMap.class);
    //    startActivity(intent);
    //}

    void afterUserBooking(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            String status = jsonObject.getString(ApiConstants.bookingStatus);
            if (status.equalsIgnoreCase(ApiConstants.ACCEPTED)) {
                Intent intent = new Intent(PushNotificationClickedActivity.this, CareGiverSideMap.class);
                startActivity(intent);
            } else if (status.equalsIgnoreCase(ApiConstants.REJECTED)) {
                Toast.makeText(getApplicationContext(), Constants.userRejected, Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    @SuppressWarnings("deprecation")
    class AcceptToServer extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String url;
        List<NameValuePair> parameter;
        String token;
        String id;
        String password;
        String status;

        AcceptToServer(String status) {
            progressDialog = new ProgressDialog(PushNotificationClickedActivity.this);
            progressDialog.setCancelable(false);
            this.status = status;

            url = Api.acceptBookingRequest;
            token = SharedPref.getData(getApplicationContext(), SharedPrefConstants.tokenValueKey);
            id = SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_CARE_GIVER_ID);
            password = SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_CARE_GIVER_PASSWORD);
            Log.e(TAG, "CareGiverId=" + id + " ,password=" + password + " ,status" + status);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle(Constants.progressText);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            parameter = new ArrayList<>();
            parameter.add(new BasicNameValuePair(ApiConstants.token_key, token));
            parameter.add(new BasicNameValuePair(ApiConstants.API_ID, id));
            parameter.add(new BasicNameValuePair(ApiConstants.passwordKey, password));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_BOOKING_STATUS, status));

            GetJsonFromServer getJsonFromServer = new GetJsonFromServer();
            String result = getJsonFromServer.getJson(url, ApiConstants.post, parameter);
            if (result != null)
                Log.e(TAG, url + "================" + result);
            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            //{"success":true,"booking_confirmed":1,"message":"The booking was confirmed"}
            try {
                JSONObject jsonObject = new JSONObject(s);
                String success = jsonObject.getString(ApiConstants.success);
                if (success.equalsIgnoreCase("true")) {
                    UserBookingStatus userBookingStatus = new UserBookingStatus();
                    userBookingStatus.execute();
                } else {
                    Toast.makeText(getApplicationContext(), ErrorConstants.acceptFailed, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                //tempFunctionForTest(); //TODO: just for testing need to delete this method later

                Toast.makeText(getApplicationContext(), ErrorConstants.acceptFailed, Toast.LENGTH_SHORT).show();
                Log.e(TAG, e.toString());
            }
        }
    }

    // Timer class for waiting screen
    class MyTask extends TimerTask {
        @Override
        public void run(){
            checkTime = false;
            Log.e(TAG, "Timer");
        }
    }

    @SuppressWarnings("deprecation")
    class UserBookingStatus extends AsyncTask<String, Void, String> {

        String url;
        String id;
        String password;
        String token;
        List<NameValuePair> parameter;
        boolean check = true;
        ProgressDialog progressDialog;

        UserBookingStatus() {
            progressDialog = new ProgressDialog(PushNotificationClickedActivity.this);
            progressDialog.setCancelable(false);

            url = Api.userBookingStatus;
            id = SharedPref.getData(getApplicationContext(), SharedPrefConstants.careGiverId);
            password = SharedPref.getData(getApplicationContext(), SharedPrefConstants.password);
            token = SharedPref.getData(getApplicationContext(), SharedPrefConstants.tokenValueKey);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.e(TAG, "progress show");
            progressDialog.setTitle(Constants.progressText);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            String result;
            parameter = new ArrayList<>();
            parameter.add(new BasicNameValuePair(ApiConstants.token_key, token));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_id, id));
            parameter.add(new BasicNameValuePair(ApiConstants.passwordKey, password));

            Timer myTimer = new Timer("MyTimer", true);
            myTimer.scheduleAtFixedRate(new MyTask(), Constants.waitTime, 1000);

            do {
                GetJsonFromServer getJsonFromServer = new GetJsonFromServer();
                result = getJsonFromServer.getJson(url, ApiConstants.post, parameter);
                if (result != null)
                    Log.e(TAG, url + "================" + result);
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String bookingStatus = jsonObject.getString(ApiConstants.bookingStatus);
                    if (bookingStatus.equalsIgnoreCase(ApiConstants.ACCEPTED) ||
                            bookingStatus.equalsIgnoreCase(ApiConstants.REJECTED)) {
                        check = false;
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            }
            while (check && checkTime);
            myTimer.cancel();
            myTimer.purge();

            /*GetJsonFromServer getJsonFromServer = new GetJsonFromServer();
            String result = getJsonFromServer.getJson(url, ApiConstants.post, parameter);
            if(result != null)
                Log.e(TAG, url+"================"+result);*/

            return result;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.e(TAG, "progress Hide");
            progressDialog.dismiss();
            if (s != null)
                afterUserBooking(s);
            else
                Toast.makeText(getApplicationContext(), ErrorConstants.serverError, Toast.LENGTH_SHORT).show();
        }
    }
}
