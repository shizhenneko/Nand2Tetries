import java.util.HashMap;
import java.util.Map;

public class JumpTable {
    public Map<String,String> jmpMap;
    public JumpTable(){
        jmpMap = new HashMap<>();
        jmpMap.put("null", "000");
        jmpMap.put("JGT", "001");
        jmpMap.put("JEQ", "010");
        jmpMap.put("JGE", "011");
        jmpMap.put("JLT", "100");
        jmpMap.put("JNE", "101");
        jmpMap.put("JLE", "110");
        jmpMap.put("JMP", "111");
    }
    public String get(String jmp){
        return jmpMap.get(jmp);
    }
}
