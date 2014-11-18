package at.jku.ssw.cmm.interpreter.memory;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import at.jku.ssw.cmm.interpreter.exceptions.StackOverflowException;
import at.jku.ssw.cmm.interpreter.exceptions.StackUnderflowException;
import at.jku.ssw.cmm.interpreter.memory.MemoryInformation;

public final class Memory {
	private static final int MEMORY_SIZE = 8388608; // 8MB

	// 0: Return value
	// 4: start global space
	// ...
	// MEMORY_SIZE: top of stack

	private static final ByteBuffer memory = ByteBuffer.allocate(MEMORY_SIZE);
	{
		for (int i = 0; i < MEMORY_SIZE; i++) {
			memory.put(i, (byte) 0);
		}
	}
	
	private static final ArrayList<MemoryInformation> memoryInformations = new ArrayList(MEMORY_SIZE);
	
	private static int framePointer;
	private static int stackPointer;
	private static int globalPointer;

	private static int returnValue;
	private static float floatreturnValue;

	public static void initialize() {
		framePointer = MEMORY_SIZE / 2;
		stackPointer = MEMORY_SIZE / 2;
		globalPointer = 0;

		// initialize arrays
		for (int i = 0; i < MEMORY_SIZE; i++) {
			memory.put(i, (byte) 0);
			memoryInformations.add(i, new MemoryInformation());
		}
	}

	/*
	 * Call Stack Using, Line Number, Frame Size + Frame Pointer + ProcID
	 */
	public static void openStackFrame(int lineNumber, int procId, int frameSize)
			throws StackOverflowException {
		memory.putInt(stackPointer, lineNumber); // Line Nummer
		stackPointer += 4;
		memory.putInt(stackPointer, procId); // Methoden ID: name?
		stackPointer += 4;
		memory.putInt(stackPointer, framePointer); // Dynamic Link
		stackPointer += 4;

		framePointer = stackPointer;
		stackPointer += frameSize; // Variablen Size

	}

	@SuppressWarnings("unused")
	private static void assertStackOverflow() throws StackOverflowException {
		if (stackPointer >= MEMORY_SIZE)
			throw new StackOverflowException();

	}

	public static void closeStackFrame() throws StackUnderflowException {
		stackPointer = framePointer;
		stackPointer -= 4;
		framePointer = memory.getInt(stackPointer);
		stackPointer -= 8;
	}

	@SuppressWarnings("unused")
	private static void assertStackUnderflow() throws StackUnderflowException {
		if (framePointer <= 0)
			throw new StackUnderflowException();
	}

	public static boolean loadBool(int address) {
		if(memory.get(address) == 0)
			return false;
		else
			return true;
	}

	public static void storeBool(int address, boolean value) {
		memoryInformations.get(address).isInitialized = true;
		if(value)
			memory.put(address, (byte)0x01);
		else
			memory.put(address, (byte)0x00);
	}
	
	public static int loadInt(int address) {
		return memory.getInt(address);
	}

	public static void storeInt(int address, int value) {
		memoryInformations.get(address).isInitialized = true;
		memory.putInt(address, value);
	}

	public static char loadChar(int address) {
		return memory.getChar(address);
	}

	public static void storeChar(int address, char value) {
		memoryInformations.get(address).isInitialized = true;
		memory.putChar(address, value);
	}

	public static float loadFloat(int address) {
		return memory.getFloat(address);
	}

	public static void storeFloat(int address, float value) {
		memoryInformations.get(address).isInitialized = true;
		memory.putFloat(address, value);
	}

	public static void storeStringAdress(int address, int value) {
		memoryInformations.get(address).isInitialized = true;
		memory.putInt(address, value);
	}

	public static int loadStringAddress(int address) {
		return memory.getInt(address);
	}

	public static void setBoolReturnValue(boolean value) {
		if(value)
			returnValue = 1;
		else
			returnValue = 0;
	}
	
	public static void setIntReturnValue(int value) {
		returnValue = value;
	}

	public static void setCharReturnValue(char value) {
		returnValue = (int) value;
	}

	public static void setFloatReturnValue(float value) {
		floatreturnValue = value;
	}

	public static boolean getBoolReturnValue() {
		if(returnValue == 0)
			return false;
		else
			return true;
	}
	
	public static int getIntReturnValue() {
		return returnValue;
	}

	public static char getCharReturnValue() {
		return (char) returnValue;
	}

	public static float getFloatReturnValue() {
		return floatreturnValue;
	}

	// TODO
	/*
	 * private static void assertAddress(int offset) {
	 * 
	 * return false; }
	 */

	public static int getFramePointer() {
		return framePointer;
	}

	public static int getStackPointer() {
		return stackPointer;
	}

	public static int getGlobalPointer() {
		return globalPointer;
	}
}
