import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class VMTranslator {
    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Enter the file name");
            String fileName = sc.nextLine();
            sc.close();
            String[] arithmetic = {"add", "sub", "neg", "eq", "lt", "gt", "and", "or", "not"};
            BufferedReader myReader = new BufferedReader(new FileReader(fileName));
            ArrayList<String> FileLines = new ArrayList<>();
            String LineRead;
            while ((LineRead = myReader.readLine()) != null) {
                if (!LineRead.startsWith("//") && !LineRead.isEmpty()) {
                    if (LineRead.contains("//")) {
                        LineRead = LineRead.substring(0, LineRead.indexOf("//"));
                    }
                    FileLines.add(LineRead);
                }
            }
            myReader.close();

            BufferedWriter myWriter = new BufferedWriter(new FileWriter("out.asm"));
            for ( String line : FileLines) {
                String[] instr = line.split(" ");
                String assemblyCode;
                if (instr.length == 3) {
                    if (instr[0].equals("push")) {
                        assemblyCode = HandlePush(instr[1], instr[2]);
                    } else if (instr[0].equals("pop")) {
                        assemblyCode = HandlePop(instr[1], instr[2]);
                    }
                    else {
                        System.out.println("Error: Invalid instruction");
                        break;
                    }
                } else if (Arrays.asList(arithmetic).contains(instr[0])) {
                    assemblyCode = HandleArithmetic(line);
                }
                else if(instr[0].equals("label"))
                {
                    assemblyCode = "("+instr[1]+")\n";
                }
//                else if(instr[0].equals("goto")){
//                    assemblyCode = "@{label}\n" + "0;JMP\n";
//                    assemblyCode = assemblyCode.replace("{label}", instr[1]);
//                }
//                else if(instr[0].equals("if-goto"))
//                {
//                    assemblyCode = "@{label}\n";
//                    assemblyCode = assemblyCode.replace("{label}", instr[1]);
//                }

//                else if(instr[0].equals("function")){
//                    assemblyCode = "("+instr[1]+")\n";
//                }
//                else if(instr[0].equals("call")){
//                    assemblyCode = "@"+instr[1]+"\n";
//                }
//                else if(instr[0].equals("return")){
//                    assemblyCode = "@"+instr[1]+"\n";
//                }
                else {
                    System.out.println("Error: Invalid instruction");
                    break;
                }
                System.out.println(line);
                System.out.println(assemblyCode);
                myWriter.write(assemblyCode);
            }
            myWriter.close();
            System.out.println("Translation completed successfully.");
        } catch (FileNotFoundException e) {
            System.out.println("Error File not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading or writing the file: " + e.getMessage());
        }
    }
    public static String HandleArithmetic(String command) {
        switch (command) {
            case "add":
                return  "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "M=M+D\n" +
                        "@SP\n" +
                        "M=M+1\n";
            case "sub":
                return  "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "M=M-D\n" +
                        "@SP\n" +
                        "M=M+1\n";
            case "neg":
                return  "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "M=-M\n";
            case "eq":
                return  "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M-D\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n";
            case "gt":
                return  "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M-D\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=-1\n" +
                        "@SP\n" +
                        "M=M+1\n" +
                        "@SP\n" +
                        "M=M-1\n";
            case "lt":
                return  "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M-D\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=0\n" +
                        "@SP\n" +
                        "M=M+1\n" +
                        "@SP\n" +
                        "M=M-1\n";
            case "and":
                return  "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "M=D&M\n";
            case "or":
                return  "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "M=D|M\n";
            case "not":
                return  "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "M=!M\n";
        }
        return "";
    }
    public static String HandlePush(String MemorySegment, String index) {
        switch (MemorySegment){
            case "constant":
                return  "@{i}\n".replace("{i}",index) +
                        "D=A\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n";
            case "temp":
                return  "@{i}\n".replace("{i}",index) +
                        "D=A\n" +
                        "@5\n" +
                        "A=D+A\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n";
            case "static":
                // file.index need to like that properly in the asm file
                return  "@{i}\n".replace("{i}",index) +
                        "D=A\n" +
                        "@16\n" +
                        "A=D+A\n" +
                        "D=M\n" +
                        "@SP\n" +
                        "A=M\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M+1\n";
            case "pointer":
                if (index.equals("0")){
                    return "@THIS\n" +
                            "D=M\n" +
                            "@SP\n" +
                            "A=M\n" +
                            "M=D\n" +
                            "@SP\n" +
                            "M=M+1\n";
                }else{
                    return  "@THAT\n" +
                            "D=M\n" +
                            "@SP\n" +
                            "A=M\n" +
                            "M=D\n" +
                            "@SP\n" +
                            "M=M+1\n";
                }
            case "local":
            case "argument":
            case "this":
            case "that":
                return  "@{i}\n".replace("{i}",index) +
                        "D=A\n" +
                        "@{segment}\n".replace("{segment}",MemorySegment) +
                        "D=D+M\n" +
                        "@R13\n" +
                        "M=D\n" +
                        "@SP\n" +
                        "M=M-1\n" +
                        "A=M\n" +
                        "D=M\n" +
                        "@R13\n" +
                        "A=M\n" +
                        "M=D\n";
        }
        return "";
    }
    public static String HandlePop(String MemorySegment, String index) {
        String out;
        switch (MemorySegment){
            case "local":
                out = "LCL";
                break;
            case "argument":
                out = "ARG";
                break;
            case "this":
                out = "THIS";
                break;
            case "that":
                out = "THAT";
                break;
            case "temp":
                out = Integer.toString(5 + (Integer.parseInt(index)));
                break;
            case "pointer":
                out = Integer.toString(3 + (Integer.parseInt(index)));
                break;
            case "static":
                out = Integer.toString(16 + (Integer.parseInt(index)));
                break;
            default:
                out = "";
                break;
        }
        String assemblyCode =
                "@" + index + "\n" +
                "D=A\n" +
                "@"+out + "\n" +
                "D=M+D\n" +
                "@R13\n" +
                "M=D\n" +
                "@SP\n" +
                "M=M-1\n" +
                "A=M\n" +
                "D=M\n" +
                "@R13\n" +
                "A=M\n" +
                "M=D\n";
        System.out.println(assemblyCode);
        return assemblyCode;
    }
//    public static int HandleValue(String string1, String string2) {
//        int y = Integer.parseInt(string2);
//        switch (string1){
//            case "local":
//                return 300 + y;
//            case "argument":
//                return 400 + y;
//            case "this":
//                return 3000 + y;
//            case "that":
//                return 3010 + y;
//            case "temp":
//                return 5 + y;
//            case "pointer":
//                return 800 + y;
//            case "constant":
//                return y;
//            case "static":
//                return 16 + y;
//        }
//        return 0;
//    }
}