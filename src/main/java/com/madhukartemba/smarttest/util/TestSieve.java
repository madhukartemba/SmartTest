package com.madhukartemba.smarttest.util;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.entity.ParametersNew;
import com.madhukartemba.smarttest.service.EnvironmentService;
import com.madhukartemba.smarttest.service.FileService;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class TestSieve {
    private String PROJECT_DIR;
    private String COMMAND_NAME;
    private String OPTION_NAME;
    private Set<String> projectNames;
    private Map<String, String> testDirToTaskMap;

    private FileService fileService;

    public TestSieve() {
        this.PROJECT_DIR = EnvironmentService.PROJECT_DIR;
        this.projectNames = EnvironmentService.PROJECT_NAMES.stream().collect(Collectors.toSet());
        this.testDirToTaskMap = EnvironmentService.TEST_DIR_TO_TASK_MAP;
        this.COMMAND_NAME = ParametersNew.GRADLE_COMMAND.getValue();
        this.OPTION_NAME = ParametersNew.GRADLE_OPTION_NAME;
        this.fileService = new FileService();
    }

    public TestSieve(String COMMAND_NAME, String OPTION_NAME, Set<String> projectNames,
            Map<String, String> testDirToTaskMap) {
        this.PROJECT_DIR = EnvironmentService.PROJECT_DIR;
        this.COMMAND_NAME = COMMAND_NAME;
        this.OPTION_NAME = OPTION_NAME;
        this.projectNames = projectNames;
        this.testDirToTaskMap = testDirToTaskMap;
        fileService = new FileService(this.PROJECT_DIR);
    }

    public TestSieve(String PROJECT_DIR, String COMMAND_NAME, String OPTION_NAME, Set<String> projectNames,
            Map<String, String> testDirToTaskMap) {
        this.PROJECT_DIR = PROJECT_DIR;
        this.COMMAND_NAME = COMMAND_NAME;
        this.OPTION_NAME = OPTION_NAME;
        this.projectNames = projectNames;
        this.testDirToTaskMap = testDirToTaskMap;
        fileService = new FileService(this.PROJECT_DIR);
    }

    public List<Command> groupify(List<String> filePaths) {

        // Sort file paths for faster grouping.
        Collections.sort(filePaths);

        Set<Command> commands = new HashSet<>();

        for (String filePath : filePaths) {
            String projectName = fileService.extractProjectName(filePath);
            String testDirName = fileService.extractTestDirName(filePath);

            if (!isValid(filePath, projectName, testDirName)) {
                continue;
            }

            String taskName = getTaskName(testDirName);
            String className = fileService.getFullClassName(filePath);

            Command newCommand = new Command(COMMAND_NAME, projectName, taskName, OPTION_NAME, className);
            if (commands.contains(newCommand)) {
                for (Command command : commands) {
                    if (command.equals(newCommand)) {
                        command.addArg(className);
                        break;
                    }
                }
            } else {
                commands.add(newCommand);
            }
        }

        return commands.stream().collect(Collectors.toList());
    }

    public String getTaskName(String testDirName) {
        String taskName = testDirToTaskMap.getOrDefault(testDirName, null);
        if (taskName == null) {
            throw new RuntimeException("The taskName for testDirName: " + testDirName + " does not exist!");
        }
        return taskName;
    }

    public boolean isValid(String filePath, String projectName, String testDirName) {
        if (projectNames.contains(projectName) == false) {
            throw new RuntimeException(
                    "The project name: " + projectName + " for file: " + filePath + " does not exist!");
        }
        if (testDirToTaskMap.containsKey(testDirName) == false) {
            throw new RuntimeException(
                    "The task name for directory: " + testDirName + " for file: " + filePath + " does not exist!");
        }

        return true;
    }

}
