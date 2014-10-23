package org.log4j2plugin;

import org.apache.logging.log4j.core.LogEvent;

import java.util.*;

public class LogRotateThread extends Thread {
    public static final int LOG_ROTATE_ITERATION_INTERVAL_MILLIS = 3 * 60 * 1000;
    private static Set<FTimeBasedTriggeringPolicy> policies = new HashSet<FTimeBasedTriggeringPolicy>();

    public boolean isRunning = false;

    /**
     * default constructor that should be used by normal users
     */
    public LogRotateThread() {
        super("mobidia.LogRotateThread");
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
                    boolean shouldRotate = policy.isTriggeringEvent(logEvent);
                    if (shouldRotate) {
                        System.out.println("LogRotateThread: triggering log rotation");
                        policy.checkRollover(logEvent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
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
}

