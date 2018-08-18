package androidboys.com.heavensfoodadmin.ViewHolders;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class UserListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {

    public final LinearLayout layout;
    final LinearLayout.LayoutParams params;
    public ImageView drawableImageView,callImageView;
    public TextView NameTextView,phoneTextView;
   public UserListViewHolder(@NonNull View itemView) {
        super(itemView);
        drawableImageView=itemView.findViewById(R.id.drawableImage);
        NameTextView=itemView.findViewById(R.id.name);
       phoneTextView=itemView.findViewById(R.id.phone);
        callImageView=itemView.findViewById(R.id.callImageView);

        callImageView.setOnClickListener(this);

       layout =(LinearLayout)itemView.findViewById(R.id.superLinearLayout);
       params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
               ViewGroup.LayoutParams.WRAP_CONTENT);
    }
    public void Layout_hide() {
        params.height = 0;
        //itemView.setLayoutParams(params); //This One.
        layout.setLayoutParams(params);   //Or This one.

    }

    @Override
    public void onClick(View view) {

    }
}
