package com.project.smartedu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SignUp extends AppCompatActivity {

    EditText emailInput;
    EditText passwordInput;
    EditText confirmPasswordInput;
    Button signUpButton;
    TextView alreadyUserText;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);


        emailInput=(EditText)findViewById(R.id.emailSignup);
        passwordInput=(EditText)findViewById(R.id.passwordSignup);
        confirmPasswordInput=(EditText)findViewById(R.id.confirmPasswordSignup);
        signUpButton=(Button)findViewById(R.id.signUpButton);
        alreadyUserText=(TextView)findViewById(R.id.already);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();


        Log.d("signup", "onCreate: " + firebaseAuth);

        if(firebaseAuth.getCurrentUser()!=null){


        }

        alreadyUserText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickedAlreadyUser();
            }
        });


        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email=emailInput.getText().toString().trim();
                final String password=passwordInput.getText().toString().trim();
                final String confirmPassword=confirmPasswordInput.getText().toString().trim();
                clickedSignUpButton(email,password,confirmPassword);
            }
        });

    }


    private void clickedAlreadyUser(){

        finish();
        Intent toLogin=new Intent(this,LoginActivity.class);
        startActivity(toLogin);

    }


    private void clickedSignUpButton(String email,String password,String confirmPassword){

        if( (email.equals("")) || (password.equals("")) || (confirmPassword.equals("")) ){

            Toast.makeText(getApplicationContext(),"Fields left blank",Toast.LENGTH_LONG).show();

        }else if(!password.equals(confirmPassword)){

            Toast.makeText(getApplicationContext(),"Password confirmation failed",Toast.LENGTH_LONG).show();

        }else{

            progressDialog.setMessage("Registering User...");
            progressDialog.show();


            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    progressDialog.dismiss();
                    if(task.isSuccessful()){

                        Toast.makeText(getApplicationContext(),"User Registration Successful",Toast.LENGTH_LONG).show();


                    }else{

                        Toast.makeText(getApplicationContext(),"User Registration Failed",Toast.LENGTH_LONG).show();

                    }



                }
            });

        }


    }

}
