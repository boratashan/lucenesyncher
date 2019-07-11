package utils;

public class ConsoleUtils {

    public enum AnsiColours {
        RESET("\u001B[0m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m");

        private String ansiCode;

        AnsiColours(String ansiCode) {
            this.ansiCode = ansiCode;
        }

        private String getAnsiCode() {
            return this.ansiCode;
        }
    }



    public static void resetColour() {
        System.out.print(AnsiColours.RESET.getAnsiCode());
    }

    public static void setColor(AnsiColours color) {
        System.out.print(color.getAnsiCode());
    }


    public static void writeLine(String line) {
        System.out.println(line);
    }
    public static void write(String message) {
        System.out.print(message);
    }

    public static void clearScreen(){
        System.out.println("\u001B[2j");
    }

    public static void writeException(Exception e) {
        ConsoleUtils.setColor(ConsoleUtils.AnsiColours.RED);
        ConsoleUtils.writeLine(String.format("Error - %s", e.getMessage()));
        ConsoleUtils.resetColour();
    }

}
