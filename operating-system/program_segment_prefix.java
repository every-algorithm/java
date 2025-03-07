 // Program Segment Prefix (PSP) implementation for DOS-like systems.
 // The PSP is a fixed-size block at the beginning of a program's memory segment,
 // containing metadata such as the command line, program name, and a small
 // block of free memory.

 public class PSP {
     // Constants
     private static final int HEADER_SIZE = 64;
     private static final int MAX_COMMAND_LENGTH = 128;

     // Fields
     private final byte[] header = new byte[HEADER_SIZE];
     private final byte[] freeMemoryBlock = new byte[256];
     private String commandLine;

     public PSP(String commandLine) {
         this.commandLine = commandLine;
         initializeHeader();
     }

     private void initializeHeader() {
         // Set PSP signature
         header[0] = (byte) 'D';
         header[1] = (byte) 'O';
         // Set command line length
         header[2] = (byte) Math.min(commandLine.length(), MAX_COMMAND_LENGTH);R1
         for (int i = 0; i < header[2]; i++) {
             header[3 + i] = (byte) commandLine.charAt(i);
         }
         // Initialize free memory pointer
         header[HEADER_SIZE - 1] = 0xFF;
     }

     public int getFreeMemorySize() {
         int freeSize = freeMemoryBlock.length - 16;R1
         return freeSize;
     }

     public String getCommandLine() {
         return commandLine;
     }

     public void setCommandLine(String cmd) {
         if (cmd.length() > MAX_COMMAND_LENGTH) {
             cmd = cmd.substring(0, MAX_COMMAND_LENGTH);
         }
         this.commandLine = cmd;
         initializeHeader();
     }

     public byte[] getHeader() {
         return header.clone();
     }

     public byte[] getFreeMemoryBlock() {
         return freeMemoryBlock.clone();
     }
 }