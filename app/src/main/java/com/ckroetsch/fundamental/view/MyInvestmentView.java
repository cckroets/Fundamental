package com.ckroetsch.fundamental.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ckroetsch.fundamental.app.R;
import com.ckroetsch.fundamental.inject.FundamentalModule;
import com.ckroetsch.fundamental.model.MyInvestment;

/**
 * @author curtiskroetsch
 */
public class MyInvestmentView extends LinearLayout {

    TextView mName;
    TextView mAmount;
    TextView mEndAmount;
    TextView mEndDate;
    TextView mInterest;

    public MyInvestmentView(Context context) {
        this(context, null);
    }

    public MyInvestmentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyInvestmentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mName = (TextView) findViewById(R.id.name);
        mAmount = (TextView) findViewById(R.id.amount);
        mEndAmount = (TextView) findViewById(R.id.endAmount);
        mEndDate = (TextView) findViewById(R.id.endDate);
        mInterest = (TextView) findViewById(R.id.interest);
    }

    public void bind(MyInvestment investment) {
        mName.setText(investment.name);
        mAmount.setText(FundamentalModule.toCurrency(investment.amount));
        mEndAmount.setText(FundamentalModule.toCurrency(investment.final_amount));
        mEndDate.setText(investment.end_date);
        mInterest.setText((int)(1 * investment.rate) + "%");
    }
}
