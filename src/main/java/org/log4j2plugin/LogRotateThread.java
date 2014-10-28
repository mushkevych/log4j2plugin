package org.log4j2plugin;

import org.apache.logging.log4j.EventLogger;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.message.StructuredDataMessage;

import java.util.*;

public class LogRotateThread extends Thread {
    public static final int LOG_ROTATE_ITERATION_INTERVAL_MILLIS = 3 * 60 * 1000;
    public static final String ID_SKIP = "SKIP";
    private static Set<FTimeBasedTriggeringPolicy> policies = new HashSet<FTimeBasedTriggeringPolicy>();

    public boolean isRunning = false;
    protected boolean isOneTimeRun = false;

    /**
     * default constructor that should be used by normal users
     */
    public LogRotateThread() {
        super("mobidia.LogRotateThread");
    }

    /**
     * This constructor should be used by Unit Tests only
     * It is to run for 1 iteration only
     *
     * @param isRunning    True if the "run" method should be allowed to run
     * @param isOneTimeRun True to conform with the purpose of the constructor
     */
    public LogRotateThread(boolean isRunning, boolean isOneTimeRun) {
        this();
        this.isRunning = isRunning;
        this.isOneTimeRun = isOneTimeRun;
    }

    public static void registerPolicy(FTimeBasedTriggeringPolicy policy) {
        policies.add(policy);
    }

    public static void unregisterPolicy(FTimeBasedTriggeringPolicy policy) {
        policies.remove(policy);
    }

    public void run() {
        System.out.println("LogRotateThread started with the sleep interval: "
                + String.valueOf(LOG_ROTATE_ITERATION_INTERVAL_MILLIS / 60 / 1000) + " minutes");

        while (isRunning) {
            LogEvent logEvent = new EmptyLogEvent();

            for (FTimeBasedTriggeringPolicy policy: policies) {
                System.out.println("LogRotateThread: validating log rotation for " + policy.toString());
                try {
                    policy.checkRollover(logEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (isOneTimeRun) {
                // isOneTimeRun should be True for Unit Tests only
                isRunning = false;
            }

            try {
                if (isRunning) {
                    sleep(LOG_ROTATE_ITERATION_INTERVAL_MILLIS);
                }
            } catch (InterruptedException e) {
                System.err.println("LogRotateThread sleep was interrupted: " + e.getMessage());
            }
        }
    }

    public static void initializeAppenders(Collection<String> types) {
        for (String msgTypes: types) {
            StructuredDataMessage msg = new StructuredDataMessage(ID_SKIP, "", msgTypes);
            EventLogger.logEvent(msg);
        }
    }
}

