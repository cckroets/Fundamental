package com.ckroetsch.fundamental.event;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author curtiskroetsch
 */
public class BusSingleton {

    final static Bus INSTANCE = new Bus(ThreadEnforcer.MAIN);

    public static Bus get() {
        return INSTANCE;
    }
}
