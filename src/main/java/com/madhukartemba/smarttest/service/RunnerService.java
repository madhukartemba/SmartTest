package com.madhukartemba.smarttest.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
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
import com.madhukartemba.smarttest.util.Printer;
import com.madhukartemba.smarttest.util.StreamIDGenerator;

public class RunnerService {

    protected static final String OUTPUT_DIR_NAME = "SmartTestOutput/";
    protected static final String OUTPUT_FILE_NAME = "smartTestOutput.txt";
    protected static String PROJECT_DIR = EnvironmentService.PROJECT_DIR;
    protected static String OUTPUT_DIR = PROJECT_DIR + OUTPUT_DIR_NAME;

    protected boolean executionComplete = false;
    protected int totalCount = 0;
    protected int successfulCount = 0;
    protected int unsuccessfulCount = 0;

    private FileService fileService;

    public RunnerService() {
        this.fileService = new FileService();
    }

    public void execute(List<Command> commands, String outputFileName) throws Exception {
        execute(commands, outputFileName, false);
    }

    public void execute(List<Command> commands, String outputFileName, boolean cleanDirectory) throws Exception {
        execute(commands, outputFileName, cleanDirectory, true);
    }

    public void execute(List<Command> commands, String outputFileName, boolean cleanDirectory, boolean addToFinalOutput)
            throws Exception {
        execute(commands, outputFileName, cleanDirectory, addToFinalOutput,
                Parameters.DELETE_CHILD_FILES.getValue());
    }

    public void execute(List<Command> commands, String outputFileName, boolean cleanDirectory, boolean addToFinalOutput,
            boolean deleteChildFiles) throws Exception {

        outputFileName += ("-output" + StreamIDGenerator.generateId() + ".txt");

        List<String> finalCommands = Arrays.asList(CommandBuilder.build(commands, EnvironmentService.TASK_PRIORITY));
        List<String> outputStreams = Arrays
                .asList((EnvironmentService.ON_SYSTEM_DIR ? OUTPUT_DIR_NAME : OUTPUT_DIR) + outputFileName);

        baseExecute(finalCommands, outputStreams, cleanDirectory, addToFinalOutput, deleteChildFiles);
    }

    public void parallelExecute(List<Command> commands) throws Exception {
        parallelExecute(commands, false);
    }

    public void parallelExecute(List<Command> commands, boolean cleanDirectory)
            throws Exception {
        parallelExecute(commands, cleanDirectory, true);
    }

    public void parallelExecute(List<Command> commands, boolean cleanDirectory, boolean addToFinalOutput)
            throws Exception {
        parallelExecute(commands, cleanDirectory, addToFinalOutput, Parameters.DELETE_CHILD_FILES.getValue());
    }

    public void parallelExecute(List<Command> commands, boolean cleanDirectory, boolean addToFinalOutput,
            boolean deleteChildFiles)
            throws Exception {

        List<String> finalCommands = CommandBuilder.parallelBuild(commands);
        List<String> outputStreams = createOutputStreams(commands);

        baseExecute(finalCommands, outputStreams, cleanDirectory, addToFinalOutput, deleteChildFiles);
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

    protected void baseExecute(List<String> finalCommands, List<String> outputStreams, boolean cleanDirectory,
            boolean addToFinalOutput,
            boolean deleteChildFiles) throws Exception {
        createOutputDirectory(cleanDirectory);

        runCommandsParallel(finalCommands, outputStreams, outputStreams);

        if (addToFinalOutput) {
            createAndPopulateOutputFile(outputStreams, deleteChildFiles);
        }

    }

    protected List<ProcessBuilderWrapper> createProcessBuilders(List<String> commands, List<String> outputStreams,
            List<String> processNames) {
        List<ProcessBuilderWrapper> processBuilderWrappers = new ArrayList<>();
        int streamId = 0;
        for (String command : commands) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command.split("\\s+"));
            processBuilder.directory(new File(RunnerService.PROJECT_DIR));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(new File(outputStreams.get(streamId)));

            processBuilderWrappers.add(new ProcessBuilderWrapper(processNames.get(streamId), processBuilder));
            streamId++;
        }

