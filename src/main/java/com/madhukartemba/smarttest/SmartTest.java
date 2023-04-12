package com.madhukartemba.smarttest;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.service.*;
import com.madhukartemba.smarttest.util.ArgsParser;
import com.madhukartemba.smarttest.util.TestSieve;
import com.madhukartemba.smarttest.util.Timer;

import java.awt.Color;
import java.util.List;
import java.util.Map;

public class SmartTest {

    private static String VERSION = "1.0.0";

    public static void main(String[] args) throws Exception {

        // Start the timer.
        Timer timer = new Timer();
        timer.start();

        // Set the user provided parameters.
        Map<String, String> argsMap = ArgsParser.parseArgs(args);
        ParametersService.setParameters(argsMap);

        // Print the logo.
        printLogoAndVersion();

        // Value of all the paramters.
        Parameters.printValues();

        // Init the environment variables.
        EnvironmentService.init();

        // Get the list of changed files from Git.
        GitService gitService = new GitService();
        List<String> gitChangedFiles = Parameters.FULL_TEST ? null : gitService.getChangedFiles();

        if (!Parameters.FULL_TEST && (gitChangedFiles == null || gitChangedFiles.isEmpty())) {
            exitWithCode("The list of changed files determined by Git is empty!", Color.YELLOW, 0);
        }

        // Pass the list of changed files to ExplorerService.
        ExplorerService explorerService = new ExplorerService();

        List<String> changedFiles = null;

        if (Parameters.EXPLORE_VIA_PACKAGE) {
            changedFiles = explorerService.exploreViaPackageName(gitChangedFiles);
        } else {
            changedFiles = explorerService.exploreViaClassname(gitChangedFiles);
        }

        if (changedFiles == null || changedFiles.isEmpty()) {
            exitWithCode("The list of changed files found by explorer is empty!", Color.YELLOW, 0);
        }

        // Extract the test files from the set of changed files.
        FileService fileService = new FileService();
        List<String> testFiles = explorerService.isCompleteRunRequired() ? changedFiles
                : fileService.getTestFiles(changedFiles);

        if (testFiles == null || testFiles.isEmpty()) {
            exitWithCode("There are no affected test files!", Color.GREEN, 0);
        }

        // Convert the list of files to commands using TestSieve.
        TestSieve testSieve = new TestSieve();
        List<Command> commands = testSieve.groupify(testFiles);

        if (commands == null || commands.isEmpty()) {
            exitWithCode("There are no generated commands for the given test files!", Color.RED, 1);
        }

        // Execute the processes using ProcessService.
        ProcessService processService = new ProcessService();
        if (Parameters.PARALLEL_EXECUTE) {
            processService.parallelExecute(commands);
        } else {
            processService.execute(commands);
        }

        // Process is complete, stop the timer.
        timer.stop();

        processService.printResults(timer);

        printEndMessage();

        // Return the exit code.
        System.exit(processService.isBuildSuccessful() ? 0 : 1);

    }

    public static void printEndMessage() {
        PrintService.boldPrintln("Thanks for using this program :)\n\n", Color.decode("#23D18B"));
    }

    public static void exitWithCode(String message, Color color, int exitCode) {
        PrintService.println("\n\n" + message + "\n\n", color);
        printEndMessage();
        System.exit(exitCode);
    }

    private static void printLogoAndVersion() {
        PrintService.boldPrintln(
                "\r\n   _____                      __ ______          __ \r\n  / ___/____ ___  ____ ______/ //_  __/__  _____/ /_\r\n  \\__ \\/ __ `__ \\/ __ `/ ___/ __// / / _ \\/ ___/ __/\r\n ___/ / / / / / / /_/ / /  / /_ / / /  __(__  ) /_  \r\n/____/_/ /_/ /_/\\__,_/_/   \\__//_/  \\___/____/\\__/  \r\n                                                    \r\n",
                Color.GREEN);
        PrintService.boldFormatPrint("Version: " + VERSION + "\n\n");
    }

}
