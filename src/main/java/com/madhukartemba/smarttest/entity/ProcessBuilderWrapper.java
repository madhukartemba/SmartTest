package com.madhukartemba.smarttest.entity;

import java.awt.Color;

import com.madhukartemba.smarttest.service.PrintService;
import com.madhukartemba.smarttest.util.Timer;

public class ProcessBuilderWrapper {
    private ProcessBuilder processBuilder;
    private Timer timer;
    private Process process;
    private String name;
    private int exitCode = -1;

    public ProcessBuilderWrapper(String name, ProcessBuilder processBuilder) {
        this.timer = new Timer();
        this.name = name;
        this.processBuilder = processBuilder;
    }

    public void start() throws Exception {
        timer.start();
        this.process = processBuilder.start();
    }

    public boolean isSuccessful() {
        if (exitCode < 0) {
            throw new RuntimeException("Process is not finished yet.");
        }
        return exitCode == 0;
    }

    public void waitForCompletion() throws Exception {
        this.exitCode = process.waitFor();
        this.timer.stop();
    }

    public void printResult() {
        PrintService.print("Process ");
        if (isSuccessful()) {
            PrintService.boldFormatPrint(
                    this.getName() +
                            ": BUILD SUCCESSFUL in " + timer.getElapsedTime(),
                    Color.WHITE,
                    Color.decode("#23D18B"));
        } else {
            PrintService.boldFormatPrint(this.getName() +
                    ": BUILD FAILED WITH EXIT CODE " + this.getExitCode() + " in " + timer.getElapsedTime(),
                    Color.WHITE,
                    Color.RED);
        }
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

}
