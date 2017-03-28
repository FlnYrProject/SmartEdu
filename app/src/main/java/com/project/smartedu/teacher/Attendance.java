package com.project.smartedu.teacher;

import android.app.Dialog;
import android.content.Intent;
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

import com.google.firebase.database.DatabaseReference;
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
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;

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
            for (int i = 0; i < adapter.getCount(); i++) {
                Model item = adapter.getItem(i);
                String stuentry=item.getName();
                String studentid=localstumap.get(stuentry);


                databasereference= Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentid).child("attendance").child(subject);

                databasereference=databasereference.child(String.valueOf(newmilliseconds));
//setting attendance bool to date

                databasereference.setValue("a");





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
            for (int i = 0; i < adapter.getCount(); i++) {
                Model item = adapter.getItem(i);
                String stuentry=item.getName();
                String studentid=localstumap.get(stuentry);


                databasereference= Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentid).child("attendance").child(subject);

                databasereference=databasereference.child(String.valueOf(newmilliseconds));
//setting attendance bool to date
                if(item.isChecked()) {
                    databasereference.setValue("a");
                   // giveMessageParent(studentRef[0], string_date);

                }else{
                    databasereference.setValue("p");
                }




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

    public void giveMessageParent(final ParseObject studentRef, final String string_date) {

     /*   Log.d("user", "in give message");
        ParseUser student_ofclient = (ParseUser) studentRef.get(StudentTable.STUDENT_USER_REF);
        ParseQuery<ParseObject> parent_relation = ParseQuery.getQuery(ParentTable.TABLE_NAME);
        parent_relation.whereEqualTo(ParentTable.CHILD_USER_REF, student_ofclient);
        parent_relation.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        Log.d("user", "in query");
                        ParseUser client_user = (ParseUser) objects.get(0).get(ParentTable.PARENT_USER_REF);
                        ParseObject newmessage = new ParseObject(MessageTable.TABLE_NAME);
                        newmessage.put(MessageTable.FROM_USER_REF, ParseUser.getCurrentUser());
                        newmessage.put(MessageTable.TO_USER_REF, client_user);
                        Log.d("user", "to parent " + client_user.getObjectId());
                        newmessage.put(MessageTable.MESSAGE_CONTENT, studentRef.get("name") + " was absent today on " + string_date);

                        newmessage.put(MessageTable.DELETED_BY_SENDER,false);
                        newmessage.put(MessageTable.DELETED_BY_RECEIVER,false);

                        java.util.Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa");
                        String date = format.format(new Date(calendar.getTimeInMillis()));
                        Date d = null;
                        try {
                            d = format.parse(date);
                        } catch (java.text.ParseException e1) {
                            e1.printStackTrace();
                        }

                        newmessage.put(MessageTable.SENT_AT, d.getTime());
                        newmessage.saveEventually();
                        //Toast.makeText(AddAttendance_everyday.this, "Message Successfully Sent to Parent", Toast.LENGTH_LONG).show();


                    }else{
                        Log.d("user ", "error in query");
                    }
                } else {
                    Log.d("user ", e.getMessage());
                }
            }
        });*/
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
