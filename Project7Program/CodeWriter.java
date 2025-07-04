import java.io.FileWriter;
import java.io.IOException;

class CodeWriter implements AutoCloseable {
    private final FileWriter writer;
    private String fileName;
    private int labelCounter = 0;

    public CodeWriter(String outputFile) throws IOException {
        this.writer = new FileWriter(outputFile);
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