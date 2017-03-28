package com.project.smartedu.teacher;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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
import com.parse.SignUpCallback;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Students extends BaseActivity {

    private Toolbar mToolbar;
    Button addStudentButton;
    private FragmentDrawer drawerFragment;

    UserPrefs userPrefs;


    ListView studentList;
   NotificationBar noti_bar;
    String classId;
    String name;
    Integer age;
    Integer rollno;
    Button createIDs;

    HashMap<String ,String> localstumap;

    int no_of_stu=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students);

        Intent from_student = getIntent();
        classId = from_student.getStringExtra("id");
        role=from_student.getStringExtra("role");

        institutionName=from_student.getStringExtra("institution_name");


        userPrefs=new UserPrefs(Students.this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
      /*  getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        getSupportActionBar().setTitle("Students");
       noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);


        addStudentButton = (Button)findViewById(R.id.addButton);
        studentList = (ListView) findViewById(R.id.studentList);
        createIDs=(Button)findViewById(R.id.shareCode);
        createIDs.setVisibility(View.INVISIBLE);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

      /*  noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(),role,institutionName);*/

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);
        Toast.makeText(Students.this, "id class selected is = " +classId, Toast.LENGTH_LONG).show();

        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Students.this, NewStudent.class);
                i.putExtra("institution_name",institutionName);
                i.putExtra("role",role);
                i.putExtra("id",classId);
                i.putExtra("no_of_stu",no_of_stu);
                startActivity(i);
            }
        });

        ArrayList<String> studentLt = new ArrayList<String>();
        localstumap=new HashMap<>();

        for(int x=0;x<TeacherUserPrefs.studentsuseridLt.size();x++){

            String studentid=TeacherUserPrefs.studentsuseridLt.get(x);
            com.project.smartedu.database.Students student=TeacherUserPrefs.studentsHashMap.get(studentid);


            if(student.getClass_id().equals(classId)){


                studentLt.add(student.getRoll_number() + ". " + student.getName());

                localstumap.put(student.getRoll_number() + ". " + student.getName(),studentid);

            }




        }

        if(studentLt.size()>0){
            no_of_stu=studentLt.size();
            ArrayAdapter adapter = new ArrayAdapter(Students.this, android.R.layout.simple_list_item_1, studentLt);
            studentList.setAdapter(adapter);
            createIDs.setVisibility(View.VISIBLE);
            createIDs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareCode();
                }
            });


            studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String item = ((TextView) view).getText().toString();
                    Log.d("user", item);

                    //item  = item.replaceAll("[\n\r\\s]", "");
                    String[] studentdetails = item.split("\\. ");

                   String rn=studentdetails[0];
                    String nm=studentdetails[1];



                    Log.d("user", "rno: " + rn.trim()+" name "+nm);


                    String stuid=localstumap.get(item);

                    Intent to_student_info = new Intent(Students.this, StudentInfo.class);
                    to_student_info.putExtra("id", stuid);
                    to_student_info.putExtra("classId", classId);
                    to_student_info.putExtra("institution_name",institutionName);
                    startActivity(to_student_info);




                }
            });


        }else{

            Toast.makeText(Students.this, " no students in this class ", Toast.LENGTH_LONG).show();


        }









        /*final ParseObject[] classGradeRef = new ParseObject[1];
        ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQuery.whereEqualTo(ClassTable.OBJECT_ID,classId);
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    Log.d("class", "Retrieved the class");
                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                    classGradeRef[0] = (ParseObject) studentListRet.get(0).get(ClassTable.CLASS_NAME);

                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
                    studentQuery.whereEqualTo(StudentTable.CLASS_REF, classGradeRef[0]);
                    studentQuery.addAscendingOrder(StudentTable.ROLL_NUMBER);
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {

                                ArrayList<String> studentLt = new ArrayList<String>();
                                ArrayAdapter adapter = new ArrayAdapter(Students.this, android.R.layout.simple_list_item_1, studentLt);
                                //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

                                Log.d("user", "Retrieved " + studentListRet.size() + " students");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                                for (int i = 0; i < studentListRet.size(); i++) {
                                    ParseObject u = (ParseObject) studentListRet.get(i);
                                    //  if(u.getString("class").equals(id)) {
                                    int rollnumber=u.getInt(StudentTable.ROLL_NUMBER);
                                    String name = u.getString(StudentTable.STUDENT_NAME);
                                    name= String.valueOf(rollnumber) + ". " + name;
                                    //name += "\n";
                                    // name += u.getInt("age");

                                    adapter.add(name);
                                    // }

                                }


                                studentList.setAdapter(adapter);
                                createIDs.setVisibility(View.VISIBLE);
                                createIDs.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        shareCode();
                                    }
                                });


                                studentList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String item = ((TextView) view).getText().toString();
                                        Log.d("user", item);

                                        //item  = item.replaceAll("[\n\r\\s]", "");
                                        String[] itemValues = item.split("\\. ");

                                        final String[] details = new String[2];
                                        int j = 0;
                                        for(int i=0; i<=1; i++){
                                            Log.d("user", itemValues[i]);
                                        }

                                        for (String x : itemValues) {
                                            details[j++] = x;
                                        }

                                        Log.d("user", "rno: " + details[0].trim()+"name "+details[1]);  //extracts Chit as Chi and query fails???

                                        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
                                        studentQuery.whereEqualTo(StudentTable.ROLL_NUMBER, Integer.parseInt(details[0].trim()));
                                        studentQuery.whereEqualTo(StudentTable.STUDENT_NAME, details[1].trim());
                                        studentQuery.whereEqualTo(StudentTable.CLASS_REF, classGradeRef[0]);
                                        studentQuery.findInBackground(new FindCallback<ParseObject>() {
                                            public void done(List<ParseObject> studentListRet, ParseException e) {
                                                if (e == null) {
                                                    if(studentListRet.size()!=0) {
                                                        ParseObject u = (ParseObject) studentListRet.get(0);
                                                        String id = u.getObjectId();
                                                        // Toast.makeText(Students.this,"id of student selected is = " + id, Toast.LENGTH_LONG).show();
                                                        Intent to_student_info = new Intent(Students.this, StudentInfo.class);
                                                        to_student_info.putExtra("id", id);
                                                        to_student_info.putExtra("classId", classId);
                                                        startActivity(to_student_info);
                                                    }
                                                } else {
                                                    Log.d("user", "Error: " + e.getMessage());
                                                }
                                            }
                                        });


                                    }
                                });


                            } else {
                                Toast.makeText(Students.this, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });


                } else {
                    Toast.makeText(Students.this, "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });



        // Toast.makeText(Students.this, "object id = " + classRef[0].getObjectId(), Toast.LENGTH_LONG).show();



        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Students.this, NewStudent.class);
                i.putExtra("institution_name",institution_name);
                i.putExtra("institution_code",institution_code);
                i.putExtra("id",classId);
                startActivity(i);
            }
        });*/




    }

    public void shareCode(){

/*

        ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery(StudentTable.TABLE_NAME);
        studentQuery.whereEqualTo(StudentTable.CLASS_REF, ParseObject.createWithoutData("Class",classId));
        studentQuery.whereEqualTo(StudentTable.ADDED_BY_USER_REF, null);
        studentQuery.whereEqualTo(StudentTable.STUDENT_USER_REF, null);
        studentQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> studentListRet, ParseException e) {
                if (e == null) {
                    if(studentListRet.size()==0){
                        Toast.makeText(Students.this, "No ID to be added. Already Updated", Toast.LENGTH_LONG).show();
                    }
                    else{
                        for(int i=0; i<studentListRet.size(); i++) {
                            ParseObject u = studentListRet.get(i);
                            name = u.getString(StudentTable.STUDENT_NAME);
                            age = u.getInt(StudentTable.STUDENT_AGE);
                            rollno = u.getInt(StudentTable.ROLL_NUMBER);

                            final String sessionToken = ParseUser.getCurrentUser().getSessionToken();
                            addStudentUser(name, age, rollno, sessionToken, u);
                            sleep(10000);
                            addParentUser(name, age, rollno, sessionToken);
                            Log.d("shareCode", "Main: ");
                            // u.put("addedBy", ParseUser.getCurrentUser());
                            //u.saveEventually();
                        }
                    }


                } else {
                    //Toast.makeText(NewStudent.this, "errorInner", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });
*/




    }

    protected void sleep(int time)
    {
        for(int x=0;x<time;x++)
        {

        }
    }
    protected void addStudentUser(final String Name, int Age, final int Rollno, final String presession, final ParseObject u)
    {
     /*   final ParseUser[] userRef = new ParseUser[1];
        // Set up a new Parse user
        final ParseUser user_student = new ParseUser();
        user_student.setUsername(Name + Rollno);
        user_student.setPassword(Rollno + Name + Rollno);


        user_student.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {// Handle the response

                if (e != null) {
                    // Show the error message
                    Toast.makeText(Students.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user_student;
                    // Toast.makeText(Students.this, "Student User made " + " " + user_student.getObjectId(), Toast.LENGTH_LONG).show();
                    Log.d("role", "added Student role of " + user_student.getObjectId());
                    ParseObject roleobject = new ParseObject(RoleTable.TABLE_NAME);
                    roleobject.put(RoleTable.OF_USER_REF, user_student);
                    roleobject.put(RoleTable.ROLE, "Student");
                    roleobject.saveInBackground();

                    ParseObject parent=new ParseObject(ParentTable.TABLE_NAME);
                    parent.put(ParentTable.CHILD_USER_REF, user_student);
                    parent.saveEventually();

                    u.put("userId", user_student);


                    try {

                        ParseUser.become(presession);
                        // Log.d("student", "adding userId");
                        // u.put("userId", user_student);
                        u.put(StudentTable.ADDED_BY_USER_REF,ParseUser.getCurrentUser());

                    } catch (ParseException e1) {
                        Toast.makeText(Students.this, "cant add student",
                                Toast.LENGTH_LONG).show();
                    }
                    u.saveInBackground();
                    //addStudent(userRef[0],rollno);

                }

            }
        });
*/
    }





    protected void addParentUser(final String Name, int Age, final int Rollno, final String presession)
    {
       /* final ParseUser[] userRef = {new ParseUser()};
        // Set up a new Parse user
        final ParseUser user_parent = new ParseUser();
        user_parent.setUsername("parent_" + Name + Rollno);
        user_parent.setPassword(Rollno + Name + Rollno);


        user_parent.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {// Handle the response

                if (e != null) {
                    // Show the error message
                    Toast.makeText(Students.this, e.getMessage(),
                            Toast.LENGTH_LONG).show();
                } else {
                    userRef[0] = user_parent;
                    // Toast.makeText(Students.this, "Parent User made "+ " "+user_parent.getObjectId(),Toast.LENGTH_LONG).show();
                    Log.d("role", "added Parent role of " + user_parent.getObjectId());
                    ParseObject roleobject = new ParseObject(RoleTable.TABLE_NAME);
                    roleobject.put(RoleTable.OF_USER_REF, user_parent);
                    roleobject.put(RoleTable.ROLE, "Parent");
                    roleobject.saveInBackground();




                    ParseQuery<ParseUser> user=ParseUser.getQuery();
                    user.whereEqualTo("username",Name +String.valueOf(Rollno));
                    Log.d("user", Name+String.valueOf(Rollno));
                    user.findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            if (e == null) {
                                if (objects.size() != 0) {
                                    Log.d("user", "student user found");
                                    ParseUser student_user = objects.get(0);
                                    ParseQuery<ParseObject> parent_relation = ParseQuery.getQuery(ParentTable.TABLE_NAME);
                                    parent_relation.whereEqualTo(ParentTable.CHILD_USER_REF, student_user);
                                    parent_relation.findInBackground(new FindCallback<ParseObject>() {
                                        @Override
                                        public void done(List<ParseObject> objects, ParseException e) {
                                            if (e == null) {
                                                if (objects.size() != 0) {
                                                    objects.get(0).put(ParentTable.PARENT_USER_REF, user_parent);
                                                    objects.get(0).saveEventually();
                                                } else {

                                                }

                                            } else {
                                                Log.d("user", "relation not found");
                                            }
                                        }
                                    });
                                } else {
                                    Log.d("user", "query logic error in student...size = " + objects.size());
                                }
                            } else {
                                Log.d("user", "no student");
                            }
                        }
                    });





                    try {

                        ParseUser.become(presession);
                    } catch (ParseException e1) {
                        Toast.makeText(Students.this,"cant add parent",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });*/

    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(Students.this,LoginActivity.class);
            startActivity(nouser);
        }
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent(Students.this,MainActivity.class);
                startActivity(i);
                finish();
                //do your own thing here
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }*/

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent task_intent = new Intent(Students.this, Classes.class);
        task_intent.putExtra("institution_name", institutionName);
        task_intent.putExtra("for","attendance");
        task_intent.putExtra("role", role);
        //task_intent.putExtra("id", classId);
        startActivity(task_intent);
    }
}
