package com.project.smartedu.common;

import android.content.Intent;
import android.os.Bundle;
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
import com.project.smartedu.Constants;
import com.project.smartedu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class Tasks extends AppCompatActivity {


    ListView taskList;


    ArrayAdapter adapter=null;
    ArrayList<String> taskLt;
    List<String> items;
    String role;

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


        databaseReference = Constants.databaseReference.child(Constants.TASK_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(role);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> taskmap=(HashMap<String, String>)dataSnapshot.getValue();

                if(taskmap==null){

                    Toast.makeText(getApplicationContext(),"No tasks added",Toast.LENGTH_LONG).show();

                }else{

                    Toast.makeText(getApplicationContext(),taskmap.size() + " tasks found ",Toast.LENGTH_LONG).show();


                    taskLt = new ArrayList<>();
                    for ( String key : taskmap.keySet() ) {
                        System.out.println( key );

                        databaseReference = Constants.databaseReference.child(Constants.TASK_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(role).child(key);


                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                HashMap<String, String> task=(HashMap<String, String>)dataSnapshot.getValue();
                                String name = task.get("name");
                                name += "\n";
                                name += task.get("description");

                                name += "\n";


                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                                Toast.makeText(getApplicationContext(),task.get("date"),Toast.LENGTH_LONG).show();
                               Log.d("date",task.get("date"));
                                 String dateString = formatter.format(new Date(Long.parseLong(task.get("date"))));
                                  name += dateString;

                                taskLt.add(name);
                                Log.d("ta",taskLt.get(taskLt.size()-1));

                                showList();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }

                    Log.d("here","here");

                    Log.d("ta","size =" + taskLt.size());



                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }



    public void showList(){

        adapter = new ArrayAdapter(Tasks.this, android.R.layout.simple_list_item_1, taskLt);
        taskList.setAdapter(adapter);
    }

}
