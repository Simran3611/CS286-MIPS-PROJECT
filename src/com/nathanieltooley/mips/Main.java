package com.nathanieltooley.mips;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final int MAX_REGISTERS = 32;

    public static int[] registers = new int[MAX_REGISTERS];
    public static Map<Integer, Integer> data = new HashMap<>();
    public static ArrayList<Instruction> instructions = new ArrayList<>();

    public static void main(String[] args) {
        // System.out.println(System.getProperty("user.dir"));
        byte[] bytes = readBinaryFile(args[0]);
        int memoryAddress = 96;

        String[] bytes32 = getBytesAs32Bits(bytes);

        boolean reachedBreak = false;

        FileWriter fileWriter = getFileWriter(args[1]);

        System.out.println("======================");
        System.out.println("      Disassembly     ");
        System.out.println("======================");

        // first loop (disassembly)
        for (String word : bytes32) {
            if (reachedBreak){
                int dataValue = Instruction.binToDec(word, true);
                System.out.printf("%s\t    %s\t %s", word, memoryAddress, dataValue);
                writeToFile(fileWriter, String.format("%s\t    %s\t %s", word, memoryAddress, dataValue));

                data.put(memoryAddress, dataValue);
            } else {
                Instruction inst = new Instruction(word);

                printAndWrite(fileWriter, createMipsCommandString(inst.sepStrings));
                printAndWrite(fileWriter, String.format(" %s\t", memoryAddress));

                if (inst.valid == 0) {
                    printAndWrite(fileWriter, " Invalid Instruction");
                }
                // nop will look like this: 10000000 00000000 00000000 00000000 which equals the min integer value
                else if (inst.asInt == Integer.MIN_VALUE){
                    printAndWrite(fileWriter, " NOP");
                    inst.opcodeType = Instruction.OpCode.NOP;
                }
                else if (inst.opcode == 40) {
                    printAndWrite(fileWriter, String.format(" ADDI\t R%s, R%s, #%s", inst.rt, inst.rs, inst.immd));
                    inst.opcodeType = Instruction.OpCode.ADDI;
                }
                else if (inst.opcode == 43) {
                    printAndWrite(fileWriter, String.format(" SW  \t R%s, %s(R%s)", inst.rt, inst.immd, inst.rs));
                    inst.opcodeType = Instruction.OpCode.SW;
                }
                else if (inst.opcode == 32 && inst.func == 0) {
                    // SLL Command
                    printAndWrite(fileWriter, String.format(" SLL\t R%s, R%s, #%s", inst.rd, inst.rt, inst.sa));
                    inst.opcodeType = Instruction.OpCode.SLL;
                    //SLL	R10, R1, #2
                }
                else if (inst.opcode == 32 && inst.func == 2){
                    printAndWrite(fileWriter, String.format(" SRL\t R%s, R%s, #%s", inst.rd, inst.rt, inst.sa));
                    inst.opcodeType = Instruction.OpCode.SRL;
                }
                else if (inst.opcode == 32 && inst.func == 34){
                    printAndWrite(fileWriter, String.format(" SUB \t R%s, R%s, R%s", inst.rd, inst.rs, inst.rt));
                    inst.opcodeType = Instruction.OpCode.SUB;
                }
                else if (inst.opcode == 32 && inst.func == 32){
                    printAndWrite(fileWriter, String.format(" ADD \t R%s, R%s, R%s", inst.rd, inst.rs, inst.rt));
                    inst.opcodeType = Instruction.OpCode.ADD;
                }
                else if (inst.opcode == 35) {
                    printAndWrite(fileWriter, String.format(" LW  \t R%s, %s(R%s)", inst.rt, inst.immd, inst.rs));
                    inst.opcodeType = Instruction.OpCode.LW;
                }
                else if (inst.opcode == 34) {
                    printAndWrite(fileWriter, String.format(" J  \t #%s", inst.j));
                    inst.opcodeType = Instruction.OpCode.J;
                }
                else if (inst.opcode == 33) {
                    printAndWrite(fileWriter, String.format(" BLTZ\t R%s, #%s", inst.rs, inst.immd));
                    inst.opcodeType = Instruction.OpCode.BLTZ;
                }
                else if (inst.opcode == 32 && inst.func == 8){
                    printAndWrite(fileWriter, String.format(" JR  \t R%s", inst.rs));
                    inst.opcodeType = Instruction.OpCode.JR;
                }
                else if (inst.opcode == 32 && inst.func == 13){
                    printAndWrite(fileWriter," BREAK");
                    inst.opcodeType = Instruction.OpCode.BREAK;
                    reachedBreak = true;
                }
                else if (inst.opcode == 60){
                    printAndWrite(fileWriter, String.format(" MUL \t R%s, R%s, R%s", inst.rd, inst.rs, inst.rt));
                    inst.opcodeType = Instruction.OpCode.MUL;
                }
                else if (inst.opcode == 32 && inst.func == 10){
                    printAndWrite(fileWriter, String.format(" MOVZ\t R%s, R%S, R%s", inst.rd, inst.rs, inst.rt));
                    inst.opcodeType = Instruction.OpCode.MOVZ;
                }

                instructions.add(inst);
            }

            System.out.println();
            writeToFile(fileWriter, "\n");

            memoryAddress += 4;
        }

        for (Instruction instruction: instructions){

        }

        try {
            fileWriter.close();
        } catch (IOException e){
            System.out.println("Error closing file");
            System.exit(-1);
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

    public static FileWriter getFileWriter(String filename){
        try {
            FileWriter writer = new FileWriter(filename);
            return writer;
        } catch (IOException exception){
            System.out.println("Could not create FileWriter for: " + filename);
            System.exit(-1);
        }

        return null;
    }

    public static void writeToFile(FileWriter writer, String writeString){
        try {
            writer.write(writeString);
        } catch (IOException exception){
            System.out.println("Could not write string: " + writeString);
            System.exit(-1);
        }
    }

    public static void printAndWrite(FileWriter writer, String writeString){
        System.out.print(writeString);
        writeToFile(writer, writeString);
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


    public static String createMipsCommandString(String[] mips) {
        String temp = "";
        for (int i = 0; i < 7; i++) {
            temp += (mips[i] + " ");
        }

        return temp;
    }
}


