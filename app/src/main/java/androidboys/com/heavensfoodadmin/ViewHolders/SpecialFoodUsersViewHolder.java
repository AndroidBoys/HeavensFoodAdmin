package androidboys.com.heavensfoodadmin.ViewHolders;

import android.view.View;
import android.widget.TextView;

import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpecialFoodUsersViewHolder extends RecyclerView.ViewHolder {

    public TextView specialNestedEmail,specialNestedAddress,specialNestedPhoneNumber,specialNestedQuantity;

    public SpecialFoodUsersViewHolder(@NonNull View itemView) {
        super(itemView);
        specialNestedEmail=itemView.findViewById(R.id.specialNestedEmail);
        specialNestedAddress=itemView.findViewById(R.id.specialNestedAddress);
        specialNestedPhoneNumber=itemView.findViewById(R.id.specialNestedPhoneNumber);
        specialNestedQuantity=itemView.findViewById(R.id.specialNestedFoodQuantity);
    }
}
