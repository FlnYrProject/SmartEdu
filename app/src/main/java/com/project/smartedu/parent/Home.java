package com.project.smartedu.parent;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.ImageAdapter;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.navigation.FragmentDrawer;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Home extends BaseActivity {

    ArrayList<String> taskLt;
    HashMap<String,String> taskidmap; //to map task to its  id

    ArrayList<com.project.smartedu.database.Schedule> scheduleslt;
    HashMap<String,ArrayList<com.project.smartedu.database.Schedule>> schedulesmaplt;
    HashMap<com.project.smartedu.database.Schedule,String> schedulekeymap;        //to map schedule to the key

    DatabaseReference databaseReference;




    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;


    String classGradeId;
    String studentId;

   // Notification_bar noti_bar;
    //String child_code;
    String child_username;


    private class TaskItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public TaskItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

            databaseReference = Constants.databaseReference.child(Constants.TASK_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(role);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Task List");
            pd.setCancelable(false);
            pd.show();
            taskLt.clear();
            taskidmap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            HashMap<String, HashMap<String,String>> retTaskList = (HashMap<String, HashMap<String,String>>) ds.getValue();
                            //   Toast.makeText(getApplicationContext(),"here in",Toast.LENGTH_LONG).show();

                            for ( String key : retTaskList.keySet() ) {
                                //  Toast.makeText(getApplicationContext(),"here",Toast.LENGTH_LONG).show();
                                Log.d("key",key);
                                HashMap<String,String> taskmap=( HashMap<String,String>)retTaskList.get(key);
                                // Toast.makeText(getApplicationContext(),taskmap.get("name") + " " + taskmap.get("date"),Toast.LENGTH_LONG).show();
                                /// System.out.print(taskmap.get("name") + " " + taskmap.get("date"));



                                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");



                                String dateString = formatter.format(new Date(Long.parseLong(taskmap.get("date"))));

                                String entry=taskmap.get("name")+ "\n" + taskmap.get("description") + "\n" + dateString;
                                Log.d("key",key);
                                taskidmap.put(entry,key);
                                taskLt.add(entry);

                            }



                        }
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
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Handles the stuff after the synchronisation with the firebase listener has been achieved
            //The main UI is already idle by this moment
            super.onPostExecute(aVoid);

            //Show the log in progress_bar for at least a few milliseconds
            Toast.makeText(getApplicationContext(),taskLt.size() + " tasks found",Toast.LENGTH_LONG).show();

            UserPrefs.taskItems=taskLt;
            UserPrefs.taskidmap=taskidmap;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    loadScheduleData();
                }
            }, 500);  // 100 milliseconds
        }
        //end firebase_async_class
    }








    private class ScheduleItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;
        String cursorday;


        public ScheduleItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            scheduleslt=new ArrayList<>();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Schedules");
            pd.setCancelable(false);
            pd.show();
            schedulekeymap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            for(int x=0;x<Constants.days.length;x++){
                cursorday=Constants.days[x];
                databaseReference = Constants.databaseReference.child(Constants.SCHEDULES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(cursorday);

                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        synchronized (lock) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                                HashMap<String, String> retScheduleList = new HashMap<String, String>();
                                retScheduleList = (HashMap<String, String>) ds.getValue();
                                HashMap<String, String> taskmap = retScheduleList;
                                try {

                                    String info = taskmap.get("info");
                                    long start_date = Long.parseLong(taskmap.get("start_time"));
                                    long end_date = Long.parseLong(taskmap.get("end_time"));
                                    com.project.smartedu.database.Schedule schedule = new com.project.smartedu.database.Schedule(cursorday, start_date, end_date, info);
                                    Log.d("sskey",ds.getKey()+" on " + cursorday);
                                    schedulekeymap.put(schedule, ds.getKey());

                                }catch (NullPointerException npe){

                                }



                            }



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


            // AdminUserPrefs.schedulesmaplt=schedulesmaplt;
            UserPrefs.schedulekeymap=schedulekeymap;

            //Show the log in progress_bar for at least a few milliseconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                    //loadClassData();
                }
            }, 500);  // 100 milliseconds
        }


        //end firebase_async_class
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_parent);



        Intent home=getIntent();

        role=home.getStringExtra("role");
        institutionName=home.getStringExtra("institution_name");
        child_username=home.getStringExtra("child_username");

        Log.d("user",role);

      /*  dbHandler = new MyDBHandler(getApplicationContext(),null,null,1);
        noti_bar = (Notification_bar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,institution_name);
        */
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Dashboard");



        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);

        taskLt = UserPrefs.taskItems;
        taskidmap = UserPrefs.taskidmap;

        schedulekeymap = UserPrefs.schedulekeymap;
        scheduleslt = new ArrayList<>();

      
        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getApplicationContext(), densityX,densityY, "Parent"));
        
        loadData();

/*
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (position == 0) {
                    Intent atten_intent = new Intent(Home.this, student_classes.class);
                    atten_intent.putExtra("role", "Parent");
                    atten_intent.putExtra("studentId", studentId);
                    atten_intent.putExtra("classGradeId", classGradeId);
                   
                    atten_intent.putExtra("institution_name",institutionName);
                    startActivity(atten_intent);

                } else if (position == 1) {
                    Intent task_intent = new Intent(Home.this, Tasks.class);
                    task_intent.putExtra("institution_name",institutionName);
                 
                    task_intent.putExtra("role", "Parent");
                    startActivity(task_intent);
                } else if (position == 2) {
                    Intent message_intent = new Intent(Home.this, view_messages.class);
                    message_intent.putExtra("role", "Parent");
                    message_intent.putExtra("classGradeId", classGradeId);
                    message_intent.putExtra("studentId", studentId);
                    message_intent.putExtra("institution", institutionName);
            
                    message_intent.putExtra("_for", "received");
                    startActivity(message_intent);

                } else if (position == 3) {

                    Intent exam_intent = new Intent(Home.this, student_exams.class);
                    exam_intent.putExtra("role", "Parent");
                    exam_intent.putExtra("institution_name", institutionName);
                   
                    exam_intent.putExtra("classGradeId", classGradeId);
                    exam_intent.putExtra("studentId", studentId);
                    startActivity(exam_intent);

                } else if (position == 4) {


                } else if (position == 5) {


                }
            }
        });

  */
    }



    private void loadData(){


        loadTaskData();


    }


    private void loadTaskData(){

        TaskItems taskasync = new TaskItems(Home.this);     //get task data
        taskasync.execute();

    }


    private void loadScheduleData(){

ScheduleItems scheduleasync = new ScheduleItems(Home.this);        //get teacher data
        scheduleasync.execute();

    }


}
