package com.project.smartedu.teacher;

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
import com.project.smartedu.database.Exam;
import com.project.smartedu.database.Notice;
import com.project.smartedu.database.Students;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.noticeBoard.NoticeBoard;
import com.project.smartedu.notification.NotificationBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class Home extends BaseActivity {

      private Toolbar mToolbar;
      private FragmentDrawer drawerFragment;
    NotificationBar noti_bar;
    TeacherUserPrefs teacherUserPrefs;
            UserPrefs userPrefs;


    ArrayList<String> taskLt;
    HashMap<String,String> taskidmap; //to map task to its  id

    ArrayList<String> uploadLt;
    HashMap<String,String> uploadidmap; //to map upload to its  id
    HashMap<com.project.smartedu.database.Uploads,String> uploadkeymap;

    ArrayList<com.project.smartedu.database.Schedule> scheduleslt;
    HashMap<String,ArrayList<com.project.smartedu.database.Schedule>> schedulesmaplt;
    HashMap<com.project.smartedu.database.Schedule,String> schedulekeymap;        //to map schedule to the key

    DatabaseReference databaseReference;



    SwipeRefreshLayout swipeRefreshLayout;

    ArrayList<String> subjects;



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







    private class ExamItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public ExamItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Exams");
            pd.setCancelable(false);
            pd.show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();


            for(int x=0;x<TeacherUserPrefs.allotments.size();x++){

                String allotted_classid=TeacherUserPrefs.allotments.get(x);

                for(int y=0;y<TeacherUserPrefs.subjectallotmentmap.get(allotted_classid).size();y++){


                    final String subject=TeacherUserPrefs.subjectallotmentmap.get(allotted_classid).get(y);

                    databaseReference=Constants.databaseReference.child(Constants.EXAMS_TABLE).child(institutionName).child(allotted_classid).child(subject);


                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            synchronized (lock) {
                                for(DataSnapshot ds:dataSnapshot.getChildren()) {

                                    Log.d("key"," " + ds.getKey());     //key of exam

                                    TeacherUserPrefs.examidLt.add(ds.getKey());
                                    String name="";
                                    String max_marks="";
                                    String date="";
                                    for(DataSnapshot dataSnapshot1:ds.getChildren()){


                                        if(dataSnapshot1.getKey().equalsIgnoreCase("name")){
                                            name=dataSnapshot1.getValue().toString();
                                        }

                                        if(dataSnapshot1.getKey().equalsIgnoreCase("max_marks")){
                                            max_marks=dataSnapshot1.getValue().toString();
                                        }

                                        if(dataSnapshot1.getKey().equalsIgnoreCase("date")){
                                            date=dataSnapshot1.getValue().toString();
                                        }



                                    }

                                    Exam exam=new Exam(ds.getKey(),name,date,max_marks,subject);
                                    TeacherUserPrefs.examHashMap.put(ds.getKey(),exam);

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
//                   Toast.makeText(async_context,userPrefs.getUserName(),Toast.LENGTH_LONG).show();
                    //setAttendanceList();
                    // noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);
                    pd.dismiss();
                    pd=null;
loadNoticeBoardData();
                }
            }, 500);  // 100 milliseconds


        }
        //end firebase_async_class
    }













    private class SubjectAllotmentItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public SubjectAllotmentItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            subjects=new ArrayList<>();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Subject Allotments");
            pd.setCancelable(false);
            pd.show();
            TeacherUserPrefs.subjectallotmentmap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();




            for(int x=0;x<TeacherUserPrefs.allotments.size();x++){

                final String classId=TeacherUserPrefs.allotments.get(x);
                String[] clsdetails=classId.split("_");
                String cls=clsdetails[1];
                String section=clsdetails[2];
               subjects.clear();
                databaseReference=Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(cls).child(section).child("subject");


                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        synchronized (lock) {
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                String teacheruid= (String) ds.getValue();

                                if(teacheruid.equals(firebaseAuth.getCurrentUser().getUid())){

                                    String subjectname=ds.getKey();     //subject name

                                    if(TeacherUserPrefs.subjectallotmentmap.containsKey(classId)){
                                        Log.d("test","here");
                                        TeacherUserPrefs.subjectallotmentmap.get(classId).add(subjectname);

                                    }else{
                                        Log.d("test","here again " + subjectname);
                                       // subjects.clear();
                                        ArrayList<String> sbjlt=new ArrayList<String>();
                                        sbjlt.add(subjectname);
                                        TeacherUserPrefs.subjectallotmentmap.put(classId,sbjlt);

                                    }


                                }


                            }

                            //TeacherUserPrefs.subjectallotmentmap.put(classId,subjects);


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


          /*  for(int x=0;x<TeacherUserPrefs.allotments.size();x++){
                String id=TeacherUserPrefs.allotments.get(x);
                ArrayList<String> sbj=TeacherUserPrefs.subjectallotmentmap.get(id);
                for(int i=0;i<sbj.size();i++){
                    Toast.makeText(getApplicationContext(),sbj.get(i)+" in "+id,Toast.LENGTH_LONG).show();
                }
            }*/

          //  Toast.makeText(getApplicationContext(),TeacherUserPrefs.subjectallotmentmap.get("NITJ_10_b").get(0),Toast.LENGTH_LONG).show();


            //Show the log in progress_bar for at least a few milliseconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                    //loadUploadData();
                    loadExamData();

                }
            }, 500);  // 100 milliseconds
        }


        //end firebase_async_class
    }














    private class StudentsItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public StudentsItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Students of alloted classes");
            pd.setCancelable(false);
            pd.show();
            TeacherUserPrefs.studentsHashMap.clear();
            TeacherUserPrefs.studentsuseridLt.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();
            final Object lock2 = new Object();


            for(int x=0;x<TeacherUserPrefs.allotments.size();x++){

                final String classid=TeacherUserPrefs.allotments.get(x);
                String[] classitems=classid.split("_");

                databaseReference=Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(classitems[1]).child(classitems[2]).child("student");


                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {




                            String roll=ds.getValue().toString(); //gives rollnumber
                            String studentid=ds.getKey();   //gives student id

                            TeacherUserPrefs.studentsuseridLt.add(studentid);

                            com.project.smartedu.database.Students stu=new Students(studentid,classid,roll,"");
                            TeacherUserPrefs.studentsHashMap.put(studentid,stu);

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



                for(int i=0;i<TeacherUserPrefs.studentsuseridLt.size();i++) {

                    final String studentid=TeacherUserPrefs.studentsuseridLt.get(i);
                    DatabaseReference dataReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(studentid);

                    final int finalI = i;
                    dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            synchronized (lock2) {
                                for (DataSnapshot dsi : dataSnapshot.getChildren()) {
                                    if (dsi.getKey().equals("name")) {

                                        String stuname=dsi.getValue().toString();

                                        TeacherUserPrefs.studentsHashMap.get(studentid).setName(stuname);


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








            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            //Handles the stuff after the synchronisation with the firebase listener has been achieved
            //The main UI is already idle by this moment
            super.onPostExecute(aVoid);


            int size=TeacherUserPrefs.studentsuseridLt.size();
           // Toast.makeText(getApplicationContext(),size + " total students found",Toast.LENGTH_LONG).show();

            //Show the log in progress_bar for at least a few milliseconds

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                    loadSubjectAllotmentData();
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
            databaseReference = Constants.databaseReference.child(Constants.ALLOTMENTS_TABLE).child(institutionName).child(firebaseAuth.getCurrentUser().getUid());

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Allotments");
            pd.setCancelable(false);
            pd.show();
          TeacherUserPrefs.allotments.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {

                            Log.d("ds.key",ds.getKey());             //random key
                            String classid= (String) ds.getValue();
                            TeacherUserPrefs.allotments.add(classid);


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


            int size=TeacherUserPrefs.allotments.size();
            Toast.makeText(getApplicationContext(),size + " allotments found",Toast.LENGTH_LONG).show();

            //Show the log in progress_bar for at least a few milliseconds
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;
                    loadStudentData();
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
                   loadAllotmentData();
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




    private class UploadItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public UploadItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);
            uploadLt=new ArrayList<>();

            databaseReference = Constants.databaseReference.child(Constants.UPLOADS_TABLE).child(institutionName);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Upload List");
            pd.setCancelable(false);
            pd.show();
            //uploadLt.clear();
            //uploadidmap.clear();
            uploadkeymap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for (DataSnapshot ds : dataSnapshot.getChildren()) {
                            HashMap<String, String> retUploadList = (HashMap<String, String>) ds.getValue();

                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

                            String upload_type = retUploadList.get("upload_type");
                            String subject = retUploadList.get("subject");
                            String topic = retUploadList.get("topic");
                            String imageUrl = retUploadList.get("imageUrl");
                            //String teacher = retUploadList.get("teacher");
                            Long date = Long.parseLong(retUploadList.get("date"));
                            //Long due_date = Long.parseLong(retUploadList.get("due_date"));


                            //String dateString = formatter.format(new Date(Long.parseLong(retUploadList.get("date"))));
                            com.project.smartedu.database.Uploads upload = new com.project.smartedu.database.Uploads(upload_type, subject, topic, imageUrl, date);

                            //String entry=retUploadList.get("upload_type")+ "\n" +retUploadList.get("subject") +"\n" +retUploadList.get("topic")+"\n" +retUploadList.get("imageUrl")+"\n" +retUploadList.get("teacher") + "\n" + dateString;
                            //Log.d("key",key);
                            uploadkeymap.put(upload, ds.getKey());
                            //uploadidmap.put(entry,ds.getKey());
                            //uploadLt.add(entry);

                            //}



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
       //     Toast.makeText(getApplicationContext(),uploadLt.size() + " uploads found",Toast.LENGTH_LONG).show();

            UserPrefs.uploadkeymap=uploadkeymap;
           // UserPrefs.uploadidmap=uploadidmap;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    pd.dismiss();
                    pd=null;

                }
            }, 500);  // 100 milliseconds
        }
        //end firebase_async_class
    }








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.activity_home_teacher);
            Intent home=getIntent();
            role=home.getStringExtra("role");
            institutionName=home.getStringExtra("institution_name");
            Log.d("user", role);



        userPrefs=new UserPrefs(Home.this);
         teacherUserPrefs=new TeacherUserPrefs(Home.this);
            teacherUserPrefs.setInstituion(institutionName);

            noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
           // setupNotiBar();
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            //getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Dashboard");


            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);//pass role
            drawerFragment.setDrawerListener(this);

        noticeBoard=(NoticeBoard)getSupportFragmentManager().findFragmentById(R.id.notice_board);

        swipeRefreshLayout=(SwipeRefreshLayout)findViewById(R.id.swipe_layout);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefresh();
            }
        });


            taskLt = UserPrefs.taskItems;
            taskidmap = UserPrefs.taskidmap;

            schedulekeymap = UserPrefs.schedulekeymap;
            scheduleslt = new ArrayList<>();

            //uploadLt = UserPrefs.uploadItems;
            //uploadidmap = UserPrefs.uploadidmap;
            uploadkeymap = UserPrefs.uploadkeymap;

        if(userPrefs.isFirstLoading()) {
            userPrefs.setFirstLoading(false);
            setupNotiBar();
            loadData();
        }else{
            noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);
            noticeBoard.setData(institutionName,role);

        }




            GridView gridview = (GridView) findViewById(R.id.gridview);
            gridview.setAdapter(new ImageAdapter(this, densityX, densityY, role));
            gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View v,
                                        int position, long id) {
                    if (position == 0) {

                        if(TeacherUserPrefs.allotments.size()==0){
                            Toast.makeText(getApplicationContext(),"No Classes Allotted",Toast.LENGTH_LONG).show();
                        }else{
                            Intent attendance_intent = new Intent(Home.this, Classes.class);
                            attendance_intent.putExtra("institution_name",institutionName);
                            attendance_intent.putExtra("for", "attendance");
                            attendance_intent.putExtra("role", role);
                            startActivity(attendance_intent);
                        }

                    } else if (position == 1) {
                        Intent task_intent = new Intent(Home.this, Tasks.class);
                        task_intent.putExtra("institution_name",institutionName);
                        task_intent.putExtra("role", role);
                        startActivity(task_intent);
                    } else if (position == 2) {

                        if(TeacherUserPrefs.allotments.size()==0){
                            Toast.makeText(getApplicationContext(),"No Classes Allotted",Toast.LENGTH_LONG).show();
                        }else{
                            Intent student_intent = new Intent(Home.this, Classes.class);
                            student_intent.putExtra("institution_name",institutionName);
                            student_intent.putExtra("role", role);
                            student_intent.putExtra("for", "students");
                            startActivity(student_intent);
                        }

                    } else if (position == 3) {
                        Intent schedule_intent = new Intent(Home.this, Schedule.class);
                        schedule_intent.putExtra("institution_name",institutionName);
                        schedule_intent.putExtra("role", role);
                        startActivity(schedule_intent);
                    } else if (position == 4) {

                        if(TeacherUserPrefs.allotments.size()==0){
                            Toast.makeText(getApplicationContext(),"No Classes Allotted",Toast.LENGTH_LONG).show();
                        }else{
                            Intent addmarks_intent = new Intent(Home.this, Classes.class);
                            addmarks_intent.putExtra("institution_name",institutionName);
                            addmarks_intent.putExtra("role", role);
                            addmarks_intent.putExtra("for", "exam");
                            startActivity(addmarks_intent);
                        }

                    } else if (position == 5) {

                        if(TeacherUserPrefs.allotments.size()==0){
                            Toast.makeText(getApplicationContext(),"No Classes Allotted",Toast.LENGTH_LONG).show();
                        }else{
                            Intent upload_intent = new Intent(Home.this,Classes.class);
                            upload_intent.putExtra("institution_name",institutionName);
                            upload_intent.putExtra("role", role);
                            upload_intent.putExtra("for", "upload");
                            startActivity(upload_intent);
                        }


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

       ScheduleItems scheduleasync = new ScheduleItems(Home.this);        //getschedule data
        scheduleasync.execute();

    }

    private void loadUploadData(){

        UploadItems uploadasync = new UploadItems(Home.this);     //get upload data
        uploadasync.execute();

    }


    private void loadAllotmentData(){

        AllotmentItems allotmentasync = new AllotmentItems(Home.this);        //get teacher data
        allotmentasync.execute();

    }




    private void loadStudentData(){

        StudentsItems studentasync = new StudentsItems(Home.this);        //get teacher data
        studentasync.execute();

    }


    private void loadSubjectAllotmentData(){

       SubjectAllotmentItems subjectAllotmentItems = new SubjectAllotmentItems(Home.this);        //get teacher data
        subjectAllotmentItems.execute();

    }



    private void loadExamData(){

        ExamItems examItems = new ExamItems(Home.this);        //get teacher data
        examItems.execute();

    }


    private void loadNoticeBoardData(){

        NoticeBoardItems noticeBoardItems=new NoticeBoardItems(Home.this);
        noticeBoardItems.execute();

    }

    public void swipeRefresh(){
        userPrefs.setFirstLoading(true);
        Intent tohome=new Intent(Home.this, Home.class);
        tohome.putExtra("institution_name",institutionName);
        tohome.putExtra("role","teacher");
        startActivity(tohome);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tohome=new Intent(Home.this, Home.class);
        tohome.putExtra("institution_name",institutionName);
        tohome.putExtra("role","teacher");
        startActivity(tohome);
    }
}
