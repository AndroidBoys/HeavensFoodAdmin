package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

import androidboys.com.heavensfoodadmin.Adapters.ExpandableFoodListAdapter;
import androidboys.com.heavensfoodadmin.Adapters.WantsToEatCustomAdapterAfterCategorySelected;
import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Common.UsersUid;
import androidboys.com.heavensfoodadmin.Models.Category;
import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.Models.LikedFood;
import androidboys.com.heavensfoodadmin.Models.SpecialFood;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.ViewHolders.WantsToEatCategoryViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class WantsToEatFragment extends Fragment implements View.OnCreateContextMenuListener {

    private RecyclerView wantsToEatRecyclerView;
//    private ListView wantsToEatCategoryListView;
    private RecyclerView.LayoutManager layoutManager;
    private Context context;
    private FirebaseRecyclerAdapter<Category, WantsToEatCategoryViewHolder> wantsToEatFoodAdapter;
    private DatabaseReference wantsToEatDatabaseReference;
//    private int maxLimit;
    private ArrayList<Food> foodArrayList=new ArrayList<>();
    private ArrayList<Food> selectedFoodArrayList=new ArrayList<>();
    private ArrayList<String> foodNamesArrayList=new ArrayList<>();
    private ArrayList<String> foodItemUid=new ArrayList<>();
//    private ArrayList<Category> categoryList=new ArrayList<>();
    private ArrayList<String> categoryNameList=new ArrayList<>();
    private HashMap<String,ArrayList<Food>> listFoodChild=new HashMap<>();
    private ArrayList<Integer> maxLimitOfCategory=new ArrayList<>();
private   ExpandableFoodListAdapter expandableFoodListAdapter;
//    private int selectedCategory;
//    private Button wantAlertSelectButton;
//    private Button wantAlertUploadButton;
    private EditText categoryNameEditText;
    private EditText maxLimitEditText;
    private Spinner timeSpinner;
    private ListView foodItemList;
//    private Uri imageUri;
    private int selectedTime;
//    private Spinner categorySpinner;
//    private CheckBox defaultCheckBox;
    private Boolean isDefault=false;
    private ExpandableListView expandableListView;
    private StorageReference storageReference;
    private EditText wantAlertFoodNameEditText;
    private CoordinatorLayout wantsToEatCoordinatorLayout;
    private FloatingActionButton wantsFloatingActionButton;
    private String mealTime;
    private ArrayAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.wants_to_eat_layout,container,false);
        fetchUserList();
        context=getContext();


        fetchAllFoodItems();

        mealTime="BreakFast";

        wantsToEatDatabaseReference=FirebaseDatabase.getInstance().getReference("TodayMenu");
        wantsToEatCoordinatorLayout=view.findViewById(R.id.wantsToEatCoordinatorLayout);
        wantsFloatingActionButton=view.findViewById(R.id.wantsFloatingActionButton);
        storageReference=FirebaseStorage.getInstance().getReference("images/");
        loadWantToEatImages(mealTime);

        PullRefreshLayout wantsRefreshLayout=view.findViewById(R.id.wantsRefreshLayout);
        wantsRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadWantToEatImages(mealTime);
            }
        });
        wantsRefreshLayout.setColor(R.color.colorPrimary);//set the color of refresh circle.

//        wantsSubmitButton.setButtonColor(getActivity().getResources().getColor(R.color.colorPrimary));

        wantsFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showNewAlertDialog();
            }
        });

        expandableListView=view.findViewById(R.id.wantsToEatExpandableListView);
        expandableFoodListAdapter=new ExpandableFoodListAdapter(context,categoryNameList,listFoodChild);
        expandableListView.setAdapter(expandableFoodListAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupIndex, int childIndex, long l) {
                Toast.makeText(context, "group "+groupIndex+"and child :"+childIndex, Toast.LENGTH_SHORT).show();
                CheckedTextView checkedTextView=view.findViewById(R.id.lblListItem);
                checkedTextView.toggle();
                if(checkedTextView.isChecked()){
                    Toast.makeText(context, "checked", Toast.LENGTH_SHORT).show();
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_outline_blank_black_24dp);
                }
                else{
                    checkedTextView.setCheckMarkDrawable(R.drawable.ic_check_box_black_24dp);
                    Toast.makeText(context, "unchecked", Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        });



        //        wantsToEatRecyclerView=view.findViewById(R.id.wantsToEatRecyclerView);
//        wantsToEatCategoryListView=view.findViewById(R.id.wantsToEatListView);
//        adapter=new ArrayAdapter(context,android.R.layout.simple_list_item_1,categoryNameList);
//        wantsToEatCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//            }
//        });



//        foodChooseList=new ArrayList<>();
//        layoutManager=new LinearLayoutManager(context);
//        wantsToEatRecyclerView.setHasFixedSize(true);
//        wantsToEatRecyclerView.setLayoutManager(layoutManager);




        return view;
    }

