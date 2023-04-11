package com.madhukartemba.smarttest.entity;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

public class Parameters {

    public static List<String> paramterNames = Arrays.asList(
            "defaultColor",
            "parallelExecute",
            "gradleCommand",
            "gradleOption",
            "maxParallelThreads",
            "deleteChildFiles",
            "officalMergeRequestPattern");

    public static Color DEFAULT_COLOR = Color.BLUE;
    public static String GRADLE_COMMAND_NAME = "./gradlew";
    public static String GRADLE_OPTION_NAME = "--tests";
    public static String OFFICIAL_MERGE_REQUEST_PATTERN = "Merge pull request #\\d+ from";
    public static int MAX_PARALLEL_THREADS = 3;
    public static boolean PARALLEL_EXECUTE = true;
    public static boolean DELETE_CHILD_FILES = false;

}
