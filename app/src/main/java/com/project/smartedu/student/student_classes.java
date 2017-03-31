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

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;

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
            noti_bar = (NotificationBar) getSupportFragmentManager().findFragmentById(R.id.noti);
            noti_bar.setTexts(ParseUser.getCurrentUser().getUsername(), role,super.institutionName);



            classList = (ListView) findViewById(R.id.classesList);
            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
            drawerFragment.setDrawerListener(this);

        }catch(Exception create_error){
            Log.d("user", "error in create student_classes: " + create_error.getMessage());
            Toast.makeText(student_classes.this,"error " + create_error, Toast.LENGTH_LONG).show();
        }

        //  myList = dbHandler.getAllTasks();

        //Log.i("Anmol", "(Inside MainActivity) dbHandler.getAllTasks().toString() gives " + dbHandler.getAllTasks().toString());
        //ListAdapter adapter = new CustomListAdapter(getApplicationContext(), dbHandler.getAllTasks());
        //taskList.setAdapter(adapter);








//        Log.d("classGradeid ", classGradeId);

        final HashMap<String,String> classMap=new HashMap<String,String>();

       /* ParseQuery<ParseObject> classQuery = ParseQuery.getQuery(ClassTable.TABLE_NAME);
        classQuery.whereEqualTo(ClassTable.CLASS_NAME, ParseObject.createWithoutData(ClassGradeTable.TABLE_NAME, classGradeId));
        classQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> classListRet, ParseException e) {
                if (e == null) {

                    if (classListRet.size() != 0) {

                        ArrayList<String> classLt = new ArrayList<String>();
                        ArrayAdapter adapter = new ArrayAdapter(student_classes.this, android.R.layout.simple_list_item_1, classLt);


                        Log.d("classes", "Retrieved " + classListRet.size() + " users");
                        //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();
                        for (int i = 0; i < classListRet.size(); i++) {
                            ParseObject u = (ParseObject) classListRet.get(i);
                            // ParseObject classGradeObject = ((ParseObject) u.get(ClassTable.CLASS_NAME));

                            String name = u.getString(ClassTable.SUBJECT);


                            adapter.add(name);
                            classMap.put(name, u.getObjectId());


                        }
                        classList.setAdapter(adapter);
                    } else {
                        Log.d("class", "error in query");
                    }
                } else {
                    Log.d("user", "Error: " + e.getMessage());
                }

            }
        });

*/



        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = ((TextView) view).getText().toString();
                //String[] classSpecs=item.split(" ");


                classId = classMap.get(item);          //object id corresponding to selected item will be retreived
                Log.d("student_classes ", "class id: " + classId);

              /*  if (_for.equals("attendance")) {
                    Intent to_view_atten = new Intent(student_classes.this, view_attendance.class);
                    to_view_atten.putExtra("studentId", studentId);
                    to_view_atten.putExtra("classId", classId);
                    to_view_atten.putExtra("institution_name",institutionName);
                    to_view_atten.putExtra("role", role);
                    startActivity(to_view_atten);
                }
                else if (_for.equals("upload")) {
                    Intent to_upload = new Intent(student_classes.this, UploadMaterial_students.class);
                    to_upload.putExtra("studentId", studentId);
                    to_upload.putExtra("classId", classId);
                    to_upload.putExtra("institution_name",institutionName);
                    to_upload.putExtra("role", role);

                    startActivity(to_upload);
                }
*/

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
