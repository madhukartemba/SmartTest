package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.util.Arrays;
import java.util.List;

import com.madhukartemba.smarttest.SmartTest;
import com.madhukartemba.smarttest.entity.Parameter;
import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.util.Printer;

public class ParametersService {

    public static void main(String[] args) {
        List<String> input = Arrays.asList("mergereqpattern", "refdeps", "refdeps");
        setParameters(input);
    }

    public static void setParameters(List<String> args) {

        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);

            if (!Parameters.isParameter(arg)) {
                SmartTest.exitWithCode(
                        "'" + arg + "' is not a valid parameter! Type 'SmartTest --help' to get more information.",
                        Color.RED, 1);
            }

            Parameter<Color> colorParameter = Parameters.COLOR_PARAMETER_MAP.getOrDefault(arg, null);
            if (colorParameter != null) {
                String nextArg = args.get(i + 1);
                checkIsNextArgAParameter(arg, nextArg);
                nextArg = removeQuotesIfPresent(nextArg);
                Color color = getColorFromString(args.get(i + 1));
                colorParameter.setValue(color);
                i++;
                continue;
            }

            Parameter<String> stringParameter = Parameters.STRING_PARAMETER_MAP.getOrDefault(arg, null);
            if (stringParameter != null) {
                String nextArg = args.get(i + 1);
                checkIsNextArgAParameter(arg, nextArg);
                nextArg = removeQuotesIfPresent(nextArg);
                stringParameter.setValue(nextArg);
                i++;
                continue;
            }

            Parameter<Integer> integerParameter = Parameters.INTEGER_PARAMETER_MAP.getOrDefault(arg, null);
            if (integerParameter != null) {
                String nextArg = args.get(i + 1);
                checkIsNextArgAParameter(arg, nextArg);
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

    public static void checkIsNextArgAParameter(String arg, String nextArg) {
        if (Parameters.isParameter(nextArg)) {
            SmartTest.exitWithCode(
                    "'" + arg + "' expects a value after it! Type 'SmartTest --help' to get more information.",
                    Color.RED, 1);
        }
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
            Printer.println("\nInvalid color string given in args: " + colorString, Color.RED);
            return Color.decode("#03A9F4");
        }
    }

}
