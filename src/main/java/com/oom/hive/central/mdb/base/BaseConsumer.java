package com.oom.hive.central.mdb.base;

import java.io.IOException;

public interface BaseConsumer {
    public void handleMessage(String messageData) ;
}
