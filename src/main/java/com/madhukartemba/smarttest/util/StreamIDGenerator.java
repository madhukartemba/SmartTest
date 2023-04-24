package com.madhukartemba.smarttest.util;

public class StreamIDGenerator {

    private static int streamId = 0;

    public static int generateId() {
        return streamId++;
    }

}
