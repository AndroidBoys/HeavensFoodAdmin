package androidboys.com.heavensfoodadmin.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.UUID;

import androidboys.com.heavensfoodadmin.Activities.DescriptionActivity;
import androidboys.com.heavensfoodadmin.Adapters.OurPlansCustomArrayAdapter;
import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Common.FirebaseStorageDeletion;
import androidboys.com.heavensfoodadmin.Models.FoodMenu;
import androidboys.com.heavensfoodadmin.Models.Plan;
import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class OurPlansFragment extends Fragment implements View.OnCreateContextMenuListener {
    private ListView ourPlanslistView;
    private DescriptionActivity hostingActivity;
    private ArrayList<Plan> planList;
    private ArrayList<String> planKeyList;
    private OurPlansCustomArrayAdapter ourPlansCustomArrayAdapter;
    private PullRefreshLayout ourPlansRefreshLayout;
    private FloatingActionButton ourPlansFloatingActionButton;
    private EditText alertPlanNameEditText;
    private EditText alertPlanDescriptionEditText;
    private EditText singleTimePriceEditText;
    private EditText twoTimePriceEditText;
    private EditText threeTimePriceEditText;
    private EditText daysEditText;

    private Button alertPlanSelectButton;
    private Button alertPlanUploadButton;
    private Uri imageUri;
    private StorageReference storageReference;
    private DatabaseReference planDatabaseReference;
    private Context context;
    private String userRef=null;
    private Activity activity;
    public static Fragment newInstance(String ref) {
    public static OurPlansFragment newInstance(String ref) {

        Bundle args = new Bundle();
        args.putString("USERREF",ref);

        OurPlansFragment fragment = new OurPlansFragment();
        fragment.setArguments(args);
        return fragment;

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.our_plans_fragment,container,false);
        activity=getActivity();
        ourPlanslistView=view.findViewById(R.id.ourPlansListview);
        ourPlansFloatingActionButton=view.findViewById(R.id.ourPlansFloatingActionButton);
        storageReference= FirebaseStorage.getInstance().getReference("images/");
        planDatabaseReference=FirebaseDatabase.getInstance().getReference("OurPlans");
        context=getContext();

        userRef=getArguments().getString("USERREF",null);
        fetchOurPlansListFromFirebase();

        hostingActivity = (DescriptionActivity)getActivity();

//        hostingActivity.getSupportActionBar().hide();

        //hostingActivity.getSupportActionBar().hide();
        String title;
        if(userRef!=null){
            title="Select a plan for user";
        }else{
            title="Our Plans";
        }
        hostingActivity.getSupportActionBar().setTitle(title);

        ourPlansCustomArrayAdapter=new OurPlansCustomArrayAdapter(hostingActivity, planList,userRef);
        ourPlanslistView.setAdapter(ourPlansCustomArrayAdapter);
        ourPlansRefreshLayout=view.findViewById(R.id.ourPlansRefreshLayout);
        ourPlansRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchOurPlansListFromFirebase();
            }
        });

        ourPlansFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewPlanAlertDialog();
            }
        });

        ourPlanslistView.setOnCreateContextMenuListener(this);

        return view;
    }



    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select an action");

        menu.add(0,0,1,Common.EDIT);
        menu.add(0,1,2,Common.DELETE);

        super.onCreateContextMenu(menu, v, menuInfo);
    }


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //This info will give us the information that which listitem you have pressed
        int position = info.position;

        if(item.getTitle().equals(Common.EDIT)) {
            showEditAlertDialog(planKeyList.get(position),planList.get(position));
        }else{
            deleteAlertDialog(position);
            ourPlansCustomArrayAdapter.notifyDataSetChanged();
        }
        return super.onContextItemSelected(item);
    }




    private void deleteAlertDialog(final int position) {
        AlertDialog.Builder alertDialg=new AlertDialog.Builder(context);
        alertDialg.setTitle("Delete the food");
        alertDialg.setMessage("Do you really want to delete it ?");
        alertDialg.setIcon(R.drawable.thali_graphic);
        alertDialg.setCancelable(false);

        alertDialg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                planDatabaseReference.child(planKeyList.get(position)).removeValue();
                Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show();

                try {
                    FirebaseStorageDeletion.deleteFileFromStorage(planList.get(position).getPlanImageUrl(), context);
                }catch (Exception e){
                    Toast.makeText(context,"This image url is not present in our server",Toast.LENGTH_SHORT).show();
                }

                ourPlansCustomArrayAdapter.notifyDataSetChanged();
                dialogInterface.dismiss();
            }
        });
        alertDialg.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialg.show();
    }


    private void showEditAlertDialog(final String key, final Plan plan) {

        Log.i("key","------------------"+key);
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setTitle("Edit the plan details");
        alertDialog.setIcon(R.drawable.thali_graphic);
        LayoutInflater layoutInflater=getLayoutInflater();
        alertDialog.setCancelable(false);

        View view=layoutInflater.inflate(R.layout.our_plans_alert_dialog_layout,null,false);
        alertPlanDescriptionEditText=view.findViewById(R.id.alertPlanDescriptionEditText);
        alertPlanNameEditText=view.findViewById(R.id.alertPlanNameEditText);
        alertPlanSelectButton=view.findViewById(R.id.alertPlanSelectButton);
        alertPlanUploadButton=view.findViewById(R.id.alertPlanUploadButton);

        singleTimePriceEditText = view.findViewById(R.id.onetimePriceEditText);
        twoTimePriceEditText= view.findViewById(R.id.twotimePriceEditText);
        threeTimePriceEditText = view.findViewById(R.id.threetimePriceEditText);
        daysEditText = view.findViewById(R.id.daysEditText);


        //setting already exist food details onto edittext
        alertPlanNameEditText.setText(plan.getPlanName());
        alertPlanDescriptionEditText.setText(plan.getDescription());
        singleTimePriceEditText.setText(plan.getOneTimePrice());
        twoTimePriceEditText.setText(plan.getTwoTimePrice());
        threeTimePriceEditText.setText(plan.getThreeTimePrice());
        daysEditText.setText(plan.getNoOfDays());


        alertPlanSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        alertPlanUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(plan);
            }
        });
        //
        alertDialog.setView(view);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Update the data
