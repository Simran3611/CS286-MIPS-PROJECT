package com.nathanieltooley.mips;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.nathanieltooley.mips.Instruction.splitMipsCommand;

public class Main {

    private static final int MAX_REGISTERS = 32;

    public static int[] registers = new int[MAX_REGISTERS];

    public static void main(String[] args) {
        byte[] bytes = readBinaryFile(args[0]);
        int memoryAddress = 96;

        String[] bytes32 = getBytesAs32Bits(bytes);

        for (String word : bytes32) {
            Instruction inst = new Instruction(word);
            printMipsCommand(inst.sepStrings);
            System.out.print(memoryAddress);

            if (inst.valid == 0) {
                System.out.print(" Invalid Instruction");
            }
            else if (inst.opcode == 40) {
                System.out.printf(" ADDI\t R%s, R%s, #%s", inst.rt, inst.rs, inst.immd);
            }
            else if (inst.opcode == 43) {
                System.out.printf(" SW  \t R%s, %s(R%s)", inst.rt, inst.immd, inst.rs);
            }
            else if (inst.opcode == 32 && inst.func == 0) {
                // SLL Command
            }
            else if (inst.opcode == 32 && inst.func == 34){
                System.out.printf(" SUB \t R%s, R%s, R%s", inst.rd, inst.rs, inst.rt);
            }


            System.out.println();
            memoryAddress += 4;
        }
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



    public static void printMipsCommand(String[] mips) {
        for (int i = 0; i < 7; i++) {
            System.out.print(mips[i] + " ");
        }
    }
}


