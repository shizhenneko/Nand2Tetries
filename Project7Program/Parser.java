import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Parser {
    private final List<String> commands;
    private int currentCommandIndex;
    private String currentCommand;
    private final CommandType commandType;

    public Parser(String filePath) throws IOException {
        commands = new ArrayList<>();
        currentCommandIndex = 0;
        commandType = new CommandType();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                // 移除注释和空行
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("//")) {
                    continue;
                }
                // 移除行内注释
                int commentIndex = trimmedLine.indexOf("//");
                if (commentIndex != -1) {
                    trimmedLine = trimmedLine.substring(0, commentIndex).trim();
                }
                commands.add(trimmedLine);
            }
        }
    }

    public boolean hasMoreCommands() {
        return currentCommandIndex < commands.size();
    }

    public void advance() {
        if (hasMoreCommands()) {
            currentCommand = commands.get(currentCommandIndex++);
        }
    }

    public int commandType() {
        if (currentCommand == null) {
            throw new IllegalStateException("No command available");
        }
        String[] parts = currentCommand.split("\\s+");
        Integer type = commandType.getCommandType(parts[0]);
        if (type == null) {
            throw new IllegalArgumentException("Unknown command: " + parts[0]);
        }
        return type;
    }

    public String arg1() {
        int type = commandType();
        String[] parts = currentCommand.split("\\s+");
        if (type == CommandType.C_ARITHMETIC) {
            return parts[0];
        } else if (type != CommandType.C_RETURN) {
            if (parts.length < 2) {
                throw new IllegalArgumentException("Command missing argument: " + currentCommand);
            }
            return parts[1];
        }
        throw new IllegalStateException("arg1() should not be called for RETURN commands");
    }

    public int arg2() {
        int type = commandType();
        String[] parts = currentCommand.split("\\s+");
        if (type == CommandType.C_PUSH || type == CommandType.C_POP ||
                type == CommandType.C_FUNCTION || type == CommandType.C_CALL) {
            if (parts.length < 3) {
                throw new IllegalArgumentException("Command missing second argument: " + currentCommand);
            }
            try {
                return Integer.parseInt(parts[2]);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format: " + parts[2]);
            }
        }
        throw new IllegalStateException("arg2() should only be called for PUSH, POP, FUNCTION or CALL commands");
    }
}