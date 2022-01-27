package com.nathanieltooley.mips;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    private static final int MAX_REGISTERS = 32;

    public static int[] registers = new int[MAX_REGISTERS];

    public static void main(String[] args) {
        byte[] bytes = readBinaryFile(args[0]);
        int memoryAddress = 96;

        String[] bytes32 = getBytesAs32Bits(bytes);

        for (String word : bytes32) {
            String[] sep = splitMipsCommand(word);
            printMipsCommand(sep);
            System.out.print(memoryAddress);

            if (sep[0].equals("0")) {
                System.out.print(" Invalid Instruction");
            }

//             if ((sep[0] + sep[1]).equals("101000")) {
//                 System.out.printf(" ADDI\t " + "R{0}" +  " R{1}");
//             }
            else if ((sep[0] + sep[1]).equals("101000")) {
                System.out.println(" ADDI\t " + "R" + sep['rt'] + " R" + sep['rs'] + "#" + sep['sa']);
                //Not sure what element would have the number 10 in it? You could just use the convert to decimal function for this part with the right parameter.
            }
            else if ((sep[0] + sep[1]).equals("100000")) {
                System.out.println(" SUB\t " + "R" + sep['rt'] + " R" + sep['rs'] + "R" + sep['sa']); //need to fix the last part
            }
            else if ((sep[0] + sep[1]).equals("101011")) {
                System.out.println(" SW\t " + "R" + sep['rt'] + ", " + sep['rs'] + " (R" + sep['sa'] + ")"); //fix the immediate value
            }


            System.out.println();
            memoryAddress += 4;
        }

//        printBytesAs32Bits(bytes);
    }
    public static int binToDec(String binstr) {
         int dec = Integer.parseInt(binstr, 2);
         return dec;
    }


    public static byte[] readBinaryFile(String filename) {
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filename));

            return bytes;
        } catch (IOException ex) {
            System.out.println("Could not read file: " + filename);
            System.exit(-1);
        }

        return null;
    }

    public static String getByteAsBinaryString(byte b) {
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public static void printBytesAs32Bits(byte[] bytes) {
        int j = 0;
        for (byte b : bytes) {
            if (j < 3) {
                System.out.print(getByteAsBinaryString(b));
                j++;
            } else {
                System.out.println(getByteAsBinaryString(b));
                j = 0;
            }
        }
    }

    public static String[] getBytesAs32Bits(byte[] bytes) {
        String[] bytes32 = new String[bytes.length / 4];
        String temp = "";
        int j = 0;
        int stringArrayIndex = 0;
        for (byte b : bytes) {
            temp += getByteAsBinaryString(b);
            if (j < 3) {
                j++;
            } else {
                bytes32[stringArrayIndex] = temp;
                temp = "";
                j = 0;
                stringArrayIndex++;
            }
        }

        return bytes32;
    }

    public static String[] splitMipsCommand(String byteString) {
        String validInstruction = byteString.substring(0, 1);
        String opCode = byteString.substring(1, 6);
        String rs = byteString.substring(6, 11);
        String rt = byteString.substring(11, 16);
        String rd = byteString.substring(16, 21);
        String sa = byteString.substring(21, 26);
        String funct = byteString.substring(26, 32);

        return new String[]{validInstruction, opCode, rs, rt, rd, sa, funct};
    }

    public static void printMipsCommand(String[] mips) {
        for (String section : mips) {
            System.out.print(section + " ");
        }
    }

    public class Instruction {

        public String binString;
        public String[] sepStrings;

        public int valid;
        public int asInt;
        public int asUint;
        public int opcode;
        public int rs;
        public int rt;
        public int rd;
        public int sa;
        public int shamt;
        public int func;

        public Instruction(String binString) {
            this.binString = binString;
            this.sepStrings = splitMipsCommand(binString);


        }

    }
}


