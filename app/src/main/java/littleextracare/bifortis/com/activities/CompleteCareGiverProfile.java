package littleextracare.bifortis.com.activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import littleextracare.bifortis.com.Constants.ApiConstants;
import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.WebApi.Api;
import littleextracare.bifortis.com.data.GetJsonFromServer;
import littleextracare.bifortis.com.data.SharedPref;


public class CompleteCareGiverProfile extends AppCompatActivity {

    private final static String TAG = CompleteCareGiverProfile.class.getName();
    ImageView ivHeart;
    ImageView ivEdu;
    ImageView ivFinger;
    ImageView ivCar;
    ImageView ivDog;
    Button btnSave;
    EditText edDesc;

    private static boolean fHeart = false;
    private static boolean fEdu = false;
    private static boolean fFinger = false;
    private static boolean fCar = false;
    private static boolean fDog = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_care_giver_profile);

        ivHeart = (ImageView) findViewById(R.id.imageIcon1);
        ivEdu = (ImageView) findViewById(R.id.imageIcon2);
        ivFinger = (ImageView) findViewById(R.id.imageIcon3);
        ivCar = (ImageView) findViewById(R.id.imageIcon4);
        ivDog = (ImageView) findViewById(R.id.imageIcon5);
        edDesc = (EditText) findViewById(R.id.editText18);
        btnSave = (Button) findViewById(R.id.button19);

        ivHeart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fHeart)
                {
                    fHeart = false;
                    ivHeart.setImageResource(R.drawable.heart_gray);
                }
                else
                {
                    fHeart = true;
                    ivHeart.setImageResource(R.drawable.heart_color);
                }

            }
        });

        ivEdu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fEdu)
                {
                    fEdu = false;
                    ivEdu.setImageResource(R.drawable.edu_gray);
                }
                else
                {
                    fEdu = true;
                    ivEdu.setImageResource(R.drawable.edu_color);
                }
            }
        });

        ivFinger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fFinger)
                {
                    fFinger = false;
                    ivFinger.setImageResource(R.drawable.finger_gray);
                }
                else
                {
                    fFinger = true;
                    ivFinger.setImageResource(R.drawable.finger_color);
                }
            }
        });

        ivCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fCar)
                {
                    fCar = false;
                    ivCar.setImageResource(R.drawable.car_gray);
                }
                else
                {
                    fCar = true;
                    ivCar.setImageResource(R.drawable.car_color);
                }
            }
        });

        ivDog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fDog)
                {
                    fDog = false;
                    ivDog.setImageResource(R.drawable.dog_gray);
                }
                else
                {
                    fDog = true;
                    ivDog.setImageResource(R.drawable.dog_color);
                }
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String descText = edDesc.getText().toString().trim();
                if( descText.length() > 1)
                {
                    StoreToServer storeToServer = new StoreToServer(descText);
                    storeToServer.execute();
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "fill above field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    class StoreToServer extends AsyncTask<String, Void, String> {

        String url;
        String description;
        String token;
        String id;
        String password;
        ProgressDialog progressDialog;

        StoreToServer(String descText)
        {
            url = Api.storeCareGiverDesc;
            description = descText;
            token = SharedPref.getData(getApplicationContext(), SharedPrefConstants.tokenValueKey);
            id = SharedPref.getData(getApplicationContext(), SharedPrefConstants.userId);
            password = SharedPref.getData(getApplicationContext(), SharedPrefConstants.password);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(getApplicationContext());
            progressDialog.setCancelable(false);
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(String... params) {
            List<NameValuePair> parameter = new ArrayList<>();
            parameter.add(new BasicNameValuePair(ApiConstants.token_key, token));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_id, id));
            parameter.add(new BasicNameValuePair(ApiConstants.passwordKey, password));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_DESC, description));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_SAVE_HEART, String.valueOf(fHeart)));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_SAVE_EDU, String.valueOf(fEdu)));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_SAVE_FINGER, String.valueOf(fFinger)));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_SAVE_CAR, String.valueOf(fCar)));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_SAVE_DOG, String.valueOf(fDog)));

            GetJsonFromServer getJson = new GetJsonFromServer();
            String response = getJson.getJson(url, ApiConstants.post, parameter);
            if(response != null)
                Log.e(TAG, url+"==============="+response);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            try {
                JSONObject jObject = new JSONObject(s);
                if(!jObject.isNull(ApiConstants.success))
                {
                    if(jObject.getString(ApiConstants.success).equalsIgnoreCase("true"))
                    {
                        String careGiverId = jObject.getString(ApiConstants.CARE_id);
                        String careGiverPassword = jObject.getString(ApiConstants.passwordKey);

                        SharedPref.setData(getApplicationContext(), SharedPrefConstants.PREF_PROMO_CODE_FLAG, "true");
                        SharedPref.setData(getApplicationContext(), SharedPrefConstants.PREF_CARE_GIVER_ID, careGiverId);
                        SharedPref.setData(getApplicationContext(), SharedPrefConstants.PREF_CARE_GIVER_PASSWORD, careGiverPassword);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "failed to save", Toast.LENGTH_SHORT).show();
                    }

                    //Intent intent = new Intent(CompleteCareGiverProfile.this, MainActivity.class);
                    //startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "failed to save", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
                Toast.makeText(getApplicationContext(), "failed to save", Toast.LENGTH_SHORT).show();
            }

        }
    }
}
