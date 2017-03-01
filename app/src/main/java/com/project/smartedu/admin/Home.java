package com.project.smartedu.admin;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.ImageAdapter;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.SignUp;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.common.Tasks;
import com.project.smartedu.database.Schedule;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class Home extends BaseActivity{


    DatabaseReference databaseReference;
    Button logout;
    UserPrefs userPrefs;
    AdminUserPrefs adminUserPrefs;


    ArrayList<String> taskLt;
    HashMap<String,String> taskidmap; //to map task to its  id


    ArrayList<String> teacherLt;
    ArrayList<String> teacheruseridLt;
    HashMap<String,String> teachersusermap; //to map teacher to its user id
    ArrayList<String> tempteacherlt;

    ArrayList<Schedule> scheduleslt;
    HashMap<String,ArrayList<Schedule>> schedulesmaplt;







    private class ScheduleItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;
        String cursorday;

        public ScheduleItems(Context context,String day){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            cursorday=day;
            databaseReference = Constants.databaseReference.child(Constants.SCHEDULES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(cursorday);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Schedules");
            pd.setCancelable(false);
            pd.show();
            scheduleslt.clear();
            schedulesmaplt.remove(cursorday);
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();
            final Object lock2 = new Object();



            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            HashMap<String, HashMap<String,String>> retScheduleList = (HashMap<String, HashMap<String,String>>) ds.getValue();



                            for ( String key : retScheduleList.keySet() ) {
                                //  Toast.makeText(getApplicationContext(),"here",Toast.LENGTH_LONG).show();
                                Log.d("key",key);
                                HashMap<String,String> taskmap=retScheduleList.get(key);
                                                                // Toast.makeText(getApplicationContext(),taskmap.get("name") + " " + taskmap.get("date"),Toast.LENGTH_LONG).show();
                                /// System.out.print(taskmap.get("name") + " " + taskmap.get("date"));

                                String info=taskmap.get("info");
                                long start_date=Long.parseLong(taskmap.get("start_time"));
                                long end_date=Long.parseLong(taskmap.get("end_time"));

                                Schedule schedule=new Schedule(cursorday,start_date,end_date,info);
                                scheduleslt.add(schedule);

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
            Toast.makeText(getApplicationContext(),scheduleslt.size() + " schedules found on " + cursorday,Toast.LENGTH_LONG).show();

            schedulesmaplt.put(cursorday,scheduleslt);
            AdminUserPrefs.schedulesmaplt=schedulesmaplt;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                }
            }, 500);  // 100 milliseconds
        }
        //end firebase_async_class
    }







    private class TeacherItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public TeacherItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            databaseReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Teachers List");
            pd.setCancelable(false);
            pd.show();
            teacherLt.clear();
            teacheruseridLt.clear();
            teachersusermap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();
            final Object lock2 = new Object();



           databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for (final DataSnapshot ds : dataSnapshot.getChildren()) {

                            tempteacherlt.add(ds.getValue().toString());
                            teacheruseridLt.add(ds.getKey());
                      //  HashMap<String, String> retTeachersList=(HashMap<String, String>)dataSnapshot.getValue();

                           // HashMap<String, HashMap<String,String>> retTeachersList = (HashMap<String, HashMap<String,String>>) ds.getValue();
                            //   Toast.makeText(getApplicationContext(),"here in",Toast.LENGTH_LONG).show();





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


            for(int i=0;i<teacheruseridLt.size();i++) {

                DatabaseReference dataReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(teacheruseridLt.get(i));

                final int finalI = i;
                dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        synchronized (lock2) {
                            for (DataSnapshot dsi : dataSnapshot.getChildren()) {
                                if (dsi.getKey().equals("name")) {
                                    String entry = tempteacherlt.get(finalI).toString() + ". " + dsi.getValue().toString();
                                    Toast.makeText(getApplicationContext(),entry,Toast.LENGTH_LONG).show();
                                    teacherLt.add(entry);
                                    teachersusermap.put(entry, teacheruseridLt.get(finalI));
                                }
                            }
                            lock2.notifyAll();
                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                synchronized (lock2) {
                    try {
                        lock2.wait();
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
           Toast.makeText(getApplicationContext(),teacherLt.size() + " teachers found",Toast.LENGTH_LONG).show();
            for(int i=0;i<teacherLt.size();i++){
                Toast.makeText(getApplicationContext(),teacherLt.get(i),Toast.LENGTH_LONG).show();
            }

            AdminUserPrefs.teacherLt=teacherLt;
            AdminUserPrefs.teachersusermap=teachersusermap;
            AdminUserPrefs.teacheruseridLt=teacheruseridLt;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                }
            }, 500);  // 100 milliseconds
        }
        //end firebase_async_class
    }












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

            AdminUserPrefs.taskItems=taskLt;
            AdminUserPrefs.taskidmap=taskidmap;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                }
            }, 500);  // 100 milliseconds
        }
        //end firebase_async_class
    }









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        adminUserPrefs=new AdminUserPrefs(getApplicationContext());

        taskLt=AdminUserPrefs.taskItems;
        taskidmap=AdminUserPrefs.taskidmap;
        teacherLt=AdminUserPrefs.teacherLt;
        teacheruseridLt=AdminUserPrefs.teacheruseridLt;
        teachersusermap=AdminUserPrefs.teachersusermap;
        tempteacherlt=new ArrayList<>();
        schedulesmaplt=AdminUserPrefs.schedulesmaplt;
        scheduleslt=new ArrayList<>();

        logout=(Button)findViewById(R.id.lo);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                startActivity(new Intent(Home.this, LoginActivity.class));
            }
        });



        Intent from_login = getIntent();
        institutionName=from_login.getStringExtra("institution_name");

        Toast.makeText(getApplicationContext(),institutionName,Toast.LENGTH_LONG).show();


        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, densityX,densityY, "admin"));


        loadData();




        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (position == 0) { //teachers

                    Intent student_intent = new Intent(Home.this, Teachers.class);
                    student_intent.putExtra("institution_name",institutionName);
                    student_intent.putExtra("role", "admin");
                    startActivity(student_intent);

                } else if (position == 1) { //tasks

                    Intent task_intent = new Intent(Home.this, Tasks.class);
                    //task_intent.putExtra("institution_code",institution_code);
                    task_intent.putExtra("institution_name",institutionName);
                    task_intent.putExtra("role", "admin");
                    startActivity(task_intent);

                } else if (position == 2) { //classes

                    Intent task_intent = new Intent(Home.this,Classes.class);
                    //task_intent.putExtra("institution_code",institution_code);
                    task_intent.putExtra("institution_name",institutionName);
                    task_intent.putExtra("role", "admin");
                    startActivity(task_intent);


                }
                else if (position == 3) {   //allotments

                }

            }
        });


    }



    private void loadData(){


            loadTaskData();




            loadTeacherData();



    }


    private void loadTaskData(){

        TaskItems taskasync = new TaskItems(Home.this);     //get task data
        taskasync.execute();

    }

    private void loadTeacherData(){

        TeacherItems teacherasync = new TeacherItems(Home.this);        //get teacher data
        teacherasync.execute();

    }



}
