package androidboys.com.heavensfoodadmin.Fragments;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
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
import androidboys.com.heavensfoodadmin.Models.Address;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import retrofit2.http.Url;

public class UsersLocationFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private Context context;
    public static GoogleMap googleMap;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.map_location_fragment,container,false);
        context=getContext();

        mapView=view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();//needed to get the map to display immediatly

        try {
            MapsInitializer.initialize(context);
        }catch(Exception e){
            e.printStackTrace();
        }

        mapView.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap=googleMap;

        googleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(30.20,78.86)));

        final ArrayList<LatLng> MarkerPoints=new ArrayList<>();
        // Setting onclick event listener for the map
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {

                // Already two locations
                if (MarkerPoints.size() > 1) {
                    MarkerPoints.clear();
                    googleMap.clear();
                }

                // Adding new item to the ArrayList
                MarkerPoints.add(point);

                // Creating MarkerOptions
                MarkerOptions options = new MarkerOptions();

                // Setting the position of the marker
                options.position(point);

                /**
                 * For the start location, the color of marker is GREEN and
                 * for the end location, the color of marker is RED.
                 */
                if (MarkerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                } else if (MarkerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                }


                // Add new marker to the Google Map Android API V2
                googleMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (MarkerPoints.size() == 2) {
                    LatLng origin = MarkerPoints.get(0);
                    LatLng dest = MarkerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = getGoogleMapDirectionUrl(origin.latitude,origin.longitude,dest.latitude,dest.longitude);
                    Log.i("onMapClick", url.toString());
                    FetchJsonDataTask fetchJsonDataTask=new FetchJsonDataTask();

                    // Start downloading json data from Google Directions API
                    fetchJsonDataTask.execute(url);
                    //move map camera
                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
                    googleMap.animateCamera(CameraUpdateFactory.zoomTo(11));
                }

            }
        });
        //showAllUsersInMap();

    }

    private void showAllUsersInMap() {

        Log.i("InsideMap",googleMap.toString());

        FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user=dataSnapshot.getValue(User.class);
                Address address=user.getUserAddress();
                LatLng yourLocation=new LatLng(Double.valueOf(address.getLatitude()),Double.valueOf(address.getLongitude()));
                googleMap.addMarker(new MarkerOptions().position(yourLocation).title(user.getPhoneNumber()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(yourLocation,17.0f));
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

    private String getGoogleMapDirectionUrl(Double adminLatitude,Double adminLongitude,Double userLatitude,Double userLongitude){

        //To get the json data we need to append all these things with goolemapapi site at the end
        String origin="origin="+adminLatitude+","+adminLongitude;
        String destination="destination="+userLatitude+","+userLongitude;
        String sensor="sensor=false";
        String mode="mode=driving";
        String params=origin+"&"+destination+"&"+sensor+"&"+mode;
        String output="json";
        String url="https://maps.googleapis.com/maps/api/directions/"+output+"?"+params;
        return url;
    }

    private String getJsonData(String mapDirectionUrl) throws IOException {
        String data="";
        InputStream inputStream=null;
        HttpURLConnection httpURLConnection=null;

        try{
            URL url=new URL(mapDirectionUrl);
            httpURLConnection= (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream=httpURLConnection.getInputStream();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(inputStream));
            StringBuffer stringBuffer=new StringBuffer();
            String line;

            //extracting the data from bufferedReader and then convert it into string
            while((line=bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
            data=stringBuffer.toString();
            Log.i("JsonData","----"+data);
            bufferedReader.close();
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            inputStream.close();
            httpURLConnection.disconnect();
        }
        return data;
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
