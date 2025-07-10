import java.io.IOException;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class VMTranslator {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java VMTranslator <file.vm or directory>");
            return;
        }

        File input = new File(args[0]);
        List<File> vmFiles = new ArrayList<>();

        if (input.isFile()) {
            if (!input.getName().endsWith(".vm")) {
                System.out.println("Error: Input file must have .vm extension");
                return;
            }
            vmFiles.add(input);
        } else if (input.isDirectory()) {
            File[] files = input.listFiles((dir, name) -> name.endsWith(".vm"));
            if (files != null) {
                vmFiles.addAll(Arrays.asList(files));
            }
        }

        if (vmFiles.isEmpty()) {
            System.out.println("Error: No .vm files found");
            return;
        }

        String outputFileName = input.isDirectory() ?
                input.getPath() + "/" + input.getName() + ".asm" :
                args[0].replace(".vm", ".asm");

        try (CodeWriter codeWriter = new CodeWriter(outputFileName)) {
            for (File vmFile : vmFiles) {
                Parser parser = new Parser(vmFile.getPath());
                codeWriter.setFileName(vmFile.getName().replace(".vm", ""));

                while (parser.hasMoreCommands()) {
                    parser.advance();
                    int commandType = parser.commandType();

                    switch (commandType) {
                        case CommandType.C_ARITHMETIC:
                            codeWriter.writeArithmetic(parser.arg1());
                            break;
                        case CommandType.C_PUSH:
                        case CommandType.C_POP:
                            codeWriter.writePushPop(
                                    commandType == CommandType.C_PUSH ? "C_PUSH" : "C_POP",
                                    parser.arg1(),
                                    parser.arg2()
                            );
                            break;
                        case CommandType.C_LABEL:
                            codeWriter.writeLabel(parser.arg1());
                            break;
                        case CommandType.C_GOTO:
                            codeWriter.writeGoto(parser.arg1());
                            break;
                        case CommandType.C_IF:
                            codeWriter.writeIf(parser.arg1());
                            break;
                        case CommandType.C_FUNCTION:
                            codeWriter.writeFunction(parser.arg1(),parser.arg2());
                            break;
                        case CommandType.C_CALL:
                            codeWriter.writeCall(parser.arg1(),parser.arg2());
                            break;
                        case CommandType.C_RETURN:
                            codeWriter.writeReturn();
                            break;

                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