        return processBuilderWrappers;
    }

    protected void runCommandsParallel(List<String> commands, List<String> outputStreams, List<String> processNames)
            throws Exception {
        List<ProcessBuilderWrapper> totalProcessBuilders = createProcessBuilders(commands, outputStreams, processNames);

        totalCount = totalProcessBuilders.size();

        runProcessBuilders(totalProcessBuilders);

        executionComplete = true;

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

        ExecutorService executorService = Executors.newFixedThreadPool(Parameters.MAX_THREADS.getValue());

        ProcessMonitorService processMonitorService = null;

        if (!Parameters.USE_LEGACY_PRINTER.getValue()) {
            processMonitorService = new ProcessMonitorService(processBuilderWrappers);
            processMonitorService.start();
        }

        for (ProcessBuilderWrapper processBuilderWrapper : processBuilderWrappers) {
            Runnable task = () -> {
                try {
                    processBuilderWrapper.startAndWaitForCompletion();
                    if (Parameters.USE_LEGACY_PRINTER.getValue()) {
                        processBuilderWrapper.printResult();
                    }
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

        if (!Parameters.USE_LEGACY_PRINTER.getValue()) {
            processMonitorService.stop();
        }

    }

    protected void createAndPopulateOutputFile(List<String> outputStreams, boolean deleteChildFiles) throws Exception {
        // Merge the output files into a single file
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(OUTPUT_DIR + OUTPUT_FILE_NAME, true))) {
            for (String outputFile : outputStreams) {
                try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {

                    writer.append("\n\n\n  ########  OUTPUT FROM: " + outputFile + "\n");
                    writer.newLine();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        writer.write(line);
                        writer.newLine();
                    }
                }
            }
        }

        if (deleteChildFiles) {
            // Delete the individual output files
            for (String outputFile : outputStreams) {
                new File(outputFile).delete();
            }
        }
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

    protected List<String> createOutputStreams(List<Command> commands) {
        // Create a new ProcessBuilder instance for each command
        int numProcesses = commands.size();
        List<String> outputStreams = new ArrayList<>();

        for (int i = 0; i < numProcesses; i++) {
            outputStreams.add(createOutputStreamFileName(commands.get(i), StreamIDGenerator.generateId()));
        }

        return outputStreams;
    }

    protected String createOutputStreamFileName(Command command, int streamId) {

        return (EnvironmentService.ON_SYSTEM_DIR ? OUTPUT_DIR_NAME : OUTPUT_DIR)
                + (command.getProjectName() == null ? "" : command.getProjectName() + "-")
                + command.getTaskName() + "-output" + streamId
                + ".txt";
    }

    public boolean isBuildSuccessful() {
        if (!executionComplete) {
            throw new RuntimeException(
                    "Cannot get the unsuccessful process count as the processes have not started/completed yet.");
        }
        return unsuccessfulCount == 0;
    }

    public int getSuccessfulCount() {
        if (!executionComplete) {
            throw new RuntimeException(
                    "Cannot get the successful process count as the processes have not started/completed yet.");
        }
        return successfulCount;
    }

    public int getUnsuccessfulCount() {
        if (!executionComplete) {
            throw new RuntimeException(
                    "Cannot get the unsuccessful processes count as the processes have not started/completed.");
        }
        return unsuccessfulCount;
    }

    public int getTotalCount() {
        if (!executionComplete) {
            throw new RuntimeException(
                    "Cannot get the unsuccessful processes count as the processes have not started/completed.");
        }
        return totalCount;
    }

    public void printOutput() {
        Printer.boldPrintln("\n\n Output \n\n");
        fileService.printFromFile(OUTPUT_DIR + OUTPUT_FILE_NAME);
        Printer.boldPrintln("\n\n Output Ended");
    }

    public static void createOutputDirectory(boolean cleanDirectory) {
        FileService.createDirectory(OUTPUT_DIR, cleanDirectory);
        if (cleanDirectory == true) {
            FileService.createFile(OUTPUT_DIR + ".gitignore", "*");
        }
    }

    public static int lightWeightExecute(String command, String directory) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command.split("\\s+"));
        processBuilder.directory(new File(directory));
        Process process = processBuilder.start();
        int exitCode = process.waitFor();

        // Read the process's output and error streams and print them to the console
        BufferedReader outputReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        String line;
        while ((line = outputReader.readLine()) != null) {
            Printer.formatPrint(line);
        }
        while ((line = errorReader.readLine()) != null) {
            Printer.formatPrint(line);
        }

        return exitCode;
    }
}
