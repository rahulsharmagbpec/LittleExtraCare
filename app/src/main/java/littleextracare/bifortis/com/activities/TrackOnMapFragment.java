package littleextracare.bifortis.com.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import littleextracare.bifortis.com.Constants.ApiConstants;
import littleextracare.bifortis.com.Constants.Constants;
import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.WebApi.Api;
import littleextracare.bifortis.com.data.GetJsonFromServer;
import littleextracare.bifortis.com.data.LocationProvider;
import littleextracare.bifortis.com.data.Logger;
import littleextracare.bifortis.com.data.SharedPref;


/**
 * A placeholder fragment containing a simple view.
 */
@SuppressWarnings("deprecation")
public class TrackOnMapFragment extends Fragment implements LocationProvider.LocationCallback {

    private MapView mapView;
    private GoogleMap googleMap;
    private Marker marker;
    private LocationProvider mLocationProvider;
    private final String TAG = TrackOnMapFragment.this.getClass().getName();
    private Timer myTimer;
    private Location myLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_track_on_map, container, false);
        mLocationProvider = new LocationProvider(getActivity(), this);
        mapView = (MapView) view.findViewById(R.id.trackMap);

        Button cancelButton = (Button) view.findViewById(R.id.btnCancel);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mapView.onCreate(savedInstanceState);

        setUpIfNeed();

        return view;
    }

    @Override
    public void onResume() {
        mapView.onResume();
        try {

            mLocationProvider.connect();

        } catch (Exception e) {
            e.printStackTrace();
        }

        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        mLocationProvider.disconnect();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();

        mapView.onLowMemory();
    }

    @Override
    public void onPause() {
        super.onPause();
        if(myTimer != null)
            myTimer.cancel();
    }

    private void setUpIfNeed() {

        if (mapView != null) {
            googleMap = mapView.getMap();
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setZoomControlsEnabled(true);
        }

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try
        {
            MapsInitializer.initialize(this.getActivity());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Button Listener for "CANCEL" button
        Button btnCancel = (Button) getView().findViewById(R.id.btnCancel);
        if(btnCancel != null)
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //SharedPref.setData(getActivity(), Constants.REQUESTING, "false");
                    //Intent intent = new Intent(getActivity(), MainActivity.class);
                    //startActivity(intent);
                    Intent intent = new Intent(getActivity(), TimerActivity.class);
                    startActivity(intent);
                }
            });

        // Button Listener for "Send SMS" Button
        ImageButton btnMessage = (ImageButton) getView().findViewById(R.id.button8);
        if(btnMessage != null)
            btnMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendSMS();
                }
            });

        // Button Listener for "Call" Button
        ImageButton btnCall = (ImageButton) getView().findViewById(R.id.button9);
        if(btnCall != null)
            btnCall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call();
                }
            });
    }

    protected void sendSMS() {
        Log.i("Send SMS", "");

        String phone = SharedPref.getData(getActivity(), SharedPrefConstants.PREF_PHONE);

        Intent smsIntent = new Intent(Intent.ACTION_VIEW);

        smsIntent.setData(Uri.parse("smsto:"));
        smsIntent.setType("vnd.android-dir/mms-sms");
        smsIntent.putExtra("address"  , phone);
        smsIntent.putExtra("sms_body"  , Constants.messageToCareGiver);

        try {
            startActivity(smsIntent);
            //finish();
            Log.i("Finished sending SMS...", "");
        }
        catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getActivity(),
                    "SMS faild, please try again later.", Toast.LENGTH_SHORT).show();
        }
    }

    private void call()
    {
        String phone = SharedPref.getData(getActivity(), SharedPrefConstants.PREF_PHONE);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:"+phone));
        startActivity(callIntent);
    }

    private void showAlert(String title, String btnName) {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();


        //LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        alertDialog.setMessage("Alert about Charging $5 for cancellation");
        alertDialog.setButton(btnName, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra("position", 0);
                startActivity(intent);
            }
        });
        alertDialog.show();
    }

    @Override
    public void handleNewLocation(Location location) {
        //updateUI(location);
        myLocation = location;

        Logger.log(TAG, "Update Ui in MapFragment");
        String token = SharedPref.getData(getActivity(), SharedPrefConstants.tokenValueKey);
        String id = SharedPref.getData(getActivity(), SharedPrefConstants.userId);
        String password = SharedPref.getData(getActivity(), SharedPrefConstants.password);
        String careGiverId = "55";

        final List<NameValuePair> parameter = new ArrayList<>();
        parameter.add(new BasicNameValuePair(ApiConstants.token_key, token));
        parameter.add(new BasicNameValuePair(ApiConstants.API_ID, id));
        parameter.add(new BasicNameValuePair(ApiConstants.passwordKey, password));
        parameter.add(new BasicNameValuePair(ApiConstants.API_CARE_GIVER_ID, careGiverId));

        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                // If you want to modify a view in your Activity
                getActivity().runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    GetNearestPointAsync async = new GetNearestPointAsync(
                                                            Api.liveLocation, ApiConstants.post, parameter);
                                                    async.execute();
                                                }
                                            }
                );
            }
        }, Constants.initialDelay, Constants.interval);

        GetNearestPointAsync async = new GetNearestPointAsync(Api.liveLocation, ApiConstants.post, parameter);
    }

    private void updateUI(Location location) {
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Current Location");
        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        marker = googleMap.addMarker(markerOptions);



        googleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(new LatLng(location.getLatitude() - 0.001, location.getLongitude() - 0.001)));

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15.5f);
        googleMap.animateCamera(cameraUpdate);
    }

    void afterAsync(Double lat, Double lon, String careGiverId)
    {
        if(googleMap == null)
            setUpIfNeed();
        if(googleMap != null)
        {
            addMarker(new LatLng(lat, lon), careGiverId);
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
            builder.include(new LatLng(lat, lon));
            LatLngBounds bounds = builder.build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, Constants.mapMarkerPadding));
        }
    }

    private void addMarker(LatLng latlng, String distance)
    {
        MarkerOptions mOption = new MarkerOptions();
        mOption.position(latlng);
        mOption.title(distance);
        mOption.rotation(30);
        mOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.heart));
        googleMap.addMarker(mOption);
    }

    class GetNearestPointAsync extends AsyncTask<String, Void, String> {
        String liveLocationUrl;
        String post;
        List<NameValuePair> parameter;
        ProgressDialog progressDialog;

        public GetNearestPointAsync(String liveLocationUrl, String post, List<NameValuePair> parameter)
        {
            this.liveLocationUrl = liveLocationUrl;
            this.post = post;
            this.parameter = parameter;

            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle(Constants.progressText);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {
            GetJsonFromServer jsonParser = new GetJsonFromServer();

            String jsonResponse = jsonParser.getJson(liveLocationUrl, post, parameter);
            Log.e(TAG, jsonResponse);
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            Double lat;
            Double lon;
            String careGiverId;
            try {
                JSONObject jObject = new JSONObject(s);
                lat = jObject.getDouble(ApiConstants.API_LATITUDE);
                lon = jObject.getDouble(ApiConstants.API_LONGITUDE);
                careGiverId = jObject.getString(ApiConstants.userIdKey); // TODO : need to change after server update
                afterAsync(lat, lon, careGiverId);
            }
            catch(Exception e)
            {
                Log.e(TAG, e.toString());
            }


        }
    }
}