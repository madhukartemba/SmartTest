package com.madhukartemba.smarttest.util;

import java.util.ArrayList;
import java.util.List;

import com.madhukartemba.smarttest.SmartTest;
import com.madhukartemba.smarttest.entity.Parameters;

public class ArgsParser {
    public static List<String> parseArgs(String[] args) {

        List<String> outputArgs = new ArrayList<>();

        for (String arg : args) {
            if (arg.equals("-v") || arg.equals("--version")) {
                System.out.println(SmartTest.VERSION);
                System.exit(0);
            } else if (arg.equals("-h") || arg.equals("--help")) {
                Parameters.printHelpAndExit();
            }

            outputArgs.add(arg);
        }

        return outputArgs;
    }
}
