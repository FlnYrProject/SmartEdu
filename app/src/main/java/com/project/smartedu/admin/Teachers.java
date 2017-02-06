package com.project.smartedu.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.Constants;
import com.project.smartedu.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Teachers extends AppCompatActivity {

    String classId;
    String name;
    Integer age;
    ArrayList<String> teacherLt;

    Button createIDs;
    Button delButton;
    ListView teacherList;

    TextView Name;
    TextView Age;

    String role;
    String institutionName;

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Teachers.this, NewTeacher.class);
                i.putExtra("institution_name",institutionName);
                i.putExtra("role",role);
                startActivity(i);
            }
        });


        Intent from_home = getIntent();
        role=from_home.getStringExtra("role");
        institutionName=from_home.getStringExtra("institution_name");


       
        createIDs=(Button)findViewById(R.id.shareCode);
        teacherList = (ListView) findViewById(R.id.teacherList);


        firebaseAuth=FirebaseAuth.getInstance();
        databaseReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName);



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> teachersmap=(HashMap<String, String>)dataSnapshot.getValue();

                if(teachersmap==null){

                    Toast.makeText(getApplicationContext(),"No teachers added",Toast.LENGTH_LONG).show();

                }else{

                    Toast.makeText(getApplicationContext(),teachersmap.size() + " teachers found ",Toast.LENGTH_LONG).show();

                    teacherLt = new ArrayList<>();
                    for ( String key : teachersmap.keySet() ) {
                        System.out.println( key );

                        databaseReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName).child(key);


                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String teacher=(String) dataSnapshot.getValue();

                                Log.d("ta",teacher);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }


                }





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });







    }

}
