package com.project.smartedu.teacher;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.smartedu.R;



public class student_result extends Fragment {

    String studentid;
    String classId;
    String institutionName;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View android = inflater.inflate(R.layout.fragment_student_result, container, false);
        studentid= getArguments().getString("id");
        classId=getArguments().getString("classId");
        institutionName=getArguments().getString("institution_name");

        return android;
    }

}
