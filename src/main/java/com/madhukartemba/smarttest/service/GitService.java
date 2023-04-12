package com.madhukartemba.smarttest.service;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.madhukartemba.smarttest.SmartTest;
import com.madhukartemba.smarttest.entity.Parameters;

public class GitService {
    private final int GIT_OUTPUT_LINE_COUNT = 100;
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

        PrintService.boldPrintln("\n\nGetting the changed files from Git...\n");

        List<String> changedFiles = new ArrayList<>();

        String currentBranchName = getCurrentBranchName();

        if (currentBranchName == null || currentBranchName.isEmpty()) {
            SmartTest.exitWithCode("Cannot get the current Git branch name!", Color.RED, 1);
        }

        int skipCount = 0;

        if (currentBranchName.equals("main") || currentBranchName.equals("master")) {
            PrintService.println(
                    "Currently on " + currentBranchName
                            + " branch, will compare the files against the previous official merge.\n",
                    Color.YELLOW);
            skipCount = 1;
        }

        String mergeLine = getMerge(skipCount);
        String mergeSHA = extractMergeSHA(mergeLine);
        String mergeNumber = extractMergePRNumber(mergeLine);

        PrintService.formatPrint(
                "Comparing the staged and committed changes wrt. official merge: " + mergeNumber);
        PrintService.println(mergeLine, Color.GREEN);

        String command = "git diff --name-only --staged " + mergeSHA;

        processBuilder.command(command.split("\\s"));
        Process gitProcess = processBuilder.start();
        BufferedReader gitOutput = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));
        gitProcess.waitFor();

        String fileName = gitOutput.readLine();

        while (fileName != null) {
            changedFiles.add(fileName);
            fileName = gitOutput.readLine();
        }

        return changedFiles;
    }

    public String getCurrentBranchName() throws Exception {
        processBuilder.command("git branch --show-current".split("\\s"));
        Process gitProcess = processBuilder.start();
        BufferedReader gitOutput = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));
        gitProcess.waitFor();

        return gitOutput.readLine();
    }

    public String getMerge(int skipCount) throws Exception {

        String command = Parameters.GIT_COMMAND.trim();

        if (!command.startsWith("git ") || command.contains("&&")) {
            SmartTest.exitWithCode(
                    "POTENTIAL SHELL INJECTION ATTACK: The git command should not contain any other command.",
                    Color.RED,
                    1);
        }

        if (!command.contains("-n")) {

            command += " -n " + GIT_OUTPUT_LINE_COUNT;

        }

        if (!command.contains("--oneline")) {

            command += " --oneline";

        }

        PrintService.formatPrint("Using Git command: " + command + "\n");

        processBuilder.command(command.split("\\s"));
        Process gitProcess = processBuilder.start();
        BufferedReader gitOutput = new BufferedReader(new InputStreamReader(gitProcess.getInputStream()));
        gitProcess.waitFor();

        for (int i = 0; i < GIT_OUTPUT_LINE_COUNT; i++) {
            String currLine = gitOutput.readLine();
            if (currLine == null) {
                SmartTest.exitWithCode("The git log output ended before an offical merge was found.", Color.RED, 1);
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
            return extractMergeSHA(line);
        }
    }

    public boolean isOfficialMerge(String line) {

        Pattern pattern = Pattern.compile(Parameters.OFFICIAL_MERGE_REQUEST_PATTERN);
        Matcher matcher = pattern.matcher(line);

        return matcher.find();
    }
}
