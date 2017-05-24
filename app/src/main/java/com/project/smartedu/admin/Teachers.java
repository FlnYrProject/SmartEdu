package com.project.smartedu.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;

import java.util.ArrayList;

public class Teachers extends BaseActivity{

    String classId;
    String name;
    Integer age;
    ArrayList<String> teacherLt;
    ArrayAdapter adapter=null;

   // Button createIDs;
    Button delButton;
    ListView teacherList;

    TextView Name;
    TextView Age;


    

    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    NotificationBar noti_bar;


    UserPrefs userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers);


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

    userPrefs=new UserPrefs(Teachers.this);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout),mToolbar,role);
        drawerFragment.setDrawerListener(this);

        noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(),role,institutionName);

       
      //  createIDs=(Button)findViewById(R.id.shareCode);
        teacherList = (ListView) findViewById(R.id.teacherList);



//        firebaseAuth=FirebaseAuth.getInstance();

        teacherLt=AdminUserPrefs.teacherLt;         //load data remaining


        showList();






    }


    public void showList(){

        adapter = new ArrayAdapter(Teachers.this, android.R.layout.simple_list_item_1, teacherLt);
        teacherList.setAdapter(adapter);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tohome=new Intent(Teachers.this,Home.class);
        tohome.putExtra("institution_name",institutionName);
        startActivity(tohome);
    }
}
