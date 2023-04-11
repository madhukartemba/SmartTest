package com.madhukartemba.smarttest.service;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.entity.ProcessBuilderWrapper;
import com.madhukartemba.smarttest.util.CommandBuilder;
import com.madhukartemba.smarttest.util.Timer;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProcessService {
    private static final String OUTPUT_DIR_NAME = "SmartTestOutput/";
    private static final String OUTPUT_FILE_NAME = "smartTestOutput.txt";
    private String PROJECT_DIR;
    private String OUTPUT_DIR;
    private boolean executionComplete = false;
    private int totalCount = 0;
    private int successfulCount = 0;
    private int unsuccessfulCount = 0;

    public ProcessService() {
        this.PROJECT_DIR = EnvironmentService.PROJECT_DIR;
        this.OUTPUT_DIR = PROJECT_DIR + OUTPUT_DIR_NAME;
    }

    public ProcessService(String PROJECT_DIR) {
        this.PROJECT_DIR = PROJECT_DIR;
        this.OUTPUT_DIR = PROJECT_DIR + OUTPUT_DIR_NAME;
    }

    public void execute(List<Command> commands) throws Exception {
        PrintService.boldPrintln("\n\nStarting to run process...\n");

        List<String> finalCommands = Arrays.asList(CommandBuilder.build(commands, EnvironmentService.TASK_PRIORITY));
        List<String> outputStreams = Arrays.asList(OUTPUT_DIR + OUTPUT_FILE_NAME);
        List<String> processNames = Arrays.asList("AllTests");

        createOutputDirectory(OUTPUT_DIR);

        runCommandsParallel(finalCommands, outputStreams, processNames);
    }

    public void parallelExecute(List<Command> commands) throws Exception {

        PrintService.boldPrintln("\n\nStarting to run processes...\n");

        List<String> finalCommands = CommandBuilder.parallelBuild(commands);
        List<String> outputStreams = createOutputStreams(commands);
        List<String> processNames = generateProcessNames(commands);

        createOutputDirectory(OUTPUT_DIR);

        runCommandsParallel(finalCommands, outputStreams, processNames);

        createAndPopulateOutputFile(outputStreams, Parameters.DELETE_CHILD_FILES);
    }

    public void runCommandsParallel(List<String> commands, List<String> outputStreams, List<String> processNames)
            throws Exception {
        List<ProcessBuilderWrapper> totalProcessBuilders = createProcessBuilders(commands, outputStreams, processNames);

        totalCount = totalProcessBuilders.size();

        List<List<ProcessBuilderWrapper>> splitProcessBuilders = splitProcessBuilderWrappers(totalProcessBuilders);

        int totalSets = splitProcessBuilders.size();
        int setId = 1;

        PrintService.formatPrint("Total process sets: " + totalSets);

        for (List<ProcessBuilderWrapper> processBuilders : splitProcessBuilders) {
            PrintService.formatPrint("\nRunning process set: " + (setId++) + " out of " + totalSets);
            runProcessBuilders(processBuilders);
        }

        executionComplete = true;

    }

    private List<ProcessBuilderWrapper> createProcessBuilders(List<String> commands, List<String> outputStreams,
            List<String> processNames) {
        List<ProcessBuilderWrapper> processBuilderWrappers = new ArrayList<>();
        int streamId = 0;
        for (String command : commands) {
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command(command.split("\\s+"));
            processBuilder.directory(new File(PROJECT_DIR));
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(new File(outputStreams.get(streamId)));

            processBuilderWrappers.add(new ProcessBuilderWrapper(processNames.get(streamId), processBuilder));
            streamId++;
        }

        return processBuilderWrappers;
    }

    private void createAndPopulateOutputFile(List<String> outputStreams, boolean deleteChildFiles) throws Exception {
        // Merge the output files into a single file
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(OUTPUT_DIR + OUTPUT_FILE_NAME))) {
            for (String outputFile : outputStreams) {
                try (BufferedReader reader = new BufferedReader(new FileReader(outputFile))) {
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

    private List<String> createOutputStreams(List<Command> commands) {
        // Create a new ProcessBuilder instance for each command
        int numProcesses = commands.size();
        List<String> outputStreams = new ArrayList<>();

        for (int streamId = 0; streamId < numProcesses; streamId++) {
            outputStreams.add(createOutputStreamFileName(commands.get(streamId), streamId));
        }

        return outputStreams;
    }

    private List<String> generateProcessNames(List<Command> commands) {
        return commands.stream().map(command -> generateProcessName(command)).collect(Collectors.toList());
    }

    private String generateProcessName(Command command) {
        return command.getProjectName() + "->" + command.getTaskName();
    }

    private String createOutputStreamFileName(Command command, int streamId) {
        return OUTPUT_DIR + command.getProjectName() + "-" + command.getTaskName() + "-output" + streamId
                + ".txt";
    }

    private void createOutputDirectory(String directoryPath) {
        File directory = new File(directoryPath);

        // Create the directory if it does not exist
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (success) {
                PrintService.println("Output directory created successfully.", Color.GREEN);
            } else {
                PrintService.println("Failed to create output directory.", Color.RED);
            }
        } else {
            PrintService.println("Output directory already exists, cleaning up the directory...", Color.GREEN);
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            PrintService.println("Output directory cleaned successfully.", Color.GREEN);
        }
    }

    private List<List<ProcessBuilderWrapper>> splitProcessBuilderWrappers(
            List<ProcessBuilderWrapper> processBuilderWrappers) {
        int sublistSize = Parameters.MAX_PARALLEL_THREADS;
        List<List<ProcessBuilderWrapper>> splitProcessBuilderWrappers = new ArrayList<>();

        for (int i = 0; i < processBuilderWrappers.size(); i += sublistSize) {
            int endIndex = Math.min(i + sublistSize, processBuilderWrappers.size());
            splitProcessBuilderWrappers.add(processBuilderWrappers.subList(i, endIndex));
        }

        return splitProcessBuilderWrappers;
    }

    private void runProcessBuilders(List<ProcessBuilderWrapper> processBuilderWrappers) throws Exception {
        // Start each process in parallel
        for (ProcessBuilderWrapper processBuilderWrapper : processBuilderWrappers) {
            processBuilderWrapper.start();
        }

        // Wait for all processes to complete
        for (ProcessBuilderWrapper processBuilderWrapper : processBuilderWrappers) {
            processBuilderWrapper.waitForCompletion();
        }

        // Print the command result
        for (ProcessBuilderWrapper processBuilderWrapper : processBuilderWrappers) {
            PrintService.print("Process ");
            if (processBuilderWrapper.isSuccessful()) {
                successfulCount++;
                PrintService.boldFormatPrint(
                        processBuilderWrapper.getName() +
                                ": BUILD SUCCESSFUL",
                        Color.WHITE,
                        Color.decode("#23D18B"));
            } else {
                unsuccessfulCount++;
                PrintService.boldFormatPrint(processBuilderWrapper.getName() +
                        ": BUILD FAILED WITH EXIT CODE " + processBuilderWrapper.getExitCode(),
                        Color.WHITE,
                        Color.RED);
            }
        }
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

    //Print the results, print in the OG 'BUILD SUCCESSFUL' color from VS Code :)
    public void printResults(Timer timer) {
        if (isBuildSuccessful()) {
            PrintService.formatPrint(
                    "\nNumber of successful processes: " + getSuccessfulCount() + " out of " + getTotalCount(),
                    Color.GREEN);
            PrintService.formatPrint(
                    "Number of unsuccessful processes: " + getUnsuccessfulCount() + " out of " + getTotalCount(),
                    Color.GREEN);
            PrintService.boldPrintln("\n\nBUILD SUCCESSFUL in " + timer.getElapsedTime() + "\n\n",
                    Color.decode("#23D18B"));
        } else {
            PrintService.formatPrint(
                    "\nNumber of successful processes: " + getSuccessfulCount() + " out of " + getTotalCount(),
                    Color.GREEN);
            PrintService.formatPrint(
                    "Number of unsuccessful processes: " + getUnsuccessfulCount() + " out of " + getTotalCount(),
                    Color.RED);
            PrintService.boldPrintln("\n\nBUILD FAILED in " + timer.getElapsedTime() + "\n\n", Color.RED);
        }
    }

}
