package com.project.smartedu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.admin.Home;

import java.util.HashMap;

import static com.project.smartedu.Constants.databaseReference;

public class LoginActivity extends AppCompatActivity {

    EditText emailInput;
    EditText passwordInput;
    Button loginButton;
    TextView notAlreadyUserText;
    ProgressDialog progressDialog;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

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

        if(firebaseAuth.getCurrentUser()!=null){

        //adminCheck();

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
                        adminCheck();


                    }else{

                        Toast.makeText(getApplicationContext(),"User Login Failed",Toast.LENGTH_LONG).show();

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

                HashMap<String, String> map=(HashMap<String, String>)dataSnapshot.getValue();

                for(int x=0;x<map.size();x++){
                    String userId=firebaseAuth.getCurrentUser().getUid();
                    boolean ifadmin=map.containsKey(userId);


                    if(ifadmin) {
                       String name=map.get(userId);

                        Toast.makeText(getApplicationContext(), "Welcome " + name + " admin", Toast.LENGTH_LONG).show();
                        Intent toAdminConsole=new Intent(LoginActivity.this, Home.class);
                        startActivity(toAdminConsole);
                        break;
                    }else{

                        Intent toChooseRole=new Intent(LoginActivity.this, ChooseRole.class);
                        startActivity(toChooseRole);

                    }

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }


}
