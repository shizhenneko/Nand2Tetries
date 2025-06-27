import java.util.HashMap;
import java.util.Map;

public class DestinationTable {
    private Map<String,String> destMap;
    public DestinationTable(){
        destMap = new HashMap<>();
        destMap.put("null", "000");
        destMap.put("M", "001");
        destMap.put("D", "010");
        destMap.put("MD", "011");
        destMap.put("A", "100");
        destMap.put("AM", "101");
        destMap.put("AD", "110");
        destMap.put("AMD", "111");
    }
    public String get(String dest){
        return destMap.get(dest);
    }
}
