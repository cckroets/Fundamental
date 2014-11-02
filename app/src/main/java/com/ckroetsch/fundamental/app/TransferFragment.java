package com.ckroetsch.fundamental.app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import roboguice.fragment.RoboFragment;

/**
 * @author curtiskroetsch
 */
public class TransferFragment extends RoboFragment {

    public static TransferFragment createInstance() {
        TransferFragment transferFragment = new TransferFragment();
        final Bundle args = new Bundle();
        transferFragment.setArguments(args);
        return transferFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


}
