package utils;

public class CommandLineReaderParam {
    private CommandType commandType;
    private String command;
    private String[] commandArgs;
    private boolean isExitRequested = false;

    public CommandLineReaderParam(CommandType commandType, String command, String[] commandArgs) {
        this.commandType = commandType;
        this.command = command;
        this.commandArgs = commandArgs;
    }

    public CommandType getCommandType() {
        return commandType;
    }

    public String getCommand() {
        return command;
    }

    public String[] getCommandArgs() {
        return commandArgs;
    }

    public Boolean getExitRequested() {
        return isExitRequested;
    }

    public void setExitRequested(Boolean exitRequested) {
        isExitRequested = exitRequested;
    }

    public boolean isArgsSpecified() {
        boolean result = commandArgs !=null;
        if (result) {
            result = commandArgs.length > 0;
        }
        return result;
    }
}
