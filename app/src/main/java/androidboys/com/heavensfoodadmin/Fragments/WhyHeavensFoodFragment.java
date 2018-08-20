package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import androidboys.com.heavensfoodadmin.Common.Common;
import androidboys.com.heavensfoodadmin.Models.Faq;
import androidboys.com.heavensfoodadmin.Models.WhyHeavenFood;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Utils.ProgressUtils;
import androidboys.com.heavensfoodadmin.ViewHolders.WhyHeavensFoodViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class WhyHeavensFoodFragment extends Fragment {
    private RecyclerView whyHeavensFoodRecyclerView;
    private DatabaseReference databaseReference;
    private FloatingActionButton addDescriptionFloatingActionButton;
    private final int PICK_IMAGE=100;
    private Uri imageUri=null;//this variable changes according to image selecte from galary.
    private EditText aboutEditText;
    private Button chooseImageButton;
    private  FirebaseRecyclerAdapter<WhyHeavenFood,WhyHeavensFoodViewHolder> adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.why_heaven_foods_fragment,container,false);
        addDescriptionFloatingActionButton=view.findViewById(R.id.addWhyHeavensFood);
        whyHeavensFoodRecyclerView=view.findViewById(R.id.whyHeavenFoodsRecyclerView);
        whyHeavensFoodRecyclerView.setHasFixedSize(true);
        whyHeavensFoodRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
       fetchAboutDataFromFirebase();
        addDescriptionFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDescription();

            }
        });

        return view;
    }

//    ************************************************************************************************
    //*****************methods used to add a new description******************************************
//    **************************************************************************************************

    private void addDescription() {
        imageUri=null;
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.add_why_heavens_food_description,null,false);
         aboutEditText=view.findViewById(R.id.addAboutEditText);
         chooseImageButton=view.findViewById(R.id.addAboutButton);
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                chooseAndSetImage();
//                Picasso.with(getContext()).load(imageUri).into(aboutImageView);
            }
        });

        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(view).setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(imageUri==null)
                    Toast.makeText(getActivity(), "Please select an image first!", Toast.LENGTH_SHORT).show();
                else {
                    ProgressUtils.showLoadingDialog(getActivity());
                    uploadImage();
                }
            }
        })
                .setNegativeButton("Cancel",null)
                .setCancelable(false)
                .show();


    }

    private void uploadImage() {
        UUID uuid=UUID.randomUUID();

        StorageTask<UploadTask.TaskSnapshot> storageReference = FirebaseStorage.getInstance().getReference().child(uuid.toString()).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ProgressUtils.cancelLoading();
                        addIntoFDB(uri.toString());
                    }
                });
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ProgressUtils.cancelLoading();
                        Toast.makeText(getContext(), ""+e, Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                    }
                });
    }

    private void addIntoFDB(String imageUrl) {

        WhyHeavenFood whyHeavenFood=new WhyHeavenFood(aboutEditText.getText().toString(),imageUrl);
        FirebaseDatabase.getInstance().getReference("WhyHeavensFood").push().setValue(whyHeavenFood).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(getContext(), "updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Try again", Toast.LENGTH_SHORT).show();
            }
        });

    }


//    ************************************************************************************************
    //*****************methods used to edit a  description******************************************
//    **************************************************************************************************


    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case Common.R_ID_EDIT:
