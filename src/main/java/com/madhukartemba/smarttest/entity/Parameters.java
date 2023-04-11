package com.madhukartemba.smarttest.entity;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.madhukartemba.smarttest.service.PrintService;

public class Parameters {

    public static List<String> paramterNames = Arrays.asList(
            "defaultColor",
            "parallelExecute",
            "gradleCommand",
            "gradleOption",
            "maxParallelThreads",
            "exploreViaPackage",
            "deleteChildFiles",
            "officalMergeRequestPattern",
            "fullTest");

    public static Color DEFAULT_COLOR = Color.decode("#03A9F4");
    public static String GRADLE_COMMAND_NAME = "./gradlew";
    public static String GRADLE_OPTION_NAME = "--tests";
    public static String OFFICIAL_MERGE_REQUEST_PATTERN = "Merge pull request #\\d+ from";
    public static int MAX_PARALLEL_THREADS = 3;
    public static boolean EXPLORE_VIA_PACKAGE = false;
    public static boolean PARALLEL_EXECUTE = true;
    public static boolean DELETE_CHILD_FILES = false;
    public static boolean FULL_TEST = false;

    public static void printValues() {
        PrintService.boldPrintln("Parameters\n");
        PrintService.println("defaultColor: " + DEFAULT_COLOR);
        PrintService.println("parallelExecute: " + PARALLEL_EXECUTE);
        PrintService.println("gradleCommand: " + GRADLE_COMMAND_NAME);
        PrintService.println("gradleOption: " + GRADLE_OPTION_NAME);
        PrintService.println("maxParallelThreads: " + MAX_PARALLEL_THREADS);
        PrintService.println("exporeViaPackage: " + EXPLORE_VIA_PACKAGE);
        PrintService.println("deleteChildFiles: " + DELETE_CHILD_FILES);
        PrintService.println("officialMergeRequestPattern: " + OFFICIAL_MERGE_REQUEST_PATTERN);
        PrintService.println("fullTest: " + FULL_TEST);
    }

}
