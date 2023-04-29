package com.madhukartemba.smarttest;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.service.*;
import com.madhukartemba.smarttest.util.ArgsParser;
import com.madhukartemba.smarttest.util.Printer;
import com.madhukartemba.smarttest.util.TestSieve;
import com.madhukartemba.smarttest.util.Timer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmartTest {

    public static String VERSION = "1.1.1";

    public static void main(String[] args) throws Exception {

        // Start the timer.
        Timer timer = new Timer();
        timer.start();

        // Set the user provided parameters.
        Map<String, String> argsMap = ArgsParser.parseArgs(args);
        ParametersService.setParameters(argsMap);

        // Print the logo.
        SmartTest.printLogoAndVersion();

        // Value of all the paramters.
        Parameters.printValues();

        // Init the environment variables.
        if (Parameters.PROJECT_DIR == null) {
            EnvironmentService.init();
        } else {
            EnvironmentService.init(Parameters.PROJECT_DIR);
        }

        // Get the list of changed files from Git.
        GitService gitService = new GitService();
        List<String> gitChangedFiles = Parameters.FULL_TEST ? null : gitService.getChangedFiles();

        if (!Parameters.FULL_TEST && (gitChangedFiles == null || gitChangedFiles.isEmpty())) {
            exitWithCode("The list of changed files determined by Git is empty!", Color.YELLOW, 0);
        }

        // Pass the list of changed files to ExplorerService.
        ExplorerService explorerService = new ExplorerService();

        List<String> changedFiles = null;

        if (Parameters.EXPLORE_WITH_PACKAGE) {
            changedFiles = explorerService.explore(gitChangedFiles);
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

        // Create the output directory.
        Printer.boldPrintln("\n\nCreating output directory...\n");
        RunnerService.createOutputDirectory(true);

        // Refresh the dependencies.
        if (Parameters.REFRESH_DEPENDENCIES) {
            SmartTest.refreshDependencies();
        }

        // Compile the code.
        if (Parameters.COMPILE_JAVA) {
            SmartTest.compileCode();
        }

        // Execute the test processes using TestRunnerService.
        TestRunnerService testRunnerService = new TestRunnerService();
        if (Parameters.PARALLEL_EXECUTE) {
            testRunnerService.parallelExecute(commands, false);
        } else {
            testRunnerService.execute(commands, "combined", false);
        }

        // Process is complete, stop the timer.
        timer.stop();

        testRunnerService.printResults(timer);

        printEndMessage();

        // Return with exit code.
        System.exit(testRunnerService.isBuildSuccessful() ? 0 : 1);

    }

    private static void refreshDependencies() throws Exception {
        Printer.boldPrintln("\n\nRefreshing dependencies...\n");
        RunnerService runnerService = new RunnerService();
        Command compileCommand = new Command(Parameters.GRADLE_COMMAND_NAME,
                null, "--refresh-dependencies", null, new ArrayList<>());
        runnerService.execute(Arrays.asList(compileCommand), "refreshDependencies", false, true, false);
        if (runnerService.isBuildSuccessful()) {
            Printer.println("\nSuccessfully refreshed dependencies!", Color.GREEN);
        } else {
            runnerService.printOutput();
            exitWithCode("Failed to refresh dependencies!", Color.RED, 1);
        }
    }

    private static void compileCode() throws Exception {
        Printer.boldPrintln("\n\nCompiling code...\n");
        RunnerService runnerService = new RunnerService();
        Command compileCommand = new Command(Parameters.GRADLE_COMMAND_NAME,
                null, "compileJava", null, new ArrayList<>());
        runnerService.execute(Arrays.asList(compileCommand), "compileJava", false, true, false);
        if (runnerService.isBuildSuccessful()) {
            Printer.println("\nCompilation successful!", Color.GREEN);
        } else {
            runnerService.printOutput();
            exitWithCode("Compilation failed!", Color.RED, 1);
        }
    }

    // Print the end message
    // Print in the OG 'BUILD SUCCESSFUL' color from VSCode :)
    public static void printEndMessage() {
        Printer.boldPrintln("Thanks for using this program :)\n\n", Color.decode("#23D18B"));
    }

    public static void exitWithCode(String message, Color color, int exitCode) {
        Printer.boldPrintln("\n\n" + message + "\n\n", color);
        if (exitCode == 0) {
            printEndMessage();
        }
        System.exit(exitCode);
    }

    public static void checkForShellInjection(String command) {
        command.trim();
        if (containsMultipleCommands(command) && hasSudoPermission()) {
            SmartTest.exitWithCode(
                    "ERROR (POTENTIAL SHELL INJECTION ATTCK): The provided command contains multiple commands that may execute with sudo permission, which may be susceptible to shell injection attacks. Please ensure that the command is validated and only executes trusted commands. The program will now exit to prevent any potential security risks.",
                    Color.RED,
                    1);
        }
    }

    public static boolean containsMultipleCommands(String commandString) {
        String commandRegex = "^(.*?)(?<!\\\\)([;&|]{2}|;|\\|\\||&)(.*)$";
        Pattern pattern = Pattern.compile(commandRegex);
        Matcher matcher = pattern.matcher(commandString);

        return matcher.matches();
    }

    public static boolean hasSudoPermission() {
        try {
            // Attempt to run a command that requires sudo permission
            Process process = Runtime.getRuntime().exec("sudo -n true");

            // Wait for the process to finish
            process.waitFor();

            // Check if the command succeeded or failed
            int exitCode = process.exitValue();
            return exitCode == 0;

        } catch (Exception e) {
            return false;
        }
    }

    private static void printLogoAndVersion() {
        Printer.boldPrintln(
                "\r\n   _____                      __ ______          __ \r\n  / ___/____ ___  ____ ______/ //_  __/__  _____/ /_\r\n  \\__ \\/ __ `__ \\/ __ `/ ___/ __// / / _ \\/ ___/ __/\r\n ___/ / / / / / / /_/ / /  / /_ / / /  __(__  ) /_  \r\n/____/_/ /_/ /_/\\__,_/_/   \\__//_/  \\___/____/\\__/  \r\n                                                    \r\n",
                Color.GREEN);
        Printer.boldFormatPrint("Version: " + VERSION + "\n\n");
    }

}