//                Toast.makeText(getContext(), "edit is tapped", Toast.LENGTH_SHORT).show();
                editDescription(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
                break;

            case Common.R_ID_DELETE:
//                Toast.makeText(getContext(), "delete is tapped", Toast.LENGTH_SHORT).show();
                showDeleteDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
                break;
        }
        return true;
    }

    public void editDescription(final String key, final WhyHeavenFood item){
        LayoutInflater inflater=getLayoutInflater();
        View view=inflater.inflate(R.layout.add_why_heavens_food_description,null,false);

        chooseImageButton=view.findViewById(R.id.addAboutButton);
        aboutEditText=view.findViewById(R.id.addAboutEditText);
        aboutEditText.setText(item.getAbout());
        imageUri= Uri.parse(item.getImageUrl());//if user do not choose a different image so we can use this previous image

        if(imageUri!=null)
            chooseImageButton.setText("Image Selected");
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseAndSetImage();
                if(imageUri!=null)
                    chooseImageButton.setText("Image Selected");
         }
        });


        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setView(view).setPositiveButton("ADD", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                ProgressUtils.showLoadingDialog(getActivity());
                uploadNewImage(item.imageUrl,key);
            }
        })
                .setNegativeButton("Cancel",null)
                .setCancelable(false)
                .show();

    }


    private void uploadNewImage(final String oldImageUrl, final String key) {
        UUID uuid=UUID.randomUUID();

        StorageTask<UploadTask.TaskSnapshot> starageReference = FirebaseStorage.getInstance().getReference().child(uuid.toString()).putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
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
        WhyHeavenFood whyHeavenFood=new WhyHeavenFood(aboutEditText.getText().toString(),updatedImageUri);
        FirebaseDatabase.getInstance().getReference("WhyHeavensFood").child(key).setValue(whyHeavenFood).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                    Toast.makeText(getContext(), "Updated", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(getContext(), "Try again!", Toast.LENGTH_SHORT).show();
            }
        });


    }



//    ************************************************************************************************
    //*****************Common methods used in editing and adding a  description******************************************
//    **************************************************************************************************

    private void chooseAndSetImage() {
        Intent chooseAndSetImageIntent=new Intent(Intent.ACTION_GET_CONTENT);
        chooseAndSetImageIntent.setType("image/*");
        startActivityForResult(chooseAndSetImageIntent,PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE && resultCode==getActivity().RESULT_OK && data!=null){
            imageUri= data.getData();
                chooseImageButton.setText("Image Selected");
        }
    }

    private void fetchAboutDataFromFirebase() {
        databaseReference=FirebaseDatabase.getInstance().getReference("WhyHeavensFood");

      adapter= new FirebaseRecyclerAdapter<WhyHeavenFood, WhyHeavensFoodViewHolder>(WhyHeavenFood.class,R.layout.why_heaven_foods_fragment_row,WhyHeavensFoodViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(WhyHeavensFoodViewHolder whyHeavensFoodViewHolder, WhyHeavenFood whyHeavenFood, int i) {
                setData(whyHeavensFoodViewHolder,whyHeavenFood);

            }
        };
        adapter.notifyDataSetChanged();

        whyHeavensFoodRecyclerView.setAdapter(adapter);
    }

    private void setData(WhyHeavensFoodViewHolder whyHeavensFoodViewHolder,WhyHeavenFood whyHeavenFood) {
        Picasso.with(getContext()).load(whyHeavenFood.getImageUrl()).into(whyHeavensFoodViewHolder.aboutImageView);
        whyHeavensFoodViewHolder.aboutTextView.setText(whyHeavenFood.getAbout());
    }

    public WhyHeavensFoodFragment() {
    }

      public static WhyHeavensFoodFragment newInstance() {
        
        Bundle args = new Bundle();
        
        WhyHeavensFoodFragment fragment = new WhyHeavensFoodFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private void showDeleteDialog(final String key, final WhyHeavenFood item) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.ic_warning_black_24dp)
                .setTitle("Are you sure?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteFromStorage(item.getImageUrl());
                        deleteFromDatabase(key);
                    }
                })
                .setNegativeButton("No",null)
                .setCancelable(false)
                .show();
    }

    private void deleteFromDatabase(String key) {
    FirebaseDatabase.getInstance().getReference("WhyHeavensFood").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
        @Override
        public void onComplete(@NonNull Task<Void> task) {
            if(task.isSuccessful())
                Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
        }
    });
    }
}
