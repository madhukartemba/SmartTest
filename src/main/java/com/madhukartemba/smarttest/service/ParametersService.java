package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.util.Map;

import com.madhukartemba.smarttest.entity.Parameters;

public class ParametersService {

    public static void setParameters(Map<String, String> argsMap) {

        if (argsMap.containsKey("maxParallelThreads")) {
            int maxParallelThreads = Integer.parseInt(argsMap.get("maxParallelThreads"));
            Parameters.MAX_PARALLEL_THREADS = maxParallelThreads;
        }

        if (argsMap.containsKey("exploreViaPackage")) {
            boolean exploreViaPackage = argsMap.get("exploreViaPackage").equals("true");
            Parameters.EXPLORE_VIA_PACKAGE = exploreViaPackage;
        }

        if (argsMap.containsKey("fullTest")) {
            boolean fullTest = argsMap.get("fullTest").equals("true");
            Parameters.FULL_TEST = fullTest;
        }

        if (argsMap.containsKey("parallelExecute")) {
            boolean parallelExecute = argsMap.get("parallelExecute").equals("true");
            Parameters.PARALLEL_EXECUTE = parallelExecute;
        }

        if (argsMap.containsKey("deleteChildFiles")) {
            boolean deleteChildFiles = argsMap.get("deleteChildFiles").equals("true");
            Parameters.DELETE_CHILD_FILES = deleteChildFiles;
        }

        if (argsMap.containsKey("gradleCommand")) {
            String gradleCommand = argsMap.get("gradleCommand");
            Parameters.GRADLE_COMMAND_NAME = gradleCommand;
        }

        if (argsMap.containsKey("gitCommand")) {
            String gitCommand = argsMap.get("gitCommand");
            Parameters.GIT_COMMAND = gitCommand;
        }

        if (argsMap.containsKey("gradleOption")) {
            String gradleOption = argsMap.get("gradleOption");
            Parameters.GRADLE_OPTION_NAME = gradleOption;
        }

        if (argsMap.containsKey("officialMergeRequestPattern")) {
            String officialMergeRequestPattern = argsMap.get("officialMergeRequestPattern");
            Parameters.OFFICIAL_MERGE_REQUEST_PATTERN = officialMergeRequestPattern;
        }

        if (argsMap.containsKey("defaultColor1")) {
            Parameters.DEFAULT_COLOR_1 = getColorFromString(argsMap.get("defaultColor1"));
        }

        if (argsMap.containsKey("defaultColor2")) {
            Parameters.DEFAULT_COLOR_2 = getColorFromString(argsMap.get("defaultColor2"));
        }

    }

    public static Color getColorFromString(String colorString) {
        try {
            // Try to decode the color string
            return Color.decode(colorString);
        } catch (NumberFormatException e) {
            // Handle invalid color string
            PrintService.println("\nInvalid color string given in args: " + colorString, Color.RED);
            return Color.decode("#03A9F4");
        }
    }

}
