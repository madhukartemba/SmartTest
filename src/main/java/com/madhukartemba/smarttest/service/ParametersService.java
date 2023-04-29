package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.util.List;

import com.madhukartemba.smarttest.SmartTest;
import com.madhukartemba.smarttest.entity.Parameter;
import com.madhukartemba.smarttest.entity.ParametersNew;
import com.madhukartemba.smarttest.util.Printer;

public class ParametersService {

    public static void setParameters(List<String> args) {

        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);

            Parameter<Color> colorParameter = ParametersNew.COLOR_PARAMETER_MAP.getOrDefault(arg, null);
            if (colorParameter != null) {
                Color color = getColorFromString(args.get(i + 1));
                colorParameter.setValue(color);
                i++;
                continue;
            }

            Parameter<String> stringParameter = ParametersNew.STRING_PARAMETER_MAP.getOrDefault(arg, null);
            if (stringParameter != null) {
                stringParameter.setValue(args.get(i + 1));
                i++;
                continue;
            }

            Parameter<Integer> integerParameter = ParametersNew.INTEGER_PARAMETER_MAP.getOrDefault(arg, null);
            if (integerParameter != null) {
                integerParameter.setValue(Integer.parseInt(args.get(i + 1)));
                i++;
                continue;
            }

            Parameter<Boolean> booleanParameter = ParametersNew.BOOLEAN_PARAMETER_MAP.getOrDefault(arg, null);
            if (booleanParameter != null) {
                booleanParameter.setValue(true);
                continue;
            }

            SmartTest.exitWithCode(arg + " is not a valid parameter! Type 'SmartTest --help' to get more information.",
                    Color.RED, 1);

        }
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
