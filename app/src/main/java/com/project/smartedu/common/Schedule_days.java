package com.project.smartedu.common;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.Constants;
import com.project.smartedu.R;
import com.project.smartedu.UserPrefs;
import com.project.smartedu.admin.AdminUserPrefs;
import com.project.smartedu.database.*;
import com.project.smartedu.database.Schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Schedule_days extends Fragment {

    ListView scheduleList;
    Button scheduleAdd;
    EditText info;
    String day;
    String role;
    Spinner starthours;
    Spinner startmins;
    Spinner endhours;
    Spinner endmins;
    String[] items;
    ArrayList<String> scheduleLt;
    ArrayAdapter adapter=null;
    TextView starttimedisplay;
    TextView endtimedisplay;
    TextView infodisplay;
    Button okButton;
    Button delButton;
    Button editButton;
    EditText Desc;
    Button EditButton;
    int flag=1;
    TextView noschedule;
    ImageView noScheduleImage;
    String institutionName;


    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;

    ArrayList<com.project.smartedu.database.Schedule> scheduleslt;
    HashMap<String,Schedule> localScheduleMap;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View schedule = inflater.inflate(R.layout.fragment_schedule_days, container, false);
        day = getArguments().getString("day");
        role = getArguments().getString("role");
        institutionName = getArguments().getString("institution_name");

        scheduleList = (ListView) schedule.findViewById(R.id.scheduleList);
        scheduleAdd = (Button) schedule.findViewById(R.id.addSchedule);
        noschedule = (TextView) schedule.findViewById(R.id.noSchedule);
        noScheduleImage = (ImageView) schedule.findViewById(R.id.noScheduleImage);

        databaseReference = Constants.databaseReference;
        firebaseAuth = FirebaseAuth.getInstance();

        Log.d("institution", institutionName);

        scheduleslt=new ArrayList<>();
        localScheduleMap=new HashMap<>();

        for(Schedule sch:UserPrefs.schedulekeymap.keySet()){
            if(sch.getDay().equals(day)){
                scheduleslt.add(sch);
            }
        }




            if (scheduleslt.size() == 0) {
                scheduleList.setVisibility(View.INVISIBLE);
                noschedule.setText("No Schedule Added");
            } else {
                items = new String[scheduleslt.size()];


                for (int i = 0; i < scheduleslt.size(); i++) {
                    Schedule scheduleobject = scheduleslt.get(i);
                    long start = TimeUnit.MILLISECONDS.toMinutes(scheduleobject.getStart_time());
                    long end = TimeUnit.MILLISECONDS.toMinutes(scheduleobject.getEnd_time());
                    String scheduleitem = start + "\n" + end + "\n" + scheduleobject.getInfo();
                    items[i] = scheduleitem;
                    localScheduleMap.put(scheduleitem,scheduleobject);
                }
                scheduleLt = new ArrayList<>(Arrays.asList(items));
                adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, scheduleLt);
                scheduleList.setAdapter(adapter);


            }








        scheduleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    final int position, final long id) {


                // selected item
                String[] scheduleobject = ((TextView) view).getText().toString().split("\n");
                final String[] details = new String[3];
                int i = 0;

                for (String x : scheduleobject) {
                    details[i++] = x;
                }

                final Dialog show_dialog = new Dialog(getActivity());
                show_dialog.setContentView(R.layout.show_schedule_details);
                show_dialog.setTitle("Schedule Details");

                starttimedisplay = (TextView) show_dialog.findViewById(R.id.start_time);
                endtimedisplay = (TextView) show_dialog.findViewById(R.id.end_time);
                infodisplay = (TextView) show_dialog.findViewById(R.id.info);

                starttimedisplay.setText(details[0].trim());
                endtimedisplay.setText(details[1].trim());

                infodisplay.setText(details[2].trim());

                String[] sttimes = details[0].split(":");
//                long time = TimeUnit.MINUTES.toMillis(Integer.parseInt(sttimes[0]) * 60 + Integer.parseInt(sttimes[1]));

                final String[] scheduleId = new String[1];


                okButton = (Button) show_dialog.findViewById(R.id.doneButton);
                delButton = (Button) show_dialog.findViewById(R.id.delButton);
                editButton = (Button) show_dialog.findViewById(R.id.editButton);


                okButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {

                        show_dialog.dismiss();

                    }
                });

                delButton.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        String selecteditem=((TextView) view).getText().toString();
                        Schedule selectedSchedule=localScheduleMap.get(selecteditem);

                      databaseReference=Constants.databaseReference.child(Constants.SCHEDULES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(day).child(UserPrefs.schedulekeymap.get(selectedSchedule));
                        databaseReference.removeValue();        //removing from server

                        localScheduleMap.remove(selecteditem);
                        UserPrefs.schedulekeymap.remove(selectedSchedule);     //remove from local data
                        scheduleslt.remove(selectedSchedule);
                        show_dialog.dismiss();
                        Intent reload=new Intent(getActivity(), com.project.smartedu.common.Schedule.class);
                        reload.putExtra("institutionName",institutionName);
                        reload.putExtra("day", day);
                        reload.putExtra("role", role);
                        startActivity(reload);

                    }
                });

                editButton.setOnClickListener(new View.OnClickListener() {

                    public void onClick(View v) {


                        final Dialog dialog_in = new Dialog(getActivity());
                        dialog_in.setContentView(R.layout.activity_edit_schedule);
                        dialog_in.setTitle("Edit Details");




                        final String selecteditem=((TextView) view).getText().toString();
                        final Schedule selectedSchedule=localScheduleMap.get(selecteditem);

                        databaseReference=Constants.databaseReference.child(Constants.SCHEDULES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(day).child(UserPrefs.schedulekeymap.get(selectedSchedule));





                        Desc = (EditText) dialog_in.findViewById(R.id.scheduleinfo);

                        EditButton = (Button) dialog_in.findViewById(R.id.editButton);

                        Desc.setText(details[2]);


                        EditButton.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                databaseReference.child("info").setValue(Desc.getText().toString());    //to server

                                localScheduleMap.remove(selecteditem);
                                String key= UserPrefs.schedulekeymap.get(selectedSchedule);
                                UserPrefs.schedulekeymap.remove(selectedSchedule);     //remove from local data
                                scheduleslt.remove(selectedSchedule);




                                Schedule neweditschedule=new Schedule(day,selectedSchedule.getStart_time(),selectedSchedule.getEnd_time(),Desc.getText().toString());
                                long start = TimeUnit.MILLISECONDS.toMinutes(selectedSchedule.getStart_time());
                                long end = TimeUnit.MILLISECONDS.toMinutes(selectedSchedule.getEnd_time());
                                String editedscheduleitem = start + "\n" + end + "\n" + Desc.getText().toString();
                                localScheduleMap.put(editedscheduleitem,neweditschedule);
                                UserPrefs.schedulekeymap.put(neweditschedule,key);
                                scheduleslt.add(neweditschedule);
                                dialog_in.dismiss();

                                Intent reload=new Intent(getActivity(), com.project.smartedu.common.Schedule.class);
                                reload.putExtra("institutionName",institutionName);
                                reload.putExtra("day", day);
                                reload.putExtra("role", role);
                                startActivity(reload);

                                //edit shedule info and save

                            }

                        });

                        dialog_in.show();
                        show_dialog.dismiss();
                    }
                });

                show_dialog.show();
                //recreate();


            }
        });









        scheduleAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                newSchedule(v);
            }
        });







        return schedule;
    }
















    public void newSchedule(final View view)
    {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.new_schedule);
        dialog.setTitle("Select Date");
        final Button addnew=(Button)dialog.findViewById(R.id.add);
        info=(EditText)dialog.findViewById(R.id.info);

        final String[] hours= new String[24];
        for(int i=0;i<24;i++)
        {
            hours[i]=String.valueOf(i);
        }
        final String[] mins=new String[60];
        for(int i=0;i<60;i++)
        {
            mins[i]=String.valueOf(i);
        }
        starthours = (Spinner)dialog.findViewById(R.id.start_hours);
        startmins = (Spinner)dialog.findViewById(R.id.start_mins);
        endhours = (Spinner)dialog.findViewById(R.id.end_hours);
        endmins = (Spinner)dialog.findViewById(R.id.end_mins);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, hours);
        starthours.setAdapter(adapter);
        adapter=new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, mins);
        startmins.setAdapter(adapter);
        starthours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedstarthours = Integer.parseInt(starthours.getSelectedItem().toString());
                String[] hours_end = new String[24 - selectedstarthours];
                int x = selectedstarthours;
                for (int i = 0; i < 24 - selectedstarthours; i++) {
                    hours_end[i] = String.valueOf(x);
                    x++;
                }
                ArrayAdapter<String> adapter_end = new ArrayAdapter<String>(getActivity(),
                        android.R.layout.simple_spinner_item, hours_end);
                endhours.setAdapter(adapter_end);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });



        endhours.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedendhours = Integer.parseInt(endhours.getSelectedItem().toString());
                if(!(selectedendhours==Integer.parseInt(starthours.getSelectedItem().toString()))) {
                    String[] mins_end = new String[60];
                    for (int i = 0; i < 60; i++) {
                        mins_end[i] = String.valueOf(i);
                    }

                    ArrayAdapter<String> adapter_end = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item, mins_end);
                    endmins.setAdapter(adapter_end);
                }else
                {
                    int selectedstartmins = Integer.parseInt(startmins.getSelectedItem().toString());
                    String[] mins_end = new String[60 - selectedstartmins];
                    int x = selectedstartmins + 1;
                    for (int i = 0; i < 59 - selectedstartmins; i++) {
                        mins_end[i] = String.valueOf(x);
                        x++;
                    }
                    ArrayAdapter<String> adapter_end = new ArrayAdapter<String>(getActivity(),
                            android.R.layout.simple_spinner_item, mins_end);
                    endmins.setAdapter(adapter_end);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add(view, dialog);
                //dialog.dismiss();
            }
        });
        dialog.show();
    }

    public void  add(View v, Dialog dialog)
    {   flag=0;
        int start=Integer.parseInt(starthours.getSelectedItem().toString())*60 + Integer.parseInt(startmins.getSelectedItem().toString());
        int end=Integer.parseInt(endhours.getSelectedItem().toString())*60 + Integer.parseInt(endmins.getSelectedItem().toString());
        long startmilli= TimeUnit.MINUTES.toMillis(start);
        long endmilli=TimeUnit.MINUTES.toMillis(end);
        if(info.getText().toString().equals(""))
        {
            Toast.makeText(getActivity(), "add schedule info ", Toast.LENGTH_LONG).show();
        }else if(checkTimeClash(startmilli,endmilli))
        {
            Toast.makeText(getActivity(), "selected time overlaps with other schedule in this or some other institute", Toast.LENGTH_LONG).show();
        }else
        {

            Schedule newSchedule=new Schedule(day,startmilli,endmilli,info.getText().toString());
            scheduleslt.add(newSchedule);

            //add to the server

            databaseReference=Constants.databaseReference.child(Constants.SCHEDULES_TABLE).child(firebaseAuth.getCurrentUser().getUid()).child(day).push();
            databaseReference.child("info").setValue(info.getText().toString());
            databaseReference.child("start_time").setValue(String.valueOf(startmilli));
            databaseReference.child("end_time").setValue(String.valueOf(endmilli));

            UserPrefs.schedulekeymap.put(newSchedule,databaseReference.getKey());     //reflecting to local data


            Toast.makeText(getActivity(), "schedule added ", Toast.LENGTH_LONG).show();
            dialog.dismiss();
            Intent reload=new Intent(getActivity(), com.project.smartedu.common.Schedule.class);
            reload.putExtra("institutionName",institutionName);
            reload.putExtra("day", day);
            reload.putExtra("role", role);
            startActivity(reload);
        }
    }








    public boolean checkTimeClash(long start,long end)
    {   // Log.d("user", "checking and flag = " + String.valueOf(flag));
        final int[] check = new int[1];


        for (int i = 0; i < scheduleslt.size(); i++) {

            Schedule schedule = scheduleslt.get(i);
            long ret_start = schedule.getStart_time();
            long ret_end = schedule.getEnd_time();
            if (start >= ret_start && start < ret_end) {
                //flag=1;
                check[0] = 1;
                return true;
                //break;
            }
            if (end > ret_start && end <= ret_end) {
                //Schedule_days.this.flag = 1;
                check[0] = 1;
                return true;
                // break;
            }
            if (start < ret_start && end > ret_end) {
                Schedule_days.this.flag = 1;
                check[0] = 1;
                return true;
                // break;
            }
        }



        return false;
    }


}



