package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Adapters.WantsToEatOrderAlertDialogCustomAdapter;
import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Interfaces.OurCustomClickListener;
import androidboys.com.heavensfoodadmin.Models.DBnotification;
import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.Models.Order;
import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.Models.SpecialFoodOrder;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Utils.ProgressUtils;
import androidboys.com.heavensfoodadmin.ViewHolders.WantsToEatOrdersViewHOlder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


//This Fragment will show the users list who have ordered the wantsToEat food.
public class WantsToEatOrdersFragment extends Fragment implements View.OnCreateContextMenuListener {

    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private FirebaseRecyclerAdapter<Order,WantsToEatOrdersViewHOlder> firebaseRecyclerAdapter;
    private DatabaseReference databaseReference;
    private ArrayList<ArrayList<Food>> usersFoodArrayList;
    private ArrayList<SpecialFood> userSpecialFoodArrayList;
    private ListView wantsFoodListView;
    private ListView specialOrderListView;
    private ArrayList<String> userUidKeyArrayList;
    private DBnotification dBnotification;
    private TextView specialOrderTextView;
    private String mealTime;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.wants_to_eat_orders_fragment,container,false);
        context=getContext();
        usersFoodArrayList= new ArrayList<>();
        userSpecialFoodArrayList=new ArrayList<>();
        userUidKeyArrayList=new ArrayList<>();
        specialOrderTextView=view.findViewById(R.id.specialOrderTextView);
        databaseReference= FirebaseDatabase.getInstance().getReference("Orders");
        recyclerView=view.findViewById(R.id.wantsToEatOrdersRecyclerView);
        linearLayoutManager=new LinearLayoutManager(context);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        fetchAllTheOrdersUsers();//it will fetch all the data from orders node
