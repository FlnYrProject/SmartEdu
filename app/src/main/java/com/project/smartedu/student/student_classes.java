package com.project.smartedu.student;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.common.UploadMaterial_students;
import com.project.smartedu.common.view_attendance;
import com.project.smartedu.database.Teachers;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;
import com.project.smartedu.parent.ParentUserPrefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class student_classes extends BaseActivity {




    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    String _for;

    ListView classList;

    NotificationBar noti_bar;
    String studentId;
    String classId;


    DatabaseReference databaseReference;

    UserPrefs userPrefs;
    StudentUserPrefs studentUserPrefs;
    ParentUserPrefs parentUserPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_student_classes);

            Intent from_home = getIntent();
            _for = from_home.getStringExtra("for");
            role = from_home.getStringExtra("role");
            institutionName=from_home.getStringExtra("institution_name");
            studentId=from_home.getStringExtra("studentId");
            classId=from_home.getStringExtra("classId");


            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Classes");


            userPrefs=new UserPrefs(student_classes.this);
            parentUserPrefs=new ParentUserPrefs(student_classes.this);
            studentUserPrefs=new StudentUserPrefs(student_classes.this);

            noti_bar = (NotificationBar) getSupportFragmentManager().findFragmentById(R.id.noti);
            noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);



            classList = (ListView) findViewById(R.id.classesList);
            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
            drawerFragment.setDrawerListener(this);

        }catch(Exception create_error){
            Log.d("user", "error in create student_classes: " + create_error.getMessage());
            Toast.makeText(student_classes.this,"error " + create_error, Toast.LENGTH_LONG).show();
        }



        final HashMap<String,Teachers> classMap=new HashMap<String,Teachers>();         //map from subject to teacher

        ArrayList<String> classLt = new ArrayList<String>();

        for(int x=0;x<StudentUserPrefs.teachersuseridLt.size();x++){

            String teacherid=StudentUserPrefs.teachersuseridLt.get(x);

            Teachers teachers =StudentUserPrefs.teacherHashMap.get(teacherid);


            for(int y=0;y<teachers.getSubjects().size();y++){

                classLt.add(teachers.getSubjects().get(y));

                classMap.put(teachers.getSubjects().get(y),teachers);

            }



        }


        ArrayAdapter adapter = new ArrayAdapter(student_classes.this, android.R.layout.simple_list_item_1, classLt);
        classList.setAdapter(adapter);






        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = ((TextView) view).getText().toString();
                //String[] classSpecs=item.split(" ");


                Teachers teachers=classMap.get(item);



                if (_for.equals("attendance")) {
                    Intent to_view_atten = new Intent(student_classes.this, view_attendance.class);
                    if(role.equalsIgnoreCase("Parent")){
                        to_view_atten.putExtra("studentId",parentUserPrefs.getSelectedChildId());
                        to_view_atten.putExtra("classId", parentUserPrefs.getChildClass());
                    }else {
                        to_view_atten.putExtra("studentId", firebaseAuth.getCurrentUser().getUid());
                        to_view_atten.putExtra("classId", studentUserPrefs.getClassId());

                    }

                    to_view_atten.putExtra("subject", item);
                    to_view_atten.putExtra("institution_name",institutionName);
                    to_view_atten.putExtra("role", role);
                    startActivity(to_view_atten);
                }
                else if (_for.equals("upload")) {
                    Intent to_upload = new Intent(student_classes.this, UploadMaterial_students.class);
                    if(role.equalsIgnoreCase("Parent")){
                        to_upload.putExtra("studentId",parentUserPrefs.getSelectedChildId());
                        to_upload.putExtra("classId", parentUserPrefs.getChildClass());
                    }else {
                        to_upload.putExtra("studentId", firebaseAuth.getCurrentUser().getUid());
                        to_upload.putExtra("classId", studentUserPrefs.getClassId());
                    }
                    to_upload.putExtra("subject", item);
                    to_upload.putExtra("institution_name",institutionName);
                    to_upload.putExtra("role", role);

                    startActivity(to_upload);
                }

            }
        });



    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(student_classes.this,LoginActivity.class);
            startActivity(nouser);
        }
    }




}
