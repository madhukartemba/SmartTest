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

    public static Color DEFAULT_COLOR_1 = Color.decode("#03A9F4");
    public static Color DEFAULT_COLOR_2 = Color.decode("#FFD300");
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
        PrintService.formatPrint("defaultColor1: " + DEFAULT_COLOR_1);
        PrintService.formatPrint("defaultColor2: " + DEFAULT_COLOR_2);
        PrintService.formatPrint("parallelExecute: " + PARALLEL_EXECUTE);
        PrintService.formatPrint("gradleCommand: " + GRADLE_COMMAND_NAME);
        PrintService.formatPrint("gradleOption: " + GRADLE_OPTION_NAME);
        PrintService.formatPrint("maxParallelThreads: " + MAX_PARALLEL_THREADS);
        PrintService.formatPrint("exporeViaPackage: " + EXPLORE_VIA_PACKAGE);
        PrintService.formatPrint("deleteChildFiles: " + DELETE_CHILD_FILES);
        PrintService.formatPrint("officialMergeRequestPattern: " + OFFICIAL_MERGE_REQUEST_PATTERN);
        PrintService.formatPrint("fullTest: " + FULL_TEST);
    }

}
