package com.project.smartedu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.admin.Home;
import com.project.smartedu.notification.NotificationBar;

public class LoginActivity extends BaseActivity {

    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    TextView notAlreadyUserText;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
boolean ifadmin;
String institutionName;
    UserPrefs userPrefs;













    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = (EditText)findViewById(R.id.userEmailInput);
        passwordInput = (EditText)findViewById(R.id.userPasswordInput);
        loginButton=(Button)findViewById(R.id.loginButton);
        notAlreadyUserText=(TextView)findViewById(R.id.notAlreadyUser);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference=Constants.databaseReference;

        userPrefs=new UserPrefs(getApplicationContext());

        if(firebaseAuth.getCurrentUser()!=null){

       // adminCheck();

            //check shared prefs
            if(userPrefs.isAdmin()){

                    Intent toAdminConsole=new Intent(LoginActivity.this, Home.class);
                toAdminConsole.putExtra("institution_name",userPrefs.getInstitution());
                    startActivity(toAdminConsole);

            }else{


                Intent toChooseRole=new Intent(LoginActivity.this, ChooseRole.class);
                startActivity(toChooseRole);

            }

        }

        notAlreadyUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedNotUserAlready();
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email=emailInput.getText().toString().trim();
                String password=passwordInput.getText().toString().trim();
                progressDialog.setMessage("Logging In");
                progressDialog.show();
              //  firebase_login authTask = new firebase_login(LoginActivity.this);
                clickedLogin(email,password);
              //  authTask.execute(email, password);

            }
        });

    }


    private void clickedNotUserAlready(){
        finish();
        Intent toSignUp=new Intent(this,SignUp.class);
        startActivity(toSignUp);
    }



    private void clickedLogin(String email,String password) {

        ifadmin = false;
        institutionName=null;



        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {

            Toast.makeText(getApplicationContext(), "Fields left blank", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();

        } else {

            Log.d("inds","ohoo");
            firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Log.d("inds","ohoo2");

                        Toast.makeText(getApplicationContext(), "User Login Successful", Toast.LENGTH_LONG).show();
                        adminCheck();

                    } else {
                        Log.d("inds","ohoo3");
                        Toast.makeText(getApplicationContext(), "User Login Failed", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();


                    }


                }


            });


        }

    }



    public void adminCheck(){

        databaseReference= databaseReference.child(Constants.INSTITUTION_TABLE);


        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                    for (DataSnapshot ds : dataSnapshot.getChildren()) {

                        if (ds.getKey().equals(firebaseAuth.getCurrentUser().getUid())) {
                            ifadmin = true;
                            institutionName = ds.getValue().toString();
                         Log.d("inds",ifadmin + institutionName);
                            break;
                        }

                    }


                String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();


                 userPrefs.setUserDetails(FirebaseAuth.getInstance().getCurrentUser().getUid(),"",emailInput.getText().toString(),passwordInput.getText().toString());

                userPrefs.setIfAdmin(ifadmin,institutionName);
                userPrefs.setFirstLoading(true);
                progressDialog.dismiss();

                if (ifadmin) {


                    Toast.makeText(getApplicationContext(), "Welcome " + institutionName + " admin", Toast.LENGTH_LONG).show();
                    Intent toAdminConsole = new Intent(LoginActivity.this, Home.class);
                    toAdminConsole.putExtra("institution_name",institutionName);
                    startActivity(toAdminConsole);

                } else {

                    Intent toChooseRole = new Intent(LoginActivity.this, ChooseRole.class);
                    startActivity(toChooseRole);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tohome=new Intent(LoginActivity.this,LoginActivity.class);
        startActivity(tohome);
    }



}
