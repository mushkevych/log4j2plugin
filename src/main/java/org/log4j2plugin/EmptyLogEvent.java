package org.log4j2plugin;

import org.apache.logging.log4j.core.AbstractLogEvent;

public class EmptyLogEvent extends AbstractLogEvent {
    private long currentTimeMillis;

    EmptyLogEvent() {
        this.currentTimeMillis = System.currentTimeMillis();
    }

    @Override
    public long getTimeMillis() {
        return this.currentTimeMillis;
    }
}