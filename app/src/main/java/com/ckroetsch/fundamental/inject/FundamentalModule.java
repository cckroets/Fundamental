package com.ckroetsch.fundamental.inject;


import com.ckroetsch.fundamental.network.FundamentalAPI;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;

import java.text.NumberFormat;

import retrofit.RestAdapter;

/**
 * @author curtiskroetsch
 */
public class FundamentalModule extends AbstractModule {

    static final String ENDPOINT = "https://fundamental-money2020.herokuapp.com";

    public static final String USERNAME = "cfung";

    @Inject
    public FundamentalModule() {
        super();
    }

    @Override
    protected void configure() {
        bind(FundamentalAPI.class).toInstance(createAPI());
    }

    public FundamentalAPI createAPI() {
        final RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(ENDPOINT)
                .build();
        return restAdapter.create(FundamentalAPI.class);
    }

    public static String toCurrency(double val) {
        NumberFormat formatter = NumberFormat.getCurrencyInstance();
        return formatter.format(val);
    }

}


