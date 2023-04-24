package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.madhukartemba.smarttest.SmartTest;

public class EnvironmentService {

    public static String PROJECT_DIR = null;
    public static String SYSTEM_DIR = null;
    public static String OS_NAME = null;
    public static boolean ON_SYSTEM_DIR = false;

    public static List<String> PROJECT_NAMES = null;

    public static final String GRADLE_COMMAND_NAME = "./gradlew";
    public static final String GRADLE_OPTION_NAME = "--tests";

    public final static Map<String, String> TEST_DIR_TO_TASK_MAP = new HashMap<String, String>() {
        {
            put("test", "test");
            put("integration-test", "integrationTest");
            put("contract-test", "contractTest");
        }
    };

    public static final List<String> TASK_PRIORITY = Arrays.asList(
            "test",
            "integrationTest",
            "contractTest");

    public static void init() {
        PrintService.boldPrintln("\nEnvironment\n");
        findOperatingSystem();
        findSystemDirectory();
        findProjectDirectory();
        findProjectNames();
        checkDir();
        PrintService.formatPrint("\nTask Priority (decreasing): " + TASK_PRIORITY.toString());
    }

    public static void init(String PROJECT_DIR) {
        PrintService.boldPrintln("\nEnvironment\n");
        EnvironmentService.PROJECT_DIR = PROJECT_DIR;
        PrintService.formatPrint("Input project directory: " + PROJECT_DIR);
        findOperatingSystem();
        findSystemDirectory();
        findProjectNames();
        checkDir();
        PrintService.formatPrint("\nTask Priority (decreasing): " + TASK_PRIORITY.toString());
    }

    public static String findOperatingSystem() {
        if (OS_NAME == null) {
            OS_NAME = System.getProperty("os.name");
            PrintService.formatPrint("Running on: " + OS_NAME);
        }

        return OS_NAME;
    }

    public static String findSystemDirectory() {

        if (SYSTEM_DIR == null) {
            SYSTEM_DIR = System.getProperty("user.dir");
            PrintService.formatPrint("System directory: " + SYSTEM_DIR);
        }

        return SYSTEM_DIR;
    }

    public static String findProjectDirectory() {
        if (PROJECT_DIR == null) {
            PROJECT_DIR = findSystemDirectory();
            if (!PROJECT_DIR.endsWith("/")) {
                PROJECT_DIR += "/";
            }
            PrintService.formatPrint("Project directory: " + PROJECT_DIR);
        }

        return PROJECT_DIR;
    }

    public static List<String> findProjectNames() {
        if (PROJECT_NAMES == null) {
            List<String> subFolders = listTopLevelSubfolders(PROJECT_DIR);
            PrintService.formatPrint("\nFound folders: " + subFolders.toString());

            PROJECT_NAMES = new ArrayList<>();
            for (String subFolder : subFolders) {

                for (String dirName : TEST_DIR_TO_TASK_MAP.keySet()) {
                    String path = PROJECT_DIR + subFolder + "/" + (subFolder.equals("src") ? "" : "src/") + dirName;
                    if (folderExists(path)) {
                        PROJECT_NAMES.add(subFolder);
                        break;
                    }
                }

            }

            PrintService.formatPrint("\nFound projects: " + PROJECT_NAMES.toString());
        }

        if (PROJECT_NAMES == null || PROJECT_NAMES.isEmpty()) {
            SmartTest.exitWithCode("Cannot find any java projects in the current directory!", Color.RED, 1);
        }

        return PROJECT_NAMES;
    }

    public static List<String> listTopLevelSubfolders(String rootFolderName) {
        File rootFolder = new File(rootFolderName);
        File[] subFolders = rootFolder.listFiles(File::isDirectory);

        List<String> subfolderNames = new ArrayList<>();
        for (File subFolder : subFolders) {
            subfolderNames.add(subFolder.getName());
        }
        return subfolderNames;
    }

    public static boolean folderExists(String folderPath) {
        File folder = new File(folderPath);
        return folder.exists() && folder.isDirectory();
    }

    private static void checkDir() {
        ON_SYSTEM_DIR = true;
        if (!SYSTEM_DIR.equals(PROJECT_DIR) && !SYSTEM_DIR.equals(PROJECT_DIR.substring(0, PROJECT_DIR.length() - 1))) {
            ON_SYSTEM_DIR = false;
        }
    }

}
