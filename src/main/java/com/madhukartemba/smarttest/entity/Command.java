package com.madhukartemba.smarttest.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Command {
    private String commandName;
    private String projectName;
    private String taskName;
    private String optionName;
    private List<String> args;

    public Command(String commandName, String projectName, String taskName, String optionName, List<String> args) {
        this.commandName = commandName;
        this.projectName = projectName;
        this.taskName = taskName;
        this.optionName = optionName;
        this.args = args;
    }

    public Command(String commandName, String projectName, String taskName, String optionName, String arg) {
        this.commandName = commandName;
        this.projectName = projectName;
        this.taskName = taskName;
        this.optionName = optionName;
        addArg(arg);
    }

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getOptionName() {
        return optionName;
    }

    public void setOptionName(String optionName) {
        this.optionName = optionName;
    }

    public List<String> getArgs() {
        return args;
    }

    public void setArgs(List<String> args) {
        this.args = args;
    }

    public void addArg(String arg) {
        if (args == null) {
            args = new ArrayList<>();
        }

        args.add(arg);
    }

    @Override
    public int hashCode() {
        return Objects.hash(commandName, projectName, taskName, optionName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Command command = (Command) o;
        return Objects.equals(commandName, command.commandName) &&
                Objects.equals(projectName, command.projectName) &&
                Objects.equals(taskName, command.taskName) &&
                Objects.equals(optionName, command.optionName);
    }

}
