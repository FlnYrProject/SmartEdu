package com.project.smartedu.teacher;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.common.Tasks;
import com.project.smartedu.database.Exam;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class Exams extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    Button addExamButton;
    Spinner subjectSelectSpinner;
    ArrayAdapter subjectadapter;

    private FragmentDrawer drawerFragment;


    ListView examsList;
    NotificationBar noti_bar;
    String classId;

    String subjectSelected;


    UserPrefs userPrefs;
    TeacherUserPrefs teacherUserPrefs;


    Button okButton;
    Button delButton;
    Button editButton;
    Button EditButton;
    TextView examname;
    TextView exammarks;
    TextView examsdate;

    Button OkButton;

    HashMap<String,Exam> examMap;


    Date date1;
    CalendarView calendar;
    EditText nameexam;
    EditText marksexam;
    ImageButton cal;
    TextView dateexam;


    int Yearcal;
    int Monthcal;
    int Daycal;
    int Year;
    int Month;
    int Day;
    TextView myDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);

        Intent from_student = getIntent();
        classId = from_student.getStringExtra("id");
        role=from_student.getStringExtra("role");
        institutionName = from_student.getStringExtra("institution_name");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
       /* getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        getSupportActionBar().setTitle("Exams");
        userPrefs=new UserPrefs(Exams.this);
        teacherUserPrefs=new TeacherUserPrefs(Exams.this);


        noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);


        addExamButton = (Button)findViewById(R.id.addExam);
        examsList = (ListView) findViewById(R.id.examList);
        subjectSelectSpinner=(Spinner)findViewById(R.id.subjectListspinner);

        subjectadapter = new ArrayAdapter(Exams.this, android.R.layout.simple_list_item_1, TeacherUserPrefs.subjectallotmentmap.get(classId));
        subjectSelectSpinner.setAdapter(subjectadapter);


        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

        subjectSelected=subjectSelectSpinner.getSelectedItem().toString();

        examMap= new HashMap<>();

setExamList();

        subjectSelectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setExamList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });









        examsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                String item = ((TextView) view).getText().toString();

                final Exam exam=examMap.get(item);


                final Dialog dialog = new Dialog(Exams.this);
                dialog.setContentView(R.layout.exam_details);
                dialog.setTitle("Exam Details");

                setDialogSize(dialog);

                examname = (TextView) dialog.findViewById(R.id.nameofexam);
                exammarks = (TextView) dialog.findViewById(R.id.end_time);
                examsdate = (TextView) dialog.findViewById(R.id.date);

                Log.d("name",exam.getName());
                examname.setText(exam.getName());
                exammarks.setText(exam.getMax_marks());


                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");



                final String dateString = formatter.format(new Date(Long.parseLong(exam.getDate())));
                examsdate.setText(dateString);


                okButton = (Button) dialog.findViewById(R.id.doneButton);
                delButton = (Button) dialog.findViewById(R.id.delButton);
                editButton = (Button) dialog.findViewById(R.id.editButton);
                dialog.show();

                okButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        dialog.dismiss();

                    }
                });

                delButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        DatabaseReference dataRef= Constants.databaseReference.child(Constants.EXAMS_TABLE).child(institutionName).child(classId).child(subjectSelected).child(exam.getId());
                        dataRef.removeValue();


                        TeacherUserPrefs.examHashMap.remove(exam.getId());
                        TeacherUserPrefs.examidLt.remove(exam.getId());


                        dialog.dismiss();

                        Intent reload=new Intent(Exams.this,Exams.class);
                        reload.putExtra("role",role);
                        reload.putExtra("id",classId);
                        reload.putExtra("institution_name",institutionName);
                        startActivity(reload);

                    }
                });









                editButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {


                        final Dialog dialog_in = new Dialog(Exams.this);
                        dialog_in.setContentView(R.layout.exam_edit_layout);
                        dialog_in.setTitle("Edit Details");

                        setDialogSize(dialog_in);
                        nameexam = (EditText) dialog_in.findViewById(R.id.nameofexam);
                        marksexam = (EditText) dialog_in.findViewById(R.id.end_time);
                        dateexam = (TextView) dialog_in.findViewById(R.id.date);
                        EditButton = (Button) dialog_in.findViewById(R.id.editButton);
                        OkButton=(Button)dialog_in.findViewById(R.id.doneButton);
                        cal = (ImageButton) dialog_in.findViewById(R.id.dateIcon);
                        nameexam.setText(exam.getName());
                        marksexam.setText(exam.getMax_marks());
                        dateexam.setText(dateString);
                        final int[] flag = {0};

                        cal.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                open(view);
                                flag[0] = 1;
                                dateexam.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
                            }
                        });

                        OkButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog_in.dismiss();
                            }
                        });

                        EditButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {

                                if (nameexam.getText().equals("") || marksexam.getText().equals("") ) {
                                    Toast.makeText(getApplicationContext(), "Exam details cannot be empty!", Toast.LENGTH_LONG).show();
                                } else {

                                    DatabaseReference dataRef=Constants.databaseReference.child(Constants.EXAMS_TABLE).child(institutionName).child(classId).child(subjectSelected).child(exam.getId());


                                   dataRef.child("name").setValue(nameexam.getText().toString());
                                    dataRef.child("max_marks").setValue(marksexam.getText().toString());

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


                                    dataRef.child("date").setValue(String.valueOf(newmilliseconds));

                                    Exam newexam=new Exam(exam.getId(),nameexam.getText().toString(),String.valueOf(newmilliseconds),marksexam.getText().toString(),subjectSelected);
                                    TeacherUserPrefs.examHashMap.put(exam.getId(),newexam);






                                    dialog_in.dismiss();
                                    Intent reload=new Intent(Exams.this,Exams.class);
                                    reload.putExtra("insitution_name",institutionName);
                                    reload.putExtra("id",classId);
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







               /* Intent to_marksstudent = new Intent(Exams.this, teacher_marks_studentlist.class);
                to_marksstudent.putExtra("institution_name",institutionName);
                to_marksstudent.putExtra("examid", examid);
                to_marksstudent.putExtra("classId", classId);
                to_marksstudent.putExtra("role",role);

                startActivity(to_marksstudent);*/



            }
        });

        addExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Exams.this, NewExam.class);
                i.putExtra("institution_name",institutionName);
                i.putExtra("id",classId);
                i.putExtra("role",role);
                startActivity(i);
            }
        });




    }