//                //This below will set the new data on that key
                plan.setPlanName(alertPlanNameEditText.getText().toString());
                plan.setDescription(alertPlanDescriptionEditText.getText().toString());

                plan.setOneTimePrice(singleTimePriceEditText.getText().toString());
                plan.setTwoTimePrice(twoTimePriceEditText.getText().toString());
                plan.setThreeTimePrice(threeTimePriceEditText.getText().toString());
                plan.setNoOfDays(daysEditText.getText().toString());

                planDatabaseReference.child(key).setValue(plan).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(hostingActivity,plan.getPlanName()+" Successfully edited",Toast.LENGTH_SHORT).show();
                    }
                });
                ourPlansCustomArrayAdapter.notifyDataSetChanged();
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

    private void fetchOurPlansListFromFirebase() {
        planList=new ArrayList<>();
        planKeyList=new ArrayList<>();
        planDatabaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Plan plan =dataSnapshot.getValue(Plan.class);
                planList.add(plan);
                planKeyList.add(dataSnapshot.getKey());
                Log.i("plans",plan.getPlanName());
                if(ourPlansCustomArrayAdapter!=null)
                    ourPlansCustomArrayAdapter.notifyDataSetChanged();

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

    private void showNewPlanAlertDialog() {

        final Plan plan=new Plan();
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(hostingActivity);
        alertDialog.setTitle("Enter New Plan");
        alertDialog.setIcon(R.drawable.thali_graphic);
        alertDialog.setCancelable(false);

        LayoutInflater layoutInflater=getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.our_plans_alert_dialog_layout,null,false);
        alertPlanDescriptionEditText=view.findViewById(R.id.alertPlanDescriptionEditText);
        alertPlanNameEditText=view.findViewById(R.id.alertPlanNameEditText);

        singleTimePriceEditText = view.findViewById(R.id.onetimePriceEditText);
        twoTimePriceEditText= view.findViewById(R.id.twotimePriceEditText);
        threeTimePriceEditText = view.findViewById(R.id.threetimePriceEditText);
        daysEditText = view.findViewById(R.id.daysEditText);



        plan.setOneTimePrice(singleTimePriceEditText.getText().toString());
        plan.setTwoTimePrice(twoTimePriceEditText.getText().toString());
        plan.setThreeTimePrice(threeTimePriceEditText.getText().toString());

        alertPlanSelectButton=view.findViewById(R.id.alertPlanSelectButton);
        alertPlanUploadButton=view.findViewById(R.id.alertPlanUploadButton);

        alertPlanSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        alertPlanUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(plan);
            }
        });

        alertDialog.setView(view);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(plan.getPlanImageUrl()!=null){

                    plan.setDescription(alertPlanDescriptionEditText.getText().toString());
                    plan.setPlanName(alertPlanNameEditText.getText().toString());

                    plan.setOneTimePrice(singleTimePriceEditText.getText().toString());
                    plan.setTwoTimePrice(twoTimePriceEditText.getText().toString());
                    plan.setThreeTimePrice(threeTimePriceEditText.getText().toString());
                    plan.setNoOfDays(daysEditText.getText().toString());

                    planDatabaseReference.push().setValue(plan).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(hostingActivity,"Added Succesfully",Toast.LENGTH_SHORT).show();
                        }
                    });
                    ourPlansCustomArrayAdapter.notifyDataSetChanged();
                    dialogInterface.dismiss();
                }
                else{
                    Toast.makeText(hostingActivity,"Please First Upload the image",Toast.LENGTH_SHORT).show();
                }
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

    private void uploadImage(final Plan plan){
        if(imageUri!=null){

            //if user edited image then we have to delete first previous image
            try {
                FirebaseStorageDeletion.deleteFileFromStorage(plan.getPlanImageUrl(), context);
            }catch (Exception e){
                //Toast.makeText(context,"This image url is not present in our server",Toast.LENGTH_SHORT).show();
            }

            final ProgressDialog progressDialog=new ProgressDialog(hostingActivity);
            progressDialog.setMessage("Uploading...");
            progressDialog.show();
            progressDialog.setCancelable(false);

            String filename= UUID.randomUUID().toString();
            final StorageReference imageFolder=storageReference.child(filename);
            Log.i("imageuri",imageUri.toString());
            imageFolder.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            plan.setPlanImageUrl(uri.toString());
                            progressDialog.dismiss();
                            Toast.makeText(hostingActivity,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
                            Log.i("imageurl:",plan.getPlanImageUrl());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(hostingActivity,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+" %");
                        }
                    });
        }
        else{
            Toast.makeText(hostingActivity,"Please first select the image",Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), Common.PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==hostingActivity.RESULT_OK && data!=null){
            imageUri=data.getData();
            alertPlanSelectButton.setText("Image Selected");
        }
    }


    public static OurPlansFragment newInstance() {

        Bundle args = new Bundle();

        OurPlansFragment fragment = new OurPlansFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DescriptionActivity)activity).setActionBarTitle("Our Plans");
    }

}
