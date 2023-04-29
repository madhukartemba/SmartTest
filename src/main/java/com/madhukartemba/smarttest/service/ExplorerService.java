package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

import com.madhukartemba.smarttest.entity.Parameters;
import com.madhukartemba.smarttest.util.Printer;

public class ExplorerService {

    private FileService fileService;
    private boolean completeRunRequired = false;

    public ExplorerService() {
        fileService = new FileService();
    }

    public List<String> explore(List<String> inputFiles) throws Exception {

        if (Parameters.FULL_TEST.getValue()) {
            completeRunRequired = true;
            Printer.boldPrintln("\n\nFull test command is given, will run all the tests...");
            return exploreAll();
        }

        Printer.boldPrintln("\n\nStarting to explore affected files...\n");
        completeRunRequired = false;

        Printer.formatPrint("Number of changed files: " + inputFiles.size());
        Queue<String> javaFileQueue = new ArrayDeque<>();
        Set<String> visitedFiles = new HashSet<>();
        Set<String> blackListFiles = new HashSet<>();
        Set<String> visitedClassNameAndPackageName = new HashSet<>();

        completeRunRequired = fileService.analyseGitFiles(inputFiles);

        if (completeRunRequired) {
            return exploreAll();
        }

        for (String inputFile : inputFiles) {
            if (fileService.isJavaFile(inputFile)) {
                javaFileQueue.add(inputFile);
                visitedFiles.add(inputFile);
            }
        }

        Printer.println("\nStarted to explore...\n", Color.GREEN);
        Printer.boldPrint("ROOT", Color.GREEN);

        int prevVisitedSize = 0;

        while (!javaFileQueue.isEmpty()) {
            String javaFile = javaFileQueue.poll();

            if (blackListFiles.contains(javaFile)) {
                continue;
            }

            String className = fileService.extractClassName(javaFile);
            String packageName = fileService.extractPackageName(javaFile);

            if (className == null) {
                blackListFiles.add(javaFile);
                continue;
            }

            if (!visitedClassNameAndPackageName.add(className + " " + packageName)) {
                continue;
            }

            List<String> foundFiles = null;

            if (packageName != null) {
                foundFiles = fileService.findFilesUsingClassNameAndPackageName(className, packageName,
                        visitedFiles);
            } else {
                foundFiles = fileService.findFilesUsingClassName(className, visitedFiles);
            }

            javaFileQueue.addAll(foundFiles);

            int currentVisitedSize = visitedFiles.size();
            if (prevVisitedSize + 50 < currentVisitedSize) {
                prevVisitedSize = currentVisitedSize;
                Printer.print(" -->");
                Printer.print(" " + currentVisitedSize, Parameters.DEFAULT_COLOR_2.getValue());
            }
        }

        if (prevVisitedSize != visitedFiles.size()) {
            Printer.print(" -->");
            Printer.print(" " + visitedFiles.size(), Parameters.DEFAULT_COLOR_2.getValue());
        }

        Printer.print(" -->");
        Printer.boldPrint(" END", Color.GREEN);

        Printer.println("\n\nExploration complete!", Color.GREEN);
        fileService.analyseResult(visitedFiles);

        return visitedFiles.stream().filter(x -> !blackListFiles.contains(x)).collect(Collectors.toList());

    }

    public List<String> exploreViaClassname(List<String> inputFiles) throws Exception {

        if (Parameters.FULL_TEST.getValue()) {
            completeRunRequired = true;
            Printer.boldPrintln("\n\nFull test command is given, will run all the tests...");
            return exploreAll();
        }

        Printer.boldPrintln("\n\nStarting to explore affected files via class name...\n");
        completeRunRequired = false;

        Printer.formatPrint("Number of changed files: " + inputFiles.size());
        Queue<String> javaFileQueue = new ArrayDeque<>();
        Set<String> visitedFiles = new HashSet<>();
        Set<String> blackListFiles = new HashSet<>();
        Set<String> visitedClassNames = new HashSet<>();

        completeRunRequired = fileService.analyseGitFiles(inputFiles);

        if (completeRunRequired) {
            return exploreAll();
        }

        for (String inputFile : inputFiles) {
            if (fileService.isJavaFile(inputFile)) {
                javaFileQueue.add(inputFile);
                visitedFiles.add(inputFile);
            }
        }

        Printer.println("\nStarted to explore...\n", Color.GREEN);
        Printer.boldPrint("ROOT", Color.GREEN);

        int prevVisitedSize = 0;

        while (!javaFileQueue.isEmpty()) {
            String javaFile = javaFileQueue.poll();

            if (blackListFiles.contains(javaFile)) {
                continue;
            }

            String className = fileService.extractClassName(javaFile);

            if (className == null) {
                blackListFiles.add(javaFile);
                continue;
            }

            if (!visitedClassNames.add(className)) {
                continue;
            }

            List<String> foundFiles = fileService.findFilesUsingClassName(className, visitedFiles);
            javaFileQueue.addAll(foundFiles);
            int currentVisitedSize = visitedFiles.size();
            if (prevVisitedSize + 50 < currentVisitedSize) {
                prevVisitedSize = currentVisitedSize;
                Printer.print(" -->");
                Printer.print(" " + currentVisitedSize, Parameters.DEFAULT_COLOR_2.getValue());
            }
        }

        if (prevVisitedSize != visitedFiles.size()) {
            Printer.print(" -->");
            Printer.print(" " + visitedFiles.size(), Parameters.DEFAULT_COLOR_2.getValue());
        }

        Printer.print(" -->");
        Printer.boldPrint(" END", Color.GREEN);

        Printer.println("\n\nExploration complete!", Color.GREEN);
        fileService.analyseResult(visitedFiles);

        return visitedFiles.stream().filter(x -> !blackListFiles.contains(x)).collect(Collectors.toList());

    }

