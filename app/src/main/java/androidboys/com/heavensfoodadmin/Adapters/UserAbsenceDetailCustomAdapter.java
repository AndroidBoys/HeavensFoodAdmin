package androidboys.com.heavensfoodadmin.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import androidboys.com.heavensfoodadmin.Models.Absence;
import androidboys.com.heavensfoodadmin.Models.User;
import androidboys.com.heavensfoodadmin.R;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class UserAbsenceDetailCustomAdapter extends ArrayAdapter<User> {

    private ArrayList<User> userArrayList;
    private Context context;
    private TextView userName;
    private TextView userNumber;
    private TextView userAbsenceDate;

    public UserAbsenceDetailCustomAdapter(@NonNull Context context,ArrayList<User> userArrayList) {
        super(context,-1);
        this.context=context;
        this.userArrayList=userArrayList;
    }

    @Override
    public int getCount() {
        return userArrayList.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.user_absence_details_row_layout,null,false);
        userName=view.findViewById(R.id.userAbsenceName);
        userNumber=view.findViewById(R.id.userPhoneNumber);
        userAbsenceDate=view.findViewById(R.id.userAbsenceDate);

        if(userArrayList.get(position).getAbsence()!=null){
            userName.setText(userArrayList.get(position).getName());
            userNumber.setText(userArrayList.get(position).getPhoneNumber());
            userAbsenceDate.setText("Absence :  "+userArrayList.get(position).getAbsence().getStartDate()+
                    " to "+userArrayList.get(position).getAbsence().getEndDate());
        }

        return view;
    }
}
