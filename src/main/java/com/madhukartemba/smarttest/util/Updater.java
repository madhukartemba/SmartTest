package com.madhukartemba.smarttest.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Timer;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

import com.madhukartemba.smarttest.SmartTest;
import com.madhukartemba.smarttest.service.FileService;
import com.madhukartemba.smarttest.service.RunnerService;

public class Updater {
    public static String GITHUB_VERSION = null;
    public static List<String> CHANGELOG = new ArrayList<>();

    private static String SYSTEM_DIR = System.getProperty("user.dir");
    private static String PROJECT_DIR = (SYSTEM_DIR.endsWith("/") ? SYSTEM_DIR : SYSTEM_DIR + "/");

    private static final AtomicBoolean SHOULD_EXIT = new AtomicBoolean(false);
    private static final CountDownLatch LATCH = new CountDownLatch(1);
    private static final String REPO_URL = "https://github.com/madhukartemba/SmartTest.git";
    private static final String VERSION_URL = "https://raw.githubusercontent.com/madhukartemba/SmartTest/main/VERSION.txt";
    private static final String UPDATE_DIR_NAME = "SmartTestUpdateFolder/";
    private static final String REPO_DIR_NAME = "SmartTest/";
    private static final String INSTALL_DIR_NAME = "install/";

    private static final String UPDATE_DIR = PROJECT_DIR + UPDATE_DIR_NAME;
    private static final String INSTALL_DIR = UPDATE_DIR + REPO_DIR_NAME + INSTALL_DIR_NAME;

    public static void main(String[] args) throws Exception {

        // checkForUpdates(false);
        // printChangelog(Printer.BUILD_SUCCESSFUL);
        // Printer.boldPrint("\n\nNew update ", Printer.BUILD_SUCCESSFUL);
        // Printer.boldPrint("(" + Updater.GITHUB_VERSION + ")",
        // Printer.DEFAULT_COLOR_2);
        // Printer.boldPrintln(" available!", Printer.BUILD_SUCCESSFUL);
        // Printer.boldPrint("Please run ", Printer.BUILD_SUCCESSFUL);
        // Printer.boldPrint("'SmartTest --updateApp'", Printer.DEFAULT_COLOR_2);
        // Printer.boldPrintln(" to install the latest version.\nThank you!\n\n",
        // Printer.BUILD_SUCCESSFUL);
        // System.out.println(checkForUpdates(true));
        Updater.updateApplication();
        // String GITHUB_VERSION = getVersionNumberFromGithub(VERSION_URL);
        // System.out.println(compareVersions(SmartTest.VERSION, "1.2.0"));
        // System.out.println(askToProceed());
    }

