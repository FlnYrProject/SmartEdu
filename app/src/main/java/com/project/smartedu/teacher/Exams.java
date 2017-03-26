package com.project.smartedu.teacher;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

import com.project.smartedu.R;

public class Exams extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exams);
    }

  /*  @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent task_intent = new Intent(Exams.this, Classes.class);
        task_intent.putExtra("institution_name", institutionName);
        task_intent.putExtra("for","attendance");
        task_intent.putExtra("role", role);
        //task_intent.putExtra("id", classId);
        startActivity(task_intent);
    }*/

}
