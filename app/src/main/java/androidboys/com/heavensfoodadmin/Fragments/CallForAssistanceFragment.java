package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Adapters.CallForAssistanceArrayAdapter;
import androidboys.com.heavensfoodadmin.Models.Assistance;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Variables.ContextMenuOptionId;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class CallForAssistanceFragment extends Fragment implements View.OnCreateContextMenuListener {

    private ListView listView;
    private ArrayList<Assistance> assistanceList=new ArrayList<>();
    private CallForAssistanceArrayAdapter callForAssistanceArrayAdapter;
    private FloatingActionButton addAssistanceFloatingActionButtton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.call_for_assistance_fragment,container,false);
        listView=view.findViewById(R.id.callForAssistenceListView);
        addAssistanceFloatingActionButtton=view.findViewById(R.id.addAssistance);
        callForAssistanceArrayAdapter=new CallForAssistanceArrayAdapter(getContext(),assistanceList);
        listView.setAdapter(callForAssistanceArrayAdapter);
        fetchAssistanceFromFirebase();
        listView.setOnCreateContextMenuListener(this);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView v=view.findViewById(R.id.phoneNo);

                dialNo(v.getText().toString());
            }

        });

        addAssistanceFloatingActionButtton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAssistance();
            }
        });
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select an action");
        menu.add(Menu.NONE,ContextMenuOptionId.R_ID_EDIT,1,"Edit");
        menu.add(Menu.NONE,ContextMenuOptionId.R_ID_DELETE,2,"Delete");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position=info.position;

        switch (item.getItemId()) {
            case ContextMenuOptionId.R_ID_EDIT:
//                Toast.makeText(getContext(), "edit is tapped", Toast.LENGTH_SHORT).show();
                editAssistance(position);
                break;

            case ContextMenuOptionId.R_ID_DELETE:
//                Toast.makeText(getContext(), "delete is tapped", Toast.LENGTH_SHORT).show();
                showDeleteDialog(position);
                break;

        }
        return true;
    }
    private void showDeleteDialog(final int position) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteAssistance(position);
                    }
                })
                .setNegativeButton("No",null)
                .show();
    }

    private void deleteAssistance(int position) {

        final Assistance oldAssitance=assistanceList.get(position);
        FirebaseDatabase.getInstance().getReference("Assistances").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Assistance assistance =dataSnapshot.getValue(Assistance.class);
                if(assistance.getCenter().equals(oldAssitance.getCenter())&&assistance.getPhoneNo().equals(oldAssitance.getPhoneNo())){
                    dataSnapshot.getRef().removeValue();
                    assistanceList.remove(oldAssitance);
                    callForAssistanceArrayAdapter.notifyDataSetChanged();
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


    }

    private void editAssistance(final int position) {
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.add_assistance,null,false);
        final EditText centerEditText=view.findViewById(R.id.centerNameEditText);
        final EditText phoneNoEditText=view.findViewById(R.id.phoneNoEditText);
        centerEditText.setText(assistanceList.get(position).getCenter());
        phoneNoEditText.setText(assistanceList.get(position).getPhoneNo());

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(view).setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Assistance assistance=new Assistance(centerEditText.getText().toString(),phoneNoEditText.getText().toString());
                updateAssistanceToFireBase(assistanceList.get(position),assistance);
            }
        })
                .setNegativeButton("Cancle",null)
                .show();

    }

    private void updateAssistanceToFireBase(final Assistance oldAssitance, final Assistance updatedassistance) {
        FirebaseDatabase.getInstance().getReference("Assistances").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Assistance assistance =dataSnapshot.getValue(Assistance.class);
                if(assistance.getCenter().equals(oldAssitance.getCenter())&&assistance.getPhoneNo().equals(oldAssitance.getPhoneNo())){
                    dataSnapshot.getRef().removeValue();
                    assistanceList.remove(oldAssitance);
                    callForAssistanceArrayAdapter.notifyDataSetChanged();
                    addAssistanceToFireBase(updatedassistance);
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

    }

    private void addAssistance() {

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.add_assistance,null,false);
        final EditText centerEditText=view.findViewById(R.id.centerNameEditText);
        final EditText phoneNoEditText=view.findViewById(R.id.phoneNoEditText);
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(view).setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Assistance assistance=new Assistance(centerEditText.getText().toString(),phoneNoEditText.getText().toString());
                addAssistanceToFireBase(assistance);
            }
        })
                .setNegativeButton("Cancel",null)
                .show();
    }

    private void addAssistanceToFireBase(Assistance assistance) {
        FirebaseDatabase.getInstance().getReference("Assistances").push().setValue(assistance).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(getContext(), "Assistance Added", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Please try again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAssistanceFromFirebase() {

        FirebaseDatabase.getInstance().getReference("Assistances").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                assistanceList.add(dataSnapshot.getValue(Assistance.class));
                if(callForAssistanceArrayAdapter!=null)
                callForAssistanceArrayAdapter.notifyDataSetChanged();
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

    public CallForAssistanceFragment() {
    }

    public static CallForAssistanceFragment newInstance() {
        
        Bundle args = new Bundle();
        
        CallForAssistanceFragment fragment = new CallForAssistanceFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private void dialNo(String s) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + s));
        startActivity(intent);
    }
}
