package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Adapters.WantsToEatOrderAlertDialogCustomAdapter;
import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Interfaces.OurCustomClickListener;
import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.Models.Order;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
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
    private ListView foodListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.wants_to_eat_orders_fragment,container,false);
        context=getContext();
        usersFoodArrayList= new ArrayList<>();
        databaseReference= FirebaseDatabase.getInstance().getReference("Orders");
        recyclerView=view.findViewById(R.id.wantsToEatOrdersRecyclerView);
        linearLayoutManager=new LinearLayoutManager(context);

        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setHasFixedSize(true);

        fetchAllTheOrdersUsers();//it will fetch all the data from orders node
        return view;
    }

    //it will fetch all the data from orders node
    private void fetchAllTheOrdersUsers() {
        firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Order, WantsToEatOrdersViewHOlder>(
                Order.class,R.layout.wants_to_eat_order_row_layout,WantsToEatOrdersViewHOlder.class,databaseReference
        ) {
            @Override
            protected void populateViewHolder(WantsToEatOrdersViewHOlder wantsToEatOrdersViewHOlder, Order order, int i) {
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
        foodListView=view.findViewById(R.id.listView);
        WantsToEatOrderAlertDialogCustomAdapter wantsToEatOrderAlertDialogCustomAdapter=new WantsToEatOrderAlertDialogCustomAdapter(context,usersFoodArrayList.get(position));
        foodListView.setAdapter(wantsToEatOrderAlertDialogCustomAdapter);
        alertDialog.setView(view);
        alertDialog.show();
    }

    private void setUsersData(WantsToEatOrdersViewHOlder wantsToEatOrdersViewHOlder, Order order) {
        User user=order.getUser();
        ArrayList<Food> foodArrayList=order.getFoodArrayList();
        int status = order.getStatus();

        wantsToEatOrdersViewHOlder.userNameTextView.setText(user.getName());
        wantsToEatOrdersViewHOlder.userNumberTextView.setText(user.getPhoneNumber());
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
                showDeliveredAlertDialog(firebaseRecyclerAdapter.getRef(item.getOrder()).getKey());
            }
        }
        return true;

    }

    private void showDeliveredAlertDialog(final String key) {
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setIcon(R.drawable.thali_graphic);
        alertDialog.setTitle("Food Delivered");
        alertDialog.setMessage("Are you sure You have delivered the food ?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                databaseReference.child(key).removeValue();
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
                databaseReference.child(key).setValue(order).addOnSuccessListener(new OnSuccessListener<Void>() {
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
