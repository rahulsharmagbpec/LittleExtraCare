package littleextracare.bifortis.com.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import littleextracare.bifortis.com.Constants.ApiConstants;
import littleextracare.bifortis.com.Constants.Constants;
import littleextracare.bifortis.com.Constants.SharedPrefConstants;
import littleextracare.bifortis.com.OnSegmentSelectedListener;
import littleextracare.bifortis.com.SliderSelector;
import littleextracare.bifortis.com.WebApi.Api;
import littleextracare.bifortis.com.data.GetJsonFromServer;
import littleextracare.bifortis.com.data.LocationProvider;
import littleextracare.bifortis.com.data.SharedPref;

public class MapsActivity extends Fragment implements LocationProvider.LocationCallback, GoogleMap.OnMarkerClickListener {

    private boolean  checkTime = true;

    private SliderSelector mSliderSelector;
    //private TextView mHelloTextView;

    private MapView mapView;
    private MapView trackMapView;
    private GoogleMap googleMap;
    private GoogleMap trackGoogleMap;
    private Marker marker;
    private LocationProvider mLocationProvider;
    private final String TAG = MapsActivity.class.getName();
    private DatePicker fromdatePicker;
    private TimePicker fromtimePicker;
    private DatePicker todatePicker;
    private TimePicker totimePicker;
    private Calendar fromCalendar;
    private Calendar toCalendar;
    private Location location;
    private Activity mActivity;
    private static int noOfChildrens = 1;

    Boolean confirmRequest;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view;

        view = inflater.inflate(R.layout.activity_maps, container, false);
        mLocationProvider = new LocationProvider(getActivity(), this);
        mapView = (MapView) view.findViewById(R.id.map);

        mapView.onCreate(savedInstanceState);

        setUpIfNeed();

        ArrayList<View> mViews = new ArrayList<>();
        mSliderSelector = (SliderSelector) view.findViewById(R.id.slider_selector);
        //mHelloTextView = (TextView) view.findViewById(R.id.hello_textview);

        TextView mTextView1 = new TextView(getActivity());
        mTextView1.setText(getResources().getString(R.string.child1));
        mViews.add(mTextView1);

        TextView mTextView2 = new TextView(getActivity());
        mTextView2.setText(getResources().getString(R.string.child2));
        mViews.add(mTextView2);

        TextView mTextView3 = new TextView(getActivity());
        mTextView3.setText(getResources().getString(R.string.child3));
        mViews.add(mTextView3);

        //mHelloTextView.setText("Hello New York!");

