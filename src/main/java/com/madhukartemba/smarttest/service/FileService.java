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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.madhukartemba.smarttest.util.CodeParser;
import com.madhukartemba.smarttest.util.FileCleaner;

public class FileService {
    private String PROJECT_DIR;
    private ProcessBuilder processBuilder;
    private boolean completeRunRequired = false;

    private static List<String> ignoredFiles = Arrays.asList(".gitignore", "README.md");

    public FileService() {
        this.PROJECT_DIR = EnvironmentService.PROJECT_DIR;
        processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(PROJECT_DIR));
    }

    public FileService(String PROJECT_DIR) {
        this.PROJECT_DIR = PROJECT_DIR;
        processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(PROJECT_DIR));
    }

    public boolean isJavaFile(String filePath) {
        return filePath.endsWith(".java");
    }

    public boolean isTestFile(String filePath) {
        if (!filePath.endsWith("Test.java") && !filePath.endsWith("IT.java")) {
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(PROJECT_DIR + filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.contains("@Test")) {
                    return true;
                }
            }
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
                PrintService.println("\nOnly the non-java files have been changed, will run all the tests.",
                        Color.YELLOW);
                completeRunRequired = true;
            } else {
                PrintService.println(
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

        PrintService.formatPrint(
                "\nTotal potentially affected files: " + visitedFiles.size());
        PrintService.formatPrint(
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
        try (BufferedReader br = new BufferedReader(new FileReader(PROJECT_DIR + filePath))) {
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
        PrintService.formatPrint("\nCannot extract the package name for file: " + filePath, Color.RED, Color.WHITE);

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
            PrintService.println("Empty class name was given as input to: findFilesUsingPackageName", Color.RED);
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

        String[] extensions = { "java" };
        Path start = Paths.get(".");
        int maxDepth = Integer.MAX_VALUE;

        CodeParser codeParser = new CodeParser(className);

        try (Stream<Path> stream = Files.find(start, maxDepth,
                (path, attr) -> {
                    try {

                        if (!attr.isRegularFile()) {
                            return false;
                        }

                        if (!Arrays.stream(extensions).anyMatch(ext -> path.toString().endsWith("." + ext))) {
                            return false;
                        }

                        String filePath = path.toString();

                        if (filePath.startsWith("./")) {
                            filePath = filePath.substring(2);
                        }

                        if (visitedFiles.contains(filePath)) {
                            return false;
                        }

                        String cleanFile = FileCleaner.clean(path);

                        return codeParser.containsKeyword(cleanFile);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    return false;
                })) {
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

        processBuilder.command("grep", "-r", "-l", "-w", "--exclude-from=.gitignore", "@Test", ".");
        Process grepProcess = processBuilder.start();
        BufferedReader grepOutput = new BufferedReader(new InputStreamReader(grepProcess.getInputStream()));
        grepProcess.waitFor();
        String referencedFile = grepOutput.readLine();

        // Add test files that reference changed file to list
        while (referencedFile != null) {
            if (isTestFile(referencedFile)) {
                if (referencedFile.startsWith("./")) {
                    referencedFile = referencedFile.substring(2);
                }
                res.add(referencedFile);
            }
            referencedFile = grepOutput.readLine();
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

                    PrintService.print(prefix);
                    // Print in the OG 'BUILD SUCCESSFUL' color from VSCode :)
                    PrintService.boldPrint(successString, Color.decode("#23D18B"));
                    PrintService.println(suffix);
                } else if (failedIndex >= 0) {
                    String prefix = line.substring(0, failedIndex);
                    String suffix = line.substring(failedIndex + failedString.length());

                    PrintService.print(prefix);
                    PrintService.boldPrint(failedString, Color.RED);
                    PrintService.println(suffix);
                } else {
                    PrintService.formatPrint(line);
                }

                line = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            PrintService.println("Error reading file, " + e.getMessage(), Color.RED);
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
            PrintService.println("Error reading file, " + e.getMessage(), Color.RED);
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
}
