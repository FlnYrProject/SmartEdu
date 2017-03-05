package com.project.smartedu.common;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.smartedu.BaseActivity;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.teacher.Home;

import java.util.ArrayList;
import java.util.List;

public class select_institution extends BaseActivity {


    private Toolbar mToolbar;
   // private FragmentDrawer drawerFragment;


    ListView institutionList;
 //   Notification_bar noti_bar;
    ImageView noinsti;
    //String child_code;
    String child_username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_institution);

        Intent from_student = getIntent();

        role=from_student.getStringExtra("role");

        if(role.equalsIgnoreCase("Parent"))
        {
            //child_code=from_student.getStringExtra("child_code");
            child_username=from_student.getStringExtra("child_username");
        }
/*
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Select institution");
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,"-");

*/
        institutionList = (ListView) findViewById(R.id.institutionList);
        noinsti=(ImageView)findViewById(R.id.noinstitute);
        noinsti.setVisibility(View.INVISIBLE);

  /*      drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,"");
        drawerFragment.setDrawerListener(this);
*/

        Toast.makeText(select_institution.this, "role selected = " +role, Toast.LENGTH_LONG).show();

        ArrayList<String> institutionLt;


        if( !(UserPrefs.roleslistmap.containsKey(role)) || (UserPrefs.roleslistmap.get(role).size()==0) ) {
            noinsti.setVisibility(View.VISIBLE);
        }else {
            institutionLt= UserPrefs.roleslistmap.get(role);


            if(institutionLt.size()==1){

            Log.d("institution", "Single institution");

                institutionName=institutionLt.get(0);

              loadInstitution(institutionName);

        }else {
            Log.d("institution", "Retrieved the institutions");

            ArrayList<String> studentLt = new ArrayList<String>();
            ArrayAdapter adapter = new ArrayAdapter(select_institution.this, android.R.layout.simple_list_item_1, institutionLt);
            //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

            Log.d("user", "Retrieved " + institutionLt.size() + " institutions");
            //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();


            institutionList.setAdapter(adapter);


            institutionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                   institutionName = ((TextView) view).getText().toString();
                    Log.d("institution", institutionName);

                    loadInstitution(institutionName);



                }
            });
            }

        }

    }

    protected void loadInstitution(String institutionName){

        if (role.equalsIgnoreCase("teacher")) {
           Intent teacher_home_page = new Intent(select_institution.this, Home.class);
            teacher_home_page.putExtra("role", role);
            teacher_home_page.putExtra("institution_name", institutionName);
            startActivity(teacher_home_page);
        } else if (role.equalsIgnoreCase("student")) {
            Intent student_home_page = new Intent(select_institution.this, com.project.smartedu.student.Home.class);
            student_home_page.putExtra("role", role);
            student_home_page.putExtra("institution_name", institutionName);
            startActivity(student_home_page);
        } else if (role.equalsIgnoreCase("parent")) {
            Intent parent_home_page = new Intent(select_institution.this, com.project.smartedu.parent.Home.class);
            parent_home_page.putExtra("role", role);
            parent_home_page.putExtra("institution_name", institutionName);
            startActivity(parent_home_page);
        }
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
           UserPrefs userPrefs=new UserPrefs(select_institution.this);
            userPrefs.clearUserDetails();
        }
    }




}
