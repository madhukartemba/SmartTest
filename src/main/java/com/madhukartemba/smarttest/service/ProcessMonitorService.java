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

        return statuses;
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
