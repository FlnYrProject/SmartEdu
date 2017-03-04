package com.project.smartedu.teacher;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.Toast;

import com.project.smartedu.BaseActivity;
import com.project.smartedu.ImageAdapter;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.common.Schedule;
import com.project.smartedu.common.Tasks;
import com.project.smartedu.common.view_messages;

import java.util.ArrayList;

public class Home extends BaseActivity {

 //   private Toolbar mToolbar;
  //  private FragmentDrawer drawerFragment;
     //  Notification_bar noti_bar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            setContentView(R.layout.activity_home_teacher);
            Intent home=getIntent();
            role=home.getStringExtra("role");
            institutionName=home.getStringExtra("institution_name");
            Log.d("user", role);

         /*   dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
            noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
            noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Dashboard");


            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);//pass role
            drawerFragment.setDrawerListener(this);



*/

            GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(this, densityX, densityY, role));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    if (position == 0) {
                        Intent attendance_intent = new Intent(Home.this, Classes.class);
                        attendance_intent.putExtra("institution_name",institutionName);
                        attendance_intent.putExtra("for", "attendance");
                        attendance_intent.putExtra("role", role);
                        startActivity(attendance_intent);
                    } else if (position == 1) {
                        Intent task_intent = new Intent(Home.this, Tasks.class);
                        task_intent.putExtra("institution_name",institutionName);
                        task_intent.putExtra("role", role);
                        startActivity(task_intent);
                    } else if (position == 2) {
                        Intent student_intent = new Intent(Home.this, Classes.class);
                        student_intent.putExtra("institution_name",institutionName);
                        student_intent.putExtra("role", role);
                        student_intent.putExtra("for", "students");
                        startActivity(student_intent);
                    } else if (position == 3) {
                        Intent schedule_intent = new Intent(Home.this, Schedule.class);
                        schedule_intent.putExtra("institution_name",institutionName);
                        schedule_intent.putExtra("role", role);
                        startActivity(schedule_intent);
                    } else if (position == 4) {
                        Intent addmarks_intent = new Intent(Home.this, Classes.class);
                        addmarks_intent.putExtra("institution_name",institutionName);
                        addmarks_intent.putExtra("role", role);
                        addmarks_intent.putExtra("for", "exam");
                        startActivity(addmarks_intent);
                    } else if (position == 5) {
                        Intent upload_intent = new Intent(Home.this,Classes.class);
                        upload_intent.putExtra("institution_name",institutionName);
                        upload_intent.putExtra("role", role);
                        upload_intent.putExtra("for", "upload");
                        startActivity(upload_intent);

                    } else if (position == 6) {

                        Intent read_message_intent = new Intent(Home.this, view_messages.class);
                        read_message_intent.putExtra("role", role);
                        read_message_intent.putExtra("institution_name",institutionName);
                        read_message_intent.putExtra("_for", "received");
                        startActivity(read_message_intent);
                    } else if (position == 7) {


                    } else if (position == 8) {

                    }

                }
            });

        }catch(Exception create_error){
            Log.d("user", "error in create main activity: " + create_error.getMessage());
            Toast.makeText(Home.this, "error " + create_error, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            UserPrefs userPrefs=new UserPrefs(Home.this);
            userPrefs.clearUserDetails();
        }
    }

   /* public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }*/




}
