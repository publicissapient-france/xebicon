package fr.xebia.xebicon.ui.map;

import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import butterknife.ButterKnife;

public class MapFragment extends com.google.android.gms.maps.SupportMapFragment implements OnMapReadyCallback {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getMapAsync(this);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ButterKnife.inject(this, view);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        LatLng eurositesCamera = new LatLng(48.8678814, 2.3017411);
        CameraUpdate camera = CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder().target(eurositesCamera).zoom(19f).tilt(0f).build());
        map.animateCamera(camera);
    }
}
