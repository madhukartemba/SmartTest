package com.madhukartemba.smarttest.util;

public class ThreadUtil {

    /**
     * Returns the optimal number of threads to use for a task based on the number
     * of available processors.
     * The returned value is computed by dividing the number of processors by 2 and
     * taking the maximum of the result
     * and 1. This is done to ensure that at least one thread is always used, even
     * on systems with a single processor.
     *
     * @return the optimal number of threads to use
     */
    public static int getOptimalThreadCount() {
        int numOfProcessors = Runtime.getRuntime().availableProcessors();
        int optimalThreadCount = Math.max(1, (numOfProcessors / 2));
        return optimalThreadCount;
    }

}
