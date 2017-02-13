package com.project.smartedu;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.admin.Home;

import java.util.HashMap;

public class ChooseRole extends BaseActivity {

    Button student;
    Button parent;
    Button teacher;

    boolean foundRole;

    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //change to add role button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with add role", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                firebaseAuth.signOut();
                startActivity(new Intent(ChooseRole.this, LoginActivity.class));
            }
        });

        databaseReference=Constants.databaseReference;

        foundRole=false;

        student=(Button)findViewById(R.id.button_student);
        parent=(Button)findViewById(R.id.button_parent);
        teacher=(Button)findViewById(R.id.button_teacher);


        databaseReference=databaseReference.child(Constants.USER_DETAILS_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("role");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> rolemap=(HashMap<String, String>)dataSnapshot.getValue();

                if(rolemap==null){

                    Toast.makeText(getApplicationContext(),"No roles found",Toast.LENGTH_LONG).show();

                }else{

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }

}
