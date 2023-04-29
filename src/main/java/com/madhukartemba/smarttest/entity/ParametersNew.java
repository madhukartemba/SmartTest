package com.madhukartemba.smarttest.entity;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.madhukartemba.smarttest.util.Printer;
import com.madhukartemba.smarttest.util.ThreadUtil;

public class ParametersNew {

    public static final String GRADLE_OPTION_NAME = "--tests";

    public static Parameter<Color> DEFAULT_COLOR_1 = new Parameter<>("defaultColor1", "color1",
            Color.decode("#03A9F4"));
    public static Parameter<Color> DEFAULT_COLOR_2 = new Parameter<>("defaultColor2", "color2",
            Color.decode("#FFD300"));

    public static Parameter<String> PROJECT_DIR = new Parameter<>("projectDir", "dir", null);
    public static Parameter<String> GRADLE_COMMAND = new Parameter<>("gradleCommand", "gradlecmd", "./gradlew");
    public static Parameter<String> GIT_COMMAND = new Parameter<>("gitCommand", "gitcmd", "git log --merges");
    public static Parameter<String> OFFICIAL_MERGE_REQUEST_PATTERN = new Parameter<>("officialMergeRequestPattern",
            "mergereqpattern", "Merge pull request #\\d+ from");

    public static Parameter<Integer> MAX_THREADS = new Parameter<>("maxThreads", "maxth",
            ThreadUtil.getOptimalThreadCount());

    public static Parameter<Boolean> SKIP_COMPILE_JAVA = new Parameter<>("skipCompileJava", "skipcompile", false);
    public static Parameter<Boolean> SERIAL_EXECUTE = new Parameter<>("serialExecute", "serexe", false);
    public static Parameter<Boolean> REFRESH_DEPENDENCIES = new Parameter<>("refreshDependencies", "refdeps", false);
    public static Parameter<Boolean> VERIFY_PACKAGE = new Parameter<>("verifyPackage", "verpkg", false);
    public static Parameter<Boolean> DELETE_CHILD_FILES = new Parameter<>("deleteChildFiles", "delchd", false);
    public static Parameter<Boolean> FULL_TEST = new Parameter<>("fullTest", "ftest", false);
    public static Parameter<Boolean> PRINT_OUTPUT = new Parameter<>("printOutput", "pout", false);
    public static Parameter<Boolean> USE_LEGACY_PRINTER = new Parameter<>("useLegacyPrinter", "ulp", false);

    public static final Map<String, Parameter<Color>> COLOR_PARAMETER_MAP = new HashMap<>();
    public static final Map<String, Parameter<String>> STRING_PARAMETER_MAP = new HashMap<>();
    public static final Map<String, Parameter<Integer>> INTEGER_PARAMETER_MAP = new HashMap<>();
    public static final Map<String, Parameter<Boolean>> BOOLEAN_PARAMETER_MAP = new HashMap<>();

