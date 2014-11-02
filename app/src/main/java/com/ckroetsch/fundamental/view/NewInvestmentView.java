package com.ckroetsch.fundamental.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ckroetsch.fundamental.app.AccountFragment;
import com.ckroetsch.fundamental.app.InvestmentsFragment;
import com.ckroetsch.fundamental.app.R;
import com.ckroetsch.fundamental.inject.FundamentalModule;
import com.ckroetsch.fundamental.model.NewInvestment;
import com.ckroetsch.fundamental.model.Response;
import com.ckroetsch.fundamental.network.FundamentalAPI;
import com.google.inject.Inject;

import retrofit.Callback;
import retrofit.RetrofitError;
import roboguice.RoboGuice;

/**
 * @author curtiskroetsch
 */
public class NewInvestmentView extends LinearLayout {

    TextView mName;
    TextView mTerm;
    TextView mInterest;
    View mAddButton;

    String mId;

    @Inject
    FundamentalAPI mAPI;

    public NewInvestmentView(Context context) {
        this(context, null);
    }

    public NewInvestmentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewInvestmentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        if (isInEditMode()) {
            return;
        }
        RoboGuice.getInjector(context).injectMembersWithoutViews(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        mName = (TextView) findViewById(R.id.name);
        mTerm = (TextView) findViewById(R.id.term);
        mInterest = (TextView) findViewById(R.id.interest);
        mAddButton = findViewById(R.id.add);
    }

    public void bind(final NewInvestment investment) {
        mId = investment.investment_id;
        mName.setText(investment.name);
        mTerm.setText(investment.term_length + " months");
        mInterest.setText((int) (1 * investment.rate) + "%");

        mAddButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(getContext())
                        .setTitle("Invest in " + investment.name)
                        .setMessage("Choose how much money you would like to invest")
                        .setView(input)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                double value;
                                try {
                                    value = Double.parseDouble(input.getText().toString());
                                } catch (NumberFormatException e) {
                                    dialogInterface.cancel();
                                    Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                mAPI.invest(FundamentalModule.USERNAME,
                                        investment.investment_id, value,
                                        new Callback<Response>() {
                                            @Override
                                            public void success(Response response, retrofit.client.Response response2) {
                                                dialogInterface.dismiss();
                                                AccountFragment.syncWithServer(mAPI);
                                                InvestmentsFragment.syncWithServer(mAPI);
                                                Toast.makeText(getContext(), "Investment successful", Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                dialogInterface.cancel();
                                                Log.e("FUND", error.getMessage());
                                                Toast.makeText(getContext(), "Investment failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        })
                        .create().show();

                //mAPI.invest(FundamentalModule.USERNAME, mId, );
            }
        });
    }
}
