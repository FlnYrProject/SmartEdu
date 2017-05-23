package com.project.smartedu.student;

import android.content.Context;
import android.content.SharedPreferences;

import com.project.smartedu.database.Exam;
import com.project.smartedu.database.Students;
import com.project.smartedu.database.Teachers;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shubham Bhasin on 31-Mar-17.
 */

public class StudentUserPrefs {


    SharedPreferences pref;

    SharedPreferences.Editor editor;


    Context _context;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "StudentUserDetails";

    public static final String INSTITUION= "Inst";

    public static final String CLASS_ID="classId";

    public static final String ROLL_NUMBER="roll_number";







    public static ArrayList<String> teachersuseridLt;
    public static HashMap<String,Teachers> teacherHashMap;                 //user id to student details






    static {
teachersuseridLt=new ArrayList<>();
        teacherHashMap=new HashMap<>();
    }



    public StudentUserPrefs(Context context){
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


    public void setClassId(String classId){

        // Storing name in pref
        editor.putString(CLASS_ID, classId);

        // commit changes
        editor.commit();
    }

    public void setRollNumber(int rollNumber){

        // Storing name in pref
        editor.putInt(ROLL_NUMBER, rollNumber);

        // commit changes
        editor.commit();
    }


    public String getInstitution(){
        return pref.getString(INSTITUION,"");
    }

    public String getClassId(){
        return pref.getString(CLASS_ID,"");
    }

    public int getRollNumber(){
        return pref.getInt(ROLL_NUMBER,0);
    }

    public void clearStudentDetails(){
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();
        //  clearAllSavedData();


    }


}
