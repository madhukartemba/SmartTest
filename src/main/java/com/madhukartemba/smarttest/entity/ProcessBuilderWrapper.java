package com.madhukartemba.smarttest.entity;

import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.madhukartemba.smarttest.util.Printer;
import com.madhukartemba.smarttest.util.Timer;

public class ProcessBuilderWrapper {

    private static final int INITIAL_DELAY = 5;
    private static final int REFRESH_TIME = 3;

    private ProcessBuilder processBuilder;
    private Timer timer;
    private Process process;
    private String name;
    private int exitCode = -1;
    private ProcessStatus processStatus;
    private ScheduledExecutorService scheduler;
    private Runnable outputMonitorTask;
    private volatile boolean isFailing = false;

    public ProcessBuilderWrapper(String name, ProcessBuilder processBuilder) {
        this.timer = new Timer();
        this.name = name;
        this.processBuilder = processBuilder;
        this.processStatus = ProcessStatus.QUEUED;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.outputMonitorTask = () -> {
            try {
                String input = new String(Files.readAllBytes(Paths.get(this.name)), StandardCharsets.UTF_8);
                if (input.contains("FAILED")) {
                    isFailing = true;
                    this.stopOutputMonitor();
                }
            } catch (Exception e) {
                // Ignore error
            }
        };
    }

    public void start() throws Exception {
        timer.start();
        this.processStatus = ProcessStatus.RUNNING;
        this.startOutputMonitor();
        this.process = processBuilder.start();
    }

    public void startAndWaitForCompletion() throws Exception {
        start();
        waitForCompletion();
    }

    public boolean isSuccessful() {
        if (exitCode < 0) {
            throw new RuntimeException("Process is not finished yet.");
        }
        return exitCode == 0;
    }

    public void waitForCompletion() throws Exception {
        this.exitCode = process.waitFor();
        this.stopOutputMonitor();
        this.processStatus = (this.exitCode == 0 ? ProcessStatus.SUCCESSFUL : ProcessStatus.FAILED);
        this.timer.stop();
    }

    private void startOutputMonitor() throws Exception {
        if (scheduler == null) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        scheduler.scheduleAtFixedRate(outputMonitorTask, INITIAL_DELAY, REFRESH_TIME, TimeUnit.SECONDS);
    }

    public void stopOutputMonitor() {
        if (scheduler != null) {
            scheduler.shutdown();
            scheduler = null;
        }
    }

    // Print the results, print in the OG 'BUILD SUCCESSFUL' color from VSCode :)
    public void printResult() {
        Printer.print("Process ");
        if (this.isSuccessful()) {
            Printer.boldFormatPrint(
                    this.getName() +
                            " : BUILD SUCCESSFUL in " + timer.getElapsedTime(),
                    Color.WHITE,
                    Printer.BUILD_SUCCESSFUL);
        } else {
            Printer.boldFormatPrint(this.getName() +
                    ": BUILD FAILED WITH EXIT CODE " + this.getExitCode() + " in " + timer.getElapsedTime(),
                    Color.WHITE,
                    Color.RED);
        }
    }

    @Override
    public String toString() {

        String output = Printer.DEFAULT_COLOR_1_CODE + "Process " + Printer.RESET + Printer.BOLD
                + Printer.getColorCode(Color.WHITE) + this.getName() + Printer.RESET + Printer.DEFAULT_COLOR_1_CODE
                + " : " + Printer.RESET + Printer.BOLD;

        if (processStatus == ProcessStatus.SUCCESSFUL) {
            output += Printer.getColorCode(Printer.BUILD_SUCCESSFUL) + "BUILD SUCCESSFUL in " + timer.getElapsedTime();
        } else if (processStatus == ProcessStatus.FAILED) {
            output += Printer.getColorCode(Color.RED) + "BUILD FAILED WITH EXIT CODE " + this.getExitCode() + " in "
                    + timer.getElapsedTime();
        } else if (processStatus == ProcessStatus.RUNNING) {
            if (isFailing) {
                output += Printer.getColorCode(Printer.BUILD_IS_FAILING) + "RUNNING (FAILING) ";
            } else {
                output += Printer.getColorCode(Printer.DEFAULT_COLOR_2) + "RUNNING ";
            }
            output += timer.getElapsedTime();
        } else if (processStatus == ProcessStatus.QUEUED) {
            output += Printer.getColorCode(Color.LIGHT_GRAY) + "QUEUED";
        } else {
            output += Printer.getColorCode(Color.ORANGE) + processStatus.toString();
        }

        output += Printer.RESET;

        return output;
    }

    public ProcessBuilder getProcessBuilder() {
        return processBuilder;
    }

    public void setProcessBuilder(ProcessBuilder processBuilder) {
        this.processBuilder = processBuilder;
    }

    public Process getProcess() {
        return process;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExitCode() {
        return exitCode;
    }

    public Timer getTimer() {
        return timer;
    }

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }
}