    public static void updateApplication() throws Exception {

        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                try {
                    if (FileService.directoryExists(UPDATE_DIR)) {
                        FileService.deleteDirectory(UPDATE_DIR);
                    }
                    LATCH.countDown();
                } catch (Exception e) {
                    System.exit(0);
                }
            }
        });

        SmartTest.printLogoAndVersion();

        Printer.boldPrintln("Checking for updates...\n");
        int res = checkForUpdates(false);

        Updater.printChangelog(Printer.BUILD_SUCCESSFUL);

        if (res >= 0) {
            if (res > 0) {
                Printer.print("\nYou are running a newer version ");
                Printer.print("(" + SmartTest.VERSION + ")", Printer.DEFAULT_COLOR_2);
                Printer.print(" than the latest available version ");
                Printer.println("(" + Updater.GITHUB_VERSION + ")", Printer.DEFAULT_COLOR_2);
            } else {
                Printer.print("\nYou are running the latest version ");
                Printer.println("(" + Updater.GITHUB_VERSION + ")", Printer.DEFAULT_COLOR_2);
            }

            boolean proceed = askToProceed();

            if (!proceed) {
                Updater.cleanExit("Installation aborted!", 0);
            }
        }

        Printer.boldPrint("\n\nStarting to update application to the latest version ");
        Printer.boldPrintln("(" + (Updater.GITHUB_VERSION == null ? "UNKNOWN" : Updater.GITHUB_VERSION) + ")",
                Printer.DEFAULT_COLOR_2);

        Printer.println("\nCreating a temporary output directory...");
        if (FileService.directoryExists(UPDATE_DIR)) {
            Printer.println("Output directory already exists, deleting it...", Color.YELLOW);
            FileService.deleteDirectory(UPDATE_DIR);
            Printer.println("Previous output directory deleted successfully!", Color.GREEN);
        }
        FileService.createDirectory(UPDATE_DIR, true);
        FileService.createFile(UPDATE_DIR + ".gitignore", "*");

        Printer.boldPrintln("\n\nDownloading the latest version...\n");
        String gitCloneCommand = "git clone " + REPO_URL;

        int exitCode = 0;

        exitCode = RunnerService.lightWeightExecute(gitCloneCommand, UPDATE_DIR);

        if (exitCode != 0) {
            Updater.cleanExit("An error occured while downloading! Error code: " + exitCode, exitCode);
        }

        Printer.println("\nDownload successful!", Color.GREEN);

        Printer.boldPrintln("\n\nRequesting execute permission, please enter your password if prompted...\n");

        String chmodCommand = "sudo chmod +x install.sh";

        exitCode = RunnerService.lightWeightExecute(chmodCommand, INSTALL_DIR);

        if (exitCode != 0) {
            Updater.cleanExit(
                    "An error occured while requesting for execute permission! Error code: " + exitCode, exitCode);
        }

        Printer.println("\nExecute permission granted!", Color.GREEN);

        Printer.boldPrintln("\n\nInstalling latest version, please enter your password if prompted...\n");

        String installCommand = "sudo ./install.sh";

        exitCode = RunnerService.lightWeightExecute(installCommand, INSTALL_DIR);

        if (exitCode != 0) {
            Updater.cleanExit("An error occured during installation! Error code: " + exitCode, 1);
        }

        Printer.println("\n\n");
        Updater.printChangelog(Printer.BUILD_SUCCESSFUL);

        Updater.cleanExit("Installation completed successfully!", exitCode);
    }

    private static void cleanExit(String message, int exitCode) throws Exception {
        if (FileService.directoryExists(UPDATE_DIR)) {
            FileService.deleteDirectory(UPDATE_DIR);
        }

        SmartTest.exitWithCode(message, exitCode);
    }

    private static void cleanExit(String message, Color color, int exitCode) throws Exception {
        if (FileService.directoryExists(UPDATE_DIR)) {
            FileService.deleteDirectory(UPDATE_DIR);
        }

        SmartTest.exitWithCode(message, color, exitCode);
    }

    public static int checkForUpdates(boolean silent) throws Exception {
        String GITHUB_VERSION = getVersionNumberFromGithub(VERSION_URL, silent);
        if (GITHUB_VERSION == null) {
            return silent ? 0 : 1;
        }
        return compareVersions(SmartTest.VERSION, GITHUB_VERSION, silent);
    }

    public static void printChangelog(Color color) throws Exception {
        List<String> CHANGELOG = getChangelogFromGitHub(VERSION_URL);
        boolean hasHigherVersion = false;
        for (String line : CHANGELOG) {
            if (hasHigherVersion == false && line.contains("VERSION")) {
                String versionNumber = line.substring("VERSION".length()).trim();
                if (Updater.compareVersions(SmartTest.VERSION, versionNumber, false) < 0) {
                    hasHigherVersion = true;
                }
            }

            if (hasHigherVersion) {
                Printer.boldFormatPrint(line, color, Printer.DEFAULT_COLOR_2);
            }
        }

    }

    private static String getVersionNumberFromGithub(String rawUrl, boolean silent) throws Exception {

        if (GITHUB_VERSION != null) {
            return GITHUB_VERSION;
        }

        try {
            List<String> CHANGELOG = getChangelogFromGitHub(rawUrl);
            for (String line : CHANGELOG) {
                if (line.contains("VERSION")) {
                    String versionNumber = line.substring("VERSION".length()).trim();
                    GITHUB_VERSION = versionNumber;
                }
            }
            return GITHUB_VERSION;

        } catch (Exception e) {
            if (!silent) {
                Printer.formatPrint(e.toString());
            }
        }

        if (!silent) {
            Printer.boldPrintln("\nCannot get the version number from GitHub!\n", Color.ORANGE);
        }

        return null;
    }

    public static List<String> getChangelogFromGitHub(String rawUrl) throws Exception {
        if (!CHANGELOG.isEmpty()) {
            return CHANGELOG;
        }
        URL url = new URL(rawUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line = "";

        while ((line = reader.readLine()) != null) {
            CHANGELOG.add(line);
        }

        reader.close();
        return CHANGELOG;
    }

    private static int compareVersions(String version1, String version2, boolean silent) {
        try {

            String[] arr1 = version1.split("\\.");
            String[] arr2 = version2.split("\\.");
            int len = Math.max(arr1.length, arr2.length);
            for (int i = 0; i < len; i++) {
                int v1 = i < arr1.length ? Integer.parseInt(arr1[i]) : 0;
                int v2 = i < arr2.length ? Integer.parseInt(arr2[i]) : 0;
                if (v1 != v2) {
                    return v1 > v2 ? 1 : -1;
                }
            }
        } catch (Exception e) {
            if (!silent) {
                Printer.println(
                        "An error occurred while parsing the version! " + version1 + " " + version2 + " "
                                + e.toString(),
                        Color.RED);
            }
            return silent ? 0 : 1;
        }
        return 0;
    }

    public static boolean askToProceed() throws Exception {
        final int TIMEOUT = 30000; // 30 seconds in milliseconds
        final Scanner scanner = new Scanner(System.in);
        final Timer timer = new Timer();
        final AtomicBoolean inputValid = new AtomicBoolean(false);
        final Thread inputThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String input = scanner.nextLine().trim().toLowerCase();

                    scanner.close();

                    // Treat empty input as equivalent to default value of "Y"
                    if (input.isEmpty()) {
                        input = "n";
                    }

                    inputValid.set(!input.equals("n"));
                } catch (Exception e) {
                    // Ignore exceptions and exit thread
                } finally {
                    LATCH.countDown();
                }
            }
        });

        // Start the timer to abort the application if no input is received in 30
        // seconds
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                inputThread.interrupt();
                SHOULD_EXIT.set(true);
                LATCH.countDown();
            }
        }, TIMEOUT);

        // Prompt the user for input
        Printer.print("Do you wish to proceed? ");
        Printer.print("(y/N)  ", Printer.DEFAULT_COLOR_2);

        // Start the input thread
        inputThread.start();

        // Wait for the input thread to finish or the timer to expire
        try {
            LATCH.await();
        } catch (InterruptedException e) {
            // Ignore interrupts and continue
        }

        // Cancel the timer and return the result
        timer.cancel();
        if (SHOULD_EXIT.get()) {
            Updater.cleanExit("User input timeout!", Color.ORANGE, 0);
        }
        return inputValid.get() && scanner != null;
    }

}
