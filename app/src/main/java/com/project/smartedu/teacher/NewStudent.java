package com.project.smartedu.teacher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.admin.NewTeacher;
import com.project.smartedu.admin.Teachers;
import com.project.smartedu.notification.NotificationBar;

import java.util.List;

import static com.project.smartedu.Constants.databaseReference;

public class NewStudent extends BaseActivity {

    private Toolbar mToolbar;
    String name;
    String email;
    String dob;
    String address;
    String contact;
    String parentname;
    String parentemail;
    String sex;
    int rollno= -1;

    EditText studentName;
    EditText studentEmail;
    EditText studentdob;
    EditText studentaddress;
    EditText studentcontact;
    EditText studentparent;
    EditText studentparentemail;
    Spinner studentsex;

    NotificationBar noti_bar;
    String classId;

    UserPrefs userPrefs;
    TeacherUserPrefs teacherUserPrefs;

    DatabaseReference databaseReference;
    FirebaseUser studentfirebaseUser;
    FirebaseUser parentfirebaseuser;

    String studentuid;

    int no_of_stu=0;

    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_student);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("New Student");

        Intent from_students = getIntent();
        classId = from_students.getStringExtra("id");
       role=from_students.getStringExtra("role");
        institutionName=from_students.getStringExtra("institution_name");
        no_of_stu=from_students.getIntExtra("no_of_stu",0);
        pd=new ProgressDialog(NewStudent.this);
        pd.setMessage("Adding Student...");
        userPrefs=new UserPrefs(NewStudent.this);
        teacherUserPrefs=new TeacherUserPrefs(NewStudent.this);

        studentName=(EditText)findViewById(R.id.studentName);
        studentEmail = (EditText) findViewById(R.id.studentemail);
        studentdob = (EditText) findViewById(R.id.studentdob);
        studentcontact=(EditText)findViewById(R.id.studentcontact);
        studentaddress = (EditText) findViewById(R.id.studentaddress);
        studentparent = (EditText) findViewById(R.id.studentparentname);
        studentparentemail=(EditText)findViewById(R.id.studentparentemail);
        studentsex=(Spinner)findViewById(R.id.studentsex);




        Button addStudentButton = (Button) findViewById(R.id.addstudentButton);


        noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), "Teacher",institutionName);
        Log.i("abcd", "studentEmail is......" + studentEmail);

        addStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name=studentName.getText().toString().trim();
                email = studentEmail.getText().toString().trim();
                dob=studentdob.getText().toString().trim();
                address = studentaddress.getText().toString().trim();
                contact=studentcontact.getText().toString().trim();
                parentemail = studentparentemail.getText().toString().trim();
                parentname=studentparent.getText().toString().trim();
                sex=studentsex.getSelectedItem().toString();

                rollno = no_of_stu+1;

                pd.setCancelable(false);
                if ( name.equals("") || email.equals("") || dob.equals("") || address.equals("") || contact.equals("") || parentname.equals("") || parentemail.equals("") || email.equals("")  ) {
                    Toast.makeText(getApplicationContext(), "Student details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {

                    pd.show();
                    addStudentUser();



                }
            }
        });
    }




    protected void sleep(int time)
    {
        for(int x=0;x<time;x++)
        {

        }
    }

    protected void addStudentUser()
    {
        String password="qwerty";


        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE);
                    studentfirebaseUser = firebaseAuth.getCurrentUser();
                    studentuid=firebaseAuth.getCurrentUser().getUid();
                    databaseReference.child(studentfirebaseUser.getUid()).child("name").setValue(name);
                    databaseReference.child(studentfirebaseUser.getUid()).child("address").setValue(address);
                    databaseReference.child(studentfirebaseUser.getUid()).child("dob").setValue(dob);
                    databaseReference.child(studentfirebaseUser.getUid()).child("contact").setValue(contact);
                    databaseReference.child(studentfirebaseUser.getUid()).child("parent_name").setValue(parentname);
                    databaseReference.child(studentfirebaseUser.getUid()).child("parent_email").setValue(parentemail);
                    databaseReference.child(studentfirebaseUser.getUid()).child("sex").setValue(sex);

                    DatabaseReference dataReference = databaseReference.child(studentfirebaseUser.getUid()).child("role").child("student").push();
                    dataReference.setValue(institutionName);


                 //   Toast.makeText(getApplicationContext(), "Student User Registration Successful ", Toast.LENGTH_LONG).show();




                    addStudent();
                }else{

                    pd.dismiss();
                    Toast.makeText(getApplicationContext(), "Student User Registration Unsuccessful ", Toast.LENGTH_LONG).show();

                }



            }
        });

    }





    protected void addStudent(){
        // Toast.makeText(NewStudent.this, "Student User made "+ userRef+" "+ParseUser.getCurrentUser().getObjectId(),Toast.LENGTH_LONG).show();
        String [] classitems=classId.split("_");


        //adding to class table
        databaseReference=Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(classitems[1]).child(classitems[2]).child("student");
        databaseReference.child(studentuid).setValue(String.valueOf(rollno));


        //adding to student table
        databaseReference=Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentfirebaseUser.getUid());
        databaseReference.child("class").setValue(classId);
        databaseReference.child("roll_number").setValue(String.valueOf(rollno));


        //local changes
        com.project.smartedu.database.Students newstudent=new com.project.smartedu.database.Students(studentuid,classId,String.valueOf(rollno),name);
        TeacherUserPrefs.studentsuseridLt.add(studentuid);
        TeacherUserPrefs.studentsHashMap.put(studentuid,newstudent);

        addParentUser();

   //     Toast.makeText(getApplicationContext(), "Student details successfully stored", Toast.LENGTH_LONG).show();



    }




    protected void addParentUser()
    {
        String password="qwerty";



        firebaseAuth.createUserWithEmailAndPassword(parentemail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE);
                    parentfirebaseuser = firebaseAuth.getCurrentUser();
                    databaseReference.child( parentfirebaseuser.getUid()).child("name").setValue(parentname);
                    databaseReference.child( parentfirebaseuser.getUid()).child("address").setValue(address);
                    databaseReference.child( parentfirebaseuser.getUid()).child("contact").setValue(contact);
                    DatabaseReference dataReference = databaseReference.child( parentfirebaseuser.getUid()).child("role").child("parent").push();
                    dataReference.setValue(studentuid);



                    databaseReference=Constants.databaseReference.child(Constants.PARENT_RELATION_TABLE).child(studentuid);
                    databaseReference.setValue(parentfirebaseuser.getUid());



              //      Toast.makeText(getApplicationContext(), "Parent User Registration Successful ", Toast.LENGTH_LONG).show();
                    loginTeacherBack();

                }else{
                    pd.dismiss();

                    //delete student data too

                    String [] classitems=classId.split("_");

                    //deleting from server

                    //deleting from class table
                    databaseReference=Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(classitems[1]).child(classitems[2]).child("student");
                    databaseReference.child(studentfirebaseUser.getUid()).removeValue();
                    //deleting from student table
                    databaseReference=Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentfirebaseUser.getUid());
                  databaseReference.removeValue();
                    //deleting from user table
                    databaseReference=Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(studentfirebaseUser.getUid());
                    databaseReference.removeValue();
                    studentfirebaseUser.delete();



                    Toast.makeText(getApplicationContext(), "Parent User Registration Unsuccessful ", Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }



            }
        });


    }





    protected void loginTeacherBack(){

        firebaseAuth.signInWithEmailAndPassword(userPrefs.getUserEmail(),userPrefs.getUserPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

               pd.dismiss();
                if(task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "Student User Registration Successful ", Toast.LENGTH_LONG).show();

                    Intent i = new Intent(NewStudent.this, Students.class);
                    i.putExtra("role",role);
                    i.putExtra("institution_name",institutionName);
                    i.putExtra("id", classId);
                    startActivity(i);
                }else{

                    Toast.makeText(getApplicationContext(), "signed out due to some error,please sign in again", Toast.LENGTH_LONG).show();
                   teacherUserPrefs.clearTeacherDetails();
                    userPrefs.clearUserDetails();

                }
            }
        });

    }



    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(NewStudent.this,LoginActivity.class);
            startActivity(nouser);
        }
    }
}
