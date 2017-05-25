package com.project.smartedu.admin;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.CustomAdapter;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.Model;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.teacher.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;

public class TeacherAttendance extends BaseActivity {
    private Toolbar mToolbar;

    // CalendarView calendar;
    Calendar calendar;
    ImageView cal;
    int Year;
    int Month;
    int Day;

    //int Yearcal;
    //int Monthcal;
    //int Daycal;

    ListView lv;
    Model[] modelItems;
    Model[] modelItemsRetrieved;

    Button saveButton;


    CustomAdapter adapter;

    private FragmentDrawer drawerFragment;


    ListView teacherList;
    NotificationBar noti_bar;
    String classId;


    UserPrefs userPrefs;
    TeacherUserPrefs teacherUserPrefs;

    ArrayList<String> studentLt;
    HashMap<String ,String> localstumap;

    DatabaseReference databasereference;

    Spinner subjectSpinner;
    ArrayAdapter subjectadapter;


    TextView confirm_message;
    Button cancel;
    Button proceed;

    TextView total_count;
    TextView present_count;
    TextView present_percentage;





    ArrayList<String> recipientLt;
    ArrayList<String> sturecipients;






    ArrayList<String> teacherLt;










    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_attendance);

        userPrefs=new UserPrefs(TeacherAttendance.this);
        teacherUserPrefs=new TeacherUserPrefs(TeacherAttendance.this);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //  getSupportActionBar().setDisplayShowHomeEnabled(true);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance");
        Intent from_student = getIntent();
        //final String id = from_student.getStringExtra("id");
        role = from_student.getStringExtra("role");

        institutionName = from_student.getStringExtra("institution_name");

        noti_bar = (NotificationBar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), "Admin", institutionName);



        teacherList = (ListView) findViewById(R.id.teacherList);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setVisibility(View.INVISIBLE);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);


