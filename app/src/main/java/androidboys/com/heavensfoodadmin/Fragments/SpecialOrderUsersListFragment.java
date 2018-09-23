package androidboys.com.heavensfoodadmin.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.baoyz.widget.PullRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Activities.HomeActivity;
import androidboys.com.heavensfoodadmin.Adapters.SpecialOrderUserListCustomAdapter;
import androidboys.com.heavensfoodadmin.Models.Address;
import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Utils.ProgressUtils;
import androidboys.com.heavensfoodadmin.ViewHolders.SpecialFoodUsersViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpecialOrderUsersListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView listView;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private DatabaseReference specialFoodReference;
    private ArrayList<String> usersUidArrayList;
    private ArrayList<SpecialFood> expectedArrayList;
    private ArrayList<String> specialFoodArrayList;
    private ArrayList<String> countTotalFoodList;
    private SpecialOrderUserListCustomAdapter specialOrderUserListCustomAdapter;
    private FirebaseRecyclerAdapter<SpecialFood, SpecialFoodUsersViewHolder> firebaseSpecialUserAdapter;
    private RecyclerView nestedRecyclerView;
//    private boolean isCompleted;
    private DatabaseReference databaseReference;
    private FirebaseDatabase firebaseDatabase;
    private String phone,email,name;
    private PullRefreshLayout pullRefreshLayout;
    private Activity activity;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.special_order_user_list_fragment,container,false);
        activity=getActivity();
        context=getContext();
        listView=view.findViewById(R.id.specialOrderUsersListView);
        layoutManager=new LinearLayoutManager(getContext());
        specialFoodArrayList=new ArrayList<>();
        countTotalFoodList=new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        specialFoodReference= firebaseDatabase.getReference("SpecialOrder").child("NewOrders");
        fetchSpecialFood();
        ProgressUtils.showLoadingDialog(context);
        specialOrderUserListCustomAdapter = new SpecialOrderUserListCustomAdapter(context, specialFoodArrayList, countTotalFoodList);
        listView.setAdapter(specialOrderUserListCustomAdapter);
        listView.setOnItemClickListener(this);
//        fetchSpecialFood();

        return view;
    }

    private void fetchSpecialFood() {
        specialFoodReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                specialFoodArrayList.add(dataSnapshot.getKey());
                Log.i("Key",dataSnapshot.getKey());
                int count=0;
                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
                    //Log.i("itemSnapshot","-------------------"+itemSnapshot.getValue(String.class));
                    Log.i("itemSnapshot key","-------------------"+itemSnapshot.getKey());
                    SpecialFood specialFood=itemSnapshot.getValue(SpecialFood.class);
                    count+=Integer.parseInt(specialFood.getFoodQuantity());
                }
                if(specialOrderUserListCustomAdapter!=null){
                    specialOrderUserListCustomAdapter.notifyDataSetChanged();
                }
                countTotalFoodList.add(String.valueOf(count));
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
        //Value events are always triggered last and are guaranteed
        // to contain updates from any other events which occurred before that snapshot was taken.
        //That's why i initialize that adapter here. Till below this method call arrayList will be filled with all data
        specialFoodReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProgressUtils.cancelLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public static SpecialOrderUsersListFragment newInstance() {
        
        Bundle args = new Bundle();
        
        SpecialOrderUsersListFragment fragment = new SpecialOrderUsersListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        LayoutInflater layoutInflater=getLayoutInflater();
        View newView=layoutInflater.inflate(R.layout.special_order_user_nested_list,null,false);
        nestedRecyclerView=newView.findViewById(R.id.specialFoodNestedRecyclerView);
        nestedRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context);
        nestedRecyclerView.setLayoutManager(layoutManager);

        //The below method will fetch the detail of user who have ordered the special food;
        fetchSpecialFoodUsers(i);
        pullRefreshLayout=newView.findViewById(R.id.nestedSpecialRefreshLayout);
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchSpecialFoodUsers(i);
            }
        });
        alertDialog.setView(newView);
        alertDialog.show();
    }


    private void fetchSpecialFoodUsers(int position) {
        Log.i("position",String.valueOf(position));
        Log.i("food","-----------------"+specialFoodArrayList.get(position));
        databaseReference=specialFoodReference.child(specialFoodArrayList.get(position));
        firebaseSpecialUserAdapter=new FirebaseRecyclerAdapter<SpecialFood, SpecialFoodUsersViewHolder>(SpecialFood.class,R.layout.special_order_user_nested_list_row_layout,SpecialFoodUsersViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(final SpecialFoodUsersViewHolder specialFoodUsersViewHolder, final SpecialFood specialFood, int i) {
                firebaseDatabase.getReference("Users").child(firebaseSpecialUserAdapter.getRef(i).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        User user=dataSnapshot.getValue(User.class);
                        Address address=null;
                        try {
                            phone = user.getPhoneNumber();
                            name=user.getName();
                            address=user.getUserAddress();

                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        if(phone!=null){
                            specialFoodUsersViewHolder.specialNestedPhoneNumber.setText(phone);
                        }
                        if(name!=null){
                            specialFoodUsersViewHolder.specialNestedEmail.setText(name);
                        }
                        if(address!=null){
                            specialFoodUsersViewHolder.specialNestedAddress.setText(address.getAddress());
                        }

                        //need to add address here
                        specialFoodUsersViewHolder.specialNestedQuantity.setText("Quantity : "+specialFood.getFoodQuantity());
                        if(firebaseSpecialUserAdapter!=null){
                            firebaseSpecialUserAdapter.notifyDataSetChanged();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        };
        nestedRecyclerView.setAdapter(firebaseSpecialUserAdapter);
//        firebaseSpecialUserAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)activity).setActionBarTitle("Users Special Order");
    }
}