//    private void fetchCategory(String mealTime) {
//        categoryList.clear();
//        categoryNameList.clear();
//    wantsToEatDatabaseReference.child(mealTime).addChildEventListener(new ChildEventListener() {
//        @Override
//        public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//            Category category=dataSnapshot.getValue(Category.class);
//            categoryList.add(category);
//            categoryNameList.add(category.getCategoryName());
//            if(adapter!=null)
//                adapter.notifyDataSetChanged();
//        }
//
//        @Override
//        public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//        }
//
//        @Override
//        public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
//
//        }
//
//        @Override
//        public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
//
//        }
//
//        @Override
//        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//        }
//    });
//
//    }

    private void fetchAllFoodItems(){
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
//        foodItemSpinner=view.findViewById(R.id.foodItemSpinner);
//        defaultCheckBox=view.findViewById(R.id.defaultCheckBox);
//        categorySpinner=view.findViewById(R.id.categorySpinner);
        maxLimitEditText=view.findViewById(R.id.maxSelectedEditText);
        foodItemList=view.findViewById(R.id.itemList);
        categoryNameEditText=view.findViewById(R.id.categotyEditText);
        timeSpinner=view.findViewById(R.id.timeSpinner);
        ArrayAdapter listAdapter=new ArrayAdapter(context,android.R.layout.simple_list_item_1,foodNamesArrayList);
        foodItemList.setAdapter(listAdapter);
        foodItemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
               if(selectedFoodArrayList.contains(foodArrayList.get(i))){
                   Toast.makeText(context, ""+foodNamesArrayList.get(i)+" is deselected", Toast.LENGTH_SHORT).show();
                   selectedFoodArrayList.remove(foodArrayList.get(i));
               }
               else{
                   Toast.makeText(context, ""+foodNamesArrayList.get(i)+" added", Toast.LENGTH_SHORT).show();
                   selectedFoodArrayList.add(foodArrayList.get(i));
               }
            }
        });
        ArrayAdapter<String> timeArrayAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,getResources().getStringArray(R.array.foodType));
        timeSpinner.setAdapter(timeArrayAdapter);
        timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedTime=i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
//        final ArrayAdapter<String> categoryNameAdapter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,categoryList);
//        categorySpinner.setAdapter(categoryNameAdapter);
//        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                if(i==1) {
//                    categoryNameEditText.setVisibility(View.VISIBLE);
//                    categoryNameEditText.requestFocus();
//
//                }
//                    selectedCategory=i;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

//        ArrayAdapter<String> foodNameArrayAdpter=new ArrayAdapter(context,android.R.layout.simple_spinner_dropdown_item,foodNamesArrayList);
//        foodItemSpinner.setAdapter(foodNameArrayAdpter);
//        foodItemSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                selectedFood=i;
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });

        alertDialog.setView(view);

        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Update the data
                //This below will set the new data on that key
                if (isAnyEmpty())
                    Toast.makeText(context, "Please fill the required field first", Toast.LENGTH_SHORT).show();

                else {
//                    if (selectedCategory == 1) {
//                        if (!categoryNameEditText.getText().toString().trim().equals("")) {
                    Category category=new Category(selectedFoodArrayList,categoryNameEditText.getText().toString(),Integer.parseInt(maxLimitEditText.getText().toString()));
                            wantsToEatDatabaseReference.child(getResources().getStringArray(R.array.foodType)[selectedTime]).push().setValue(category);
//                        } else
//                            Toast.makeText(context, "Please enter the category first", Toast.LENGTH_SHORT).show();
//
//                    } else {
//                        wantsToEatDatabaseReference.child("Category").child(categoryList.get(selectedCategory)).push().setValue(foodArrayList.get(selectedFood));
//                    }

//                    if (defaultCheckBox.isChecked());
//                    isDefault = defaultCheckBox.isChecked();
//                    Log.d("checkedfdfdfdsf", "************" + defaultCheckBox.isChecked());

//                    addFoodForAllUser(foodArrayList.get(selectedFood)); //whenever admin upload  a new food add this food for all user if it contain isDefalut=true

                    Snackbar.make(wantsToEatCoordinatorLayout, categoryNameEditText.getText().toString() + " Added", Snackbar.LENGTH_LONG).show();
//                    wantsToEatFoodAdapter.notifyDataSetChanged();

                    dialogInterface.dismiss();

                }
                selectedFoodArrayList.clear();

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

    private boolean isAnyEmpty() {
    if(selectedFoodArrayList.isEmpty()||categoryNameEditText.getText().toString().trim().equals("")||maxLimitEditText.getText().toString().trim().equals("")||selectedTime==0)
        return true;
    else
        return false;

    }

    private void addFoodForAllUser(Food food) {
        if (isDefault) {
            LikedFood likedFood = new LikedFood(food.getFoodName(), isDefault);
            for(int i = 0; i<UsersUid.usersUid.size(); i++)
                wantsToEatDatabaseReference.child("LikedFood").child(likedFood.getFoodName()).push().setValue(UsersUid.usersUid.get(i));
        }
    }


    public void loadWantToEatImages(String mealTime) {
        categoryNameList.clear();
        maxLimitOfCategory.clear();
        listFoodChild.clear();
        wantsToEatDatabaseReference.child(mealTime).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Category category=dataSnapshot.getValue(Category.class);
                categoryNameList.add(" "+category.getCategoryName()+"   (Limit :"+category.getMaxSelect()+")");
                maxLimitOfCategory.add(category.getMaxSelect());
                listFoodChild.put(" "+category.getCategoryName()+"   (Limit :"+category.getMaxSelect()+")",category.getFoodArrayList());
                expandableFoodListAdapter.notifyDataSetChanged();

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

//        wantsToEatFoodAdapter=new FirebaseRecyclerAdapter<Category, WantsToEatCategoryViewHolder>(Category.class,
//                R.layout.wants_to_eat_category_raw_layout,WantsToEatCategoryViewHolder.class,databaseReference) {
//            @Override
//            protected void populateViewHolder(WantsToEatCategoryViewHolder wantsToEatCategoryViewHolder, final Category category, int i) {
////                wantsToEatViewHolder.wantsFoodNameTextView.setText(foodMenu.getFoodName());
////                wantsToEatViewHolder.wantsFoodDescriptionTextView.setText(foodMenu.getFoodDescription());
////                Picasso.with(context).load(foodMenu.getImageUrl()).into(wantsToEatViewHolder.wantsFoodImageView);
//                  wantsToEatCategoryViewHolder.wantsToEatCategoryTextView.setText(category.getCategoryName());
//                  WantsToEatCustomAdapterAfterCategorySelected wantsToEatCustomAdapterAfterCategorySelected=new WantsToEatCustomAdapterAfterCategorySelected(context,category.getFoodArrayList(),category.getMaxSelect());
//                  wantsToEatCategoryViewHolder.foodListView.setAdapter(wantsToEatCustomAdapterAfterCategorySelected);
//                  wantsToEatCategoryViewHolder.foodListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                      @Override
//                      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                          if(view.getId()==R.id.checkBox1){
//                              Toast.makeText(context, "checked", Toast.LENGTH_SHORT).show();
//                          }
//                      }
//                  });
//
//
//            }
//        };
//
//        wantsToEatRecyclerView.setAdapter(wantsToEatFoodAdapter);
        //
    }

