package com.madhukartemba.smarttest.util;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import com.madhukartemba.smarttest.SmartTest;
import com.madhukartemba.smarttest.service.FileService;
import com.madhukartemba.smarttest.service.RunnerService;

public class Updater {
    private static String SYSTEM_DIR = System.getProperty("user.dir");
    private static String PROJECT_DIR = (SYSTEM_DIR.endsWith("/") ? SYSTEM_DIR : SYSTEM_DIR + "/");

    private static final String REPO_URL = "https://github.com/madhukartemba/SmartTest.git";
    private static final String VERSION_URL = "https://raw.githubusercontent.com/madhukartemba/SmartTest/main/VERSION.txt";
    private static final String UPDATE_DIR_NAME = "SmartTestUpdateFolder/";
    private static final String REPO_DIR_NAME = "SmartTest/";
    private static final String INSTALL_DIR_NAME = "install/";

    private static final String UPDATE_DIR = PROJECT_DIR + UPDATE_DIR_NAME;
    private static final String INSTALL_DIR = UPDATE_DIR + REPO_DIR_NAME + INSTALL_DIR_NAME;

    public static void main(String[] args) throws Exception {
        // Updater.updateApplication();
        // String GITHUB_VERSION = getVersionNumberFromGithub(VERSION_URL);
        System.out.println(compareVersions(SmartTest.VERSION, "1.2.0"));
    }

    public static void updateApplication() throws Exception {
        SmartTest.printLogoAndVersion();

        Printer.boldPrintln("Starting to update application to the latest version...");

        Printer.println("\nCreating a temporary output directory...");
        if (FileService.directoryExists(UPDATE_DIR)) {
            Printer.println("Output directory already exists, deleting it...", Color.YELLOW);
            FileService.deleteDirectory(UPDATE_DIR);
            Printer.println("Previous output directory deleted successfully!", Color.GREEN);
        }
        FileService.createDirectory(UPDATE_DIR, true);

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
                    "An error occured while requesting for update permission! Error code: " + exitCode, exitCode);
        }

        Printer.println("\nExecute permission granted!", Color.GREEN);

        Printer.boldPrintln("\n\nInstalling latest version, please enter your password if prompted...\n");

        String installCommand = "sudo ./install.sh";

        exitCode = RunnerService.lightWeightExecute(installCommand, INSTALL_DIR);

        if (exitCode != 0) {
            Updater.cleanExit("An error occured during installation! Error code: " + exitCode, 1);
        }

        Updater.cleanExit("Installation completed successfully!", exitCode);
    }

    private static void cleanExit(String message, int exitCode) throws Exception {
        if (FileService.directoryExists(UPDATE_DIR)) {
            FileService.deleteDirectory(UPDATE_DIR);
        }

        SmartTest.exitWithCode(message, exitCode);
    }

    private static String getVersionNumberFromGithub(String rawUrl) throws Exception {
        URL url = new URL(rawUrl);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.startsWith("VERSION")) {
                String versionNumber = line.substring("VERSION".length()).trim();
                reader.close();
                return versionNumber;
            }
        }
        reader.close();
        return null;
    }

    private static int compareVersions(String version1, String version2) {
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
        return 0;
    }

}
