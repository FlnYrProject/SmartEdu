package com.project.smartedu.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.R;
import com.project.smartedu.admin.AdminUserPrefs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Tasks extends BaseActivity {


    ListView taskList;


    ArrayAdapter adapter=null;
    ArrayList<String> taskLt;
    List<String> items;


    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;














    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Bundle fromhome= getIntent().getExtras();
        role = fromhome.getString("role");


        //change to add task
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Tasks.this, NewTask.class);
                i.putExtra("role", role);
                startActivity(i);
            }
        });


        firebaseAuth = FirebaseAuth.getInstance();

        taskList=(ListView)findViewById(R.id.taskList);

        if(role.equals("admin")) {
            taskLt = AdminUserPrefs.taskItems;           //load data afterwards
        }
        showList();



    }



    public void showList(){

        adapter = new ArrayAdapter(Tasks.this, android.R.layout.simple_list_item_1, taskLt);
        taskList.setAdapter(adapter);
    }

}
