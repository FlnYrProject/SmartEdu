package com.project.smartedu.admin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewTeacher extends BaseActivity {

    String name;

    int age=0;
    int serial;
    String email;

    Button addTeacherButton;
    EditText teacherName;
    EditText teacherAge;
    EditText teacheremail;


    String classId;
    String parentId;
    String studentId;

    EditText admin_pass;
    Button ok;

    DatabaseReference databaseReference;

    FirebaseUser firebaseUser;
    FirebaseUser teacherfirebaseUser;


    String pass,admin_email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_teacher);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent from_teachers = getIntent();
        role = from_teachers.getStringExtra("role");
        institutionName=from_teachers.getStringExtra("institution_name");

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseUser=firebaseAuth.getCurrentUser();



        teacherName = (EditText) findViewById(R.id.teacherName);
        teacherAge = (EditText) findViewById(R.id.teacherAge);
        teacheremail=(EditText) findViewById(R.id.teacheremail);
        addTeacherButton = (Button) findViewById(R.id.addTeacherButton);


        addTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = teacherName.getText().toString().trim();
                age = Integer.parseInt(teacherAge.getText().toString().trim());
                email =teacheremail.getText().toString();


                if (name.equals(null) || (age == 0) ) {
                    Toast.makeText(getApplicationContext(), "Teacher details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {

                    final Dialog enter_password_dialog = new Dialog(NewTeacher.this);
                    enter_password_dialog.setContentView(R.layout.enter_password);
                    enter_password_dialog.setTitle("Provide authentication");

                    setDialogSize(enter_password_dialog);
                    admin_pass = (EditText) enter_password_dialog.findViewById(R.id.admin_pass);

                    ok = (Button) enter_password_dialog.findViewById(R.id.done);
                    enter_password_dialog.show();

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            admin_email=firebaseUser.getEmail();

                            firebaseAuth.signInWithEmailAndPassword(admin_email,admin_pass.getText().toString()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if(task.isSuccessful()){
                                        pass=admin_pass.getText().toString();
                                        enter_password_dialog.dismiss();
                                        addTeacherUser(name, age,email, firebaseUser);
                                        sleep(3000);

                                    }else{

                                        Toast.makeText(getApplicationContext(),"Incorrect Authentication",Toast.LENGTH_LONG).show();

                                    }

                                }
                            });


                        }
                    });



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


    protected void addTeacherUser(final String Name,int Age,String email, final FirebaseUser prefirebaseuser)
    {

        String password=institutionName + Name + institutionName;


        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE);
                    teacherfirebaseUser = firebaseAuth.getCurrentUser();
                    databaseReference.child(teacherfirebaseUser.getUid()).child("name").setValue(Name);
                    DatabaseReference dataReference = databaseReference.child(teacherfirebaseUser.getUid()).child("role").child("teacher").push();
                    dataReference.setValue(institutionName);


                    Toast.makeText(getApplicationContext(), "Teacher User Registration Successful ", Toast.LENGTH_LONG).show();




                    addTeacher(teacherfirebaseUser);
                }else{

                    Toast.makeText(getApplicationContext(), "Teacher User Registration Unsuccessful ", Toast.LENGTH_LONG).show();

                }



            }
        });





    }



    protected void addTeacher(FirebaseUser firebaseUser){

        databaseReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName);
/*

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, Object> teachermap=(HashMap<String, Object>)dataSnapshot.getValue();

                    serial=teachermap.size()  ;
                Toast.makeText(getApplicationContext(), " serial = " + serial, Toast.LENGTH_LONG).show();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        serial=AdminUserPrefs.teacherLt.size()+1;

        String entry=String.valueOf(serial)+ ". " + teacherName.getText().toString().trim();        //saving to local list
        AdminUserPrefs.teacherLt.add(entry);
        AdminUserPrefs.teacheruseridLt.add(firebaseUser.getUid());
        AdminUserPrefs.teachersusermap.put(entry,firebaseUser.getUid());

        databaseReference.child(firebaseUser.getUid()).setValue(String.valueOf(serial));

        Toast.makeText(getApplicationContext(), "Teacher details successfully stored with serial = " + serial, Toast.LENGTH_LONG).show();

        loginAdminBack();

    }



   protected void loginAdminBack(){

       firebaseAuth.signInWithEmailAndPassword(admin_email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if(task.isSuccessful()){
                   Intent i = new Intent(NewTeacher.this, Teachers.class);
                   i.putExtra("institution_name", institutionName);
                   i.putExtra("role", role);
                   startActivity(i);
               }else{
                   Toast.makeText(getApplicationContext(), "signed out due to some error,please sign in again", Toast.LENGTH_LONG).show();
                   firebaseAuth.signOut();
                   startActivity(new Intent(NewTeacher.this, LoginActivity.class));

               }
           }
       });

   }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent i = new Intent(NewTeacher.this, Teachers.class);
        i.putExtra("institution_name", institutionName);
        i.putExtra("role", role);
        startActivity(i);

    }

}
