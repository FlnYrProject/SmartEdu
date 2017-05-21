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
import android.widget.Spinner;
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
import com.project.smartedu.UserPrefs;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewTeacher extends BaseActivity {

    String name;
    String email;
    String dob;
    String address;
    String contact;
    String parentname;
    String sex;

    int serial;


    Button addTeacherButton;
    EditText teacherName;
    EditText teacheremail;
    EditText teacherdob;
    EditText teacheraddress;
    EditText teachercontact;
    EditText teacherparent;
Spinner teachersex;

    String admin_pass;

    DatabaseReference databaseReference;

    FirebaseUser firebaseUser;
    FirebaseUser teacherfirebaseUser;


    String admin_email;

    UserPrefs userPrefs;


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


        userPrefs=new UserPrefs(NewTeacher.this);

        teacherName = (EditText) findViewById(R.id.teacherName);
        teacheremail=(EditText) findViewById(R.id.teacheremail);
        teacherdob = (EditText) findViewById(R.id.teacherdob);
        teachercontact = (EditText) findViewById(R.id.teachercontact);
        teacheraddress=(EditText) findViewById(R.id.teacheraddress);
        teacherparent=(EditText) findViewById(R.id.teacherparentname);
        teachersex=(Spinner)findViewById(R.id.teachersex);


        addTeacherButton = (Button) findViewById(R.id.addTeacherButton);


        addTeacherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = teacherName.getText().toString().trim();
                email =teacheremail.getText().toString();
                dob=teacherdob.getText().toString();
                contact=teachercontact.getText().toString();
                address=teacheraddress.getText().toString();
                parentname=teacherparent.getText().toString();
                sex=teachersex.getSelectedItem().toString();

                if (name.equals(null) || email.equals(null) || dob.equals(null) || contact.equals(null) || address.equals(null) || parentname.equals(null) ) {
                    Toast.makeText(getApplicationContext(), "Teacher details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
/*

                    final Dialog enter_password_dialog = new Dialog(NewTeacher.this);
                    enter_password_dialog.setContentView(R.layout.enter_password);
                    enter_password_dialog.setTitle("Provide authentication");

                    setDialogSize(enter_password_dialog);
                    admin_pass = (EditText) enter_password_dialog.findViewById(R.id.admin_pass);

                    ok = (Button) enter_password_dialog.findViewById(R.id.done);
                    enter_password_dialog.show();
*/

                    admin_pass = userPrefs.getUserPassword();
                    admin_email=firebaseUser.getEmail();

                    firebaseAuth.signInWithEmailAndPassword(admin_email,admin_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if(task.isSuccessful()){

                                addTeacherUser(name,sex,dob,contact,address,parentname,email, firebaseUser);
                                sleep(3000);

                            }else{

                                Toast.makeText(getApplicationContext(),"Incorrect Authentication",Toast.LENGTH_LONG).show();

                            }

                        }
                    });

                   /* ok.setOnClickListener(new View.OnClickListener() {
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
                    });*/



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


    protected void addTeacherUser(final String Name,final String sex,final String DOB,final String contact,final String address, final String parentname, String email, final FirebaseUser prefirebaseuser)
    {

        String password="qwerty";


        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE);
                    teacherfirebaseUser = firebaseAuth.getCurrentUser();
                    databaseReference.child(teacherfirebaseUser.getUid()).child("name").setValue(Name);
                    databaseReference.child(teacherfirebaseUser.getUid()).child("address").setValue(address);
                    databaseReference.child(teacherfirebaseUser.getUid()).child("dob").setValue(DOB);
                    databaseReference.child(teacherfirebaseUser.getUid()).child("contact").setValue(contact);
                    databaseReference.child(teacherfirebaseUser.getUid()).child("parent_name").setValue(parentname);
                    databaseReference.child(teacherfirebaseUser.getUid()).child("sex").setValue(sex);

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

       firebaseAuth.signInWithEmailAndPassword(admin_email,admin_pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
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
