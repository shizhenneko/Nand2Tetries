import java.util.HashMap;
import java.util.Map;

public class ComputationTable {
    private Map<String,String> compMap;
    public ComputationTable(){
        compMap = new HashMap<>();
        compMap.put("0", "0101010");
        compMap.put("1", "0111111");
        compMap.put("-1", "0111010");
        compMap.put("D", "0001100");
        compMap.put("A", "0110000");
        compMap.put("!D", "0001101");
        compMap.put("!A", "0110001");
        compMap.put("-D", "0001111");
        compMap.put("-A", "0110011");
        compMap.put("D+1", "0011111");
        compMap.put("A+1", "0110111");
        compMap.put("D-1", "0001110");
        compMap.put("A-1", "0110010");
        compMap.put("D+A", "0000010");
        compMap.put("D-A", "0010011");
        compMap.put("A-D", "0000111");
        compMap.put("D&A", "0000000");
        compMap.put("D|A", "0010101");
        compMap.put("M", "1110000");
        compMap.put("!M", "1110001");
        compMap.put("-M", "1110011");
        compMap.put("M+1", "1110111");
        compMap.put("M-1", "1110010");
        compMap.put("D+M", "1000010");
        compMap.put("D-M", "1010011");
        compMap.put("M-D", "1000111");
        compMap.put("D&M", "1000000");
        compMap.put("D|M", "1010101");
    }
    public String get(String comp){
        return compMap.get(comp);
    }
}