//    private void showChoices(Category category) {
//
//
//         AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
//        alertDialog.setTitle("Select foods");
//        alertDialog.setIcon(R.drawable.thali_graphic);
//        alertDialog.setCancelable(false);
//
//        LayoutInflater layoutInflater=getLayoutInflater();
//
//        //Since i am using same layout for alertDialog .Hence the id will also same
//        View view=layoutInflater.inflate(R.layout.wants_to_eat_layout_listview,null,false);
//        ListView listView=view.findViewById(R.id.wantsToEatListView);
//        WantsToEatCustomAdapterAfterCategorySelected wantsToEatCustomAdapterAfterCategorySelected=new WantsToEatCustomAdapterAfterCategorySelected(context,category.getFoodArrayList(),category.getMaxSelect());
//        listView.setAdapter(wantsToEatCustomAdapterAfterCategorySelected);
//
//        alertDialog.setView(view);
//
//        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                //Update the data
//                //This below will set the new data on that key
//                if (isAnyEmpty())
//                    Toast.makeText(context, "Please fill the required field first", Toast.LENGTH_SHORT).show();
//
//                else {
////                    if (selectedCategory == 1) {
////                        if (!categoryNameEditText.getText().toString().trim().equals("")) {
//                    Category category=new Category(selectedFoodArrayList,categoryNameEditText.getText().toString(),Integer.parseInt(maxLimitEditText.getText().toString()));
//                    wantsToEatDatabaseReference.child(getResources().getStringArray(R.array.foodType)[selectedTime]).push().setValue(category);
////                        } else
////                            Toast.makeText(context, "Please enter the category first", Toast.LENGTH_SHORT).show();
////
////                    } else {
////                        wantsToEatDatabaseReference.child("Category").child(categoryList.get(selectedCategory)).push().setValue(foodArrayList.get(selectedFood));
////                    }
//
////                    if (defaultCheckBox.isChecked());
////                    isDefault = defaultCheckBox.isChecked();
////                    Log.d("checkedfdfdfdsf", "************" + defaultCheckBox.isChecked());
//
////                    addFoodForAllUser(foodArrayList.get(selectedFood)); //whenever admin upload  a new food add this food for all user if it contain isDefalut=true
//
//                    Snackbar.make(wantsToEatCoordinatorLayout, categoryNameEditText.getText().toString() + " Added", Snackbar.LENGTH_LONG).show();
////                    wantsToEatFoodAdapter.notifyDataSetChanged();
//
//                    dialogInterface.dismiss();
//
//                }
//                selectedFoodArrayList.clear();
//
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
//
//
//
//
//
//
//
//
//
//
//    }

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
