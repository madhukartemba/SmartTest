package com.madhukartemba.smarttest.entity;

import java.awt.Color;

import com.madhukartemba.smarttest.util.Printer;
import com.madhukartemba.smarttest.util.Timer;

public class ProcessBuilderWrapper {
    private ProcessBuilder processBuilder;
    private Timer timer;
    private Process process;
    private String name;
    private int exitCode = -1;
    private ProcessStatus processStatus;

    public ProcessBuilderWrapper(String name, ProcessBuilder processBuilder) {
        this.timer = new Timer();
        this.name = name;
        this.processBuilder = processBuilder;
        this.processStatus = ProcessStatus.QUEUED;
    }

    public void start() throws Exception {
        timer.start();
        this.processStatus = ProcessStatus.RUNNING;
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
        this.processStatus = (this.exitCode == 0 ? ProcessStatus.SUCCESSFUL : ProcessStatus.FAILED);
        this.timer.stop();
    }

    // Print the results, print in the OG 'BUILD SUCCESSFUL' color from VSCode :)
    public void printResult() {
        Printer.print("Process ");
        if (isSuccessful()) {
            Printer.boldFormatPrint(
                    this.getName() +
                            " : BUILD SUCCESSFUL in " + timer.getElapsedTime(),
                    Color.WHITE,
                    Color.decode("#23D18B"));
        } else {
            Printer.boldFormatPrint(this.getName() +
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

    public ProcessStatus getProcessStatus() {
        return processStatus;
    }
}
