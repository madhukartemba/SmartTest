package com.madhukartemba.smarttest.util;

import com.madhukartemba.smarttest.entity.Command;
import com.madhukartemba.smarttest.service.Printer;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CommandBuilder {
    public static String build(Command command) {
        StringBuilder outputCommand = new StringBuilder();

        outputCommand.append(command.getCommandName() + " ");
        if (command.getProjectName() != null && !command.getProjectName().equals("src")) {
            outputCommand.append(command.getProjectName() + ":");
        }
        outputCommand.append(command.getTaskName());
        for (String arg : command.getArgs()) {
            outputCommand.append(" " + command.getOptionName() + " " + arg);
        }

        return outputCommand.toString();
    }

    public static String build(List<Command> commands) {

        if (commands == null || commands.isEmpty()) {
            Printer.println("\nGiven commands list to CommandBuilder is empty!", Color.RED);
            return null;
        }

        StringBuilder outputCommand = new StringBuilder();

        String commandName = commands.get(0).getCommandName();

        outputCommand.append(commandName + " ");

        for (Command command : commands) {
            outputCommand.append(buildWithoutCommandName(command));
        }

        return outputCommand.toString();
    }

    public static String build(List<Command> inputCommandList, List<String> taskPriority) {

        if (inputCommandList == null || inputCommandList.isEmpty()) {
            Printer.println("\nGiven commands list to CommandBuilder is empty!", Color.RED);
            return null;
        }

        if (taskPriority == null || taskPriority.isEmpty()) {
            throw new RuntimeException("\nGiven task priority list to CommandBuilder is empty.");
        }

        StringBuilder outputCommand = new StringBuilder();

        String commandName = inputCommandList.get(0).getCommandName();
        outputCommand.append(commandName + " ");

        TreeMap<String, List<Command>> sortedCommands = seperateWrtTaskName(inputCommandList, taskPriority);

        for (Map.Entry<String, List<Command>> entry : sortedCommands.entrySet()) {
            List<Command> commandList = entry.getValue();
            for (Command command : commandList) {
                outputCommand.append(buildWithoutCommandName(command));
            }
        }

        return outputCommand.toString();
    }

    public static List<String> parallelBuild(List<Command> inputCommandList) {

        if (inputCommandList == null || inputCommandList.isEmpty()) {
            Printer.println("\nGiven commands list to CommandBuilder is empty!", Color.RED);
            return null;
        }

        List<String> output = new ArrayList<>();

        for (Command command : inputCommandList) {
            output.add(build(command));
        }

        return output;
    }

    public static List<String> extremeParallelBuild(List<Command> inputCommandList, int chunkSize) {

        if (inputCommandList == null || inputCommandList.isEmpty()) {
            Printer.println("\nGiven commands list to CommandBuilder is empty!", Color.RED);
            return null;
        }

        List<Command> splitCommandList = new ArrayList<>();

        for (Command command : inputCommandList) {
            splitCommandList.addAll(splitCommand(command, chunkSize));
        }

        return parallelBuild(splitCommandList);

    }

    public static List<Command> splitCommands(List<Command> inputCommandList, int chunkSize) {

        if (inputCommandList == null || inputCommandList.isEmpty()) {
            Printer.println("\nGiven commands list to CommandBuilder is empty!", Color.RED);
            return null;
        }

        List<Command> splitCommandList = new ArrayList<>();

        for (Command command : inputCommandList) {
            splitCommandList.addAll(splitCommand(command, chunkSize));
        }

        return splitCommandList;

    }

    public static List<Command> splitCommand(Command command, int chunkSize) {
        List<String> args = command.getArgs();
        int argsLength = args.size();
        List<Command> output = new ArrayList<>();

        String commandName = command.getCommandName();
        String projectName = command.getProjectName();
        String taskName = command.getTaskName();
        String optionName = command.getOptionName();

        for (int i = 0; i < argsLength; i += chunkSize) {
            List<String> spiltArgs = args.subList(i, Math.min(i + chunkSize, argsLength));
            Command splitCommand = new Command(commandName, projectName, taskName, optionName, spiltArgs);
            output.add(splitCommand);
        }

        return output;
    }

    public static String buildWithoutCommandName(Command command) {
        StringBuilder outputCommand = new StringBuilder();
        if (command.getProjectName() != null && !command.getProjectName().equals("src")) {
            outputCommand.append(command.getProjectName() + ":");
        }
        outputCommand.append(command.getTaskName());
        for (String arg : command.getArgs()) {
            outputCommand.append(" " + command.getOptionName() + " " + arg);
        }

        return outputCommand.toString();
    }

    public static TreeMap<String, List<Command>> seperateWrtTaskName(List<Command> inputCommands,
            List<String> taskPriority) {
        TreeMap<String, List<Command>> outputMap = new TreeMap<>((a, b) -> {
            return taskPriority.indexOf(a) - taskPriority.indexOf(b);
        });

        Set<String> taskNames = getTaskNames(inputCommands);

        for (String taskName : taskNames) {
            outputMap.put(taskName, new ArrayList<>());
        }

        for (Command command : inputCommands) {
            String taskName = command.getTaskName();
            outputMap.get(taskName).add(command);
        }

        return outputMap;
    }

    public static TreeMap<String, List<Command>> seperateWrtProjectName(List<Command> inputCommands) {
        TreeMap<String, List<Command>> outputMap = new TreeMap<>();

        Set<String> projectNames = getProjectNames(inputCommands);

        for (String projectName : projectNames) {
            outputMap.put(projectName, new ArrayList<>());
        }

        for (Command command : inputCommands) {
            String projectName = command.getProjectName();
            outputMap.get(projectName).add(command);
        }

        return outputMap;
    }

    public static Set<String> getTaskNames(List<Command> commands) {
        Set<String> taskNames = new HashSet<>();

        for (Command command : commands) {
            taskNames.add(command.getTaskName());
        }

        return taskNames;
    }

    public static Set<String> getProjectNames(List<Command> commands) {
        Set<String> projectNames = new HashSet<>();

        for (Command command : commands) {
            projectNames.add(command.getProjectName());
        }

        return projectNames;
    }

}
