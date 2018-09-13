package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Common.FirebaseStorageDeletion;
import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.Models.FoodMenu;
import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.ViewHolders.SpecialFoodViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SpecialOrders extends Fragment implements View.OnCreateContextMenuListener {

    private RecyclerView specialOrderRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private DatabaseReference specialFoodDatabaseReference;
    private FirebaseRecyclerAdapter<SpecialFood, SpecialFoodViewHolder> specialFoodAdapter;
    private ArrayList<SpecialFood> specialFoodArrayList;
    private FloatingActionButton specialFoodFloatingActionButton;
    private ArrayList<String> foodNamesArrayList=new ArrayList<>();
    private ArrayList<Food> foodArrayList=new ArrayList<>();
    private ArrayList<String> foodItemUid=new ArrayList<>(); //this is required in order to delete the item if item is deleted from the foddItem list
//    private EditText specialFoodDescriptionEditText;
//    private EditText specialFoodNameEditText;
//    private Button specialFoodSelectButton;
//    private Button specialFoodUploadButton;
    private Spinner foodItemSpinner;
    private Uri imageUri;
    private int selectedFood;
    private StorageReference storageReference;
//    private ElegantNumberButton elegantNumberButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.special_order_fragment,container,false);
        context=getContext();
        specialFoodArrayList=new ArrayList<>();
        storageReference= FirebaseStorage.getInstance().getReference("images/");
        specialOrderRecyclerView=view.findViewById(R.id.specialFoodRecyclerView);
        specialFoodFloatingActionButton=view.findViewById(R.id.specialFoodFloatingActionButton);
        layoutManager=new LinearLayoutManager(context);
        specialFoodDatabaseReference= FirebaseDatabase.getInstance().getReference("SpecialOrder").child("FoodImages");
//      elegantNumberButton=view.findViewById(R.id.elegantNumberButton);
        PullRefreshLayout pullRefreshLayout=view.findViewById(R.id.pullRefreshLayout);
        specialOrderRecyclerView.setHasFixedSize(true);
        specialOrderRecyclerView.setLayoutManager(layoutManager);

        //since first element is "select item "
        foodArrayList.add(new Food());
        foodNamesArrayList.add("Select Item");
        foodItemUid.add(" ");

        fetchAllFoodItems();



        specialFoodFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewAlertDialog();
            }
        });
        showSpecialFoodList();
        //This below method is used to refresh the page on swiping down the recyclerview
        pullRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showSpecialFoodList();
            }
        });
        pullRefreshLayout.setColor(R.color.colorPrimary);
        return view;
    }

    private void showSpecialFoodList() {
        specialFoodAdapter=new FirebaseRecyclerAdapter<SpecialFood, SpecialFoodViewHolder>(SpecialFood.class,
                R.layout.special_food_raw_layout,SpecialFoodViewHolder.class,specialFoodDatabaseReference) {
            @Override
            protected void populateViewHolder(final SpecialFoodViewHolder specialFoodViewHolder, final SpecialFood specialFood, int i) {
                specialFoodViewHolder.specialFoodDescriptionTextView.setText(specialFood.getFoodDescription());
                specialFoodViewHolder.specialFoodNameTextView.setText(specialFood.getFoodName());
//                specialFoodViewHolder.specialFoodQuantityTextView.setText(specialFood.getFoodQuantity());
                Picasso.with(context).load(specialFood.getImageUrl()).into(specialFoodViewHolder.specialFoodImageView);
                
                
            }
        };
        specialOrderRecyclerView.setAdapter(specialFoodAdapter);
    }

    private void showNewAlertDialog() {

        final SpecialFood specialFood=new SpecialFood();

        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setTitle("Enter new food details");
        alertDialog.setIcon(R.drawable.thali_graphic);
        alertDialog.setCancelable(false);

        LayoutInflater layoutInflater=getLayoutInflater();
        
        //Since i am using same layout for alertDialog .Hence the id will also same
        View view=layoutInflater.inflate(R.layout.food_edit_alert_dialog,null,false);
        foodItemSpinner=view.findViewById(R.id.foodItemSpinner);
        CheckBox checkBox=view.findViewById(R.id.defaultCheckBox);
        checkBox.setVisibility(View.GONE);
        ArrayAdapter<String> foodNameArrayAdpter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,foodNamesArrayList);
        foodItemSpinner.setAdapter(foodNameArrayAdpter);
        foodItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFood=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

//        specialFoodDescriptionEditText=view.findViewById(R.id.alertDescriptionEditText);
//        specialFoodSelectButton=view.findViewById(R.id.alertSelectButton);
//        specialFoodUploadButton=view.findViewById(R.id.alertUploadButton);
//        specialFoodNameEditText=view.findViewById(R.id.alertFoodNameEditText);


