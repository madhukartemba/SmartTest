package com.madhukartemba.smarttest.entity;

import java.awt.Color;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.madhukartemba.smarttest.util.Printer;
import com.madhukartemba.smarttest.util.ThreadUtil;

public class Parameters {

        public static final String GRADLE_OPTION_NAME = "--tests";

        public static Parameter<Boolean> HELP = new Parameter<>("help", "h", false, "Prints the help documentation.");

        public static Parameter<Boolean> VERSION = new Parameter<>("version", "v", false, "Prints the version.");

        public static Parameter<Color> DEFAULT_COLOR_1 = new Parameter<>("defaultColor1", "color1",
                        Color.decode("#03A9F4"),
                        "Sets the default value of the first color to be used in the program. This value should be a hexadecimal color code in the format like this: #03A9F4.");
        public static Parameter<Color> DEFAULT_COLOR_2 = new Parameter<>("defaultColor2", "color2",
                        Color.decode("#FFD300"),
                        "Sets the default value of the second color to be used in the program. This value should be a hexadecimal color code in the format like this: #FFD300.");

        public static Parameter<String> PROJECT_DIR = new Parameter<>("projectDir", "dir", null,
                        "Sets the project directory to be used by the program.");
        public static Parameter<String> GRADLE_COMMAND = new Parameter<>("gradleCommand", "gradlecmd", "./gradlew",
                        "Sets the Gradle command to be used by the program.");
        public static Parameter<String> GIT_COMMAND = new Parameter<>("gitCommand", "gitcmd", "git log --merges",
                        "Sets the Git command to be used by the program.");
        public static Parameter<String> OFFICIAL_MERGE_REQUEST_PATTERN = new Parameter<>("officialMergeRequestPattern",
                        "mergereqpattern", "Merge pull request #\\d+ from",
                        "Sets the official merge request pattern to be used by the program. This pattern should be a regular expression that matches the merge request format used in the project.");

        public static Parameter<Integer> MAX_THREADS = new Parameter<>("maxThreads", "maxth",
                        ThreadUtil.getOptimalThreadCount(),
                        "Sets the maximum number of threads to be used by the program.");

        public static Parameter<Boolean> SKIP_COMPILE_JAVA = new Parameter<>("skipCompileJava", "skipcompile", false,
                        "Flag to execute tasks serially.");
        public static Parameter<Boolean> SERIAL_EXECUTE = new Parameter<>("serialExecute", "serexe", false,
                        "Flag to skip the compilation of the project before testing.");
        public static Parameter<Boolean> REFRESH_DEPENDENCIES = new Parameter<>("refreshDependencies", "refdeps",
                        false, "Flag to refresh dependencies.");
        public static Parameter<Boolean> ASSEMBLE = new Parameter<>("assemble", "asm",
                        false, "Flag to assemble project.");
        public static Parameter<Boolean> CLEAN = new Parameter<>("clean", "c",
                        false,
                        "Flag that removes all generated build files, test results, and other temporary files that are created during the build process.");
        public static Parameter<Boolean> VIA_CLASSNAME = new Parameter<>("viaClassName", "viaclass", false,
                        "Flag to explore files only via class name (slightly faster than normal mode but slightly inefficient as well).");
        public static Parameter<Boolean> VIA_PACKAGE = new Parameter<>("viaPackage", "viapkg", false,
                        "Flag to explore files only via package name (very fast but very inefficient as well).");
        public static Parameter<Boolean> DELETE_CHILD_FILES = new Parameter<>("deleteChildFiles", "delchd", false,
                        "Flag to delete child files after the program ends.");
        public static Parameter<Boolean> FULL_TEST = new Parameter<>("fullTest", "ftest", false,
                        "Flag to perform a full test.");
        public static Parameter<Boolean> PRINT_OUTPUT = new Parameter<>("printOutput", "pout", false,
                        "Flag to print output after the tests are complete.");
        public static Parameter<Boolean> USE_LEGACY_PRINTER = new Parameter<>("useLegacyPrinter", "ulp", false,
                        "Flag to print using the old printer (it does not refresh the text). It maybe useful if you are trying to log the output to a file.");
        public static Parameter<Boolean> UPDATE_APP = new Parameter<>("updateApp", "update", false,
                        "Updates the SmartTest app to the latest version.");