    static {
        COLOR_PARAMETER_MAP.put(ParametersNew.DEFAULT_COLOR_1.getName(), ParametersNew.DEFAULT_COLOR_1);
        COLOR_PARAMETER_MAP.put(ParametersNew.DEFAULT_COLOR_1.getAliasName(), ParametersNew.DEFAULT_COLOR_1);
        COLOR_PARAMETER_MAP.put(ParametersNew.DEFAULT_COLOR_2.getName(), ParametersNew.DEFAULT_COLOR_2);
        COLOR_PARAMETER_MAP.put(ParametersNew.DEFAULT_COLOR_2.getAliasName(), ParametersNew.DEFAULT_COLOR_2);

        STRING_PARAMETER_MAP.put(ParametersNew.PROJECT_DIR.getName(), ParametersNew.PROJECT_DIR);
        STRING_PARAMETER_MAP.put(ParametersNew.PROJECT_DIR.getAliasName(), ParametersNew.PROJECT_DIR);
        STRING_PARAMETER_MAP.put(ParametersNew.GRADLE_COMMAND.getName(), ParametersNew.GRADLE_COMMAND);
        STRING_PARAMETER_MAP.put(ParametersNew.GRADLE_COMMAND.getAliasName(), ParametersNew.GRADLE_COMMAND);
        STRING_PARAMETER_MAP.put(ParametersNew.GIT_COMMAND.getName(), ParametersNew.GIT_COMMAND);
        STRING_PARAMETER_MAP.put(ParametersNew.GIT_COMMAND.getAliasName(), ParametersNew.GIT_COMMAND);
        STRING_PARAMETER_MAP.put(ParametersNew.OFFICIAL_MERGE_REQUEST_PATTERN.getName(),
                ParametersNew.OFFICIAL_MERGE_REQUEST_PATTERN);
        STRING_PARAMETER_MAP.put(ParametersNew.OFFICIAL_MERGE_REQUEST_PATTERN.getAliasName(),
                ParametersNew.OFFICIAL_MERGE_REQUEST_PATTERN);

        INTEGER_PARAMETER_MAP.put(ParametersNew.MAX_THREADS.getName(), ParametersNew.MAX_THREADS);
        INTEGER_PARAMETER_MAP.put(ParametersNew.MAX_THREADS.getAliasName(), ParametersNew.MAX_THREADS);

        BOOLEAN_PARAMETER_MAP.put(ParametersNew.SKIP_COMPILE_JAVA.getName(), ParametersNew.SKIP_COMPILE_JAVA);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.SKIP_COMPILE_JAVA.getAliasName(), ParametersNew.SKIP_COMPILE_JAVA);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.SERIAL_EXECUTE.getName(), ParametersNew.SERIAL_EXECUTE);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.SERIAL_EXECUTE.getAliasName(), ParametersNew.SERIAL_EXECUTE);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.REFRESH_DEPENDENCIES.getName(), ParametersNew.REFRESH_DEPENDENCIES);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.REFRESH_DEPENDENCIES.getAliasName(),
                ParametersNew.REFRESH_DEPENDENCIES);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.VERIFY_PACKAGE.getName(), ParametersNew.VERIFY_PACKAGE);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.VERIFY_PACKAGE.getAliasName(), ParametersNew.VERIFY_PACKAGE);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.DELETE_CHILD_FILES.getName(), ParametersNew.DELETE_CHILD_FILES);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.DELETE_CHILD_FILES.getAliasName(), ParametersNew.DELETE_CHILD_FILES);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.FULL_TEST.getName(), ParametersNew.FULL_TEST);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.FULL_TEST.getAliasName(), ParametersNew.FULL_TEST);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.PRINT_OUTPUT.getName(), ParametersNew.PRINT_OUTPUT);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.PRINT_OUTPUT.getAliasName(), ParametersNew.PRINT_OUTPUT);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.USE_LEGACY_PRINTER.getName(), ParametersNew.USE_LEGACY_PRINTER);
        BOOLEAN_PARAMETER_MAP.put(ParametersNew.USE_LEGACY_PRINTER.getAliasName(), ParametersNew.USE_LEGACY_PRINTER);
    }

    public static void main(String[] args) {
        printValues();
        printHelpAndExit();
    }

    public static boolean isParameter(String str) {
        return COLOR_PARAMETER_MAP.containsKey(str) || STRING_PARAMETER_MAP.containsKey(str)
                || INTEGER_PARAMETER_MAP.containsKey(str) || BOOLEAN_PARAMETER_MAP.containsKey(str);

    }

    public static void printValues() {
        Printer.boldPrintln("Parameters\n");

        // for (Parameter<?> parameter : PARAMETER_MAP.values()) {
        // Printer.formatPrint(parameter.getName() + ": " + parameter.getValue());
        // }

        Printer.formatPrint("defaultColor1: " + DEFAULT_COLOR_1.getValue());
        Printer.formatPrint("defaultColor2: " + DEFAULT_COLOR_2.getValue());
        Printer.formatPrint("projectDir: " + (PROJECT_DIR.isModified() ? PROJECT_DIR.getValue() : "current directory"));
        Printer.formatPrint("gradleCommand: " + GRADLE_COMMAND.getValue());
        Printer.formatPrint("gitCommand: " + GIT_COMMAND.getValue());
        Printer.formatPrint("officialMergeRequestPattern: " + OFFICIAL_MERGE_REQUEST_PATTERN.getValue());
        Printer.formatPrint("maxThreads: " + MAX_THREADS.getValue()
                + (MAX_THREADS.isModified() ? " (user provided)" : " (determined automatically)"));
        Printer.formatPrint("parallelExecute: " + SERIAL_EXECUTE.getValue());
        Printer.formatPrint("refreshDependencies: " + REFRESH_DEPENDENCIES.getValue());
        Printer.formatPrint("skipCompileJava: " + SKIP_COMPILE_JAVA.getValue());
        Printer.formatPrint("verifyPackage: " + VERIFY_PACKAGE.getValue());
        Printer.formatPrint("fullTest: " + FULL_TEST.getValue());
        Printer.formatPrint("printOutput: " + PRINT_OUTPUT.getValue());
        Printer.formatPrint("deleteChildFiles: " + DELETE_CHILD_FILES.getValue());
        Printer.formatPrint("useLegacyPrinter: " + USE_LEGACY_PRINTER.getValue());
    }

    public static void printHelpAndExit() {

        Printer.formatPrint("Usage: SmartTest [options]");
        Printer.formatPrint("Options:");
        Printer.formatPrint("  --help, -h      : Print this help message and exit");
        Printer.formatPrint("  --version, -v   : Print the version and exit");

        Printer.println(
                "\n  --defaultColor1, -color1 <value>");
        Printer.println(
                "    Sets the default value of the first color to be used in the program. This value should be a hexadecimal color code in the format like this: #03A9F4.",
                Printer.DEFAULT_COLOR_2);

        Printer.println(
                "\n  --defaultColor2, -color2 <value>");
        Printer.println(
                "    Sets the default value of the second color to be used in the program. This value should be a hexadecimal color code in the format like this: #FFD300.",
                Printer.DEFAULT_COLOR_2);

        Printer.println("\n  --projectDir, -dir <value>");
        Printer.println("    Sets the project directory to be used by the program.", Printer.DEFAULT_COLOR_2);

        Printer.println("\n  --gradleCommand, -gradlecmd <value>");
        Printer.println("    Sets the Gradle command to be used by the program.", Printer.DEFAULT_COLOR_2);

        Printer.println("\n  --gitCommand, -gitcmd <value>");
        Printer.println("    Sets the Git command to be used by the program.", Printer.DEFAULT_COLOR_2);

        Printer.println(
                "\n  --officialMergeRequestPattern, -mergereqpattern <value>");
        Printer.println(
                "    Sets the official merge request pattern to be used by the program. This pattern should be a regular expression that matches the merge request format used in the project.",
                Printer.DEFAULT_COLOR_2);

        Printer.println(
                "\n  --maxThreads, -maxth <value>");
        Printer.println("    Sets the maximum number of threads to be used by the program.", Printer.DEFAULT_COLOR_2);

        Printer.println(
                "\n  --serialExecute, -serexe");
        Printer.println("    Flag to execute tasks serially.", Printer.DEFAULT_COLOR_2);

        Printer.println(
                "\n  --skipCompileJava, -skipcompile");
        Printer.println("    Flag to skip the compilation of the project before testing.", Printer.DEFAULT_COLOR_2);

        Printer.println(
                "\n  --refreshDependencies, -refdeps");
        Printer.println("    Flag to refresh dependencies.", Printer.DEFAULT_COLOR_2);

        Printer.println(
                "\n  --verifyPackage, -verpkg");
        Printer.println("    Flag to verify the package also along with the classname while exploring files.",
                Printer.DEFAULT_COLOR_2);

        Printer.println("\n  --fullTest, -ftest");
        Printer.println("    Flag to perform a full test.", Printer.DEFAULT_COLOR_2);

        Printer.println("\n  --printOutput, -pout");
        Printer.println("    Flag to print output after the tests are complete.", Printer.DEFAULT_COLOR_2);

        Printer.println(
                "\n  --deleteChildFiles, -delchd");
        Printer.println("    Flag to delete child files after the program ends.", Printer.DEFAULT_COLOR_2);

        Printer.println(
                "\n  --useLegacyPrinter, -ulp");
        Printer.println(
                "    Flag to print using the old printer (it does not refresh the text). It maybe useful if you are trying to log the output to a file.",
                Printer.DEFAULT_COLOR_2);

        Printer.println("\n");
        System.exit(0);

    }
}