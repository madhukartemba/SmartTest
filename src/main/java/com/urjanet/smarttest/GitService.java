package com.urjanet.smarttest;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitService {
    private final int GIT_OUTPUT_LINE_COUNT = 20;
    private String PROJECT_DIR;
    private ProcessBuilder processBuilder;

    public GitService() {
        this.PROJECT_DIR = EnvironmentService.PROJECT_DIR;
        processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(this.PROJECT_DIR));
    }

    public GitService(String PROJECT_DIR) {
        this.PROJECT_DIR = PROJECT_DIR;
        processBuilder = new ProcessBuilder();
        processBuilder.directory(new File(this.PROJECT_DIR));
    }

    public List<String> getChangedFiles() throws Exception {

        PrintService.println("\n\nGetting the changed files from Git...");

        List<String> changedFiles = new ArrayList<>();

        String currentBranchName = getCurrentBranchName();

        int skipCount = 0;

        if (currentBranchName.equals("main") || currentBranchName.equals("master")) {
            PrintService.println(
                    "Currently on " + currentBranchName
                            + "  branch, will compare the files against the previous official merge.",
                    Color.YELLOW);
            skipCount = 1;
        }

        String mergeLine = getMerge(skipCount);
        String mergeSHA = extractMergeSHA(mergeLine);
        String mergeNumber = extractMergePRNumber(mergeLine);

        PrintService.formatPrint(
                "Comparing the staged and committed changes wrt. official merge: " + mergeNumber);
        PrintService.println(mergeLine, Color.GREEN);

        processBuilder.command("sh", "-c", "git diff --name-only --staged " + mergeSHA);
        Process gitProcess = processBuilder.start();
        BufferedReader gitOutput = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));

        String fileName = gitOutput.readLine();

        while (fileName != null) {
            changedFiles.add(fileName);
            fileName = gitOutput.readLine();
        }

        return changedFiles;
    }

    public String getCurrentBranchName() throws Exception {
        processBuilder.command("sh", "-c", "git branch --show-current");
        Process gitProcess = processBuilder.start();
        BufferedReader gitOutput = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));

        return gitOutput.readLine();
    }

    public String getMerge(int skipCount) throws Exception {
        processBuilder.command("sh", "-c", "git log --merges --oneline -n " + GIT_OUTPUT_LINE_COUNT);
        Process gitProcess = processBuilder.start();
        BufferedReader gitOutput = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));

        for (int i = 0; i < GIT_OUTPUT_LINE_COUNT; i++) {
            String currLine = gitOutput.readLine();
            if (currLine == null) {
                throw new RuntimeException("The git log output ended before an offical merge was found.");
            }
            if (isOfficialMerge(currLine)) {
                if (skipCount-- <= 0) {
                    return currLine;
                }
            }
        }

        throw new RuntimeException(
                "Did not find any official merge in the first " + GIT_OUTPUT_LINE_COUNT + " merges.");
    }

    public String extractMergeSHA(String line) {
        return line.substring(0, line.indexOf(" "));
    }

    public String extractMergePRNumber(String line) {
        Pattern pattern = Pattern.compile("(#\\d+)");
        Matcher matcher = pattern.matcher(line);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            throw new RuntimeException("Cannot extract the merge PR number for the line: " + line);
        }
    }

    public boolean isOfficialMerge(String line) {

        Pattern pattern = Pattern.compile("Merge pull request #\\d+ from Urjanet\\/");
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }
}
