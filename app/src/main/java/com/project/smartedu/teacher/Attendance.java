package com.project.smartedu.teacher;

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
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.CustomAdapter;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.Model;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.admin.AdminUserPrefs;
import com.project.smartedu.admin.NewClass;
import com.project.smartedu.database.*;
import com.project.smartedu.database.Students;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.student.StudentUserPrefs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Attendance extends BaseActivity {
    private Toolbar mToolbar;
    Button addButton;
    Button editButton;
    Button doneButton;
    EditText editabsentDays;
    //EditText editpercentage;
    EditText edittotalDays;
    TextView absentDays;
    TextView percentage;
    TextView totalDays;
    TextView myDate;
    TextView editmyDate;
    Date date1;
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
    Button presentall;
    Button absentall;

    CustomAdapter adapter;

    private FragmentDrawer drawerFragment;


    ListView studentList;
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




    private class ParentItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;


        public ParentItems(Context context){
            this.async_context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recipientLt.clear();

        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();


            for(int x=0;x<sturecipients.size();x++){
                String studentid=sturecipients.get(x);
                final com.project.smartedu.database.Students students=TeacherUserPrefs.studentsHashMap.get(studentid);
                databasereference=Constants.databaseReference.child(Constants.PARENT_RELATION_TABLE).child(studentid);

                databasereference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        synchronized (lock) {

                            recipientLt.add(dataSnapshot.getValue().toString()+". "+ students.getName());
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
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Handles the stuff after the synchronisation with the firebase listener has been achieved
            //The main UI is already idle by this moment
            super.onPostExecute(aVoid);

          sendMessage();


            Toast.makeText(Attendance.this, "Message Successfully Sent to Parents", Toast.LENGTH_LONG).show();

        }
        //end firebase_async_class
    }











            @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance);

        userPrefs=new UserPrefs(Attendance.this);
        teacherUserPrefs=new TeacherUserPrefs(Attendance.this);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
      //  getSupportActionBar().setDisplayShowHomeEnabled(true);
      //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Attendance");
        Intent from_student = getIntent();
        //final String id = from_student.getStringExtra("id");
        role = from_student.getStringExtra("role");
        classId = from_student.getStringExtra("id");

        institutionName = from_student.getStringExtra("institution_name");

        noti_bar = (NotificationBar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), "Teacher", institutionName);


        subjectSpinner=(Spinner)findViewById(R.id.subjectListspinner);
        studentList = (ListView) findViewById(R.id.studentList);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setVisibility(View.INVISIBLE);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);
        presentall=(Button)findViewById(R.id.allpresent);
        absentall=(Button)findViewById(R.id.allabsent);
        presentall.setVisibility(View.INVISIBLE);
        absentall.setVisibility(View.INVISIBLE);

        subjectadapter = new ArrayAdapter(Attendance.this, android.R.layout.simple_list_item_1, TeacherUserPrefs.subjectallotmentmap.get(classId));
        subjectSpinner.setAdapter(subjectadapter);

                sturecipients=new ArrayList<>();
                recipientLt=new ArrayList<>();

        studentLt = new ArrayList<String>();
        localstumap=new HashMap<>();

        for(int x=0;x<TeacherUserPrefs.studentsuseridLt.size();x++){

            String studentid=TeacherUserPrefs.studentsuseridLt.get(x);
            com.project.smartedu.database.Students student=TeacherUserPrefs.studentsHashMap.get(studentid);


            if(student.getClass_id().equals(classId)){


                studentLt.add(student.getRoll_number() + ". " + student.getName());

                localstumap.put(student.getRoll_number() + ". " + student.getName(),studentid);

            }


        }


        if (studentLt.size() == 0) {
            Toast.makeText(Attendance.this, "No Students", Toast.LENGTH_LONG).show();
        } else {

                        modelItems = new Model[studentLt.size()];
                        //ArrayList<String> studentLt = new ArrayList<String>();
                        // ArrayAdapter adapter = new ArrayAdapter(AddAttendance_everyday.this, android.R.layout.simple_list_item_1, studentLt);
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                        Log.d("user", "Retrieved " + studentLt.size() + " students in this class");
                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < studentLt.size(); i++) {

                            String student = studentLt.get(i);

                            modelItems[i] = new Model(student, 0);


                        }


                        adapter = new CustomAdapter(Attendance.this, modelItems, classId);
                        studentList.setAdapter(adapter);
                        saveButton.setVisibility(View.VISIBLE);
                        presentall.setVisibility(View.VISIBLE);
                        absentall.setVisibility(View.VISIBLE);


                        saveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog attendance_details = new Dialog(Attendance.this);
                                attendance_details.setContentView(R.layout.attendance_info);
                                total_count = (TextView)  attendance_details.findViewById(R.id.total_students);
                                present_count = (TextView)  attendance_details.findViewById(R.id.present_count);
                                present_percentage = (TextView)  attendance_details.findViewById(R.id.present_percentage);
                                cancel = (Button)  attendance_details.findViewById(R.id.cancelButton);
                                proceed = (Button)  attendance_details.findViewById(R.id.proceedButton);
                                total_count.setText(String.valueOf(studentLt.size()));

                                int c=0;


                                for (int i = 0; i < adapter.getCount(); i++) {
                                    Model item = adapter.getItem(i);

                                    if(!item.isChecked()) {

                                       c++;
                                    }

                                }

                                present_count.setText(String.valueOf(c));

                                double percentage=(double) c/(double)studentLt.size();
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

                        presentall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                final Dialog confirm_allpresent = new Dialog(Attendance.this);
                                confirm_allpresent.setContentView(R.layout.confirm_message);
                                confirm_message = (TextView) confirm_allpresent.findViewById(R.id.confirm_message);
                                cancel = (Button) confirm_allpresent.findViewById(R.id.cancelButton);
                                proceed = (Button) confirm_allpresent.findViewById(R.id.proceedButton);
                                confirm_message.setText("All Student in class would be marked PRESENT !! Click Proceed to continue !");
                                confirm_allpresent.show();
                                proceed.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makeAllPresent();
                                        confirm_allpresent.dismiss();
                                    }
                                });

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirm_allpresent.dismiss();
                                    }
                                });


                            }
                        });


                        absentall.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                final Dialog confirm_allabsent = new Dialog(Attendance.this);
                                confirm_allabsent.setContentView(R.layout.confirm_message);
                                confirm_message = (TextView) confirm_allabsent.findViewById(R.id.confirm_message);
                                cancel = (Button) confirm_allabsent.findViewById(R.id.cancelButton);
                                proceed = (Button) confirm_allabsent.findViewById(R.id.proceedButton);
                                confirm_message.setText("All Student in class would be marked ABSENT !! Click Proceed to continue !");
                                confirm_allabsent.show();
                                proceed.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        makeAllAbsent();
                                        confirm_allabsent.dismiss();
                                    }
                                });

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirm_allabsent.dismiss();
                                    }
                                });
                            }
                        });
        }

    }


    public void makeAllPresent(){


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
        Toast.makeText(Attendance.this, "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

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
            String subject=subjectSpinner.getSelectedItem().toString();
            for (int i = 0; i < adapter.getCount(); i++) {
                Model item = adapter.getItem(i);
                String stuentry=item.getName();
                String studentid=localstumap.get(stuentry);


                databasereference= Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentid).child("attendance").child(subject);

                databasereference=databasereference.child(String.valueOf(newmilliseconds));
//setting attendance bool to date

                    databasereference.setValue("p");





            }

       /* if (checked==0)
            Toast.makeText(getApplicationContext(), "None Selected", Toast.LENGTH_LONG).show();

        else{ */





            Intent task_intent = new Intent(Attendance.this, Classes.class);
            task_intent.putExtra("institution_name", institutionName);
            task_intent.putExtra("for","attendance");
            task_intent.putExtra("role", role);
            //task_intent.putExtra("id", classId);
            startActivity(task_intent);

        }

        catch(
                Exception ex
                )

        {
            Toast.makeText(getApplicationContext(), "error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("user", "Error catch: " + ex.getMessage());
        }


    }



    public void makeAllAbsent(){
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
        Toast.makeText(Attendance.this, "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

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
            String subject=subjectSpinner.getSelectedItem().toString();
            sturecipients.clear();
            for (int i = 0; i < adapter.getCount(); i++) {
                Model item = adapter.getItem(i);
                String stuentry=item.getName();
                String studentid=localstumap.get(stuentry);


                databasereference= Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentid).child("attendance").child(subject);

                databasereference=databasereference.child(String.valueOf(newmilliseconds));
//setting attendance bool to date

                databasereference.setValue("a");


                sturecipients.add(studentid);


            }

       /* if (checked==0)
            Toast.makeText(getApplicationContext(), "None Selected", Toast.LENGTH_LONG).show();

        else{ */

            giveMessageToParent();





        }

        catch(
                Exception ex
                )

        {
            Toast.makeText(getApplicationContext(), "error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("user", "Error catch: " + ex.getMessage());
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
        Toast.makeText(Attendance.this, "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

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
            String subject=subjectSpinner.getSelectedItem().toString();
            sturecipients.clear();
            for (int i = 0; i < adapter.getCount(); i++) {
                Model item = adapter.getItem(i);
                String stuentry=item.getName();
                String studentid=localstumap.get(stuentry);


                databasereference= Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentid).child("attendance").child(subject);

                databasereference=databasereference.child(String.valueOf(newmilliseconds));
//setting attendance bool to date
                if(item.isChecked()) {
                    databasereference.setValue("a");
                    sturecipients.add(studentid);


                }else{
                    databasereference.setValue("p");
                }




            }




            giveMessageToParent();


        }

        catch(
                Exception ex
                )

        {
            Toast.makeText(getApplicationContext(), "error: " + ex.getMessage(), Toast.LENGTH_LONG).show();
            Log.d("user", "Error catch: " + ex.getMessage());
        }

    }

    public void giveMessageToParent() {
        ParentItems parentItems=new ParentItems(Attendance.this);
        parentItems.execute();

    }



    public void  sendMessage(){

        java.util.Calendar calendar = Calendar.getInstance();
        long millis=calendar.getTimeInMillis();
        millis=(millis/1000)*1000;

        for(int x=0;x<recipientLt.size();x++){

            String values[]=recipientLt.get(x).split("\\. ");

            String client_userid=values[0];
            String name=values[1];

            String subject=subjectSpinner.getSelectedItem().toString();

            String message="Hello, Your child, " + name + " was absent today for " + subject;
            databasereference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(client_userid).push();
            databasereference.child("content").setValue(message);
            databasereference.child("time").setValue(String.valueOf(millis));
            databasereference.child("name").setValue(name);


            databasereference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(client_userid).child("received").child(firebaseAuth.getCurrentUser().getUid()).push();
            databasereference.child("content").setValue(message);
            databasereference.child("time").setValue(String.valueOf(millis));
            databasereference.child("name").setValue(userPrefs.getUserName());

        }


        Intent task_intent = new Intent(Attendance.this, Classes.class);
        task_intent.putExtra("institution_name", institutionName);
        task_intent.putExtra("for","attendance");
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



    @Override
    protected void onPostResume () {
        super.onPostResume();
        if (firebaseAuth.getCurrentUser() == null) {
            Intent nouser = new Intent(Attendance.this, LoginActivity.class);
            startActivity(nouser);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent task_intent = new Intent(Attendance.this, Classes.class);
        task_intent.putExtra("institution_name", institutionName);
        task_intent.putExtra("for","attendance");
        task_intent.putExtra("role", role);
        //task_intent.putExtra("id", classId);
        startActivity(task_intent);
    }
}
