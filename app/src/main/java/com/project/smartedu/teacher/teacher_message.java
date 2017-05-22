package com.project.smartedu.teacher;

import android.app.Dialog;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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
import com.project.smartedu.CustomAdapter;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.Model;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.common.view_messages;
import com.project.smartedu.database.*;
import com.project.smartedu.database.Students;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class teacher_message extends BaseActivity {

    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;


    ListView studentList;
    NotificationBar noti_bar;
    String classId;
    String classGradeId;

    Button selected_button;
    EditText message;
    Button broadcast;
    Button sendmessage;
    Spinner role;
    Model[] modelItems;
    CustomAdapter customAdapter;

    HashMap<String,String> localstumap;
    ArrayList<String> studentLt;

    DatabaseReference databaseReference;

    UserPrefs userPrefs;
    TeacherUserPrefs teacherUserPrefs;


    ArrayList<String> recipientLt;


    ArrayList<String> sturecipients;

    ProgressDialog progressDialog;








    private class BroadcastParentItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;


        public BroadcastParentItems(Context context){
            this.async_context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recipientLt.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();



            for(int x=0;x<TeacherUserPrefs.studentsuseridLt.size();x++){

                String studentid=TeacherUserPrefs.studentsuseridLt.get(x);
                final com.project.smartedu.database.Students student=TeacherUserPrefs.studentsHashMap.get(studentid);


                if(student.getClass_id().equals(classId)){




                    databaseReference=Constants.databaseReference.child(Constants.PARENT_RELATION_TABLE).child(studentid);

                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {


                            synchronized (lock) {
                                recipientLt.add(dataSnapshot.getValue().toString() + ". " + student.getName());

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

           sendMessage();

            Toast.makeText(teacher_message.this, "Message Successfully Broadcasted to Parents", Toast.LENGTH_LONG).show();
progressDialog.dismiss();
        }
        //end firebase_async_class
    }











    private class ParentItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;


        public ParentItems(Context context){
            this.async_context = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            recipientLt.clear();

        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();


for(int x=0;x<sturecipients.size();x++){
    String studentid=sturecipients.get(x);
    final Students students=TeacherUserPrefs.studentsHashMap.get(studentid);
    databaseReference=Constants.databaseReference.child(Constants.PARENT_RELATION_TABLE).child(studentid);

    databaseReference.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {


            synchronized (lock) {

                recipientLt.add(dataSnapshot.getValue().toString()+". "+ students.getName());
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

            sendMessage();
            progressDialog.dismiss();

            Toast.makeText(teacher_message.this, "Message Successfully Sent to Parents", Toast.LENGTH_LONG).show();

        }
        //end firebase_async_class
    }










    public void  sendMessage(){

        java.util.Calendar calendar = Calendar.getInstance();
        long millis=calendar.getTimeInMillis();
        millis=(millis/1000)*1000;

        for(int x=0;x<recipientLt.size();x++){

            String values[]=recipientLt.get(x).split("\\. ");

            String client_userid=values[0];
            String name=values[1];

            databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(client_userid).push();
            databaseReference.child("content").setValue(message.getText().toString());
            databaseReference.child("time").setValue(String.valueOf(millis));
            databaseReference.child("name").setValue(name);


            databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(client_userid).child("received").child(firebaseAuth.getCurrentUser().getUid()).push();
            databaseReference.child("content").setValue(message.getText().toString());
            databaseReference.child("time").setValue(String.valueOf(millis));
            databaseReference.child("name").setValue(userPrefs.getUserName());

        }

        goToViewMessage();


    }











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_message);

        Intent from_student = getIntent();
        classId = from_student.getStringExtra("id");
        super.role=from_student.getStringExtra("role");
        institutionName=from_student.getStringExtra("institution_name");


        mToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mToolbar);
       /* getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);*/
        getSupportActionBar().setTitle("Students");

        userPrefs=new UserPrefs(teacher_message.this);
        teacherUserPrefs=new TeacherUserPrefs(teacher_message.this);

        noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), super.role,institutionName);


        broadcast=(Button)findViewById(R.id.broadcast);;
        studentList = (ListView) findViewById(R.id.studentList);
        selected_button=(Button)findViewById(R.id.selected);
        selected_button.setVisibility(View.INVISIBLE);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,"Teacher");
        drawerFragment.setDrawerListener(this);


        Toast.makeText(teacher_message.this, "id class selected is = " +classId, Toast.LENGTH_LONG).show();







recipientLt=new ArrayList<>();
        sturecipients=new ArrayList<>();
        progressDialog=new ProgressDialog(teacher_message.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Sending...");

        studentLt = new ArrayList<String>();
        localstumap=new HashMap<>();

        for(int x=0;x<TeacherUserPrefs.studentsuseridLt.size();x++){

            String studentid=TeacherUserPrefs.studentsuseridLt.get(x);
            com.project.smartedu.database.Students student=TeacherUserPrefs.studentsHashMap.get(studentid);


            if(student.getClass_id().equals(classId)){


                studentLt.add(student.getRoll_number() + ". " + student.getName());

                localstumap.put(student.getRoll_number() + ". " + student.getName(),studentid);

            }


        }


        broadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(studentLt.size()==0){

                    Toast.makeText(teacher_message.this,"No Students",Toast.LENGTH_LONG).show();

                } else {

                    broadcasting(classId);

                }
            }
        });


        if (studentLt.size() == 0) {
            Toast.makeText(teacher_message.this, "No Students", Toast.LENGTH_LONG).show();
        } else {


            modelItems = new Model[studentLt.size()];
            //ArrayList<String> studentLt = new ArrayList<String>();
            // ArrayAdapter adapter = new ArrayAdapter(AddAttendance_everyday.this, android.R.layout.simple_list_item_1, studentLt);
            //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

            Log.d("user", "Retrieved " + studentLt.size() + " students in this class");
            //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
            for (int i = 0; i < studentLt.size(); i++) {

                String student = studentLt.get(i);

                modelItems[i] = new Model(student, 0);


            }


            customAdapter = new CustomAdapter(teacher_message.this, modelItems, classId);
            studentList.setAdapter(customAdapter);
            selected_button.setVisibility(View.VISIBLE);
            selected_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToSelected();
                }
            });


        }


    }



    protected void broadcasting(final String classid)
    {
        final Dialog marks_add=new Dialog(teacher_message.this);
        marks_add.setContentView(R.layout.teacher_message);
        marks_add.setTitle("Give Message");
        role = (Spinner) marks_add.findViewById(R.id.role);
        ArrayAdapter<String> adapter;
        List<String> list;
        list = new ArrayList<String>();
        list.add("Student");
        list.add("Parent");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(adapter);
        message = (EditText)marks_add.findViewById(R.id.message);
        sendmessage=(Button)marks_add.findViewById(R.id.send_message);

        marks_add.show();
        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(message.getText().equals("")||role.getSelectedItem().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "no message or role not selected", Toast.LENGTH_LONG).show();
                }else
                {


                    progressDialog.show();


                    if(role.getSelectedItem().equals("Student"))
                    {

                        ArrayList<String> studentLt = new ArrayList<String>();
                        localstumap=new HashMap<>();



                        for(int x=0;x<TeacherUserPrefs.studentsuseridLt.size();x++){

                            String studentid=TeacherUserPrefs.studentsuseridLt.get(x);
                            com.project.smartedu.database.Students student=TeacherUserPrefs.studentsHashMap.get(studentid);


                            if(student.getClass_id().equals(classId)){

                                String client_userid = studentid;


                                databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(client_userid).push();
                              databaseReference.child("content").setValue(message.getText().toString());
                                java.util.Calendar calendar = Calendar.getInstance();
                                databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
                                databaseReference.child("name").setValue(student.getName());




                                databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(client_userid).child("received").child(firebaseAuth.getCurrentUser().getUid()).push();
                                databaseReference.child("content").setValue(message.getText().toString());
                                databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
                                databaseReference.child("name").setValue(userPrefs.getUserName());

                            }




                        }
                        Toast.makeText(teacher_message.this, "Message Successfully Broadcasted to Students", Toast.LENGTH_LONG).show();
marks_add.dismiss();


progressDialog.dismiss();

                        goToViewMessage();


                    }else       //if parent selected
                    {

                        BroadcastParentItems broadcastParentItems=new BroadcastParentItems(teacher_message.this);
                        broadcastParentItems.execute();
                        marks_add.dismiss();

                    }
                }

            }
        });
        marks_add.show();
    }


    protected void sendToSelected()
    {
        int count=0;
        for(int i=0;i<customAdapter.getCount();i++)
        {
            Model item=customAdapter.getItem(i);
            if (item.isChecked())
            {
                count++;
                break;
            }
        }


        if (count==0)
        {
            Toast.makeText(getApplicationContext(), "no recipients selected", Toast.LENGTH_LONG).show();
        }else {

            final Dialog marks_add = new Dialog(teacher_message.this);
            marks_add.setContentView(R.layout.teacher_message);
            marks_add.setTitle("Give Message");
            role = (Spinner) marks_add.findViewById(R.id.role);
            ArrayAdapter<String> adapter;
            List<String> list;
            list = new ArrayList<String>();
            list.add("Student");
            list.add("Parent");
            adapter = new ArrayAdapter<String>(getApplicationContext(),
                    android.R.layout.simple_spinner_item, list);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            role.setAdapter(adapter);
            message = (EditText) marks_add.findViewById(R.id.message);
            sendmessage = (Button) marks_add.findViewById(R.id.send_message);




            sendmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sturecipients.clear();

                    if (message.getText().equals("") || role.getSelectedItem().toString().equals("")) {
                        Toast.makeText(getApplicationContext(), "no message", Toast.LENGTH_LONG).show();
                    } else {
                        //   giveMessage(classobject);


                        for (int i = 0; i < customAdapter.getCount(); i++) {
                            final Model item = customAdapter.getItem(i);

                            if(item.isChecked()) {

                                String stuentry=item.getName();
                                String studentid=localstumap.get(stuentry);
                                sturecipients.add(studentid);



                            }

                        }

                        giveMessage(role.getSelectedItem().toString());
                        marks_add.dismiss();

                    }
                }
            });
            marks_add.show();
        }
    }




    protected void giveMessage(String to_role)
    {
        progressDialog.show();
        if(to_role.equals("Student")) {


            for(int x=0;x<sturecipients.size();x++){
                String client_userid = sturecipients.get(x);
                Students students=TeacherUserPrefs.studentsHashMap.get(client_userid);
                databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(client_userid).push();
                databaseReference.child("content").setValue(message.getText().toString());
                java.util.Calendar calendar = Calendar.getInstance();
                databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
                databaseReference.child("name").setValue(students.getName());




                databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(client_userid).child("received").child(firebaseAuth.getCurrentUser().getUid()).push();
                databaseReference.child("content").setValue(message.getText().toString());
                databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
                databaseReference.child("name").setValue(userPrefs.getUserName());


            }


progressDialog.dismiss();

            Toast.makeText(teacher_message.this, "Message Successfully Sent to Student", Toast.LENGTH_LONG).show();
            goToViewMessage();

        }else       //if parent selected
        {

           ParentItems parentItems=new ParentItems(teacher_message.this);
            parentItems.execute();
        }
    }


    private void goToViewMessage(){
        Intent to_view_message=new Intent(teacher_message.this,view_messages.class);
        to_view_message.putExtra("institution_name",institutionName);
        to_view_message.putExtra("role",super.role);
        to_view_message.putExtra("_for","received");
        startActivity(to_view_message);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(teacher_message.this,LoginActivity.class);
            startActivity(nouser);
        }
    }

}
