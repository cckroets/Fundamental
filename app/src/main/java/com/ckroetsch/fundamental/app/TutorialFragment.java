package com.ckroetsch.fundamental.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import roboguice.fragment.RoboFragment;

/**
 * @author curtiskroetsch
 */
public class TutorialFragment extends RoboFragment {

    public static TutorialFragment createInstance() {
        return new TutorialFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tutorial, container, false);
    }
}
