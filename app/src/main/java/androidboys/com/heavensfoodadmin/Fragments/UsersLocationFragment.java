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
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UsersLocationFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;
    private Context context;
    private GoogleMap googleMap;
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
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap=googleMap;
        showAllUsersInMap();

    }

    private void showAllUsersInMap() {

        Log.i("InsideMap",googleMap.toString());
        String latitude="30.221399";
        String longitude=" 78.780045";
        LatLng yourLocation =null;
        for(int i=0;i<5;i++) {
            yourLocation = new LatLng(Double.valueOf(latitude)+i*2, Double.valueOf(longitude)+i);
            googleMap.addMarker(new MarkerOptions().position(yourLocation).title("User location"));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(yourLocation,16.0f));
        }
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));

//        FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//                User user=dataSnapshot.getValue(User.class);
//
//            }
//
//            @Override
//            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//            }
//
//            @Override
//            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
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
