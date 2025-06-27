import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String,Integer> symbolMap;
    public SymbolTable(){
        symbolMap = new HashMap<>();
        symbolMap.put("SP",0);
        symbolMap.put("LCL",1);
        symbolMap.put("ARG",2);
        symbolMap.put("THIS",3);
        symbolMap.put("THAT",4);

        for(int i=0;i<=15;i++)
        {
            symbolMap.put("R"+i,i);
        }
        symbolMap.put("SCREEN",16384);
        symbolMap.put("KBD",24576);

    }
    public void addEntry(String symbol, int address){
        symbolMap.put(symbol,address);
    }
    public boolean contains(String symbol){
        return symbolMap.containsKey(symbol);
    }
    public int getAddress(String symbol){
        return symbolMap.get(symbol);
    }

}
