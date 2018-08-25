package androidboys.com.heavensfoodadmin.Common;

import android.content.Context;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import androidx.annotation.NonNull;

public class FirebaseStorageDeletion {

    public static void deleteFileFromStorage(String url, final Context context){

        FirebaseStorage.getInstance().getReferenceFromUrl(url).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                //Toast.makeText(context,"Deleted Successfully from our server",Toast.LENGTH_SHORT).show();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context,"File can't be deleted",Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
