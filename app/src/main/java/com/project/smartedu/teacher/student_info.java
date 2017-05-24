package com.project.smartedu.teacher;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.project.smartedu.Constants;
import com.project.smartedu.R;

import java.util.List;



public class student_info extends Fragment{

    TextView studentName;
    TextView studentEmail;
    TextView studentParentEmail;
    TextView studentDob;
    TextView studentContact;
    TextView studentAddress;

    Button deleteStudent;
    String studentId;
    String classId;
    String userid;

    String institutionName;
    TextView confirm_message;
    Button cancel;
    Button proceed;

    DatabaseReference databaseReference;


    TeacherUserPrefs teacherUserPrefs;








    private class StudentDetailsItem extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public StudentDetailsItem(Context context){
            this.async_context = context;

            pd = new ProgressDialog(async_context);
            pd.setMessage("Loading Data...");
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
            databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(studentId);

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {
                            if(ds.getKey().equals("name")) {

                                TeacherUserPrefs.studentName=ds.getValue().toString();

                            }

                            if(ds.getKey().equals("dob")) {
                                TeacherUserPrefs.studentDob=ds.getValue().toString();

                            }



                            if(ds.getKey().equals("address")) {
                                TeacherUserPrefs.studentAddress=ds.getValue().toString();

                            }

                            if(ds.getKey().equals("contact")) {
                                TeacherUserPrefs.studentContact=ds.getValue().toString();

                            }



                            if(ds.getKey().equals("parent_email")) {
                                TeacherUserPrefs.studentParentEmail=ds.getValue().toString();

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

            teacherUserPrefs.setFirstInfoLoading(false);
setDetails();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    //  Toast.makeText(async_context,userPrefs.getUserName(),Toast.LENGTH_LONG).show();


                    pd.dismiss();

                }
            }, 500);  // 100 milliseconds


        }
        //end firebase_async_class
    }









    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View android = inflater.inflate(R.layout.fragment_student_info, container, false);
        studentId= getArguments().getString("id");
        classId= getArguments().getString("classId");
        institutionName=getArguments().getString("institution_node");
        studentName=(TextView)android.findViewById(R.id.student_name);
   //     studentEmail=(TextView)android.findViewById(R.id.student_email);
        studentParentEmail=(TextView)android.findViewById(R.id.student_parentemail);
        studentDob=(TextView)android.findViewById(R.id.student_dob);
        studentContact=(TextView)android.findViewById(R.id.student_contact);
        studentAddress=(TextView)android.findViewById(R.id.student_address);

        deleteStudent=(Button)android.findViewById(R.id.delete_student);


        teacherUserPrefs=new TeacherUserPrefs(getContext());
        com.project.smartedu.database.Students student=TeacherUserPrefs.studentsHashMap.get(studentId);


        if(teacherUserPrefs.getFirstInfoLoading()) {
            StudentDetailsItem studentDetailsItem = new StudentDetailsItem(getContext());     //get upload data
            studentDetailsItem.execute();
        }else{

            setDetails();
        }




        deleteStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                final Dialog confirm_step=new Dialog(getActivity());
                confirm_step.setContentView(R.layout.confirm_message);
                confirm_message=(TextView)confirm_step.findViewById(R.id.confirm_message);
                proceed=(Button)confirm_step.findViewById(R.id.proceedButton);
                cancel=(Button)confirm_step.findViewById(R.id.cancelButton);
                confirm_message.setText("All data related to this student including attendance,marks etc, will be deleted permanently!!");


                confirm_step.show();

                proceed.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                      /*  ParseObject institution = ParseObject.createWithoutData(InstitutionTable.TABLE_NAME, institution_code);
                        ParseObject studentObject = ParseObject.createWithoutData(StudentTable.TABLE_NAME, studentId);
                        ParseUser studentUser = studentObject.getParseUser(StudentTable.STUDENT_USER_REF);
                        deleteParentData(institution, studentUser);
                        deleteStudentData(institution, studentUser, studentObject);*/

                        confirm_step.dismiss();
                        Intent to_student = new Intent(getActivity(), Students.class);
                        to_student.putExtra("institution_name", institutionName);
                        to_student.putExtra("id", classId);

                        startActivity(to_student);

                    }
                });


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        confirm_step.dismiss();
                    }
                });








            }
        });
        return android;
    }


    public void setDetails(){
        studentName.setText( TeacherUserPrefs.studentName);
        studentDob.setText(TeacherUserPrefs.studentDob);
        studentAddress.setText(  TeacherUserPrefs.studentAddress);
        studentContact.setText( TeacherUserPrefs.studentContact);
        studentParentEmail.setText( TeacherUserPrefs.studentParentEmail);

    }

   /* protected void deleteParentData(final ParseObject institution,ParseUser studentUser)
    {

        ParseQuery<ParseObject> parentrelationQuery = ParseQuery.getQuery(ParentTable.TABLE_NAME);
        parentrelationQuery.whereEqualTo(ParentTable.CHILD_USER_REF, studentUser);
        parentrelationQuery.whereEqualTo(ParentTable.INSTITUTION, institution);
        parentrelationQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> parentReltionListRet, ParseException e) {
                if (e == null) {
                    if (parentReltionListRet.size() != 0) {
                        ParseUser parent_user = parentReltionListRet.get(0).getParseUser(ParentTable.PARENT_USER_REF);
                        deleteParentRole(parent_user, institution);
                        parentReltionListRet.get(0).deleteEventually();
                        Toast.makeText(getActivity(), "error deleted parent", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "error deleting parent info", Toast.LENGTH_LONG).show();
                        Log.d("parent relation", "error in query");
                    }
                } else {
                    Toast.makeText(getActivity(), "error deleting parent info", Toast.LENGTH_LONG).show();
                    Log.d("parent relation", "Error: " + e.getMessage());
                }
            }
        });



    }*/






    /*protected void deleteParentRole(ParseUser parentUser,ParseObject institution){
        ParseQuery<ParseObject> deleteParentRole=ParseQuery.getQuery(RoleTable.TABLE_NAME);
        deleteParentRole.whereEqualTo(RoleTable.OF_USER_REF,parentUser);
        deleteParentRole.whereEqualTo(RoleTable.ROLE,"Parent");
        deleteParentRole.whereEqualTo(RoleTable.ENROLLED_WITH, institution);
        deleteParentRole.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> roleobjects, ParseException e) {
                if (e == null) {
                    if (roleobjects.size() != 0) {
                        for (int x = 0; x < roleobjects.size(); x++) {
                            roleobjects.get(x).deleteEventually();
                        }
                    } else {
                        Log.d("role", "error in query");
                        Toast.makeText(getActivity(), "error deleting parent role", Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(getActivity(), "error deleting parent role", Toast.LENGTH_LONG).show();
                    Log.d("role", "exception error in class deletion");
                }
            }
        });
    }




    protected void deleteStudentData(ParseObject institution,ParseUser studentUser,ParseObject studentObject)
    {
        ParseQuery<ParseObject> deleteAttendanceQuery = ParseQuery.getQuery(AttendanceDailyTable.TABLE_NAME);
        deleteAttendanceQuery.whereEqualTo(AttendanceDailyTable.STUDENT_USER_REF, studentObject);
        deleteAttendanceQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> attendanceListRet, ParseException e) {
                if (e == null) {
                    if (attendanceListRet.size() != 0) {
                        for(int x=0;x<attendanceListRet.size();x++) {
                            attendanceListRet.get(x).deleteEventually();
                        }
                        Toast.makeText(getActivity(), "deleted student attendance", Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getActivity(), "error deleting attendance", Toast.LENGTH_LONG).show();

                        Log.d("attendance", "error in query");
                    }
                } else {
                    Toast.makeText(getActivity(), "error deleting attendance", Toast.LENGTH_LONG).show();

                    Log.d("attendance", "Exceptional error");
                }
            }
        });





        ParseQuery<ParseObject> deleteMarksQuery = ParseQuery.getQuery(MarksTable.TABLE_NAME);
        deleteMarksQuery.whereEqualTo(MarksTable.STUDENT_USER_REF, studentUser);
        deleteMarksQuery.findInBackground(new FindCallback<ParseObject>()

                                          {
                                              public void done(List<ParseObject> marksListRet, ParseException e) {
                                                  if (e == null) {
                                                      if (marksListRet.size() != 0) {
                                                          for (int i = 0; i < marksListRet.size(); i++) {
                                                              marksListRet.get(i).deleteEventually();
                                                          }
                                                          Toast.makeText(getActivity(), "Marks deleted", Toast.LENGTH_LONG).show();
                                                      } else {
                                                          Toast.makeText(getActivity(), "error deleting marks", Toast.LENGTH_LONG).show();
                                                          Log.d("marks", "error in query");
                                                      }
                                                  } else {
                                                      Toast.makeText(getActivity(), "error deleting marks", Toast.LENGTH_LONG).show();
                                                      Log.d("marks", "Error: " + e.getMessage());
                                                  }
                                              }
                                          }

        );



        ParseQuery<ParseObject> roleQuery = ParseQuery.getQuery(RoleTable.TABLE_NAME);
        roleQuery.whereEqualTo(RoleTable.OF_USER_REF,studentUser);
        //roleQuery.whereEqualTo("createdBy",ParseUser.createWithoutData("User",userid));
        roleQuery.whereEqualTo(RoleTable.ROLE, "Student");
        roleQuery.whereEqualTo(RoleTable.ENROLLED_WITH, institution);
        roleQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> roleListRet, ParseException e) {
                if (e == null) {
                    if (roleListRet.size() != 0) {
                        roleListRet.get(0).deleteEventually();
                        Log.d("role", "Student Deleted from roles");
                    } else {
                        Toast.makeText(getActivity(), "error in deleting student role", Toast.LENGTH_LONG).show();
                        Log.d("role", "error in query");
                    }
                } else {
                    Toast.makeText(getActivity(), "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });


        studentObject.deleteEventually();
    }
*/
  /*  @Override
    public void onDrawerItemSelected(View view, int position) {

    }*/

}


