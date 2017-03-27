package com.project.smartedu.common;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.admin.AdminUserPrefs;
import com.project.smartedu.database.Task;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewTask extends BaseActivity {


    String myTitle;
    String myDesc;
    TextView DATE;
    Button addTaskButton;
    int Year;
    int Month;
    int Day;
    CalendarView calendar;
    ImageButton test;
    Date date1;
    EditText taskTitle;
    EditText taskDescription;
    ImageButton imageButton;
    String role;


    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_task);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        firebaseAuth =FirebaseAuth.getInstance();

        taskTitle = (EditText) findViewById(R.id.taskTitle);

        taskDescription = (EditText) findViewById(R.id.scheduleinfo);

        addTaskButton = (Button) findViewById(R.id.addTaskButton);
        imageButton= (ImageButton) findViewById(R.id.test);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });

        DATE= (TextView) findViewById(R.id.date);
        //test=(ImageButton)findViewById(R.id.test);


        Bundle fromrole = getIntent().getExtras();
        role = fromrole.getString("role");
        institutionName=fromrole.getString("institution_name");


        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myTitle = taskTitle.getText().toString().trim();
                myDesc = taskDescription.getText().toString();


                if (myTitle.equals("") || myDesc.equals("") || ((DATE.getText().equals("Select Due Date")))) {
                    Toast.makeText(getApplicationContext(), "Task details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {


                    String string_date = String.valueOf(Day) + "-" + String.valueOf(Month+1) + "-" + String.valueOf(Year);

                    SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = null;
                    try {
                        d = f.parse(string_date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    long milliseconds = d.getTime();


                   // Task task=new Task(myTitle,myDesc,milliseconds);

                    databaseReference = Constants.databaseReference.child(Constants.TASK_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(role).push();

                    databaseReference.child("name").setValue(myTitle);
                    databaseReference.child("description").setValue(myDesc);
                    databaseReference.child("date").setValue(String.valueOf(milliseconds));     //adding to server


                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");



                    String dateString = formatter.format(new Date(milliseconds));

                 //   String entry=mm+ "\n" + taskmap.get("description") + "\n" + dateString;


                    String entry=myTitle+"\n"+myDesc+"\n"+dateString;


                    UserPrefs.taskItems.add(entry);
                    UserPrefs.taskidmap.put(entry,databaseReference.getKey());



                    Intent to_tasks = new Intent(NewTask.this, Tasks.class);
                    to_tasks.putExtra("role", role);
                    startActivity(to_tasks);
                    finish();



                }
            }
        });



    }


    public void open()
    {

        final Dialog dialog = new Dialog(NewTask.this);
        //dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setTitle("Select Date");
        dialog.setContentView(R.layout.activity_calendar);

        setDialogSize(dialog);

        calendar= (CalendarView)dialog.findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                Year = year;
                Month = month;
                Day = dayOfMonth;
                long[] milliseconds = new long[2];

                checkDate(Day, Month+1, Year, milliseconds);
                Log.d("date test ", milliseconds[0] + " selected:" + milliseconds[1]);
                if(milliseconds[1] <= milliseconds[0]){
                    Toast.makeText(getApplicationContext(), "Choose Future Date!", Toast.LENGTH_LONG).show();
                }
                else {
                    date1 = new Date(Year - 1900, Month, Day);
                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                    DATE.setText(dateFormat.format(date1), TextView.BufferType.EDITABLE);
                    Toast.makeText(getApplicationContext(), dayOfMonth + "/" + (Month + 1) + "/" + year, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }

            }
        });
        dialog.show();



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent totask=new Intent(NewTask.this,Tasks.class);
        totask.putExtra("role",role);
        totask.putExtra("institution_name",institutionName);
        startActivity(totask);

    }
}
