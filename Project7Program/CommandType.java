import java.util.HashMap;
import java.util.Map;

public class CommandType {
    public static final int C_ARITHMETIC = 0;
    public static final int C_PUSH = 1;
    public static final int C_POP = 2;
    public static final int C_LABEL = 3;
    public static final int C_GOTO = 4;
    public static final int C_IF = 5;
    public static final int C_FUNCTION = 6;
    public static final int C_RETURN = 7;
    public static final int C_CALL = 8;

    private final Map<String,Integer> commandMap;
    public CommandType(){
        commandMap = new HashMap<>();
        initializeCommandMap();
    }

    private void initializeCommandMap(){
        commandMap.put("add", C_ARITHMETIC);
        commandMap.put("sub", C_ARITHMETIC);
        commandMap.put("neg", C_ARITHMETIC);
        commandMap.put("eq", C_ARITHMETIC);
        commandMap.put("gt", C_ARITHMETIC);
        commandMap.put("lt", C_ARITHMETIC);
        commandMap.put("and", C_ARITHMETIC);
        commandMap.put("or", C_ARITHMETIC);
        commandMap.put("not", C_ARITHMETIC);
        commandMap.put("push", C_PUSH);
        commandMap.put("pop", C_POP);
        commandMap.put("label", C_LABEL);
        commandMap.put("goto", C_GOTO);
        commandMap.put("if-goto", C_IF);
        commandMap.put("function", C_FUNCTION);
        commandMap.put("return", C_RETURN);
        commandMap.put("call", C_CALL);
    }
    public Integer getCommandType(String command){
        return commandMap.get(command);
    }

}
