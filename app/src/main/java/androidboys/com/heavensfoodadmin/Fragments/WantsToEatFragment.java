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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.UUID;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Common.UsersUid;
import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.Models.FoodMenu;
import androidboys.com.heavensfoodadmin.Models.LikedFood;
import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.ViewHolders.WantsToEatViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class WantsToEatFragment extends Fragment implements View.OnCreateContextMenuListener {

    private RecyclerView wantsToEatRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private FirebaseRecyclerAdapter<FoodMenu, WantsToEatViewHolder> wantsToEatFoodAdapter;
    private DatabaseReference wantsToEatDatabaseReference;
    private int maxLimit;
    private ArrayList<String> foodChooseList;
    private ArrayList<Food> foodArrayList=new ArrayList<>();
    private ArrayList<String> foodNamesArrayList=new ArrayList<>();
    private ArrayList<String> foodItemUid=new ArrayList<>();
    private ArrayList<String> categoryList=new ArrayList<>();
    private int selectedCategory;
//    private Button wantAlertSelectButton;
//    private Button wantAlertUploadButton;
    private EditText categoryNameEditText;
    private Spinner foodItemSpinner;
//    private Uri imageUri;
    private int selectedFood;
    private Spinner categorySpinner;
    private CheckBox defaultCheckBox;
    private Boolean isDefault=false;
    private StorageReference storageReference;
    private EditText wantAlertFoodNameEditText;
    private CoordinatorLayout wantsToEatCoordinatorLayout;
    private FloatingActionButton wantsFloatingActionButton;
    private String mealTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.wants_to_eat_layout,container,false);
        fetchUserList();
        context=getContext();

        foodArrayList.add(new Food());
        foodNamesArrayList.add("Select Item");
        foodItemUid.add(" ");
        categoryList.add("Select Category");
        categoryList.add("Create New Category");
        fetchCategory();

        fetchAllFoodItems();



        wantsToEatRecyclerView=view.findViewById(R.id.wantsToEatRecyclerView);
        wantsToEatDatabaseReference=FirebaseDatabase.getInstance().getReference("TodayMenu");
        wantsToEatCoordinatorLayout=view.findViewById(R.id.wantsToEatCoordinatorLayout);
        wantsFloatingActionButton=view.findViewById(R.id.wantsFloatingActionButton);
        storageReference=FirebaseStorage.getInstance().getReference("images/");
        foodChooseList=new ArrayList<>();
        layoutManager=new LinearLayoutManager(context);
        wantsToEatRecyclerView.setHasFixedSize(true);
        wantsToEatRecyclerView.setLayoutManager(layoutManager);


        loadWantToEatImages();

        PullRefreshLayout wantsRefreshLayout=view.findViewById(R.id.wantsRefreshLayout);
        wantsRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadWantToEatImages();
            }
        });
        wantsRefreshLayout.setColor(R.color.colorPrimary);//set the color of refresh circle.

        //wantsSubmitButton.setButtonColor(getActivity().getResources().getColor(R.color.colorPrimary));

        wantsFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewAlertDialog();
            }
        });
        return view;
    }

    private void fetchCategory() {
    FirebaseDatabase.getInstance().getReference("TodayMenu").child("Category").addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            categoryList.add(dataSnapshot.getKey());
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


    private void fetchUserList() {
    FirebaseDatabase.getInstance().getReference("Users").addChildEventListener(new ChildEventListener() {
        @Override
        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            UsersUid.usersUid.add(dataSnapshot.getKey());
            Log.d("key",dataSnapshot.getKey());
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

    private void showNewAlertDialog() {

        final SpecialFood specialFood=new SpecialFood();

        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setTitle("Enter new food details");
        alertDialog.setIcon(R.drawable.thali_graphic);
        alertDialog.setCancelable(false);

        LayoutInflater layoutInflater=getLayoutInflater();

        //Since i am using same layout for alertDialog .Hence the id will also same
        View view=layoutInflater.inflate(R.layout.wants_to_eat_category,null,false);
        foodItemSpinner=view.findViewById(R.id.foodItemSpinner);
        defaultCheckBox=view.findViewById(R.id.defaultCheckBox);
        categorySpinner=view.findViewById(R.id.categorySpinner);
        categoryNameEditText=view.findViewById(R.id.categotyEditText);
        final ArrayAdapter<String> categoryNameAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,categoryList);
        categorySpinner.setAdapter(categoryNameAdapter);
        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==1) {
                    categoryNameEditText.setVisibility(View.VISIBLE);
                    categoryNameEditText.requestFocus();

                }
                    selectedCategory=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

        alertDialog.setView(view);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Update the data
                //This below will set the new data on that key
                if (selectedCategory == 0)
                    Toast.makeText(context, "Please select a category first", Toast.LENGTH_SHORT).show();

                else {
                    if (selectedCategory == 1) {
                        if (!categoryNameEditText.getText().toString().trim().equals("")) {
                            wantsToEatDatabaseReference.child("Category").child(categoryNameEditText.getText().toString()).push().setValue(foodArrayList.get(selectedFood));
                        } else
                            Toast.makeText(context, "Please enter the category first", Toast.LENGTH_SHORT).show();

                    } else {
                        wantsToEatDatabaseReference.child("Category").child(categoryList.get(selectedCategory)).push().setValue(foodArrayList.get(selectedFood));
                    }

                    if (defaultCheckBox.isChecked()) ;
                    isDefault = defaultCheckBox.isChecked();
                    Log.d("checkedfdfdfdsf", "************" + defaultCheckBox.isChecked());

                    addFoodForAllUser(foodArrayList.get(selectedFood)); //whenever admin upload  a new food add this food for all user if it contain isDefalut=true

                    Snackbar.make(wantsToEatCoordinatorLayout, foodArrayList.get(selectedFood).getFoodName() + " Added", Snackbar.LENGTH_LONG).show();
                    wantsToEatFoodAdapter.notifyDataSetChanged();
                    dialogInterface.dismiss();

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

    private void addFoodForAllUser(Food food) {
        if (isDefault) {
            LikedFood likedFood = new LikedFood(food.getFoodName(), isDefault);
            for(int i = 0; i<UsersUid.usersUid.size(); i++)
                wantsToEatDatabaseReference.child("LikedFood").child(likedFood.getFoodName()).push().setValue(UsersUid.usersUid.get(i));
        }
    }


    public void loadWantToEatImages() {


        DatabaseReference databaseReference=wantsToEatDatabaseReference.child("FoodImages");
//        wantsToEatDatabaseReference.child("maxLimit").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if(dataSnapshot!=null){
//                    maxLimit=dataSnapshot.getValue(Integer.class);
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        wantsToEatFoodAdapter=new FirebaseRecyclerAdapter<FoodMenu, WantsToEatViewHolder>(FoodMenu.class,
                R.layout.wants_to_eat_raw_layout,WantsToEatViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(WantsToEatViewHolder wantsToEatViewHolder, final FoodMenu foodMenu, int i) {
                wantsToEatViewHolder.wantsFoodNameTextView.setText(foodMenu.getFoodName());
                wantsToEatViewHolder.wantsFoodDescriptionTextView.setText(foodMenu.getFoodDescription());
                Picasso.with(context).load(foodMenu.getImageUrl()).into(wantsToEatViewHolder.wantsFoodImageView);
            }
        };
        wantsToEatRecyclerView.setAdapter(wantsToEatFoodAdapter);
        //
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

//        if(item.getTitle().equals(Common.EDIT)){
//            showEditAlertDialog(wantsToEatFoodAdapter.getRef(item.getOrder()).getKey(),wantsToEatFoodAdapter.getItem(item.getOrder()));
//        }
//        else
          if(item.getTitle().equals(Common.DELETE)){
            //delete the data from the database
            deleteAlertDialog(item);
        }
        return super.onContextItemSelected(item);
    }

    private void deleteAlertDialog(final MenuItem item) {
        AlertDialog.Builder alertDialg=new AlertDialog.Builder(context);
        alertDialg.setTitle("Delete the food");
        alertDialg.setMessage("Do you really want to delete it ?");
        alertDialg.setIcon(R.drawable.thali_graphic);
        alertDialg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFood(wantsToEatFoodAdapter.getRef(item.getOrder()).getKey());
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

    private void deleteFood(String key) {
        wantsToEatDatabaseReference.child("FoodImages").child(key).removeValue();
        Toast.makeText(context,"Deleted Successfully",Toast.LENGTH_SHORT).show();
        wantsToEatFoodAdapter.notifyDataSetChanged();
    }

//    private void showEditAlertDialog(final String key, final FoodMenu foodMenu) {
//
//        Log.i("key","------------------"+key);
//        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
//        alertDialog.setCancelable(false);
//
//        alertDialog.setTitle("Edit the food details");
//        alertDialog.setIcon(R.drawable.thali_graphic);
//        LayoutInflater layoutInflater=getLayoutInflater();
//        View view=layoutInflater.inflate(R.layout.food_edit_alert_dialog,null,false);
//        wantAlertDescriptionEditText=view.findViewById(R.id.alertDescriptionEditText);
//        wantAlertSelectButton=view.findViewById(R.id.alertSelectButton);
//        wantAlertUploadButton=view.findViewById(R.id.alertUploadButton);
//        wantAlertFoodNameEditText=view.findViewById(R.id.alertFoodNameEditText);
//
//        //setting already exist food details onto edittext
//        wantAlertFoodNameEditText.setText(foodMenu.getFoodName());
//        wantAlertDescriptionEditText.setText(foodMenu.getFoodDescription());
//
//
//        wantAlertSelectButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseImage();
//            }
//        });
//        wantAlertUploadButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                 uploadImage(foodMenu);
//            }
//        });
//        alertDialog.setView(view);
//
//        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                //Update the data
//                //This below will set the new data on that key
//                foodMenu.setFoodDescription(wantAlertDescriptionEditText.getText().toString());
//                foodMenu.setFoodName(wantAlertFoodNameEditText.getText().toString());
//                Log.i("insidurl:",foodMenu.getImageUrl());
//                wantsToEatDatabaseReference.child(mealTime).child(key).setValue(foodMenu);
//                Snackbar.make(wantsToEatCoordinatorLayout,foodMenu.getFoodName()+ " updated",Snackbar.LENGTH_LONG).show();
//                wantsToEatFoodAdapter.notifyDataSetChanged();
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
//
//    private void uploadImage(final FoodMenu foodMenu){
//        if(imageUri!=null){
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
//                            foodMenu.setImageUrl(uri.toString());
//                            Log.i("imageurl:",foodMenu.getImageUrl());
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
//            wantAlertSelectButton.setText("Image Selected");
//        }
//    }

    public static WantsToEatFragment newInstance() {
        
        Bundle args = new Bundle();

        WantsToEatFragment fragment = new WantsToEatFragment();
        fragment.setArguments(args);
        return fragment;
    }
}
