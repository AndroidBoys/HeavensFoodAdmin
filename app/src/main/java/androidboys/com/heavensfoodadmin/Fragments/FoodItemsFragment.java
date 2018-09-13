package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.Models.FoodMenu;
import androidboys.com.heavensfoodadmin.ViewHolders.FoodViewHolder;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.UUID;

import androidboys.com.heavensfoodadmin.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FoodItemsFragment extends Fragment {

    private FirebaseRecyclerAdapter<Food, FoodViewHolder> adapter;
    private RecyclerView recyclerView;
    private Context hostingActivity;
    private DatabaseReference reference;
    private FloatingActionButton addFoodItemButton;
    private Uri imageUri;
    private UploadTask uploadTask;

    //Alert Dialog UI compnents
    private AlertDialog alertDialog;
    private EditText foodNameEditText;
    private EditText foodDescriptionEditText;
    private Button chooseImageButton;
    private Button uploadImageButton;

    public FoodItemsFragment() {
        // Required empty public constructor
    }

    public static FoodItemsFragment newInstance() {
        FoodItemsFragment fragment = new FoodItemsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        reference = FirebaseDatabase.getInstance().getReference("Fooditems");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_food_items, container, false);
        addFoodItemButton = view.findViewById(R.id.add_food_item);
        addFoodItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              addFoodItem();
            }
        });
        recyclerView=view.findViewById(R.id.food_items_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(hostingActivity));
        attachAdapter();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        hostingActivity=context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void attachAdapter(){

        adapter=new FirebaseRecyclerAdapter<Food, FoodViewHolder>(Food.class,R.layout.wants_to_eat_raw_layout
                ,FoodViewHolder.class,reference) {
            @Override
            protected void populateViewHolder(FoodViewHolder foodViewHolder, Food food, int i) {
                    foodViewHolder.foodDescriptionTextView.setText(food.getFoodDescription());
                    foodViewHolder.foodNameTextView.setText(food.getFoodName());
                    Picasso.with(hostingActivity).load(food.getImageUrl()).into(foodViewHolder.foodImageView);

            }
        };

        recyclerView.setAdapter(adapter);
    }

    private void addFoodItem(){
        showNewAlertDialog();
    }

    private void showNewAlertDialog() {


        alertDialog=new AlertDialog.Builder(hostingActivity).create();
        alertDialog.setTitle("Enter new food details");
        alertDialog.setIcon(R.drawable.thali_graphic);
        LayoutInflater layoutInflater=getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.food_edit_alert_dialog,null,false);

        foodDescriptionEditText=view.findViewById(R.id.alertDescriptionEditText);
        chooseImageButton=view.findViewById(R.id.alertSelectButton);
        uploadImageButton=view.findViewById(R.id.alertUploadButton);
        foodNameEditText=view.findViewById(R.id.alertFoodNameEditText);


        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });
        uploadImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

       alertDialog.setView(view);
