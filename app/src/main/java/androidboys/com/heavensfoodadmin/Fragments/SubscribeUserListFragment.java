package androidboys.com.heavensfoodadmin.Fragments;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.Models.WhyHeavenFood;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.ViewHolders.UserListViewHolder;
import androidboys.com.heavensfoodadmin.ViewHolders.WhyHeavensFoodViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribeUserListFragment extends Fragment {

    private RecyclerView recyclerView;
    private TextDrawable textDrawable;
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

        return view;
    }

    private void fetchAboutDataFromFirebase() {

        generator = ColorGenerator.MATERIAL;//to generate random colors

       DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference("Users");
       adapter=new FirebaseRecyclerAdapter<User, UserListViewHolder>(User.class,R.layout.user_list_row_layout,UserListViewHolder.class,databaseReference) {
           @Override
           protected void populateViewHolder(final UserListViewHolder userListViewHolder, User user, int i) {

               Log.d("info",user.getEmail());

               if(user.subscribedPlan!=null) {
                   userListViewHolder.NameTextView.setText(user.getEmail());
                   textDrawable = TextDrawable.builder()
                           .buildRound(""+"" + user.getEmail().charAt(0), generator.getRandomColor());//setting first letter of the user name

                   userListViewHolder.drawableImageView.setImageDrawable(textDrawable);
                   userListViewHolder.phoneTextView.setText(user.getPhoneNumber());
                   userListViewHolder.callImageView.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + userListViewHolder.phoneTextView.getText()));
                           startActivity(intent);
                       }
                   });

                   userListViewHolder.layout.setOnClickListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View view) {
                           //GO TO THE PROFILE
                           Toast.makeText(getContext(), "layoutpressed", Toast.LENGTH_SHORT).show();
                       }
                   });
               }
               else
                   userListViewHolder.Layout_hide();
           }
       };
       recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    public static SubscribeUserListFragment newInstance() {

        Bundle args = new Bundle();

        SubscribeUserListFragment fragment = new SubscribeUserListFragment();
        fragment.setArguments(args);
        return fragment;
    }
}