        public static final Map<String, Parameter<Color>> COLOR_PARAMETER_MAP = new HashMap<>();
        public static final Map<String, Parameter<String>> STRING_PARAMETER_MAP = new HashMap<>();
        public static final Map<String, Parameter<Integer>> INTEGER_PARAMETER_MAP = new HashMap<>();
        public static final Map<String, Parameter<Boolean>> BOOLEAN_PARAMETER_MAP = new HashMap<>();

        public static final Map<String, Parameter<?>> PARAMETER_MAP = new HashMap<>();

        public static final List<Parameter<?>> PARAMETERS = Arrays.asList(
                        Parameters.HELP,
                        Parameters.VERSION,
                        Parameters.DEFAULT_COLOR_1,
                        Parameters.DEFAULT_COLOR_2,
                        Parameters.PROJECT_DIR,
                        Parameters.GRADLE_COMMAND,
                        Parameters.GIT_COMMAND,
                        Parameters.OFFICIAL_MERGE_REQUEST_PATTERN,
                        Parameters.MAX_THREADS,
                        Parameters.SKIP_COMPILE_JAVA,
                        Parameters.SERIAL_EXECUTE,
                        Parameters.ASSEMBLE,
                        Parameters.CLEAN,
                        Parameters.REFRESH_DEPENDENCIES,
                        Parameters.VIA_CLASSNAME,
                        Parameters.VIA_PACKAGE,
                        Parameters.DELETE_CHILD_FILES,
                        Parameters.FULL_TEST,
                        Parameters.PRINT_OUTPUT,
                        Parameters.USE_LEGACY_PRINTER,
                        Parameters.UPDATE_APP);

