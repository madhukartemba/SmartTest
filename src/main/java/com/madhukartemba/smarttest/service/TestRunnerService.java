package com.madhukartemba.smarttest.service;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.util.Timer;

import java.awt.Color;
import java.util.List;

public class TestRunnerService extends RunnerService {
    private FileService fileService;

    public TestRunnerService() {
        this.fileService = new FileService();
    }

    public TestRunnerService(String PROJECT_DIR) {
        super(PROJECT_DIR);
        this.OUTPUT_DIR = PROJECT_DIR + OUTPUT_DIR_NAME;
    }

    @Override
    public void execute(List<Command> commands, boolean cleanDirectory) throws Exception {
        PrintService.boldPrintln("\n\nStarting to run test process...\n");
        super.execute(commands, cleanDirectory);
    }

    @Override
    public void parallelExecute(List<Command> commands, boolean cleanDirectory) throws Exception {

        PrintService.boldPrintln("\n\nStarting to run test processes...\n");
        super.parallelExecute(commands, cleanDirectory);
    }

    // Print the results, print in the OG 'BUILD SUCCESSFUL' color from VSCode :)
    public void printResults(Timer timer) {

        if (Parameters.PRINT_OUTPUT) {
            PrintService.boldPrintln("\n\n Output \n\n");
            fileService.printFromFile(OUTPUT_DIR + OUTPUT_FILE_NAME);
            PrintService.boldPrintln("\n\n Output Ended");
        }

        PrintService.formatPrint(
                "\nNumber of successful test processes: " + getSuccessfulCount() + " out of " + getTotalCount(),
                Color.GREEN);

        if (isBuildSuccessful()) {
            PrintService.formatPrint(
                    "Number of unsuccessful test processes: " + getUnsuccessfulCount() + " out of " + getTotalCount(),
                    Color.GREEN);
            PrintService.boldPrintln("\n\nBUILD SUCCESSFUL in " + timer.getElapsedTime() + "\n\n",
                    Color.decode("#23D18B"));
        } else {
            PrintService.formatPrint(
                    "Number of unsuccessful test processes: " + getUnsuccessfulCount() + " out of " + getTotalCount(),
                    Color.RED);
            PrintService.boldPrintln("\n\nBUILD FAILED in " + timer.getElapsedTime() + "\n\n", Color.RED);
        }
    }

}
