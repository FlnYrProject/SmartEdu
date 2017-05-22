package com.project.smartedu.common;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.CustomAdapter;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.Model;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.database.Teachers;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.student.StudentUserPrefs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class message_to_teacher extends BaseActivity {

    private Toolbar mToolbar;

    private FragmentDrawer drawerFragment;


    ListView teacherList;
    NotificationBar noti_bar;
    String classGradeId;

    Button selected_button;
    EditText message;
    Button broadcast;
    Button sendmessage;


    Model[] modelItems;
    CustomAdapter customAdapter;
    String studentId;


    UserPrefs userPrefs;
    StudentUserPrefs studentUserPrefs;

    HashMap<String,String> localteachermap;

    DatabaseReference databaseReference;

    ArrayList<String> teacherrecipients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message_to_teacher);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Teachers");
        Intent from_student = getIntent();
        role = from_student.getStringExtra("role");
        institutionName= from_student.getStringExtra("institution");
        noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);

        userPrefs=new UserPrefs(message_to_teacher.this);
        studentUserPrefs=new StudentUserPrefs(message_to_teacher.this);
        teacherrecipients=new ArrayList<>();

        noti_bar.setTexts(userPrefs.getUserName(),role,institutionName);



        broadcast=(Button)findViewById(R.id.broadcast);;
        teacherList = (ListView) findViewById(R.id.studentList);
        selected_button=(Button)findViewById(R.id.selected);
        selected_button.setVisibility(View.INVISIBLE);
        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar,role);
        drawerFragment.setDrawerListener(this);



        localteachermap=new HashMap<>();






        if(StudentUserPrefs.teachersuseridLt.size()>0) {
            modelItems = new Model[StudentUserPrefs.teachersuseridLt.size()];
            //ArrayList<String> studentLt = new ArrayList<String>();
            // ArrayAdapter adapter = new ArrayAdapter(AddAttendance_everyday.this, android.R.layout.simple_list_item_1, studentLt);
            //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();

            Log.d("user", "Retrieved " + StudentUserPrefs.teachersuseridLt.size() + " students in this class");
            //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
            for (int i = 0; i < StudentUserPrefs.teachersuseridLt.size(); i++) {

                String teacherid=StudentUserPrefs.teachersuseridLt.get(i);
                Teachers teachers=StudentUserPrefs.teacherHashMap.get(teacherid);
               String entry= teachers.getSerial_number() + ". " + teachers.getName();
                localteachermap.put(entry,teacherid);


                modelItems[i] = new Model(entry, 0);


            }

            customAdapter = new CustomAdapter(message_to_teacher.this, modelItems, studentUserPrefs.getClassId());
            // no use of the reference context here but in attendance_everyday

            teacherList.setAdapter(customAdapter);
            selected_button.setVisibility(View.VISIBLE);


            broadcast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                       broadcasting();
                }
            });


            selected_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sendToSelected();
                }
            });

        }else{
            Toast.makeText(message_to_teacher.this,"No teachers alloted to class",Toast.LENGTH_LONG).show();
        }



    }



    protected void broadcasting()
    {
        final Dialog marks_add=new Dialog(message_to_teacher.this);
        marks_add.setContentView(R.layout.sending_message_to_teacher);
        marks_add.setTitle("Give Message");
        setDialogSize(marks_add);

        message = (EditText)marks_add.findViewById(R.id.message);
        sendmessage=(Button)marks_add.findViewById(R.id.send_message);

        sendmessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(message.getText().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "no message or role not selected", Toast.LENGTH_LONG).show();
                }else
                {

                    for(int x = 0; x< StudentUserPrefs.teachersuseridLt.size(); x++){

                        String teacherid= StudentUserPrefs.teachersuseridLt.get(x);
                        Teachers teachers= StudentUserPrefs.teacherHashMap.get(teacherid);




                            String client_userid = teacherid;


                            databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(client_userid).push();
                            databaseReference.child("content").setValue(message.getText().toString());
                            java.util.Calendar calendar = Calendar.getInstance();
                            databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
                            databaseReference.child("name").setValue(teachers.getName());




                            databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(client_userid).child("received").child(firebaseAuth.getCurrentUser().getUid()).push();
                            databaseReference.child("content").setValue(message.getText().toString());
                            databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
                            databaseReference.child("name").setValue(userPrefs.getUserName());






                    }
                    Toast.makeText(message_to_teacher.this, "Message Successfully Broadcasted to Teachers", Toast.LENGTH_LONG).show();
                    marks_add.dismiss();


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

            final Dialog marks_add = new Dialog(message_to_teacher.this);
            marks_add.setContentView(R.layout.sending_message_to_teacher);
            marks_add.setTitle("Give Message");
            setDialogSize(marks_add);
            message = (EditText) marks_add.findViewById(R.id.message);
            sendmessage = (Button) marks_add.findViewById(R.id.send_message);



            sendmessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    teacherrecipients.clear();
                    if (message.getText().equals("")) {
                        Toast.makeText(getApplicationContext(), "no message", Toast.LENGTH_LONG).show();
                    } else {

                        for (int i = 0; i < customAdapter.getCount(); i++) {
                            final Model item = customAdapter.getItem(i);

                            if(item.isChecked()) {

                                String teacherentry=item.getName();
                                String teacherid=localteachermap.get(teacherentry);
                                teacherrecipients.add(teacherid);



                            }

                        }

                        giveMessage();
                        marks_add.dismiss();

                    }
                }
            });
            marks_add.show();
        }
    }




    protected void giveMessage()
    {


        for(int x=0;x<teacherrecipients.size();x++){
            String client_userid = teacherrecipients.get(x);
            Teachers teachers=StudentUserPrefs.teacherHashMap.get(client_userid);
            databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child("sent").child(client_userid).push();
            databaseReference.child("content").setValue(message.getText().toString());
            java.util.Calendar calendar = Calendar.getInstance();
            databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
            databaseReference.child("name").setValue(teachers.getName());




            databaseReference= Constants.databaseReference.child(Constants.MESSAGES_TABLE).child(client_userid).child("received").child(firebaseAuth.getCurrentUser().getUid()).push();
            databaseReference.child("content").setValue(message.getText().toString());
            databaseReference.child("time").setValue(String.valueOf((calendar.getTimeInMillis()/1000)*1000));
            databaseReference.child("name").setValue(userPrefs.getUserName());


        }

        Toast.makeText(message_to_teacher.this, "Message Successfully Sent to Teacher", Toast.LENGTH_LONG).show();


    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(message_to_teacher.this,LoginActivity.class);
            startActivity(nouser);
        }
    }
}
