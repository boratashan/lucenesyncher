import org.apache.commons.cli.*;


import java.util.ArrayList;

public class CommandLineArgsProcessor {

    private CommandLineParser parser;


    private Options options;

    public CommandLineArgsProcessor() {
        this.parser = new DefaultParser();
        this.options = new Options();

        options.addOption("h", "help", false, "print help.");
        options.addOption("i", "index", false, "creates index for given data under given index folder.");
        options.addOption("df", "datafile", true, "name of the data file in csv format.");
        options.addOption("if", "indexfolder", true, "Path of the index folder.");
        options.addOption("c", "console", false, "Open commandline interface");

        Option option = new Option("idf", "idfields", true, "list of the id fields to be stored(Document identifier)");
        option.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(option);

        option = new Option("ixf", "indexfields", true, "list of the fields to be indexed");
        option.setArgs(Option.UNLIMITED_VALUES);
        options.addOption(option);
    }

    public Options getOptions() {
        return options;
    }

    public ApplicationParams buildApplicationParams(String[] args) throws ParseException, MissingParamsException {
        CommandLine commandLine = parser.parse(options, args);

        ApplicationParams params = new ApplicationParams();

        if (commandLine.hasOption("h")) {
            params.commandMode = CommandMode.PRINT_HELP;

        } else if (commandLine.hasOption("i")) {
            params.commandMode = CommandMode.INDEX_CREATE;
        } else if (commandLine.hasOption("c")) {
            params.commandMode = CommandMode.CLI;

        } else {
            throw new MissingParamsException("Missing params!");
        }

        switch (params.commandMode) {
            case PRINT_HELP:
                break;
            case INDEX_CREATE:
                processIndexCreation(commandLine, params);
                break;
            case CLI:
                //processCli(commandLine, params);
                break;
        }

        return params;
    }


    private void processIndexCreation(CommandLine commandLine, ApplicationParams params) throws MissingParamsException {
        int requiredArg = 4;
        if (commandLine.hasOption("if")) {
            params.pathOfIndex = commandLine.getOptionValue("if");
            requiredArg--;
        }
        if (commandLine.hasOption("df")) {
            params.dataFileFullName = commandLine.getOptionValue("df");
            requiredArg--;
        }
        if (commandLine.hasOption("idf")) {//Process id fields
            params.csvIdFields = new ArrayList<String>();
            for (String value : commandLine.getOptionValues("idf")) {
                params.csvIdFields.add(value);
            }
            if (params.csvIdFields.size() == 0) {
                throw new MissingParamsException("id fields should be specified");
            }
            requiredArg--;
        }
        if (commandLine.hasOption("ixf")) {//Process index fields
            params.csvIndexFields = new ArrayList<String>();
            for (String value : commandLine.getOptionValues("ixf")) {
                params.csvIndexFields.add(value);
            }
            if (params.csvIndexFields.size() == 0) {
                throw new MissingParamsException("at least one index field should be specified");
            }
            requiredArg--;
        }

        if (requiredArg > 0)
            throw new MissingParamsException("Unsufficient parameters for index creation process!");
    }

    private void processCli(CommandLine commandLine, ApplicationParams params) throws MissingParamsException {
        if (commandLine.hasOption("if")) {
            params.pathOfIndex = commandLine.getOptionValue("if");
        } else throw new MissingParamsException("Index folder must be specified for CLI!");

    }

}
