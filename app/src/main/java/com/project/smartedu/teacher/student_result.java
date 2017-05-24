package com.project.smartedu.teacher;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.project.smartedu.Constants;
import com.project.smartedu.R;
import com.project.smartedu.database.Exam;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;


public class student_result extends Fragment {

    String studentid;
    String classId;
    String institutionName;

    DatabaseReference databaseReference;




    ListView examsubjectLt;
    ListView examdetailLt;


    TeacherUserPrefs teacherUserPrefs;

    private class ExamResultItems extends AsyncTask<Void, Void, Void> {

        private Context async_context;
        private ProgressDialog pd;

        public ExamResultItems(Context context){
            this.async_context = context;
            pd = new ProgressDialog(async_context);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Fetching Exams Data");
            pd.setCancelable(false);
            pd.show();
            databaseReference= Constants.databaseReference.child(Constants.STUDENTS_TABLE).child(institutionName).child(studentid).child("exam");
            TeacherUserPrefs.exammarksobtMap.clear();
            TeacherUserPrefs.examMap.clear();
        }

        @Override
        protected Void doInBackground(Void... params) {
            final Object lock = new Object();

            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    synchronized (lock) {
                        for(DataSnapshot ds:dataSnapshot.getChildren()) {

                            Log.d("nowdata","sname = " + ds.getKey());          //gives subject


                            String subject=ds.getKey();

                            for(DataSnapshot dataSnapshot1:ds.getChildren()){
                                    //dataSnapshot1.getKey() gives exam id

                                Exam exam=new Exam();
                                exam.setId(dataSnapshot1.getKey());
                                exam.setSubject(subject);

                                    for(DataSnapshot dataSnapshot2:dataSnapshot1.getChildren()){
                                        //dataSnapshot2.getKey() gives exam details

                                            if(dataSnapshot2.getKey().equalsIgnoreCase("name")){
                                                exam.setName(dataSnapshot2.getValue().toString());
                                            }

                                            if(dataSnapshot2.getKey().equalsIgnoreCase("date")){
                                                exam.setDate(dataSnapshot2.getValue().toString());
                                            }

                                            if(dataSnapshot2.getKey().equalsIgnoreCase("max_marks")){
                                                exam.setMax_marks(dataSnapshot2.getValue().toString());
                                            }

                                            if(dataSnapshot2.getKey().equalsIgnoreCase("marks_obtained")){
                                                    TeacherUserPrefs.exammarksobtMap.put(dataSnapshot1.getKey(),dataSnapshot2.getValue().toString());
                                            }

                                    }

                                TeacherUserPrefs.examMap.put(dataSnapshot1.getKey(),exam);


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
            teacherUserPrefs.setFirstMarksLoading(false);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
//                   Toast.makeText(async_context,userPrefs.getUserName(),Toast.LENGTH_LONG).show();
                    setExamSubjectList();
                    // noti_bar.setTexts(userPrefs.getUserName(), role,institutionName);
                    pd.dismiss();

                }
            }, 500);  // 100 milliseconds


        }
        //end firebase_async_class
    }




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View android = inflater.inflate(R.layout.fragment_student_result, container, false);
        studentid= getArguments().getString("id");
        classId=getArguments().getString("classId");
        institutionName=getArguments().getString("institution_name");


        teacherUserPrefs=new TeacherUserPrefs(getContext());
        examsubjectLt=(ListView)android.findViewById(R.id.exam_list);
        if(teacherUserPrefs.getFirstMarksLoading()) {
            ExamResultItems examResultItems = new ExamResultItems(getContext());
            examResultItems.execute();
        }else {
            setExamSubjectList();
        }

        return android;
    }




    public void setExamSubjectList(){

        if (TeacherUserPrefs.examMap.size()==0){
            Toast.makeText(getContext(),"No exam result uploaded",Toast.LENGTH_LONG).show();
        }else{

            ArrayList<String> examsubjectitems=new ArrayList<>();

            for (String examid:TeacherUserPrefs.examMap.keySet()){


                Exam exam=TeacherUserPrefs.examMap.get(examid);


                if(!examsubjectitems.contains(exam.getSubject())){
                    examsubjectitems.add(exam.getSubject());
                }



            }

            ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, examsubjectitems);
            examsubjectLt.setAdapter(adapter);



            examsubjectLt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String selectedSubject=((TextView) view).getText().toString();


                    final Dialog dialog=new Dialog(getContext());
                    dialog.setContentView(R.layout.list_view_dialog);
                   examdetailLt=(ListView) dialog.findViewById(R.id.detail_list);
                    //setDialogSize(dialog);
                    dialog.setTitle("Exams");

                    dialog.show();

                    ArrayList<String> exammarksitems=new ArrayList<>();

                    for (String examId:TeacherUserPrefs.examMap.keySet()){

                        Exam exam=TeacherUserPrefs.examMap.get(examId);

                        if(exam.getSubject().equalsIgnoreCase(selectedSubject)){

                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String dateString = formatter.format(new Date(Long.parseLong(exam.getDate())));

                            String entry=exam.getName() + " on " + dateString + "\nMaximum Marks = " + exam.getMax_marks() +"\nMarks Obtained = " + TeacherUserPrefs.exammarksobtMap.get(exam.getId());

                            exammarksitems.add(entry);
                        }



                    }

                    ArrayAdapter marksadapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, exammarksitems);
                    examdetailLt.setAdapter(marksadapter);


                    dialog.show();




                }
            });



        }

    }


    public void setDialogSize(Dialog dialogcal){
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogcal.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.CENTER;

        dialogcal.getWindow().setAttributes(lp);

    }

}
