import utils.CommandLineReader;
import utils.ConsoleUtils;

import java.io.File;
import java.io.IOException;

public class CommandLineManager {
    private static final String COMMAND_OPEN = "open";
    private static final String COMMAND_CLOSE = "close";
    private static final String COMMAND_SEARCH = "search";
    private static final String COMMAND_QUIT = "quit";

    private static CommandLineManager instance = null;

    private IndexSearcher indexSearcher;
    private String activeIndexPath;
    private boolean isIndexActive = false;


    private CommandLineManager() {

    }

    public static CommandLineManager getNew() {
        if (instance == null) {
            synchronized (CommandLineManager.class) {
                if (instance == null) {
                    instance = new CommandLineManager();
                }
            }
        }
        return instance;
    }


    private void writeErrorLine(String line) {
        ConsoleUtils.setColor(ConsoleUtils.AnsiColours.RED);
        ConsoleUtils.writeLine(line);
        ConsoleUtils.resetColour();
    }

    private void writeLine(String line) {
        ConsoleUtils.resetColour();
        ConsoleUtils.writeLine(line);
    }


    private boolean doIndexClose() throws IOException {
        if (isIndexActive) {
            ConsoleUtils.writeLine(String.format("Closing index %s", activeIndexPath));
            indexSearcher.close();
            isIndexActive = false;
            return true;
        } else {
            return false;
        }
    }

    private void doOpenIndex(String indexPath, boolean closeActiveIndex) throws IOException {
        if (isIndexActive && closeActiveIndex) {
            doIndexClose();
        }
        IndexSearcher indexSearcher = new IndexSearcher(new File(indexPath));
        indexSearcher.open();

        activeIndexPath = indexPath;
        this.indexSearcher = indexSearcher;
        this.isIndexActive = true;

        ConsoleUtils.writeLine("Index is opened.");
    }


    private void doSearch(String query) {
        try {
            if (!isIndexActive) {
                ConsoleUtils.writeLine("Please first open an index");
                return;
            }

            indexSearcher.search(query);
        }
        catch (Exception e) {
            ConsoleUtils.writeException(e);
        }


    }

    public void Start() {

        CommandLineReader commandLineReader = new CommandLineReader();
        commandLineReader.registerCommand(COMMAND_OPEN);
        commandLineReader.registerCommand(COMMAND_SEARCH);
        commandLineReader.registerCommand(COMMAND_QUIT);
        commandLineReader.registerCommand(COMMAND_CLOSE);


        try {
            commandLineReader.start((param) -> {
                try {
                    switch (param.getCommandType()) {
                        case EMPTY:
                            break;
                        case DEFINED:
                            if (param.getCommand().equalsIgnoreCase(COMMAND_QUIT)) {
                                doIndexClose();
                                param.setExitRequested(true);
                            } else if (param.getCommand().equalsIgnoreCase(COMMAND_OPEN)) {
                                if (!param.isArgsSpecified()) {
                                    writeErrorLine("Please specify index directory to open");
                                } else {
                                    String indexPath = param.getCommandArgs()[0];
                                    doOpenIndex(indexPath, true);
                                }
                            } else if (param.getCommand().equalsIgnoreCase(COMMAND_CLOSE)) {
                                if (!doIndexClose()) {
                                    this.writeErrorLine("No active index.");
                                }
                            } else if (param.getCommand().equalsIgnoreCase(COMMAND_SEARCH)) {
                                if (!param.isArgsSpecified()) {
                                    writeErrorLine("Please specify query text");
                                } else {
                                    String query = param.getCommandArgs()[0];
                                    doSearch(query);
                                }
                            }
                            break;
                        case UNDEFINED:
                            ConsoleUtils.setColor(ConsoleUtils.AnsiColours.RED);
                            ConsoleUtils.writeLine("Undefined command");
                            ConsoleUtils.resetColour();
                            break;
                    }

                } catch (IOException e) {
                    ConsoleUtils.writeException(e);
                }

            });
        } catch (IOException e) {
            ConsoleUtils.writeException(e);
        }

        ConsoleUtils.setColor(ConsoleUtils.AnsiColours.RED);
        ConsoleUtils.writeLine("Finished");
        ConsoleUtils.resetColour();
    }
}

