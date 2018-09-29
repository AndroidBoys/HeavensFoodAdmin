package androidboys.com.heavensfoodadmin.Fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidboys.com.heavensfoodadmin.Activities.HomeActivity;
import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Models.Food;
import androidboys.com.heavensfoodadmin.Models.WhyHeavenFood;
import androidboys.com.heavensfoodadmin.Utils.FileUtil;
import androidboys.com.heavensfoodadmin.Utils.FileUtil1;
import androidboys.com.heavensfoodadmin.Utils.ImageUtil;
import androidboys.com.heavensfoodadmin.Utils.ProgressUtils;
import androidboys.com.heavensfoodadmin.ViewHolders.FoodItemViewHolder;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

import androidboys.com.heavensfoodadmin.R;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import id.zelory.compressor.Compressor;

import static android.net.Uri.parse;

public class FoodItemsFragment extends Fragment {

    private FirebaseRecyclerAdapter<Food, FoodItemViewHolder> adapter;
    private RecyclerView recyclerView;
    private Context hostingActivity;
    private DatabaseReference reference;
    private FloatingActionButton addFoodItemButton;
    private Uri imageUri=null;
    private UploadTask uploadTask;

    //Alert Dialog UI compnents
    private AlertDialog alertDialog;
    private EditText foodNameEditText;
    private EditText foodDescriptionEditText;
    private Button chooseImageButton;
    private Button uploadImageButton;
    private Activity activity;

