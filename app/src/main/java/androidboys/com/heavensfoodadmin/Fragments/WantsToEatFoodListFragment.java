package androidboys.com.heavensfoodadmin.Fragments;

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

import androidboys.com.heavensfoodadmin.Adapters.WantsToEatCustomAdapter;
import androidboys.com.heavensfoodadmin.Models.FoodMenu;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Utils.ProgressUtils;
import androidboys.com.heavensfoodadmin.ViewHolders.SpecialFoodUsersViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WantsToEatFoodListFragment extends Fragment implements AdapterView.OnItemClickListener {

    private ListView wantsToEatListView;
    private Context context;
    private ArrayList<String> wantsFoodNameArrayList;
    private ArrayList<String> countFoodArrayList;
    private DatabaseReference wantsToEatFoodReference;
    private WantsToEatCustomAdapter wantsToEatCustomAdapter;
    private RecyclerView nestedRecyclerView;
    private PullRefreshLayout pullRefreshLayout;
    private DatabaseReference databaseReference;
    private FirebaseRecyclerAdapter<String,SpecialFoodUsersViewHolder> firebaseSpecialUserAdapter;
    private FirebaseDatabase firebaseDatabase;
    private String phone,email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //Since special order and wantsTo eat have same Ui so i am using specialOrder xml here also.So Don't get confused
        View view=inflater.inflate(R.layout.special_order_user_list_fragment,container,false);
        context=getContext();
        wantsFoodNameArrayList=new ArrayList<>();
        countFoodArrayList=new ArrayList<>();
        ProgressUtils.showLoadingDialog(context);
        wantsToEatListView=view.findViewById(R.id.specialOrderUsersListView);
        firebaseDatabase=FirebaseDatabase.getInstance();
        wantsToEatFoodReference=firebaseDatabase.getReference("FavouriteFood");
        fetchWantsToEatFood();
        wantsToEatCustomAdapter=new WantsToEatCustomAdapter(context,wantsFoodNameArrayList,countFoodArrayList);
        wantsToEatListView.setAdapter(wantsToEatCustomAdapter);
        wantsToEatListView.setOnItemClickListener(this);

        return view;

    }

    private void fetchWantsToEatFood() {
        wantsToEatFoodReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                wantsFoodNameArrayList.add(dataSnapshot.getKey());
                Log.i("Key",dataSnapshot.getKey());
//                for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){
//                    //Log.i("itemSnapshot","-------------------"+itemSnapshot.getValue(String.class));
//                    Log.i("itemSnapshot key","-------------------"+itemSnapshot.getKey());
//                    FoodMenu foodMenu=itemSnapshot.getValue(FoodMenu.class);
//                }
                if(wantsToEatCustomAdapter!=null){
                    wantsToEatCustomAdapter.notifyDataSetChanged();
                }
                countFoodArrayList.add(String.valueOf(dataSnapshot.getChildrenCount()));
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
        wantsToEatFoodReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ProgressUtils.cancelLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
//
//        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context,android.R.style.Theme_Black_NoTitleBar_Fullscreen);
//        LayoutInflater layoutInflater=getLayoutInflater();
//        View newView=layoutInflater.inflate(R.layout.special_order_user_nested_list,null,false);
//        nestedRecyclerView=newView.findViewById(R.id.specialFoodNestedRecyclerView);
//        nestedRecyclerView.setHasFixedSize(true);
//        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(context);
//        nestedRecyclerView.setLayoutManager(layoutManager);
//
//        //The below method will fetch the detail of user who have ordered the special food;
//        fetchWantsToEatFoodUsers(i);
//        pullRefreshLayout=newView.findViewById(R.id.nestedSpecialRefreshLayout);
//        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                fetchWantsToEatFoodUsers(i);
//            }
//        });
//        alertDialog.setView(newView);
//        alertDialog.show();
    }
//
//    private void fetchWantsToEatFoodUsers(int position) {
//
//        Log.i("position",String.valueOf(position));
//        Log.i("food","-----------------"+wantsFoodNameArrayList.get(position));
//        databaseReference=wantsToEatFoodReference.child(wantsFoodNameArrayList.get(position));
//        firebaseSpecialUserAdapter=new FirebaseRecyclerAdapter<String, SpecialFoodUsersViewHolder>(String.class,R.layout.special_order_user_nested_list_row_layout,SpecialFoodUsersViewHolder.class,databaseReference) {
//            @Override
//            protected void populateViewHolder(final SpecialFoodUsersViewHolder specialFoodUsersViewHolder, final String string, int i) {
//                firebaseDatabase.getReference("Users").child(firebaseSpecialUserAdapter.getRef(i).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        User user=dataSnapshot.getValue(User.class);
//                        phone=user.getPhoneNumber();
//                        email=user.getEmail();
//
//                        if(phone!=null){
//                            specialFoodUsersViewHolder.specialNestedPhoneNumber.setText(phone);
//                        }
//                        if(email!=null){
//                            specialFoodUsersViewHolder.specialNestedEmail.setText(email);
//                        }
//
//                        //need to add address here
//
//                        specialFoodUsersViewHolder.specialNestedQuantity.setVisibility(View.GONE);
//
//                        if(firebaseSpecialUserAdapter!=null){
//                            firebaseSpecialUserAdapter.notifyDataSetChanged();
//                        }
//
//                    }
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });
//            }
//        };
//        nestedRecyclerView.setAdapter(firebaseSpecialUserAdapter);
//    }

    public static WantsToEatFoodListFragment newInstance() {

        Bundle args = new Bundle();
        WantsToEatFoodListFragment fragment = new WantsToEatFoodListFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
