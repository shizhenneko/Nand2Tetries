public class Parse {
    private List<String> codes;
    private List<String> asmcodes;
    private String fileName;

    public void parse(String filePath){
    codes = new ArrayList<>();
    asmcodes = new ArrayList<>();
    fileName = new File(filePath).getName();

    if(!fileName.endsWith(".vm"))
        throw new IllegalArgumentException("Please choose an input file ends with .asm");

    try (BufferedReader reader = new BufferedReader)
    }
    public boolean hasMoreCommands(){

    }
    public commandType(){

    }
    public String arg1(){

    }
    public int Arg2(){

    }
}
