package com.project.smartedu.admin;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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

import com.google.firebase.database.DatabaseReference;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.LoginActivity;
import com.project.smartedu.R;
import com.project.smartedu.database.Allotments;
import com.project.smartedu.database.Class;
import com.project.smartedu.navigation.FragmentDrawer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class Classes extends BaseActivity{


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


    HashMap<String,ArrayList<String>> classtosectionmap;
    DatabaseReference databaseReference;
    Class selectedclass;

    private FragmentDrawer drawerFragment;

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

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), toolbar,role);
        drawerFragment.setDrawerListener(this);


        classList = (ListView) findViewById(R.id.classList);

        classLt = new ArrayList<>();

        classtosectionmap=new HashMap<>();    //class to section mao

        if(AdminUserPrefs.classes.size()==0){

            Toast.makeText(getApplication(),"No classes added",Toast.LENGTH_LONG).show();

        }else{


            HashSet<String> classes=new HashSet<>();

            for(int x=0;x<AdminUserPrefs.classes.size();x++){
                Class cls=AdminUserPrefs.classes.get(x);
                String[] classkeydecode=cls.getClassid().split("_");
                String clsname=classkeydecode[1];
                String section=classkeydecode[2];
                classes.add(clsname);

                if(classtosectionmap.containsKey(clsname)){

                    ArrayList<String> tempsectionlist=classtosectionmap.get(clsname);
                    tempsectionlist.add(section);
                    classtosectionmap.put(clsname,tempsectionlist);
                 //   Toast.makeText(getApplicationContext(),tempsectionlist.size() + " sections in "+ clsname,Toast.LENGTH_LONG ).show();

                }else{
                    ArrayList<String> tempsectionlist=new ArrayList<>();
                    tempsectionlist.add(section);
                    classtosectionmap.put(clsname,tempsectionlist);
                }

            }


            for(String cls:classes){
                classLt.add(cls);
            }

            ArrayAdapter adapter = new ArrayAdapter(Classes.this, android.R.layout.simple_list_item_1, classLt);
            classList.setAdapter(adapter);


        }










        classList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String item = ((TextView) view).getText().toString();
                Log.d("class", item);

                classname=item;

                showSections(classname);


            }
        });




    }


    protected void showSections(final String classSelected){

        final Dialog class_info = new Dialog(Classes.this);
        class_info.setContentView(R.layout.class_details);
        class_info.setTitle(classSelected);

        setDialogSize(class_info);
        dialog_heading = (TextView) class_info.findViewById(R.id.description);
        dialog_heading.setText("Sections");
        classSectionList = (ListView) class_info.findViewById(R.id.subjectList);
        ok = (Button) class_info.findViewById(R.id.doneButton);

        addSectionButton = (Button) class_info.findViewById(R.id.addSubjectButton);
        addSectionButton.setText("Add Section");


        sectionLt = classtosectionmap.get(classname);
     //   Toast.makeText(getApplicationContext(),sectionLt.size() + " sections found ",Toast.LENGTH_LONG).show();

        ArrayAdapter adapter = new ArrayAdapter(Classes.this, android.R.layout.simple_list_item_1, sectionLt);
        classSectionList.setAdapter(adapter);

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




        addSectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSectionCall(classSelected);

            }
        });



    }



    protected void sectionSelected(final String section)
    {

        final Dialog classSection_details=new Dialog(Classes.this);
        classSection_details.setContentView(R.layout.class_details);
        setDialogSize(classSection_details);
        classSection_details.setTitle(classname+ " " +sectioname);
        addSubjectButton=(Button)classSection_details.findViewById(R.id.addSubjectButton);
        dialog_heading=(TextView)classSection_details.findViewById(R.id.description);
        dialog_heading.setText("Subjects");
        addSubjectButton.setText("Add Subject");

        done=(Button)classSection_details.findViewById(R.id.doneButton);
        classSubjectList=(ListView)classSection_details.findViewById(R.id.subjectList);








        //  databaseReference=databaseReference.child(section);


        String selectedclassid=institutionName+"_"+classname+"_"+sectioname;

        for(int x=0;x<AdminUserPrefs.classes.size();x++){

            if(AdminUserPrefs.classes.get(x).getClassid().equals(selectedclassid)){
                selectedclass=AdminUserPrefs.classes.get(x);
                break;
            }

        }


        ArrayList<String> subjectLt = new ArrayList<String>();

        //Toast.makeText(getApplicationContext(),classdetailsmap.size() + " sections found ",Toast.LENGTH_LONG).show();

        subjectLt = new ArrayList<>();

        for ( String subjectentry : selectedclass.getSubjects().keySet() ) {
            //System.out.println(  subjectkey );
            String teacherid= selectedclass.getSubjects().get(subjectentry);

            String teacher=AdminUserPrefs.teachersuserreversemap.get(teacherid);

            String[] teacheritems=teacher.split("\\. ");
            String teacher_serial=teacheritems[0];
            String teacher_name=teacheritems[1];

            subjectLt.add( subjectentry + " by " + teacher_name + " ( id = " + teacher_serial + " ) ");

        }

        ArrayAdapter subjectadapter = new ArrayAdapter(Classes.this, android.R.layout.simple_list_item_1, subjectLt);
        classSubjectList.setAdapter(subjectadapter);

        dialog_heading.setText("Subjects:");


        classSection_details.show();










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


        //final HashMap<String,String> teacherusermap=new HashMap<>();

        //final DatabaseReference dataReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName);


        if(AdminUserPrefs.teacherLt.size()==0){

            Toast.makeText(getApplicationContext(),"Add teachers first",Toast.LENGTH_LONG).show();

        }else {


            final ArrayAdapter teacheradapter = new ArrayAdapter(Classes.this, android.R.layout.simple_list_item_1, AdminUserPrefs.teacherLt);
            subjectTeacherSpinner.setAdapter(teacheradapter);
            newSubejectDialog.show();
            addSubject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String subjectname = newSubject.getText().toString().trim();
                    String teacheruserkey=AdminUserPrefs.teachersusermap.get( subjectTeacherSpinner.getSelectedItem().toString());



                    if(!subjectname.equals("")) {

                        //adding data to class databse
                        databaseReference = Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(classname).child(sectioname);
                        HashMap<String, Object> newsubjectmap = new HashMap<String, Object>();
                        newsubjectmap.put(subjectname, teacheruserkey);
                        databaseReference.child("subject").updateChildren(newsubjectmap);       //server change

                        HashMap<String, Object> newteachermap = new HashMap<String, Object>();
                        if(ifclassteacher.isChecked()){
                            newteachermap.put(teacheruserkey, "1");
                            databaseReference.child("teacher").updateChildren(newteachermap);       //server change
                        }else{
                            newteachermap.put(teacheruserkey,"0");
                            databaseReference.child("teacher").updateChildren(newteachermap);
                        }



                        //add ddata to classes
                        int classindex=0;
                        for(int x=0;x<AdminUserPrefs.classes.size();x++){
                            Class c=AdminUserPrefs.classes.get(x);
                            if(c.getClassid().equals(selectedclass.getClassid())){

                                AdminUserPrefs.classes.get(x).getSubjects().put(subjectname,teacheruserkey);
                                if(ifclassteacher.isChecked()) {
                                    AdminUserPrefs.classes.get(x).getTeachers().put(teacheruserkey, "1");
                                }else{
                                    AdminUserPrefs.classes.get(x).getTeachers().put(teacheruserkey, "0");
                                }

                            }
                        }


                        //add data to allotments
                        boolean altalreadypresent=false;
                        boolean teacheralreadypresent=false;
                        Allotments altifonlyteachpresent=new Allotments();
                        Allotments altifbothpresent=new Allotments();

                        int index=0;
                        for(int x=0;x<AdminUserPrefs.allotmments.size();x++){       //check if enrtry is already present
                            Allotments altmt=AdminUserPrefs.allotmments.get(x);
                            teacheralreadypresent=false;
                            if(altmt.getTeacherid().equals(teacheruserkey)) {
                                teacheralreadypresent=true;
                                index=x;

                                altifonlyteachpresent=altmt;
                                HashMap<String, String> mp = altmt.getAllots();

                                for (String clsid : mp.keySet()) {

                                    if (mp.get(clsid).equals(institutionName+"_"+classname+"_"+sectioname)){
                                        altalreadypresent=true;
                                        altifbothpresent=altmt;

                                        index=x;
                                        break;
                                    }

                                }
                            }

                            if(teacheralreadypresent){
                                break;
                            }

                        }



                        if( (!altalreadypresent)  ) {
                            //server cahnges
                            databaseReference = Constants.databaseReference.child(Constants.ALLOTMENTS_TABLE).child(institutionName).child(teacheruserkey).push();
                            databaseReference.setValue(selectedclass.getClassid());

                            //local changes

                            if(teacheralreadypresent){


                                AdminUserPrefs.allotmments.get(index).getAllots().put(databaseReference.getKey(),selectedclass.getClassid());

                                //    Toast.makeText(getApplicationContext(),"t present",Toast.LENGTH_LONG).show();


                            }else {
                               // Toast.makeText(getApplicationContext(),"t not present",Toast.LENGTH_LONG).show();
                                HashMap<String, String> allotmap = new HashMap<String, String>();
                                allotmap.put(databaseReference.getKey(), selectedclass.getClassid());
                                Allotments newAllot = new Allotments(teacheruserkey, allotmap);
                                //  Toast.makeText(getApplicationContext(),"sp = "+AdminUserPrefs.allotmments.size(),Toast.LENGTH_LONG).show();
                                AdminUserPrefs.allotmments.add(newAllot);
                                //  Toast.makeText(getApplicationContext(),"sn="+AdminUserPrefs.allotmments.size(),Toast.LENGTH_LONG).show();


                            }

                        }else{      //only local changes required

                            if(teacheralreadypresent) {
                                //nothing as both are present
                           //     Toast.makeText(getApplicationContext(),"Already present",Toast.LENGTH_LONG).show();
                            }






                        }




                        newSubejectDialog.dismiss();
                        for(int x=0;x<AdminUserPrefs.allotmments.size();x++){
                            Log.d("altteacher",AdminUserPrefs.allotmments.get(x).getTeacherid());
                            for(String s:AdminUserPrefs.allotmments.get(x).getAllots().keySet()){
                                Log.d("alt",s+ " " + AdminUserPrefs.allotmments.get(x).getAllots().get(s));

                            }
                        }
                        newSubejectDialog.dismiss();
                        sectionSelected(sectioname);

                    }else{

                        Toast.makeText(getApplicationContext(),"enter complete details",Toast.LENGTH_LONG).show();
                        //  newSubejectDialog.dismiss();

                    }


                }
            });



        }



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




    protected  void addSectionCall(final String selectedClass)
    {
        final Dialog newSection=new Dialog(Classes.this);
        newSection.setContentView(R.layout.new_section);
        newsectionclassteacher=(Spinner)newSection.findViewById(R.id.sectionclassteacherselection);
        done=(Button)newSection.findViewById(R.id.addsectionButton);
        getNewSection=(EditText)newSection.findViewById(R.id.newsectionname);
        classTeacherSubject=(EditText)newSection.findViewById(R.id.classteachersubject);

        ArrayList<String> teacherLt = AdminUserPrefs.teacherLt;

        if(teacherLt.size()==0){

            Toast.makeText(Classes.this, "no teacher is added in this institution", Toast.LENGTH_LONG).show();
            newSection.dismiss();

        }else{

            ArrayAdapter teacheradapter = new ArrayAdapter(Classes.this, android.R.layout.simple_spinner_item, teacherLt);
            newsectionclassteacher.setAdapter(teacheradapter);
            newSection.show();


            done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String[] itemValues = newsectionclassteacher.getSelectedItem().toString().split("\\. ");
                    String selectedTeacher=itemValues[1];
                    String teacherserial=itemValues[0];
                    String sectionname=getNewSection.getText().toString();
                    ArrayList<String> secLt=classtosectionmap.get(selectedClass);

                    // final String selectedTeacher=newsectionclassteacher.getSelectedItem().toString();
                    if( (getNewSection.getText().toString().equals("")) || (newsectionclassteacher.getSelectedItem().equals("")) || (classTeacherSubject.getText().equals("")) )
                    {
                        Toast.makeText(Classes.this,"Information incomplete",Toast.LENGTH_LONG).show();
                    }else if(secLt.contains(sectionname)){//check if section already exists

                        Toast.makeText(Classes.this,"Section already exists",Toast.LENGTH_LONG).show();

                    }else
                    {



                        databaseReference=Constants.databaseReference.child(Constants.CLASS_TABLE).child(institutionName).child(classname).child(sectionname);


                        databaseReference.child("id").setValue(institutionName + "_" + classname + "_" + sectionname);      //changes to server in class table
                        String teacheruserid=AdminUserPrefs.teachersusermap.get(newsectionclassteacher.getSelectedItem().toString());

                        databaseReference.child("subject").child(classTeacherSubject.getText().toString()).setValue(teacheruserid);
                        databaseReference.child("teacher").child(teacheruserid).setValue("1");

                        HashMap<String,String> newsectionsubjectmap=new HashMap<String, String>();      //local changes
                        HashMap<String,String> newsectionteachermap=new HashMap<String, String>();
                        newsectionsubjectmap.put(classTeacherSubject.getText().toString(),teacheruserid);
                        newsectionteachermap.put(teacheruserid,"1");
                        Class cls=new Class(institutionName + "_" + classname + "_" + sectionname,newsectionsubjectmap,newsectionteachermap);
                        AdminUserPrefs.classes.add(cls);

                        secLt.add(sectionname);
                        classtosectionmap.put(selectedClass,secLt);     //replacing section list with new one




                        databaseReference=Constants.databaseReference.child(Constants.ALLOTMENTS_TABLE).child(institutionName).child(teacheruserid).push();        //changes to server in allotments table
                        databaseReference.setValue(institutionName + "_" + classname + "_" + sectionname);

                        boolean found=false;

                        for(int x=0;x<AdminUserPrefs.allotmments.size();x++){
                            Allotments alt=AdminUserPrefs.allotmments.get(x);
                            if(alt.getTeacherid().equals(teacheruserid)){
                                index=x;
                                found=true;
                                break;
                            }
                        }

                        if(found){
                            Allotments alt=AdminUserPrefs.allotmments.get(index);           //local changes
                            alt.getAllots().put(databaseReference.getKey(),institutionName + "_" + classname + "_" + sectionname);
                            AdminUserPrefs.allotmments.remove(index);
                            AdminUserPrefs.allotmments.add(alt);
                        }else{
                            HashMap<String,String> newaltmap=new HashMap<String, String>();
                            newaltmap.put(databaseReference.getKey(),institutionName + "_" + classname + "_" + sectionname);
                            Allotments newalt=new Allotments(teacheruserid,newaltmap);
                            AdminUserPrefs.allotmments.add(newalt);
                        }


                        newSection.dismiss();
                        showSections(classname);

                    }
                }
            });             //end os on click of done button while adding new section







        }



    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
        if(firebaseAuth.getCurrentUser()==null)
        {
            Intent nouser=new Intent(Classes.this,LoginActivity.class);
            startActivity(nouser);
        }
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent tohome=new Intent(Classes.this,Home.class);
        tohome.putExtra("institution_name",institutionName);
        startActivity(tohome);
    }


}