//        fetchSpecialFoodOfUser();
        return view;
    }



    //it will fetch all the data from orders node
    private void fetchAllTheOrdersUsers() {
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Order, WantsToEatOrdersViewHOlder>(
                Order.class,R.layout.wants_to_eat_order_row_layout,WantsToEatOrdersViewHOlder.class,databaseReference.child("NewFoodOrders")
        ) {
            @Override
            protected void populateViewHolder(WantsToEatOrdersViewHOlder wantsToEatOrdersViewHOlder, Order order, int i) {
                userUidKeyArrayList.add(firebaseRecyclerAdapter.getRef(i).getKey());//Storing key into arraylist
                setUsersData(wantsToEatOrdersViewHOlder,order);
                wantsToEatOrdersViewHOlder.setOnOurCustomClickListener(new OurCustomClickListener() {
                    @Override
                    public void onClick(View view,int position) {
                        showAlertDialog(position);
                    }
                });
            }
        };
        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    private void showAlertDialog(int position) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.thali_graphic);
        alertDialog.setTitle("Food selected by user");
        LayoutInflater layoutInflater=getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.wants_to_eat_order_alert_dialog,null,false);
        wantsFoodListView=view.findViewById(R.id.wantsListView);
        specialOrderListView=view.findViewById(R.id.specialOrderListView);
        specialOrderTextView=view.findViewById(R.id.specialOrderTextView);

        ProgressUtils.showLoadingDialog(context);
        fetchSpecialFoodOfUser(position);

        WantsToEatOrderAlertDialogCustomAdapter wantsToEatOrderAlertDialogCustomAdapter=new WantsToEatOrderAlertDialogCustomAdapter(context,usersFoodArrayList.get(position),null);
        wantsFoodListView.setAdapter(wantsToEatOrderAlertDialogCustomAdapter);
        setListViewHeightBasedOnChildren(wantsFoodListView);

        alertDialog.setView(view);
        alertDialog.show();
    }

    // it will fetch the special order of corresponding user
    private void fetchSpecialFoodOfUser(final int position) {

       // ProgressUtils.showLoadingDialog(context);
        FirebaseDatabase.getInstance().getReference("Orders").child("mealTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealTime=dataSnapshot.getValue(String.class);
                databaseReference.child("NewSpecialOrders").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int i=0;
                        for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){

                            if(itemSnapshot.getKey().equals(userUidKeyArrayList.get(position))){
                                SpecialFoodOrder specialFoodOrder=itemSnapshot.getValue(SpecialFoodOrder.class);
                                if(specialFoodOrder.getMealTime().equals(mealTime)){
                                    userSpecialFoodArrayList=specialFoodOrder.getSpecialFoodsArrayList();

                                    //Setting Special Orders list also
                                    WantsToEatOrderAlertDialogCustomAdapter wantsToEatOrderAlertDialogCustomAdapter1 = new WantsToEatOrderAlertDialogCustomAdapter(context, null, userSpecialFoodArrayList);
                                    specialOrderListView.setAdapter(wantsToEatOrderAlertDialogCustomAdapter1);
                                    setListViewHeightBasedOnChildren(specialOrderListView);
                                    ProgressUtils.cancelLoading();
                                }
                                break;
                            }
                            i++;
                        }
                        if(i==dataSnapshot.getChildrenCount()){
                            ProgressUtils.cancelLoading();
                            specialOrderTextView.setText("No Special Order");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    private void setUsersData(WantsToEatOrdersViewHOlder wantsToEatOrdersViewHOlder, Order order) {
        User user=order.getUser();
        ArrayList<Food> foodArrayList=order.getFoodArrayList();
        int status = order.getStatus();

        wantsToEatOrdersViewHOlder.userNameTextView.setText(user.getName());
        wantsToEatOrdersViewHOlder.userNumberTextView.setText(user.getPhoneNumber());

        if(user.getUserAddress()!=null)
        wantsToEatOrdersViewHOlder.userAddressTextView.setText(user.getUserAddress().getAddress());
        usersFoodArrayList.add(foodArrayList);
        if(status ==1){
            //Packed condition(means when the food is packed by their staffs)
            wantsToEatOrdersViewHOlder.checkImageView.setVisibility(View.VISIBLE);
        }
    }


    public static WantsToEatOrdersFragment newInstance() {

        Bundle args = new Bundle();
        WantsToEatOrdersFragment fragment = new WantsToEatOrdersFragment();
        fragment.setArguments(args);
        return fragment;
    }


    //This below method is used to set the height of listview based on the number of items present inside it.
    private void setListViewHeightBasedOnChildren(ListView listView) {
        ArrayAdapter listAdapter = (ArrayAdapter) listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        //Since we know that onContextItemSelected will be called whenever activity will be created
        //This method will also be called at that time when we want to call it for only a particular fragment which we don't want.
        //getUserVisiblityHint will check. Is this fragment is visible to user at current time.
        if(getUserVisibleHint()){
            if(item.getTitle().equals(Common.PACKED)){
                if(firebaseRecyclerAdapter.getItem(item.getOrder()).getStatus()!=1) {
                    showPackedAlertDialog(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey(), firebaseRecyclerAdapter.getItem(item.getOrder()));
                }else{
                    Toast.makeText(context,"Food is already packed for this user",Toast.LENGTH_SHORT).show();
                }
            }else{
                Log.i("Position",String.valueOf(item.getOrder()));
                if(firebaseRecyclerAdapter.getItem(item.getOrder()).getStatus()==1) {
                    showDeliveredAlertDialog(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey(), item.getOrder());
                }else{
                    Toast.makeText(context,"Please First pack the food",Toast.LENGTH_SHORT).show();
                }
            }
        }
        return true;

    }

    private void showDeliveredAlertDialog(final String key, final int position) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.thali_graphic);
        alertDialog.setTitle("Food Delivered");
        alertDialog.setMessage("Are you sure You have delivered the food ?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                ProgressUtils.showLoadingDialog(context);
                deleteFromFavouriteFood(position);  //deleting user from favourite food
                deleteFromSpecialOrder(key,position);  // deleting user from special order
                databaseReference.child("NewFoodOrders").child(key).removeValue();
                dialogInterface.dismiss();
                }
        });
         alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    private void deleteFromSpecialOrder(final String key, final int position) {

        FirebaseDatabase.getInstance().getReference("Orders").child("mealTime").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mealTime=dataSnapshot.getValue(String.class);
                databaseReference.child("NewSpecialOrders").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot itemSnapshot:dataSnapshot.getChildren()){

                            if(itemSnapshot.getKey().equals(userUidKeyArrayList.get(position))) {
                                SpecialFoodOrder specialFoodOrder = itemSnapshot.getValue(SpecialFoodOrder.class);
                                if (specialFoodOrder.getMealTime().equals(mealTime)) {
                                    userSpecialFoodArrayList = specialFoodOrder.getSpecialFoodsArrayList();

                                    DatabaseReference favDbReference = FirebaseDatabase.getInstance().getReference("SpecialOrder").child("NewOrders");

                                    //Deleting all the user from special order also
                                    for (int j = 0; j < userSpecialFoodArrayList.size(); j++) {
                                        favDbReference.child(userSpecialFoodArrayList.get(j).getFoodName())
                                                .child(userUidKeyArrayList.get(position)).removeValue();

                                    }
                                }
                                break;
                            }
                        }

                        databaseReference.child("NewSpecialOrders").child(key).removeValue();
                        userUidKeyArrayList.remove(position);//Remove id from id arraylist also
                        usersFoodArrayList.remove(position);
                        ProgressUtils.cancelLoading();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteFromFavouriteFood(final int position) {

        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("FavouriteFood");
        ArrayList<Food> arrayList=usersFoodArrayList.get(position);
        for(int i=0;i<arrayList.size();i++){
            reference.child(arrayList.get(i).getFoodName()).
                    child(userUidKeyArrayList.get(position)).removeValue();
        }
    }

    private void showPackedAlertDialog(final String key, final Order order) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setTitle("Packed the Food");
        alertDialog.setIcon(R.drawable.thali_graphic);
        alertDialog.setMessage("Are you sure You have Packed the food ?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {

                //status 1 means food is packed..
                order.setStatus(1);
                databaseReference.child("NewFoodOrders").child(key).setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context,"Good Job bro..",Toast.LENGTH_SHORT).show();
                        dialogInterface.dismiss();
                    }
                });
            }
        });

        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();
    }
}
