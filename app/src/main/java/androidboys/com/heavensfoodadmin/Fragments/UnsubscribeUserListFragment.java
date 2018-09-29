package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidboys.com.heavensfoodadmin.Activities.DescriptionActivity;
import androidboys.com.heavensfoodadmin.Activities.HomeActivity;
import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.Payments.PaymentsActivity;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.ViewHolders.UserListViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class UnsubscribeUserListFragment extends Fragment{

    private RecyclerView recyclerView;
    private TextDrawable textDrawable;
    private Context context;
    private FirebaseRecyclerAdapter<User,UserListViewHolder> adapter;
    private ColorGenerator generator;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.user_list_fragment,container,false);
        recyclerView=view.findViewById(R.id.subscribedUserListRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        fetchAboutDataFromFirebase();

        context=getContext();
        return view;
    }

    private void fetchAboutDataFromFirebase() {

        generator = ColorGenerator.MATERIAL;//to generate random colors

       DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
       adapter=new FirebaseRecyclerAdapter<User, UserListViewHolder>(User.class,R.layout.user_list_row_layout,UserListViewHolder.class,databaseReference) {
           @Override
           protected void populateViewHolder(final UserListViewHolder userListViewHolder, final User user, final int i) {

               if(user.subscribedPlan==null) {
                   userListViewHolder.NameTextView.setText(user.email);
                   textDrawable = TextDrawable.builder()
                           .buildRound("" + user.getEmail().charAt(0), generator.getRandomColor());//setting first letter of the user name
                   userListViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                       @Override
                       public boolean onLongClick(View view) {
                           Toast.makeText(context, "hello", Toast.LENGTH_SHORT).show();
                           showAlert(adapter.getRef(i));
                           //this ref will move in this way
                           // ourplans--->showDetailsButtonClick----->SubscribeButtonClick---->finally use the ref
                           Log.d("ref",""+adapter.getRef(i));
                           return true;
                       }
                   });

                   userListViewHolder.drawableImageView.setImageDrawable(textDrawable);
                   userListViewHolder.phoneTextView.setText(user.getPhoneNumber());
                   userListViewHolder.callImageView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userListViewHolder.phoneTextView.getText()));
                           startActivity(intent);
                       }
                   });

                   userListViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           //GO TO THE PROFILE
                           Toast.makeText(getContext(), "layoutpressed", Toast.LENGTH_SHORT).show();
                           showProfile(user);
                       }
                   });

               }
               else
                   userListViewHolder.Layout_hide();

               }
//           }
       };
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    private void showAlert(final DatabaseReference user) {
    AlertDialog builder=new AlertDialog.Builder(context)
            .setIcon(R.drawable.thali_graphic)
            .setTitle("Aprove subscription")
            .setCancelable(false)
            .setMessage("Do you really want to grant this user a plan?")
            .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                  Intent intent=new Intent(context, PaymentsActivity.class);
                  intent.putExtra("USERREF", String.valueOf(user));
//                  intent.putExtra("ID",R.id.ourPlansButton);
//                    args.putExtra("NAME",user.getName());
//                    args.putExtra("EMAIL",user.getEmail());
//                    args.putExtra("PHONE",user.getPhoneNumber());
//                    args.putExtra("PASS",user.getPassword());
//                    args.putExtra("ADDRESS",user.getUserAddress());
//                    args.putExtra("ABSENCE",user.getAbsence());
//                    args.putSerializable("PLAN",user.getSubscribedPlan());
//                    args.putSerializable("WALLET",user.getWallet());
                    startActivity(intent);

                }
            })
            .setNegativeButton("Deny",null)
            .show();

    }

    private void showProfile(User user) {
        UserProfileFragment userProfileFragment=UserProfileFragment.newInstance(user);
        FragmentManager fragmentManager=getFragmentManager();
        userProfileFragment.show(fragmentManager,"Profile");

    }

    public static UnsubscribeUserListFragment newInstance() {

        Bundle args = new Bundle();

        UnsubscribeUserListFragment fragment = new UnsubscribeUserListFragment();
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        if(getUserVisibleHint()) {
//            switch (item.getItemId()) {
//                case Common.R_ID_SUBSCRIBE:
//                    Toast.makeText(context, ""+adapter.getRef(item.getOrder()), Toast.LENGTH_SHORT).show();
//                    showPlans(adapter.getRef(item.getOrder()));
////                Toast.makfragmentInForeground instanceof WantsToEatFragmenteText(getContext(), "edit is tapped", Toast.LENGTH_SHORT).show();
////                    editItem(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
//                    break;
//            }
//            return true;
//        }
//        else
//            return false;
//    }
    private void showPlans(DatabaseReference ref) {

//        View view=LayoutInflater.from(context).inflate(R.layout.)
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context,android.R.style.Theme_DeviceDefault_Light_NoActionBar_Fullscreen)
                .setPositiveButton("SUBSCRIBE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Toast.makeText(context, "button clicked", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel",null);
//                .setView(view);

    }

}
