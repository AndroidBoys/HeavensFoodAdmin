package androidboys.com.heavensfoodadmin;

import android.app.Application;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


public class MyApplication extends Application {

    public static MyApplication thisApp;
    public static boolean notificationStatus;

    @Override
    public void onCreate() {
        super.onCreate();
        thisApp = this;

        FirebaseDatabase.getInstance().getReference("Notification").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue()!=null)
                    notificationStatus=true;
                else
                    notificationStatus=false;

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
