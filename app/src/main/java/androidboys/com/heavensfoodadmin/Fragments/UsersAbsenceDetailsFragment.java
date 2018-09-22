package androidboys.com.heavensfoodadmin.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Adapters.UserAbsenceDetailCustomAdapter;
import androidboys.com.heavensfoodadmin.Common.UserList;
import androidboys.com.heavensfoodadmin.Models.Absence;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Utils.ProgressUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class UsersAbsenceDetailsFragment extends Fragment {


    private ListView userAbsenceDetailListView;
    private ArrayList<User> userArrayList;
    private Context context;
    private UserAbsenceDetailCustomAdapter userAbsenceDetailCustomAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.user_absence_details_fragment,container,false);
        context=getContext();
        userArrayList=new ArrayList<>();
        ProgressUtils.showLoadingDialog(context);
        userAbsenceDetailListView=view.findViewById(R.id.userAbsenceDetailListView);
        fetchAbsentUsers();

        if(userArrayList!=null) {
            userAbsenceDetailCustomAdapter = new UserAbsenceDetailCustomAdapter(context,userArrayList);
            userAbsenceDetailListView.setAdapter(userAbsenceDetailCustomAdapter);
        }
        return view;
    }

    private void fetchAbsentUsers() {
        FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                User user=dataSnapshot.getValue(User.class);
                if(user.getAbsence()!=null) {
                    userArrayList.add(user);
                    Log.i("user",user.getAbsence().getStartDate());
                    if(userAbsenceDetailCustomAdapter!=null){
                        userAbsenceDetailCustomAdapter.notifyDataSetChanged();
                    }
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

        FirebaseDatabase.getInstance().getReference("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

//                userAbsenceDetailCustomAdapter = new UserAbsenceDetailCustomAdapter(context,userArrayList);
//                userAbsenceDetailListView.setAdapter(userAbsenceDetailCustomAdapter);
                ProgressUtils.cancelLoading();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    public static UsersAbsenceDetailsFragment newInstance() {

        Bundle args = new Bundle();

        UsersAbsenceDetailsFragment fragment = new UsersAbsenceDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
