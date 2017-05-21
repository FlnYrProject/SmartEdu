package com.project.smartedu.teacher;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.parse.FindCallback;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.ImageAdapter;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.common.NewTask;
import com.project.smartedu.database.Exam;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class NewExam extends BaseActivity {


    EditText examName;
    EditText maxMarks;
    String ExamName;
    String marks;
    Button addExamButton;
    String classId;
    String subjectSelected;


    DatabaseReference databaseReference;

    Spinner subjectSelectSpinner;
    ArrayAdapter subjectadapter;

    ImageButton dateimagebutton;


    CalendarView calendar;

    int Year;
    int Month;
    int Day;
    Date date1;
    TextView DATE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_exam);
        classId=getIntent().getStringExtra("id");
        institutionName=getIntent().getStringExtra("institution_name");
        role=getIntent().getStringExtra("role");
       /* mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Task");*/

        examName = (EditText) findViewById(R.id.editText3);
        maxMarks = (EditText) findViewById(R.id.editText4);
        addExamButton = (Button) findViewById(R.id.button2);
        subjectSelectSpinner=(Spinner)findViewById(R.id.subjectListspinner);
        dateimagebutton= (ImageButton) findViewById(R.id.test);
DATE=(TextView)findViewById(R.id.date);
        dateimagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open();
            }
        });




        subjectadapter = new ArrayAdapter(NewExam.this, android.R.layout.simple_list_item_1, TeacherUserPrefs.subjectallotmentmap.get(classId));
        subjectSelectSpinner.setAdapter(subjectadapter);


        addExamButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExamName = examName.getText().toString();
                marks = maxMarks.getText().toString();
                subjectSelected=subjectSelectSpinner.getSelectedItem().toString();
                if (ExamName.equals("") || marks.equals("") || (DATE.getText().equals("Select Due Date"))) {
                    Toast.makeText(getApplicationContext(), "Exam details cannot be empty!", Toast.LENGTH_LONG).show();
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



                                //data sent to server
                                databaseReference= Constants.databaseReference.child(Constants.EXAMS_TABLE).child(institutionName).child(classId).child(subjectSelected).push();
                                databaseReference.child("name").setValue(ExamName);
                                databaseReference.child("max_marks").setValue(marks);
                                databaseReference.child("date").setValue(String.valueOf(milliseconds));


                                //data to local database
                                TeacherUserPrefs.examidLt.add(databaseReference.getKey());
                    Exam exam=new Exam(databaseReference.getKey(),ExamName,String.valueOf(milliseconds),marks,subjectSelected);
                    TeacherUserPrefs.examHashMap.put(databaseReference.getKey(),exam);

                                Toast.makeText(getApplicationContext(), "Event details successfully stored", Toast.LENGTH_LONG).show();

                                Intent to_exams = new Intent(NewExam.this,Exams.class);
                                to_exams.putExtra("institution_name",institutionName);
                                to_exams.putExtra("id", classId);
                                to_exams.putExtra("role",role);
                                startActivity(to_exams);
                                finish();







                }
            }
        });


    }



    public void open()
    {

        final Dialog dialog = new Dialog(NewExam.this);
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
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(NewExam.this,LoginActivity.class);
            startActivity(nouser);
        }
    }
}

