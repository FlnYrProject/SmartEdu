package com.project.smartedu.database;

import android.os.AsyncTask;
import android.os.Handler;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.Constants;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shubham Bhasin on 13-Feb-17.
 */

public class TeacherTable {


    String operation;

    String teacherserial;
    String teacheruserid;
    String teachername;


    DatabaseReference databaseReference;

    ArrayList<String> teacherLt;



    public ArrayList<String> getAllTeachersWithSerial(final String institutionName){


        databaseReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> teachersmap=(HashMap<String, String>)dataSnapshot.getValue();

                if(teachersmap==null){

                    //Toast.makeText(getApplicationContext(),"No teachers added",Toast.LENGTH_LONG).show();

                }else{

                   // Toast.makeText(getApplicationContext(),teachersmap.size() + " teachers found ",Toast.LENGTH_LONG).show();


                    teacherLt = new ArrayList<>();
                    for ( String key : teachersmap.keySet() ) {

                        System.out.println( key );              //key is teacher user id


                        databaseReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName).child(key);

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                teacherserial=(String) dataSnapshot.getValue();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        databaseReference = Constants.databaseReference.child(Constants.USER_DETAILS_TABLE).child(key);

                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                HashMap<String, String> teachermap=(HashMap<String, String>)dataSnapshot.getValue();
                                teacherLt.add(teacherserial + ". " + teachermap.get("name"));
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }


                }





            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return teacherLt;

    }






    public String getTeacherWithNameAndSerial(String institutionName, final String serial){
        databaseReference = Constants.databaseReference.child(Constants.TEACHER_TABLE).child(institutionName);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, String> teachermap=(HashMap<String, String>)dataSnapshot.getValue();
                if(teachermap==null){
                    teacheruserid=null;
                }else{

                    for ( String key : teachermap.keySet() ) {

                        if(teachermap.get(key)==serial){
                            teacheruserid=key;
                            break;
                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return teacheruserid;
    }



}
