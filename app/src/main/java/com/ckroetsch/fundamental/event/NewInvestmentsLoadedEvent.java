package com.ckroetsch.fundamental.event;

import com.ckroetsch.fundamental.model.NewInvestment;

import java.util.List;

/**
 * @author curtiskroetsch
 */
public class NewInvestmentsLoadedEvent {

    public List<NewInvestment> investments;

    public NewInvestmentsLoadedEvent(List<NewInvestment> i) {
        investments = i;
    }
}
