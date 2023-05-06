package com.madhukartemba.smarttest;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.service.*;
import com.madhukartemba.smarttest.util.Printer;
import com.madhukartemba.smarttest.util.TestSieve;
import com.madhukartemba.smarttest.util.Timer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SmartTest {

    public static String VERSION = "1.2.0";

    public static void main(String[] args) throws Exception {

        // Start the timer.
        Timer timer = new Timer();
        timer.start();

        // Set the user provided parameters.
        ParametersService.setParameters(Arrays.stream(args).collect(Collectors.toList()));

        // Print the logo.
        SmartTest.printLogoAndVersion();

        // Value of all the paramters.
        Parameters.printValues();

        // Init the environment variables.
        if (Parameters.PROJECT_DIR.getValue() == null) {
            EnvironmentService.init();
        } else {
            EnvironmentService.init(Parameters.PROJECT_DIR.getValue());
        }

        // Get the list of changed files from Git.
        GitService gitService = new GitService();
        List<String> gitChangedFiles = Parameters.FULL_TEST.getValue() ? null : gitService.getChangedFiles();

        if (!Parameters.FULL_TEST.getValue() && (gitChangedFiles == null || gitChangedFiles.isEmpty())) {
            SmartTest.exitWithCode("The list of changed files determined by Git is empty!", Color.YELLOW, 0);
        }

        // Pass the list of changed files to ExplorerService.
        ExplorerService explorerService = new ExplorerService();

        List<String> changedFiles = null;

        if (Parameters.VIA_CLASSNAME.getValue()) {
            changedFiles = explorerService.exploreViaClassname(gitChangedFiles);
        } else if (Parameters.VIA_PACKAGE.getValue()) {
            changedFiles = explorerService.exploreViaPackageName(gitChangedFiles);
        } else {
            changedFiles = explorerService.explore(gitChangedFiles);
        }

        if (changedFiles == null || changedFiles.isEmpty()) {
            SmartTest.exitWithCode("The list of changed files found by explorer is empty!", Color.YELLOW, 0);
        }

        // Extract the test files from the set of changed files.
        FileService fileService = new FileService();
        List<String> testFiles = explorerService.isCompleteRunRequired() ? changedFiles
                : fileService.getTestFiles(changedFiles);

        if (testFiles == null || testFiles.isEmpty()) {
            SmartTest.exitWithCode("There are no affected test files!", Color.GREEN, 0);
        }

        // Convert the list of files to commands using TestSieve.
        TestSieve testSieve = new TestSieve();
        List<Command> commands = testSieve.groupify(testFiles);

        if (commands == null || commands.isEmpty()) {
            SmartTest.exitWithCode("There are no generated commands for the given test files!", Color.RED, 1);
        }

        // Create the output directory.
        Printer.boldPrintln("\n\nCreating output directory...\n");
        RunnerService.createOutputDirectory(true);

        // Clean the project.
        if (Parameters.CLEAN.getValue()) {
            SmartTest.clean();
        }

        // Refresh the dependencies.
        if (Parameters.REFRESH_DEPENDENCIES.getValue()) {
            SmartTest.refreshDependencies();
        }

        // Compile the code.
        if (!Parameters.SKIP_COMPILE_TEST_JAVA.getValue()) {
            SmartTest.compileTestCode();
        }

        // Assemble the project.
        if (Parameters.ASSEMBLE.getValue()) {
            SmartTest.assemble();
        }

        // Execute the test processes using TestRunnerService.
        TestRunnerService testRunnerService = new TestRunnerService();
        if (Parameters.SERIAL_EXECUTE.getValue()) {
            testRunnerService.execute(commands, "combined", false);
        } else {
            testRunnerService.parallelExecute(commands, false);
        }

        // Process is complete, stop the timer.
        timer.stop();

        testRunnerService.printResults(timer);

        printEndMessage();

        // Return with exit code.
        System.exit(testRunnerService.isBuildSuccessful() ? 0 : 1);

    }

    private static void clean() throws Exception {
        Printer.boldPrintln("\n\nCleaning project...\n");
        RunnerService runnerService = new RunnerService();
        Command compileCommand = new Command(Parameters.GRADLE_COMMAND.getValue(),
                null, "clean", null, new ArrayList<>());
        runnerService.execute(Arrays.asList(compileCommand), "clean", false, true, false);
        if (runnerService.isBuildSuccessful()) {
            Printer.println("\nSuccessfully cleaned project!", Color.GREEN);
        } else {
            runnerService.printOutput();
            SmartTest.exitWithCode("Failed to clean project!", Color.RED, 1);
        }
    }

    private static void assemble() throws Exception {
        Printer.boldPrintln("\n\nAssembling project...\n");
        RunnerService runnerService = new RunnerService();
        Command compileCommand = new Command(Parameters.GRADLE_COMMAND.getValue(),
                null, "assemble", null, new ArrayList<>());
        runnerService.execute(Arrays.asList(compileCommand), "assemble", false, true, false);
        if (runnerService.isBuildSuccessful()) {
            Printer.println("\nSuccessfully assembled project!", Color.GREEN);
        } else {
            runnerService.printOutput();
            SmartTest.exitWithCode("Failed to assemble project!", Color.RED, 1);
        }
    }

    private static void refreshDependencies() throws Exception {
        Printer.boldPrintln("\n\nRefreshing dependencies...\n");
        RunnerService runnerService = new RunnerService();
        Command compileCommand = new Command(Parameters.GRADLE_COMMAND.getValue(),
                null, "--refresh-dependencies", null, new ArrayList<>());
        runnerService.execute(Arrays.asList(compileCommand), "refreshDependencies", false, true, false);
        if (runnerService.isBuildSuccessful()) {
            Printer.println("\nSuccessfully refreshed dependencies!", Color.GREEN);
        } else {
            runnerService.printOutput();
            SmartTest.exitWithCode("Failed to refresh dependencies!", Color.RED, 1);
        }
    }

    private static void compileTestCode() throws Exception {
        Printer.boldPrintln("\n\nCompiling test files...\n");
        RunnerService runnerService = new RunnerService();
        Command compileCommand = new Command(Parameters.GRADLE_COMMAND.getValue(),
                null, "compileTestJava", null, new ArrayList<>());
        runnerService.execute(Arrays.asList(compileCommand), "compileTestJava", false, true, false);
        if (runnerService.isBuildSuccessful()) {
            Printer.println("\nSuccessfully compiled test files!", Color.GREEN);
        } else {
            runnerService.printOutput();
            SmartTest.exitWithCode("Failed to compile test files!", Color.RED, 1);
        }
    }

    // Print the end message
    // Print in the OG 'BUILD SUCCESSFUL' color from VSCode :)
    public static void printEndMessage() {
        Printer.boldPrintln("Thanks for using this program :)\n\n", Color.decode("#23D18B"));
    }

    public static void exitWithCode(String message, int exitCode) {
        Printer.boldPrintln("\n\n" + message + "\n\n", (exitCode == 0 ? Color.GREEN : Color.RED));
        if (exitCode == 0) {
            printEndMessage();
        }
        System.exit(exitCode);
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

    public static void printLogoAndVersion() {
        Printer.boldPrintln(
                "\r\n   _____                      __ ______          __ \r\n  / ___/____ ___  ____ ______/ //_  __/__  _____/ /_\r\n  \\__ \\/ __ `__ \\/ __ `/ ___/ __// / / _ \\/ ___/ __/\r\n ___/ / / / / / / /_/ / /  / /_ / / /  __(__  ) /_  \r\n/____/_/ /_/ /_/\\__,_/_/   \\__//_/  \\___/____/\\__/  \r\n                                                    \r\n",
                Color.GREEN);
        Printer.boldFormatPrint("Version: " + VERSION + "\n\n");
    }

}
