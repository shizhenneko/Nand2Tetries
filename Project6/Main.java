public class Main {
    public static void main(String[] args){
        if (args.length==0)
        {
            System.out.println("Please provide an input file");
            return;
        }
        String filePath = args[0];
        Assembler assembler = new Assembler(filePath);
        assembler.processLabel();
        assembler.parse();
        assembler.saveBinary();
    }
}
