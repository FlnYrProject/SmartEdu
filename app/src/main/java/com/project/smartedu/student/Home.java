package com.project.smartedu.student;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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
import com.project.smartedu.common.Schedule;
import com.project.smartedu.common.Tasks;
import com.project.smartedu.common.view_messages;
import com.project.smartedu.database.Class;
import com.project.smartedu.database.Notice;
import com.project.smartedu.database.Students;
import com.project.smartedu.database.Teachers;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.noticeBoard.NoticeBoard;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.teacher.TeacherUserPrefs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Home extends BaseActivity {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
 NotificationBar noti_bar;

    ArrayList<String> taskLt;
    HashMap<String, String> taskidmap; //to map task to its  id

    ArrayList<com.project.smartedu.database.Schedule> scheduleslt;
    HashMap<String, ArrayList<com.project.smartedu.database.Schedule>> schedulesmaplt;
    HashMap<com.project.smartedu.database.Schedule, String> schedulekeymap;        //to map schedule to the key

    DatabaseReference databaseReference;

    UserPrefs userPrefs;
    StudentUserPrefs studentUserPrefs;
    SwipeRefreshLayout swipeRefreshLayout;

    String classId;





    NoticeBoard noticeBoard;


    private class NoticeBoardItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public NoticeBoardItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            databaseReference = Constants.databaseReference.child(Constants.NOTIFICATION_TABLE).child(institutionName);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Notices");
            pd.setCancelable(false);
            pd.show();
            UserPrefs.noticeLt.clear();
            UserPrefs.noticemap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            Log.d("noticekey",ds.getKey());             //notice id



                            UserPrefs.noticeLt.add(ds.getKey());

                            Notice notice=new Notice();
                            notice.setNoticeid(ds.getKey());

                            for(DataSnapshot dataSnapshot1:ds.getChildren()){

                                if(dataSnapshot1.getKey().equalsIgnoreCase("by")){
                                    notice.setBystring(dataSnapshot1.getValue().toString());
                                }

                                if(dataSnapshot1.getKey().equalsIgnoreCase("content")){
                                    notice.setContent(dataSnapshot1.getValue().toString());
                                }

                                if(dataSnapshot1.getKey().equalsIgnoreCase("time")){
                                    notice.setTime(dataSnapshot1.getValue().toString());
                                }

                                if(dataSnapshot1.getKey().equalsIgnoreCase("user_id")){
                                    notice.setUserid(dataSnapshot1.getValue().toString());
                                }

                                if(dataSnapshot1.getKey().equalsIgnoreCase("user_name")){
                                    notice.setUsername(dataSnapshot1.getValue().toString());
                                }




                            }

                            UserPrefs.noticemap.put(ds.getKey(),notice);



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

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                    noticeBoard.setData(institutionName,role);
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
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching alloted teachers...");
            pd.setCancelable(false);
            pd.show();
           StudentUserPrefs.teacherHashMap.clear();
            StudentUserPrefs.teachersuseridLt.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();
            final Object lock2 = new Object();



                final String classid=studentUserPrefs.getClassId();
            Log.d("cid","class id = "+ classid);
                String[] classitems=classid.split("_");

                databaseReference=Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(classitems[1]).child(classitems[2]).child("teacher");


                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        synchronized (lock) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {




                                String serial_number=ds.getValue().toString(); //gives teacher serial
                                String teacherid=ds.getKey();   //gives teacher id

                                StudentUserPrefs.teachersuseridLt.add(teacherid);

                                ArrayList<String> subjects=new ArrayList<String>();
                                com.project.smartedu.database.Teachers teachers=new Teachers("",teacherid,serial_number,subjects);
                               StudentUserPrefs.teacherHashMap.put(teacherid,teachers);



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






            //getting names
                for(int i=0;i<StudentUserPrefs.teachersuseridLt.size();i++) {

                    final String teacherid=StudentUserPrefs.teachersuseridLt.get(i);
                    DatabaseReference dataReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(teacherid);

                    final int finalI = i;
                    dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            synchronized (lock2) {
                                for (DataSnapshot dsi : dataSnapshot.getChildren()) {
                                    if (dsi.getKey().equals("name")) {

                                        String stuname=dsi.getValue().toString();

                                        StudentUserPrefs.teacherHashMap.get(teacherid).setName(stuname);


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









        //setting subjects
            databaseReference=Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(classitems[1]).child(classitems[2]).child("subject");


            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {




                            String teacherid=ds.getValue().toString(); //gives teacher id
                            String subject_name=ds.getKey();   //gives subject name

                            Teachers teachers=StudentUserPrefs.teacherHashMap.get(teacherid);

                            ArrayList<String> subjects=teachers.getSubjects();
                            subjects.add(subject_name);
                            teachers.setSubjects(subjects);
                            StudentUserPrefs.teacherHashMap.put(teacherid,teachers);




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


            int size=StudentUserPrefs.teachersuseridLt.size();
          //  Toast.makeText(getApplicationContext(),size + " total students found",Toast.LENGTH_LONG).show();

            //Show the log in progress_bar for at least a few milliseconds

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                  loadNoticeBoardData();

                }
            }, 500);  // 100 milliseconds
        }


        //end firebase_async_class
    }













    private class ClassItem extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public ClassItem(Context context) {
            this.async_context = context;
            pd = new ProgressDialog(async_context);

            databaseReference = Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(firebaseAuth.getCurrentUser().getUid());
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Class");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {


                            if(ds.getKey().equals("class")){
                                    studentUserPrefs.setClassId(ds.getValue().toString());
                            }

                            if(ds.getKey().equals("roll_number")){
                                studentUserPrefs.setRollNumber(Integer.parseInt(ds.getValue().toString()));
                            }

                        }
                        lock.notifyAll();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }

            });

            synchronized (lock) {
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
    //        Toast.makeText(getApplicationContext(), studentUserPrefs.getClassId() + " " + studentUserPrefs.getRollNumber(), Toast.LENGTH_LONG).show();



            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {

                    pd.dismiss();
                    pd=null;
                    loadTeacherData();
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
            databaseReference = Constants.databaseReference.child(Constants.TASK_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(role);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            // HashMap<String, HashMap<String,String>> retTaskList = (HashMap<String, HashMap<String,String>>) ds.getValue();
                            //   Toast.makeText(getApplicationContext(),"here in",Toast.LENGTH_LONG).show();
                            String name="",description="",time="0";

                            for(DataSnapshot dataSnapshot1:ds.getChildren()){




                                if (dataSnapshot1.getKey().equalsIgnoreCase("name")){
                                    Log.d("key","here");
                                    name=dataSnapshot1.getValue().toString();
                                }

                                if (dataSnapshot1.getKey().equalsIgnoreCase("description")){
                                    description=dataSnapshot1.getValue().toString();
                                }

                                if (dataSnapshot1.getKey().equalsIgnoreCase("date")){
                                    time=dataSnapshot1.getValue().toString();
                                }





                            }



                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");



                            String dateString = formatter.format(new Date(Long.parseLong(time)));
                            String entry=name+ "\n" + description + "\n" + dateString;
                            Log.d("key",ds.getKey());
                            taskidmap.put(entry,ds.getKey());
                            taskLt.add(entry);


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
                    pd=null;
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


        public ScheduleItems(Context context) {
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            scheduleslt = new ArrayList<>();

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

            for (int x = 0; x < Constants.days.length; x++) {
                cursorday = Constants.days[x];
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
                                    Log.d("sskey", ds.getKey() + " on " + cursorday);
                                    schedulekeymap.put(schedule, ds.getKey());

                                } catch (NullPointerException npe) {

                                }


                            }


                            lock.notifyAll();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });


                synchronized (lock) {
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
            UserPrefs.schedulekeymap = schedulekeymap;

            //Show the log in progress_bar for at least a few milliseconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd = null;
                    loadClassData();
                }
            }, 500);  // 100 milliseconds
        }


        //end firebase_async_class
    }




    private class NameItem extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public NameItem(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Data");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();
            databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(firebaseAuth.getCurrentUser().getUid());

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            if(ds.getKey().equals("name")) {
                                userPrefs.setUserName(ds.getValue().toString());
                            }

                            if(ds.getKey().equals("dob")) {
                                userPrefs.setUserDob(ds.getValue().toString());
                            }

                            if(ds.getKey().equals("address")) {
                                userPrefs.setUserAddress(ds.getValue().toString());
                            }

                            if(ds.getKey().equals("contact")) {
                                userPrefs.setUserContact(ds.getValue().toString());
                            }

                            if(ds.getKey().equals("parent_name")) {
                                userPrefs.setUserParentName(ds.getValue().toString());
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

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    //  Toast.makeText(async_context,userPrefs.getUserName(),Toast.LENGTH_LONG).show();

                    noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);
                    pd.dismiss();

                }
            }, 500);  // 100 milliseconds


        }
        //end firebase_async_class
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_student);

        Intent home = getIntent();
        role = home.getStringExtra("role");
        institutionName = home.getStringExtra("institution_name");

        Log.d("user", role);
        userPrefs=new UserPrefs(Home.this);
        studentUserPrefs=new StudentUserPrefs(Home.this);

           noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
        setupNotiBar();

            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Dashboard");



            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
            drawerFragment.setDrawerListener(this);
        noticeBoard=(NoticeBoard)getSupportFragmentManager().findFragmentById(R.id.notice_board);

        noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
       // noti_bar.setTexts(userPrefs.getUserName(),role,institutionName);


            taskLt = UserPrefs.taskItems;
            taskidmap = UserPrefs.taskidmap;

            schedulekeymap = UserPrefs.schedulekeymap;
            scheduleslt = new ArrayList<>();

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh();
            }
        });

        if(userPrefs.isFirstLoading()) {
            userPrefs.setFirstLoading(false);
            setupNotiBar();
            loadData();
        }else{
            noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);
            noticeBoard.setData(institutionName,role);

        }

        final GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getApplicationContext(), densityX, densityY, role));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                if (position == 0) {

                    Intent atten_intent = new Intent(Home.this, student_classes.class);

                    atten_intent.putExtra("role", role);
                    atten_intent.putExtra("studentId",firebaseAuth.getCurrentUser().getUid());
                    atten_intent.putExtra("classId", studentUserPrefs.getClassId());
                    atten_intent.putExtra("institution_name", institutionName);
                    atten_intent.putExtra("for", "attendance");
                    startActivity(atten_intent);

                } else if (position == 1) {
                    Intent task_intent = new Intent(Home.this, Tasks.class);
                    task_intent.putExtra("institution_name", institutionName);
                    task_intent.putExtra("role", role);
                    startActivity(task_intent);
                } else if (position == 2) {
                    Intent message_intent = new Intent(Home.this, view_messages.class);
                    message_intent.putExtra("role", role);

                    message_intent.putExtra("institution_name", institutionName);
                   
                    message_intent.putExtra("_for", "received");
                    startActivity(message_intent);

                } else if (position == 3) {
                    Intent schedule_intent = new Intent(Home.this, Schedule.class);
                    schedule_intent.putExtra("institution_name", institutionName);
                  
                    schedule_intent.putExtra("role", role);
                    startActivity(schedule_intent);

                } else if (position == 4) {
                    Intent exam_intent = new Intent(Home.this, student_classes.class);
                    exam_intent.putExtra("institution_name", institutionName);
                    exam_intent.putExtra("role", role);
                    exam_intent.putExtra("classId", studentUserPrefs.getClassId());
                    exam_intent.putExtra("studentId", studentUserPrefs.getClassId());
                    exam_intent.putExtra("for", "exam");
                    startActivity(exam_intent);

                } else if (position == 5) {
                    Intent upload_intent = new Intent(Home.this, student_classes.class);
                    upload_intent.putExtra("institution_name", institutionName);
                 
                    upload_intent.putExtra("role", role);
                    upload_intent.putExtra("classId",   studentUserPrefs.getClassId());
                    upload_intent.putExtra("for", "upload");
                    startActivity(upload_intent);
                } else if (position == 6) {


                } else if (position == 7) {

                }
            }
        });

    }

    private void setupNotiBar(){
        NameItem nameItem=new NameItem(Home.this);
        nameItem.execute();
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


    private void loadClassData(){

        ClassItem classasync = new ClassItem(Home.this);        //get teacher data
        classasync.execute();

    }



    private void loadTeacherData(){

        TeacherItems teacherItems = new TeacherItems(Home.this);        //get teacher data
        teacherItems.execute();

    }

    private void loadNoticeBoardData(){

        NoticeBoardItems noticeBoardItems=new NoticeBoardItems(Home.this);
        noticeBoardItems.execute();

    }


    public void swipeRefresh(){
        userPrefs.setFirstLoading(true);
        Intent tohome=new Intent(Home.this, Home.class);
        tohome.putExtra("institution_name",institutionName);
        tohome.putExtra("role","student");
        startActivity(tohome);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tohome=new Intent(Home.this, Home.class);
        tohome.putExtra("institution_name",institutionName);
        tohome.putExtra("role","Student");
        startActivity(tohome);
    }



}
