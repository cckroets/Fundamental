package com.ckroetsch.fundamental.app;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.ckroetsch.fundamental.event.BusSingleton;
import com.ckroetsch.fundamental.event.MyInvestmentsLoadedEvent;
import com.ckroetsch.fundamental.event.NewInvestmentsLoadedEvent;
import com.ckroetsch.fundamental.inject.FundamentalModule;
import com.ckroetsch.fundamental.model.MyInvestment;
import com.ckroetsch.fundamental.model.NewInvestment;
import com.ckroetsch.fundamental.model.NewInvestmentsList;
import com.ckroetsch.fundamental.network.FundamentalAPI;
import com.ckroetsch.fundamental.view.MyInvestmentView;
import com.ckroetsch.fundamental.view.NewInvestmentView;
import com.google.inject.Inject;
import com.squareup.otto.Subscribe;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class InvestmentsFragment extends RoboFragment {

    @InjectView(R.id.myInvestments)
    LinearLayout mMyInvestmentsContainer;

    @InjectView(R.id.newInvestments)
    LinearLayout mNewInvestmentsContainer;

    @Inject
    FundamentalAPI mAPI;

    @Inject
    LayoutInflater mInflater;

    Handler mHandler;

    List<MyInvestment> mMyInvestments;
    List<NewInvestment> mNewInvestments;

    public static InvestmentsFragment createInstance() {
        InvestmentsFragment fragment = new InvestmentsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void syncWithServer(FundamentalAPI api) {
        api.getNewInvestments(FundamentalModule.USERNAME, new Callback<NewInvestmentsList>() {
            @Override
            public void success(NewInvestmentsList newInvestmentsList, Response response) {
                Log.d("FUND", "New investments success");
                BusSingleton.get().post(new NewInvestmentsLoadedEvent(newInvestmentsList.List));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("FUND", error.getMessage());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHandler = new Handler(getActivity().getMainLooper());
        BusSingleton.get().register(this);
        syncWithServer(mAPI);
    }

    @Subscribe
    public void onNewInvestmentsLoaded(final NewInvestmentsLoadedEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mNewInvestments = event.investments;
                updateNewInvestmentsView();
            }
        });
    }

    @Subscribe
    public void onMyInvestmentsLoaded(final MyInvestmentsLoadedEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMyInvestments = event.investments;
                updateMyInvestmentsView();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_investments, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void updateNewInvestmentsView() {
        mNewInvestmentsContainer.removeAllViews();
        for (NewInvestment investment : mNewInvestments) {
            if (investment.is_purchased) {
                continue;
            }
            final NewInvestmentView view = (NewInvestmentView) mInflater.inflate(R.layout.view_new_investment, mNewInvestmentsContainer, false);
            view.bind(investment);
            mNewInvestmentsContainer.addView(view);
        }
    }


    public void updateMyInvestmentsView() {
        mMyInvestmentsContainer.removeAllViews();
        for (MyInvestment investment : mMyInvestments) {
            final MyInvestmentView view = (MyInvestmentView) mInflater.inflate(R.layout.view_my_investment, mMyInvestmentsContainer, false);
            view.bind(investment);
            mMyInvestmentsContainer.addView(view);
        }
    }

    @Override
    public void onDestroy() {
        BusSingleton.get().unregister(this);
        super.onDestroy();
    }
}
