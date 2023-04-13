package com.madhukartemba.smarttest.entity;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.madhukartemba.smarttest.service.PrintService;
import com.madhukartemba.smarttest.util.ThreadUtil;

public class Parameters {

    public static List<String> parameterNames = Arrays.asList(
            "defaultColor1",
            "defaultColor2",
            "gradleCommand",
            "maxParallelThreads",
            "gitCommand",
            "officialMergeRequestPattern",
            "parallelExecute",
            "exploreViaPackage",
            "deleteChildFiles",
            "fullTest",
            "printOutput");

    public static Color DEFAULT_COLOR_1 = Color.decode("#03A9F4");
    public static Color DEFAULT_COLOR_2 = Color.decode("#FFD300");
    public static String GRADLE_COMMAND_NAME = "./gradlew";
    public static final String GRADLE_OPTION_NAME = "--tests";
    public static String OFFICIAL_MERGE_REQUEST_PATTERN = "Merge pull request #\\d+ from";
    public static String GIT_COMMAND = "git log --merges";
    public static int MAX_PARALLEL_THREADS = ThreadUtil.getOptimalThreadCount();
    public static boolean USER_PROVIDED_THREAD_COUNT = false;
    public static boolean EXPLORE_VIA_PACKAGE = false;
    public static boolean PARALLEL_EXECUTE = true;
    public static boolean DELETE_CHILD_FILES = false;
    public static boolean FULL_TEST = false;
    public static boolean PRINT_OUTPUT = false;

    public static void printValues() {
        PrintService.boldPrintln("Parameters\n");
        PrintService.formatPrint("defaultColor1: " + DEFAULT_COLOR_1);
        PrintService.formatPrint("defaultColor2: " + DEFAULT_COLOR_2);
        PrintService.formatPrint("parallelExecute: " + PARALLEL_EXECUTE);
        PrintService.formatPrint("gradleCommand: " + GRADLE_COMMAND_NAME);
        PrintService.formatPrint("maxParallelThreads: " + MAX_PARALLEL_THREADS
                + (USER_PROVIDED_THREAD_COUNT ? " (user provided)" : " (determined automatically)"));
        PrintService.formatPrint("exporeViaPackage: " + EXPLORE_VIA_PACKAGE);
        PrintService.formatPrint("deleteChildFiles: " + DELETE_CHILD_FILES);
        PrintService.formatPrint("gitCommand: " + GIT_COMMAND);
        PrintService.formatPrint("officialMergeRequestPattern: " + OFFICIAL_MERGE_REQUEST_PATTERN);
        PrintService.formatPrint("fullTest: " + FULL_TEST);
        PrintService.formatPrint("printOutput: " + PRINT_OUTPUT);
    }

    public static void printHelp(String args[]) {
        for (String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                // Print out the help information and options
                PrintService.println("Usage: SmartTest [options]");
                PrintService.println("Options:");
                PrintService.println("  --argName=value  Description of argument");
                PrintService.println("  -h, --help       Print this help message and exit");
                PrintService.println("Program Options: (argName: value default-value)");

                // Print out the default values of the program options
                for (String paramName : parameterNames) {
                    Object paramValue = null;
                    switch (paramName) {
                        case "defaultColor1":
                            paramValue = "HEXCODE " + DEFAULT_COLOR_1.toString();
                            break;
                        case "defaultColor2":
                            paramValue = "HEXCODE " + DEFAULT_COLOR_2.toString();
                            break;
                        case "parallelExecute":
                            paramValue = "(true or false) " + PARALLEL_EXECUTE;
                            break;
                        case "gradleCommand":
                            paramValue = "string " + GRADLE_COMMAND_NAME;
                            break;
                        case "maxParallelThreads":
                            paramValue = "number " + MAX_PARALLEL_THREADS + " (determined automatically)";
                            break;
                        case "exploreViaPackage":
                            paramValue = "(true or false) " + EXPLORE_VIA_PACKAGE;
                            break;
                        case "deleteChildFiles":
                            paramValue = "(true or false) " + DELETE_CHILD_FILES;
                            break;
                        case "officialMergeRequestPattern":
                            paramValue = "string " + OFFICIAL_MERGE_REQUEST_PATTERN;
                            break;
                        case "fullTest":
                            paramValue = "(true or false) " + FULL_TEST;
                            break;
                        case "printOutput":
                            paramValue = "(true or false) " + PRINT_OUTPUT;
                            break;
                        case "gitCommand":
                            paramValue = "string " + GIT_COMMAND;
                            break;
                        default:
                            break;
                    }
                    PrintService.formatPrint("  " + paramName + ": " + paramValue);
                }

                System.exit(0);
            }
        }

    }

}
