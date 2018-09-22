package androidboys.com.heavensfoodadmin.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baoyz.widget.PullRefreshLayout;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import androidboys.com.heavensfoodadmin.Activities.DescriptionActivity;
import androidboys.com.heavensfoodadmin.Models.FoodMenu;
import androidboys.com.heavensfoodadmin.R;
import androidboys.com.heavensfoodadmin.ViewHolders.FoodMenuViewHolder;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class SubscribedUserTodaysMenu extends Fragment implements View.OnCreateContextMenuListener {

    private RecyclerView breakFastRecyclerView;
    private RecyclerView lunchRecyclerView;
    private RecyclerView dinnerRecyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Context context;
    private FirebaseRecyclerAdapter<FoodMenu, FoodMenuViewHolder> dinnerAdapter;
    private FirebaseRecyclerAdapter<FoodMenu,FoodMenuViewHolder> breakFastAdapter;
    private FirebaseRecyclerAdapter<FoodMenu,FoodMenuViewHolder> lunchAdapter;
    private DatabaseReference foodMenuDatabaseReference;
    private TextView markAbsenceTextView;
    private TextView wantToEatTextView;
    private Button startDateButton;
    private Button endDateButton;
    private boolean isStartDate;
    private Button submitButton;
    private String startDate;
    private String endDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.subscribed_user_todays_menu_layout,container,false);
        breakFastRecyclerView=view.findViewById(R.id.breakFastRecyclerView);
        lunchRecyclerView=view.findViewById(R.id.lunchRecyclerView);
        dinnerRecyclerView=view.findViewById(R.id.dinnerRecyclerView);
        markAbsenceTextView=view.findViewById(R.id.markAbsenceTextView);
        wantToEatTextView=view.findViewById(R.id.wantToEatTextView);
        context=getContext();

        String[] weekDays = new String[]{"Sun", "Mon", "Tues", "Wed", "Thrus", "Fri", "Sat"};
        int day=findTodayDay();
        foodMenuDatabaseReference=FirebaseDatabase.getInstance().getReference("WeeklyMenu").child(weekDays[day-1]);//since it returns day number from 1 to 7

       // linearLayoutManager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        setRecyclerView(breakFastRecyclerView);
        setRecyclerView(lunchRecyclerView);
        setRecyclerView(dinnerRecyclerView);
        showBreakFastImages();
        showlunchImages();
        showDinnerImages();

        findTodayDay();

        markAbsenceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAbsenceDialog();
            }
        });

        wantToEatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Moving into description activity and passed text id.
                Intent intent=new Intent(context, DescriptionActivity.class);
                intent.putExtra("ID",wantToEatTextView.getId());
                startActivity(intent);

            }
        });

        PullRefreshLayout todayMenuRefreshLayout=view.findViewById(R.id.todayMenuRefreshLayout);
        todayMenuRefreshLayout.setOnRefreshListener(new PullRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showBreakFastImages();
                showlunchImages();
                showDinnerImages();
            }
        });
        todayMenuRefreshLayout.setColor(R.color.colorPrimary);
        registerForContextMenu(view);

        return view;
    }

    private int findTodayDay() {

        Calendar calendar=Calendar.getInstance(Locale.getDefault());
        int day=calendar.get(Calendar.DAY_OF_WEEK);
        return day;
        //Log.i("Dayname","-------------------"+weekdays[day]+"   "+day);
    }

    private void showAbsenceDialog() {
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        //alertDialog.setTitle("Mark Your Absence");
        //alertDialog.setIcon(R.drawable.thali_graphic);
        LayoutInflater layoutInflater=LayoutInflater.from(context);
        View view=layoutInflater.inflate(R.layout.mark_absence_layout,null,false);
        startDateButton=view.findViewById(R.id.startDateButton);
        endDateButton=view.findViewById(R.id.endDateButton);
        submitButton=view.findViewById(R.id.submitButton);
        startDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartDate=true;
                showDatePicker();
            }
        });

        endDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isStartDate=false;
                showDatePicker();
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String,String> map=new HashMap<>();
                map.put("startDate",startDate);
                map.put("endDate",endDate);
                FirebaseDatabase.getInstance().getReference("Absence").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).
                        setValue(map);
                Toast.makeText(context,"Submitted Succesfully", Toast.LENGTH_SHORT).show();
                alertDialog.setView(null);//To dimiss the alert dialog set its view to null
            }
        });
        alertDialog.setView(view);
        alertDialog.show();

    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = DatePickerDialog.newInstance(
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
                        String date=dayOfMonth+"/"+(monthOfYear+1)+"/"+year;
                        if(isStartDate){
                            startDateButton.setText(date);
                            startDate=date;

                        }else{
                            endDateButton.setText(date);
                            endDate=date;
                        }
                    }
                },
                calendar.get(Calendar.YEAR), // Initial year selection
                calendar.get(Calendar.MONTH), // Initial month selection
                calendar.get(Calendar.DAY_OF_MONTH) // Inital day selection
        );
        datePickerDialog.show(getActivity().getFragmentManager(), "Datepickerdialog");
    }

    private void setRecyclerView(RecyclerView recyclerView) {

        linearLayoutManager=new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    private void showDinnerImages() {
        DatabaseReference databaseReference=foodMenuDatabaseReference.child("Dinner");
        dinnerAdapter=new FirebaseRecyclerAdapter<FoodMenu, FoodMenuViewHolder>(
                FoodMenu.class,R.layout.food_menu_row_layout,FoodMenuViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(FoodMenuViewHolder foodMenuViewHolder, FoodMenu foodMenu, int i) {
                setFoodDetails(foodMenuViewHolder, foodMenu);

            }
        };
        dinnerAdapter.notifyDataSetChanged();
        dinnerRecyclerView.setAdapter(dinnerAdapter);
        //Download the images from the firebase
    }

    private void setFoodDetails(FoodMenuViewHolder foodMenuViewHolder,FoodMenu foodMenu) {
        foodMenuViewHolder.foodNameTextView.setText(foodMenu.getFoodName());
        foodMenuViewHolder.foodDescriptionTextView.setText(foodMenu.getFoodDescription());
        Picasso.with(context).load(foodMenu.getImageUrl()).placeholder(R.drawable.progress_animation).into(foodMenuViewHolder.foodImageView);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("pagal");
        Log.i("menuItem","------------"+menu.getItem(0));

        //we don't want to give edit and delete option in today's menu fragment.That's why i set the visibility of item false
        menu.getItem(0).setVisible(false);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        return super.onContextItemSelected(item);
    }

    private void showlunchImages() {
        DatabaseReference databaseReference=foodMenuDatabaseReference.child("Lunch");
        lunchAdapter=new FirebaseRecyclerAdapter<FoodMenu, FoodMenuViewHolder>(
                FoodMenu.class,R.layout.food_menu_row_layout,FoodMenuViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(FoodMenuViewHolder foodMenuViewHolder, FoodMenu foodMenu, int i) {
                setFoodDetails(foodMenuViewHolder, foodMenu);

            }
        };
        lunchAdapter.notifyDataSetChanged();
        lunchRecyclerView.setAdapter(lunchAdapter);
        //Download the images from the firebase
    }

    private void showBreakFastImages() {
        DatabaseReference databaseReference=foodMenuDatabaseReference.child("BreakFast");
        breakFastAdapter=new FirebaseRecyclerAdapter<FoodMenu, FoodMenuViewHolder>(
                FoodMenu.class,R.layout.food_menu_row_layout,FoodMenuViewHolder.class,databaseReference) {
            @Override
            protected void populateViewHolder(FoodMenuViewHolder foodMenuViewHolder, FoodMenu foodMenu, int i) {
                setFoodDetails(foodMenuViewHolder, foodMenu);

            }
        };//since it returns day number from 1 to 7

        breakFastAdapter.notifyDataSetChanged();
        breakFastRecyclerView.setAdapter(breakFastAdapter);
        //Download the images from the firebase
    }


    public static SubscribedUserTodaysMenu newInstance() {

        Bundle args = new Bundle();

        SubscribedUserTodaysMenu fragment = new SubscribedUserTodaysMenu();
        fragment.setArguments(args);
        return fragment;
    }
}
