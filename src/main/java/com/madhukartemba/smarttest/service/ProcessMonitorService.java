package com.madhukartemba.smarttest.service;

import java.util.ArrayList;
import java.util.List;

import com.madhukartemba.smarttest.entity.ProcessBuilderWrapper;

public class ProcessMonitorService extends RefreshableDisplayService implements Runnable {

    private List<ProcessBuilderWrapper> wrapperList;
    private long refreshTime = 500l;
    private Thread thread;

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

        return statuses;
    }

    public void start() {
        thread.start();
    }

    public void stop() {
        thread.interrupt();
    }

    @Override
    public void run() {
        // your code for the thread's task goes here
        while (!Thread.currentThread().isInterrupted()) {
            List<String> processStatuses = getProcessStatuses();
            update(processStatuses);
            try {
                Thread.sleep(refreshTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
