package androidboys.com.heavensfoodadmin.ViewHolders;

import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.Variables.ContextMenuOptionId;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class WhyHeavensFoodViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {


    public ImageView aboutImageView;
    public TextView aboutTextView;

    public WhyHeavensFoodViewHolder(@NonNull View itemView) {
        super(itemView);
        aboutImageView=itemView.findViewById(R.id.aboutImage1);
        aboutTextView=itemView.findViewById(R.id.aboutTextView1);
        itemView.setOnCreateContextMenuListener(this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {

        contextMenu.setHeaderTitle("Select an action!");
        //first argument of the add method is a group, second argument is an id, third order in which you want item to show , foruth title.
        contextMenu.add(Menu.NONE,ContextMenuOptionId.R_ID_EDIT, getAdapterPosition(), "Edit");
        contextMenu.add(Menu.NONE,ContextMenuOptionId.R_ID_DELETE,getAdapterPosition(),"Delete");

    }

}
