package com.project.smartedu;

/**
 * Created by Shubham Bhasin on 26-Mar-17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.project.smartedu.teacher.Attendance;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.graphics.Color.RED;
import static android.graphics.Color.WHITE;

public class CustomAdapter extends ArrayAdapter<Model> implements View.OnClickListener {
    Model[] modelItems = null;
    Context context;
 //   ParseObject classRef;


    String classId;
    Button addButton;
    Button editButton;
    Button doneButton;
    EditText editabsentDays;
    //EditText editpercentage;
    EditText edittotalDays;
    TextView absentDays;
    TextView percentage;
    TextView totalDays;
    TextView myDate;
    TextView editmyDate;
    Date date1;
    // CalendarView calendar;
    java.util.Calendar calendar;
    ImageView cal;
    int Year;
    int Month;
    int Day;
    String role;

    View mainView;
    public CustomAdapter(Context context, Model[] resource, String classId) {
        super(context, R.layout.list_row, resource);
        // TODO Auto-generated constructor stub
        this.context = context;
        this.modelItems = resource;
        this.classId = classId;

    }

   /* public void clearList() {
        searchArrayList.clear();
        this.notifyDataSetChanged();
    }*/

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        Model item = (Model) this.getItem(position);
        TextView name;
        CheckBox cb;
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        if (convertView == null) {

            convertView = inflater.inflate(R.layout.list_row, parent, false);

            name = (TextView) convertView.findViewById(R.id.textView1);
            name.setOnClickListener(this);
            cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
            cb.setOnClickListener(this);
            convertView.setTag(new ModelViewHolder(name, cb));


        } else {
            ModelViewHolder viewHolder = (ModelViewHolder) convertView
                    .getTag();
            cb = viewHolder.getCheckBox();
            name = viewHolder.getTextView();
        }


        name.setText(modelItems[position].getName());
        if (modelItems[position].getValue() == 1)
            cb.setChecked(true);
        else
            cb.setChecked(false);




        cb.setTag(item);
        return convertView;
    }

    @Override
    public void onClick(final View v) {
        switch (v.getId()) {
            case R.id.textView1:
                // Do stuff accordingly...
               /* if (context instanceof Attendance) {
                    String[] item = ((TextView) v).getText().toString().split(". ");
                    final int itemvalue = Integer.parseInt(item[0]);
                    Log.d("classRef", classRef.toString());

                    final ParseObject[] studentRef = new ParseObject[1];
                    ParseQuery<ParseObject> studentQuery = ParseQuery.getQuery("Student");
                    studentQuery.whereEqualTo("rollNumber", itemvalue);
                    studentQuery.whereEqualTo("class", classRef);
                    //Log.d("class", itemvalue + "");
                    studentQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> studentListRet, ParseException e) {
                            if (e == null) {
                                Log.d("class", "Retrieved the class");
                                //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                                if (studentListRet.size() != 0) {
                                    studentRef[0] = studentListRet.get(0);
                                    // Log.d("class object id", classRef[0].getObjectId());

                                    ParseQuery<ParseObject> attQuery = ParseQuery.getQuery("AttendanceDaily");
                                    attQuery.whereEqualTo("student", studentRef[0]);
                                    Log.d("class", itemvalue + "");
                                    attQuery.findInBackground(new FindCallback<ParseObject>() {
                                        public void done(List<ParseObject> attListRet, ParseException e) {
                                            if (e == null) {
                                                if (attListRet.size() != 0) {
                                                    Log.d("class", "Retrieved the class" + attListRet.size());
                                                    //Toast.makeText(getApplicationContext(), studentListRet.toString(), Toast.LENGTH_LONG).show();

                                                    //studentRef[0] = studentListRet.get(0);
                                                    //information(studentRef[0], v);
                                                    double present = 0;
                                                    double absent = 0;
                                                    double totalDays = 0;
                                                    double percentage = 0.0;
                                                    for (int i = 0; i < attListRet.size(); i++) {
                                                        ParseObject u = attListRet.get(i);
                                                        if (u.get("p_a").equals("A")) {
                                                            absent++;
                                                        }
                                                        totalDays++;
                                                    }
                                                    present = totalDays - absent;
                                                    percentage = (present / totalDays) * 100;
                                                    information(v, absent, totalDays, percentage);
                                                } else {
                                                    Toast.makeText(getContext(), "No Attendance Added", Toast.LENGTH_LONG).show();
                                                }

                                            } else {
                                                Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                                                Log.d("user", "Error: " + e.getMessage());
                                            }
                                        }
                                    });
                                }
                            } else {
                                Toast.makeText(context, "error", Toast.LENGTH_LONG).show();
                                Log.d("user", "Error: " + e.getMessage());
                            }
                        }
                    });
                }*/


                break;
            case R.id.checkBox1:
                // Do stuff accordingly...
                CheckBox cb = (CheckBox) v;
                Model model = (Model) cb.getTag();
                model.setChecked(cb.isChecked());

                if(cb.isChecked()) {
                   v.setBackgroundColor(RED);


                }else {
                    v.setBackgroundColor(WHITE);
                }

                break;

            default:
                break;
        }
    }

    public void information(final View view, double absent, double totalDays, double percentage) {

        final Dialog dialog2 = new Dialog(context);
        dialog2.setContentView(R.layout.show_attendance_daily);
        dialog2.setTitle("Attendance Information");
        this.absentDays = (TextView) dialog2.findViewById(R.id.exam);
        this.totalDays = (TextView) dialog2.findViewById(R.id.totalMarks);
        this.percentage = (TextView) dialog2.findViewById(R.id.percentage);
        myDate = (TextView) dialog2.findViewById(R.id.dateText1);
        String absentDays = absent + "";
        String total = totalDays + "";
        String PER = percentage + "";

        this.absentDays.setText(absentDays.trim());
        this.totalDays.setText(total.trim());
        this.percentage.setText(PER.trim());

        calendar = java.util.Calendar.getInstance();
        //System.out.println("Current time =&gt; " + calendar.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());
        myDate.setText(string_current_date);

        // dialog2.dismiss();
        dialog2.show();

    }


   /* public void information(final ParseObject studentRef, final View view){
        ParseQuery<ParseObject> attendanceQuery = ParseQuery.getQuery("Attendance");
        attendanceQuery.whereEqualTo("student", studentRef);
        attendanceQuery.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> attendanceListRet, ParseException e) {
                if (e == null) {
                    if (attendanceListRet.size() == 0) {
                        final Dialog dialog1 = new Dialog(context);
                        dialog1.setContentView(R.layout.attendance_not_added);
                        dialog1.setTitle("Attendance Information");

                        addButton = (Button) dialog1.findViewById(R.id.addButton);

                        addButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                addDialog(studentRef, view);
                                dialog1.dismiss();

                            }
                        });

                        dialog1.show();
                    } else {

                        final Dialog dialog2 = new Dialog(context);
                        dialog2.setContentView(R.layout.show_attendance);
                        dialog2.setTitle("Attendance Information");
                        editButton = (Button) dialog2.findViewById(R.id.editButton);
                        absentDays = (TextView) dialog2.findViewById(R.id.exam);
                        totalDays = (TextView) dialog2.findViewById(R.id.totalMarks);
                        percentage = (TextView) dialog2.findViewById(R.id.percentage);
                        myDate = (TextView) dialog2.findViewById(R.id.dateText1);

                        ParseObject u = attendanceListRet.get(0);
                        absentDays.setText(u.getNumber("absentDays").toString().trim());
                        totalDays.setText(u.getNumber("totalDays").toString().trim());
                        percentage.setText(u.getNumber("percentage").toString().trim());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                        final String dateString = formatter.format(new Date(u.getLong("date")));
                        myDate.setText(dateString);


                        editButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                editDialog(studentRef, view);

                                dialog2.dismiss();
                            }
                        });

                        dialog2.show();
                    }

                } else {
                    Toast.makeText(getContext(), "error", Toast.LENGTH_LONG).show();
                    Log.d("user", "Error: " + e.getMessage());
                }
            }
        });
    }
*/



   /* public void addDialog(final ParseObject studentRef, final View view){

        final Dialog dialog_in = new Dialog(context);
        dialog_in.setContentView(R.layout.edit_attendance);
        dialog_in.setTitle("Edit Details");

        editabsentDays = (EditText) dialog_in.findViewById(R.id.exam);
        edittotalDays = (EditText) dialog_in.findViewById(R.id.totalMarks);
        editmyDate = (TextView) dialog_in.findViewById(R.id.dateText1);

        calendar = java.util.Calendar.getInstance();
        //System.out.println("Current time =&gt; " + calendar.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());
        editmyDate.setText(string_current_date);

        doneButton = (Button) dialog_in.findViewById(R.id.doneButton);
       *//* cal = (ImageButton) dialog_in.findViewById(R.id.test);
        //final int[] flag = {0};
        cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(view);
                //flag[0] = 1;
                editmyDate.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
            }
        });*//*

        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (editabsentDays.equals("") || edittotalDays.equals("")) {
                    Toast.makeText(context, "Attendance details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    ParseObject attendance= new ParseObject("Attendance");
                    attendance.put("student",studentRef);
                    attendance.put("absentDays", Float.parseFloat(editabsentDays.getText().toString()));
                    attendance.put("totalDays", Float.parseFloat(edittotalDays.getText().toString()));
                    float abs = Float.parseFloat(editabsentDays.getText().toString());
                    float total = Float.parseFloat(edittotalDays.getText().toString());
                    float percent = ((total - abs) / total) * 100;
                    attendance.put("percentage", percent);

                    String[] date = string_current_date.trim().split("/");
                    final String[] datedetails = new String[3];
                    int j = 0;

                    for (String x : date) {
                        datedetails[j++] = x;
                    }

                    Day = Integer.parseInt(datedetails[0]);
                    Month = Integer.parseInt(datedetails[1]);
                    Year = Integer.parseInt(datedetails[2]);


                    String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
                    Toast.makeText(context, "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

                    SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                    Date d = null;
                    try {
                        d = f.parse(string_date);
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    long newmilliseconds = d.getTime();
                    attendance.put("date", newmilliseconds);
                    attendance.saveEventually();



                    dialog_in.dismiss();
                }
            }

        });

        dialog_in.show();

    }*/

   /* public void editDialog(final ParseObject studentRef, final View view){

        final Dialog dialog_in = new Dialog(context);
        dialog_in.setContentView(R.layout.edit_attendance);
        dialog_in.setTitle("Edit Details");

        editabsentDays = (EditText) dialog_in.findViewById(R.id.exam);
        edittotalDays = (EditText) dialog_in.findViewById(R.id.totalMarks);
        editmyDate = (TextView) dialog_in.findViewById(R.id.dateText1);

        editabsentDays.setText(absentDays.getText().toString().trim());
        edittotalDays.setText(totalDays.getText().toString().trim());

        doneButton = (Button) dialog_in.findViewById(R.id.doneButton);
        cal = (ImageButton) dialog_in.findViewById(R.id.test);
        //final int[] flag = {0};

        calendar = Calendar.getInstance();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
        final String string_current_date = df.format(calendar.getTime());
        editmyDate.setText(string_current_date);


     *//*   cal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open(view);
                flag[0] = 1;
                myDate.setText(String.valueOf(Daycal) + "/" + String.valueOf(Monthcal) + "/" + String.valueOf(Yearcal));
            }
        }); *//*

        doneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (editabsentDays.equals("") || edittotalDays.equals("") ) {
                    Toast.makeText(context, "Attendance details cannot be empty!", Toast.LENGTH_LONG).show();
                } else {
                    ParseQuery<ParseObject> inQuery = ParseQuery.getQuery("Attendance");
                    inQuery.whereEqualTo("student", studentRef);
                    inQuery.findInBackground(new FindCallback<ParseObject>() {
                        public void done(List<ParseObject> objectRet, ParseException e) {

                            if (e == null) {
                                objectRet.get(0).put("absentDays", Float.parseFloat(editabsentDays.getText().toString()));
                                objectRet.get(0).put("totalDays", Float.parseFloat(edittotalDays.getText().toString()));
                                float abs = Float.parseFloat(editabsentDays.getText().toString());
                                float total = Float.parseFloat(edittotalDays.getText().toString());
                                float percent = ((total - abs) / total) * 100;
                                objectRet.get(0).put("percentage", percent);

                                String[] datenew = string_current_date.split("/");

                                final String[] datedetailsnew = new String[3];
                                int j = 0;

                                for (String x : datenew) {
                                    datedetailsnew[j++] = x;
                                }
                                Log.d("Post retrieval", datedetailsnew[0]);
                                Toast.makeText(context, datedetailsnew[0], Toast.LENGTH_LONG);
                                Day = Integer.parseInt(datedetailsnew[0]);
                                Month = Integer.parseInt(datedetailsnew[1]);
                                Year = Integer.parseInt(datedetailsnew[2]);

                                String string_date = String.valueOf(Day) + "-" + String.valueOf(Month) + "-" + String.valueOf(Year);
                                Toast.makeText(context, "current date = " + Day + "/" + Month + "/" + Year, Toast.LENGTH_LONG).show();

                                SimpleDateFormat f = new SimpleDateFormat("dd-MM-yyyy");
                                Date d = null;
                                try {
                                    d = f.parse(string_date);
                                } catch (java.text.ParseException e1) {
                                    e.printStackTrace();
                                }
                                long newmilliseconds = d.getTime();
                                objectRet.get(0).put("date", newmilliseconds);
                                objectRet.get(0).saveEventually();

                            } else {
                                Log.d("Post retrieval", "Error: " + e.getMessage());
                            }

                        }
                    });

                    dialog_in.dismiss();
                }
            }

        });

        dialog_in.show();

    }*/


}