package littleextracare.bifortis.com.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.data.SharedPref;


/**
 * A placeholder fragment containing a simple view.
 */
public class RegisterActivityWithFacebookFragment extends Fragment {

    private final static String TAG = RegisterActivityWithFacebookFragment.class.getName();

    private EditText edFirstName;
    private EditText edLastName;
    private EditText edEmail;
    private ImageView imageView;
    private ProfilePictureView ivProfilePic;
    private Uri imageUri;
    private String profileId;

    private CallbackManager callbackManager;

    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            AccessToken accessToken = loginResult.getAccessToken();
            Profile profile = Profile.getCurrentProfile();

            GraphRequest request = GraphRequest.newMeRequest(
                    accessToken,
                    new GraphRequest.GraphJSONObjectCallback() {

                        @Override
                        public void onCompleted(
                                JSONObject object,
                                GraphResponse response) {
                            // Application code
                            Log.e("email", response.getJSONObject().toString());
                            try {
                                SharedPref.setData(getActivity(), SharedPrefConstants.fbEmail,
                                        response.getJSONObject().getString("email"));
                                profileId = response.getJSONObject().getString("id");
                                String name = response.getJSONObject().getString("name");
                                String[] names = name.split(" ");
                                SharedPref.setData(getActivity(), SharedPrefConstants.fbId,
                                        profileId);
                                SharedPref.setData(getActivity(), SharedPrefConstants.fbFirstName,
                                        names[0]);
                                SharedPref.setData(getActivity(), SharedPrefConstants.fbLastName,
                                        names[1]);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            edFirstName.setText(SharedPref.getData(getActivity(),
                                    SharedPrefConstants.fbFirstName));
                            edLastName.setText(SharedPref.getData(getActivity(),
                                    SharedPrefConstants.fbLastName));
                            edEmail.setText(SharedPref.getData(getActivity(),
                                    SharedPrefConstants.fbEmail));
                            imageView.setVisibility(View.GONE);
                            ivProfilePic.setVisibility(View.VISIBLE);
                            ivProfilePic.setProfileId(profileId);
                            DownloadImageViaAndroidHttpClientTask downloadImage = new
                                    DownloadImageViaAndroidHttpClientTask("https://graph.facebook.com/" + profileId + "/picture?type=large");
                            downloadImage.execute();
                        }
                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email");
            request.setParameters(parameters);
            request.executeAsync();

            displayMessage(profile);
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException e) {
        }
    };

    public RegisterActivityWithFacebookFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        setHasOptionsMenu(true);
        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {

            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                displayMessage(newProfile);
            }
        };

        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_register_activity_with_facebook, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.e(TAG, "clicked");
        switch (item.getItemId()) {
            case R.id.action_next:

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_activity_with_facebook, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);

