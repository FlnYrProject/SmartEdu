package com.project.smartedu.teacher;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.project.smartedu.LoginActivity;
import com.project.smartedu.database.*;
import com.project.smartedu.database.Class;
import com.project.smartedu.database.Students;

import java.util.ArrayList;
import java.util.HashMap;

import static com.project.smartedu.admin.AdminUserPrefs.allotmments;

/**
 * Created by Shubham Bhasin on 08-Mar-17.
 */

public class TeacherUserPrefs {

    SharedPreferences pref;

    SharedPreferences.Editor editor;


    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "TeacherUserDetails";

    public static final String INSTITUION= "Inst";

    public  static final String FIRST_INFO_LOADING="FirstInfoLoading";

    public  static final String FIRST_MARKS_LOADING="FirstMarksLoading";

    public  static final String FIRST_ATTENDANCE_LOADING="FirstAttendanceLoading";

    public static String studentName="";
    public static String studentDob="";

    public static String studentAddress="";

    public static String studentContact="";
    public static String studentParentEmail="";




    public static ArrayList<String> allotments;

    public static HashMap<String,ArrayList<String>> subjectallotmentmap;

    public static ArrayList<String> studentsuseridLt;
    public static HashMap<String,Students> studentsHashMap;                 //user id to student details


    public static ArrayList<String> examidLt;
    public static HashMap<String,Exam> examHashMap;





    public static  HashMap<String,Exam> examMap=new HashMap<>();    //exam id--> marks obtained
    public static  HashMap<String,String> exammarksobtMap=new HashMap<>();       //exam id---> exam



    static {

        allotments=new ArrayList<>();
        subjectallotmentmap=new HashMap<>();
        studentsuseridLt=new ArrayList<>();
        studentsHashMap=new HashMap<>();
        examidLt=new ArrayList<>();
        examHashMap=new HashMap<>();


        studentName=new String();
        studentDob=new String();
        studentAddress=new String();
        studentContact=new String();
        studentParentEmail=new String();

        examMap=new HashMap<>();
        exammarksobtMap=new HashMap<>();

    }



    public TeacherUserPrefs(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }



    public void setInstituion(String instituion){

        // Storing name in pref
        editor.putString(INSTITUION, instituion);

        // commit changes
        editor.commit();
    }


    public void setFirstInfoLoading(Boolean firstInfoLoading){

        // Storing name in pref
        editor.putBoolean(FIRST_INFO_LOADING, firstInfoLoading);

        // commit changes
        editor.commit();
    }

    public Boolean getFirstInfoLoading(){
        return pref.getBoolean(FIRST_INFO_LOADING,true);
    }

    public void setFirstAttendanceLoading(Boolean firstAttendanceLoading){

        // Storing name in pref
        editor.putBoolean(FIRST_ATTENDANCE_LOADING, firstAttendanceLoading);

        // commit changes
        editor.commit();
    }

    public Boolean getFirstAttendanceLoading(){
        return pref.getBoolean(FIRST_ATTENDANCE_LOADING,true);
    }



    public void setFirstMarksLoading(Boolean firstMarksLoading){

        // Storing name in pref
        editor.putBoolean(FIRST_MARKS_LOADING, firstMarksLoading);

        // commit changes
        editor.commit();
    }

    public Boolean getFirstMarksLoading(){
        return pref.getBoolean(FIRST_MARKS_LOADING,true);
    }
    public void clearTeacherDetails(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
      //  clearAllSavedData();

        allotments.clear();
        subjectallotmentmap.clear();
        studentsuseridLt.clear();
        studentsHashMap.clear();

    }



}
