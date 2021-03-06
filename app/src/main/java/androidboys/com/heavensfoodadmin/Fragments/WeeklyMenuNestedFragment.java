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
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

import androidboys.com.heavensfoodadmin.Activities.DescriptionActivity;
import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Common.FirebaseStorageDeletion;
import androidboys.com.heavensfoodadmin.Models.Food;
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
    private String day,fullNameOfDay;

    private DatabaseReference todayMenuDatabaseReference;
    private FloatingActionButton weeklyMenuFloatingActionButton;
//    private EditText weeklyFoodDescriptionEditText;
//    private EditText weeklyFoodNameEditText;
//    private Button weeklyFoodSelectButton;
//    private Button weeklyFoodUploadButton;
    private Uri imageUri;
    private ArrayList<String> foodNamesArrayList=new ArrayList();
    private ArrayList<Food> foodArrayList=new ArrayList<>();
    private ArrayList<String> foodItemUid=new ArrayList<>();
    private StorageReference storageReference;
    private Spinner weeklySpinner,foodItemSpinner;
    private int selectedFoodType;
    private int selectedFood;//This is used in spinner when new food will be added
    private CheckBox weeklyBreakfastCheckBox;
    private CheckBox weeklyLunchCheckBox;
    private CheckBox weeklyDinnerCheckBox;
    private String chooseFoodType; //This is used when we edited the food
    private Activity activity;
    private ProgressBar breakFastProgressBar,lunchProgressBar,dinnerProgressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.weekly_menu_nested_fragment,container,false);
        activity=getActivity();
        breakFastRecyclerView=view.findViewById(R.id.breakFastRecyclerView);
        lunchRecyclerView=view.findViewById(R.id.lunchRecyclerView);
        dinnerRecyclerView=view.findViewById(R.id.dinnerRecyclerView);
        foodArrayList.add(new Food());
        foodNamesArrayList.add("Select Item");
        foodItemUid.add(" ");
        breakFastProgressBar=view.findViewById(R.id.breakFastProgressBar);
        lunchProgressBar=view.findViewById(R.id.lunchProgressBar);
        dinnerProgressBar=view.findViewById(R.id.dinnerProgressBar);

        fetchAllFoodItems();

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
        fullNameOfDay=getFullNameOfDay(day);

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
                    View newView=weeklyDinnerCheckBox.getRootView();
                    View newView1=weeklyLunchCheckBox.getRootView();
                    unregisterForContextMenu(newView1);
                    unregisterForContextMenu(newView);

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

        registerForContextMenu(view);

        return view;
    }

    private String getFullNameOfDay(String day) {
        switch (day){
            case "Mon":
                return "Monday";
            case "Tues":
                return "Tuesday";
            case "Wed":
                return "Wednesday";
            case "Thrus":
                return "Thrusday";
            case "Fri":
                return "Friday";
            case "Sat":
                return "Saturday";
            case "Sun":
                return "Sunday";
        }
        return null;
    }

    private void fetchAllFoodItems() {
    FirebaseDatabase.getInstance().getReference("FoodItems").addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            Food food=dataSnapshot.getValue(Food.class);
            foodArrayList.add(food);
            foodNamesArrayList.add(food.getFoodName());
            Log.d("key",dataSnapshot.getKey());
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
                if(dinnerProgressBar!=null){
                    dinnerProgressBar.setVisibility(View.GONE);
                }

            }
        };
        dinnerAdapter.notifyDataSetChanged();
        dinnerRecyclerView.setAdapter(dinnerAdapter);
        //Download the images from the firebase
    }

    private void setFoodDetails(final FoodMenuViewHolder foodMenuViewHolder, FoodMenu foodMenu) {
        foodMenuViewHolder.foodNameTextView.setText(foodMenu.getFoodName());
        foodMenuViewHolder.foodDescriptionTextView.setText(foodMenu.getFoodDescription());
        Picasso.with(context).load(foodMenu.getImageUrl()).into(foodMenuViewHolder.foodImageView, new Callback() {
            @Override
            public void onSuccess() {
                if(foodMenuViewHolder.imageProgressBar!=null) {
                    foodMenuViewHolder.imageProgressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError() {

            }
        });

    }

    private void showlunchImages() {
        DatabaseReference databaseReference=todayMenuDatabaseReference.child("Lunch");
        lunchAdapter=new FirebaseRecyclerAdapter<FoodMenu, FoodMenuViewHolder>(
                FoodMenu.class,R.layout.food_menu_row_layout,FoodMenuViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(FoodMenuViewHolder foodMenuViewHolder, FoodMenu foodMenu, int i) {
                setFoodDetails(foodMenuViewHolder, foodMenu);
                if(lunchProgressBar!=null){
                    lunchProgressBar.setVisibility(View.GONE);
                }

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
                if(breakFastProgressBar!=null){
                    breakFastProgressBar.setVisibility(View.GONE);
                }
            }
        };
        breakFastAdapter.notifyDataSetChanged();
        breakFastRecyclerView.setAdapter(breakFastAdapter);
        //Download the images from the firebase
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(chooseFoodType!=null) {
//
//            if (item.getTitle().equals(Common.EDIT)) {
//
//                switch (chooseFoodType) {
//
//                    case "BreakFast":
//                        try {
//                            showEditAlertDialog(breakFastAdapter.getRef(item.getOrder()).getKey(), breakFastAdapter.getItem(item.getOrder()), breakFastAdapter);
//                        }catch (Exception e){
//                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//
//                    case "Lunch":
//                        try {
//                            showEditAlertDialog(lunchAdapter.getRef(item.getOrder()).getKey(), lunchAdapter.getItem(item.getOrder()), lunchAdapter);
//                        }catch(Exception e){
//                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//
//                    default:
//                        try{
//                        showEditAlertDialog(dinnerAdapter.getRef(item.getOrder()).getKey(), dinnerAdapter.getItem(item.getOrder()), dinnerAdapter);
//                        }catch(Exception e){
//                               Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
//                        }
//                        break;
//
//                }
//            } else {

                switch (chooseFoodType){

                    case "BreakFast":
                        try{
                        deleteAlertDialog(breakFastAdapter.getItem(item.getOrder()),breakFastAdapter.getRef(item.getOrder()).getKey(),breakFastAdapter);
                        }catch(Exception e){
                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
                        }break;

                    case "Lunch":
                        try{
                        deleteAlertDialog(lunchAdapter.getItem(item.getOrder()),lunchAdapter.getRef(item.getOrder()).getKey(),lunchAdapter);
                        }catch(Exception e){
                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
                        }break;

                    default:
                        try {
                            deleteAlertDialog(dinnerAdapter.getItem(item.getOrder()),dinnerAdapter.getRef(item.getOrder()).getKey(), dinnerAdapter);
                        }catch(Exception e){
                            Toast.makeText(context,"Please select that food item which you have checked",Toast.LENGTH_SHORT).show();
                        }
                        break;
                }
            }
        else{
            Toast.makeText(context,"Please first select the food type",Toast.LENGTH_SHORT).show();
        }
        return true;

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
//        weeklyFoodDescriptionEditText=view.findViewById(R.id.weeklyDescriptionEditText);
//      weeklyFoodSelectButton=view.findViewById(R.id.weeklySelectButton);
//        weeklyFoodUploadButton=view.findViewById(R.id.weeklyUploadButton);
//        weeklyFoodNameEditText=view.findViewById(R.id.weeklyFoodNameEditText);
        weeklySpinner=view.findViewById(R.id.weeklyMenuSpinner);
        foodItemSpinner=view.findViewById(R.id.itemSpinner);

        ArrayAdapter<String> arrayAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.foodType));
        weeklySpinner.setAdapter(arrayAdapter);

        ArrayAdapter<String> foodNameArrayAdpter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,foodNamesArrayList);
        foodItemSpinner.setAdapter(foodNameArrayAdpter);

        weeklySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFoodType=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        foodItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedFood=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




