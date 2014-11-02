package com.ckroetsch.fundamental.event;

import com.ckroetsch.fundamental.model.AccountInfo;

/**
 * @author curtiskroetsch
 */
public class AccountInfoLoadedEvent {

    public AccountInfo accountInfo;

    public AccountInfoLoadedEvent(AccountInfo i) {
        accountInfo = i;
    }
}
