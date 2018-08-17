package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Adapters.FaqArrayAdapter;
import androidboys.com.heavensfoodadmin.Models.Faq;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Variables.ContextMenuOptionId;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FaqFragment extends Fragment implements View.OnClickListener,View.OnCreateContextMenuListener{
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
        faqlistView.setOnCreateContextMenuListener(this);

        addQAFloatingActionButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

        menu.setHeaderTitle("Select an action!");
        //first argument of the add method is a group, second argument is an id, third order in which you want item to show , foruth title.
        menu.add(Menu.NONE,ContextMenuOptionId.R_ID_EDIT, 0, "Edit");
        menu.add(Menu.NONE,ContextMenuOptionId.R_ID_DELETE,0,"Delete");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info= (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position=info.position;

        switch (item.getItemId()) {
            case ContextMenuOptionId.R_ID_EDIT:
//                Toast.makeText(getContext(), "edit is tapped", Toast.LENGTH_SHORT).show();
                editQA(position);
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
               deleteQA(position);
                }
            })
            .setNegativeButton("No",null)
            .show();
    }


    private void editQA(final int position) {
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.add_q_a,null,false);
        final EditText questionEditText=view.findViewById(R.id.quesEditText);
        final EditText ansEditText=view.findViewById(R.id.ansEditText);
        questionEditText.setText(faqArrayList.get(position).getQuestion());
        ansEditText.setText(faqArrayList.get(position).getAnswer());
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(view).setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Faq faq=new Faq(questionEditText.getText().toString(),ansEditText.getText().toString());
                updateQAToFireBase(faqArrayList.get(position),faq);
            }
        })
                .setNegativeButton("Cancle",null)
                .show();
    }

    private void updateQAToFireBase(final Faq oldFaq, final Faq updatedfaq) {
        FirebaseDatabase.getInstance().getReference("Faq").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Faq faq=dataSnapshot.getValue(Faq.class);
                if(faq.getQuestion().equals(oldFaq.getQuestion())&&faq.getAnswer().equals(oldFaq.getAnswer())){
                    dataSnapshot.getRef().removeValue();
                    faqArrayList.remove(oldFaq);
                    faqArrayAdapter.notifyDataSetChanged();
                    addQAToFireBase(updatedfaq);
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

    private void deleteQA(int position) {

       final Faq oldFaq= faqArrayList.get(position);
        FirebaseDatabase.getInstance().getReference("Faq").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Faq faq=dataSnapshot.getValue(Faq.class);
                if(faq.getQuestion().equals(oldFaq.getQuestion())&&faq.getAnswer().equals(oldFaq.getAnswer())) {
                    dataSnapshot.getRef().removeValue();
                    faqArrayList.remove(oldFaq);
                    faqArrayAdapter.notifyDataSetChanged();
                    Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
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
                .setNegativeButton("Cancel",null)
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.addQA:
                addQA();
                break;

        }

    }
}
