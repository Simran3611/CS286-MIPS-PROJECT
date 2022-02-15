all:
	javac -d . Main.java Instruction.java Opcode.java
	jar cvmf manifest.mf mipssim.jar Main.class Main\$$1.class Instruction.class Opcode.class