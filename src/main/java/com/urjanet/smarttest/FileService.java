package com.urjanet.smarttest;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileService {
    private String PROJECT_DIR;
    private ProcessBuilder processBuilder;

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

    public void analyseFiles(Set<String> visitedFiles) {
        int affectedTestFiles = 0;

        for (String visitedFile : visitedFiles) {
            if (isTestFile(visitedFile)) {
                affectedTestFiles++;
            }
        }

        PrintService.formatPrint("\nTotal potentially affected files: " + visitedFiles.size());
        PrintService.formatPrint("Total potentially affected test files: " + affectedTestFiles);
    }

    public String getFullClassName(String fileName) {
        String packageName = extractPackageName(fileName);
        String className = extractClassName(fileName);

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
        return parts[2]; // index 2 corresponds to the test folder name
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

        String referencedFile = grepOutput.readLine();

        // Add test files that reference changed file to list
        while (referencedFile != null) {
            if (isJavaFile(referencedFile) && !visitedFiles.contains(referencedFile)) {
                referencedFile = referencedFile.substring(2);
                visitedFiles.add(referencedFile);
                res.add(referencedFile);
            }
            referencedFile = grepOutput.readLine();
        }

        return res;
    }

    public List<String> findFilesUsingClassName(String className, Set<String> visitedFiles) throws Exception {
        List<String> res = new ArrayList<>();

        if (className == null || className.isEmpty()) {
            PrintService.println("Empty class name was given as input to: findFilesUsingClassName", Color.RED);
            return res;
        }

        processBuilder.command("grep", "-r", "-l", "-w", "--exclude-from=.gitignore", className, ".");
        Process grepProcess = processBuilder.start();
        BufferedReader grepOutput = new BufferedReader(new InputStreamReader(grepProcess.getInputStream()));

        String referencedFile = grepOutput.readLine();

        // Add test files that reference changed file to list
        while (referencedFile != null) {
            if (isJavaFile(referencedFile) && !visitedFiles.contains(referencedFile)) {
                referencedFile = referencedFile.substring(2);
                visitedFiles.add(referencedFile);
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
            // write empty string to clear the file
            writer.write("");
            for (String className : classNameSet) {
                writer.write(className);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