//
//       alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                //Update the data
//                //This below will set the new data on that key
//                if(food.getImageUrl()!=null) {
//                    food.setFoodDescription(foodDescriptionEditText.getText().toString());
//                    food.setFoodName(foodNameEditText.getText().toString());
//                    reference.child(UUID.randomUUID().toString()).setValue(food);
//                    Toast.makeText(hostingActivity, food.getFoodName()+" added!", Toast.LENGTH_SHORT).show();
//                    adapter.notifyDataSetChanged();
//                    dialogInterface.dismiss();
//                }else{
//                    Toast.makeText(context,"Please first upload the image",Toast.LENGTH_SHORT).show();
////                    alertDialog.show();
//                }
//            }
//        });
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void uploadImage(){
        if(!isAnyFieldEmpty()){
            alertDialog.dismiss();
            final KProgressHUD hud = KProgressHUD.create(hostingActivity)
                    .setStyle(KProgressHUD.Style.ANNULAR_DETERMINATE)
                    .setLabel("Please wait")
                    .setMaxProgress(100)
//                    .setCancellable(new DialogInterface.OnCancelListener() {
//                        @Override
//                        public void onCancel(final DialogInterface dialogInterface1) {
//                            new AlertDialog.Builder(hostingActivity)
//                                             .setMessage("Cancel Adding Fooditem?")
//                                             .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                                                 @Override
//                                                 public void onClick(DialogInterface dialogInterface, int i) {
//                                                        if(uploadTask.isInProgress()&&uploadTask!=null){
//                                                            uploadTask.cancel();
//                                                            dialogInterface.dismiss();
//                                                            dialogInterface1.dismiss();
//                                                        }
//                                                 }
//                                             })
//                                             .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                                                 @Override
//                                                 public void onClick(DialogInterface dialogInterface, int i) {
//                                                     dialogInterface.dismiss();
//                                                 }
//                                             });
//                        }
//                    })
                    .setCancellable(false)
                    .show();


            String filename= UUID.randomUUID().toString();
            final StorageReference imageFolder=FirebaseStorage.getInstance().getReference("images/"+filename);
            Log.i("imageuri",imageUri.toString());



            uploadTask =imageFolder.putFile(imageUri);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {

                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    imageFolder.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Food food = new Food();
                            food.setImageUrl(uri.toString());
                            addFoodToDB(food);
                            hud.dismiss();
                            Toast.makeText(hostingActivity,"FoodItem added",Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            hud.dismiss();
                            Toast.makeText(hostingActivity,e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            int progress=(int)(100.0*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                            hud.setProgress(progress);
                        }
                    });
        }else{
            Toast.makeText(hostingActivity,"Fill the all mandatory fields!",Toast.LENGTH_SHORT).show();
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
            Log.i("imageuri", "onActivityResult: imageuri:"+imageUri);
            chooseImageButton.setText("Image Selected");
        }
    }

    private boolean isAnyFieldEmpty(){
        return imageUri==null||foodNameEditText.getText()==null||foodDescriptionEditText.getText()==null;
    }

    private void addFoodToDB(Food food){
        food.setFoodDescription(foodDescriptionEditText.getText().toString());
        food.setFoodName(foodNameEditText.getText().toString());
        reference.child(UUID.randomUUID().toString()).setValue(food);
        adapter.notifyDataSetChanged();

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

//        if(item.getTitle().equals(Common.EDIT)){
//            isEditMode=true;
//            showEditAlertDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
//        }
         if(item.getTitle().equals(Common.DELETE)){
            //delete the data from the database
            deleteAlertDialog(item);
        }
        return super.onContextItemSelected(item);
    }

    private void deleteAlertDialog(final MenuItem item) {
        AlertDialog.Builder alertDialg=new AlertDialog.Builder(hostingActivity);
        alertDialg.setTitle("Delete the food");
        alertDialg.setMessage("Do you really want to delete it ?");
        alertDialg.setIcon(R.drawable.thali_graphic);
        alertDialg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFood(adapter.getRef(item.getOrder()).getKey());
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
        reference.child(key).removeValue();
        Toast.makeText(hostingActivity,"Deleted Successfully",Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }

//    private void showEditAlertDialog(final String key, final Food food) {
//
//        Log.i("key","------------------"+key);
//        alertDialog=new AlertDialog.Builder(hostingActivity).create();
//        alertDialog.setCancelable(false);
//
//        alertDialog.setTitle("Edit the food details");
//        alertDialog.setIcon(R.drawable.thali_graphic);
//        LayoutInflater layoutInflater=getLayoutInflater();
//        View view=layoutInflater.inflate(R.layout.food_edit_alert_dialog,null,false);
//        foodDescriptionEditText=view.findViewById(R.id.alertDescriptionEditText);
//        chooseImageButton=view.findViewById(R.id.alertSelectButton);
//        uploadImageButton=view.findViewById(R.id.alertUploadButton);
//        foodNameEditText=view.findViewById(R.id.alertFoodNameEditText);
//
//        //setting already exist food details onto edittext
//        foodNameEditText.setText(food.getFoodName());
//        foodDescriptionEditText.setText(food.getFoodDescription());
//        imageUri=Uri.parse(food.getImageUrl());
//        Log.i("uri", "showEditAlertDialog: "+imageUri);
//        chooseImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                chooseImage();
//            }
//        });
//        uploadImageButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                uploadImage();
//            }
//        });
//        alertDialog.setView(view);
//        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialogInterface) {
//                dialogInterface.dismiss();
//            }
//        });
//        alertDialog.show();
//    }



}
