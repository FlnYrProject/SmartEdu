package com.project.smartedu.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.student.student_classes;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class view_attendance extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    String role;
    String studentId;
    String classId;
    String subject;

    TextView absentDays;
    TextView totalDays;
    TextView percentage;
    java.util.Calendar calendar;
    ListView attendanceLogs;
    Button showLogButton;

    TextView myDate;
    // Students students = new Students();
    //ArrayList<Task> myList;
    ListView classList;
    NotificationBar noti_bar;

    DatabaseReference databaseReference;


    HashMap<String,String> attendancemap;


    int present = 0;
   int absent = 0;
    int totalDaysnumber = 0;
    double percent = 0.0;






    private class AttendanceItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public AttendanceItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Attendance Data");
            pd.setCancelable(false);
            pd.show();
            databaseReference= Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentId).child("attendance").child(subject);

             present = 0;
             absent = 0;
            totalDaysnumber = 0;
            percent = 0.0;
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {

                          Log.d("att",ds.getKey()+ " " + ds.getValue().toString());
                            attendancemap.put(ds.getKey(),ds.getValue().toString());

                                if (ds.getValue().toString().equalsIgnoreCase("A")) {
                                    absent++;
                                 }
                            totalDaysnumber++;
                            present = totalDaysnumber - absent;

                        }
                        lock.notifyAll();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

            synchronized (lock){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Handles the stuff after the synchronisation with the firebase listener has been achieved
            //The main UI is already idle by this moment
            super.onPostExecute(aVoid);


            if(attendancemap.size()==0){
                showLogButton.setVisibility(View.INVISIBLE);
            }else{
                showLogButton.setVisibility(View.VISIBLE);
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
//                   Toast.makeText(async_context,userPrefs.getUserName(),Toast.LENGTH_LONG).show();
                    information(absent, totalDaysnumber);
                   // setAttendanceList();
                    // noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);
                    pd.dismiss();

                }
            }, 500);  // 100 milliseconds


        }
        //end firebase_async_class
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.show_attendance_daily);

        absentDays = (TextView) findViewById(R.id.absentdays);
        totalDays = (TextView) findViewById(R.id.totalDays);
       percentage = (TextView) findViewById(R.id.percentage);
        attendanceLogs=(ListView)findViewById(R.id.attendancelog);
        showLogButton=(Button)findViewById(R.id.show_logs);


        Intent from_home = getIntent();
        role = from_home.getStringExtra("role");
        studentId = from_home.getStringExtra("studentId");
        classId = from_home.getStringExtra("classId");
        subject=from_home.getStringExtra("subject");
        institutionName=from_home.getStringExtra("institution_name");

        attendanceLogs.setVisibility(View.INVISIBLE);

        showLogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(attendanceLogs.getVisibility()==View.VISIBLE)  {
                showLogButton.setText("SEE LOGS");

                attendanceLogs.setVisibility(View.INVISIBLE);

            }else if(attendanceLogs.getVisibility()==View.INVISIBLE){
                showLogButton.setText("HIDE LOGS");
                makeList();
                attendanceLogs.setVisibility(View.VISIBLE);

            }

            }
        });
        /*
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance");



        noti_bar = (Notification_bar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role);

        dbHandler = new MyDBHandler(getApplicationContext(), null, null, 1);
        classList = (ListView) findViewById(R.id.classesList);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this); */

attendancemap=new HashMap<>();

        AttendanceItems attendanceItems=new AttendanceItems(view_attendance.this);
        attendanceItems.execute();



    }



    public void makeList(){

        ArrayList<String> attendancelist=new ArrayList<>();

        for(String key:attendancemap.keySet()){
            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");

            String messsage="";

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(Long.parseLong(key));
            final String string_current_date= df.format(calendar.getTime());

            if(attendancemap.get(key).equalsIgnoreCase("p")){
                messsage="Present on "+ string_current_date;
            }

            if(attendancemap.get(key).equalsIgnoreCase("a")){
                messsage="Absent on "+ string_current_date;
            }

            attendancelist.add(messsage);

        }



        ArrayAdapter adapter = new ArrayAdapter(view_attendance.this, android.R.layout.simple_list_item_1, attendancelist);
        attendanceLogs.setAdapter(adapter);

    }



    public void information(int absent, int totalDays){

        String absentDays= String.valueOf(absent);
        String total=String.valueOf( totalDays);
        if(totalDays!=0) {
            percent = ((totalDays - absent) / (double) totalDays) * 100;
        }else{
            percent=0.0;
        }
        String PER= String.valueOf(percent)+"%";

        this.absentDays.setText(absentDays.trim());
        this.totalDays.setText(total.trim());
        this.percentage.setText(PER.trim());

        calendar = java.util.Calendar.getInstance();
        //System.out.println("Current time =&gt; " + calendar.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());
//        myDate.setText(string_current_date);
    }


   /* public void setAttendanceList(){

        if (attendancemap.size()==0){
            Toast.makeText(getContext(),"No attendance found",Toast.LENGTH_LONG).show();
        }else{

            ArrayList<String> attendanceitems=new ArrayList<>();

            for (String subject:attendancemap.keySet()){
                int total=0;
                int presentcount=0;


                for(String date:attendancemap.get(subject).keySet()){

                    total++;
                    if(attendancemap.get(subject).get(date).equals("p")){
                        presentcount++;
                    }

                }


                attendanceitems.add(subject + " total = " + total + " present = " + presentcount);

            }

            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, attendanceitems);
            attendanceList.setAdapter(adapter);

        }

    }
*/

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(view_attendance.this,LoginActivity.class);
            startActivity(nouser);
        }
    }


}