        mSliderSelector.setSegmentViews(mViews);
        mSliderSelector.setSegmentSelectedListener(new OnSegmentSelectedListener() {
            @Override
            public void onSegmentSelected(int segmentIndex) {
                switch (segmentIndex) {
                    case 0:
                        noOfChildrens = 1;
                        Log.e(TAG, getResources().getString(R.string.child1));
                        break;
                    case 1:
                        Log.e(TAG, getResources().getString(R.string.child2));
                        noOfChildrens = 2;
                        break;
                    case 2:
                        Log.e(TAG, getResources().getString(R.string.child3));
                        noOfChildrens = 3;
                        break;
                }
            }
        });
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
    }

    @Override
    public void onResume() {
        if (mapView != null) {
            mapView.onResume();
            try {
                mLocationProvider.connect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocationProvider != null)
            mLocationProvider.disconnect();
        if (mapView != null)
            mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }


    private void setUpIfNeed() {

        if (mapView != null) {
            googleMap = mapView.getMap();

            googleMap.getUiSettings().setZoomControlsEnabled(false);

            googleMap.setMyLocationEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        }

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setUpTrackerMap() {
        if (trackMapView != null) {
            trackGoogleMap = trackMapView.getMap();
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        /*Button btnRequest = (Button) getView().findViewById(R.id.btnRequest);
        if (btnRequest != null)
            btnRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAlert("Select Options", "Request", R.layout.custom_alert_radio_buttons);
                }
            });*/

        Button book = (Button) getView().findViewById(R.id.btnBook);
        book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert(getResources().getString(R.string.selectAddress),
                        getResources().getString(R.string.button_next), R.layout.book_later_dialog);
            }
        });

        Button request = (Button) getView().findViewById(R.id.btnRequest);
        request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (location != null) {
                    getNearestCareGiver nearestCareGiver = new getNearestCareGiver();
                    nearestCareGiver.execute();
                } else {
                    Toast.makeText(getActivity(), "Waiting for Location", Toast.LENGTH_SHORT).show();
                }
                //ProgressDialog myDialog = new ProgressDialog(getActivity());
                //myDialog.setTitle(getResources().getString(R.string.requesting_caregiver));
                //myDialog.setMessage("Loading...");
                //myDialog.setCancelable(false);
                //myDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                //    @Override
                //    public void onClick(DialogInterface dialog, int which) {

                //        Intent intent = new Intent(getActivity(), CareGiverProfileActivity.class);
                //        startActivity(intent);
                //    }
                //});
                //myDialog.show();
            }
        });

    }


    private TextView fromTextView;
    private TextView toTextView;
    Date fromDate;
    Date toDate;

    private void showAlert(String title, String btnName, int layoutId) {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(title);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(layoutId, null);

        alertDialog.setView(view);

        Button buttonDialogNext = (Button) view.findViewById(R.id.buttonDialogNext);
        buttonDialogNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromCalendar != null && toCalendar != null) {
                    LocalDate fromLocalDate = new LocalDate(fromCalendar.getTimeInMillis());
                    LocalDate toLocalDate = new LocalDate(toCalendar.getTimeInMillis());
                    int days = Days.daysBetween(fromLocalDate, toLocalDate).getDays();

                    Log.e(TAG, "Difference in Days " + days);
                    if (days >= 0 && days < Constants.maxDays) {
                        Intent intent = new Intent(getActivity(), ProfileActivity.class);
                        startActivity(intent);
                        alertDialog.dismiss();
                    } else if (days < Constants.maxDays) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(R.string.timeDialogError), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity().getApplicationContext(),
                                getResources().getString(R.string.minTimeDialogError), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),
                            getResources().getString(R.string.dialogNullError), Toast.LENGTH_SHORT).show();
                }

            }
        });

        fromTextView = (TextView) view.findViewById(R.id.textViewFrom);
        fromTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDateTimePickerDialog();
            }
        });

        toTextView = (TextView) view.findViewById(R.id.textViewTo);
        toTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDateTimePickerDialog();
            }
        });
        alertDialog.show();
    }

    private void fromDateTimePickerDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fromdatePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                fromtimePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                fromCalendar = new GregorianCalendar(fromdatePicker.getYear(),
                        fromdatePicker.getMonth(),
                        fromdatePicker.getDayOfMonth(),
                        fromtimePicker.getCurrentHour(),
                        fromtimePicker.getCurrentMinute());
                String date = fromdatePicker.getDayOfMonth() + "-" + fromdatePicker.getMonth() + "-"
                        + fromdatePicker.getYear();
                String time = fromtimePicker.getCurrentHour() + ":" + fromtimePicker.getCurrentMinute();

                fromTextView.setText(date + " " + time);
                Toast.makeText(getActivity().getApplicationContext(), fromCalendar.getTimeInMillis() + "",
                        Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    private void toDateTimePickerDialog() {
        final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                todatePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                totimePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                toCalendar = new GregorianCalendar(todatePicker.getYear(),
                        todatePicker.getMonth(),
                        todatePicker.getDayOfMonth(),
                        totimePicker.getCurrentHour(),
                        totimePicker.getCurrentMinute());
                String date = todatePicker.getDayOfMonth() + "-" + todatePicker.getMonth() + "-"
                        + todatePicker.getYear();
                String time = totimePicker.getCurrentHour() + ":" + totimePicker.getCurrentMinute();

                toTextView.setText(date + " " + time);
                Toast.makeText(getActivity().getApplicationContext(), toCalendar.getTimeInMillis() + "",
                        Toast.LENGTH_SHORT).show();
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    @Override
    public void handleNewLocation(Location location) {
        this.location = location;
        //updateUI(location);
        if (location != null) {
            getNearestCareGiver(location);
        }
    }

    /*private void updateUI(Location location) {
        if (marker != null) {
            marker.remove();
        }
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(location.getLatitude(), location.getLongitude()))
                .title("Current Location");
        //.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));

        marker = googleMap.addMarker(markerOptions);

        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(),
                location.getLongitude()), 15.5f);
        googleMap.animateCamera(cameraUpdate);
    }*/

    private void getNearestCareGiver(Location location) {
        GetNearCareGiversLocation getPointAsync = new GetNearCareGiversLocation(location);
        getPointAsync.execute();
    }

    // function to run after getting data from server
    private void afterAsyncTask(String result) {
        if (googleMap == null) {
            setUpIfNeed();
        }
        googleMap.clear();
        try {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
            JSONArray jsonArray = new JSONArray(result);
            int length = jsonArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject jObject = new JSONObject(jsonArray.getString(i));
                Double lat = jObject.getDouble(ApiConstants.API_LATITUDE);
                Double lon = jObject.getDouble(ApiConstants.API_LONGITUDE);
                int id = jObject.getInt(ApiConstants.API_ID);
                String distance = jObject.getString(ApiConstants.API_DISTANCE);

                builder.include(new LatLng(lat, lon));
                addMarker(new LatLng(lat, lon), distance, id);
            }
            LatLngBounds bounds = builder.build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, Constants.mapMarkerPadding));
        } catch (Exception e) {
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(location.getLatitude(), location.getLongitude()));
            LatLngBounds bounds = builder.build();
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, Constants.mapMarkerPadding));

            Log.e(TAG, e.toString());
        }
    }

    // function to add marker to map
    private void addMarker(LatLng latlng, String distance, int id) {
        MarkerOptions mOption = new MarkerOptions();
        mOption.position(latlng);
        mOption.title(id + ": " + distance);
        mOption.rotation(30);
        mOption.icon(BitmapDescriptorFactory.fromResource(R.drawable.heart));
        googleMap.addMarker(mOption);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast.makeText(getActivity(), "marker Clicked", Toast.LENGTH_SHORT).show();
        return false;
    }

    private void nearestLocationOfCareGiverReceived() {
        BookCareGiver bookCareGiver = new BookCareGiver();
        bookCareGiver.execute();
    }

    // get all near care givers locations
    class GetNearCareGiversLocation extends AsyncTask<String, Void, String> {
        String latitude;
        String longitude;
        String token;
        String id;
        String password;
        String url;
        ProgressDialog progressDialog;

        GetNearCareGiversLocation(Location location) {
            latitude = location.getLatitude() + "";
            longitude = location.getLongitude() + "";
            token = SharedPref.getData(mActivity, SharedPrefConstants.tokenValueKey);
            id = SharedPref.getData(mActivity, SharedPrefConstants.userId);
            password = SharedPref.getData(mActivity, SharedPrefConstants.password);
            url = Api.location;

            progressDialog = new ProgressDialog(mActivity);
            progressDialog.setCancelable(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setTitle(Constants.progressText);
            progressDialog.show();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(String... params) {
            if (latitude != null && longitude != null && token != null && id != null && password != null) {
                List<NameValuePair> parameter = new ArrayList<>();
                parameter.add(new BasicNameValuePair(ApiConstants.token_key, token));
                parameter.add(new BasicNameValuePair(ApiConstants.CONS_LATITUDE, latitude));
                parameter.add(new BasicNameValuePair(ApiConstants.CONS_LONGITUDE, longitude));
                parameter.add(new BasicNameValuePair(ApiConstants.CONS_ID, id));
                parameter.add(new BasicNameValuePair(ApiConstants.passwordKey, password));

                GetJsonFromServer jsonParser = new GetJsonFromServer();
                String jsonResponse = jsonParser.getJson(url, "POST", parameter);
                return jsonResponse;
            } else {
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s != null) {
                afterAsyncTask(s);
            } else {
                Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Get Nearest Care Giver location and id to Book The careGiver
    @SuppressWarnings("deprecation")
    class getNearestCareGiver extends AsyncTask<String, Void, String> {
        String url;
        List<NameValuePair> parameter;
        Double latitude;
        Double longitude;
        String token;
        String id;
        String password;
        ProgressDialog progressDialog;

        getNearestCareGiver() {
            url = Api.nearestCareGiver;
            latitude = location.getLatitude();
            Log.e(TAG, "latitude: " + latitude);
            longitude = location.getLongitude();
            Log.e(TAG, "longitude: " + longitude);
            token = SharedPref.getData(getActivity(), SharedPrefConstants.tokenValueKey);
            Log.e(TAG, "token: " + token);
            id = SharedPref.getData(getActivity(), SharedPrefConstants.userId);
            Log.e(TAG, "id: " + id);
            password = SharedPref.getData(getActivity(), SharedPrefConstants.password);
            Log.e(TAG, "password: " + password);

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
            parameter = new ArrayList<>();
            parameter.add(new BasicNameValuePair(ApiConstants.token_key, token));
            parameter.add(new BasicNameValuePair(ApiConstants.API_LATITUDE, latitude + ""));
            parameter.add(new BasicNameValuePair(ApiConstants.API_LONGITUDE, longitude + ""));
            parameter.add(new BasicNameValuePair(ApiConstants.API_ID, id));
            parameter.add(new BasicNameValuePair(ApiConstants.passwordKey, password));

            GetJsonFromServer jsonParser = new GetJsonFromServer();
            String jsonResponse = jsonParser.getJson(url, ApiConstants.post, parameter);
            if(jsonResponse != null)
                Log.e(TAG, url+"===================="+jsonResponse);
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                progressDialog.dismiss();
                Double cRating = null;
                String cDesc = null;

                JSONObject jObject = new JSONObject(s);
                String cId = jObject.getString(ApiConstants.CARE_id);
                Double cLat = jObject.getDouble(ApiConstants.CARE_LATITUDE);
                Double cLon = jObject.getDouble(ApiConstants.CARE_LONGITUDE);
                String cDist = jObject.getString(ApiConstants.CARE_DISTANCE);
                String cName = jObject.getString(ApiConstants.CARE_NAME);
                if (!jObject.isNull(ApiConstants.CARE_ratings)) {
                    cRating = jObject.getDouble(ApiConstants.CARE_ratings);
                }
                if (!jObject.isNull(ApiConstants.CARE_DESC)) {
                    cDesc = jObject.getString(ApiConstants.CARE_DESC);
                }
                // TODO: Need to fix this later acc. to API
                //JSONObject iconObject = jObject.getJSONObject(ApiConstants.CARE_ICONS);
                //boolean cHeart = iconObject.getBoolean(ApiConstants.CARE_HEART);
                //boolean cEdu = iconObject.getBoolean(ApiConstants.CARE_EDU);
                //boolean cFinger = iconObject.getBoolean(ApiConstants.CARE_FINGER);
                //boolean cCar = iconObject.getBoolean(ApiConstants.CARE_CAR);
                //boolean cDog = iconObject.getBoolean(ApiConstants.CARE_DOG);

                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_id, cId);
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_LATITUDE, cLat + "");
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_LONGITUDE, cLon + "");
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_DISTANCE, cDist);
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_NAME, cName);
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_ratings, cRating + "");
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_DESC, cDesc);
                // TODO:Dummy data for testing
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_HEART, "true");
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_EDU, "false");
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_FINGER, "true");
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_CAR, "false");
                SharedPref.setData(getActivity(), SharedPrefConstants.PREF_DOG, "true");

                //SharedPref.setData(getActivity(), SharedPrefConstants.PREF_HEART, cHeart+"");
                //SharedPref.setData(getActivity(), SharedPrefConstants.PREF_EDU, cEdu+"");
                //SharedPref.setData(getActivity(), SharedPrefConstants.PREF_FINGER, cFinger+"");
                //SharedPref.setData(getActivity(), SharedPrefConstants.PREF_CAR, cCar+"");
                //SharedPref.setData(getActivity(), SharedPrefConstants.PREF_DOG, cDog+"");

                nearestLocationOfCareGiverReceived();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    // Book the nearest CareGiver
    @SuppressWarnings("deprecation")
    class BookCareGiver extends AsyncTask<String, Void, String> {
        String token;
        String url;
        String latitude;
        String longitude;
        String id;
        String password;
        String careGiverId;
        String childrens;
        List<NameValuePair> parameter;
        ProgressDialog progressDialog;

        BookCareGiver() {
            url = Api.bookRequest;
            token = SharedPref.getData(getActivity(), SharedPrefConstants.tokenValueKey);
            latitude = location.getLatitude() + "";
            longitude = location.getLongitude() + "";
            id = SharedPref.getData(getActivity(), SharedPrefConstants.userId);
            password = SharedPref.getData(getActivity(), SharedPrefConstants.password);
            careGiverId = SharedPref.getData(getActivity(), SharedPrefConstants.PREF_id);
            Log.e(TAG, "careGiver ID: " + careGiverId);
            childrens = noOfChildrens + "";

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

            parameter = new ArrayList<>();
            parameter.add(new BasicNameValuePair(ApiConstants.token_key, token));
            parameter.add(new BasicNameValuePair(ApiConstants.API_LATITUDE, latitude + ""));
            parameter.add(new BasicNameValuePair(ApiConstants.API_LONGITUDE, longitude + ""));
            parameter.add(new BasicNameValuePair(ApiConstants.API_ID, id));
            parameter.add(new BasicNameValuePair(ApiConstants.passwordKey, password));
            parameter.add(new BasicNameValuePair(ApiConstants.API_CARE_GIVER_ID, careGiverId));
            parameter.add(new BasicNameValuePair(ApiConstants.API_CHILDREN_NUMBER, childrens));

            GetJsonFromServer jsonParser = new GetJsonFromServer();
            String jsonResponse = jsonParser.getJson(url, ApiConstants.post, parameter);
            if(jsonResponse != null) {
                //Toast.makeText(getActivity(), jsonResponse, Toast.LENGTH_LONG).show();
                Log.e(TAG, url + "========================" + jsonResponse);
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            try {
                JSONObject jObject = new JSONObject(s);
                if (jObject.getInt(ApiConstants.success) == 1) {
                    //Intent intent = new Intent(getActivity(), CareGiverProfileActivity.class);
                    //startActivity(intent);
                    CheckBookingStatus checkBookingStatus = new CheckBookingStatus();
                    checkBookingStatus.execute();
                } else
                    Toast.makeText(getActivity(), "Some Error Occured", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                Toast.makeText(getActivity(), "Authentication Failed", Toast.LENGTH_SHORT).show();
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
    class CheckBookingStatus extends AsyncTask<String, Void, String> {

        ProgressDialog progressDialog;
        String url;
        List<NameValuePair> parameter;
        String careGiverId;
        String id;
        String password;
        String token;

        CheckBookingStatus()
        {
            url = Api.bookingStatus;
            careGiverId = SharedPref.getData(getActivity(), SharedPrefConstants.PREF_id);
            id = SharedPref.getData(getActivity(), SharedPrefConstants.userId);
            password = SharedPref.getData(getActivity(), SharedPrefConstants.password);
            token = SharedPref.getData(getActivity(), SharedPrefConstants.tokenValueKey);

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
            boolean check = true;
            String jsonResponse;

                parameter = new ArrayList<>();
                parameter.add(new BasicNameValuePair(ApiConstants.token_key, token));
                parameter.add(new BasicNameValuePair(ApiConstants.API_ID, id));
                parameter.add(new BasicNameValuePair(ApiConstants.passwordKey, password));
                parameter.add(new BasicNameValuePair(ApiConstants.API_CARE_GIVER_ID, careGiverId));

            Timer myTimer = new Timer("MyTimer", true);
            myTimer.scheduleAtFixedRate(new MyTask(), Constants.waitTime, 1000);

            do {
                GetJsonFromServer jsonParser = new GetJsonFromServer();
                jsonResponse = jsonParser.getJson(url, ApiConstants.post, parameter);
                try{
                    Log.e(TAG, url +" ==================== "+jsonResponse+"");
                    JSONObject jObject = new JSONObject(jsonResponse);
                    String res = jObject.getString(ApiConstants.bookingStatus);
                    if(res.equalsIgnoreCase(ApiConstants.REJECTED) || res.equalsIgnoreCase(ApiConstants.ACCEPTED))
                    {
                        check = false;
                    }
                }
                catch (Exception e)
                {
                    Log.e(TAG, e.toString());
                }
            }
            while (check && checkTime);
            myTimer.cancel();
            myTimer.purge();

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            try
            {
                JSONObject jObject = new JSONObject(s);
                String result = jObject.getString(ApiConstants.bookingStatus);
                if(result.equalsIgnoreCase(ApiConstants.ACCEPTED))
                {
                    Intent intent = new Intent(getActivity(), CareGiverProfileActivity.class);
                    startActivity(intent);
                }
                else if(result.equalsIgnoreCase(ApiConstants.REJECTED))
                {
                    Toast.makeText(getActivity(), "Rejected", Toast.LENGTH_SHORT).show();
                }
            }
            catch (Exception e)
            {
                Log.e(TAG, e.toString());
            }
        }
    }
}