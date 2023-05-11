package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.madhukartemba.smarttest.util.CodeParser;
import com.madhukartemba.smarttest.util.FileCleaner;
import com.madhukartemba.smarttest.util.Printer;

public class FileService {
    private String PROJECT_DIR;
    private ProcessBuilder processBuilder;
    private boolean completeRunRequired = false;
    private Path startPath = null;

    private static List<String> ignoredFiles = Arrays.asList(".gitignore", "README.md");

    public FileService() {
        this.PROJECT_DIR = EnvironmentService.PROJECT_DIR;
        if (EnvironmentService.ON_SYSTEM_DIR) {
            startPath = Paths.get(".");
        } else {
            startPath = Paths.get(PROJECT_DIR);
        }
        processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(PROJECT_DIR));
    }

    public FileService(String PROJECT_DIR) {
        this.PROJECT_DIR = PROJECT_DIR;
        if (EnvironmentService.ON_SYSTEM_DIR) {
            startPath = Paths.get(".");
        } else {
            startPath = Paths.get(PROJECT_DIR);
        }
        processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(PROJECT_DIR));
    }

    public boolean isJavaFile(String filePath) {
        return filePath.endsWith(".java");
    }

    public boolean isTestFile(String filePath) {
        if (!isJavaFile(filePath)) {
            return false;
        }

        if (EnvironmentService.ON_SYSTEM_DIR) {
            filePath = PROJECT_DIR + filePath;
        }

        if (!FileService.fileExists(filePath)) {
            return false;
        }

        CodeParser testCodeParser = new CodeParser("@Test");

        try {

            String cleanFileOutput = FileCleaner.clean(Paths.get(filePath));

            return testCodeParser.containsKeyword(cleanFileOutput);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public List<String> getJavaFiles(List<String> inputFiles) {
        return inputFiles.stream().filter(filePath -> isJavaFile(filePath)).collect(Collectors.toList());
    }

    public List<String> getTestFiles(List<String> inputFiles) {
        return inputFiles.stream().filter(filePath -> isTestFile(filePath)).collect(Collectors.toList());
    }

    public boolean analyseGitFiles(List<String> gitFiles) throws Exception {

        completeRunRequired = false;

        int affectedFiles = 0;
        int affectedNonJavaFiles = 0;

        for (String gitFile : gitFiles) {
            if (!isJavaFile(gitFile) && !isIgnored(gitFile)) {
                affectedNonJavaFiles++;
            } else if (!isIgnored(gitFile)) {
                affectedFiles++;
            }
        }

        if (affectedNonJavaFiles > 0) {
            if (affectedFiles == 0) {
                Printer.println("\nOnly the non-java files have been changed, will run all the tests.",
                        Color.YELLOW);
                completeRunRequired = true;
            } else {
                Printer.println(
                        "\nFound " + affectedNonJavaFiles + " changed non-java file"
                                + (affectedNonJavaFiles == 1 ? "" : "s")
                                + ". It is recommended to perform a full test run by giving the command 'SmartTest --fullTest'.",
                        Color.YELLOW);
            }
        }

        return completeRunRequired;
    }

    public void analyseVisitedFiles(List<String> visitedFiles) throws Exception {
        int affectedTestFiles = 0;
        int controllerAffected = 0;

        for (String visitedFile : visitedFiles) {
            if (isTestFile(visitedFile)) {
                affectedTestFiles++;
            } else if (isJavaFile(visitedFile) && !isIgnored(visitedFile)) {
                String cleanFile = FileCleaner.clean(Paths.get(visitedFile));
                if (cleanFile != null && (cleanFile.contains("@Controller") || cleanFile.contains("@RestController"))) {
                    controllerAffected++;
                }
            }
        }

        if (controllerAffected > 0) {
            Printer.println(
                    "\nFound " + controllerAffected + " affected java controller file"
                            + (controllerAffected == 1 ? "" : "s")
                            + ". It is recommended to perform a full test run by giving the command 'SmartTest --fullTest'.",
                    Color.ORANGE);
        }

        Printer.formatPrint(
                "\nTotal potentially affected files: " + visitedFiles.size());
        Printer.formatPrint(
                "Total potentially affected test files: " + affectedTestFiles);
    }

    public String getFullClassName(String fileName) {
        String packageName = extractPackageName(fileName);
        String className = extractClassName(fileName);

        if (packageName == null) {
            return className;
        }

        return packageName + "." + className;
    }

    public String extractPackageName(String filePath) {
        if (EnvironmentService.ON_SYSTEM_DIR) {
            filePath = PROJECT_DIR + filePath;
        }

        if (!FileService.fileExists(filePath)) {
            return null;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.startsWith("//") && line.startsWith("package")) {
                    return line.substring(line.indexOf(" ") + 1, line.lastIndexOf(";")).trim();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Printer.formatPrint("\nCannot extract the package name for file: " + filePath, Color.RED, Color.WHITE);

        return null;
    }

    public String extractClassName(String filePath) {

        // Extract the file name from the file path
        String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);

        // Remove the ".java" file extension
        return fileName.substring(0, fileName.lastIndexOf("."));

    }

    public String extractProjectName(String filePath) {
        if (filePath.startsWith(PROJECT_DIR)) {
            filePath = filePath.substring(PROJECT_DIR.length());
        }
        return filePath.substring(0, filePath.indexOf("/"));
    }

    public String extractTestDirName(String filePath) {
        String[] parts = filePath.split("/");
        int i = 0;
        while (i < parts.length && EnvironmentService.TEST_DIR_TO_TASK_MAP.containsKey(parts[i]) == false) {
            i++;
        }

        if (i == parts.length) {
            throw new RuntimeException("Did not find the test directory name for the file path: " + filePath);
        }

        return parts[i];
    }

    public List<String> findFilesUsingPackageName(String packageName, Set<String> visitedFiles) throws Exception {
        List<String> res = new ArrayList<>();

        if (packageName == null || packageName.isEmpty()) {
            throw new IllegalArgumentException("Empty class name was given as input to: findFilesUsingClassName");
        }

        int maxDepth = Integer.MAX_VALUE;

        CodeParser packageCodeParser = new CodeParser(
                Pattern.compile("\\s+" + Pattern.quote(packageName) + "[^a-zA-Z0-9_$]"));

        try (Stream<Path> stream = Files.find(startPath, maxDepth,
                (path, attr) -> {
                    try {

                        if (!attr.isRegularFile()) {
                            return false;
                        }

                        if (!isJavaFile(path.toString())) {
                            return false;
                        }

                        String filePath = path.toString();

                        if (filePath.startsWith("./")) {
                            filePath = filePath.substring(2);
                        }

                        if (visitedFiles.contains(filePath)) {
                            return false;
                        }

                        String cleanFileOutput = FileCleaner.clean(path);

                        if (cleanFileOutput == null) {
                            return true;
                        }

                        return packageCodeParser.containsKeyword(cleanFileOutput);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }).parallel()) {
            stream.forEach(path -> {
                String referencedFile = path.toString();
                if (referencedFile.startsWith("./")) {
                    referencedFile = referencedFile.substring(2);
                }
                if (!visitedFiles.contains(referencedFile)) {
                    visitedFiles.add(referencedFile);
                    res.add(referencedFile);
                }
            });
        }

        return res;
    }

    public List<String> findFilesUsingClassName(String className, Set<String> visitedFiles) throws IOException {
        List<String> res = new ArrayList<>();

        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("Empty class name was given as input to: findFilesUsingClassName");
        }

        int maxDepth = Integer.MAX_VALUE;

        CodeParser classCodeParser = new CodeParser(className);

        try (Stream<Path> stream = Files.find(startPath, maxDepth,
                (path, attr) -> {
                    try {

                        if (!attr.isRegularFile()) {
                            return false;
                        }

                        if (!isJavaFile(path.toString())) {
                            return false;
                        }

                        String filePath = path.toString();

                        if (filePath.startsWith("./")) {
                            filePath = filePath.substring(2);
                        }

                        if (visitedFiles.contains(filePath)) {
                            return false;
                        }

                        String cleanFileOutput = FileCleaner.clean(path);

                        if (cleanFileOutput == null) {
                            return true;
                        }

                        return classCodeParser.containsKeyword(cleanFileOutput);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }).parallel()) {
            stream.forEach(path -> {
                String referencedFile = path.toString();
                if (referencedFile.startsWith("./")) {
                    referencedFile = referencedFile.substring(2);
                }
                if (!visitedFiles.contains(referencedFile)) {
                    visitedFiles.add(referencedFile);
                    res.add(referencedFile);
                }
            });
        }

        return res;
    }

    public List<String> findFilesUsingClassNameAndPackageName(String className, String packageName,
            Set<String> visitedFiles)
            throws IOException {
        List<String> res = new ArrayList<>();

        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("Empty class name was given as input to: findFilesUsingClassName");
        }

        int maxDepth = Integer.MAX_VALUE;

        CodeParser classCodeParser = new CodeParser(className);
        CodeParser packageCodeParser = new CodeParser(
                Pattern.compile("\\s+" + Pattern.quote(packageName) + "[^a-zA-Z0-9_$]"));

        try (Stream<Path> stream = Files.find(startPath, maxDepth,
                (path, attr) -> {
                    try {

                        if (!attr.isRegularFile()) {
                            return false;
                        }

                        if (!isJavaFile(path.toString())) {
                            return false;
                        }

                        String filePath = path.toString();

                        if (filePath.startsWith("./")) {
                            filePath = filePath.substring(2);
                        }

                        if (visitedFiles.contains(filePath)) {
                            return false;
                        }

                        String cleanFileOutput = FileCleaner.clean(path);

                        if (cleanFileOutput == null) {
                            return true;
                        }

                        return classCodeParser.containsKeyword(cleanFileOutput)
                                && packageCodeParser.containsKeyword(cleanFileOutput);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }).parallel()) {
            stream.forEach(path -> {
                String referencedFile = path.toString();
                if (referencedFile.startsWith("./")) {
                    referencedFile = referencedFile.substring(2);
                }
                if (!visitedFiles.contains(referencedFile)) {
                    visitedFiles.add(referencedFile);
                    res.add(referencedFile);
                }
            });
        }

        return res;
    }

    public List<String> findAllTestFiles() throws Exception {
        List<String> res = new ArrayList<>();

        int maxDepth = Integer.MAX_VALUE;

        CodeParser testCodeParser = new CodeParser("@Test");

        try (Stream<Path> stream = Files.find(startPath, maxDepth,
                (path, attr) -> {
                    try {

                        if (!attr.isRegularFile()) {
                            return false;
                        }

                        if (!isJavaFile(path.toString())) {
                            return false;
                        }

                        String filePath = path.toString();

                        if (filePath.startsWith("./")) {
                            filePath = filePath.substring(2);
                        }

                        String cleanFileOutput = FileCleaner.clean(path);

                        if (cleanFileOutput == null) {
                            return true;
                        }

                        return testCodeParser.containsKeyword(cleanFileOutput);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                }).parallel()) {
            stream.forEach(path -> {
                String referencedFile = path.toString();
                if (referencedFile.startsWith("./")) {
                    referencedFile = referencedFile.substring(2);
                }

                res.add(referencedFile);
            });
        }

        return res;
    }

    public void writeClassNamesToFile(String fileName, Set<String> classNameSet) {
        fileName = PROJECT_DIR + fileName;
        File file = new File(fileName);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            // Write empty string to clear the file
            writer.write("");
            for (String className : classNameSet) {
                writer.write(className);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String printFromFile(String fileName) {
        String output = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                String successString = "BUILD SUCCESSFUL";
                String failedString = "BUILD FAILED";
                int successIndex = line.indexOf(successString);
                int failedIndex = line.indexOf(failedString);
                if (successIndex >= 0) {
                    String prefix = line.substring(0, successIndex);
                    String suffix = line.substring(successIndex + successString.length());

                    Printer.print(prefix);
                    // Print in the OG 'BUILD SUCCESSFUL' color from VSCode :)
                    Printer.boldPrint(successString, Printer.BUILD_SUCCESSFUL);
                    Printer.println(suffix);
                } else if (failedIndex >= 0) {
                    String prefix = line.substring(0, failedIndex);
                    String suffix = line.substring(failedIndex + failedString.length());

                    Printer.print(prefix);
                    Printer.boldPrint(failedString, Color.RED);
                    Printer.println(suffix);
                } else {
                    Printer.formatPrint(line);
                }

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            Printer.println("Error reading file, " + e.getMessage(), Color.RED);
        }
        return output;
    }

    public String getStringFromFile(String fileName) {
        String output = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileName));
            String line = reader.readLine();
            while (line != null) {
                output += line + "\n";
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            Printer.println("Error reading file, " + e.getMessage(), Color.RED);
        }
        return output;
    }

    public boolean isIgnored(String filePath) {
        for (String ignoredFile : ignoredFiles) {
            if (filePath.endsWith(ignoredFile)) {
                return true;
            }
        }

        return false;
    }

    public static boolean fileExists(String path) {
        File file = new File(path);
        return file.exists();
    }

    public boolean isCompleteRunRequired() {
        return completeRunRequired;
    }

    public static boolean directoryExists(String path) {
        File directory = new File(path);

        return (directory.exists() && directory.isDirectory());
    }

    public static void createFile(String fileName, String content) {
        try {
            FileWriter fileWriter = new FileWriter(fileName);
            fileWriter.write(content);
            fileWriter.close();
        } catch (IOException e) {
            Printer.println("An error occured while create the file " + fileName + ": " + e.toString());
        }
    }

    public static void createDirectory(String directoryName, boolean cleanDirectory) {
        File directory = new File(directoryName);

        // Create the directory if it does not exist
        if (!directory.exists()) {
            boolean success = directory.mkdirs();
            if (success) {
                Printer.println("Output directory created successfully.", Color.GREEN);
            } else {
                Printer.boldPrintln("Failed to create output directory.", Color.RED);
            }
        } else if (cleanDirectory) {
            Printer.println("Output directory already exists, cleaning up the directory...", Color.GREEN);

            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    file.delete();
                }
            }
            Printer.println("Output directory cleaned successfully.", Color.GREEN);
        }
    }

    public static void deleteDirectory(String directory) throws Exception {
        Path path = Paths.get(directory);
        Files.walk(path)
                .sorted(java.util.Comparator.reverseOrder())
                .map(Path::toFile)
                .forEach(File::delete);
    }
}
