package littleextracare.bifortis.com.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;

public class TempMapsActivity extends Fragment {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_temp_maps2, container, false);
        return view;
    }
}
