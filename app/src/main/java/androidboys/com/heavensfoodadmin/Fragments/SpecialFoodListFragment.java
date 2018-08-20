package androidboys.com.heavensfoodadmin.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Adapters.SpecialFoodListArrayAdapter;
import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class SpecialFoodListFragment extends Fragment {
    private ListView listView;
    private SpecialFoodListArrayAdapter specialFoodListArrayAdapter;
    private ArrayList<String> specialFoodArrayList=new ArrayList<>();
    private ArrayList<Long> countUser=new ArrayList<>();
    private ArrayList<String> userUid=new ArrayList<String>();
    private ArrayList<String> quantity=new ArrayList<String>();



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.special_order_list_fragment,container,false);
        listView=view.findViewById(R.id.specialFoodList);
        specialFoodListArrayAdapter=new SpecialFoodListArrayAdapter(getContext(),specialFoodArrayList,countUser);
        listView.setAdapter(specialFoodListArrayAdapter);

        fetchFoodListFromFirebase();

        return view;
    }

    private void fetchFoodListFromFirebase() {
        FirebaseDatabase.getInstance().getReference("SpecialOrder").child("NewOrders").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                for(DataSnapshot finalDataSnapshot : dataSnapshot.getChildren()){
                    Log.i("uid",finalDataSnapshot.getKey());
                }


                Log.i("values: ",dataSnapshot.getKey());
                Log.i("count",""+dataSnapshot.getChildrenCount());

                specialFoodArrayList.add(dataSnapshot.getKey());
                countUser.add(dataSnapshot.getChildrenCount());
                specialFoodListArrayAdapter.notifyDataSetChanged();


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

    public static SpecialFoodListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        SpecialFoodListFragment fragment = new SpecialFoodListFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
