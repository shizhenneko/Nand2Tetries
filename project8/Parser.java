import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private File file;
    private BufferedReader reader;
    private String currentCommand;

    /** Constructor */
    public Parser(File file) throws IOException {
        this.file = file;
        this.reader = new BufferedReader(new FileReader(file));
    }

    public boolean hasMoreCommands() throws IOException {
        // Mark the current position so we can reset if needed
        reader.mark(1000);
        try {
            String line = reader.readLine();
            while (line != null) {
                String trimmed = line.trim();
                // Skip empty lines and comment lines
                if (!trimmed.isEmpty() && !trimmed.startsWith("//")) {
                    reader.reset(); // Reset to before we read this line
                    return true;
                }
                line = reader.readLine();
            }
            return false;
        } finally {
            reader.reset(); // Ensure we always reset
        }
    }

    public void advance() throws IOException {
        do {
            currentCommand = reader.readLine();
            if (currentCommand == null) {
                close();
                return;
            }
        } while (!getCommand());
    }

    private boolean getCommand() {
        String stringLine = currentCommand.trim();

        // Skip empty lines
        if (stringLine.isEmpty()) {
            return false;
        }

        // Skip comment lines
        if (stringLine.startsWith("//")) {
            return false;
        }

        // Remove inline comments
        int commentIndex = stringLine.indexOf("//");
        if (commentIndex != -1) {
            stringLine = stringLine.substring(0, commentIndex).trim();
        }

        currentCommand = stringLine;
        return !stringLine.isEmpty();
    }

    public CommandType commandType() {
        if (currentCommand == null) return null;

        String commandLabel = currentCommand.split("\\s+")[0];

        switch (commandLabel) {
            case "add": case "sub": case "neg":
            case "eq":  case "gt":  case "lt":
            case "and": case "or":  case "not":
                return CommandType.C_ARITHMETIC;
            case "pop":
                return CommandType.C_POP;
            case "push":
                return CommandType.C_PUSH;
            case "label":
                return CommandType.C_LABEL;
            case "goto":
                return CommandType.C_GOTO;
            case "if-goto":
                return CommandType.C_IF;
            case "function":
                return CommandType.C_FUNCTION;
            case "call":
                return CommandType.C_CALL;
            case "return":
                return CommandType.C_RETURN;
            default:
                return null;
        }
    }

    public String args1() {
        CommandType type = commandType();
        if (type == null) return null;

        if (type.equals(CommandType.C_ARITHMETIC)) {
            return currentCommand;
        }

        String[] parts = currentCommand.split("\\s+");
        if (type.equals(CommandType.C_LABEL) ||
                type.equals(CommandType.C_GOTO) ||
                type.equals(CommandType.C_IF)) {
            return parts.length > 1 ? parts[1] : "";
        } else {
            return parts.length > 1 ? parts[1] : "";
        }
    }

    public String args2() {
        String[] parts = currentCommand.split("\\s+");
        return parts.length > 2 ? parts[2] : "";
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }

    public File getFile() {
        return file;
    }

    public String getCurrentCommand() {
        return currentCommand;
    }
}