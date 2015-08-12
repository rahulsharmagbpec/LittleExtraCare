package littleextracare.bifortis.com.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import littleextracare.bifortis.com.Constants.ApiConstants;
import littleextracare.bifortis.com.Constants.Constants;
import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.WebApi.Api;
import littleextracare.bifortis.com.data.SharedPref;

public class RegisterActivityWithFacebook extends AppCompatActivity {
    private static final int RESULT_LOAD_IMAGE = 1;
    private ImageView imageView;
    private String selectedImagePath;
    private static String img_str;
    private static final String TAG = RegisterActivityWithFacebook.class.getName();

    private CircleImageView circleImageView;

    private EditText nameEditText;
    private EditText lastNameEditText;
    private EditText emailEditText;
    private EditText descriptionEditText;
    private EditText streetEditText;
    private EditText cityEditText;
    private EditText stateEditText;
    private EditText zipEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_activity_with_facebook);

        circleImageView = (CircleImageView) findViewById(R.id.imageViewNormal);
        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });


        /*imageView = (ImageView) findViewById(R.id.imageView2);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });*/

        nameEditText = (EditText) findViewById(R.id.editText5);
        lastNameEditText = (EditText) findViewById(R.id.editText4);
        emailEditText = (EditText) findViewById(R.id.editTextEmail);
        descriptionEditText = (EditText) findViewById(R.id.editText3);
        streetEditText = (EditText) findViewById(R.id.editText2);
        cityEditText = (EditText) findViewById(R.id.editText);
        stateEditText = (EditText) findViewById(R.id.editTextState);
        zipEditText = (EditText) findViewById(R.id.editTextZip);

        //Uri path = Uri.parse("android.resource://littleextracare.bifortis.com.activities/" + R.drawable.heart_color);
        //Log.e(TAG,"=======================" +path.getPath()+"=======================" );


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            img_str = getPath (selectedImage);
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();

            Bitmap bm;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(picturePath, options);
            final int REQUIRED_SIZE = 200;
            int scale = 1;
            while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                    && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                scale *= 2;
            options.inSampleSize = scale;
            options.inJustDecodeBounds = false;
            bm = BitmapFactory.decodeFile(picturePath, options);

            ImageView imageView = (ImageView) findViewById(R.id.imageViewNormal);
            //imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            imageView.setImageBitmap(bm);
        }
    }

    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public void registerButtonClicked()
    {
        String token = SharedPref.getData(this, SharedPrefConstants.tokenValueKey);
        String phone = SharedPref.getData(this, SharedPrefConstants.phoneKey);

        Log.e(TAG, "getString"+token);
        String name = nameEditText.getText().toString().trim();
        String lastName = lastNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();
        String street = streetEditText.getText().toString().trim();
        String city = cityEditText.getText().toString().trim();
        String state = stateEditText.getText().toString().trim();
        String zipCode = zipEditText.getText().toString().trim();
        String gcmKey = SharedPref.getData(getApplicationContext(), SharedPrefConstants.gcmRegistrationKey);

        if(img_str == null)
        {
            //File SDCardRoot = Environment.getExternalStorageDirectory();
            //File file = new File(SDCardRoot,"somefile.jpg");

            String path = SharedPref.getData(getApplicationContext(), SharedPrefConstants.fbImagePath);
            //String path = android.os.Environment.getExternalStorageDirectory().getPath()+
            //        File.separatorChar+ filename;
            if(path != null) {
                Log.e("PATH", path);
                //Uri imageUri = Uri.fromFile(f);
                img_str = path;
            }
        }


        if( name.length()>1 &&
                lastName.length()>1 &&
                description.length()>1 &&
                zipCode.length()>1 &&
                street.length()>1 &&
                city.length()>1 &&
                state.length()>1 &&
                email.length()>1 &&
                img_str != null)
        {
            Log.e(TAG, "image path: "+img_str);
            JSONParser jsonParser = new JSONParser(Api.registerUser, token, name, lastName, description,
                                            zipCode, street, city, state, img_str, email, phone, gcmKey);
            jsonParser.execute();
        }
        else
        {
            Toast.makeText(getApplicationContext(), "Please fill All Details, Select Image.", Toast.LENGTH_SHORT).show();
        }
    }

    private void afterAsyncClass(String s) {
        if(s != null) {
            try {
                JSONObject jObject = new JSONObject(s);
                Boolean result = jObject.getBoolean(ApiConstants.RESPONSE_KEY);
                SharedPref.setData(this, SharedPrefConstants.userId,
                        jObject.getString(ApiConstants.userIdKey));
                SharedPref.setData(this, SharedPrefConstants.password,
                        jObject.getString(ApiConstants.passwordKey));
                if (result)
                {
                    Intent intent = new Intent(this, PaymentMethodActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Register UnSuccessful", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else
        {
            Log.e(TAG, "Response is null in afterAsyncClass(String s)");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_next)
        {
            registerButtonClicked();
            Toast.makeText(getApplicationContext(), "Next", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    class JSONParser extends AsyncTask<String, Void, String> {
        private final String TAG = JSONParser.class.getName();
        private String responseStr;
        ProgressDialog progressDialog;

        final String url;
        final String token;
        final String name;
        final String lastName;
        final String description;
        final String zipCode;
        final String street;
        final String city;
        final String state;
        final String img_str;
        final String email;
        final String phone;
        final String gcmkey;

        public JSONParser(String url, String token, String name, String lastName, String description,
                         String zipCode, String street, String city, String state, String img_str,
                          String email, String phone, String gcmkey) {
            this.url = url;
            this.token = token;
            this.name= name;
            this.lastName = lastName;
            this.description = description;
            this.zipCode = zipCode;
            this.street = street;
            this.city = city;
            this.state = state;
            this.img_str = img_str;
            this.email = email;
            this.phone = phone;
            this.gcmkey = gcmkey;
            progressDialog = new ProgressDialog(RegisterActivityWithFacebook.this);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setTitle(Constants.progressText);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... param) {
            String response = executeMultipartPost(url, name, lastName, email, description, zipCode, street,
                    city, state, img_str, token, gcmkey);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            afterAsyncClass(s);
        }

        public String executeMultipartPost(String url, String name, String lastName,
                                         String email, String description, String zip, String street,
                                         String city, String state, String fileToUpload, String token,
                                         String gcmkey){
            try {
                HttpClient client = new DefaultHttpClient();
                HttpPost poster = new HttpPost(url);

                File image = new File(fileToUpload);  //get the actual file from the device
                MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                entity.addPart(ApiConstants.firstName_key, new StringBody(name));
                entity.addPart(ApiConstants.lastName_key, new StringBody(lastName));
                entity.addPart(ApiConstants.email_key, new StringBody(email));
                entity.addPart(ApiConstants.description_key, new StringBody(description));
                entity.addPart(ApiConstants.zip_key, new StringBody(zip));
                entity.addPart(ApiConstants.street_key, new StringBody(street));
                entity.addPart(ApiConstants.city_key, new StringBody(city));
                entity.addPart(ApiConstants.state_key, new StringBody(state));
                entity.addPart(ApiConstants.token_key, new StringBody(token));
                entity.addPart(ApiConstants.image_key, new FileBody(image, "application/octet-stream"));
                entity.addPart(ApiConstants.phone_key, new StringBody(phone));
                entity.addPart(ApiConstants.os_key, new StringBody(ApiConstants.DeviceOS));
                entity.addPart(ApiConstants.gcm_key, new StringBody(gcmkey));
                poster.setEntity(entity );

                client.execute(poster, new ResponseHandler<Object>() {
                    public Object handleResponse(HttpResponse response) throws IOException {
                        HttpEntity respEntity = response.getEntity();
                        String responseString = EntityUtils.toString(respEntity);
                        // do something with the response string
                        Log.e(TAG+" response",responseString);
                        responseStr =  responseString;
                        return responseString;
                    }
                });
            } catch (Exception e){
                Log.e(TAG, e.toString());
                //do something with the error
            }
            return responseStr;
        }
    }
}