//

//        weeklyFoodUploadButton.setOnOurCustomClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
////                uploadImage(foodMenu);
//
//            }
//        });
        alertDialog.setView(view);

        alertDialog.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                    switch(selectedFoodType){
                        case 0:
                            Toast.makeText(context,"Please select the food type",Toast.LENGTH_LONG).show();
                            break;



                        case 1:
                            todayMenuDatabaseReference.child("BreakFast").child(foodItemUid.get(selectedFood)).setValue(foodArrayList.get(selectedFood));
                            breakFastAdapter.notifyDataSetChanged();
                            break;


                        case 2:
                            todayMenuDatabaseReference.child("Lunch").child(foodItemUid.get(selectedFood)).setValue(foodArrayList.get(selectedFood));
                            lunchAdapter.notifyDataSetChanged();
                            break;


                        case 3:
                            todayMenuDatabaseReference.child("Dinner").child(foodItemUid.get(selectedFood)).setValue(foodArrayList.get(selectedFood));
                            dinnerAdapter.notifyDataSetChanged();
                            break;
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


    private void deleteAlertDialog(final FoodMenu foodMenu, final String key, final FirebaseRecyclerAdapter<FoodMenu,FoodMenuViewHolder> adapter) {

        AlertDialog.Builder alertDialg=new AlertDialog.Builder(context);
        alertDialg.setTitle("Delete the food");
        alertDialg.setMessage("Do you really want to delete it ?");
        alertDialg.setIcon(R.drawable.thali_graphic);
        alertDialg.setCancelable(false);

        alertDialg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFood(foodMenu,key,adapter);
                dialogInterface.dismiss();
                adapter.notifyDataSetChanged();
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

    private void deleteFood(FoodMenu foodMenu,String key,FirebaseRecyclerAdapter<FoodMenu,FoodMenuViewHolder> adapter) {
        todayMenuDatabaseReference.child(chooseFoodType).child(key).removeValue();
        Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show();
        try {
            FirebaseStorageDeletion.deleteFileFromStorage(foodMenu.getImageUrl(), context);
        }catch (Exception e){
            Toast.makeText(context,"This image url is not present in our server",Toast.LENGTH_SHORT).show();
        }
        adapter.notifyDataSetChanged();
    }

//    private void showEditAlertDialog(final String key, final FoodMenu foodMenu, final FirebaseRecyclerAdapter<FoodMenu,FoodMenuViewHolder> adapter) {
//
////        Log.i("key","------------------"+key);
//        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
//        alertDialog.setTitle("Edit the "+chooseFoodType+" food menu details");
//        alertDialog.setIcon(R.drawable.thali_graphic);
//        LayoutInflater layoutInflater=getLayoutInflater();
//        alertDialog.setCancelable(false);
//
//        View view=layoutInflater.inflate(R.layout.weekly_menu_add_alert_dialog,null,false);
//        weeklyFoodDescriptionEditText=view.findViewById(R.id.weeklyDescriptionEditText);
//        weeklyFoodSelectButton=view.findViewById(R.id.weeklySelectButton);
//        weeklyFoodUploadButton=view.findViewById(R.id.weeklyUploadButton);
//        weeklyFoodNameEditText=view.findViewById(R.id.weeklyFoodNameEditText);
//        weeklySpinner=view.findViewById(R.id.weeklyMenuSpinner);
//
//        weeklySpinner.setVisibility(View.GONE);//setting visibility to GONE ensure that your view will not take any space
//
//        //setting already exist food details onto edittext
//        weeklyFoodDescriptionEditText.setText(foodMenu.getFoodDescription());
//        weeklyFoodNameEditText.setText(foodMenu.getFoodName());
//
//
//        weeklyFoodSelectButton.setOnOurCustomClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseImage();
//            }
//        });
//        weeklyFoodUploadButton.setOnOurCustomClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadImage(foodMenu);
//            }
//        });
//        alertDialog.setView(view);
//
//        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                //Update the data
//                //This below will set the new data on that key
//                foodMenu.setFoodName(weeklyFoodNameEditText.getText().toString());
//                foodMenu.setFoodDescription(weeklyFoodDescriptionEditText.getText().toString());
//                todayMenuDatabaseReference.child(chooseFoodType).child(key).setValue(foodMenu);
//                Log.i("alerturl", "onSuccess: "+foodMenu.getImageUrl());
//                Toast.makeText(context,foodMenu.getFoodName()+ " updated",Toast.LENGTH_LONG).show();
//                adapter.notifyDataSetChanged();
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

//    private void uploadImage(final FoodMenu foodMenu){
//        if(imageUri!=null){
//
//
//            //if user edited image then we have to delete first previous image
//            try {
//                FirebaseStorageDeletion.deleteFileFromStorage(foodMenu.getImageUrl(), context);
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
//                            foodMenu.setImageUrl(uri.toString());
//                            Toast.makeText(context,"Uploaded Successfully",Toast.LENGTH_SHORT).show();
//                            Log.i("immageurl", "onSuccess: "+foodMenu.getImageUrl());
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


//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode==Common.PICK_IMAGE_REQUEST && resultCode==getActivity().RESULT_OK && data!=null){
//            imageUri=data.getData();
//            weeklyFoodSelectButton.setText("Image Selected");
//        }
//    }
//

    public static WeeklyMenuNestedFragment newInstance(String day) {

        Bundle args = new Bundle();
        args.putString("DAY",day);

       WeeklyMenuNestedFragment fragment = new WeeklyMenuNestedFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((DescriptionActivity)activity).setActionBarTitle(fullNameOfDay+" Food Menu");
    }
}
