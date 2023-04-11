package com.madhukartemba.smarttest.util;

public class Timer {
    long startTime;
    long endTime;

    public Timer() {
        startTime = 0;
        endTime = 0;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        endTime = System.currentTimeMillis();
    }

    public String getElapsedTime() {
        long elapsedTime = endTime - startTime;
        int hours = (int) (elapsedTime / (60 * 60 * 1000));
        int minutes = (int) ((elapsedTime / (60 * 1000)) % 60);
        int seconds = (int) ((elapsedTime / 1000) % 60);

        StringBuilder output = new StringBuilder();
        if (hours > 0) {
            output.append(hours).append("h ");
        }
        if (minutes > 0 || hours > 0) {
            output.append(minutes).append("m ");
        }
        output.append(seconds).append("s");

        return output.toString();

    }

}
