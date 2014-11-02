package com.ckroetsch.fundamental.app;

import android.graphics.PorterDuff;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ckroetsch.fundamental.event.AccountInfoLoadedEvent;
import com.ckroetsch.fundamental.event.BusSingleton;
import com.ckroetsch.fundamental.inject.FundamentalModule;
import com.ckroetsch.fundamental.model.Response;
import com.ckroetsch.fundamental.network.FundamentalAPI;
import com.google.inject.Inject;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GraphViewDataInterface;
import com.jjoe64.graphview.GraphViewSeries;
import com.jjoe64.graphview.GraphViewStyle;
import com.jjoe64.graphview.LineGraphView;
import com.squareup.otto.Subscribe;

import java.text.NumberFormat;

import retrofit.Callback;
import retrofit.RetrofitError;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * @author curtiskroetsch
 */
public class ProjectionsFragment extends RoboFragment {

    private static final String KEY_INTEREST = "interest";
    private static final String KEY_ALLOWANCE = "allowance";

    private static final int WEEKS = 104;

    @InjectView(R.id.layout)
    LinearLayout mLayout;

    @InjectView(R.id.slider)
    SeekBar mSlider;

    @InjectView(R.id.selectedValue)
    TextView mSelectedValue;

    @InjectView(R.id.allowanceHigh)
    TextView mAllowanceHigh;

    @InjectView(R.id.statement)
    TextView mStatement;

    @InjectView(R.id.save)
    Button mSaveButton;

    @Inject
    FundamentalAPI mFundAPI;

    public double mAllowance;
    public double mInterestRate;
    public double mSavingsAmount;

    GraphViewSeries mInterestSeries;
    GraphViewSeries mRegularSeries;
    GraphView mGraphView;

    public static ProjectionsFragment createInstance() {
        final ProjectionsFragment fragment = new ProjectionsFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusSingleton.get().register(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_projections, container, false);
    }


    private GraphViewDataInterface[] getSeriesWithInterest(double allowance) {
        final GraphViewDataInterface[] data = new GraphViewDataInterface[WEEKS];
        for (int i = 0; i < WEEKS; i++) {
            data[i] = new GraphView.GraphViewData(i, calcPresentValue(allowance, i));
        }
        return data;
    }

    private double calcPresentValue(double allowance, int n) {
        return allowance * (Math.pow(1f + mInterestRate,n) - 1f)/ mInterestRate;
    }

    private GraphViewDataInterface[] getSeriesWithoutInterest(double allowance) {
        final GraphViewDataInterface[] data = new GraphViewDataInterface[WEEKS];
        for (int week = 0; week < WEEKS; week++) {
            data[week] = new GraphView.GraphViewData(week, allowance * week);
        }
        return data;
    }

    private String toCurrency(double val) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(val);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LayerDrawable ld = (LayerDrawable) mSlider.getProgressDrawable();
        ScaleDrawable d1 = (ScaleDrawable) ld.findDrawableByLayerId(android.R.id.progress);
        d1.setColorFilter(getResources().getColor(R.color.blue), PorterDuff.Mode.SRC_IN);
        mSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mFundAPI.setSavingsAmount(FundamentalModule.USERNAME, (double)mSlider.getProgress(), new Callback<Response>() {
                    @Override
                    public void success(Response response, retrofit.client.Response response2) {
                        Log.d("FUND", "success: " + response.success);
                        AccountFragment.syncWithServer(mFundAPI);
                        Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e("FUND", "failure: " + error.getMessage());
                    }
                });
            }
        });

        mSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mSelectedValue.setText("$" + i);
                mRegularSeries.resetData(getSeriesWithoutInterest(i));
                mInterestSeries.resetData(getSeriesWithInterest(i));

                final double diff = calcPresentValue(i, WEEKS) - i * WEEKS;
                final String withArgs = getString(R.string.projections, toCurrency(i), "2 years", toCurrency(diff));
                mStatement.setText(Html.fromHtml(withArgs), TextView.BufferType.SPANNABLE);
                mSaveButton.setText(getString(R.string.save_button, i));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Subscribe
    public void onAccountLoaded(AccountInfoLoadedEvent event) {
        mSavingsAmount = event.accountInfo.savings_amount;
        mAllowance = event.accountInfo.allowance_amount;
        mInterestRate = (event.accountInfo.savings_interest_rate / 100) / 52;
        mInterestSeries = new GraphViewSeries(((int)(1 * event.accountInfo.savings_interest_rate)) + "% interest", null, getSeriesWithInterest(mAllowance / 2));
        mRegularSeries = new GraphViewSeries("No interest", null, getSeriesWithoutInterest(mSavingsAmount));
        Log.d("FUND", "mAllownace = " + mAllowance);
        Log.d("FUND", "mInterestRate = " + mInterestRate);
        updateView();
    }

    private void updateView() {
        mAllowanceHigh.setText("$" + mAllowance);
        mSlider.setMax((int) mAllowance);
        mSlider.setProgress((int) mSavingsAmount);
        mGraphView = new LineGraphView(getActivity(), "");

        styleSeries(mRegularSeries, R.color.red);
        styleSeries(mInterestSeries, R.color.green);

        mGraphView.addSeries(mRegularSeries);
        mGraphView.addSeries(mInterestSeries);
        mGraphView.setScalable(true);
        mGraphView.setScrollable(false);
        mGraphView.getGraphViewStyle().setGridStyle(GraphViewStyle.GridStyle.NONE);
        mGraphView.getGraphViewStyle().setNumVerticalLabels(5);
        mGraphView.getGraphViewStyle().setTextSize(30);
        mGraphView.getGraphViewStyle().setVerticalLabelsWidth(100);
        mGraphView.getGraphViewStyle().useTextColorFromTheme(getActivity());
        mGraphView.setShowLegend(true);
        mGraphView.setLegendAlign(GraphView.LegendAlign.BOTTOM);
        mGraphView.getGraphViewStyle().setLegendMarginBottom(20);
        mGraphView.setLegendWidth(220);

        final double max = calcPresentValue(mAllowance, WEEKS);
        mGraphView.setManualYAxisBounds(max + 1, 0);

        mGraphView.setHorizontalLabels(new String[] {
                "0M", "6M", "12M", "18M", "24M"
        });
        mLayout.addView(mGraphView);

        final String withArgs = getString(R.string.projections, toCurrency(mSavingsAmount), "2 years", toCurrency(calcPresentValue(mSavingsAmount, 104)));
        mStatement.setText(Html.fromHtml(withArgs), TextView.BufferType.SPANNABLE);
        mSaveButton.setText(getString(R.string.save_button, (int) mSavingsAmount));
    }


    private void styleSeries(GraphViewSeries series, int colorId) {
        series.getStyle().thickness = 8;
        series.getStyle().color = getResources().getColor(colorId);
    }

    @Override
    public void onDestroy() {
        BusSingleton.get().unregister(this);
        super.onDestroy();
    }
}
