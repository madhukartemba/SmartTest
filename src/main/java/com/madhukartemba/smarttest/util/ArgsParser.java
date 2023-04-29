package com.madhukartemba.smarttest.util;

import java.util.ArrayList;
import java.util.List;

import com.madhukartemba.smarttest.SmartTest;
import com.madhukartemba.smarttest.entity.ParametersNew;

public class ArgsParser {
    public static List<String> parseArgs(String[] args) {

        List<String> outputArgs = new ArrayList<>();

        for (String arg : args) {
            if (arg.equals("-v") || arg.equals("--version")) {
                System.out.println(SmartTest.VERSION);
                System.exit(0);
            } else if (arg.equals("-h") || arg.equals("--help")) {
                ParametersNew.printHelpAndExit();
            } else if ((arg.startsWith("'") && arg.endsWith("'"))
                    || (arg.startsWith("\"") && arg.endsWith("\""))) {
                arg = arg.substring(1, arg.length() - 1);
            } else if (arg.startsWith("--")) {
                arg = arg.substring(2);
            } else if (arg.startsWith("-")) {
                arg = arg.substring(1);
            }

            outputArgs.add(arg);
        }

        return outputArgs;
    }
}
