package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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

    public boolean analyseGitFiles(List<String> visitedFiles) {

        completeRunRequired = false;

        int affectedFiles = 0;
        int affectedNonJavaFiles = 0;

        for (String visitedFile : visitedFiles) {
            if (!isJavaFile(visitedFile) && !isIgnored(visitedFile)) {
                affectedNonJavaFiles++;
            } else {
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
                                + ". It is recommended to perform a full test run.",
                        Color.YELLOW);
            }
        }

        return completeRunRequired;
    }

    public void analyseResult(Set<String> visitedFiles) {
        int affectedTestFiles = 0;

        for (String visitedFile : visitedFiles) {
            if (isTestFile(visitedFile)) {
                affectedTestFiles++;
            }
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
            Printer.println("Empty class name was given as input to: findFilesUsingPackageName", Color.RED);
            return res;
        }

        processBuilder.command("grep", "-r", "-l", "--exclude-from=.gitignore", packageName + ".*", ".");
        Process grepProcess = processBuilder.start();
        BufferedReader grepOutput = new BufferedReader(new InputStreamReader(grepProcess.getInputStream()));
        grepProcess.waitFor();
        String referencedFile = grepOutput.readLine();

        // Add test files that reference changed file to list
        while (referencedFile != null) {
            if (isJavaFile(referencedFile) && !visitedFiles.contains(referencedFile)) {
                if (referencedFile.startsWith("./")) {
                    referencedFile = referencedFile.substring(2);
                }
                visitedFiles.add(referencedFile);
                res.add(referencedFile);
            }
            referencedFile = grepOutput.readLine();
        }

        return res;
    }

    public List<String> findFilesUsingClassName(String className, Set<String> visitedFiles) throws IOException {
        List<String> res = new ArrayList<>();

        if (className == null || className.isEmpty()) {
            throw new IllegalArgumentException("Empty class name was given as input to: findFilesUsingClassName");
        }

        String[] extensions = { ".java" };
        int maxDepth = Integer.MAX_VALUE;

        CodeParser codeParser = new CodeParser(className);

        try (Stream<Path> stream = Files.find(startPath, maxDepth,
                (path, attr) -> {
                    try {

                        if (!attr.isRegularFile()) {
                            return false;
                        }

                        if (!Arrays.stream(extensions).anyMatch(ext -> path.toString().endsWith(ext))) {
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

                        return codeParser.containsKeyword(cleanFileOutput);

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

        String[] extensions = { ".java" };
        int maxDepth = Integer.MAX_VALUE;

        CodeParser classCodeParser = new CodeParser(className);
        CodeParser packageCodeParser = new CodeParser(Pattern.compile(".*" + Pattern.quote(packageName) + ".*"));

        try (Stream<Path> stream = Files.find(startPath, maxDepth,
                (path, attr) -> {
                    try {

                        if (!attr.isRegularFile()) {
                            return false;
                        }

                        if (!Arrays.stream(extensions).anyMatch(ext -> path.toString().endsWith(ext))) {
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

        String[] extensions = { ".java" };
        int maxDepth = Integer.MAX_VALUE;

        CodeParser testCodeParser = new CodeParser("@Test");

        try (Stream<Path> stream = Files.find(startPath, maxDepth,
                (path, attr) -> {
                    try {

                        if (!attr.isRegularFile()) {
                            return false;
                        }

                        if (!Arrays.stream(extensions).anyMatch(ext -> path.toString().endsWith(ext))) {
                            return false;
                        }

                        String filePath = path.toString();

                        if (filePath.startsWith("./")) {
                            filePath = filePath.substring(2);
                        }

                        String cleanFileOutput = FileCleaner.clean(path);

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
                    Printer.boldPrint(successString, Color.decode("#23D18B"));
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

    public boolean isCompleteRunRequired() {
        return completeRunRequired;
    }

    public static boolean directoryExists(String path) {
        File directory = new File(path);

        return (directory.exists() && directory.isDirectory());
    }
}
