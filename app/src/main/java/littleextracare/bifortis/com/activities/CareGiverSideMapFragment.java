package littleextracare.bifortis.com.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;


public class CareGiverSideMapFragment extends Fragment {

    MapView mapView;
    GoogleMap googleMap;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_track_on_map, container, false);
        mapView = (MapView) view.findViewById(R.id.trackMap);

        mapView.onCreate(savedInstanceState);

        setUpIfNeed();

        return super.onCreateView(inflater, container, savedInstanceState);
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
}
