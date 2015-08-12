package littleextracare.bifortis.com.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import littleextracare.bifortis.com.Constants.ApiConstants;
import littleextracare.bifortis.com.Constants.Constants;
import littleextracare.bifortis.com.WebApi.Api;

@SuppressWarnings("deprecation")
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = RegisterActivity.class.getName();
    private static final int SELECT_PICTURE = 1;
    private String selectedImagePath;
    private ImageView imageView;
    private static String img_str;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,"Select Picture"), SELECT_PICTURE);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {

                Uri selectedImageUri = data.getData();
                selectedImagePath = getPath(selectedImageUri);
                Log.e(TAG, "Image Path : " + selectedImagePath);
                imageView.setImageURI(selectedImageUri);

                InputStream iStream = null;
                try {
                    iStream = getContentResolver().openInputStream(selectedImageUri);
                    byte[] inputData = getBytes(iStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                Log.e(TAG, "string:" + img_str);
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        img_str = byteBuffer.toString();
        return byteBuffer.toByteArray();
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void registerButtonClicked(View v)
    {
        EditText nameEditText = (EditText) findViewById(R.id.editText5);
        EditText lastNameEditText = (EditText) findViewById(R.id.editText4);
        EditText descriptionEdittext = (EditText) findViewById(R.id.editText3);
        EditText zipEditText = (EditText) findViewById(R.id.editText2);
        EditText addressEditText = (EditText) findViewById(R.id.editText);
        EditText emailEditText = (EditText) findViewById(R.id.editTextEmail);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

        String token = sharedPref.getString(Constants.TOKEN, "");
        String name = nameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String description = descriptionEdittext.getText().toString().trim();
        String zipCode = zipEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();

        if(name.length() > 1 && lastName.length()>1 && description.length() > 1
                && zipCode.length()>1 && address.length() > 1 && email.length() > 1)
        {
            List<NameValuePair> param = new ArrayList<NameValuePair>();
            param.add(new BasicNameValuePair(ApiConstants.token_key, token));
            param.add(new BasicNameValuePair(ApiConstants.firstName_key, name));
            param.add(new BasicNameValuePair(ApiConstants.lastName_key, lastName));
            param.add(new BasicNameValuePair(ApiConstants.description_key, description));
            param.add(new BasicNameValuePair(ApiConstants.zip_key, zipCode));
            //param.add(new BasicNameValuePair(ApiConstants.address_key, address));
            param.add(new BasicNameValuePair(ApiConstants.image_key, img_str));
            param.add(new BasicNameValuePair(ApiConstants.email_key, email));
            param.add(new BasicNameValuePair(ApiConstants.phone_key, "7777777777"));
            // TODO: need to change above number later

            JSONParser jsonParser = new JSONParser(Api.registerUser, "POST" ,param);
            jsonParser.execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please fill Above Details First", Toast.LENGTH_SHORT).show();
        }


    }

    //method to run after getting result from server
    private void afterAsyncClass(String s) {
        try {
            JSONObject jobject = new JSONObject(s);
            Boolean result = jobject.getBoolean(ApiConstants.RESPONSE_KEY);
            if(result)
            {
                Intent intent = new Intent(this, PaymentMethodActivity.class);
                startActivity(intent);
            }
            else
            {
                Toast.makeText(getApplicationContext(), "Register UnSuccessful", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    class JSONParser extends AsyncTask<String, Void, String> {
        private final String TAG = JSONParser.class.getName();

        private InputStream inputStream = null;
        private JSONObject jsonObject = null;
        private String json = "";

        String url;
        String method;
        List<NameValuePair> params;

        public JSONParser(String url, String method,
                          List<NameValuePair> params) {
            this.url = url;
            this.method = method;
            this.params = params;
        }

        @Override
        protected String doInBackground(String... paramss) {
            try {
                // if condition is for POST method
                if(method.equalsIgnoreCase("POST")){
                    // request method inputStream POST
                    // defaultHttpClient
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(url);
                    httpPost.setEntity(new UrlEncodedFormEntity(params));

                    HttpResponse httpResponse = httpClient.execute(httpPost);
                    HttpEntity httpEntity = httpResponse.getEntity();
                    inputStream = httpEntity.getContent();
                }
                //else condition is for GET method
                else if(method.equalsIgnoreCase("GET"))
                {
                    // request method inputStream GET
                    DefaultHttpClient httpClient = new DefaultHttpClient();
                    String paramString = URLEncodedUtils.format(params, "utf-8");
                    url += "?" + paramString;
                    HttpGet httpGet = new HttpGet(url);

                    HttpResponse httpResponse = httpClient.execute(httpGet);
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
                Log.e(TAG, json);
            } catch (Exception e) {
                Log.e(TAG, "Error converting result " + e.toString());
            }

            // try parse the string to a JSON object
            try {
                jsonObject = new JSONObject(json);
            } catch (JSONException e) {
                Log.e(TAG, "Error parsing data " + e.toString());
            }

            Log.e(TAG, jsonObject.toString()
            );

            // return JSON String
            return jsonObject.toString();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            afterAsyncClass(s);

        }
    }


}
