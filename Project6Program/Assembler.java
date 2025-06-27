import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Assembler {
    private List<String> codes;
    private List<String> binaryCodes;
    private int allocAddress;
    private SymbolTable symTable;
    private ComputationTable compTable;
    private DestinationTable destTable;
    private JumpTable jmpTable;
    private String fileName;

    public Assembler(String filePath){
        codes = new ArrayList<>();
        binaryCodes = new ArrayList<>();
        allocAddress = 16;
        symTable = new SymbolTable();
        compTable = new ComputationTable();
        destTable = new DestinationTable();
        jmpTable = new JumpTable();

        fileName = new File(filePath).getName();
        if (!fileName.endsWith(".asm"))
            throw new IllegalArgumentException("Please choose an input file ends with .asm");
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String processedLine = line.trim();
                if (processedLine.isEmpty() || processedLine.startsWith("//")) {
                    continue;
                }
                if (processedLine.contains("//")) {
                    processedLine = processedLine.substring(0, processedLine.indexOf("//")).trim();
                }
                codes.add(processedLine);
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
    public void processLabel(){
        List<String> noLabelCodes = new ArrayList<>();
        int currLine = -1;
        for(String line : codes){
            if (line.startsWith("(")){
                String label = line.substring(1,line.length() -1);
                symTable.addEntry(label,currLine+1);
            }
            else{
                noLabelCodes.add(line);;
                currLine++;
            }
            codes = noLabelCodes;
        }
    }
    public void parse(){
        for(String line:codes){
            if (line.startsWith("@")){
                binaryCodes.add(processACommand(line.substring(1)));
            }
            else{
                binaryCodes.add(processCCommand(line));
            }
        }
    }
    private String processACommand(String command){
        if(command.matches("\\d+")){
            return decimalToBinary(command);
        }
        else if (!symTable.contains(command)){
            String binary = decimalToBinary(String.valueOf(allocAddress));
            symTable.addEntry(command,allocAddress);
            allocAddress++;
            return binary;
        }
        else{
            int address = symTable.getAddress(command);
            return decimalToBinary(String.valueOf(address));
        }

    }
    private String processCCommand(String command){
       int equalLoc = command.indexOf("=");
       int semicolonLoc = command.indexOf(";");

       String dest = "";
       String comp = "";
       String jmp = "";

       if (equalLoc ==-1)
           dest = "null";
       if (semicolonLoc==-1){
           jmp = "null";
           semicolonLoc = command.length();
       }
       if (dest.isEmpty()){
           dest = command.substring(0,equalLoc);
       }
       if (jmp.isEmpty()){
           jmp = command.substring(semicolonLoc + 1);
       }
       comp = command.substring(equalLoc + 1,semicolonLoc);
       return "111" + compTable.get(comp) + destTable.get(dest) + jmpTable.get(jmp);
    }
    private String decimalToBinary(String decimal){
        String binary = Integer.toBinaryString(Integer.parseInt(decimal));
        return String.format("%16s",binary).replace(' ','0');
    }
    public void saveBinary(){
        String binaryFilename = fileName.substring(0,fileName.length()-4) + ".hack";
        try (PrintWriter writer  = new PrintWriter(new FileWriter(binaryFilename))){
            for(String line : binaryCodes){
                writer.println(line);
            }
        } catch (IOException e){
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }
}
