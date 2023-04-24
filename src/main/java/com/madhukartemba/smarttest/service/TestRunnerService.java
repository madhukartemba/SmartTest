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

public class TestRunnerService extends RunnerService {
    private static final String OUTPUT_DIR_NAME = "SmartTestOutput/";
    private static final String OUTPUT_FILE_NAME = "smartTestOutput.txt";
    private FileService fileService;
    private String OUTPUT_DIR;

    public TestRunnerService() {
        this.fileService = new FileService();
        this.OUTPUT_DIR = PROJECT_DIR + OUTPUT_DIR_NAME;
    }

    public TestRunnerService(String PROJECT_DIR) {
        super(PROJECT_DIR);
        this.OUTPUT_DIR = PROJECT_DIR + OUTPUT_DIR_NAME;
    }

    public void execute(List<Command> commands) throws Exception {
        PrintService.boldPrintln("\n\nStarting to run process...\n");

        List<String> finalCommands = Arrays.asList(CommandBuilder.build(commands, EnvironmentService.TASK_PRIORITY));
        List<String> outputStreams = Arrays.asList(OUTPUT_DIR + OUTPUT_FILE_NAME);

        createOutputDirectory(OUTPUT_DIR);

        runCommandsParallel(finalCommands, outputStreams, outputStreams);
    }

    @Override
    public void parallelExecute(List<Command> commands) throws Exception {

        PrintService.boldPrintln("\n\nStarting to run processes...\n");

        List<String> finalCommands = CommandBuilder.parallelBuild(commands);
        List<String> outputStreams = createOutputStreams(commands);

        createOutputDirectory(OUTPUT_DIR);

        runCommandsParallel(finalCommands, outputStreams, outputStreams);

        createAndPopulateOutputFile(outputStreams, Parameters.DELETE_CHILD_FILES);
    }

    public void runCommandsParallel(List<String> commands, List<String> outputStreams, List<String> processNames)
            throws Exception {
        List<ProcessBuilderWrapper> totalProcessBuilders = createProcessBuilders(commands, outputStreams, processNames);

        totalCount = totalProcessBuilders.size();

        PrintService.formatPrint("\nTotal number of test processes: " + totalCount + "\n");

        runProcessBuilders(totalProcessBuilders);

        executionComplete = true;

    }

    protected List<ProcessBuilderWrapper> createProcessBuilders(List<String> commands, List<String> outputStreams,
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

    // Print the results, print in the OG 'BUILD SUCCESSFUL' color from VSCode :)
    public void printResults(Timer timer) {

        if (Parameters.PRINT_OUTPUT) {
            PrintService.boldPrintln("\n\n Output \n\n");
            fileService.printFromFile(OUTPUT_DIR + OUTPUT_FILE_NAME);
            PrintService.boldPrintln("\n\n Output Ended");
        }

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
