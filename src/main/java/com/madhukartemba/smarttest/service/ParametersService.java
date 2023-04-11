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

        if (argsMap.containsKey("gradleOption")) {
            String gradleOption = argsMap.get("gradleOption");
            Parameters.GRADLE_OPTION_NAME = gradleOption;
        }

        if (argsMap.containsKey("officialMergeRequestPattern")) {
            String officialMergeRequestPattern = argsMap.get("officialMergeRequestPattern");
            Parameters.OFFICIAL_MERGE_REQUEST_PATTERN = officialMergeRequestPattern;
        }

        if (argsMap.containsKey("defaultColor")) {
            Parameters.DEFAULT_COLOR = getColorFromString(argsMap.get("defaultColor"));
        }

    }

    public static Color getColorFromString(String colorString) {
        try {
            // Try to decode the color string
            return Color.decode(colorString);
        } catch (NumberFormatException e) {
            // Handle invalid color string
            PrintService.println("\nInvalid color string given in args: " + colorString, Color.RED);
            return Color.BLUE;
        }
    }

}
