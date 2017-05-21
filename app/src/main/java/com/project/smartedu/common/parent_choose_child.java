package com.project.smartedu.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.database.Children;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.student.ParentUserPrefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class parent_choose_child  extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;


    ListView childList;
    NotificationBar noti_bar;


    UserPrefs userPrefs;
    ParentUserPrefs parentUserprefs;

    DatabaseReference databaseReference;




    private class ChildrenItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public ChildrenItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching your Childs...");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();







            for(int x=0;x<ParentUserPrefs.childuseridLt.size();x++) {
                final String childid=ParentUserPrefs.childuseridLt.get(x);
                final Children children=new Children();
                final String[] name = new String[1];
                databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(childid);

                databaseReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {


                        synchronized (lock) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {


                                if (ds.getKey().equalsIgnoreCase("name")) {
                                    children.setName(ds.getValue().toString());
                                    Log.d("name", children.getName());
                                }


                                if (ds.getKey().equalsIgnoreCase("role")) {


                                    for (DataSnapshot dataSnapshot1 : ds.getChildren()) {

                                        if (dataSnapshot1.getKey().equalsIgnoreCase("student")) {
                                            ArrayList<String> childinstitutes = new ArrayList<String>();

                                            for (DataSnapshot dataSnapshot2 : dataSnapshot1.getChildren()) {


                                                Log.d("institute", dataSnapshot2.getValue().toString());        //gives institute

                                                childinstitutes.add(dataSnapshot2.getValue().toString());
                                            }

                                            children.setInsitutions(childinstitutes);

                                        }


                                    }


                                }

                            }
                            ParentUserPrefs.childinsitutionmap.put(childid,children);
                            Log.d("institute2",children.getInsitutions().get(0));        //gives institute


                            lock.notifyAll();



                        }




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }


            }





            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Handles the stuff after the synchronisation with the firebase listener has been achieved
            //The main UI is already idle by this moment
            super.onPostExecute(aVoid);

            //Show the log in progress_bar for at least a few milliseconds
            Log.d("institute2","here");        //gives institute

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                    loadChildren();
                }
            }, 500);  // 100 milliseconds
        }
        //end firebase_async_class
    }






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_choose_child);

        Intent from_student = getIntent();

        role=from_student.getStringExtra("role");

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
       /* getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        getSupportActionBar().setTitle("Select child");


        userPrefs=new UserPrefs(parent_choose_child.this);
        parentUserprefs= new ParentUserPrefs(parent_choose_child.this);

        noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), role,"-");

        childList = (ListView) findViewById(R.id.childList);


        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, "");
        drawerFragment.setDrawerListener(this);


        Toast.makeText(parent_choose_child.this, "role selected = " +role, Toast.LENGTH_LONG).show();

        ChildrenItems childrenitems=new ChildrenItems(parent_choose_child.this);
        childrenitems.execute();




    }



    public void loadChildren(){


            if(ParentUserPrefs.childuseridLt.size()==1){

            Log.d("child", "Single child");



                parentUserprefs.setSelectedChildId(ParentUserPrefs.childuseridLt.get(0));


                Intent institute_selection=new Intent(parent_choose_child.this,select_institution.class);
                institute_selection.putExtra("role", role);
                startActivity(institute_selection);





        }else {
            Log.d("child", "Retrieved children");

            ArrayList<String> studentLt = new ArrayList<String>();

                for(int x=0;x<ParentUserPrefs.childuseridLt.size();x++){
                    String child_id=ParentUserPrefs.childuseridLt.get(x);
                    Children child=ParentUserPrefs.childinsitutionmap.get(child_id);
                    studentLt.add(child.getName());

                }

            ArrayAdapter adapter = new ArrayAdapter(parent_choose_child.this, android.R.layout.simple_list_item_1, studentLt);
            //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();


            childList.setAdapter(adapter);


            childList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final String selected_child = ((TextView) view).getText().toString();
                    Log.d("child", selected_child);

                    for(int x=0;x<ParentUserPrefs.childuseridLt.size();x++){
                        String child_id=ParentUserPrefs.childuseridLt.get(x);
                        Children child=ParentUserPrefs.childinsitutionmap.get(child_id);


                        if(child.getName().equalsIgnoreCase(selected_child)){
                            parentUserprefs.setSelectedChildId(child_id);

                            Intent parent_home_page = new Intent(parent_choose_child.this, select_institution.class);
                            parent_home_page.putExtra("role", role);
                            startActivity(parent_home_page);
                        }


                    }




                }
            });


        }

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(parent_choose_child.this,LoginActivity.class);
            startActivity(nouser);
        }
    }





}
