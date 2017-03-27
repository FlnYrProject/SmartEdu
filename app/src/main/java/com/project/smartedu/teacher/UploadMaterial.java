package com.project.smartedu.teacher;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.project.smartedu.BaseActivity;
import com.project.smartedu.Constants;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.database.Uploads;
import com.project.smartedu.navigation.FragmentDrawer;
import com.project.smartedu.notification.NotificationBar;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;


public class UploadMaterial extends BaseActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    NotificationBar noti_bar;
    TeacherUserPrefs teacherUserPrefs;
    UserPrefs userPrefs;
    ListView list;

    String _for;
    String classId;
    DatabaseReference databaseReference;
    int rollno= -1;

    Button uploadButton;
    Button editButton;
    Button doneButton;
    Button okButton;
    Button delButton;
    Button removeDueDate;
    Button addMoreButton;
    Button viewAllButton;
    Button addUploadButton;

    TextView DeadlineHead;

    TextView myDate;
    TextView editmyDate;
    TextView myDueDate;
    TextView myType;
    TextView mySubject;
    TextView myTopic;
    TextView detailHead;
    TextView subjectAssigned;

    //EditText subject;
    String subject;
    EditText topic;
    Date date1;
    CalendarView calendarView;
    Calendar calendar;
    ImageButton cal;
    ImageView imageUpload;
    int Year;
    int Month;
    int Day;

    String typeSelected;
    String subjectDesc;
    String uploadId;

    String topicDesc;
    Spinner type;
    int Yearcal;
    int Monthcal;
    int Daycal;

    String[] items;

    //HashMap<String,String> uploadidmap;
    ListView uploadList;
    ArrayAdapter adapter=null;
    ArrayList<String> uploadLtString;
    ArrayList<com.project.smartedu.database.Uploads> uploadLt;

    Spinner subjectSpinner;
    ArrayAdapter subjectadapter;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_material);

        userPrefs=new UserPrefs(UploadMaterial.this);
        teacherUserPrefs=new TeacherUserPrefs(UploadMaterial.this);

        Intent from_upload = getIntent();
        classId = from_upload.getStringExtra("id");
        _for = from_upload.getStringExtra("for");
        role = from_upload.getStringExtra("role");
        institutionName = from_upload.getStringExtra("institution_name");

        userPrefs = new UserPrefs(UploadMaterial.this);
        /*uploadidmap= UserPrefs.uploadidmap;

        for ( String key : uploadidmap.keySet() ){
            Log.d("keyi",key);
        }*/

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
        //   getSupportActionBar().setDisplayShowHomeEnabled(true);
        //   getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Uploads");

        drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar, role);
        drawerFragment.setDrawerListener(this);


        noti_bar = (NotificationBar)getSupportFragmentManager().findFragmentById(R.id.noti);
        noti_bar.setTexts(userPrefs.getUserName(), role,super.institutionName);

        subjectSpinner=(Spinner)findViewById(R.id.subjectListspinner);
        subjectadapter = new ArrayAdapter(UploadMaterial.this, android.R.layout.simple_list_item_1, TeacherUserPrefs.subjectallotmentmap.get(classId));
        subjectSpinner.setAdapter(subjectadapter);


        uploadList = (ListView) findViewById(R.id.uploadList);
        firebaseAuth = FirebaseAuth.getInstance();
        uploadLt=new ArrayList<>();
        uploadLtString=new ArrayList<>();

        for(Uploads upl:UserPrefs.uploadkeymap.keySet()){
                uploadLt.add(upl);
        }

        if (uploadLt.size() == 0) {
            uploadList.setVisibility(View.INVISIBLE);

        } else {
            items = new String[uploadLt.size()];


            for (int i = 0; i < uploadLt.size(); i++) {
                Uploads uploadobject = uploadLt.get(i);
                long date = TimeUnit.MILLISECONDS.toMinutes(uploadobject.getDate());
                String upload_type = uploadobject.getUploadType();
                String subject = uploadobject.getSubject();
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                String dateString = formatter.format(new Date(date));
                String uploaditem = upload_type + "\n" + subject + "\n" + dateString;
                items[i] = uploaditem;

            }
            uploadLtString = new ArrayList<>(Arrays.asList(items));
            adapter = new ArrayAdapter(UploadMaterial.this, android.R.layout.simple_list_item_1, uploadLtString);
            uploadList.setAdapter(adapter);


        }





        addUploadButton = (Button) findViewById(R.id.uploadButton);

        Toast.makeText(UploadMaterial.this, "id class selected is = " + classId, Toast.LENGTH_LONG).show();

        addUploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subject = subjectSpinner.getSelectedItem().toString().trim();
             uploadNew();
            }
        });




    }



    public void uploadNew(){

        final Dialog dialog_upload = new Dialog(UploadMaterial.this);
        dialog_upload.setContentView(R.layout.upload_material);
        dialog_upload.setTitle("Upload Material");

        setDialogSize(dialog_upload);

        DeadlineHead= (TextView) dialog_upload.findViewById(R.id.deadlineHead);
        DeadlineHead.setSelected(true);

        myDate = (TextView) dialog_upload.findViewById(R.id.date);
        myDueDate= (TextView) dialog_upload.findViewById(R.id.deadline);
        subjectAssigned=(TextView) dialog_upload.findViewById(R.id.subject);
        cal=(ImageButton) dialog_upload.findViewById(R.id.calButton);
        removeDueDate=(Button) dialog_upload.findViewById(R.id.removeDueDate);
        topic=(EditText) dialog_upload.findViewById(R.id.topic);


        subjectAssigned.setText(subject);



        final String[] values=new String[2];
        values[0]="Assignment";
        values[1]="Study Material";
        type = (Spinner)dialog_upload.findViewById(R.id.type);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, values);
        type.setAdapter(adapter);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });





        calendar = Calendar.getInstance();
        //System.out.println("Current time =&gt; " + calendar.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());
        myDate.setText(string_current_date);

        String[] uploaddate = myDate.getText().toString().split("/");

        final String[] datedetails_upload = new String[3];
        int j = 0;

        for (String x : uploaddate) {
            datedetails_upload[j++] = x;
        }
        Log.d("Post retrieval", datedetails_upload[0]);
        // Toast.makeText(getApplicationContext(), datedetails_upload[0], Toast.LENGTH_LONG).show();
        Day = Integer.parseInt(datedetails_upload[0]);
        Month = Integer.parseInt(datedetails_upload[1]);
        Year = Integer.parseInt(datedetails_upload[2]);

        String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);

        Toast.makeText(getApplicationContext(), "updated date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

        SimpleDateFormat f1 = new SimpleDateFormat("dd-MM-yyyy");
        Date d = null;
        try {
            d = f1.parse(string_date);
        } catch (java.text.ParseException e) {
            e.printStackTrace();
        }
        final long upload_date_milliseconds = d.getTime();


        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                open();
                myDueDate.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
            }
        });


        removeDueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myDueDate.setText("Deadline Date");
                Daycal=0;
                Monthcal=0;
                Yearcal=0;
            }
        });



        uploadButton= (Button) dialog_upload.findViewById(R.id.uploadButton);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                typeSelected=type.getSelectedItem().toString().trim();

                //subjectDesc=subject.getText().toString().trim();
                topicDesc=topic.getText().toString().trim();

                if (typeSelected.equals("") || subject.equals("") ||topicDesc.equals("")) {
                    Toast.makeText(getApplicationContext(), "Type or Subject details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {



                    //adding to upload table
                    databaseReference = Constants.databaseReference.child(Constants.UPLOADS_TABLE);
                    databaseReference= databaseReference.child(institutionName).push();
                    uploadId = databaseReference.getKey();
                    databaseReference.child("class").setValue(classId);
                    databaseReference.child("upload_type").setValue(typeSelected);
                    databaseReference.child("topic").setValue(topicDesc);
                    databaseReference.child("subject").setValue(subject);
                    databaseReference.child("date").setValue(String.valueOf(upload_date_milliseconds));
                    databaseReference.child("teacher").setValue(firebaseAuth.getCurrentUser().getUid());//adding to server

                    Log.d("Upload", "Object Id: uploadImage: " + uploadId);

                    Intent to_upload_image = new Intent(UploadMaterial.this, UploadImage.class);
                    to_upload_image.putExtra("classId", classId);
                    to_upload_image.putExtra("uploadId", uploadId);
                    to_upload_image.putExtra("institution_name", institutionName);
                    to_upload_image.putExtra("role", role);
                   // to_upload_image.putExtra("permission_storage", permission_storage);
                    startActivity(to_upload_image);

                    dialog_upload.dismiss();
                }


            }
        });

        dialog_upload.show();

    }

    public void open()
    {

        final Dialog dialogcal = new Dialog(UploadMaterial.this);
        dialogcal.setContentView(R.layout.activity_calendar2);
        dialogcal.setTitle("Select Date");

        setDialogSize(dialogcal);

        calendarView= (CalendarView)dialogcal.findViewById(R.id.calendar);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {

                date1=null;
                Yearcal = year;
                Monthcal = month+1;
                Daycal = dayOfMonth;

                long[] milliseconds = new long[2];


                checkDate(Daycal, Monthcal, Yearcal, milliseconds);
                Log.d("date test ", milliseconds[0] + " selected:" + milliseconds[1]);
                if(milliseconds[1] <= milliseconds[0]){
                    Toast.makeText(getApplicationContext(), "Choose Future Date!", Toast.LENGTH_LONG).show();
                }
                else {
                    date1 = new Date(Yearcal - 1900, Monthcal - 1, Daycal);
                    DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());
                    myDueDate.setText(dateFormat.format(date1), TextView.BufferType.EDITABLE);
                    Toast.makeText(getApplicationContext(), Daycal + "/" + Monthcal + "/" + Yearcal, Toast.LENGTH_LONG).show();
                    dialogcal.dismiss();
                }

            }
        });
        dialogcal.show();



    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent task_intent = new Intent(UploadMaterial.this, Classes.class);
        task_intent.putExtra("institution_name", institutionName);
        task_intent.putExtra("for","upload");
        task_intent.putExtra("role", role);
        //task_intent.putExtra("id", classId);
        startActivity(task_intent);
    }

}
