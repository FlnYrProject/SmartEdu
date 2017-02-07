package com.project.smartedu.admin;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.ImageAdapter;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.common.Tasks;

public class Home extends BaseActivity{


    DatabaseReference databaseReference;
    Button logout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        logout=(Button)findViewById(R.id.lo);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(Home.this, LoginActivity.class));
            }
        });



        Intent from_login = getIntent();
        institutionName=from_login.getStringExtra("institution_name");


        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, densityX,densityY, "admin"));


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (position == 0) { //teachers

                    Intent student_intent = new Intent(Home.this, Teachers.class);
                    student_intent.putExtra("institution_name",institutionName);
                    student_intent.putExtra("role", "admin");
                    startActivity(student_intent);

                } else if (position == 1) { //tasks

                    Intent task_intent = new Intent(Home.this, Tasks.class);
                    //task_intent.putExtra("institution_code",institution_code);
                    task_intent.putExtra("institution_name",institutionName);
                    task_intent.putExtra("role", "admin");
                    startActivity(task_intent);

                } else if (position == 2) { //classes

                }
                else if (position == 3) {   //allotments

                }

            }
        });


    }

}
