package com.project.smartedu.common;

/**
 * Created by Shubham Bhasin on 14-Feb-17.
 */


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;


public class ScheduleTabsAdapter extends FragmentStatePagerAdapter {
    String role;
    String institution_name;

    private String fragments[] ={"Mon","Tues","Wed","Thu","Fri","Sat","Sun"};
    public ScheduleTabsAdapter(FragmentManager fm,String role,String institution_name) {
        super(fm);
        this.role=role;

        this.institution_name=institution_name;
        // TODO Auto-generated constructor stub


        Log.d("const", this.institution_name);
    }

    @Override
    public Fragment getItem(int i) {
        Bundle bundle=new Bundle();
        bundle.putString("role", role);
        bundle.putString("institution_name",institution_name);

        // Fragment day = new Schedule_days();
        switch (i) {
            case 0:
                //Fragement for student information
                bundle.putString("day","Monday");
                Fragment monday = new Schedule_days();
                monday.setArguments(bundle);
                return monday;
            case 1:
                //Fragment for attendance
                bundle.putString("day","Tuesday");
                Fragment tuesday=new Schedule_days();
                tuesday.setArguments(bundle);
                return  tuesday;
            case 2:
                //Fragment for result
                bundle.putString("day","Wednesday");
                Fragment wednesday=new Schedule_days();
                wednesday.setArguments(bundle);
                return  wednesday;
            case 3:
                //Fragement for student information
                bundle.putString("day","Thursday");
                Fragment thursday = new Schedule_days();
                thursday.setArguments(bundle);
                return thursday;
            case 4:
                //Fragment for attendance
                bundle.putString("day","Friday");
                Fragment friday=new Schedule_days();
                friday.setArguments(bundle);
                return  friday;
            case 5:
                //Fragment for result
                bundle.putString("day","Saturday");
                Fragment saturday=new Schedule_days();
                saturday.setArguments(bundle);
                return  saturday;
            case 6:
                //Fragment for result
                bundle.putString("day","Sunday");
                Fragment sunday=new Schedule_days();
                sunday.setArguments(bundle);
                return  sunday;
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