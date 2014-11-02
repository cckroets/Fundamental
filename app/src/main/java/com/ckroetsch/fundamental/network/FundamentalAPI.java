package com.ckroetsch.fundamental.network;

import com.ckroetsch.fundamental.model.AccountInfo;
import com.ckroetsch.fundamental.model.MyInvestmentsList;
import com.ckroetsch.fundamental.model.NewInvestmentsList;
import com.ckroetsch.fundamental.model.Response;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * @author curtiskroetsch
 */
public interface FundamentalAPI {

    @FormUrlEncoded
    @POST("/account/savings_amount")
    public void setSavingsAmount(@Field("venmo_name") String venmoName,
                                 @Field("amount") Double amount,
                                 Callback<Response> listener);

    @FormUrlEncoded
    @POST("/account/withdraw")
    public void withdraw(@Field("venmo_name") String venmoName,
                         @Field("amount") Double amount,
                         Callback<Response> listener);

    @FormUrlEncoded
    @POST("/account/transfer_to_chequing")
    public void transferToChequing(@Field("venmo_name") String venmoName,
                                   @Field("amount") Double amount,
                                   Callback<Response> listener);

    @FormUrlEncoded
    @POST("/account/transfer_to_savings")
    public void transferToSavings(@Field("venmo_name") String venmoName,
                                  @Field("amount") Double amount,
                                  Callback<Response> listener);

    @GET("/account/info/{venmo_name}")
    public void getInfo(@Path("venmo_name") String venmoName,
                        Callback<AccountInfo> listener);

    @GET("/account/investment/current/{venmo_name}")
    public void getMyInvestments(@Path("venmo_name") String venmoName,
                                 Callback<MyInvestmentsList> listener);

    @GET("/account/investment/new/{venmo_name}")
    public void getNewInvestments(@Path("venmo_name") String venmoName,
                                 Callback<NewInvestmentsList> listener);

    @FormUrlEncoded
    @POST("/account/investment/current")
    public void invest(@Field("venmo_name") String venmoName,
                       @Field("investment_id") String investId,
                       @Field("amount") Double amount,
                       Callback<Response> listener);

    @FormUrlEncoded
    @POST("/account/take_loan")
    public void takeLoan(@Field("venmo_name") String venmoName,
                         @Field("amount") Double amount,
                         Callback<Response> listener);

    @FormUrlEncoded
    @POST("/account/pay_loan")
    public void payLoan(@Field("venmo_name") String venmoName,
                        @Field("amount") Double amount,
                        Callback<Response> listener);

    @FormUrlEncoded
    @PUT("/register")
    public void register(@Field("venmo_name") String venmoName,
                        @Field("user_name") String userName,
                        @Field("parent_venmo_name") String venmoParent,
                        @Field("parent_name") String parentName);


}
