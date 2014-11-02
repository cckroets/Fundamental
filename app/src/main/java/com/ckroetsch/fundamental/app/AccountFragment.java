package com.ckroetsch.fundamental.app;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ckroetsch.fundamental.event.AccountInfoLoadedEvent;
import com.ckroetsch.fundamental.event.BusSingleton;
import com.ckroetsch.fundamental.event.MyInvestmentsLoadedEvent;
import com.ckroetsch.fundamental.inject.FundamentalModule;
import com.ckroetsch.fundamental.model.AccountInfo;
import com.ckroetsch.fundamental.model.MyInvestment;
import com.ckroetsch.fundamental.model.MyInvestmentsList;
import com.ckroetsch.fundamental.network.FundamentalAPI;
import com.ckroetsch.fundamental.view.AccountInvestment;
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
public class AccountFragment extends RoboFragment {

    static float total = 0;

    @InjectView(R.id.savings)
    TextView mSavingsView;

    @InjectView(R.id.savingsTitle)
    TextView mSavingsTitle;

    @InjectView(R.id.allowance)
    TextView mAllowanceView;

    @InjectView(R.id.allowanceDate)
    TextView mAllowanceDateView;

    @InjectView(R.id.savingsAmount)
    TextView mSavingsAmount;

    @InjectView(R.id.chequing)
    TextView mChequingView;

    @InjectView(R.id.investments)
    LinearLayout mInvestmentsContainer;

    @InjectView(R.id.total)
    TextView mTotalView;

    @InjectView(R.id.takeLoan)
    View mTakeLoanButton;

    @InjectView(R.id.payLoan)
    View mPayLoanButton;

    @InjectView(R.id.debt)
    TextView mDebtView;

    @InjectView(R.id.debtTitle)
    TextView mDebtTitle;

    @InjectView(R.id.cashOut)
    View mCashOut;

    @InjectView(R.id.transfer)
    View mTransferButton;

    @InjectView(R.id.nextPayment)
    TextView mNextLoanPayment;

    @Inject
    FundamentalAPI mAPI;

    @Inject
    LayoutInflater mInflater;

    AccountInfo mAccountInfo;

    List<MyInvestment> mMyInvestments;

    Handler mHandler;

    public static AccountFragment createInstance() {
        final AccountFragment fragment = new AccountFragment();
        final Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public static void syncWithServer(FundamentalAPI api) {
        api.getInfo(FundamentalModule.USERNAME, new Callback<AccountInfo>() {
            @Override
            public void success(final AccountInfo accountInfo, Response response) {
                Log.d("FUND", "Account info success");
                BusSingleton.get().post(new AccountInfoLoadedEvent(accountInfo));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("FUND", error.getMessage());
            }
        });
        api.getMyInvestments(FundamentalModule.USERNAME, new Callback<MyInvestmentsList>() {
            @Override
            public void success(MyInvestmentsList myInvestments, Response response) {
                Log.d("FUND", "Got my investments");
                BusSingleton.get().post(new MyInvestmentsLoadedEvent(myInvestments.List));
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e("FUND", error.getMessage());
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BusSingleton.get().register(this);
        mHandler = new Handler(getActivity().getMainLooper());
        total = 0;
        syncWithServer(mAPI);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    int mOption = 0;

    double mTransferAmount;

    double mLoanAmount;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPayLoanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(getActivity());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Pay your loan")
                        .setMessage("Enter the amount of money you would like to pay off")
                        .setView(editText)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    mLoanAmount = Double.parseDouble(editText.getText().toString());
                                } catch (NumberFormatException e) {
                                    dialogInterface.cancel();
                                    Toast.makeText(getActivity(), "Invalid payment amount", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                mAPI.takeLoan(FundamentalModule.USERNAME, mLoanAmount, new Callback<com.ckroetsch.fundamental.model.Response>() {
                                    @Override
                                    public void success(com.ckroetsch.fundamental.model.Response response, Response response2) {
                                        Toast.makeText(getActivity(), "Loan Payment made", Toast.LENGTH_SHORT).show();
                                        mAccountInfo.debt_amount -= mLoanAmount;
                                        mAccountInfo.chequing_balance -= mLoanAmount;
                                        total -= mLoanAmount;
                                        updateInfo();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Toast.makeText(getActivity(), "Loan Could not be paid", Toast.LENGTH_SHORT).show();
                                        Log.e("FUND", error.getMessage());
                                    }
                                });
                            }
                        }).create().show();
            }
        });

        mTakeLoanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(getActivity());
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Take out a loan")
                        .setMessage("Enter the amount of money you would like to take out a loan for")
                        .setView(editText)
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    mLoanAmount = Double.parseDouble(editText.getText().toString());
                                } catch (NumberFormatException e) {
                                    dialogInterface.cancel();
                                    Toast.makeText(getActivity(), "Invalid loan amount", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                mAPI.takeLoan(FundamentalModule.USERNAME, mLoanAmount, new Callback<com.ckroetsch.fundamental.model.Response>() {
                                    @Override
                                    public void success(com.ckroetsch.fundamental.model.Response response, Response response2) {
                                        Toast.makeText(getActivity(), "Loan Granted", Toast.LENGTH_SHORT).show();
                                        mAccountInfo.debt_amount += mLoanAmount;
                                        mAccountInfo.chequing_balance += mLoanAmount;
                                        total += mLoanAmount;
                                        updateInfo();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Toast.makeText(getActivity(), "Loan Could not be taken", Toast.LENGTH_SHORT).show();
                                        Log.e("FUND", error.getMessage());
                                    }
                                });
                            }
                        }).create().show();
            }
        });

        mCashOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String msg = getString(R.string.cashOut, (int) (1 * mAccountInfo.chequing_balance));
                new AlertDialog.Builder(getActivity())
                        .setTitle("Cash out")
                        .setMessage(msg)
                        .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAPI.withdraw(FundamentalModule.USERNAME, mAccountInfo.chequing_balance,
                                        new Callback<com.ckroetsch.fundamental.model.Response>() {
                                            @Override
                                            public void success(com.ckroetsch.fundamental.model.Response response, Response response2) {
                                                Log.d("FUND", "Cash out success");
                                                Toast.makeText(getActivity(), "Cash out successful", Toast.LENGTH_SHORT);
                                            }

                                            @Override
                                            public void failure(RetrofitError error) {
                                                Log.d("FUND", "Cash out fail: " + error.getMessage());
                                                Toast.makeText(getActivity(), "Cash out failed", Toast.LENGTH_SHORT);
                                            }
                                        });
                            }
                        }).setNegativeButton("Cancel", null).create().show();
            }
        });

        mTransferButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(getActivity());
                final TextView textView = new TextView(getActivity());
                textView.setText("Enter amount:");
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);

                final LinearLayout layout = new LinearLayout(getActivity());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
                params2.setMargins(0, 0, 80, 0);
                params2.weight = 1;
                params.weight = 1;
                layout.addView(textView, params);
                layout.addView(editText, params2);
                layout.setPadding(80, 0, 0, 80);
                new AlertDialog.Builder(getActivity())
                        .setTitle("Transfer Funds")
                        .setView(layout)
                        .setSingleChoiceItems(R.array.transfers, 0,  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mOption = i;
                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .setPositiveButton("Transfer", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                final Callback<com.ckroetsch.fundamental.model.Response> callback = new Callback<com.ckroetsch.fundamental.model.Response>() {
                                    @Override
                                    public void success(com.ckroetsch.fundamental.model.Response response, Response response2) {
                                        Toast.makeText(getActivity(), "Money transferred", Toast.LENGTH_SHORT).show();
                                        if (mOption == 0) {
                                            mAccountInfo.savings_balance -= mTransferAmount;
                                            mAccountInfo.chequing_balance += mTransferAmount;
                                        } else if (mOption == 1) {
                                            mAccountInfo.savings_balance += mTransferAmount;
                                            mAccountInfo.chequing_balance -= mTransferAmount;
                                        }
                                        updateInfo();
                                        dialogInterface.dismiss();
                                    }

                                    @Override
                                    public void failure(RetrofitError error) {
                                        Log.e("FUND", error.getMessage());
                                        Toast.makeText(getActivity(), "Could not transfer money", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                };

                                if (mOption < 0) {
                                    Toast.makeText(getActivity(), "You must choose a transfer method", Toast.LENGTH_SHORT).show();
                                    return;
                                } else if (mOption == 0) {
                                    try {
                                        mTransferAmount = Double.parseDouble(editText.getText().toString());
                                    } catch (NumberFormatException e) {
                                        dialogInterface.cancel();
                                        Toast.makeText(getActivity(), "Invalid transfer amount", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    mAPI.transferToChequing(FundamentalModule.USERNAME, mTransferAmount, callback);
                                } else if (mOption == 1) {
                                    try {
                                        mTransferAmount = Double.parseDouble(editText.getText().toString());
                                    } catch (NumberFormatException e) {
                                        dialogInterface.cancel();
                                        Toast.makeText(getActivity(), "Invalid transfer amount", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    mAPI.transferToSavings(FundamentalModule.USERNAME, mTransferAmount, callback);
                                }
                            }
                        }).create().show();
            }
        });
    }

    @Subscribe
    public void onAccountLoaded(final AccountInfoLoadedEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mAccountInfo = event.accountInfo;
                updateInfo();
            }
        });
    }

    @Subscribe
    public void onMyInvestmentsLoaded(final MyInvestmentsLoadedEvent event) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                mMyInvestments = event.investments;
                updateInvestments();
            }
        });
    }

    @Override
    public void onDestroy() {
        BusSingleton.get().unregister(this);
        super.onDestroy();
    }

    public void updateInfo() {
        mSavingsView.setText(FundamentalModule.toCurrency(mAccountInfo.savings_balance));
        mChequingView.setText(FundamentalModule.toCurrency(mAccountInfo.chequing_balance));
        mSavingsTitle.setText(String.format("Savings (%d", (int) (1 * mAccountInfo.savings_interest_rate)) + "%)");
        mSavingsAmount.setText(FundamentalModule.toCurrency(mAccountInfo.savings_amount));
        mAllowanceView.setText(FundamentalModule.toCurrency(mAccountInfo.allowance_amount));
        mAllowanceDateView.setText("Nov. 2, 2014");
        mDebtView.setText(FundamentalModule.toCurrency(mAccountInfo.debt_amount));
        mDebtTitle.setText(String.format("Debt (%d", (int) (1 * mAccountInfo.loan_interest_rate)) + "%)");
        mNextLoanPayment.setText(FundamentalModule.toCurrency(mAccountInfo.loan_interest_rate/100f * mAccountInfo.debt_amount));
        total += mAccountInfo.savings_balance + mAccountInfo.chequing_balance;
        mTotalView.setText(FundamentalModule.toCurrency(total));
    }

    public void updateInvestments() {
        mInvestmentsContainer.removeAllViews();
        for (MyInvestment investment : mMyInvestments) {
            final AccountInvestment view = (AccountInvestment) mInflater.inflate(R.layout.view_investment_account, mInvestmentsContainer, false);
            view.bind(investment.name, investment.amount);
            mInvestmentsContainer.addView(view);
            total += investment.amount;
        }
        mTotalView.setText(FundamentalModule.toCurrency(total));
    }
}
