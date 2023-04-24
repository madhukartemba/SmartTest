package com.madhukartemba.smarttest.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.entity.ProcessBuilderWrapper;
import com.madhukartemba.smarttest.util.CommandBuilder;

public class RunnerService {

    protected boolean executionComplete = false;
    protected int totalCount = 0;
    protected int successfulCount = 0;
    protected int unsuccessfulCount = 0;
    protected String PROJECT_DIR;

    public RunnerService() {
        this.PROJECT_DIR = EnvironmentService.PROJECT_DIR;
    }

    public RunnerService(String PROJECT_DIR) {
        this.PROJECT_DIR = PROJECT_DIR;
    }

    public void execute(List<Command> commands) throws Exception {
        List<String> finalCommands = Arrays.asList(CommandBuilder.build(commands, EnvironmentService.TASK_PRIORITY));
        List<String> processNames = generateProcessNames(commands);

        runCommandsParallel(finalCommands, processNames);
    }

    public void parallelExecute(List<Command> commands) throws Exception {

        List<String> finalCommands = CommandBuilder.parallelBuild(commands);
        List<String> processNames = generateProcessNames(commands);

        runCommandsParallel(finalCommands, processNames);
    }

    public void runCommandsParallel(List<String> commands, List<String> processNames)
            throws Exception {
        List<ProcessBuilderWrapper> totalProcessBuilders = createProcessBuilders(commands, processNames);

        totalCount = totalProcessBuilders.size();

        runProcessBuilders(totalProcessBuilders);

        executionComplete = true;

    }

    protected List<ProcessBuilderWrapper> createProcessBuilders(List<String> commands, List<String> processNames) {
        List<ProcessBuilderWrapper> processBuilderWrappers = new ArrayList<>();
        int streamId = 0;
        for (String command : commands) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command.split("\\s+"));
            processBuilder.directory(new File(PROJECT_DIR));
            processBuilder.redirectErrorStream(true);

            processBuilderWrappers.add(new ProcessBuilderWrapper(processNames.get(streamId), processBuilder));
            streamId++;
        }

        return processBuilderWrappers;
    }

    protected void runProcessBuilders(List<ProcessBuilderWrapper> processBuilderWrappers) throws Exception {
        // Run parallely.
        parallelRun(processBuilderWrappers);

        // Store the result.
        for (ProcessBuilderWrapper processBuilderWrapper : processBuilderWrappers) {
            if (processBuilderWrapper.isSuccessful()) {
                successfulCount++;
            } else {
                unsuccessfulCount++;
            }
        }
    }

    protected void parallelRun(List<ProcessBuilderWrapper> processBuilderWrappers) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(Parameters.MAX_PARALLEL_THREADS);

        for (ProcessBuilderWrapper processBuilderWrapper : processBuilderWrappers) {
            Runnable task = () -> {
                try {
                    processBuilderWrapper.start();
                    processBuilderWrapper.waitForCompletion();
                    processBuilderWrapper.printResult();
                } catch (Exception e) {
                    // Handle exception
                    e.printStackTrace();
                }
            };
            executorService.execute(task);
        }
        executorService.shutdown();
        try {
            // This will terminate after 3 hours
            executorService.awaitTermination(3L, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void waitForProcesses(List<ProcessBuilderWrapper> processBuilderWrappers)
            throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(processBuilderWrappers.size());
        List<Future<Void>> futures = new ArrayList<>();

        for (ProcessBuilderWrapper processBuilderWrapper : processBuilderWrappers) {
            futures.add(executor.submit(() -> {
                processBuilderWrapper.waitForCompletion();
                processBuilderWrapper.printResult();
                return null;
            }));
        }

        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (ExecutionException e) {
                // Handle exception
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

    protected List<String> generateProcessNames(List<Command> commands) {
        return commands.stream().map(command -> generateProcessName(command)).collect(Collectors.toList());
    }

    protected String generateProcessName(Command command) {
        if (command.getProjectName() == null) {
            return command.getTaskName();
        }

        return command.getProjectName() + " -> " + command.getTaskName();
    }
}