        static {

                COLOR_PARAMETER_MAP.put(Parameters.DEFAULT_COLOR_1.getName(), Parameters.DEFAULT_COLOR_1);
                COLOR_PARAMETER_MAP.put(Parameters.DEFAULT_COLOR_1.getAliasName(), Parameters.DEFAULT_COLOR_1);
                COLOR_PARAMETER_MAP.put(Parameters.DEFAULT_COLOR_2.getName(), Parameters.DEFAULT_COLOR_2);
                COLOR_PARAMETER_MAP.put(Parameters.DEFAULT_COLOR_2.getAliasName(), Parameters.DEFAULT_COLOR_2);

                STRING_PARAMETER_MAP.put(Parameters.PROJECT_DIR.getName(), Parameters.PROJECT_DIR);
                STRING_PARAMETER_MAP.put(Parameters.PROJECT_DIR.getAliasName(), Parameters.PROJECT_DIR);
                STRING_PARAMETER_MAP.put(Parameters.GRADLE_COMMAND.getName(), Parameters.GRADLE_COMMAND);
                STRING_PARAMETER_MAP.put(Parameters.GRADLE_COMMAND.getAliasName(), Parameters.GRADLE_COMMAND);
                STRING_PARAMETER_MAP.put(Parameters.GIT_COMMAND.getName(), Parameters.GIT_COMMAND);
                STRING_PARAMETER_MAP.put(Parameters.GIT_COMMAND.getAliasName(), Parameters.GIT_COMMAND);
                STRING_PARAMETER_MAP.put(Parameters.OFFICIAL_MERGE_REQUEST_PATTERN.getName(),
                                Parameters.OFFICIAL_MERGE_REQUEST_PATTERN);
                STRING_PARAMETER_MAP.put(Parameters.OFFICIAL_MERGE_REQUEST_PATTERN.getAliasName(),
                                Parameters.OFFICIAL_MERGE_REQUEST_PATTERN);

                INTEGER_PARAMETER_MAP.put(Parameters.MAX_THREADS.getName(), Parameters.MAX_THREADS);
                INTEGER_PARAMETER_MAP.put(Parameters.MAX_THREADS.getAliasName(), Parameters.MAX_THREADS);

                BOOLEAN_PARAMETER_MAP.put(Parameters.SKIP_COMPILE_JAVA.getName(), Parameters.SKIP_COMPILE_JAVA);
                BOOLEAN_PARAMETER_MAP.put(Parameters.SKIP_COMPILE_JAVA.getAliasName(), Parameters.SKIP_COMPILE_JAVA);
                BOOLEAN_PARAMETER_MAP.put(Parameters.SERIAL_EXECUTE.getName(), Parameters.SERIAL_EXECUTE);
                BOOLEAN_PARAMETER_MAP.put(Parameters.SERIAL_EXECUTE.getAliasName(), Parameters.SERIAL_EXECUTE);
                BOOLEAN_PARAMETER_MAP.put(Parameters.ASSEMBLE.getName(), Parameters.ASSEMBLE);
                BOOLEAN_PARAMETER_MAP.put(Parameters.ASSEMBLE.getAliasName(), Parameters.ASSEMBLE);
                BOOLEAN_PARAMETER_MAP.put(Parameters.CLEAN.getName(), Parameters.CLEAN);
                BOOLEAN_PARAMETER_MAP.put(Parameters.CLEAN.getAliasName(), Parameters.CLEAN);
                BOOLEAN_PARAMETER_MAP.put(Parameters.REFRESH_DEPENDENCIES.getName(), Parameters.REFRESH_DEPENDENCIES);
                BOOLEAN_PARAMETER_MAP.put(Parameters.REFRESH_DEPENDENCIES.getAliasName(),
                                Parameters.REFRESH_DEPENDENCIES);
                BOOLEAN_PARAMETER_MAP.put(Parameters.VIA_CLASSNAME.getName(), Parameters.VIA_CLASSNAME);
                BOOLEAN_PARAMETER_MAP.put(Parameters.VIA_CLASSNAME.getAliasName(), Parameters.VIA_CLASSNAME);
                BOOLEAN_PARAMETER_MAP.put(Parameters.VIA_PACKAGE.getName(), Parameters.VIA_PACKAGE);
                BOOLEAN_PARAMETER_MAP.put(Parameters.VIA_PACKAGE.getAliasName(), Parameters.VIA_PACKAGE);
                BOOLEAN_PARAMETER_MAP.put(Parameters.DELETE_CHILD_FILES.getName(), Parameters.DELETE_CHILD_FILES);
                BOOLEAN_PARAMETER_MAP.put(Parameters.DELETE_CHILD_FILES.getAliasName(), Parameters.DELETE_CHILD_FILES);
                BOOLEAN_PARAMETER_MAP.put(Parameters.FULL_TEST.getName(), Parameters.FULL_TEST);
                BOOLEAN_PARAMETER_MAP.put(Parameters.FULL_TEST.getAliasName(), Parameters.FULL_TEST);
                BOOLEAN_PARAMETER_MAP.put(Parameters.PRINT_OUTPUT.getName(), Parameters.PRINT_OUTPUT);
                BOOLEAN_PARAMETER_MAP.put(Parameters.PRINT_OUTPUT.getAliasName(), Parameters.PRINT_OUTPUT);
                BOOLEAN_PARAMETER_MAP.put(Parameters.USE_LEGACY_PRINTER.getName(), Parameters.USE_LEGACY_PRINTER);
                BOOLEAN_PARAMETER_MAP.put(Parameters.USE_LEGACY_PRINTER.getAliasName(), Parameters.USE_LEGACY_PRINTER);
                BOOLEAN_PARAMETER_MAP.put(Parameters.UPDATE_APP.getName(), Parameters.UPDATE_APP);
                BOOLEAN_PARAMETER_MAP.put(Parameters.UPDATE_APP.getAliasName(), Parameters.UPDATE_APP);
                BOOLEAN_PARAMETER_MAP.put(Parameters.VERSION.getName(), Parameters.VERSION);
                BOOLEAN_PARAMETER_MAP.put(Parameters.VERSION.getAliasName(), Parameters.VERSION);
                BOOLEAN_PARAMETER_MAP.put(Parameters.HELP.getName(), Parameters.HELP);
                BOOLEAN_PARAMETER_MAP.put(Parameters.HELP.getAliasName(), Parameters.HELP);

                PARAMETER_MAP.putAll(COLOR_PARAMETER_MAP);
                PARAMETER_MAP.putAll(STRING_PARAMETER_MAP);
                PARAMETER_MAP.putAll(INTEGER_PARAMETER_MAP);
                PARAMETER_MAP.putAll(BOOLEAN_PARAMETER_MAP);

        }

