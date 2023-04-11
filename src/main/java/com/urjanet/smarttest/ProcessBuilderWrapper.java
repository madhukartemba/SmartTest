package com.urjanet.smarttest;

public class ProcessBuilderWrapper {
    private ProcessBuilder processBuilder;
    private Process process;
    private String name;
    private int exitCode = -1;

    public ProcessBuilderWrapper(String name, ProcessBuilder processBuilder) {
        this.name = name;
        this.processBuilder = processBuilder;
    }

    public void start() throws Exception {
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

}
