package com.ckroetsch.fundamental.event;

import com.ckroetsch.fundamental.model.MyInvestment;

import java.util.List;

/**
 * @author curtiskroetsch
 */
public class MyInvestmentsLoadedEvent {

    public List<MyInvestment> investments;

    public MyInvestmentsLoadedEvent(List<MyInvestment> i) {
        investments = i;
    }
}
