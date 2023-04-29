package com.madhukartemba.smarttest.service;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.util.Printer;
import com.madhukartemba.smarttest.util.Timer;

import java.awt.Color;
import java.util.List;

public class TestRunnerService extends RunnerService {

    @Override
    public void execute(List<Command> commands, String outputFileName, boolean cleanDirectory) throws Exception {
        Printer.boldPrintln("\n\nStarting to run test process...\n");
        super.execute(commands, outputFileName, cleanDirectory);
    }

    @Override
    public void parallelExecute(List<Command> commands, boolean cleanDirectory) throws Exception {

        Printer.boldPrintln("\n\nStarting to run test processes...\n");
        super.parallelExecute(commands, cleanDirectory);
    }

    // Print the results, print in the OG 'BUILD SUCCESSFUL' color from VSCode :)
    public void printResults(Timer timer) {

        if (Parameters.PRINT_OUTPUT.getValue()) {
            super.printOutput();
        }

        Printer.formatPrint(
                "\nNumber of successful test processes: " + getSuccessfulCount() + " out of " + getTotalCount(),
                Color.GREEN);

        if (isBuildSuccessful()) {
            Printer.formatPrint(
                    "Number of unsuccessful test processes: " + getUnsuccessfulCount() + " out of " + getTotalCount(),
                    Color.GREEN);
            Printer.boldPrintln("\n\nBUILD SUCCESSFUL in " + timer.getElapsedTime() + "\n\n",
                    Color.decode("#23D18B"));
        } else {
            Printer.formatPrint(
                    "Number of unsuccessful test processes: " + getUnsuccessfulCount() + " out of " + getTotalCount(),
                    Color.RED);
            Printer.boldPrintln("\n\nBUILD FAILED in " + timer.getElapsedTime() + "\n\n", Color.RED);
        }
    }

}
