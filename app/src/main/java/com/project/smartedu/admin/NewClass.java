package com.project.smartedu.admin;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.database.Allotments;
import com.project.smartedu.database.Class;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class NewClass extends BaseActivity {
    private Toolbar mToolbar;
    String classname;
    String classsection;

    EditText classGradeName;
    EditText newSection;
    Button addClassButton;
    Spinner classteacherspinner;
    String classteachername;
    EditText classteachersubject;
    String subjectofclassteacher;

    ArrayList<String> teacherLt;
    ArrayAdapter teacheradapter=null;

    DatabaseReference databaseReference;

    String teacher;
    String teacherserial;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_class);



        Intent for_new_class = getIntent();
        role=for_new_class.getStringExtra("role");
        institutionName=for_new_class.getStringExtra("institution_name");

        classGradeName = (EditText) findViewById(R.id.newClassGrade);
        newSection = (EditText) findViewById(R.id.newClassSection);
        classteacherspinner=(Spinner)findViewById(R.id.classteacherselection);
        addClassButton = (Button) findViewById(R.id.addClassButton);
        classteachersubject=(EditText)findViewById(R.id.classteachersubject);

        final HashMap<String,String> teacherserialidmap=new HashMap<>();

        databaseReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName);

        if(AdminUserPrefs.teacherLt.size()==0){

            Toast.makeText(getApplicationContext(),"Add teachers first",Toast.LENGTH_LONG).show();

        }else{

            teacheradapter = new ArrayAdapter(NewClass.this, android.R.layout.simple_list_item_1, AdminUserPrefs.teacherLt);
            classteacherspinner.setAdapter(teacheradapter);

            addClassButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String[] itemValues = classteacherspinner.getSelectedItem().toString().split("\\. ");
                    classname = classGradeName.getText().toString().trim();
                    classsection = newSection.getText().toString().trim();
                    classteachername=itemValues[1];
                    teacherserial=itemValues[0];
                    String selectedteacherid=AdminUserPrefs.teachersusermap.get( classteacherspinner.getSelectedItem().toString()); //getting user id through selected entry
                    subjectofclassteacher=classteachersubject.getText().toString();

                    if( (classname.equals("")) || (classsection.equals("")) || (classteachername.equals("")) || subjectofclassteacher.equals("")) {
                        Toast.makeText(getApplicationContext(), "New Class details cannot be empty!", Toast.LENGTH_LONG).show();
                    } else {



                        databaseReference = Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(classname).child(classsection);

                        databaseReference.child("id").setValue(institutionName + "_" + classname + "_" + classsection);
                        //databaseReference.child("teacher").child(selectedteacherid).child("subject").setValue(subjectofclassteacher);
                        databaseReference.child("teacher").child(selectedteacherid).setValue("1");

                        databaseReference.child("subject").child(subjectofclassteacher).setValue(selectedteacherid);


                        Class newClass=new Class();     ///local db changes
                        newClass.setClassid(institutionName + "_" + classname + "_" + classsection);

                        HashMap<String,String> tmap=new HashMap<String, String>();
                        tmap.put(selectedteacherid,"1");
                        newClass.setTeachers(tmap);

                        HashMap<String,String> smap=new HashMap<String, String>();
                        smap.put(subjectofclassteacher,selectedteacherid);
                        newClass.setSubjects(smap);
                        AdminUserPrefs.classes.add(newClass);

                        addAllotment(classname,classsection,selectedteacherid, subjectofclassteacher);

                        Toast.makeText(NewClass.this, "New Class Added", Toast.LENGTH_LONG).show();
                        Intent to_admin_classes = new Intent(NewClass.this, Classes.class);
                        to_admin_classes.putExtra("institution_name", institutionName);
                        to_admin_classes.putExtra("role", role);
                        startActivity(to_admin_classes);



                    }
                }
            });

        }






    }


    protected void  addAllotment(String classname,String section, String teacheruid, final String subject){

        databaseReference= Constants.databaseReference.child(Constants.ALLOTMENTS_TABLE).child(institutionName).child(teacheruid).push();
        databaseReference.setValue(institutionName+"_"+classname+"_"+section);

        boolean ifteacherpresent=false;
        int index=0;

        for(int x=0;x<AdminUserPrefs.allotmments.size();x++){

            Allotments allotment=AdminUserPrefs.allotmments.get(x);

            if(allotment.getTeacherid().equals(teacheruid)){        //if teacher present
                ifteacherpresent=true;
                index=x;
                break;
            }

        }

        if(ifteacherpresent){

            AdminUserPrefs.allotmments.get(index).getAllots().put(databaseReference.getKey(),institutionName+"_"+classname+"_"+section);

        }else{
            HashMap<String, String> allotmap = new HashMap<String, String>();
            allotmap.put(databaseReference.getKey(), institutionName+"_"+classname+"_"+section );
            Allotments newAllot = new Allotments(teacheruid, allotmap);
            AdminUserPrefs.allotmments.add(newAllot);
        }


    }





    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(NewClass.this,LoginActivity.class);
            startActivity(nouser);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent to_admin_classes = new Intent(NewClass.this, Classes.class);
        to_admin_classes.putExtra("institution_name", institutionName);
        to_admin_classes.putExtra("role", role);
        startActivity(to_admin_classes);
    }


}
