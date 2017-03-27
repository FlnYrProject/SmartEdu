package com.project.smartedu.common;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.admin.Home;

public class Schedule extends AppCompatActivity implements ActionBar.TabListener{

    ViewPager Tab;
    ScheduleTabsAdapter TabAdapter;
    ActionBar actionBar;
    //private FragmentDrawer drawerFragment;
    private Toolbar mToolbar;
    TabLayout tabLayout;
    String role;
    String institutionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_schedule);
         /*   mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);*/
            //getSupportActionBar().setTitle("Schedule");
       /* drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
        drawerFragment.setDrawerListener(this);*/
            Bundle fromrole = getIntent().getExtras();
            role = fromrole.getString("role");

            institutionName=fromrole.getString("institution_name");
          //  Log.d("institution",institutionName);


            Tab = (ViewPager) findViewById(R.id.pager);
            Tab.setAdapter(new ScheduleTabsAdapter(getSupportFragmentManager(), role,institutionName));


            tabLayout = (TabLayout) findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(Tab);

            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    Tab.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    Tab.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                    Tab.setCurrentItem(tab.getPosition());
                }
            });

        /*
        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {

                        actionBar = getActionBar();
                        if (actionBar!=null)
                            actionBar.setSelectedNavigationItem(position);                    }
                });
        Tab.setAdapter(TabAdapter);

        actionBar = getActionBar();
        if (
                actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
        }
        //Enable Tabs on Action Bar
        if (
                actionBar != null) {

            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }


        if (
                actionBar != null) {
            Tab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageSelected(int postion) {

                    actionBar.setSelectedNavigationItem(postion);
                }

                @Override
                public void onPageScrolled(int arg0, float arg1, int arg2) {
                }

                @Override
                public void onPageScrollStateChanged(int arg0) {
                }
            });

        }
*/

       /*ActionBar.TabListener tabListener = new ActionBar.TabListener(){

            @Override
            public void onTabReselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

                Tab.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(android.app.ActionBar.Tab tab,
                                        FragmentTransaction ft) {
                // TODO Auto-generated method stub

            }}; */
            //Add New Tab
            // actionBar.addTab(actionBar.newTab().setText("Android").setTabListener(tabListener));
            //actionBar.addTab(actionBar.newTab().setText("iOS").setTabListener(tabListener));
            // actionBar.addTab(actionBar.newTab().setText("Windows").setTabListener(tabListener));

        }catch(Exception create_error){
            Log.d("user", "error in create schedule activity: " + create_error.getMessage());
            Toast.makeText(Schedule.this, "error " + create_error, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
        {
            Intent nouser=new Intent(Schedule.this,LoginActivity.class);
            startActivity(nouser);
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        View v = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (v instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            Log.d("Activity", "Touch event " + event.getRawX() + "," + event.getRawY() + " " + x + "," + y + " rect " + w.getLeft() + "," + w.getTop() + "," + w.getRight() + "," + w.getBottom() + " coords " + scrcoords[0] + "," + scrcoords[1]);
            if (event.getAction() == MotionEvent.ACTION_UP && (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w.getBottom()) ) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
            }
        }
        return ret;
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {

        Tab.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tohome;
        if(role.equalsIgnoreCase("admin")){
            tohome=new Intent(Schedule.this, Home.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institutionName);
            startActivity(tohome);
        }else if(role.equalsIgnoreCase("teacher")){
            tohome=new Intent(Schedule.this, com.project.smartedu.teacher.Home.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institutionName);
            startActivity(tohome);
        }else if(role.equalsIgnoreCase("student")){
            tohome=new Intent(Schedule.this, com.project.smartedu.student.Home.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institutionName);
            startActivity(tohome);
        }else if(role.equalsIgnoreCase("parent")){
            tohome=new Intent(Schedule.this, com.project.smartedu.parent.Home.class);
            tohome.putExtra("role",role);
            tohome.putExtra("institution_name",institutionName);
            startActivity(tohome);
        }
    }
}
