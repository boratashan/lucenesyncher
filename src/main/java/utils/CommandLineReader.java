package utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class CommandLineReader {

    @FunctionalInterface
    public interface CommandRead {
        public void process(CommandLineReaderParam param);
    }

    InputStream inputStream;

    private List<String > commandRegistry;
    private BufferedReader br;



    public CommandLineReader() {
        this(System.in);
    }

    public CommandLineReader(InputStream inputStream ) {
        this.inputStream = inputStream;
        commandRegistry = new ArrayList<String>();

    }

    private void close() throws IOException {
        this.br.close();
    }

    public CommandLineReader registerCommand(String command){
        commandRegistry.add(command.toUpperCase());
        return this;
    }

    private void printPromt() {
        ConsoleUtils.write("$>");
    }

    public void start(CommandRead commandRead) throws IOException {
        this.br = new BufferedReader((new InputStreamReader(this.inputStream)));
        String line;
        CommandType commandType = CommandType.EMPTY;
        boolean keepScan = true;
        while(keepScan) {
            ConsoleUtils.setColor(ConsoleUtils.AnsiColours.GREEN);
            printPromt();
            line = br.readLine();
            keepScan = line != null;
            if (!keepScan)
                break;

            if (!line.isEmpty()){
                StringTokenizer tokenizer = new StringTokenizer(line);
                String command = tokenizer.nextToken().trim().toUpperCase();
                if (commandRegistry.contains(command)) {
                    commandType = CommandType.DEFINED;
                    String[] args = new String[tokenizer.countTokens()];
                    int cnt = 0;
                    while(tokenizer.hasMoreElements()) {
                        args[cnt] = tokenizer.nextToken();
                        cnt++;
                    }
                    CommandLineReaderParam param = new CommandLineReaderParam(commandType, command, args);
                    commandRead.process(param);
                    if (param.getExitRequested()) {
                        break;
                    }
                }
                else {
                    commandRead.process(new CommandLineReaderParam(CommandType.UNDEFINED, null, null));
                }
            }
            else {
                commandRead.process(new CommandLineReaderParam(CommandType.EMPTY, null, null));
            }
        }
        this.close();
    }



}
