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

    public static final Map<String, Parameter<?>> PARAMETER_MAP = new HashMap<>();

    static {
        PARAMETER_MAP.put(ParametersNew.DEFAULT_COLOR_1.getName(), ParametersNew.DEFAULT_COLOR_1);
        PARAMETER_MAP.put(ParametersNew.DEFAULT_COLOR_1.getAliasName(), ParametersNew.DEFAULT_COLOR_1);
        PARAMETER_MAP.put(ParametersNew.DEFAULT_COLOR_2.getName(), ParametersNew.DEFAULT_COLOR_2);
        PARAMETER_MAP.put(ParametersNew.DEFAULT_COLOR_2.getAliasName(), ParametersNew.DEFAULT_COLOR_2);
        PARAMETER_MAP.put(ParametersNew.PROJECT_DIR.getName(), ParametersNew.PROJECT_DIR);
        PARAMETER_MAP.put(ParametersNew.PROJECT_DIR.getAliasName(), ParametersNew.PROJECT_DIR);
        PARAMETER_MAP.put(ParametersNew.GRADLE_COMMAND.getName(), ParametersNew.GRADLE_COMMAND);
        PARAMETER_MAP.put(ParametersNew.GRADLE_COMMAND.getAliasName(), ParametersNew.GRADLE_COMMAND);
        PARAMETER_MAP.put(ParametersNew.GIT_COMMAND.getName(), ParametersNew.GIT_COMMAND);
        PARAMETER_MAP.put(ParametersNew.GIT_COMMAND.getAliasName(), ParametersNew.GIT_COMMAND);
        PARAMETER_MAP.put(ParametersNew.OFFICIAL_MERGE_REQUEST_PATTERN.getName(),
                ParametersNew.OFFICIAL_MERGE_REQUEST_PATTERN);
        PARAMETER_MAP.put(ParametersNew.OFFICIAL_MERGE_REQUEST_PATTERN.getAliasName(),
                ParametersNew.OFFICIAL_MERGE_REQUEST_PATTERN);
        PARAMETER_MAP.put(ParametersNew.MAX_THREADS.getName(), ParametersNew.MAX_THREADS);
        PARAMETER_MAP.put(ParametersNew.MAX_THREADS.getAliasName(), ParametersNew.MAX_THREADS);
        PARAMETER_MAP.put(ParametersNew.SKIP_COMPILE_JAVA.getName(), ParametersNew.SKIP_COMPILE_JAVA);
        PARAMETER_MAP.put(ParametersNew.SKIP_COMPILE_JAVA.getAliasName(), ParametersNew.SKIP_COMPILE_JAVA);
        PARAMETER_MAP.put(ParametersNew.SERIAL_EXECUTE.getName(), ParametersNew.SERIAL_EXECUTE);
        PARAMETER_MAP.put(ParametersNew.SERIAL_EXECUTE.getAliasName(), ParametersNew.SERIAL_EXECUTE);
        PARAMETER_MAP.put(ParametersNew.REFRESH_DEPENDENCIES.getName(), ParametersNew.REFRESH_DEPENDENCIES);
        PARAMETER_MAP.put(ParametersNew.REFRESH_DEPENDENCIES.getAliasName(), ParametersNew.REFRESH_DEPENDENCIES);
        PARAMETER_MAP.put(ParametersNew.VERIFY_PACKAGE.getName(), ParametersNew.VERIFY_PACKAGE);
        PARAMETER_MAP.put(ParametersNew.VERIFY_PACKAGE.getAliasName(), ParametersNew.VERIFY_PACKAGE);
        PARAMETER_MAP.put(ParametersNew.DELETE_CHILD_FILES.getName(), ParametersNew.DELETE_CHILD_FILES);
        PARAMETER_MAP.put(ParametersNew.DELETE_CHILD_FILES.getAliasName(), ParametersNew.DELETE_CHILD_FILES);
        PARAMETER_MAP.put(ParametersNew.FULL_TEST.getName(), ParametersNew.FULL_TEST);
        PARAMETER_MAP.put(ParametersNew.FULL_TEST.getAliasName(), ParametersNew.FULL_TEST);
        PARAMETER_MAP.put(ParametersNew.PRINT_OUTPUT.getName(), ParametersNew.PRINT_OUTPUT);
        PARAMETER_MAP.put(ParametersNew.PRINT_OUTPUT.getAliasName(), ParametersNew.PRINT_OUTPUT);
        PARAMETER_MAP.put(ParametersNew.USE_LEGACY_PRINTER.getName(), ParametersNew.USE_LEGACY_PRINTER);
        PARAMETER_MAP.put(ParametersNew.USE_LEGACY_PRINTER.getAliasName(), ParametersNew.USE_LEGACY_PRINTER);
    }

    public static void printHelpAndExit() {

        Printer.println("Usage: SmartTest [options]");
        Printer.println("Options:");
        Printer.println("  --argName=value  Description of argument");
        Printer.println("  -h, --help       Print this help message and exit");
        Printer.println("  -v, --version    Print the version and exit");

        Printer.println(
                "--defaultColor1, -color1 <value> \nSets the default value of the first color to be used in the program. This value should be a hexadecimal color code in the format like this: #03A9F4.");

        Printer.println(
                "--defaultColor2, -color2 <value> \nSets the default value of the second color to be used in the program. This value should be a hexadecimal color code in the format like this: #FFD300.");

        Printer.println("--projectDir, -dir <value> \nSets the project directory to be used by the program.");

        Printer.println("--gradleCommand, -gradlecmd <value> \nSets the Gradle command to be used by the program.");

        Printer.println("--gitCommand, -gitcmd <value> \nSets the Git command to be used by the program.");

        Printer.println(
                "--officialMergeRequestPattern, -mergereqpattern <value> \nSets the official merge request pattern to be used by the program. This pattern should be a regular expression that matches the merge request format used in the project.");

        Printer.println(
                "--maxThreads, -maxth <value> \nSets the maximum number of threads to be used by the program.");

        Printer.println(
                "--skipCompileJava, -skipcompile \nFlag to skip the compilation of the project before testing.");

        Printer.println(
                "--serialExecute, -serexe \nFlag to execute tasks serially.");

        Printer.println(
                "--refreshDependencies, -refdeps \nFlag to refresh dependencies.");

        Printer.println(
                "--verifyPackage, -verpkg \nFlag to verify the package also along with the classname while exploring files.");

        Printer.println(
                "--deleteChildFiles, -delchd \nFlag to delete child files after the program ends.");

        Printer.println("--fullTest, -ftest \nFlag to perform a full test.");

        Printer.println("--printOutput, -pout \nFlag to print output after the tests are complete.");

        Printer.println(
                "--useLegacyPrinter, -ulp \nFlag to use the legacy printer.");

        System.exit(0);

    }
}
