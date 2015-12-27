package info.fandroid.navdrawer.fragments;

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import info.fandroid.navdrawer.R;
import info.fandroid.navdrawer.util.LocationParams;


public class FragmentMap extends Fragment implements GoogleMap.OnMapLongClickListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {
    MapView mapView;
    GoogleMap map;
    private Double latitude;
    private Double longitude;
    private Circle circle;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        latitude = ((LocationParams)getActivity().getApplication()).getLatitude();
        longitude = ((LocationParams)getActivity().getApplication()).getLongitude();

        // Gets the MapView from the XML layout and creates it
        mapView = (MapView) v.findViewById(R.id.mapview);
        mapView.onCreate(savedInstanceState);

        // Gets to GoogleMap from the MapView and does initialization stuff
        map = mapView.getMap();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
//            return TODO;
        }
        map.setMyLocationEnabled(true);
        map.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .title("Your location")
                .draggable(true)
                .snippet("Home Address"));
        map.getUiSettings().setCompassEnabled(true);
        map.getUiSettings().setMyLocationButtonEnabled(true);
        map.setOnMarkerDragListener(this);
        map.setOnMapLongClickListener(this);
        map.setOnMapClickListener(this);
        setRadius(latitude, longitude);

        // Needs to call MapsInitializer before doing any CameraUpdateFactory calls
        MapsInitializer.initialize(this.getActivity());

        // Updates the location and zoom of the MapView
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10);
        map.animateCamera(cameraUpdate);

        return v;
    }

    private Circle setRadius(Double lat, Double lng) {
        CircleOptions circleOptions = new CircleOptions()
                .center(new LatLng(lat, lng))
                .radius(500)
                .strokeWidth(2)
                .strokeColor(Color.BLUE)
                .fillColor(Color.parseColor("#500084d3"));
        // Supported formats are: #RRGGBB #AARRGGBB
        //   #AA is the alpha, or amount of transparency
        circle = map.addCircle(circleOptions);
        return circle;
    }

    private void removeRadius(Circle circle){
        circle.remove();
    }

    @Override
    public void onResume() {
        mapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onMapClick(LatLng latLng) {
        // TODO Auto-generated method stub
        map.animateCamera(CameraUpdateFactory.newLatLng(latLng));
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // TODO Auto-generated method stub

        //create new marker when user long clicks
//        map.addMarker(new MarkerOptions()
//                .position(latLng)
//                .draggable(true));
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        // TODO Auto-generated method stub
        LatLng dragPosition = marker.getPosition();
        latitude = dragPosition.latitude;
        longitude = dragPosition.longitude;
        ((LocationParams) getActivity().getApplication()).setLatitude(latitude);
        ((LocationParams) getActivity().getApplication()).setLongitude(longitude);
        Log.i("info", "on drag end :" + latitude + " dragLong :" + longitude);
        circle.remove();
        circle = setRadius(latitude, longitude);
    }
}