recipientLt=new ArrayList<>();



        teacherLt=sortList(AdminUserPrefs.teacherLt);         //load data remaining

        if (teacherLt.size() == 0) {
            Toast.makeText(TeacherAttendance.this, "No Teacher", Toast.LENGTH_LONG).show();
        } else {

            modelItems = new Model[teacherLt.size()];

            for (int i = 0; i < teacherLt.size(); i++) {

                String teacher = teacherLt.get(i);

                modelItems[i] = new Model(teacher, 0);


            }


            adapter = new CustomAdapter(TeacherAttendance.this, modelItems,"");
            teacherList.setAdapter(adapter);
            saveButton.setVisibility(View.VISIBLE);



            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Dialog attendance_details = new Dialog(TeacherAttendance.this);
                    attendance_details.setContentView(R.layout.attendance_info);
                    total_count = (TextView)  attendance_details.findViewById(R.id.total_students);
                    present_count = (TextView)  attendance_details.findViewById(R.id.present_count);
                    present_percentage = (TextView)  attendance_details.findViewById(R.id.present_percentage);
                    cancel = (Button)  attendance_details.findViewById(R.id.cancelButton);
                    proceed = (Button)  attendance_details.findViewById(R.id.proceedButton);
                    total_count.setText(String.valueOf(teacherLt.size()));

                    int c=0;


                    for (int i = 0; i < adapter.getCount(); i++) {
                        Model item = adapter.getItem(i);

                        if(!item.isChecked()) {

                            c++;
                        }

                    }

                    present_count.setText(String.valueOf(c));

                    double percentage=(double) c/(double)teacherLt.size();
                    present_percentage.setText(String.valueOf(percentage));

                    // confirm_message.setText("All Student in class would be marked PRESENT !! Click Proceed to continue !");
                    attendance_details.show();
                    proceed.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            save();
                            attendance_details.dismiss();
                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            attendance_details.dismiss();
                        }
                    });





                }
            });


        }

    }






    public void save() {


        calendar = java.util.Calendar.getInstance();
        //System.out.println("Current time =&gt; " + calendar.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());

        String[] date = string_current_date.trim().split("/");
        final String[] datedetails = new String[3];
        int j = 0;

        for (String x : date) {
            datedetails[j++] = x;
        }

        Day = Integer.parseInt(datedetails[0]);
        Month = Integer.parseInt(datedetails[1]);
        Year = Integer.parseInt(datedetails[2]);


        final String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
       // Toast.makeText(TeacherAttendance.this, "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

        SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;
        try {
            d = f.parse(string_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        final long newmilliseconds = d.getTime();

        try {
            //int checked=0;

            for (int i = 0; i < adapter.getCount(); i++) {

                Model item = adapter.getItem(i);
                String teacherentry=item.getName();
                String teacherid=AdminUserPrefs.teachersusermap.get(teacherentry);


                databasereference= Constants.databaseReference.child(Constants.TEACHER_ATTENDANCE_TABLE).child(institutionName).child(teacherid);

                databasereference=databasereference.child(String.valueOf(newmilliseconds));
//setting attendance bool to date
                if(item.isChecked()) {
                    recipientLt.add(teacherentry);
                    databasereference.setValue("a");
                }else{
                    databasereference.setValue("p");
                }




            }



sendMessage();



        }

        catch(
                Exception ex
                )

        {
            Toast.makeText(getApplicationContext(), "error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("user", "Error catch: " + ex.getMessage());
        }

    }





    public void  sendMessage(){

        java.util.Calendar calendar = Calendar.getInstance();
        long millis=calendar.getTimeInMillis();
        millis=(millis/1000)*1000;

        for(int x=0;x<recipientLt.size();x++){

            String values[]=recipientLt.get(x).split("\\. ");

            String client_userid=AdminUserPrefs.teachersusermap.get(recipientLt.get(x));
            String name=values[1];



            String message="Hello " + name + ", You are marked absent for today";
            databasereference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(client_userid).push();
            databasereference.child("content").setValue(message);
            databasereference.child("time").setValue(String.valueOf(millis));
            databasereference.child("name").setValue(name);


            databasereference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(client_userid).child("received").child(firebaseAuth.getCurrentUser().getUid()).push();
            databasereference.child("content").setValue(message);
            databasereference.child("time").setValue(String.valueOf(millis));
            databasereference.child("name").setValue(userPrefs.getUserName());

        }


        Intent task_intent = new Intent(TeacherAttendance.this,Home.class);
        task_intent.putExtra("institution_name", institutionName);

        task_intent.putExtra("role", role);
        //task_intent.putExtra("id", classId);
        startActivity(task_intent);


    }


    protected void sleep(int time)
    {
        for(int x=0;x<time;x++)
        {

        }
    }





    public ArrayList<String> sortList(ArrayList<String> arrayList){

        ArrayList<Integer> serials=new ArrayList<>();
        HashMap<Integer,String> map=new HashMap<>();


        for(int x=0;x<arrayList.size();x++){

            String[] entry=arrayList.get(x).split("\\. ");
            serials.add(Integer.parseInt(entry[0]));
            map.put(Integer.parseInt(entry[0]),arrayList.get(x));

        }

        Collections.sort(serials);

        ArrayList<String> sortedList=new ArrayList<>();

        for(int x=0;x<serials.size();x++){
            Integer serial=serials.get(x);
            sortedList.add(map.get(serial));
        }



        return  sortedList;


    }


    @Override
    protected void onPostResume () {
        super.onPostResume();
        if (firebaseAuth.getCurrentUser() == null) {
            Intent nouser = new Intent(TeacherAttendance.this, LoginActivity.class);
            startActivity(nouser);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent task_intent = new Intent(TeacherAttendance.this, Home.class);
        task_intent.putExtra("institution_name", institutionName);
        task_intent.putExtra("role", role);
        startActivity(task_intent);
    }
}
