package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Adapters.FaqArrayAdapter;
import androidboys.com.heavensfoodadmin.Models.Assistance;
import androidboys.com.heavensfoodadmin.Models.Faq;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FaqFragment extends Fragment {
    private ListView faqlistView;
    private ArrayList<Faq> faqArrayList=new ArrayList<>();
    private FaqArrayAdapter faqArrayAdapter;
    private FloatingActionButton addQAFloatingActionButton;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.f_a_q_fragment,container,false);
        faqlistView=view.findViewById(R.id.faqListView);
        addQAFloatingActionButton=view.findViewById(R.id.addQA);
        faqArrayAdapter=new FaqArrayAdapter(getContext(),faqArrayList);
        faqlistView.setAdapter(faqArrayAdapter);
        fetchFaqFromFirebase();

        addQAFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addQA();
            }
        });

        return view;
    }
    private void addQA() {

        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.add_q_a,null,false);
        final EditText questionEditText=view.findViewById(R.id.quesEditText);
        final EditText ansEditText=view.findViewById(R.id.ansEditText);
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(view).setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
               Faq faq=new Faq(questionEditText.getText().toString(),ansEditText.getText().toString());
                addQAToFireBase(faq);
            }
        })
                .setNegativeButton("Cancle",null)
                .show();
    }

    private void addQAToFireBase(Faq faq) {
        FirebaseDatabase.getInstance().getReference("Faq").push().setValue(faq).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(getContext(), "updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "please try again", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void fetchFaqFromFirebase() {

        FirebaseDatabase.getInstance().getReference("Faq").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                faqArrayList.add(dataSnapshot.getValue(Faq.class));
                if (faqArrayAdapter != null)
                    faqArrayAdapter.notifyDataSetChanged();
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

    public FaqFragment() {
    }

    public static FaqFragment newInstance() {
        
        Bundle args = new Bundle();
        
        FaqFragment fragment = new FaqFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
