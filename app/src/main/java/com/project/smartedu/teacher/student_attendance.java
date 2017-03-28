package com.project.smartedu.teacher;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.Constants;
import com.project.smartedu.R;
import com.project.smartedu.common.Tasks;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class student_attendance extends Fragment {

    String studentid;
    String classId;
    String institutionName;

    ListView attendanceList;

    DatabaseReference databaseReference;



    HashMap<String,HashMap<String,String>> attendancemap;       //map from subject to attendance hashmap ( day to  status)





    private class AttendanceItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public AttendanceItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Attendance Data");
            pd.setCancelable(false);
            pd.show();
            databaseReference=Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentid).child("attendance");

        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {

                            Log.d("nowdata","sname = " + ds.getKey());


                            HashMap<String, String> retAttendanceData = (HashMap<String, String>) ds.getValue();

                           /* for(String date:retAttendanceData.keySet()){

                                Log.d("nowdata","date = " + date);
                                Log.d("nowdata","status = " + retAttendanceData.get(date));

                            }*/

                            attendancemap.put(ds.getKey(),retAttendanceData);
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
//                   Toast.makeText(async_context,userPrefs.getUserName(),Toast.LENGTH_LONG).show();
setAttendanceList();
                   // noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);
                    pd.dismiss();

                }
            }, 500);  // 100 milliseconds


        }
        //end firebase_async_class
    }







    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View android = inflater.inflate(R.layout.fragment_student_attendance, container, false);
       studentid= getArguments().getString("id");
        classId=getArguments().getString("classId");
        institutionName=getArguments().getString("institution_name");

        Log.d("nowdata",institutionName + " " + classId + " " + studentid);

        attendancemap=new HashMap<>();

        attendanceList=(ListView)android.findViewById(R.id.attendance_list);
        AttendanceItems attendanceItems=new AttendanceItems(getContext());
        attendanceItems.execute();



        return android;
    }



    public void setAttendanceList(){

        if (attendancemap.size()==0){
            Toast.makeText(getContext(),"No attendance found",Toast.LENGTH_LONG).show();
        }else{

            ArrayList<String> attendanceitems=new ArrayList<>();

            for (String subject:attendancemap.keySet()){
                int total=0;
                int presentcount=0;


                for(String date:attendancemap.get(subject).keySet()){

                    total++;
                    if(attendancemap.get(subject).get(date).equals("p")){
                        presentcount++;
                    }

                }


                attendanceitems.add(subject + " total = " + total + " present = " + presentcount);

              }

            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, attendanceitems);
           attendanceList.setAdapter(adapter);

        }

    }

}