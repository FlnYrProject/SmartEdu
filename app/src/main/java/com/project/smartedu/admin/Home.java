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
import android.widget.GridView;

import com.google.firebase.database.DatabaseReference;
import com.project.smartedu.Constants;
import com.project.smartedu.ImageAdapter;
import com.project.smartedu.R;
import com.project.smartedu.common.Tasks;

public class Home extends AppCompatActivity {

    int densityX;
    int densityY;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });



        // put in base activity later
        WindowManager windowManager = ((WindowManager) getSystemService(Context.WINDOW_SERVICE));
        Display display = windowManager.getDefaultDisplay();
        densityX = display.getWidth();
        densityY= display.getHeight();





        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, densityX,densityY, "admin"));


        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (position == 0) { //teachers

                } else if (position == 1) { //tasks

                    Intent task_intent = new Intent(Home.this, Tasks.class);
                    //task_intent.putExtra("institution_code",institution_code);
                    //task_intent.putExtra("institution_name",institution_name);
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
