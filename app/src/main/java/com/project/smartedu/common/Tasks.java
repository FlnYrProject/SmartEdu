package com.project.smartedu.common;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.admin.AdminUserPrefs;
import com.project.smartedu.admin.Home;
import com.project.smartedu.database.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Tasks extends BaseActivity {


    ListView taskList;


    ArrayAdapter adapter=null;
    ArrayList<String> taskLt;



    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    TextView myTitle;
    TextView myDesc;
    TextView myDate;
    Button okButton;
    Button delButton;
    Button editButton;
    Button EditButton;
    int Year;
    int Month;
    int Day;
    int Yearcal;
    int Monthcal;
    int Daycal;
    Date date1;
    CalendarView calendar;
    EditText Title;
    EditText Desc;
    EditText Date;
    String taskid="";

    String [] items;
    ImageButton cal;



    HashMap<String,String> taskidmap;











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Log.d("classsize", String.valueOf(AdminUserPrefs.classes.size()));

        taskidmap= UserPrefs.taskidmap;

        for ( String key :UserPrefs.taskidmap.keySet() ){
            Log.d("keyi",key);
        }

        Bundle fromhome= getIntent().getExtras();
        role = fromhome.getString("role");
        institutionName=fromhome.getString("institution_name");


        //change to add task
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Tasks.this, NewTask.class);
                i.putExtra("institution_name",institutionName);
                i.putExtra("role", role);
                startActivity(i);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();

        taskList=(ListView)findViewById(R.id.taskList);


            taskLt =UserPrefs.taskItems;           //load data afterwards
        
        showList();






        taskList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, final long id) {


                // selected item
                String[] product = ((TextView) view).getText().toString().split("\n");
                final String[] details = new String[3];
                int i = 0;

                for (String x : product) {
                    details[i++] = x;
                }

                final Dialog dialog = new Dialog(Tasks.this);
                dialog.setContentView(R.layout.activity_show_details);
                dialog.setTitle("Task Details");

                setDialogSize(dialog);

                myTitle = (TextView) dialog.findViewById(R.id.start_time);
                myDesc = (TextView) dialog.findViewById(R.id.end_time);
                myDate = (TextView) dialog.findViewById(R.id.date);

                myTitle.setText(details[0].trim());
                myDesc.setText(details[1]);

                myDate.setText(details[2].trim());

                String[] date = details[2].split("/");
                final String[] datedetails = new String[3];
                int j = 0;

                for (String x : date) {
                    datedetails[j++] = x;
                }

                Day = Integer.parseInt(datedetails[0]);
                Month = Integer.parseInt(datedetails[1]);
                Year = Integer.parseInt(datedetails[2]);

                String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
                //Toast.makeText(Tasks.this, "date = " + string_date, Toast.LENGTH_LONG).show();
                SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                Date d = null;
                try {
                    d = f.parse(string_date);
                } catch (java.text.ParseException e) {
                    e.printStackTrace();
                }
                final long milliseconds = d.getTime();



                final String entry=details[0].trim()+"\n"+details[1]+"\n"+details[2].trim();


                for ( String key :taskidmap.keySet() ){
                    Log.d("keyi","key = " +key);
                }

                    taskid=taskidmap.get(entry);

                Log.d("taskid","taskid = " +taskid);

                okButton = (Button) dialog.findViewById(R.id.doneButton);
                delButton = (Button) dialog.findViewById(R.id.delButton);
                editButton = (Button) dialog.findViewById(R.id.editButton);


                okButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                delButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        DatabaseReference dataRef=Constants.databaseReference.child(Constants.TASK_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(role).child(taskid);
                       dataRef.removeValue();
                    UserPrefs.taskidmap.remove(entry);
                       UserPrefs.taskItems.remove(entry);




                        dialog.dismiss();

                        Intent reload=new Intent(Tasks.this,Tasks.class);
                        reload.putExtra("role",role);
                        startActivity(reload);

                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {


                        final Dialog dialog_in = new Dialog(Tasks.this);
                        dialog_in.setContentView(R.layout.activity_new_event_teacher);
                        dialog_in.setTitle("Edit Details");

                        Title = (EditText) dialog_in.findViewById(R.id.taskTitle);
                        Desc = (EditText) dialog_in.findViewById(R.id.scheduleinfo);
                        myDate = (TextView) dialog_in.findViewById(R.id.date);
                        EditButton = (Button) dialog_in.findViewById(R.id.editButton);
                        cal = (ImageButton) dialog_in.findViewById(R.id.test);
                        Title.setText(details[0]);
                        Desc.setText(details[1]);
                        myDate.setText(details[2]);
                        final int[] flag = {0};
                        cal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                myDate.setText(details[2]);
                                open(view);
                                flag[0] = 1;
                                myDate.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
                            }
                        });

                        EditButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                if (Title.equals("") || Desc.equals("") ) {
                                    Toast.makeText(getApplicationContext(), "Event details cannot be empty!", Toast.LENGTH_LONG).show();
                                } else {

                                    DatabaseReference dataRef=Constants.databaseReference.child(Constants.TASK_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(role).child(taskid);

                                    Log.d("title"," "+ ((EditText) dialog_in.findViewById(R.id.taskTitle)).getText().toString());
                                    Log.d("info"," " +((EditText) dialog_in.findViewById(R.id.scheduleinfo)).getText().toString() );
                                    dataRef.child("name").setValue(((EditText) dialog_in.findViewById(R.id.taskTitle)).getText().toString());
                                    dataRef.child("description").setValue(((EditText) dialog_in.findViewById(R.id.scheduleinfo)).getText().toString());

                                    if (flag[0] == 1) {
                                        Day = Daycal;
                                        Month = Monthcal;
                                        Year = Yearcal;
                                    } else {
                                        String[] datenew = myDate.getText().toString().split("/");

                                        final String[] datedetailsnew = new String[3];
                                        int j = 0;

                                        for (String x : datenew) {
                                            datedetailsnew[j++] = x;
                                        }
                                        Log.d("Post retrieval", datedetailsnew[0]);
                                        Toast.makeText(getApplicationContext(), datedetailsnew[0], Toast.LENGTH_LONG);
                                        Day = Integer.parseInt(datedetailsnew[0]);
                                        Month = Integer.parseInt(datedetailsnew[1]);
                                        Year = Integer.parseInt(datedetailsnew[2]);
                                    }
                                    String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
                                    Toast.makeText(getApplicationContext(), "updated date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

                                    SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                    Date d = null;

                                    try {
                                        d = f.parse(string_date);
                                    } catch (java.text.ParseException e1) {
                                        e1.printStackTrace();
                                    }
                                    long newmilliseconds = d.getTime();

                                    String newtitle= ((EditText) dialog_in.findViewById(R.id.taskTitle)).getText().toString();
                                            String newDesc=((EditText) dialog_in.findViewById(R.id.scheduleinfo)).getText().toString();
                                            String newDate=String.valueOf(newmilliseconds);
                                   dataRef.child("name").setValue(newtitle);
                                    dataRef.child("description").setValue(newDesc);
                                    dataRef.child("date").setValue(newDate);



                                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                                    String dateString = formatter.format(new Date(milliseconds));
                                    String newentry=newtitle+ "\n" + newDesc + "\n" + dateString;
                                    UserPrefs.taskidmap.remove(entry);
                                    UserPrefs.taskItems.remove(entry);
                                   UserPrefs.taskidmap.put(newentry,taskid);
                                  UserPrefs.taskItems.add(newentry);



                                    dialog_in.dismiss();
                                    Intent reload=new Intent(Tasks.this,Tasks.class);
                                    reload.putExtra("insitution_name",institutionName);
                                    reload.putExtra("role",role);
                                    startActivity(reload);

                                   // onRestart();
                                }
                            }

                        });

                        dialog_in.show();
                        // taskLt.set(position, Title.getText().toString() + "\n" + Desc.getText().toString() + "\n" + myDate.getText().toString());
                        //adapter.notifyDataSetChanged();
                        dialog.dismiss();
                    }
                });

                dialog.show();
                //recreate();


            }
        });








    }



    public void showList(){

        adapter = new ArrayAdapter(Tasks.this, android.R.layout.simple_list_item_1, taskLt);
        taskList.setAdapter(adapter);
    }



    public void open(View view)
    {

        final Dialog dialogcal = new Dialog(Tasks.this);
        dialogcal.setContentView(R.layout.activity_calendar2);
        dialogcal.setTitle("Select Date");
        calendar= (CalendarView)dialogcal.findViewById(R.id.calendar);

        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                date1=null;
                Yearcal = year;
                Monthcal = month+1;
                Daycal = dayOfMonth;
                date1 = new Date(Yearcal - 1900, Monthcal-1, Daycal);
                DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                myDate.setText(dateFormat.format(date1), TextView.BufferType.EDITABLE);
                Toast.makeText(getApplicationContext(), Daycal + "/" + Monthcal + "/" + Yearcal, Toast.LENGTH_LONG).show();
                dialogcal.dismiss();

            }
        });
        dialogcal.show();



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tohome;
        if(role.equalsIgnoreCase("admin")){
            tohome=new Intent(Tasks.this, Home.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institutionName);
            startActivity(tohome);
        }else if(role.equalsIgnoreCase("teacher")){
            tohome=new Intent(Tasks.this, com.project.smartedu.teacher.Home.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institutionName);
            startActivity(tohome);
        }else if(role.equalsIgnoreCase("student")){
            tohome=new Intent(Tasks.this, com.project.smartedu.student.Home.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institutionName);
            startActivity(tohome);
        }else if(role.equalsIgnoreCase("parent")){
            tohome=new Intent(Tasks.this, com.project.smartedu.parent.Home.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institutionName);
            startActivity(tohome);
        }
    }


}
