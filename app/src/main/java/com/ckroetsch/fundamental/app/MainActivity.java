package com.ckroetsch.fundamental.app;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;

import roboguice.activity.RoboFragmentActivity;


public class MainActivity extends RoboFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            openTabs();
        }
    }

    public void openStartFragment() {

    }

    public void openAccountFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, AccountFragment.createInstance());
        transaction.commit();
    }

    final static double interest = 0.2f / 52f;
    final static int allowance = 10;

    public void openTabs() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, TabFragment.createInstance());
        transaction.commit();
    }

}
