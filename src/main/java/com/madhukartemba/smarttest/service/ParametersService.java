package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.madhukartemba.smarttest.SmartTest;
import com.madhukartemba.smarttest.entity.Parameter;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.util.Printer;

public class ParametersService {

    public static void main(String[] args) {
        List<String> inputArgs = Arrays.asList("-mergereqpatterns", "refdeps", "-refdeps");
        setParameters(inputArgs);

        Map<String, String> map = new HashMap<>();
        map.put("apple", "fruit");
        map.put("banana", "fruit");
        map.put("carrot", "vegetable");
        map.put("potato", "vegetable");

        String input = "appel";
        String closestMatch = findClosestMatch(input, map);

        Printer.formatPrint("Input: " + input);
        Printer.formatPrint("Closest match: " + closestMatch);
    }

    public static void setParameters(List<String> args) {

        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);

            if (!Parameters.isParameter(arg)) {
                String closestMatch = findClosestMatch(arg, Parameters.PARAMETER_MAP);

                SmartTest.exitWithCode(
                        "'" + arg + "' is not a valid parameter!\n\nDid you mean \'" + closestMatch
                                + "\' ?\n\nType 'SmartTest --help' to get more information.",
                        Color.RED, 1);
            }

            Parameter<Color> colorParameter = Parameters.COLOR_PARAMETER_MAP.getOrDefault(arg, null);
            if (colorParameter != null) {
                String nextArg = getNextParameterWithCheck(i, args);
                nextArg = removeQuotesIfPresent(nextArg);
                Color color = getColorFromString(nextArg);
                colorParameter.setValue(color);
                i++;
                continue;
            }

            Parameter<String> stringParameter = Parameters.STRING_PARAMETER_MAP.getOrDefault(arg, null);
            if (stringParameter != null) {
                String nextArg = getNextParameterWithCheck(i, args);
                nextArg = removeQuotesIfPresent(nextArg);
                stringParameter.setValue(nextArg);
                i++;
                continue;
            }

            Parameter<Integer> integerParameter = Parameters.INTEGER_PARAMETER_MAP.getOrDefault(arg, null);
            if (integerParameter != null) {
                String nextArg = getNextParameterWithCheck(i, args);
                nextArg = removeQuotesIfPresent(nextArg);
                integerParameter.setValue(Integer.parseInt(nextArg));
                i++;
                continue;
            }

            Parameter<Boolean> booleanParameter = Parameters.BOOLEAN_PARAMETER_MAP.getOrDefault(arg, null);
            if (booleanParameter != null) {
                booleanParameter.setValue(true);
                continue;
            }

        }
    }

    public static String getNextParameterWithCheck(int i, List<String> args) {
        if (i + 1 >= args.size() || Parameters.isParameter(args.get(i + 1))) {
            SmartTest.exitWithCode(
                    "'" + args.get(i) + "' expects a value after it! Type 'SmartTest --help' to get more information.",
                    Color.RED, 1);
        }
        return args.get(i + 1);
    }

    public static String removeQuotesIfPresent(String str) {
        if ((str.startsWith("'") && str.endsWith("'"))
                || (str.startsWith("\"") && str.endsWith("\""))) {
            str = str.substring(1, str.length() - 1);
        }
        return str;
    }

    public static Color getColorFromString(String colorString) {
        try {
            // Try to decode the color string
            return Color.decode(colorString);
        } catch (NumberFormatException e) {
            // Handle invalid color string
            Printer.println("\nInvalid color string given in args: " + colorString, Color.ORANGE);
            return Printer.DEFAULT_COLOR_1;
        }
    }

    public static String findClosestMatch(String input, Map<String, ?> map) {
        String closestMatch = null;
        int closestDistance = Integer.MAX_VALUE;

        for (String key : map.keySet()) {
            int distance = levenshteinDistance(input, key);
            if (distance < closestDistance) {
                closestMatch = key;
                closestDistance = distance;
            }
        }

        return closestMatch;
    }

    private static int levenshteinDistance(String s1, String s2) {
        int[][] matrix = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 1; i <= s1.length(); i++) {
            matrix[i][0] = i;
        }
        for (int j = 1; j <= s2.length(); j++) {
            matrix[0][j] = j;
        }

        for (int j = 1; j <= s2.length(); j++) {
            for (int i = 1; i <= s1.length(); i++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    matrix[i][j] = matrix[i - 1][j - 1];
                } else {
                    matrix[i][j] = 1 + Math.min(matrix[i - 1][j], Math.min(matrix[i][j - 1], matrix[i - 1][j - 1]));
                }
            }
        }

        return matrix[s1.length()][s2.length()];
    }

}
