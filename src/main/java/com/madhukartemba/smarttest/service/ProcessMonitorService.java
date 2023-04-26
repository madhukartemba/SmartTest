package com.madhukartemba.smarttest.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.madhukartemba.smarttest.entity.ProcessBuilderWrapper;
import com.madhukartemba.smarttest.util.Printer;

public class ProcessMonitorService extends RefreshableDisplayService implements Runnable {

    private List<ProcessBuilderWrapper> wrapperList;
    private long refreshTime = 1000l;
    private Thread thread;
    private volatile boolean isRunning = false;

    public static void main(String[] args) throws Exception {
        ProcessBuilderWrapper processBuilderWrapper1 = new ProcessBuilderWrapper("TestProcess1", null);
        ProcessBuilderWrapper processBuilderWrapper2 = new ProcessBuilderWrapper("TestProcess2", null);
        ProcessBuilderWrapper processBuilderWrapper3 = new ProcessBuilderWrapper("TestProcess3", null);

        // System.out.println(processBuilderWrapper.toString());

        ProcessMonitorService processMonitorService = new ProcessMonitorService(
                Arrays.asList(processBuilderWrapper1, processBuilderWrapper2, processBuilderWrapper3));
        processMonitorService.start();
        Thread.sleep(10000);
        processMonitorService.stop();
    }

    public ProcessMonitorService(List<ProcessBuilderWrapper> list) {
        this.wrapperList = list;
        this.thread = new Thread(this);
    }

    public ProcessMonitorService(List<ProcessBuilderWrapper> list, long refreshTime) {
        this.wrapperList = list;
        this.refreshTime = refreshTime;
        this.thread = new Thread(this);
    }

    public List<String> getProcessStatuses() {
        List<String> statuses = new ArrayList<>();

        for (ProcessBuilderWrapper processBuilderWrapper : wrapperList) {
            statuses.add(processBuilderWrapper.toString());
        }

        return formatProcessStatuses(statuses);
    }

    public List<String> formatProcessStatuses(List<String> statuses) {
        int maxLen = 0;

        // Find the maximum length of the first part of the string (before the
        // separator)
        for (String status : statuses) {
            maxLen = Math.max(maxLen, status.indexOf(Printer.SEPERATOR) + 1);
        }

        List<String> formattedStatuses = new ArrayList<>();

        // Add each formatted string to the result list
        for (String status : statuses) {
            int separatorIndex = status.indexOf(Printer.SEPERATOR);
            if (separatorIndex >= 0) {
                // Split the string into two parts at the separator position
                String firstPart = status.substring(0, separatorIndex + 1);
                String secondPart = status.substring(separatorIndex + 1);

                // Append spaces to the first part to make it as long as the longest first part
                int padding = maxLen - firstPart.length();
                String paddedFirstPart = firstPart + repeat(" ", padding);

                // Concatenate the two parts and add the formatted string to the result list
                formattedStatuses.add(paddedFirstPart + secondPart);
            } else {
                // If there is no separator, add the original string to the result list
                // unchanged
                formattedStatuses.add(status);
            }
        }

        return formattedStatuses;
    }

    // A utility method to repeat a string n times
    private static String repeat(String s, int n) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++) {
            sb.append(s);
        }
        return sb.toString();
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (Exception e) {
            Printer.print(e.toString());
        }
    }

    public void update() {
        List<String> processStatuses = getProcessStatuses();
        super.update(processStatuses);
    }

    @Override
    public void run() {
        isRunning = true;

        while (isRunning) {
            update();
            try {
                Thread.sleep(refreshTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        update();

    }

}
