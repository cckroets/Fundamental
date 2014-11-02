package com.ckroetsch.fundamental.app;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class TabFragment extends RoboFragment {

    public static TabFragment createInstance() {
        final TabFragment fragment = new TabFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @InjectView(R.id.pager)
    ViewPager mViewPager;

    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mTabs;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tabs, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager.setAdapter(new TabsAdapter(getChildFragmentManager()));
        mViewPager.setOffscreenPageLimit(3);
        mTabs.setViewPager(mViewPager);
        mTabs.setIndicatorColor(getResources().getColor(R.color.blue_dark));
        mTabs.setIndicatorHeight(10);
        mTabs.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/Action_Man.ttf"), 0);

    }

    public class TabsAdapter extends FragmentPagerAdapter {

        public TabsAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0: return TutorialFragment.createInstance();
                case 1: return AccountFragment.createInstance();
                case 2: return ProjectionsFragment.createInstance();
                case 3: return InvestmentsFragment.createInstance();
                default: return null;
            }
        }


        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0: return "Tutorials";
                case 1: return "Account Info";
                case 2: return "Savings Calculator";
                case 3: return "Investments";
                default: return "";
            }
        }

        @Override
        public int getCount() {
            return 4;
        }
    }
}
