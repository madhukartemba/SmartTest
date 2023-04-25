package com.madhukartemba.smarttest.entity;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.madhukartemba.smarttest.util.Printer;
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
        Printer.boldPrintln("Parameters\n");
        Printer.formatPrint("defaultColor1: " + DEFAULT_COLOR_1);
        Printer.formatPrint("defaultColor2: " + DEFAULT_COLOR_2);
        Printer.formatPrint("parallelExecute: " + PARALLEL_EXECUTE);
        Printer.formatPrint("gradleCommand: " + GRADLE_COMMAND_NAME);
        Printer.formatPrint("maxParallelThreads: " + MAX_PARALLEL_THREADS
                + (USER_PROVIDED_THREAD_COUNT ? " (user provided)" : " (determined automatically)"));
        Printer.formatPrint("exporeViaPackage: " + EXPLORE_VIA_PACKAGE);
        Printer.formatPrint("deleteChildFiles: " + DELETE_CHILD_FILES);
        Printer.formatPrint("gitCommand: " + GIT_COMMAND);
        Printer.formatPrint("officialMergeRequestPattern: " + OFFICIAL_MERGE_REQUEST_PATTERN);
        Printer.formatPrint("fullTest: " + FULL_TEST);
        Printer.formatPrint("printOutput: " + PRINT_OUTPUT);
    }

    public static void printHelp(String args[]) {
        for (String arg : args) {
            if (arg.equals("-h") || arg.equals("--help")) {
                // Print out the help information and options
                Printer.println("Usage: SmartTest [options]");
                Printer.println("Options:");
                Printer.println("  --argName=value  Description of argument");
                Printer.println("  -h, --help       Print this help message and exit");
                Printer.println("Program Options: (argName: value default-value)");

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
                    Printer.formatPrint("  " + paramName + ": " + paramValue);
                }

                System.exit(0);
            }
        }

    }

}
