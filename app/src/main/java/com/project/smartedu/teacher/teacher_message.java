package com.project.smartedu.teacher;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.project.smartedu.R;

public class teacher_message extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_message);
    }

   /* @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent task_intent = new Intent(teacher_message.this, Classes.class);
        task_intent.putExtra("institution_name", institutionName);
        task_intent.putExtra("for","attendance");
        task_intent.putExtra("role", role);
        //task_intent.putExtra("id", classId);
        startActivity(task_intent);
    }*/
}