    public FoodItemsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        reference = FirebaseDatabase.getInstance().getReference("FoodItems");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_food_items, container, false);
        activity=getActivity();
        addFoodItemButton = view.findViewById(R.id.add_food_item);
        addFoodItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              addFoodItem();
            }
        });
        recyclerView=view.findViewById(R.id.food_items_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(hostingActivity));
        recyclerView.setHasFixedSize(true);
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

        adapter=new FirebaseRecyclerAdapter<Food, FoodItemViewHolder>(Food.class,R.layout.wants_to_eat_raw_layout
                ,FoodItemViewHolder.class,reference) {
            @Override
            protected void populateViewHolder(final FoodItemViewHolder foodItemViewHolder, Food food, int i) {
                    foodItemViewHolder.foodDescriptionTextView.setText(food.getFoodDescription());
                    foodItemViewHolder.foodNameTextView.setText(food.getFoodName());
                    Picasso.with(hostingActivity).load(food.getImageUrl()).into(foodItemViewHolder.foodImageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            if(foodItemViewHolder.imageProgressBar!=null){
                                foodItemViewHolder.imageProgressBar.setVisibility(View.GONE);
                            }
                        }

                        @Override
                        public void onError() {

                        }
                    });

            }
        };

        recyclerView.setAdapter(adapter);
    }

    private void addFoodItem(){
        showNewAlertDialog();
    }

    private void showNewAlertDialog() {


        imageUri=null; //since we are using same Uri variable for editing and adding a new item

        alertDialog=new AlertDialog.Builder(hostingActivity).create();
        alertDialog.setTitle("Enter new food details");
        alertDialog.setIcon(R.drawable.thali_graphic);
        LayoutInflater layoutInflater=getLayoutInflater();
        View view=layoutInflater.inflate(R.layout.food_item_edit_dialog,null,false);

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
                    .setCancellable(false)
                    .show();

            String filename= UUID.randomUUID().toString();
            final StorageReference imageFolder=FirebaseStorage.getInstance().getReference("images/"+filename);
            Log.i("imageuri",imageUri.toString());

          //  imageUri=Uri.fromFile(new File(new ImageUtil(hostingActivity).compressImage(imageUri.getPath())));
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
            String path = null;
            try {
                path = FileUtil1.getPath(hostingActivity,data.getData());
                Log.i("Path::", "onActivityResult: "+path);
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            Log.i("path", "onActivityResult: "+path);
            File file = new File(path);
            try {
                File compressedImage = new Compressor(hostingActivity)
                        .setMaxWidth(640)
                        .setMaxHeight(480)
                        .setQuality(75)
                        .setCompressFormat(Bitmap.CompressFormat.WEBP)
                        .compressToFile(file);
                imageUri = Uri.fromFile(compressedImage);



            } catch (IOException e) {
                e.printStackTrace();
            }
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


    //****************************************edit and delete options***********************//
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(getUserVisibleHint()) {
            switch (item.getItemId()) {
                case Common.R_ID_EDIT:
//                Toast.makeText(getContext(), "edit is tapped", Toast.LENGTH_SHORT).show();
                    editItem(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
                    break;

                case Common.R_ID_DELETE:
//                Toast.makeText(getContext(), "delete is tapped", Toast.LENGTH_SHORT).show();
                    showDeleteDialog(adapter.getRef(item.getOrder()).getKey(), adapter.getItem(item.getOrder()));
                    break;
            }
            return true;
        }
        else
            return false;

    }

    /********************************************************************************/


    public void editItem(final String key, final Food item){

            alertDialog=new AlertDialog.Builder(hostingActivity).create();
            alertDialog.setTitle("Enter new food details");
            alertDialog.setIcon(R.drawable.thali_graphic);
            LayoutInflater layoutInflater=getLayoutInflater();
            View view=layoutInflater.inflate(R.layout.food_item_edit_dialog,null,false);

            foodDescriptionEditText=view.findViewById(R.id.alertDescriptionEditText);
            chooseImageButton=view.findViewById(R.id.alertSelectButton);
            uploadImageButton=view.findViewById(R.id.alertUploadButton);
            foodNameEditText=view.findViewById(R.id.alertFoodNameEditText);

            foodDescriptionEditText.setText(item.getFoodDescription());
            foodNameEditText.setText(item.getFoodName());
            imageUri=parse(item.getImageUrl());

            if(imageUri!=null)
                chooseImageButton.setText("Image Selected");

            chooseImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    chooseImage();
                }
            });
            uploadImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    uploadNewImage(item.getImageUrl(),key);

                }
            });

            alertDialog.setView(view);

            alertDialog.show();
        }

    private void uploadNewImage(final String oldImageUrl, final String key) {
        UUID uuid=UUID.randomUUID();

             FirebaseStorage.getInstance().getReference().child(uuid.toString()).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ProgressUtils.cancelLoading();
                        deleteFromStorage(oldImageUrl);
                        updateValueAtKey(key,uri.toString());
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(oldImageUrl.equals(imageUri.toString())){
                            updateValueAtKey(key,imageUri.toString());
                        }
                        Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
                        ProgressUtils.cancelLoading();

                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });

    }
    private void deleteFromStorage(String oldImageUri) {

        //to delete old image
        FirebaseStorage.getInstance().getReferenceFromUrl(oldImageUri).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Old image deleted", Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Failed to delete old image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateValueAtKey(String key, String updatedImageUri) {
        Food food=new Food(updatedImageUri,foodNameEditText.getText().toString(),foodDescriptionEditText.getText().toString(),false,false);

        FirebaseDatabase.getInstance().getReference("FoodItems").child(key).setValue(food).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                    alertDialog.dismiss();
                }else
                    Toast.makeText(getContext(), "Try again!", Toast.LENGTH_SHORT).show();
            }
        });


    }


/***********************************************************************/


    private void  showDeleteDialog(final String key, final Food item) {
        AlertDialog.Builder alertDialg=new AlertDialog.Builder(hostingActivity);
        alertDialg.setTitle("Delete the food");
        alertDialg.setMessage("Do you really want to delete it ?");
        alertDialg.setIcon(R.drawable.thali_graphic);
        alertDialg.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteFood(key);
                deleteFromStorage(item.imageUrl);
                dialogInterface.dismiss();
            }
        });
        alertDialg.setNegativeButton("No",null)
        .show();
    }

    private void deleteFood(String key) {
        reference.child(key).removeValue();
        Toast.makeText(hostingActivity,"Deleted Successfully",Toast.LENGTH_SHORT).show();
        adapter.notifyDataSetChanged();
    }
    public static FoodItemsFragment newInstance() {
        FoodItemsFragment fragment = new FoodItemsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onResume() {
        super.onResume();
        ((HomeActivity)activity).setActionBarTitle("Food Items");
    }


}
