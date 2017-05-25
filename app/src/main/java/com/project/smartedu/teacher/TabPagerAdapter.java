package com.project.smartedu.teacher;

/**
 * Created by Shubham Bhasin on 20-Mar-17.
 */
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class TabPagerAdapter extends FragmentStatePagerAdapter {
    String id;
    String classId;
    String institution_code;
    String  institutionName;
    private String fragments[] ={"Info","Attendance","Result"};
    String role;
    public TabPagerAdapter(FragmentManager fm, String id, String classId,String institutionName,String role) {
        super(fm);
        this.id=id;
        this.classId=classId;
        this.institutionName=institutionName;
        this.role=role;
        // TODO Auto-generated constructor stub
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle=new Bundle();
        bundle.putString("institution_name",institutionName);
        bundle.putString("id",id);
        bundle.putString("classId",classId);
        bundle.putString("role",role);
        switch (i) {
            case 0:
                //Fragement for student information
                Fragment student_info = new student_info();
                student_info.setArguments(bundle);
                return student_info;
            case 1:
                //Fragment for attendance
                Fragment student_attendance=new student_attendance();
                student_attendance.setArguments(bundle);
                return  student_attendance;
            case 2:
                //Fragment for result
                Fragment student_result=new student_result();
                student_result.setArguments(bundle);
                return  student_result;
        }
        return null;

    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return fragments.length; //No of Tabs
    }

    @Override
    public CharSequence getPageTitle(int position){
        return fragments[position];
    }

}