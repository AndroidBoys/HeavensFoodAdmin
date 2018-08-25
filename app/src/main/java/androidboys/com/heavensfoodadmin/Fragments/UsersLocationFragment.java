package androidboys.com.heavensfoodadmin.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.AsynckTasks.FetchJsonDataTask;
import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Models.Address;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import retrofit2.http.Url;

public class UsersLocationFragment extends Fragment implements OnMapReadyCallback{

    private MapView mapView;
    private Context context;
    public static GoogleMap googleMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String locationProvider;
    private Location userLocation;
    private double latitude;
    private double longitude;
    private boolean isNetworkEnabled;
    private boolean isGpsEnabled;
    private ArrayList<Marker> markerArrayList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.map_location_fragment, container, false);

        Log.i("Inside","Location fragment");
        context = getContext();
        markerArrayList=new ArrayList<>();


        mapView = view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();//needed to get the map to display immediatly

        try {
            MapsInitializer.initialize(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap map) {
        googleMap = map;


        Log.i("MapReady","-------------------------------inside it");
        if(!hasPermission()){
            getPermission();
        }else{
            Log.i("Inside","getuserlocation");
            getUserLocation();
        }

        googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                for(int i=0;i<markerArrayList.size();i++){
                    if(marker.getTag().equals(markerArrayList.get(i).getTag())){
                        marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
                    }
                }
                return false;
            }
        });

    }


    private void getPermission() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, Common.LOCATION_PERMISSION_REQUEST_CODE);
    }

    private boolean hasPermission() {

        if (Build.VERSION.SDK_INT > 21) {
            return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Common.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!hasPermission()) {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, Common.LOCATION_PERMISSION_REQUEST_CODE);
                }else{
                    getUserLocation();
                }
            }
        }
    }


    private void getUserLocation() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        //locationProvider = locationManager.getBestProvider(new Criteria(), false);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location!=null) {
                    longitude=location.getLongitude();
                    latitude=location.getLatitude();
//                    showAdminLocation();

                    //To keep track of admin we need to uncomment below line
                    //settingAllLocationOnMap();
                }
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };

        //This below method is used to get the location at first time after that onLocation Changed will do its work;

        Log.i("inside","------------------------somethoing");
        againRequestLocation();
    }

    private void showAdminLocation() {

        Log.i("latitude",String.valueOf(latitude));
        Log.i("longitude",String.valueOf(longitude));
        MarkerOptions markerOptions=new MarkerOptions();
        LatLng adminLatLng=new LatLng(latitude,longitude);
        markerOptions.position(adminLatLng);
        markerOptions.title("Your Location");
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        Marker adminMarker=googleMap.addMarker(markerOptions);
        adminMarker.setTag("admin");
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(adminLatLng));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }


    private void settingAllLocationOnMap() {

        googleMap.clear();
        //admin location marker on googlemap
        showAdminLocation();
        showAllUsersInMap();
    }

    private void againRequestLocation() {
        if (ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(context,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED) {


            //since network provider gives you the accurate result so i used here network provider.
            isNetworkEnabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            isGpsEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if(isNetworkEnabled) {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
                if (locationManager != null) {
                    userLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (userLocation != null) {
                        longitude = userLocation.getLongitude();
                        latitude = userLocation.getLatitude();

                        //Initially i need to set all the users as well as admin location
                        settingAllLocationOnMap();

                        Log.i("Network latitude",String.valueOf(latitude));
                        Log.i("Network longitude",String.valueOf(longitude));
                    }
                    //googleMap.setMyLocationEnabled(true);
                }
            }else if(isGpsEnabled){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                if (locationManager != null) {
                    userLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (userLocation != null) {

                        longitude = userLocation.getLongitude();
                        latitude = userLocation.getLatitude();
                        //Initially i need to set all the users as well as admin location
                        settingAllLocationOnMap();
                    }
                    //googleMap.setMyLocationEnabled(true);
                }
            }else{
                Toast.makeText(context,"Please enable gps from setting or open your internet connection",Toast.LENGTH_SHORT).show();
            }
        }

    }


    private void showAllUsersInMap() {

        Log.i("InsideMap", googleMap.toString());

        FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user = dataSnapshot.getValue(User.class);
                Address address = user.getUserAddress();
                if(address!=null) {
                    double userLatitude=Double.valueOf(address.getLatitude());
                    double userLongitude=Double.valueOf(address.getLongitude());
                    LatLng userLocation = new LatLng(userLatitude,userLongitude);

                    Log.i("userLocation",userLocation.toString());
                    MarkerOptions markerOptions=new MarkerOptions();
                    markerOptions.position(userLocation);
                    markerOptions.title(user.getPhoneNumber());
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    Marker marker=googleMap.addMarker(markerOptions);
                    marker.setTag(user.getPhoneNumber());
                    markerArrayList.add(marker);

                    //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 17.0f));

                    //The below will  get the google map api url
                    String url=getGoogleMapDirectionUrl(latitude,longitude,userLatitude,userLongitude);
                    FetchJsonDataTask fetchJsonDataTask=new FetchJsonDataTask();
                    fetchJsonDataTask.execute(url); //fetch json data from that url
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getGoogleMapDirectionUrl(Double adminLatitude, Double adminLongitude, Double userLatitude, Double userLongitude) {

        //To get the json data we need to append all these things with goolemapapi site at the end
        String origin = "origin=" + adminLatitude + "," + adminLongitude;
        String destination = "destination=" + userLatitude + "," + userLongitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String params = origin + "&" + destination + "&" + sensor + "&" + mode;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + params;
        return url;
    }

    public static UsersLocationFragment newInstance() {

        Bundle args = new Bundle();
        UsersLocationFragment fragment = new UsersLocationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
        //againRequestLocation();
    }



    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
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


}
