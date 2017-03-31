package com.project.smartedu.student;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class student_exams extends BaseActivity {



    private Toolbar mToolbar;
    Button addExamButton;
    private FragmentDrawer drawerFragment;


    ListView examsList;
    NotificationBar noti_bar;
    String classId;
    String studentId;
    String examid;
    String examName;
    ListView subjectList;

    Number totalMarks;
    Number marksObtained;

    TextView myExamName;
    TextView myTotalMarks;
    TextView myMarksObtained;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_exams);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Exams");

        Intent from_student = getIntent();
        classId = from_student.getStringExtra("classId");
        studentId = from_student.getStringExtra("studentId");
        role = from_student.getStringExtra("role");

        institutionName = from_student.getStringExtra("institution_name");

        noti_bar = (NotificationBar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role, institutionName);



        // examsList = (ListView) findViewById(R.id.examList);
        subjectList = (ListView) findViewById(R.id.examList);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);

        /*ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Class");
        studentQuery.whereEqualTo("class",classname);
        studentQuery.whereEqualTo("teacher",ParseUser.getCurrentUser());*/


        final HashMap<String, String> classMap = new HashMap<String, String>();


      /*  final ParseObject[] classRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        // classQuery.whereEqualTo(ClassTable.OBJECT_ID, classId);
        classQuery.whereEqualTo(ClassTable.CLASS_NAME, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME, classGradeId));
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                    if (studentListRet.size() != 0) {

                        ArrayList<String> classLt = new ArrayList<String>();
                        //ArrayAdapter adapter = new ArrayAdapter(teacher_exams.this, android.R.layout.simple_list_item_1, studentLt);
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();
                        ArrayAdapter subjectadapter = new ArrayAdapter(student_exams.this, android.R.layout.simple_list_item_1, classLt);

                        for (int x = 0; x < studentListRet.size(); x++) {
                            ParseObject u = (ParseObject) studentListRet.get(x);
                            // ParseObject classGradeObject = ((ParseObject) u.get(ClassTable.CLASS_NAME));

                            String name = u.getString(ClassTable.SUBJECT);

                            classMap.put(name, u.getObjectId());
                            subjectadapter.add(name);
                        }
                        // classRef[0] = studentListRet.get(0);
                        subjectList.setAdapter(subjectadapter);
                    } else {
                        Log.d("class", "error in query");
                    }
                } else {
                    Log.d("class", "error");
                }
            }
        });
*/

        subjectList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = ((TextView) view).getText().toString();
                displayExams(item, classMap.get(item));
            }
        });
    }



























    public void displayExams(String subject,String subjectObjectId)
    {
       /* classId=subjectObjectId;
        final Dialog examListDialog=new Dialog(student_exams.this);
        examListDialog.setContentView(R.layout.singelistdisplay);
        setDialogSize(examListDialog);
        examsList=(ListView)examListDialog.findViewById(R.id.examList);

        final HashMap<String,String> examMap=new HashMap<String,String>();
        final HashMap<String,Number> examMaxMarksMap=new HashMap<String,Number>();

        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(ExamTable.TABLE_NAME);
        studentQuery.whereEqualTo(ExamTable.FOR_CLASS,ParseObject.createWithoutData(ClassTable.TABLE_NAME,classId));
        studentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> examListRet, ParseException e) {
                if (e == null) {
                    if (examListRet.size() != 0) {
                        ArrayList<String> examLt = new ArrayList<String>();
                        //ArrayAdapter adapter = new ArrayAdapter(teacher_exams.this, android.R.layout.simple_list_item_1, studentLt);
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                        ArrayAdapter adapter = new ArrayAdapter(student_exams.this, android.R.layout.simple_list_item_1, examLt) {

                            @Override
                            public View getView(int position, View convertView,
                                                ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);

                                TextView textView = (TextView) view.findViewById(android.R.id.text1);

            *//*YOUR CHOICE OF COLOR*//*
                                textView.setTextColor(Color.WHITE);

                                return view;
                            }
                        };


                        Log.d("user", "Retrieved " + examListRet.size() + " students");
                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < examListRet.size(); i++) {
                            ParseObject u = (ParseObject) examListRet.get(i);
                            //  if(u.getString("class").equals(id)) {
                            String name = u.getString(ExamTable.EXAM_NAME);
                            //name += "\n";
                            // name += u.getInt("age");
                            examMap.put(name, u.getObjectId());
                            examMaxMarksMap.put(name, u.getNumber(ExamTable.MAX_MARKS));
                            adapter.add(name);
                            // }

                        }


                        examsList.setAdapter(adapter);
                        examListDialog.show();

                        examsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                                             @Override
                                                             public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                                                                 String item = ((TextView) view).getText().toString().trim();
                                                                 examSelected(item, examMap.get(item), examMaxMarksMap.get(item));

                                                             }
                                                         }

                        );


                    }else{
                        Toast.makeText(student_exams.this, "no exam added", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(student_exams.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });

*/

        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();
    }



    public void examSelected(String examName,String examObjectId,Number maxMarks)
    {


        examid = examObjectId;
        Log.d("user", "examId: " + examid);
        Log.d("user", "examId: " + studentId);
        this.examName =examName;
        totalMarks = maxMarks;

        /*ParseQuery<ParseObject> marksQuery = ParseQuery.getQuery(MarksTable.TABLE_NAME);
        marksQuery.whereEqualTo(MarksTable.STUDENT_USER_REF, ParseObject.createWithoutData(StudentTable.TABLE_NAME, studentId));
        marksQuery.whereEqualTo(MarksTable.EXAM_REF, ParseObject.createWithoutData(ExamTable.TABLE_NAME, examid));
        marksQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> marksListRet, ParseException e) {
                if (e == null) {

                    if (marksListRet.size() != 0) {
                        marksObtained = marksListRet.get(0).getNumber(MarksTable.MARKS_OBTAINED);
                        callDialog();
                    } else {
                        Toast.makeText(student_exams.this, "Not Yet Added", Toast.LENGTH_LONG).show();
                    }

                } else {
                    Log.d("user", "ErrorIn: " + e.getMessage());
                }
            }
        });
*/


    }


    public void callDialog(){
       /* Dialog dialog = new Dialog(student_exams.this);
        dialog.setContentView(R.layout.view_marks);
        dialog.setTitle("Marks Information");

        myExamName= (TextView) dialog.findViewById(R.id.exam);
        myMarksObtained= (TextView) dialog.findViewById(R.id.obtained);
        myTotalMarks= (TextView) dialog.findViewById(R.id.totalMarks);
        myExamName.setText(examName);
        myMarksObtained.setText(marksObtained.toString());
        myTotalMarks.setText(totalMarks.toString());

        dialog.dismiss();

        dialog.show();*/
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(student_exams.this,LoginActivity.class);
            startActivity(nouser);
        }
    }




}
