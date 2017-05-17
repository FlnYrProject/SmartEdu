package com.project.smartedu;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Shubham Bhasin on 04-Feb-17.
 */

public class Constants {

  public static final DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference().child(Constants.DATABASE);

    public static final String DATABASE = "SmartEdu_DB";
    public static final String USER_DETAILS_TABLE= "user_details";
    public static final String INSTITUTION_TABLE= "institution";
    public static final String TASK_TABLE= "tasks";
    public static final String TEACHER_TABLE= "teachers";
  public static final String CLASS_TABLE= "classes";
  public static final String ALLOTMENTS_TABLE= "allotments";
  public static final String SCHEDULES_TABLE= "schedules";
  public static final String STUDENTS_TABLE= "students";
  public static final String PARENT_RELATION_TABLE= "parents";
  public static final String UPLOADS_TABLE= "uploads";
  public static final String MESSAGES_TABLE= "messages";



  public static final String[] days= {"Monday","Tuesday","Wednesday","Thursday","Friday","Saturday","Sunday"};









}
