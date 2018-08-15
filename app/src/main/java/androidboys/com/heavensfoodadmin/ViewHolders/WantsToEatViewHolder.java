package androidboys.com.heavensfoodadmin.ViewHolders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WantsToEatViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


    public ImageView wantsFoodImageView;
    public TextView wantsFoodNameTextView;
    public TextView wantsFoodDescriptionTextView;
    public CheckBox wantsFoodCheckBox;

    public WantsToEatViewHolder(@NonNull View itemView) {
        super(itemView);
        wantsFoodImageView=itemView.findViewById(R.id.wantsFoodImageView);
        wantsFoodNameTextView=itemView.findViewById(R.id.wantsFoodNameTextView);
        wantsFoodDescriptionTextView=itemView.findViewById(R.id.wantsFoodDescriptionTextView);
        wantsFoodCheckBox=itemView.findViewById(R.id.wantsFoodCheckBox);

        itemView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {

    }
}
