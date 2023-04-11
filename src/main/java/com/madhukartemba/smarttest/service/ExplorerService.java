package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Collectors;

public class ExplorerService {

    private FileService fileService;

    public ExplorerService() {
        fileService = new FileService();
    }

    public List<String> exploreViaClassname(List<String> inputFiles) throws Exception {

        PrintService.boldPrintln("\n\nStarting to explore affected files via class name...\n");
        PrintService.formatPrint("Number of changed files: " + inputFiles.size());
        Queue<String> javaFileQueue = new ArrayDeque<>();
        Set<String> visitedFiles = new HashSet<>();
        Set<String> blackListFiles = new HashSet<>();
        Set<String> visitedClassNames = new HashSet<>();

        for (String inputFile : inputFiles) {
            if (fileService.isJavaFile(inputFile)) {
                javaFileQueue.add(inputFile);
                visitedFiles.add(inputFile);
            }
        }

        PrintService.println("\nStarted to explore...\n", Color.GREEN);
        PrintService.print("ROOT", Color.GREEN);

        int prevVisitedSize = 0;

        while (!javaFileQueue.isEmpty()) {
            String javaFile = javaFileQueue.poll();

            if (blackListFiles.contains(javaFile)) {
                continue;
            }

            String className = fileService.extractClassName(javaFile);

            if (className == null) {
                blackListFiles.add(className);
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
                PrintService.print(" -->");
                PrintService.print(" " + currentVisitedSize, Color.YELLOW);
            }
        }

        if (prevVisitedSize != visitedFiles.size()) {
            PrintService.print(" -->");
            PrintService.print(" " + visitedFiles.size(), Color.YELLOW);
        }

        PrintService.print(" -->");
        PrintService.print(" END", Color.GREEN);

        PrintService.println("\n\nExploration complete!", Color.GREEN);
        fileService.analyseFiles(visitedFiles);

        return visitedFiles.stream().filter(x -> !blackListFiles.contains(x)).collect(Collectors.toList());

    }

    public List<String> exploreViaPackageName(List<String> inputFiles) throws Exception {

        PrintService.boldPrintln("\n\nStarting to explore affected files via package name...\n");
        PrintService.formatPrint("Number of changed files: " + inputFiles.size());
        Queue<String> javaFileQueue = new ArrayDeque<>();
        Set<String> visitedFiles = new HashSet<>();
        Set<String> blackListFiles = new HashSet<>();
        Set<String> visitedPackages = new HashSet<>();

        for (String inputFile : inputFiles) {
            if (fileService.isJavaFile(inputFile)) {
                javaFileQueue.add(inputFile);
                visitedFiles.add(inputFile);
            }
        }

        PrintService.println("\nStarted to explore...\n", Color.GREEN);
        PrintService.print("ROOT", Color.GREEN);

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
                PrintService.print(" -->");
                PrintService.print(" " + currentVisitedSize, Color.YELLOW);
            }
        }

        if (prevVisitedSize != visitedFiles.size()) {
            PrintService.print(" -->");
            PrintService.print(" " + visitedFiles.size(), Color.YELLOW);
        }

        PrintService.print(" -->");
        PrintService.print(" END", Color.GREEN);

        PrintService.println("\n\nExploration complete!");
        fileService.analyseFiles(visitedFiles);

        return visitedFiles.stream().filter(x -> !blackListFiles.contains(x)).collect(Collectors.toList());
    }
}