protected void setExamList(){


    ArrayList<String> examsLt = new ArrayList<String>();

    for(int x=0;x<TeacherUserPrefs.examidLt.size();x++){

        Exam exam=TeacherUserPrefs.examHashMap.get(TeacherUserPrefs.examidLt.get(x));

        if(exam.getSubject().equalsIgnoreCase(subjectSelectSpinner.getSelectedItem().toString())) {

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String dateString = formatter.format(new Date(Long.parseLong(exam.getDate())));
            String entry = exam.getName() + " on " + dateString;
            examMap.put(entry, exam);
            examsLt.add(entry);
        }
    }

    ArrayAdapter adapter = new ArrayAdapter(Exams.this, android.R.layout.simple_list_item_1, examsLt);
    //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();
    examsList.setAdapter(adapter);


}




    public void open(View view)
    {

        final Dialog dialogcal = new Dialog(Exams.this);
        dialogcal.setContentView(R.layout.activity_calendar2);
        dialogcal.setTitle("Select Date");
        setDialogSize(dialogcal);
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
                dateexam.setText(dateFormat.format(date1), TextView.BufferType.EDITABLE);
                Toast.makeText(getApplicationContext(), Daycal + "/" + Monthcal + "/" + Yearcal, Toast.LENGTH_LONG).show();
                dialogcal.dismiss();

            }
        });
        dialogcal.show();



    }




    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(Exams.this,LoginActivity.class);
            startActivity(nouser);
        }
    }


}
