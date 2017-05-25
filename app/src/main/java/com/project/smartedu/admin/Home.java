package com.project.smartedu.admin;

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
import android.widget.Button;
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
import com.project.smartedu.common.Tasks;
import com.project.smartedu.database.Allotments;
import com.project.smartedu.database.Class;
import com.project.smartedu.database.Notice;
import com.project.smartedu.database.Schedule;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.noticeBoard.NoticeBoard;
import com.project.smartedu.notification.NotificationBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class Home extends BaseActivity{

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    //  Notification_bar noti_bar;

    SwipeRefreshLayout swipeRefreshLayout;

    DatabaseReference databaseReference;
    Button logout;
    UserPrefs userPrefs;
    AdminUserPrefs adminUserPrefs;


    ArrayList<String> taskLt;
    HashMap<String,String> taskidmap; //to map task to its  id


    ArrayList<String> teacherLt;
    ArrayList<String> teacheruseridLt;
    HashMap<String,String> teachersusermap; //to map teacher to its user id
    HashMap<String,String> teachersuserreversemap; //to map user id to teacher
    ArrayList<String> tempteacherlt;

    ArrayList<Schedule> scheduleslt;
    HashMap<String,ArrayList<Schedule>> schedulesmaplt;
    HashMap<Schedule,String> schedulekeymap;        //to map schedule to the key

    NotificationBar noti_bar;
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










    private class AllotmentItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public AllotmentItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            databaseReference = Constants.databaseReference.child(Constants.ALLOTMENTS_TABLE).child(institutionName);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Allotments");
            pd.setCancelable(false);
            pd.show();
            AdminUserPrefs.allotmments.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            Log.d("ds.key",ds.getKey());             //teacher id
                            HashMap<String,String> allotmentsmap= (HashMap<String,String>) ds.getValue();


                            Allotments retAllotment=new Allotments(ds.getKey(),allotmentsmap);
                            AdminUserPrefs.allotmments.add(retAllotment);


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


            int size=AdminUserPrefs.allotmments.size();
            Toast.makeText(getApplicationContext(),size + " allotments found",Toast.LENGTH_LONG).show();



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














    private class ClassItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public ClassItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            databaseReference = Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Classes");
            pd.setCancelable(false);
            pd.show();
            AdminUserPrefs.classes.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        synchronized (lock) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {

                               Log.d("ds.key",ds.getKey());             //class name
                                HashMap<String,Object> classesmap=(HashMap<String,Object>)ds.getValue();

                                for(String key:classesmap.keySet()){
                                    Log.d("key",key);                 //section

                                    HashMap<String,Object> classdetailmap=(HashMap<String, Object>) classesmap.get(key);

                                    Log.d("id", String.valueOf(classdetailmap.get("id")));               //class id

                                    HashMap<String,String> subjects=(HashMap<String, String>) classdetailmap.get("subject");
                                    HashMap<String,String> teachers= (HashMap<String, String>) classdetailmap.get("teacher");

                                    Class retClass=new Class(String.valueOf(classdetailmap.get("id")),subjects,teachers);
                                    AdminUserPrefs.classes.add(retClass);


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

            int size=AdminUserPrefs.classes.size();
            Toast.makeText(getApplicationContext(),size + " classes found",Toast.LENGTH_LONG).show();



            //Show the log in progress_bar for at least a few milliseconds

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                    loadAllotmentData();
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
                                    Schedule schedule = new Schedule(cursorday, start_date, end_date, info);
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
                    loadClassData();
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
            teachersuserreversemap.clear();
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
                                 //   Toast.makeText(getApplicationContext(),entry,Toast.LENGTH_LONG).show();
                                    teacherLt.add(entry);
                                    teachersusermap.put(entry, teacheruseridLt.get(finalI));
                                    teachersuserreversemap.put(teacheruseridLt.get(finalI),entry);

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


            AdminUserPrefs.teacherLt=teacherLt;
            AdminUserPrefs.teachersusermap=teachersusermap;
            AdminUserPrefs.teachersuserreversemap=teachersuserreversemap;
            AdminUserPrefs.teacheruseridLt=teacheruseridLt;

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












    private class TaskItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public TaskItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

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
                    loadTeacherData();
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
        setContentView(R.layout.activity_home);


        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Dashboard");


        role="admin";
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,"admin");
        drawerFragment.setDrawerListener(this);

        userPrefs=new UserPrefs(Home.this);
        adminUserPrefs=new AdminUserPrefs(getApplicationContext());

        noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);


        noticeBoard=(NoticeBoard)getSupportFragmentManager().findFragmentById(R.id.notice_board);


        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh();
            }
        });

        taskLt=UserPrefs.taskItems;
        taskidmap=UserPrefs.taskidmap;
        teacherLt=AdminUserPrefs.teacherLt;
        teacheruseridLt=AdminUserPrefs.teacheruseridLt;
        teachersusermap=AdminUserPrefs.teachersusermap;
        teachersuserreversemap=AdminUserPrefs.teachersuserreversemap;
        tempteacherlt=new ArrayList<>();
        //schedulesmaplt=AdminUserPrefs.schedulesmaplt;
        scheduleslt=new ArrayList<>();
        schedulekeymap=UserPrefs.schedulekeymap;


        Intent from_login = getIntent();
        institutionName=from_login.getStringExtra("institution_name");
        if(userPrefs.isFirstLoading()) {
            userPrefs.setFirstLoading(false);
            setupNotiBar();
            loadData();

        }else{
            noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);
            noticeBoard.setData(institutionName,role);


        }











        GridView gridview = (GridView) findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(this, densityX,densityY, "admin"));








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
                        task_intent.putExtra("institution_name",institutionName);
                        task_intent.putExtra("role", "admin");
                        startActivity(task_intent);


                } else if (position == 2) { //classes

                 Intent task_intent = new Intent(Home.this,Classes.class);

                    task_intent.putExtra("institution_name",institutionName);
                    task_intent.putExtra("role", "admin");
                    startActivity(task_intent);

                  //  loadAllotmentData();

                }else if (position == 3) { //classes

                    Intent task_intent = new Intent(Home.this,TeacherAttendance.class);

                    task_intent.putExtra("institution_name",institutionName);
                    task_intent.putExtra("role", "admin");
                    startActivity(task_intent);

                    //  loadAllotmentData();

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

    private void loadTeacherData(){

        TeacherItems teacherasync = new TeacherItems(Home.this);        //get teacher data
        teacherasync.execute();

    }


    private void loadScheduleData(){



            ScheduleItems scheduleasync = new ScheduleItems(Home.this);        //get teacher data
            scheduleasync.execute();


    }


    private void loadClassData(){

        ClassItems classesasync = new ClassItems(Home.this);        //get teacher data
        classesasync.execute();

    }


    private void loadAllotmentData(){

        AllotmentItems allotmentItemsasync = new AllotmentItems(Home.this);        //get teacher data
       allotmentItemsasync.execute();

    }



    private void loadNoticeBoardData(){

        NoticeBoardItems noticeBoardItems=new NoticeBoardItems(Home.this);
        noticeBoardItems.execute();

    }

    public void swipeRefresh(){
        userPrefs.setFirstLoading(true);
        Intent tohome=new Intent(Home.this,Home.class);
        tohome.putExtra("institution_name",institutionName);
        startActivity(tohome);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tohome=new Intent(Home.this,Home.class);
        tohome.putExtra("institution_name",institutionName);
        startActivity(tohome);
    }
}