        loginButton.setReadPermissions("user_friends");
        loginButton.setReadPermissions("email");
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);

        edFirstName = (EditText) view.findViewById(R.id.editText5);
        edLastName = (EditText) view.findViewById(R.id.editText4);
        edEmail = (EditText) view.findViewById(R.id.editTextEmail);
        imageView = (ImageView) view.findViewById(R.id.imageViewNormal);
        //imageView = (ImageView) view.findViewById(R.id.imageView2);
        ivProfilePic = (ProfilePictureView) view.findViewById(R.id.image);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void displayMessage(Profile profile) {
        if (profile != null) {
            SharedPref.setData(this.getActivity(), SharedPrefConstants.fbFirstName,
                    profile.getFirstName());
            SharedPref.setData(this.getActivity(), SharedPrefConstants.fbLastName,
                    profile.getLastName());

            Log.e(TAG, profile.getFirstName());
            Log.e(TAG, profile.getLastName());
            Log.e(TAG, profile.getId());
            imageUri = profile.getProfilePictureUri(100, 100);
            Async async = new Async(imageUri);
            async.execute();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    @Override
    public void onResume() {
        super.onResume();
        Profile profile = Profile.getCurrentProfile();
        displayMessage(profile);
    }

    class Async extends AsyncTask<String, Void, String> {
        Uri sourceuri;

        Async(Uri uri) {
            this.sourceuri = uri;
        }

        @Override
        protected String doInBackground(String... params) {
            savefile();
            return null;
        }

        void savefile() {
            try {

                String url = "http://www.twitter.com";

                URL obj = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) obj.openConnection();
                conn.setReadTimeout(5000);
                conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                conn.addRequestProperty("User-Agent", "Mozilla");
                conn.addRequestProperty("Referer", "google.com");

                System.out.println("Request URL ... " + url);

                boolean redirect = false;

                // normally, 3xx is redirect
                int status = conn.getResponseCode();
                if (status != HttpURLConnection.HTTP_OK) {
                    if (status == HttpURLConnection.HTTP_MOVED_TEMP
                            || status == HttpURLConnection.HTTP_MOVED_PERM
                            || status == HttpURLConnection.HTTP_SEE_OTHER)
                        redirect = true;
                }

                System.out.println("Response Code ... " + status);

                if (redirect) {

                    // get redirect url from "location" header field
                    String newUrl = conn.getHeaderField("Location");

                    // get the cookie if need, for login
                    String cookies = conn.getHeaderField("Set-Cookie");

                    // open the new connnection again
                    conn = (HttpURLConnection) new URL(newUrl).openConnection();
                    conn.setRequestProperty("Cookie", cookies);
                    conn.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
                    conn.addRequestProperty("User-Agent", "Mozilla");
                    conn.addRequestProperty("Referer", "google.com");

                    System.out.println("Redirect to URL : " + newUrl);

                }

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuffer html = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    html.append(inputLine);
                }
                in.close();

                Log.e("done", "URL Content... \n" + html.toString());
                Log.e("Done", "Done");

            } catch (Exception e) {
                e.printStackTrace();
            }

            /*String sourceFilename= sourceuri.getPath();
            sourceFilename = "http://graph.facebook.com"+sourceFilename+"?type=large";
            try {
                //set the download URL, a url that points to a file on the internet
                //this is the file to be downloaded
                URL url = new URL(sourceFilename);

                //create the new connection
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                //set up some things on the connection
                urlConnection.setRequestMethod("GET");
                //urlConnection.setDoOutput(true);

                //and connect!
                urlConnection.connect();

                //set the path where we want to save the file
                //in this case, going to save it on the root directory of the
                //sd card.
                File SDCardRoot = Environment.getExternalStorageDirectory();
                //create a new file, specifying the path, and the filename
                //which we want to save the file as.
                File file = new File(SDCardRoot,"somefile.jpg");
                Log.e("saving path", file.getAbsolutePath());

                //this will be used to write the downloaded data into the file we created
                FileOutputStream fileOutput = new FileOutputStream(file);

                //this will be used in reading the data from the internet
                InputStream inputStream = urlConnection.getInputStream();

                //this is the total size of the file
                int totalSize = urlConnection.getContentLength();
                Log.e("size", totalSize+"");
                //variable to store total downloaded bytes
                int downloadedSize = 0;

                //create a buffer...
                byte[] buffer = new byte[1024];
                int bufferLength = 0; //used to store a temporary size of the buffer

                //now, read through the input buffer and write the contents to the file
                while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
                    Log.e("while","while");
                    //add the data in the buffer to the file in the file output stream (the file on the sd card
                    fileOutput.write(buffer, 0, bufferLength);
                    //add up the size so we know how much is downloaded
                    downloadedSize += bufferLength;
                    //this is where you would do something to report the prgress, like this maybe
                    //updateProgress(downloadedSize, totalSize);

                }
                //close the output stream when done
                fileOutput.flush();
                fileOutput.close();
                inputStream.close();

                //catch some possible errors...
            } catch (MalformedURLException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
                e.printStackTrace();
            }*/

            /*Log.e(TAG, "saving file");
            String sourceFilename= sourceuri.getPath();
            String destinationFilename = android.os.Environment.getExternalStorageDirectory().getPath()+
                    File.separatorChar+"profile.jpg";

            BufferedInputStream bis = null;
            BufferedOutputStream bos = null;
            Log.e(TAG+ " source file", sourceFilename);
            sourceFilename = "graph.facebook.com"+sourceFilename+"?type=large";
            try {
                bis = new BufferedInputStream(new FileInputStream(sourceFilename));
                bos = new BufferedOutputStream(new FileOutputStream(destinationFilename, false));
                byte[] buf = new byte[1024];
                bis.read(buf);
                do {
                    bos.write(buf);
                } while(bis.read(buf) != -1);
            } catch (IOException e) {
                Log.e(TAG+"failed", e.toString());

            } finally {
                try {
                    if (bis != null) bis.close();
                    if (bos != null) bos.close();
                } catch (IOException e) {
                    Log.e(TAG +"save fialed", e.toString());
                }
            }*/
        }
    }

    class DownloadImageViaAndroidHttpClientTask extends AsyncTask<Object, Void, Drawable> {
        //private ImageView imgView;
        private ProgressDialog dialog;
        private AndroidHttpClient androidHttpClient = AndroidHttpClient.newInstance("Android");
        String url;

        DownloadImageViaAndroidHttpClientTask(String url) {
            this.url = url;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(getActivity());
            dialog.setMessage("Please wait...");
            dialog.show();
        }


        @Override
        protected Drawable doInBackground(Object... params) {

            //imgView =  (ImageView) params[1];

            try {

                HttpGet httpGet = new HttpGet(url);
                HttpResponse httpResponse = androidHttpClient.execute(httpGet);
                androidHttpClient.close();

                final int statusCode = httpResponse.getStatusLine().getStatusCode();

                if (statusCode != HttpStatus.SC_OK) {
                    Header[] headers = httpResponse.getHeaders("Location");

                    if (headers != null && headers.length != 0) {
                        String newUrl = headers[headers.length - 1].getValue();

                        Log.e(TAG, "newUrl=>" + newUrl);

                        /**
                         * call again with new URL to get image
                         */
                        return downloadImage(newUrl);
                    } else {
                        return null;
                    }
                }
            } catch (Exception ex) {
                Log.e(TAG, "DownloadImageTask doInBackground() Exception=>" + ex);
            }

            return null;
        }


        @Override
        protected void onPostExecute(Drawable drawable) {

            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            if (drawable != null) {
                Log.e(TAG, "image download");
                //imgView.setImageDrawable(drawable);
            }
        }

        private Drawable downloadImage(String stringUrl) throws IOException {
            URL url = new URL(stringUrl);
            InputStream input = url.openStream();
            try {
                //The sdcard directory e.g. '/sdcard' can be used directly, or
                //more safely abstracted with getExternalStorageDirectory()
                File storagePath = Environment.getExternalStorageDirectory();
                OutputStream output = new FileOutputStream(storagePath + "/myImage.png");
                SharedPref.setData(getActivity(), SharedPrefConstants.fbImagePath, storagePath + "/myImage.png");
                try {
                    byte[] buffer = new byte[1024];
                    int bytesRead = 0;
                    while ((bytesRead = input.read(buffer, 0, buffer.length)) >= 0) {
                        output.write(buffer, 0, bytesRead);
                    }
                } finally {
                    output.close();
                }
            } finally {
                input.close();
            }
            Log.e(TAG, "image Saved");

           /* URL url = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;

            try {
                url = new URL(stringUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setUseCaches(true);
                inputStream = connection.getInputStream();


                Drawable d = Drawable.createFromStream(inputStream, "src name");
                inputStream.close();

                return d;
            } catch (Exception e) {
                Log.e(TAG, "Error while retrieving bitmap from " + e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }*/

            return null;
        }
    }
}