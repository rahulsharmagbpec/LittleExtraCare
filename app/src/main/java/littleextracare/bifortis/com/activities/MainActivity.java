package littleextracare.bifortis.com.activities;


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
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
import littleextracare.bifortis.com.adapters.NavigationDrawerAdapter;
import littleextracare.bifortis.com.data.GetJsonFromServer;
import littleextracare.bifortis.com.data.LocationProvider;
import littleextracare.bifortis.com.data.SharedPref;


public class MainActivity extends AppCompatActivity
        implements NavigationDrawerCallbacks,LocationProvider.LocationCallback {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private Toolbar mToolbar;

    private static final String TAG = MainActivity.class.getName();

  private static Location mCurrentLocation;
    private boolean confirmRequest = false;
    Dialog dialog;
    boolean tempflag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.fragment_drawer);

        // Set up the drawer.
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);

        try {
            int position = getIntent().getIntExtra("position", 0);
            selectedItem(position);
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        //Toast.makeText(this, "Menu item selected -> " + position, Toast.LENGTH_SHORT).show();

        selectedItem(position);
    }


    @Override
    public void onBackPressed() {
        if (mNavigationDrawerFragment.isDrawerOpen())
            mNavigationDrawerFragment.closeDrawer();
        else
            super.onBackPressed();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //final Context context = getApplicationContext();
        //Navigation Drawer Switch Listener
        Switch s = (Switch) findViewById(R.id.nav_switch);
        if(s != null) {
            s.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    SharedPref.setData(getApplicationContext(), SharedPrefConstants.PREF_SWITCH, isChecked+"");

                    // TODO : Need to change Later
                    if(isChecked) {
                        mNavigationDrawerFragment.oldDrawer();
                    }
                    else {
                        mNavigationDrawerFragment.updateDrawer();
                    }

                    String flag = SharedPref.getData(getApplicationContext(), SharedPrefConstants.PREF_PROMO_CODE_FLAG);
                    if(flag != null)
                    {
                        if(!flag.equalsIgnoreCase("true"))
                        {
                            showPromoCodeDialog();
                        }
                        else
                        {
                            if(isChecked)
                            {
                                //mNavigationDrawerFragment.closeDrawer();
                                Toast.makeText(getApplicationContext(), Constants.becomeCareGiver, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                    else
                    {
                        showPromoCodeDialog();
                    }
                }
            });
        }


        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }


    private void showPromoCodeDialog()
    {
        //================this code for testing ==========================================================
        String userId = SharedPref.getData(getApplicationContext(),SharedPrefConstants.userId);//=========
        Log.e(TAG, "USER ID : "+userId);                                                       //=========
        //================================================================================================

        dialog = new Dialog(MainActivity.this);
        //final Dialog finaldialog = dialog;
        dialog.setContentView(R.layout.dialog_promo_code);
        dialog.setTitle("Enter Invitation code");

        Button dialogButton = (Button) dialog.findViewById(R.id.button18);
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //dialog.dismiss();
                //Intent intent = new Intent(getApplicationContext(), CompleteCareGiverProfile.class);
                //startActivity(intent);


                TextView tvPromoCode = (TextView) dialog.findViewById(R.id.textView11);
                String promoCodeText = tvPromoCode.getText().toString().trim();
                if(promoCodeText != null) {
                    if (promoCodeText.length() > 1) {
                        CheckPromoCode checkPromoCode = new CheckPromoCode(promoCodeText);
                        checkPromoCode.execute();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), Constants.descError, Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(getApplicationContext(), Constants.descError, Toast.LENGTH_SHORT).show();
                }

            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Log.e(TAG, "Dialog is Dismissed");
                Switch s2 = (Switch) findViewById(R.id.nav_switch);
                s2.setChecked(false);
            }
        });

        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //finish();
                    dialog.dismiss();
                    Log.e(TAG, "discmiss");
                    Switch s2 = (Switch) findViewById(R.id.nav_switch);
                    s2.setChecked(false);
                }
                return true;
            }
        });
        dialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void selectedItem(int position){
        Fragment fragment=null;
        NavigationDrawerAdapter.count = 0;
        switch (position){
            case 0:
                String track = SharedPref.getData(this, Constants.REQUESTING);
                if(track == null)
                    track = "false";
                if(track.equalsIgnoreCase("true"))
                {
                    fragment = new TrackOnMapFragment();
                }
                else
                {
                    fragment = new MapsActivity();
                }
                break;
            case 1:
                fragment = new MapsActivity();
                //Intent profileIntent=new Intent(MainActivity.this, CustomerProfileActivity.class);
                //startActivity(profileIntent);
                break;

            case 2:

                fragment = new BookMarksActivity();

                //Intent bookmarkIntent=new Intent(MainActivity.this, BookMarksActivity.class);
                //startActivity(bookmarkIntent);
                //fragment = new FragmentBookmark();
                break;
            case 101:
                fragment=new TrackOnMapFragment();
                break;
            case 3:
                fragment = new ActivityInviteFriends();
                //Intent intent = new Intent(MainActivity.this, ActivityInviteFriends.class);
                //startActivity(intent);
                break;
            case 4:
                //Intent availability = new Intent(this, AvailabilityActivity.class);
                //this.finish();
                //startActivity(availability);
                break;
            case 5:
                break;
        }

        if(fragment!=null){


          FragmentManager  fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment).commit();
        }
    }


    @Override
    public void handleNewLocation(Location location) {

       mCurrentLocation=location;
    }

    class CheckPromoCode extends AsyncTask<String, Void, String> {

        String url;
        String token;
        String id;
        String password;
        String careGiverToken;
        ProgressDialog progressDialog;

        CheckPromoCode(String careGiverToken)
        {
            url = Api.careGiverPromoCode;
            token = SharedPref.getData(getApplicationContext(), SharedPrefConstants.tokenValueKey );
            id = SharedPref.getData(getApplicationContext(), SharedPrefConstants.userId);
            password = SharedPref.getData(getApplicationContext(), SharedPrefConstants.password);
            this.careGiverToken = careGiverToken;
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
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_GIVER_TOKEN, careGiverToken));
            parameter.add(new BasicNameValuePair(ApiConstants.CARE_id, id));
            parameter.add(new BasicNameValuePair(ApiConstants.passwordKey, password));
            parameter.add(new BasicNameValuePair(ApiConstants.token_key, token));

            GetJsonFromServer getJson = new GetJsonFromServer();
            String response = getJson.getJson(url, ApiConstants.post, parameter);
            return response;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();

            try
            {
                JSONObject jObject = new JSONObject(s);
                int success = jObject.getInt(ApiConstants.success);
                if(success == 1)
                {
                    if(dialog != null)
                        dialog.dismiss();
                    Intent intent = new Intent(getApplicationContext(), CompleteCareGiverProfile.class);
                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(getApplicationContext(), Constants.authFailed, Toast.LENGTH_SHORT).show();
                }
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
                Toast.makeText(getApplicationContext(), Constants.authFailed, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
