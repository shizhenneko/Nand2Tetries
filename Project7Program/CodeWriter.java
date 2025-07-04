import java.io.FileWriter;
import java.io.IOException;

class CodeWriter implements AutoCloseable {
    private final FileWriter writer;
    private String fileName;
    private int labelCounter = 0;
    private String currFunc;
    public CodeWriter(String outputFile) throws IOException {
        this.currFunc = "";
        this.writer = new FileWriter(outputFile);
        writer.write("@256\nD=A\n@SP\nM=D\n");
        writeCall("Sys.init",0);
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void writeArithmetic(String command) throws IOException {
        switch (command) {
            case "add":
                writer.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=D+M\n");
                break;
            case "sub":
                writer.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=M-D\n");
                break;
            case "neg":
                writer.write("@SP\nA=M-1\nM=-M\n");
                break;
            case "eq":
            case "gt":
            case "lt":
                writer.write(createComparisonAssembly(command));
                break;
            case "and":
                writer.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=D&M\n");
                break;
            case "or":
                writer.write("@SP\nAM=M-1\nD=M\nA=A-1\nM=D|M\n");
                break;
            case "not":
                writer.write("@SP\nA=M-1\nM=!M\n");
                break;
        }
    }

    private String createComparisonAssembly(String command) {
        String jumpInstruction = command.equals("eq") ? "JEQ" :
                command.equals("gt") ? "JGT" : "JLT";
        String label = "COMP_" + labelCounter++;
        return "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "A=A-1\n" +
                "D=M-D\n" +
                "@" + label + "\n" +
                "D;" + jumpInstruction + "\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=0\n" +
                "@END_" + label + "\n" +
                "0;JMP\n" +
                "(" + label + ")\n" +
                "@SP\n" +
                "A=M-1\n" +
                "M=-1\n" +
                "(END_" + label + ")\n";
    }

    public void writePushPop(String command, String segment, int index) throws IOException {
        if (command.equals("C_PUSH")) {
            switch (segment) {
                case "constant":
                    writer.write("@" + index + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "local":
                case "argument":
                case "this":
                case "that":
                    writer.write(pushFromSegment(segment, index));
                    break;
                case "temp":
                    writer.write("@5\nD=A\n@" + index + "\nA=D+A\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "pointer":
                    String pointer = index == 0 ? "THIS" : "THAT";
                    writer.write("@" + pointer + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
                case "static":
                    writer.write("@" + fileName + "." + index + "\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
                    break;
            }
        } else if (command.equals("C_POP")) {
            switch (segment) {
                case "local":
                case "argument":
                case "this":
                case "that":
                    writer.write(popToSegment(segment, index));
                    break;
                case "temp":
                    writer.write("@5\nD=A\n@" + index + "\nD=D+A\n@R13\nM=D\n@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n");
                    break;
                case "pointer":
                    String pointer = index == 0 ? "THIS" : "THAT";
                    writer.write("@SP\nAM=M-1\nD=M\n@" + pointer + "\nM=D\n");
                    break;
                case "static":
                    writer.write("@SP\nAM=M-1\nD=M\n@" + fileName + "." + index + "\nM=D\n");
                    break;
            }
        }
    }
    public void writeLabel(String label) throws IOException{
        writer.write("(" + getFullLabel(label) + ")\n");
    }
    public void writeGoto(String label) throws IOException{
        writer.write("@" + getFullLabel(label) +"\n0;JMP\n");
    }
    public void writeIf(String label) throws IOException{
        writer.write("@SP\nAM=M-1\nD=M\n@" + getFullLabel(label) + "\nD;JNE\n");
    }
    public void writeFuntion(String functionName, int nVars) throws IOException{
       currFunc = functionName;
       writer.write("(" + functionName + ")\n");
       for(int i=0;i<nVars;i++){
           writer.write("@SP\nA=M\nM=0\n@SP\nM=M+1");
       }
    }
    public void writeCall(String functionName, int nArgs) throws IOException{
        String returnLabel = "RETURN_" + labelCounter++;
        writer.write("@" + returnLabel + "\nD=A\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        writer.write("@LCL\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        writer.write("@ARG\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        writer.write("@THIS\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        writer.write("@THAT\nD=M\n@SP\nA=M\nM=D\n@SP\nM=M+1\n");
        writer.write("@SP\nD=M\n@5\nD=D-A\n@" + nArgs + "\nD=D-A\n@ARG\nM=D\n");
        writer.write("@SP\nD=M\n@LCL\nM=D\n");
        writer.write("@" + functionName + "\n0;JMP\n");
        writer.write("(" + returnLabel + ")\n");
    }
    public void writeReturn() throws IOException{
        writer.write("@LCL\nD=M\n@R13\nM=D\n");
        writer.write("@5\nA=D-A\nD=M\n@R14\nM=D");
        writer.write("@SP\nAM=M-1\nD=M\n@ARG\nA=M\nM=D\n");
        writer.write("@ARG\nD=M+1\n@SP\nM=D\n");
        writer.write("@R13\nAM=M-1\nD=M\n@THAT\nM=D\n");
        writer.write("@R13\nAM=M-1\nD=M\n@THIS\nM=D\n");
        writer.write("@R13\nAM=M-1\nD=M\n@ARG\nM=D\n");
        writer.write("@R13\nAM=M-1\nD=M\n@LCL\nM=D\n");
        writer.write("@R14\nA=M\n0;JMP\n");
    }
    private String pushFromSegment(String segment, int index) {
        String base = getBaseAddress(segment);
        return "@" + base + "\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "A=D+A\n" +
                "D=M\n" +
                "@SP\n" +
                "A=M\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M+1\n";
    }

    private String popToSegment(String segment, int index) {
        String base = getBaseAddress(segment);
        return "@" + base + "\n" +
                "D=M\n" +
                "@" + index + "\n" +
                "D=D+A\n" +
                "@R13\n" +
                "M=D\n" +
                "@SP\n" +
                "AM=M-1\n" +
                "D=M\n" +
                "@R13\n" +
                "A=M\n" +
                "M=D\n";
    }
    private String getFullLabel(String label){
        return currFunc.isEmpty()?label:currFunc + "$" + label;
    }
    private String getBaseAddress(String segment) {
        switch (segment) {
            case "local": return "LCL";
            case "argument": return "ARG";
            case "this": return "THIS";
            case "that": return "THAT";
            default: throw new IllegalArgumentException("Invalid segment: " + segment);
        }
    }


    @Override
    public void close() throws IOException {
        writer.close();
    }
}