        public static void main(String[] args) {
                printValues();
                printHelpAndExit();
        }

        public static boolean isParameter(String str) {
                return PARAMETER_MAP.containsKey(str);
        }

        public static void printValues() {
                Printer.boldPrintln("Parameters\n");
                Printer.formatPrint("defaultColor1: " + DEFAULT_COLOR_1.getValue());
                Printer.formatPrint("defaultColor2: " + DEFAULT_COLOR_2.getValue());
                Printer.formatPrint("projectDir: "
                                + (PROJECT_DIR.isModified() ? PROJECT_DIR.getValue() : "(current directory)"));
                Printer.formatPrint("gradleCommand: " + GRADLE_COMMAND.getValue());
                Printer.formatPrint("gitCommand: " + GIT_COMMAND.getValue());
                Printer.formatPrint("officialMergeRequestPattern: " + OFFICIAL_MERGE_REQUEST_PATTERN.getValue());
                Printer.formatPrint("maxThreads: " + MAX_THREADS.getValue()
                                + (MAX_THREADS.isModified() ? " (user provided)" : " (determined automatically)"));
                Printer.formatPrint("serialExecute: " + SERIAL_EXECUTE.getValue());
                Printer.formatPrint("refreshDependencies: " + REFRESH_DEPENDENCIES.getValue());
                Printer.formatPrint("skipCompileJava: " + SKIP_COMPILE_JAVA.getValue());
                Printer.formatPrint("assemble:" + ASSEMBLE.getValue());
                Printer.formatPrint("clean:" + CLEAN.getValue());
                Printer.formatPrint("viaClassname: " + VIA_CLASSNAME.getValue());
                Printer.formatPrint("viaPackage: " + VIA_PACKAGE.getValue());
                Printer.formatPrint("fullTest: " + FULL_TEST.getValue());
                Printer.formatPrint("printOutput: " + PRINT_OUTPUT.getValue());
                Printer.formatPrint("deleteChildFiles: " + DELETE_CHILD_FILES.getValue());
                Printer.formatPrint("useLegacyPrinter: " + USE_LEGACY_PRINTER.getValue());
        }

        public static void printHelpAndExit() {

                Printer.boldFormatPrint("\nUsage: SmartTest [options]");
                Printer.boldFormatPrint("\nOptions:");

                for (Parameter<?> parameter : PARAMETERS) {
                        if (parameter.getValue() instanceof Boolean) {
                                Printer.boldPrintln("\n  " + parameter.getName() + ", " + parameter.getAliasName());
                        } else {

                                Printer.boldPrintln("\n  " + parameter.getName() + ", " + parameter.getAliasName()
                                                + " <value>");
                        }
                        Printer.println("    " + parameter.getDescription(), Printer.DEFAULT_COLOR_2);
                }

                System.exit(0);

        }
}
