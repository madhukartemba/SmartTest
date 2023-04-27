package com.madhukartemba.smarttest.util;

import java.util.HashMap;
import java.util.Map;

import com.madhukartemba.smarttest.SmartTest;
import com.madhukartemba.smarttest.entity.Parameters;

public class ArgsParser {
    public static Map<String, String> parseArgs(String[] args) {

        for (String arg : args) {
            if (arg.equals("-v") || arg.equals("--version")) {
                System.out.println(SmartTest.VERSION);
                System.exit(0);
            }
        }

        Parameters.printHelp(args);

        Map<String, String> argsMap = new HashMap<String, String>();

        for (String arg : args) {
            if (arg.startsWith("--")) {
                String[] argParts = arg.split("=");
                if (argParts.length == 2) {
                    String argName = argParts[0].substring(2);
                    String argValue = argParts[1];
                    if ((argValue.startsWith("'") && argValue.endsWith("'"))
                            || (argValue.startsWith("\"") && argValue.endsWith("\""))) {
                        argValue = argValue.substring(1, argValue.length() - 1);
                    }
                    argsMap.put(argName, argValue);
                }
            }
        }

        return argsMap;
    }
}
