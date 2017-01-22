package com.project.smartedu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    TextView notAlreadyUserText;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;

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

        if(firebaseAuth.getCurrentUser()!=null){


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
                clickedLogin(email,password);
            }
        });

    }


    private void clickedNotUserAlready(){
        finish();
        Intent toSignUp=new Intent(this,SignUp.class);
        startActivity(toSignUp);
    }

    private void clickedLogin(String email,String password){

        if( TextUtils.isEmpty(email) || TextUtils.isEmpty(password)  ){

            Toast.makeText(getApplicationContext(),"Fields left blank",Toast.LENGTH_LONG).show();

        }else{



            progressDialog.setMessage("Logging In...");
            progressDialog.show();




            firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressDialog.dismiss();
                    if(task.isSuccessful()){


                        Toast.makeText(getApplicationContext(),"User Login Successful",Toast.LENGTH_LONG).show();

                    }else{

                        Toast.makeText(getApplicationContext(),"User Login Failed",Toast.LENGTH_LONG).show();

                    }



                }
            });

        }

    }

}
