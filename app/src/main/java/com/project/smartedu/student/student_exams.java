package com.project.smartedu.student;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
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
import com.project.smartedu.UserPrefs;
import com.project.smartedu.database.Exam;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.parent.ParentUserPrefs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class student_exams extends BaseActivity {



    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;


    ListView examsList;
    NotificationBar noti_bar;
    String classId;
    String studentId;
    String examid;
    String examName;
    String subject;


    Number totalMarks;
    Number marksObtained;

    TextView myExamName;
    TextView myTotalMarks;
    TextView myMarksObtained;



DatabaseReference databaseReference;

    UserPrefs userPrefs;
    ParentUserPrefs parentUserPrefs;
    StudentUserPrefs studentUserPrefs;





    HashMap<String,String> exammarksobtMap;             //exam id--> marks obtained
    HashMap<String,Exam> examMap;               //exam id---> exam








    private class ExamResultItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public ExamResultItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Exams Data");
            pd.setCancelable(false);
            pd.show();
            databaseReference= Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentId).child("exam").child(subject);
            exammarksobtMap.clear();
            examMap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {

                            Log.d("nowdata","sname = " + ds.getKey());          //gives exam id

                            String subject=ds.getKey();
                            Exam exam=new Exam();
                            exam.setId(ds.getKey());
                            exam.setSubject(subject);

                            for(DataSnapshot dataSnapshot1:ds.getChildren()){
                                //dataSnapshot1.getKey() gives exam details

                                if(dataSnapshot1.getKey().equalsIgnoreCase("name")){
                                    exam.setName(dataSnapshot1.getValue().toString());
                                }

                                if(dataSnapshot1.getKey().equalsIgnoreCase("date")){
                                    exam.setDate(dataSnapshot1.getValue().toString());
                                }

                                if(dataSnapshot1.getKey().equalsIgnoreCase("max_marks")){
                                    exam.setMax_marks(dataSnapshot1.getValue().toString());
                                }

                                if(dataSnapshot1.getKey().equalsIgnoreCase("marks_obtained")){
                                    exammarksobtMap.put(ds.getKey(),dataSnapshot1.getValue().toString());
                                }


                            }

                            examMap.put(ds.getKey(),exam);

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

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
//                   Toast.makeText(async_context,userPrefs.getUserName(),Toast.LENGTH_LONG).show();
                    setExamSubjectList();
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
        setContentView(R.layout.activity_student_exams);
        exammarksobtMap=new HashMap<>();
        examMap=new HashMap<>();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Exams");

        Intent from_student = getIntent();
        classId = from_student.getStringExtra("classId");
        studentId = from_student.getStringExtra("studentId");
        role = from_student.getStringExtra("role");
        subject=from_student.getStringExtra("subject");
        institutionName = from_student.getStringExtra("institution_name");

        userPrefs=new UserPrefs(student_exams.this);
        parentUserPrefs=new ParentUserPrefs(student_exams.this);
        studentUserPrefs=new StudentUserPrefs(student_exams.this);

        noti_bar = (NotificationBar) getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), role, institutionName);



        // examsList = (ListView) findViewById(R.id.examList);
        examsList = (ListView) findViewById(R.id.examdetailList);

        exammarksobtMap=new HashMap<>();
        examMap=new HashMap<>();

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);


        ExamResultItems examResultItems=new ExamResultItems(student_exams.this);
        examResultItems.execute();

    }



    public void setExamSubjectList(){

        if (examMap.size()==0){
            Toast.makeText(student_exams.this,"No exam result uploaded",Toast.LENGTH_LONG).show();
        }else{


                    ArrayList<String> exammarksitems=new ArrayList<>();

                    for (String examId:examMap.keySet()){

                        Exam exam=examMap.get(examId);



                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String dateString = formatter.format(new Date(Long.parseLong(exam.getDate())));

                            String entry=exam.getName() + " on " + dateString + "\nMaximum Marks = " + exam.getMax_marks() +"\nMarks Obtained = " + exammarksobtMap.get(exam.getId());

                            exammarksitems.add(entry);
                        Log.d("testing",entry);



                    }

                    ArrayAdapter marksadapter = new ArrayAdapter(student_exams.this, android.R.layout.simple_list_item_1, exammarksitems);


                    examsList.setAdapter(marksadapter);










        }

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
