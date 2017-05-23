package com.project.smartedu.teacher;

import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.widget.ProgressBar;
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
import com.project.smartedu.MarksCustomAdapter;
import com.project.smartedu.MarksListAdapter;
import com.project.smartedu.MarksModel;
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

import static java.lang.Boolean.FALSE;

public class AddMarks extends BaseActivity {
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
    MarksModel[] modelItems;


    Button saveButton;
   Button cancelButton;

    MarksCustomAdapter adapter;

    private FragmentDrawer drawerFragment;


    ListView studentList;
    NotificationBar noti_bar;
    String classId;


    String examId;

    UserPrefs userPrefs;
    TeacherUserPrefs teacherUserPrefs;

    ArrayList<String> studentLt;
    HashMap<String ,String> localstumap;
    ArrayList<Integer> marksLt;

    DatabaseReference databasereference;

    Spinner subjectSpinner;
    ArrayAdapter subjectadapter;


    TextView confirm_message;
    Button cancel;
    Button proceed;

    TextView total_count;
    TextView present_count;
    TextView present_percentage;
    ProgressDialog progressDialog;





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

                String arr[]=sturecipients.get(x).split("\\. ");

                String studentid=arr[0];
                final String marks=arr[1];
                final com.project.smartedu.database.Students students=TeacherUserPrefs.studentsHashMap.get(studentid);
                databasereference=Constants.databaseReference.child(Constants.PARENT_RELATION_TABLE).child(studentid);

                databasereference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        synchronized (lock) {

                            recipientLt.add(dataSnapshot.getValue().toString()+". "+ students.getName() + ". " +marks);
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


            Toast.makeText(AddMarks.this, "Message Successfully Sent to Parents", Toast.LENGTH_LONG).show();

        }
        //end firebase_async_class
    }











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marks);

        userPrefs=new UserPrefs(AddMarks.this);
        teacherUserPrefs=new TeacherUserPrefs(AddMarks.this);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //  getSupportActionBar().setDisplayShowHomeEnabled(true);
        //  getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Marks");
        Intent from_student = getIntent();
        //final String id = from_student.getStringExtra("id");
        role = from_student.getStringExtra("role");
        classId = from_student.getStringExtra("classId");
        examId=from_student.getStringExtra("examid");

        institutionName = from_student.getStringExtra("institution_name");

        noti_bar = (NotificationBar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), "Teacher", institutionName);


        progressDialog=new ProgressDialog(AddMarks.this);
        progressDialog.setCancelable(FALSE);

        studentList = (ListView) findViewById(R.id.studentList);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setVisibility(View.INVISIBLE);
        cancelButton=(Button)findViewById(R.id.cancelButton);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);




        sturecipients=new ArrayList<>();
        recipientLt=new ArrayList<>();

        studentLt = new ArrayList<String>();
        marksLt=new ArrayList<>();
        localstumap=new HashMap<>();

        for(int x=0;x<TeacherUserPrefs.studentsuseridLt.size();x++){

            String studentid=TeacherUserPrefs.studentsuseridLt.get(x);
            com.project.smartedu.database.Students student=TeacherUserPrefs.studentsHashMap.get(studentid);

            Log.d("classId",classId);
            if(student.getClass_id().equals(classId)){


                studentLt.add(student.getRoll_number() + ". " + student.getName());
                marksLt.add(0);

                localstumap.put(student.getRoll_number() + ". " + student.getName(),studentid);

            }


        }


        if (studentLt.size() == 0) {
            Toast.makeText(AddMarks.this, "No Students", Toast.LENGTH_LONG).show();
        } else {

            modelItems = new MarksModel[studentLt.size()];
            //ArrayList<String> studentLt = new ArrayList<String>();
            // ArrayAdapter adapter = new ArrayAdapter(AddAttendance_everyday.this, android.R.layout.simple_list_item_1, studentLt);
            //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

            Log.d("user", "Retrieved " + studentLt.size() + " students in this class");
            //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
            for (int i = 0; i < studentLt.size(); i++) {

                String student = studentLt.get(i);



                modelItems[i] = new MarksModel(student, i);


            }

          /*  MarksListAdapter myListAdapter = new MarksListAdapter(AddMarks.this,studentLt,marksLt);

            studentList.setAdapter(myListAdapter);*/

            adapter = new MarksCustomAdapter(AddMarks.this, modelItems, classId);
            studentList.setAdapter(adapter);
            saveButton.setVisibility(View.VISIBLE);



            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    boolean flag=false;
                    for (int i = 0; i < adapter.getCount(); i++) {
                        MarksModel item = adapter.getItem(i);
                        if(item.getValue()>Integer.parseInt(TeacherUserPrefs.examHashMap.get(examId).getMax_marks())){
                            Toast.makeText(AddMarks.this,"Marks cannot be greater than maximum marks",Toast.LENGTH_LONG).show();
                            flag=true;
                            break;
                        }
                        if(item.getValue()<0){
                            Toast.makeText(AddMarks.this,"Marks cannot be less than zero",Toast.LENGTH_LONG).show();
                            flag=true;
                            break;
                        }


                        Log.d("marks",String.valueOf(item.getValue()));
                    }

                    if(!flag) {
                        save();
                    }






                }
            });




        }

    }










    public void save() {


        progressDialog.setMessage("Adding Marks and Sending notification...");
        progressDialog.show();

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
        Toast.makeText(AddMarks.this, "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

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
            Exam exam=TeacherUserPrefs.examHashMap.get(examId);
            sturecipients.clear();
            for (int i = 0; i < adapter.getCount(); i++) {
                MarksModel item = adapter.getItem(i);
                String stuentry=item.getName();
                String studentid=localstumap.get(stuentry);


                databasereference= Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentid).child("exam").child(exam.getSubject()).child(exam.getId());


                databasereference.child("name").setValue(exam.getName());
                databasereference.child("date").setValue(exam.getDate());
                databasereference.child("max_marks").setValue(exam.getMax_marks());
                databasereference.child("marks_obtained").setValue(String.valueOf(item.getValue()));

                    sturecipients.add(studentid + ". " + String.valueOf(item.getValue()));






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
        ParentItems parentItems=new ParentItems(AddMarks.this);
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
            String marks=values[2];
          String subject=TeacherUserPrefs.examHashMap.get(examId).getSubject();
            String max_marks=TeacherUserPrefs.examHashMap.get(examId).getMax_marks();
            String message="Hello, Your child, " +name +" attained " + marks + "/" + max_marks+ " in the test help for " + subject;
            databasereference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(client_userid).push();
            databasereference.child("content").setValue(message);
            databasereference.child("time").setValue(String.valueOf(millis));
            databasereference.child("name").setValue(name);


            databasereference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(client_userid).child("received").child(firebaseAuth.getCurrentUser().getUid()).push();
            databasereference.child("content").setValue(message);
            databasereference.child("time").setValue(String.valueOf(millis));
            databasereference.child("name").setValue(userPrefs.getUserName());

        }


        progressDialog.dismiss();
        Intent task_intent = new Intent(AddMarks.this, Exams.class);
        task_intent.putExtra("institution_name", institutionName);
        task_intent.putExtra("for","attendance");
        task_intent.putExtra("role", role);
        task_intent.putExtra("id", classId);
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
            Intent nouser = new Intent(AddMarks.this, LoginActivity.class);
            startActivity(nouser);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent task_intent = new Intent(AddMarks.this,Exams.class);
        task_intent.putExtra("institution_name", institutionName);
        task_intent.putExtra("id",classId);
        task_intent.putExtra("role", role);
        //task_intent.putExtra("id", classId);
        startActivity(task_intent);
    }
}
