import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class VMTest {

    public static void main(String[] args) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader("SimpleAdd.vm"));
            BufferedWriter writer = new BufferedWriter(new FileWriter("out.asm"));
            String line;
            int sp = 256;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("//") || line.isEmpty()) {
                    continue;
                } else {
                    String[] x = line.split(" ");

                    String stack = "";
                    System.out.println(Arrays.toString(x));
                    if(x.length==3)
                    {
                        int y = remainingpart(x[1], x[2]);


                        if (x[0].equals("push")) {
                            stack = "@" + y + "\nD=A\n@" + sp + "\nA=M\nM=D\n@" + "sp" + "\nM=M+1\n";
                            sp++;
                            writer.write(stack);
                            System.out.print(stack);
                        } else if (x[0].equals("pop")) {
                            stack = "@" + y + "\nD=A\n@" + memory(x[1], x[2]) + "\nD=M+D\n@R13\nM=D\n"
                                    + "@SP\nAM=M-1\nD=M\n@R13\nA=M\nM=D\n";
                            sp++;
                            writer.write(stack);
                            System.out.print(stack);
                        }

                    }else if (x[0].equals("add")||x[0].equals("sub")||x[0].equals("neg")||x[0].equals("eq")||x[0].equals("lt")||x[0].equals("gt")||x[0].equals("and")||x[0].equals("or")||x[0].equals("not")) {
                        stack = arg(line);
                        writer.write(stack);
                        //System.out.println(stack);
                    }
                    else if(x[0].equals("label"))
                    {
                        stack = "("+x[1]+")\n";
                        writer.write(stack);
                        //System.out.println(stack);
                    }
                    else if(x[0].equals("if-goto"))
                    {
                        stack = "@"+x[1]+"\n";
                        writer.write(stack);
                        //System.out.println(stack);

                    }
                }
            }
            reader.close();
            writer.close();

            System.out.println("Translation completed successfully.");

        } catch (FileNotFoundException e) {
            System.out.println("Input file not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Error reading or writing the file: " + e.getMessage());
        }
    }

    public static String arg(String string) {
        String oneFromStack = "@SP\nA=M-1\n";
        String twoFromStack = "@SP\nAM=M-1\nD=M\nA=A-1\n";
        String asm = "";
        switch (string) {
            case "add":
                asm += twoFromStack + "M=M+D\n";
                break;
            case "sub":
                asm += twoFromStack + "M=M-D\n";
                break;
            case "neg":
                asm += oneFromStack + "M=-M\n";
                break;
            case "eq":
            case "lt":
            case "gt":
                String jump = "J" + string.toUpperCase();
                asm += "@" + (14) + "\nD=A\n@R13\nM=D\n" + twoFromStack + "D=M-D\n";
                asm += "@" + jump + "\nD;" + jump + "\n@R14\nA=M\nM=-1\n";
                asm += "@CONTINUE\n0;JMP\n";
                asm += "(TRUE)\n@R14\nA=M\nM=0\n";
                asm += "(CONTINUE)\n@R14\nA=M\n0;JMP\n";
                break;
            case "and":
                asm += twoFromStack + "M=M&D\n";
                break;
            case "or":
                asm += twoFromStack + "M=M|D\n";
                break;
            case "not":
                asm += oneFromStack + "M=!M\n";
                break;
        }
        return asm;
    }

    private static String memory(String string, String string2) {
        if (string.equals("local")) {
            return "LCL";
        } else if (string.equals("argument")) {
            return "ARG";
        } else if (string.equals("this")) {
            return "THIS";
        } else if (string.equals("that")) {
            return "THAT";
        } else if (string.equals("temp")) {
            int y = 5 + (Integer.parseInt(string2));
            return Integer.toString(y);
        } else if (string.equals("pointer")) {
            int y = 3 + (Integer.parseInt(string2));
            return Integer.toString(y);
        } else if (string.equals("constant")) {
            return string2;
        } else if (string.equals("static")) {
            int y = 16 + (Integer.parseInt(string2));
            return Integer.toString(y);
        }
        return null;
    }

    private static int remainingpart(String string1, String string2) {
        int y = Integer.parseInt(string2);
        if (string1.equals("local")) {
            return 300 + y;
        } else if (string1.equals("argument")) {
            return 400 + y;
        } else if (string1.equals("this")) {
            return 3000 + y;
        } else if (string1.equals("that")) {
            return 3010 + y;
        } else if (string1.equals("temp")) {
            return 5 + y;
        } else if (string1.equals("pointer")) {
            return 800 + y;
        } else if (string1.equals("constant")) {
            return y;
        } else if (string1.equals("static")) {
            return 16 + y;
        }
        return 0;
    }
}