    public List<String> exploreViaPackageName(List<String> inputFiles) throws Exception {

        if (Parameters.FULL_TEST.getValue()) {
            completeRunRequired = true;
            Printer.boldPrintln("\n\nFull test command is given, will run all the tests...");
            return exploreAll();
        }

        completeRunRequired = false;

        Printer.boldPrintln("\n\nStarting to explore affected files via package name...\n");
        Printer.formatPrint("Number of changed files: " + inputFiles.size());
        Queue<String> javaFileQueue = new ArrayDeque<>();
        Set<String> visitedFiles = new HashSet<>();
        Set<String> blackListFiles = new HashSet<>();
        Set<String> visitedPackages = new HashSet<>();

        completeRunRequired = fileService.analyseGitFiles(inputFiles);

        if (completeRunRequired) {
            return exploreAll();
        }

        for (String inputFile : inputFiles) {
            if (fileService.isJavaFile(inputFile)) {
                javaFileQueue.add(inputFile);
                visitedFiles.add(inputFile);
            }
        }

        Printer.println("\nStarted to explore...\n", Color.GREEN);
        Printer.boldPrint("ROOT", Color.GREEN);

        int prevVisitedSize = 0;

        while (!javaFileQueue.isEmpty()) {
            String javaFile = javaFileQueue.poll();

            if (blackListFiles.contains(javaFile)) {
                continue;
            }

            String packageName = fileService.extractPackageName(javaFile);

            if (packageName == null) {
                blackListFiles.add(javaFile);
                continue;
            }

            if (!visitedPackages.add(packageName)) {
                continue;
            }

            List<String> foundFiles = fileService.findFilesUsingPackageName(packageName, visitedFiles);
            javaFileQueue.addAll(foundFiles);

            int currentVisitedSize = visitedFiles.size();
            if (prevVisitedSize + 50 < currentVisitedSize) {
                prevVisitedSize = currentVisitedSize;
                Printer.print(" -->");
                Printer.print(" " + currentVisitedSize, Parameters.DEFAULT_COLOR_2.getValue());
            }
        }

        if (prevVisitedSize != visitedFiles.size()) {
            Printer.print(" -->");
            Printer.print(" " + visitedFiles.size(), Parameters.DEFAULT_COLOR_2.getValue());
        }

        Printer.print(" -->");
        Printer.boldPrint(" END", Color.GREEN);

        Printer.println("\n\nExploration complete!", Color.GREEN);
        fileService.analyseResult(visitedFiles);

        return visitedFiles.stream().filter(x -> !blackListFiles.contains(x)).collect(Collectors.toList());
    }

    public List<String> exploreAll() throws Exception {
        Printer.println("\nExploring all files...\n", Color.GREEN);

        Printer.boldPrint("ROOT", Color.GREEN);

        List<String> output = fileService.findAllTestFiles();

        Printer.print(" -->");
        Printer.print(" " + output.size(), Parameters.DEFAULT_COLOR_2.getValue());

        Printer.print(" -->");
        Printer.boldPrint(" END", Color.GREEN);

        Printer.println("\n\nExploration complete!", Color.GREEN);

        fileService.analyseResult(output.stream().collect(Collectors.toSet()));

        return output;
    }

    public boolean isCompleteRunRequired() {
        return completeRunRequired;
    }
}
