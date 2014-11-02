package com.ckroetsch.fundamental.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ckroetsch.fundamental.app.R;
import com.ckroetsch.fundamental.inject.FundamentalModule;

/**
 * @author curtiskroetsch
 */
public class AccountInvestment extends LinearLayout {

    TextView mName;
    TextView mAmount;

    public AccountInvestment(Context context) {
        this(context, null);
    }

    public AccountInvestment(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AccountInvestment(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mName = (TextView) findViewById(R.id.name);
        mAmount = (TextView) findViewById(R.id.amount);
    }


    public void bind(String name, double amount) {
        mName.setText(name);
        mAmount.setText(FundamentalModule.toCurrency(amount));
    }

}