//        specialFoodSelectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseImage();
//            }
//        });
//        specialFoodUploadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadImage(specialFood);
//            }
//        });
        alertDialog.setView(view);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Update the data
                //This below will set the new data on that key
                    specialFoodDatabaseReference.child(foodItemUid.get(selectedFood)).setValue(foodArrayList.get(selectedFood));
                    specialFoodAdapter.notifyDataSetChanged();
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

    private void fetchAllFoodItems() {
        FirebaseDatabase.getInstance().getReference("FoodItems").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Food food=dataSnapshot.getValue(Food.class);
                foodArrayList.add(food);
                foodNamesArrayList.add(food.getFoodName());
                foodItemUid.add(dataSnapshot.getKey());
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {


        //getUserVisibleHint
        if(getUserVisibleHint()){

//            if (item.getTitle().equals(Common.EDIT)) {
//                showEditAlertDialog(specialFoodAdapter.getRef(item.getOrder()).getKey(), specialFoodAdapter.getItem(item.getOrder()));
//            } else if
 if(item.getTitle().equals(Common.DELETE)) {
                //delete the data from the database
                deleteAlertDialog(item);
            }
            return  true;

        }
        else
            return false;
    }

    private void deleteAlertDialog(final MenuItem item) {
        AlertDialog.Builder alertDialg=new AlertDialog.Builder(context);
        alertDialg.setTitle("Delete the food");
        alertDialg.setMessage("Do you really want to delete it ?");
        alertDialg.setIcon(R.drawable.thali_graphic);
        alertDialg.setCancelable(false);

        alertDialg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFood(specialFoodAdapter.getItem(item.getOrder()),specialFoodAdapter.getRef(item.getOrder()).getKey());
                dialogInterface.dismiss();
                specialFoodAdapter.notifyDataSetChanged();
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

    private void deleteFood(SpecialFood specialFood,String key) {
        specialFoodDatabaseReference.child(key).removeValue();
        Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show();
        try {
            FirebaseStorageDeletion.deleteFileFromStorage(specialFood.getImageUrl(), context);
        }catch (Exception e){
            Toast.makeText(context,"This image url is not present in our server",Toast.LENGTH_SHORT).show();
        }
        specialFoodAdapter.notifyDataSetChanged();
    }

//    private void showEditAlertDialog(final String key, final SpecialFood specialFood) {
//
//        Log.i("key","------------------"+key);
//        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
//        alertDialog.setTitle("Edit the Special food details");
//        alertDialog.setIcon(R.drawable.thali_graphic);
//        LayoutInflater layoutInflater=getLayoutInflater();
//        alertDialog.setCancelable(false);
//
//        View view=layoutInflater.inflate(R.layout.food_edit_alert_dialog,null,false);
//        specialFoodDescriptionEditText=view.findViewById(R.id.alertDescriptionEditText);
//        specialFoodSelectButton=view.findViewById(R.id.alertSelectButton);
//        specialFoodUploadButton=view.findViewById(R.id.alertUploadButton);
//        specialFoodNameEditText=view.findViewById(R.id.alertFoodNameEditText);
//
//        //setting already exist food details onto edittext
//        specialFoodNameEditText.setText(specialFood.getFoodName());
//        specialFoodDescriptionEditText.setText(specialFood.getFoodDescription());
//
//
//        specialFoodSelectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseImage();
//            }
//        });
//        specialFoodUploadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadImage(specialFood);
//            }
//        });
//        alertDialog.setView(view);
//
//        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                //Update the data
//                //This below will set the new data on that key
//                specialFood.setFoodDescription(specialFoodDescriptionEditText.getText().toString());
//                specialFood.setFoodName(specialFoodNameEditText.getText().toString());
//                Log.i("immageurl inside alert", "onSuccess: "+specialFood.getImageUrl());
//                specialFoodDatabaseReference.child(key).setValue(specialFood);
//                Toast.makeText(context,specialFood.getFoodName()+ " updated",Toast.LENGTH_LONG).show();
//                specialFoodAdapter.notifyDataSetChanged();
//                dialogInterface.dismiss();
//            }
//        });
//        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//
//                dialogInterface.dismiss();
//            }
//        });
//        alertDialog.show();
//    }

//    private void uploadImage(final SpecialFood specialFood){
//        if(imageUri!=null){
//
//            //
//            //if user edited image then we have to delete first previous image
//            try {
//                FirebaseStorageDeletion.deleteFileFromStorage(specialFood.getImageUrl(), context);
//            }catch (Exception e){
//                //Toast.makeText(context,"This image url is not present in our server",Toast.LENGTH_SHORT).show();
//            }
//
//            final ProgressDialog progressDialog=new ProgressDialog(context);
//            progressDialog.setMessage("Uploading...");
//            progressDialog.show();
//            progressDialog.setCancelable(false);
//
//            String filename= UUID.randomUUID().toString();
//            final StorageReference imageFolder=storageReference.child(filename);
//            Log.i("imageuri",imageUri.toString());
//            imageFolder.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            progressDialog.dismiss();
//                            Toast.makeText(context,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
//                            specialFood.setImageUrl(uri.toString());
//                            Log.i("immageurl", "onSuccess: "+specialFood.getImageUrl());
//                        }
//                    });
//                }
//            })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//
//                            progressDialog.dismiss();
//                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress=(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
//                            progressDialog.setMessage("Uploaded "+(int)progress+" %");
//                        }
//                    });
//        }
//        else{
//            Toast.makeText(context,"Please first select the image",Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void chooseImage(){
//        Intent intent=new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);
//    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==getActivity().RESULT_OK && data!=null){
//            imageUri=data.getData();
//            specialFoodSelectButton.setText("Image Selected");
//        }
//    }
//


    public static SpecialOrders newInstance() {

        Bundle args = new Bundle();

        SpecialOrders fragment = new SpecialOrders();
        fragment.setArguments(args);
        return fragment;
    }
}
