package com.madhukartemba.smarttest.util;

import java.awt.Color;

public class Timer {
    long startTime;
    long endTime;

    public Timer() {
        startTime = -1;
        endTime = -1;
    }

    public void start() {
        startTime = System.currentTimeMillis();
    }

    public void stop() {
        endTime = System.currentTimeMillis();
    }

    public String getElapsedTime() {

        // If not it was started then print a warning.
        if (startTime == -1) {
            Printer.println("The timer was never started! Will calculate the elapsed time from the start.",
                    Color.RED);
        }

        // Stop if not stopped.
        if (endTime == -1) {
            this.stop();
        }

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
