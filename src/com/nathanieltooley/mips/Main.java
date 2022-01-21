package com.nathanieltooley.mips;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {

    public static void main(String[] args) {
        byte[] bytes = readBinaryFile(args[0]);

        String[] bytes32 = getBytesAs32Bits(bytes);

        for (String word : bytes32){
            String[] sep = splitMipsCommand(word);
            printMipsCommand(sep);
            System.out.println();
        }

//        printBytesAs32Bits(bytes);
    }

    public static byte[] readBinaryFile(String filename){
        try {
            byte[] bytes = Files.readAllBytes(Paths.get(filename));

            return bytes;
        } catch (IOException ex){
            System.out.println("Could not read file: " + filename);
            System.exit(-1);
        }

        return null;
    }

    public static String getByteAsBinaryString(byte b){
        return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
    }

    public static void printBytesAs32Bits(byte[] bytes){
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

    public static String[] getBytesAs32Bits(byte[] bytes){
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

    public static String[] splitMipsCommand(String byteString){
        String validInstruction = byteString.substring(0, 1);
        String opCode = byteString.substring(1, 6);
        String rs = byteString.substring(6, 11);
        String rt = byteString.substring(11, 16);
        String rd = byteString.substring(16, 21);
        String sa = byteString.substring(21, 26);
        String funct = byteString.substring(26, 32);

        return new String[] {validInstruction, opCode, rs, rt, rd, sa, funct};
    }

    public static void printMipsCommand(String[] mips){
        for (String section: mips){
            System.out.print(section + " ");
        }
    }

//    public static Word[] createWordByteArray(byte[] bytes) {
//        int j = 0;
//        Word[] words;
//        byte[] temp = new byte[4];
//        for (byte b : bytes) {
//            if (j < 4){
//                temp[j] = b;
//                j++;
//            } else {
//                Word w = new Word(temp);
//            }
//        }
//    }

//    public class Word {
//
//        public byte[] bytes;
//
//        public Word(byte[] bytes){
//            this.bytes = bytes;
//        }
//
//    }
}


