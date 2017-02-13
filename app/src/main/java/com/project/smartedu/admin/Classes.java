package com.project.smartedu.admin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.database.TeacherTable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class Classes extends BaseActivity {


    ArrayList<String>  classLt;
    ArrayList<String>  sectionLt;
    ArrayList<String> teacherLt;

    int index;

    ListView classList;
    ListView classSectionList;
    Button ok;
    Button deleteClassButton;
    Button addSectionButton;
    EditText getNewSection;
    CheckBox ifclassteacher;

    Button done;
    Button deleteSectionButton;
    Button addSubjectButton;
    ListView classSubjectList;
    TextView dialog_heading;
    TextView confirm_message;
    Button cancel;
    Button proceed;
    EditText newSubject;
    Spinner subjectTeacherSpinner;
    Button addSubject;
    Spinner newsectionclassteacher;
    EditText classTeacherSubject;

    String classname,sectioname;


    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton addClassButton = (FloatingActionButton) findViewById(R.id.fab);
        addClassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent to_new_class = new Intent(Classes.this, NewClass.class);
                to_new_class.putExtra("institution_name", institutionName);
                to_new_class.putExtra("role", role);
                startActivity(to_new_class);
            }
        });

        Intent from_student = getIntent();
        role=from_student.getStringExtra("role");
        institutionName=from_student.getStringExtra("institution_name");

      
        classList = (ListView) findViewById(R.id.classList);
       

        databaseReference= Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> classmap=(HashMap<String, String>)dataSnapshot.getValue();

                if(classmap==null){

                    Toast.makeText(getApplicationContext(),"No classes added",Toast.LENGTH_LONG).show();

                }else{

                    Toast.makeText(getApplicationContext(),classmap.size() + " classes found ",Toast.LENGTH_LONG).show();
                    classLt = new ArrayList<>();

                    for ( String classname : classmap.keySet() ) {
                        System.out.println( classname );
                        classLt.add(classname);

                    }

                    ArrayAdapter adapter = new ArrayAdapter(Classes.this, android.R.layout.simple_list_item_1, classLt);
                    classList.setAdapter(adapter);


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = ((TextView) view).getText().toString();
                Log.d("class", item);

                classname=item;


                final Dialog class_info = new Dialog(Classes.this);
                class_info.setContentView(R.layout.class_details);
                class_info.setTitle(item);

                setDialogSize(class_info);
                dialog_heading = (TextView) class_info.findViewById(R.id.description);
                dialog_heading.setText("Sections");
                classSectionList = (ListView) class_info.findViewById(R.id.subjectList);
                ok = (Button) class_info.findViewById(R.id.doneButton);
                deleteClassButton = (Button) class_info.findViewById(R.id.delClassButton);
                addSectionButton = (Button) class_info.findViewById(R.id.addSubjectButton);
                addSectionButton.setText("Add Section");



                databaseReference=databaseReference.child(classname);




                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        HashMap<String, String> classsectionmap=(HashMap<String, String>)dataSnapshot.getValue();

                        if(classsectionmap==null){

                            Toast.makeText(getApplicationContext(),"No sections added",Toast.LENGTH_LONG).show();

                        }else{

                            Toast.makeText(getApplicationContext(),classsectionmap.size() + " sections found ",Toast.LENGTH_LONG).show();

                            classLt = new ArrayList<>();
                            for ( String sectionname : classsectionmap.keySet() ) {
                                System.out.println( sectionname );
                                sectionLt.add(sectionname);

                            }
                            ArrayAdapter adapter = new ArrayAdapter(Classes.this, android.R.layout.simple_list_item_1, sectionLt);
                            classSectionList.setAdapter(adapter);


                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                classSectionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String item = ((TextView) view).getText().toString();
                        sectioname=item;
                        sectionSelected(sectioname);

                    }
                });


                class_info.show();


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        class_info.dismiss();
                    }
                });


                deleteClassButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // deleteClassGrade(item);
                        class_info.dismiss();
                    }
                });

                addSectionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       // addSectionCall(item);

                    }
                });


            }
        });











    }




    protected void sectionSelected(final String section)
    {

        final Dialog classSection_details=new Dialog(Classes.this);
        classSection_details.setContentView(R.layout.class_details);
        setDialogSize(classSection_details);
        deleteSectionButton=(Button)classSection_details.findViewById(R.id.delClassButton);
        addSubjectButton=(Button)classSection_details.findViewById(R.id.addSubjectButton);
        dialog_heading=(TextView)classSection_details.findViewById(R.id.description);
        dialog_heading.setText("Subjects");
        addSubjectButton.setText("Add Subject");
        deleteSectionButton.setText("Delete Section");
        done=(Button)classSection_details.findViewById(R.id.doneButton);
        classSubjectList=(ListView)classSection_details.findViewById(R.id.subjectList);








        databaseReference=databaseReference.child(section);






        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                HashMap<String, String> classdetailsmap=(HashMap<String, String>)dataSnapshot.getValue();

                if(classdetailsmap==null){

                    Toast.makeText(getApplicationContext(),"No details added",Toast.LENGTH_LONG).show();

                }else{
                    ArrayList<String> subjectLt = new ArrayList<String>();

                    //Toast.makeText(getApplicationContext(),classdetailsmap.size() + " sections found ",Toast.LENGTH_LONG).show();

                    subjectLt = new ArrayList<>();
                    for ( String subjectkey : classdetailsmap.keySet() ) {
                        System.out.println(  subjectkey );
                        if(subjectkey!="id" || subjectkey!="teacher")
                        subjectLt.add( subjectkey + " by " + classdetailsmap.get(subjectkey));

                    }

                    ArrayAdapter subjectadapter = new ArrayAdapter(Classes.this, android.R.layout.simple_list_item_1, subjectLt);
                    classSubjectList.setAdapter(subjectadapter);

                    dialog_heading.setText("Subjects:");


                    classSection_details.show();

                }



            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /*    to be done
        deleteSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog confirm_delete = new Dialog(Classes.this);
                confirm_delete.setContentView(R.layout.confirm_message);
                confirm_message = (TextView) confirm_delete.findViewById(R.id.confirm_message);
                cancel = (Button) confirm_delete.findViewById(R.id.cancelButton);
                proceed = (Button) confirm_delete.findViewById(R.id.proceedButton);
                confirm_message.setText("All data related to  " + item + ",including students,attendance,uploads etc, will be deleted permanently!!");
                confirm_delete.show();
                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirm_delete.dismiss();
                        deleteClass(classSectionObject);
                        deleteStudent(classSectionObject);

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirm_delete.dismiss();
                    }
                });

            }
        });
        */



        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classSection_details.dismiss();
            }
        });

        addSubjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSubjectCall();
            }
        });


    }



    protected void addSubjectCall(){
        final Dialog newSubejectDialog=new Dialog(Classes.this);
        newSubejectDialog.setContentView(R.layout.new_subject);
        newSubject=(EditText)newSubejectDialog.findViewById(R.id.newsubject);
        subjectTeacherSpinner=(Spinner)newSubejectDialog.findViewById(R.id.subjectteacherselection);
        addSubject=(Button)newSubejectDialog.findViewById(R.id.addsubjectButton);
        ifclassteacher=(CheckBox)newSubejectDialog.findViewById(R.id.ifclassteacher);


        final HashMap<String,String> teacherusermap=new HashMap<>();

        final DatabaseReference dataReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName);


        dataReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> teachersmap=(HashMap<String, String>)dataSnapshot.getValue();

                if(teachersmap==null){

                    Toast.makeText(getApplicationContext(),"No teachers present in the institute",Toast.LENGTH_LONG).show();

                }else{

                    Toast.makeText(getApplicationContext(),teachersmap.size() + " teachers found ",Toast.LENGTH_LONG).show();




                    teacherLt = new ArrayList<>();
                    index=1;
                    for ( final String key : teachersmap.keySet() ) {
                        System.out.println( key );

                        databaseReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName).child(key);


                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                String teacher=(String) dataSnapshot.getValue();

                                databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(teacher);

                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        HashMap<String, String> teachermap=(HashMap<String, String>)dataSnapshot.getValue();
                                        teacherLt.add(index + teachermap.get("name"));
                                        teacherusermap.put(index + teachermap.get("name"),key);
                                        index++;

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });

                                Log.d("ta",teacher);

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }



                    ArrayAdapter teacheradapter = new ArrayAdapter(Classes.this, android.R.layout.simple_list_item_1, teacherLt);
                    subjectTeacherSpinner.setAdapter(teacheradapter);


                }







                addSubject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String subjectname = newSubject.getText().toString().trim();
                        String teacheruserkey=subjectTeacherSpinner.getSelectedItem().toString();



                        if(subjectname.equals("")) {

                            databaseReference = Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(classname).child(sectioname);
                            HashMap<String, Object> newsubjectmap = new HashMap<String, Object>();
                            newsubjectmap.put(subjectname, teacheruserkey);
                            databaseReference.updateChildren(newsubjectmap);


                            databaseReference= Constants.databaseReference.child(Constants.ALLOTMENTS_TABLE).child(institutionName).child(teacheruserkey).child(institutionName+"_"+classname+"_"+sectioname);
                            HashMap<String, Object> newallotmentmap = new HashMap<String, Object>();
                            newallotmentmap.put("subject", subjectname);

                            if(ifclassteacher.isChecked()) {

                                newallotmentmap.put("class_teacher", "1");

                            }else{
                                newallotmentmap.put("class_teacher", "0");
                            }
                            databaseReference.updateChildren(newallotmentmap);


                        }else{

                            Toast.makeText(getApplicationContext(),"enter complete details",Toast.LENGTH_LONG).show();

                        }


                    }
                });








            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

















    }



    /* to be done
    protected void deleteClassGrade(final String selectedClass)                 //incomplete code....class must be deleted from other tables too
    {
        final Dialog confirm_step=new Dialog(Classes.this);
        confirm_step.setContentView(R.layout.confirm_message);
        confirm_message=(TextView)confirm_step.findViewById(R.id.confirm_message);
        proceed=(Button)confirm_step.findViewById(R.id.proceedButton);
        cancel=(Button)confirm_step.findViewById(R.id.cancelButton);
        confirm_message.setText("All data related to class " + selectedClass + ",including sections,students,attendance,uploads etc, will be deleted permanently!!");
        //setDialogSize(confirm_step);

        confirm_step.show();

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                confirm_step.dismiss();

                ParseQuery<ParseObject> deleteClassGradeQuery = ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
                deleteClassGradeQuery.whereEqualTo(ClassGradeTable.CLASS_GRADE, selectedClass);
                deleteClassGradeQuery.whereEqualTo(ClassGradeTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                deleteClassGradeQuery.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> classgradeobjects, ParseException e) {
                        if (e == null) {
                            if (classgradeobjects.size() != 0) {
                                for (int x = 0; x < classgradeobjects.size(); x++) {


                                    deleteClass(classgradeobjects.get(x));
                                    deleteStudent(classgradeobjects.get(x));


                                    //class object deletion

                                    classgradeobjects.get(x).deleteEventually();//classgrade object deletion
                                    Toast.makeText(Classes.this,"Deletion completed",Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Log.d("classGrade", "error in query");
                            }
                        } else {
                            Log.d("classGrade", "error");
                        }
                    }
                });


            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirm_step.dismiss();
            }
        });


    }



    protected void deleteClass(ParseObject classGradeObject){
        ParseQuery<ParseObject> deleteClassQuery=ParseQuery.getQuery(ClassTable.TABLE_NAME);
        deleteClassQuery.whereEqualTo(ClassTable.CLASS_NAME, classGradeObject);
        deleteClassQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> classobjects, ParseException e) {
                if (e == null) {
                    if (classobjects.size() != 0) {
                        for (int x = 0; x < classobjects.size(); x++) {

                            deleteUpload(classobjects.get(x));          //uploads deletion related to class
                            deleteAttendance(classobjects.get(x));          //attendance deletion related to class
                            deleteExam(classobjects.get(x));            //exam deletions related to class

                            classobjects.get(x).deleteEventually(); //class object deletion
                        }


                    } else {
                        Log.d("class", "error in query");
                    }
                } else {
                    Log.d("class", "exception error in class deletion");
                }
            }
        });

    }


    protected void deleteUpload(ParseObject classObject){
        ParseQuery<ParseObject> deleteUploads=ParseQuery.getQuery(ImageUploadsTable.TABLE_NAME);
        deleteUploads.whereEqualTo(ImageUploadsTable.CLASS_REF, classObject);
        deleteUploads.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> uploadsobjects, ParseException e) {
                if (e == null) {
                    if (uploadsobjects.size() != 0) {
                        for (int x = 0; x < uploadsobjects.size(); x++) {
                            uploadsobjects.get(x).deleteEventually();
                        }
                    } else {
                        Log.d("uploads", "error in query");
                    }
                } else {
                    Log.d("uploads", "exception error in class deletion");
                }
            }
        });
    }


    protected void deleteAttendance(ParseObject classObject){
        ParseQuery<ParseObject> deleteAttendance=ParseQuery.getQuery(AttendanceDailyTable.TABLE_NAME);
        deleteAttendance.whereEqualTo(AttendanceDailyTable.FOR_CLASS,classObject);
        deleteAttendance.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> attendanceobjects, ParseException e) {
                if(e==null){
                    if(attendanceobjects.size()!=0){
                        for(int x=0;x<attendanceobjects.size();x++){
                            attendanceobjects.get(x).deleteEventually();
                        }
                    }else{
                        Log.d("attendance", "error in query");
                    }
                }else
                {
                    Log.d("attendance", "exception error in class deletion");
                }
            }
        });
    }

    protected void deleteExam(ParseObject classObject){
        ParseQuery<ParseObject> deleteExams=ParseQuery.getQuery(ExamTable.TABLE_NAME);
        deleteExams.whereEqualTo(ExamTable.FOR_CLASS,classObject);
        deleteExams.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> examobjects, ParseException e) {
                if(e==null){
                    if(examobjects.size()!=0){
                        for(int x=0;x<examobjects.size();x++){
                            examobjects.get(x).deleteEventually();
                        }
                    }else{
                        Log.d("exam", "error in query");
                    }
                }else
                {
                    Log.d("exam", "exception error in class deletion");
                }
            }
        });

    }



    protected void deleteStudent(ParseObject classGradeObject)
    {
        ParseQuery<ParseObject> deleteStudent=ParseQuery.getQuery(StudentTable.TABLE_NAME);
        deleteStudent.whereEqualTo(StudentTable.CLASS_REF,classGradeObject);
        deleteStudent.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> studentobjects, ParseException e) {
                if(e==null){
                    if(studentobjects.size()!=0){

                        ParseObject institution=ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code);

                        for(int x=0;x<studentobjects.size();x++){
                            deleteMarks(studentobjects.get(x));      //deletion marks objects related to student
                            deleteStudentRelatedData(studentobjects.get(x),institution);        //parent object,roles deletion

                            studentobjects.get(x).deleteEventually();
                            //parent object deletion
                            //role deletion
                            //student deletion
                        }

                    }else{
                        Log.d("student","error in query");
                    }

                }else{
                    Log.d("student","exception error in student query while deletion");
                }
            }
        });
    }



    protected void deleteMarks(ParseObject studentObject){

        ParseQuery<ParseObject> deleteMarks=ParseQuery.getQuery(MarksTable.TABLE_NAME);
        deleteMarks.whereEqualTo(MarksTable.STUDENT_USER_REF,studentObject);
        deleteMarks.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> marksobjects, ParseException e) {
                if(e==null){
                    if(marksobjects.size()!=0){
                        for(int x=0;x<marksobjects.size();x++){
                            marksobjects.get(x).deleteEventually();
                        }
                    }else{
                        Log.d("marks", "error in query");
                    }
                }else
                {
                    Log.d("marks", "exception error in class deletion");
                }
            }
        });

    }


    protected void deleteStudentRelatedData(ParseObject studentObject,ParseObject institution){
        ParseUser studentUser=studentObject.getParseUser(StudentTable.STUDENT_USER_REF);
        deleteParentData(studentUser, institution);          //deleting parent relationand role
        deleteStudentData(studentUser, institution);         //deleting student role
    }



    protected void deleteParentData(final ParseUser studentUser, final ParseObject institution)
    {
        ParseQuery<ParseObject> deleteParentRelation=ParseQuery.getQuery(ParentTable.TABLE_NAME);
        deleteParentRelation.whereEqualTo(ParentTable.CHILD_USER_REF,studentUser);
        deleteParentRelation.whereEqualTo(ParentTable.INSTITUTION, institution);
        deleteParentRelation.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parentrelationobjects, ParseException e) {
                if (e == null) {
                    if (parentrelationobjects.size() != 0) {
                        for (int x = 0; x < parentrelationobjects.size(); x++) {
                            ParseUser u = parentrelationobjects.get(x).getParseUser(ParentTable.PARENT_USER_REF);

                            deleteParentRole(u,institution);                    //parent role deletion
                            parentrelationobjects.get(x).deleteEventually();        //deleting relation
                        }
                    } else {
                        Log.d("parent", "error in query");
                    }
                } else {
                    Log.d("parent", "exception error in class deletion");
                }
            }
        });
    }




    protected void deleteStudentData(ParseUser studentUser,ParseObject institution)
    {
        ParseQuery<ParseObject> deleteStudentRole=ParseQuery.getQuery(RoleTable.TABLE_NAME);
        deleteStudentRole.whereEqualTo(RoleTable.OF_USER_REF,studentUser);
        deleteStudentRole.whereEqualTo(RoleTable.ENROLLED_WITH,institution);
        deleteStudentRole.whereEqualTo(RoleTable.ROLE,"Student");
        deleteStudentRole.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> roleobjects, ParseException e) {
                if(e==null){
                    if(roleobjects.size()!=0){
                        for(int x=0;x<roleobjects.size();x++){
                            roleobjects.get(x).deleteEventually();
                        }
                    }else{
                        Log.d("parent", "error in query");
                    }
                }else
                {
                    Log.d("parent", "exception error in class deletion");
                }
            }
        });
    }



    protected void deleteParentRole(ParseUser parentUser,ParseObject institution){
        ParseQuery<ParseObject> deleteParentRole=ParseQuery.getQuery(RoleTable.TABLE_NAME);
        deleteParentRole.whereEqualTo(RoleTable.OF_USER_REF,parentUser);
        deleteParentRole.whereEqualTo(RoleTable.ROLE,"Parent");
        deleteParentRole.whereEqualTo(RoleTable.ENROLLED_WITH,institution);
        deleteParentRole.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> roleobjects, ParseException e) {
                if(e==null){
                    if(roleobjects.size()!=0){
                        for(int x=0;x<roleobjects.size();x++){
                            roleobjects.get(x).deleteEventually();
                        }
                    }else{
                        Log.d("role", "error in query");
                    }
                }else
                {
                    Log.d("role", "exception error in class deletion");
                }
            }
        });
    }


    */


    /*

    protected  void addSectionCall(final String selectedClass)
    {
        final Dialog newSection=new Dialog(Classes.this);
        newSection.setContentView(R.layout.new_section);
        newsectionclassteacher=(Spinner)newSection.findViewById(R.id.sectionclassteacherselection);
        done=(Button)newSection.findViewById(R.id.addsectionButton);
        getNewSection=(EditText)newSection.findViewById(R.id.newsectionname);
        classTeacherSubject=(EditText)newSection.findViewById(R.id.classteachersubject);

        ArrayList<String> teacherLt = new TeacherTable().getAllTeachersWithSerial(institutionName);

        if(teacherLt==null){

            Toast.makeText(Classes.this, "no teacher is added in this institution", Toast.LENGTH_LONG).show();

        }else{

            ArrayAdapter teacheradapter = new ArrayAdapter(Classes.this, android.R.layout.simple_spinner_item, teacherLt);
            newsectionclassteacher.setAdapter(teacheradapter);
            newSection.show();


        }




        final HashMap<String,String> teacherMap=new HashMap<String,String>();
        ParseQuery teacherListQuery=ParseQuery.getQuery(TeacherTable.TABLE_NAME);
        teacherListQuery.whereEqualTo(TeacherTable.INSTITUTION, ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
        teacherListQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> teacherListRet, ParseException e) {
                if (e == null) {
                    if (teacherListRet.size() != 0) {

                        ArrayList<String> teacherLt = new ArrayList<String>();
                        ArrayAdapter teacheradapter = new ArrayAdapter(Classes.this, android.R.layout.simple_spinner_item, teacherLt);
                        teacheradapter.add("");
                        //Toast.makeText(Students.this, "here = ", Toast.LENGTH_LONG).show();
                        for (int x = 0; x < teacherListRet.size(); x++) {
                            ParseObject teacher = teacherListRet.get(x);
                            String teacher_name = teacher.getString(TeacherTable.TEACHER_NAME);
                            teacheradapter.add(teacher_name);
                            teacherMap.put(teacher_name,teacher.getObjectId());
                        }

                        newsectionclassteacher.setAdapter(teacheradapter);
                        newSection.show();


                    } else {
                        Toast.makeText(Classes.this, "no teacher is added in this institution", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Log.d("teachers", "error");
                }
            }

        });











        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] itemValues = newsectionclassteacher.getSelectedItem().toString().split("\\. ");
                String selectedTeacher=itemValues[1];
                String teacherserial=itemValues[0];
               // final String selectedTeacher=newsectionclassteacher.getSelectedItem().toString();
                if( (getNewSection.getText().toString().equals("")) || (newsectionclassteacher.getSelectedItem().equals("")) || (classTeacherSubject.getText().equals("")) )
                {
                    Toast.makeText(Classes.this,"Information incomplete",Toast.LENGTH_LONG).show();
                }else
                {
                    String sectionname=getNewSection.getText().toString();
                    databaseReference=Constants.databaseReference.child(Constants.INSTITUTION_TABLE).child(classname);

                    if(databaseReference.child(sectionname)==null){

                        databaseReference.child(sectionname).child("id").setValue(institutionName + "_" + classname + "_" + sectionname);
                        String teacheruserid=new TeacherTable().getTeacherWithNameAndSerial(institutionName,teacherserial);


                    }else{
                        Toast.makeText(Classes.this,"Section already added. Type Another section name",Toast.LENGTH_LONG).show();

                    }



                    ParseQuery<ParseObject> newSectionQuery=ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
                    newSectionQuery.whereEqualTo(ClassGradeTable.CLASS_GRADE,selectedClass);
                    newSectionQuery.whereEqualTo(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                    newSectionQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> sectionsList, ParseException e) {
                            if(e==null)
                            {
                                Log.d("classSection",sectionsList.size() + " Sections");
                                int flag=0;
                                if(sectionsList.size()!=0)
                                {
                                    for(int x=0;x<sectionsList.size();x++)
                                    {
                                        if((sectionsList.get(x).getString(ClassGradeTable.SECTION)).equalsIgnoreCase(getNewSection.getText().toString())){
                                            Toast.makeText(Classes.this,"Section already added. Type Another section name",Toast.LENGTH_LONG).show();
                                            flag=1;
                                            break;
                                        }
                                    }
                                    if(flag==0)
                                    {

                                        final ParseObject newSectionObject=new ParseObject(ClassGradeTable.TABLE_NAME);
                                        newSectionObject.put(ClassGradeTable.CLASS_GRADE,selectedClass);
                                        newSectionObject.put(ClassGradeTable.SECTION,getNewSection.getText().toString());
                                        newSectionObject.put(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                                        newSectionObject.saveEventually(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {


                                                ParseObject newClass=new ParseObject(ClassTable.TABLE_NAME);
                                                newClass.put(ClassTable.SUBJECT, classTeacherSubject.getText().toString());
                                                newClass.put(ClassTable.IF_CLASS_TEACHER, true);
                                                newClass.put(ClassTable.CLASS_NAME, newSectionObject);
                                                String selectedteacherObjectId=teacherMap.get(selectedTeacher);
                                                ParseUser  teacheruser=(ParseUser)(ParseObject.createWithoutData(TeacherTable.TABLE_NAME, selectedteacherObjectId)).get(TeacherTable.TEACHER_USER_REF);
                                                newClass.put(ClassTable.TEACHER_USER_REF, teacheruser);
                                                newClass.saveEventually();




                                            }
                                        });
                                        newSection.dismiss();
                                    }
                                }else
                                {
                                    Log.d("classSection","error in query logic");
                                }
                            }else
                            {
                                Log.d("classSection","error");
                            }
                        }
                    });



                }
            }
        });             //end os on click of done button while adding new section







        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String selectedTeacher=newsectionclassteacher.getSelectedItem().toString();
                if( (getNewSection.getText().toString().equals("")) || (newsectionclassteacher.getSelectedItem().equals("")) || (classTeacherSubject.getText().equals("")) )
                {
                    Toast.makeText(Classes.this,"Information incomplete",Toast.LENGTH_LONG).show();
                }else
                {


                    ParseQuery<ParseObject> newSectionQuery=ParseQuery.getQuery(ClassGradeTable.TABLE_NAME);
                    newSectionQuery.whereEqualTo(ClassGradeTable.CLASS_GRADE,selectedClass);
                    newSectionQuery.whereEqualTo(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME,institution_code));
                    newSectionQuery.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> sectionsList, ParseException e) {
                            if(e==null)
                            {
                                Log.d("classSection",sectionsList.size() + " Sections");
                                int flag=0;
                                if(sectionsList.size()!=0)
                                {
                                    for(int x=0;x<sectionsList.size();x++)
                                    {
                                        if((sectionsList.get(x).getString(ClassGradeTable.SECTION)).equalsIgnoreCase(getNewSection.getText().toString())){
                                            Toast.makeText(Classes.this,"Section already added. Type Another section name",Toast.LENGTH_LONG).show();
                                            flag=1;
                                            break;
                                        }
                                    }
                                    if(flag==0)
                                    {

                                        final ParseObject newSectionObject=new ParseObject(ClassGradeTable.TABLE_NAME);
                                        newSectionObject.put(ClassGradeTable.CLASS_GRADE,selectedClass);
                                        newSectionObject.put(ClassGradeTable.SECTION,getNewSection.getText().toString());
                                        newSectionObject.put(ClassGradeTable.INSTITUTION,ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code));
                                        newSectionObject.saveEventually(new SaveCallback() {
                                            @Override
                                            public void done(ParseException e) {


                                                ParseObject newClass=new ParseObject(ClassTable.TABLE_NAME);
                                                newClass.put(ClassTable.SUBJECT, classTeacherSubject.getText().toString());
                                                newClass.put(ClassTable.IF_CLASS_TEACHER, true);
                                                newClass.put(ClassTable.CLASS_NAME, newSectionObject);
                                                String selectedteacherObjectId=teacherMap.get(selectedTeacher);
                                                ParseUser  teacheruser=(ParseUser)(ParseObject.createWithoutData(TeacherTable.TABLE_NAME, selectedteacherObjectId)).get(TeacherTable.TEACHER_USER_REF);
                                                newClass.put(ClassTable.TEACHER_USER_REF, teacheruser);
                                                newClass.saveEventually();




                                            }
                                        });
                                        newSection.dismiss();
                                    }
                                }else
                                {
                                    Log.d("classSection","error in query logic");
                                }
                            }else
                            {
                                Log.d("classSection","error");
                            }
                        }
                    });



                }
            }
        });             //end os on click of done button while adding new section

    }*/


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(Classes.this,LoginActivity.class);
            startActivity(nouser);
        }
    }


    

}
