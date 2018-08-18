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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Models.FoodMenu;
import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.ViewHolders.FoodMenuViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WeeklyMenuNestedFragment extends Fragment implements View.OnCreateContextMenuListener {

    private RecyclerView breakFastRecyclerView,
            lunchRecyclerView,
            dinnerRecyclerView;
    private Context context;
    private LinearLayoutManager linearLayoutManager;
    private FirebaseRecyclerAdapter<FoodMenu, FoodMenuViewHolder> dinnerAdapter,
            lunchAdapter,
            breakFastAdapter;
    private String day;

    private DatabaseReference todayMenuDatabaseReference;
    private FloatingActionButton weeklyMenuFloatingActionButton;
    private EditText weeklyFoodDescriptionEditText;
    private EditText weeklyFoodNameEditText;
    private Button weeklyFoodSelectButton;
    private Button weeklyFoodUploadButton;
    private Uri imageUri;
    private StorageReference storageReference;
    private Spinner weeklySpinner;
    private int selectedFoodType; //This is used in spinner when new food will be added
    private CheckBox weeklyBreakfastCheckBox;
    private CheckBox weeklyLunchCheckBox;
    private CheckBox weeklyDinnerCheckBox;
    private String chooseFoodType; //This is used when we edited the food

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.weekly_menu_nested_fragment,container,false);
        breakFastRecyclerView=view.findViewById(R.id.breakFastRecyclerView);
        lunchRecyclerView=view.findViewById(R.id.lunchRecyclerView);
        dinnerRecyclerView=view.findViewById(R.id.dinnerRecyclerView);

        chooseFoodType=null;

        weeklyBreakfastCheckBox=view.findViewById(R.id.weeklyBreakfastCheckBox);
        weeklyLunchCheckBox=view.findViewById(R.id.weeklyLunchCheckBox);
        weeklyDinnerCheckBox=view.findViewById(R.id.weeklyDinnerCheckBox);

        context=getContext();
        weeklyMenuFloatingActionButton=view.findViewById(R.id.weeklyMenuFloatingActionButton);
        storageReference= FirebaseStorage.getInstance().getReference("images/");

        //PullRefreshLayout is used to refresh the page
        PullRefreshLayout weeklyRefreshLayout=view.findViewById(R.id.weeklyRefreshLayout);

        //it will get the day name passed when a perticular day is pressed from weeklyMenuFragment
        Bundle args=getArguments();
        day=args.getString("DAY",null);

        todayMenuDatabaseReference=FirebaseDatabase.getInstance().getReference("WeeklyMenu").child(day);

        // linearLayoutManager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        setRecyclerView(breakFastRecyclerView);
        setRecyclerView(lunchRecyclerView);
        setRecyclerView(dinnerRecyclerView);
        showBreakFastImages();
        showlunchImages();
        showDinnerImages();
        weeklyRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showBreakFastImages();
                showlunchImages();
                showDinnerImages();
            }
        });
        weeklyRefreshLayout.setColor(R.color.colorPrimary);


        weeklyMenuFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewAlertDialog();
            }
        });

        weeklyBreakfastCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b && !weeklyDinnerCheckBox.isChecked() && !weeklyLunchCheckBox.isChecked()){
                    chooseFoodType="BreakFast";
                }else if(!b){
                    chooseFoodType=null;
                }else{
                    Toast.makeText(context,"Please first uncheck the other one",Toast.LENGTH_SHORT).show();
                    weeklyBreakfastCheckBox.setChecked(false);
                }
            }
        });

        weeklyDinnerCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b && !weeklyBreakfastCheckBox.isChecked() && !weeklyLunchCheckBox.isChecked()){
                    chooseFoodType="Dinner";
                }else if(!b){
                    chooseFoodType=null;
                }else{
                    Toast.makeText(context,"Please first uncheck the other one",Toast.LENGTH_SHORT).show();
                    weeklyDinnerCheckBox.setChecked(false);
                }
            }
        });

        weeklyLunchCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b && !weeklyBreakfastCheckBox.isChecked() && !weeklyDinnerCheckBox.isChecked()) {
                    chooseFoodType = "Lunch";
                }else if(!b){
                    chooseFoodType=null;
                }else{
                    Toast.makeText(context,"Please first uncheck the other one",Toast.LENGTH_SHORT).show();
                    weeklyLunchCheckBox.setChecked(false);
                }
            }
        });
        return view;
    }


    private void setRecyclerView(RecyclerView recyclerView) {

        linearLayoutManager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void showDinnerImages() {
        DatabaseReference databaseReference= todayMenuDatabaseReference.child("Dinner");
        dinnerAdapter=new FirebaseRecyclerAdapter<FoodMenu,FoodMenuViewHolder>(
                FoodMenu.class,R.layout.food_menu_row_layout,FoodMenuViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(FoodMenuViewHolder foodMenuViewHolder, FoodMenu foodMenu, int i) {
                setFoodDetails(foodMenuViewHolder, foodMenu);

            }
        };
        dinnerAdapter.notifyDataSetChanged();
        dinnerRecyclerView.setAdapter(dinnerAdapter);
        //Download the images from the firebase
    }

    private void setFoodDetails(FoodMenuViewHolder foodMenuViewHolder,FoodMenu foodMenu) {
        foodMenuViewHolder.foodNameTextView.setText(foodMenu.getFoodName());
        foodMenuViewHolder.foodDescriptionTextView.setText(foodMenu.getFoodDescription());
        Picasso.with(context).load(foodMenu.getImageUrl()).into(foodMenuViewHolder.foodImageView);

    }

    private void showlunchImages() {
        DatabaseReference databaseReference=todayMenuDatabaseReference.child("Lunch");
        lunchAdapter=new FirebaseRecyclerAdapter<FoodMenu, FoodMenuViewHolder>(
                FoodMenu.class,R.layout.food_menu_row_layout,FoodMenuViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(FoodMenuViewHolder foodMenuViewHolder, FoodMenu foodMenu, int i) {
                setFoodDetails(foodMenuViewHolder, foodMenu);

            }
        };
        lunchAdapter.notifyDataSetChanged();
        lunchRecyclerView.setAdapter(lunchAdapter);
        //Download the images from the firebase
    }

    private void showBreakFastImages() {
        DatabaseReference databaseReference=todayMenuDatabaseReference.child("BreakFast");
        breakFastAdapter=new FirebaseRecyclerAdapter<FoodMenu, FoodMenuViewHolder>(
                FoodMenu.class,R.layout.food_menu_row_layout,FoodMenuViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(FoodMenuViewHolder foodMenuViewHolder, FoodMenu foodMenu, int i) {
                setFoodDetails(foodMenuViewHolder, foodMenu);

            }
        };
        breakFastAdapter.notifyDataSetChanged();
        breakFastRecyclerView.setAdapter(breakFastAdapter);
        //Download the images from the firebase
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(chooseFoodType!=null) {

            if (item.getTitle().equals(Common.EDIT)) {

                switch (chooseFoodType) {


                    case "BreakFast":
                        try {
                            showEditAlertDialog(breakFastAdapter.getRef(item.getOrder()).getKey(), breakFastAdapter.getItem(item.getOrder()), breakFastAdapter);
                        }catch (Exception e){
                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case "Lunch":
                        try {
                            showEditAlertDialog(lunchAdapter.getRef(item.getOrder()).getKey(), lunchAdapter.getItem(item.getOrder()), lunchAdapter);
                        }catch(Exception e){
                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
                        }
                        break;

                    default:
                        try{
                        showEditAlertDialog(dinnerAdapter.getRef(item.getOrder()).getKey(), dinnerAdapter.getItem(item.getOrder()), dinnerAdapter);
                        }catch(Exception e){
                               Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
                        }
                        break;

                }
            } else {

                switch (chooseFoodType){

                    case "BreakFast":
                        try{
                        deleteAlertDialog(breakFastAdapter.getRef(item.getOrder()).getKey(),breakFastAdapter);
                        }catch(Exception e){
                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
                        }break;

                    case "Lunch":
                        try{
                        deleteAlertDialog(lunchAdapter.getRef(item.getOrder()).getKey(),lunchAdapter);
                        }catch(Exception e){
                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
                        }break;

                    default:
                        try {
                            deleteAlertDialog(dinnerAdapter.getRef(item.getOrder()).getKey(), dinnerAdapter);
                        }catch(Exception e){
                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        }else{
            Toast.makeText(context,"Please first select the food type",Toast.LENGTH_SHORT).show();
        }

        return super.onContextItemSelected(item);
    }


    private void showNewAlertDialog() {

        final FoodMenu foodMenu=new FoodMenu();

        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setTitle("Enter new food details");
        alertDialog.setIcon(R.drawable.thali_graphic);
        alertDialog.setCancelable(false);

        LayoutInflater layoutInflater=getLayoutInflater();

        //Since i am using same layout for alertDialog .Hence the id will also same
        View view=layoutInflater.inflate(R.layout.weekly_menu_add_alert_dialog,null,false);
        weeklyFoodDescriptionEditText=view.findViewById(R.id.weeklyDescriptionEditText);
        weeklyFoodSelectButton=view.findViewById(R.id.weeklySelectButton);
        weeklyFoodUploadButton=view.findViewById(R.id.weeklyUploadButton);
        weeklyFoodNameEditText=view.findViewById(R.id.weeklyFoodNameEditText);
        weeklySpinner=view.findViewById(R.id.weeklyMenuSpinner);

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.foodType));
        weeklySpinner.setAdapter(arrayAdapter);


        weeklySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFoodType=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        weeklyFoodSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        weeklyFoodUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(foodMenu);
            }
        });
        alertDialog.setView(view);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Update the data
                //This below will set the new data on that key
                Log.i("weekly image url","--------------"+foodMenu.getImageUrl()+"  "+selectedFoodType);
                if(foodMenu.getImageUrl()!=null && selectedFoodType!=0) {
                    foodMenu.setFoodDescription(weeklyFoodDescriptionEditText.getText().toString());
                    foodMenu.setFoodName(weeklyFoodNameEditText.getText().toString());
                    todayMenuDatabaseReference.push().setValue(foodMenu);
                    Toast.makeText(context, foodMenu.getFoodName() + " Added", Toast.LENGTH_LONG).show();

                    switch(selectedFoodType){

                        case 1:
                            todayMenuDatabaseReference.child("BreakFast").push().setValue(foodMenu);
                            breakFastAdapter.notifyDataSetChanged();
                            break;


                        case 2:
                            todayMenuDatabaseReference.child("Lunch").push().setValue(foodMenu);
                            lunchAdapter.notifyDataSetChanged();
                            break;


                        case 3:
                            todayMenuDatabaseReference.child("Dinner").push().setValue(foodMenu);
                            dinnerAdapter.notifyDataSetChanged();
                            break;
                    }
                    dialogInterface.dismiss();

                }else if(selectedFoodType==0) {
                    Toast.makeText(context,"Please select the food type",Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(context,"Please first upload the image",Toast.LENGTH_LONG).show();
//                    alertDialog.show();
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


    private void deleteAlertDialog(final String key, final FirebaseRecyclerAdapter<FoodMenu,FoodMenuViewHolder> adapter) {

        AlertDialog.Builder alertDialg=new AlertDialog.Builder(context);
        alertDialg.setTitle("Delete the food");
        alertDialg.setMessage("Do you really want to delete it ?");
        alertDialg.setIcon(R.drawable.thali_graphic);
        alertDialg.setCancelable(false);

        alertDialg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFood(key,adapter);
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

    private void deleteFood(String key,FirebaseRecyclerAdapter<FoodMenu,FoodMenuViewHolder> adapter) {
        todayMenuDatabaseReference.child(chooseFoodType).child(key).removeValue();
        Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

    private void showEditAlertDialog(final String key, final FoodMenu foodMenu, final FirebaseRecyclerAdapter<FoodMenu,FoodMenuViewHolder> adapter) {

//        Log.i("key","------------------"+key);
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setTitle("Edit the "+chooseFoodType+" food menu details");
        alertDialog.setIcon(R.drawable.thali_graphic);
        LayoutInflater layoutInflater=getLayoutInflater();
        alertDialog.setCancelable(false);

        View view=layoutInflater.inflate(R.layout.weekly_menu_add_alert_dialog,null,false);
        weeklyFoodDescriptionEditText=view.findViewById(R.id.weeklyDescriptionEditText);
        weeklyFoodSelectButton=view.findViewById(R.id.weeklySelectButton);
        weeklyFoodUploadButton=view.findViewById(R.id.weeklyUploadButton);
        weeklyFoodNameEditText=view.findViewById(R.id.weeklyFoodNameEditText);
        weeklySpinner=view.findViewById(R.id.weeklyMenuSpinner);

        weeklySpinner.setVisibility(View.GONE);//setting visibility to GONE ensure that your view will not take any space

        //setting already exist food details onto edittext
        weeklyFoodDescriptionEditText.setText(foodMenu.getFoodDescription());
        weeklyFoodNameEditText.setText(foodMenu.getFoodName());


        weeklyFoodSelectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        weeklyFoodUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage(foodMenu);
            }
        });
        alertDialog.setView(view);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Update the data
                //This below will set the new data on that key
                foodMenu.setFoodName(weeklyFoodNameEditText.getText().toString());
                foodMenu.setFoodDescription(weeklyFoodDescriptionEditText.getText().toString());
                todayMenuDatabaseReference.child(chooseFoodType).child(key).setValue(foodMenu);
                Log.i("alerturl", "onSuccess: "+foodMenu.getImageUrl());
                Toast.makeText(context,foodMenu.getFoodName()+ " updated",Toast.LENGTH_LONG).show();
                adapter.notifyDataSetChanged();
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

    private void uploadImage(final FoodMenu foodMenu){
        if(imageUri!=null){
            final ProgressDialog progressDialog=new ProgressDialog(context);
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
                            progressDialog.dismiss();
                            foodMenu.setImageUrl(uri.toString());
                            Toast.makeText(context,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
                            Log.i("immageurl", "onSuccess: "+foodMenu.getImageUrl());
                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(context,e.getMessage(),Toast.LENGTH_SHORT).show();
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
            Toast.makeText(context,"Please first select the image",Toast.LENGTH_SHORT).show();
        }
    }

    private void chooseImage(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Picture"),Common.PICK_IMAGE_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==getActivity().RESULT_OK && data!=null){
            imageUri=data.getData();
            weeklyFoodSelectButton.setText("Image Selected");
        }
    }


    public static WeeklyMenuNestedFragment newInstance(String day) {

        Bundle args = new Bundle();
        args.putString("DAY",day);

       WeeklyMenuNestedFragment fragment = new WeeklyMenuNestedFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
