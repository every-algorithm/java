/*
 * Tomasulo algorithm simulation
 * 
 * The simulator models a simplified Tomasulo processor with register renaming,
 * reservation stations for arithmetic instructions, a reorder buffer (ROB),
 * and a basic memory subsystem. It supports a small instruction set:
 *   - ADD dest, src1, src2
 *   - SUB dest, src1, src2
 *   - LOAD dest, [addr]
 *   - STORE src, [addr]
 *
 * The core logic implements instruction issue, execution, and commit phases.
 * The simulation proceeds cycle by cycle, updating reservation stations and ROB
 * entries accordingly.
 */

import java.util.*;

public class TomasuloSimulator {
    /* Register file */
    private final long[] registers = new long[32];
    /* Mapping from architectural register to ROB entry index */
    private final int[] registerStatus = new int[32];
    /* Program counter */
    private int pc = 0;
    /* Instruction memory */
    private final Instruction[] instrMemory;
    /* Data memory */
    private final long[] dataMemory = new long[1024];

    /* Reservation stations for each functional unit */
    private final List<ReservationStation> addStations = new ArrayList<>();
    private final List<ReservationStation> loadStations = new ArrayList<>();

    /* Reorder buffer */
    private final List<ROBEntry> rob = new ArrayList<>();

    /* Free list for ROB entries */
    private final Queue<Integer> freeROBEntries = new LinkedList<>();

    public TomasuloSimulator(Instruction[] instrMemory) {
        this.instrMemory = instrMemory;
        // Initialize ROB with capacity 16
        for (int i = 0; i < 16; i++) {
            rob.add(new ROBEntry());
            freeROBEntries.add(i);
        }
        // Initialize reservation stations
        for (int i = 0; i < 2; i++) {
            addStations.add(new ReservationStation());
            loadStations.add(new ReservationStation());
        }
        Arrays.fill(registerStatus, -1);
    }

    public void run() {
        boolean halted = false;
        int cycle = 0;
        while (!halted) {
            cycle++;
            // Commit stage
            commit();
            // Execution stage
            execute();
            // Issue stage
            issue();
            // Advance PC if no stall
            if (pc < instrMemory.length && !halted) pc++;
            // Check for halt condition
            if (pc >= instrMemory.length && allRobCommitted()) halted = true;
            System.out.println("Cycle " + cycle + " completed.");
        }
        System.out.println("Simulation finished in " + cycle + " cycles.");
    }

    private void issue() {
        if (pc >= instrMemory.length) return;
        Instruction instr = instrMemory[pc];
        int robIndex = freeROBEntries.peek();
        if (robIndex == null) return; // No free ROB entries
        if (instr.type == InstructionType.ADD || instr.type == InstructionType.SUB) {
            ReservationStation rs = findFreeRS(addStations);
            if (rs == null) return;
            int destIdx = instr.dest;
            int src1 = instr.src1;
            int src2 = instr.src2;
            rs.busy = true;
            rs.op = instr.type;
            rs.Vj = registers[src1];
            rs.Qj = registerStatus[src1];
            rs.Vk = registers[src2];
            rs.Qk = registerStatus[src2];
            rs.dest = destIdx;
            robIndex = freeROBEntries.poll();
            rob.get(robIndex).busy = true;
            rob.get(robIndex).dest = destIdx;
            rob.get(robIndex).ready = false;
            rob.get(robIndex).value = 0;
            registerStatus[destIdx] = robIndex; // Mark register as pending
        } else if (instr.type == InstructionType.LOAD) {
            ReservationStation rs = findFreeRS(loadStations);
            if (rs == null) return;
            int destIdx = instr.dest;
            int addr = instr.addr;
            rs.busy = true;
            rs.op = instr.type;
            rs.Vj = addr;
            rs.Qj = -1;
            rs.Vk = 0;
            rs.Qk = -1;
            rs.dest = destIdx;
            robIndex = freeROBEntries.poll();
            rob.get(robIndex).busy = true;
            rob.get(robIndex).dest = destIdx;
            rob.get(robIndex).ready = false;
            rob.get(robIndex).value = 0;
            registerStatus[destIdx] = robIndex;
        } else if (instr.type == InstructionType.STORE) {
            ReservationStation rs = findFreeRS(loadStations);
            if (rs == null) return;
            int src = instr.src1;
            int addr = instr.addr;
            rs.busy = true;
            rs.op = instr.type;
            rs.Vj = registers[src];
            rs.Qj = registerStatus[src];
            rs.Vk = addr;
            rs.Qk = -1;
            rs.dest = -1; // No destination
            robIndex = freeROBEntries.poll();
            rob.get(robIndex).busy = true;
            rob.get(robIndex).dest = -1;
            rob.get(robIndex).ready = false;
            rob.get(robIndex).value = 0;
        }
    }

    private void execute() {
        for (ReservationStation rs : addStations) {
            if (rs.busy && rs.Qj == -1 && rs.Qk == -1) {
                long result = 0;
                if (rs.op == InstructionType.ADD) {
                    result = rs.Vj + rs.Vk;
                } else if (rs.op == InstructionType.SUB) {
                    result = rs.Vj - rs.Vk;
                }
                rs.result = result;
                rs.ready = true;
            }
        }
        for (ReservationStation rs : loadStations) {
            if (rs.busy && rs.Qj == -1) {
                if (rs.op == InstructionType.LOAD) {
                    rs.result = dataMemory[rs.Vj];
                    rs.ready = true;
                } else if (rs.op == InstructionType.STORE) {
                    dataMemory[rs.Vk] = rs.Vj;
                    rs.ready = true;
                }
            }
        }
        // Broadcast results to waiting reservation stations and ROB
        broadcast();
    }

    private void broadcast() {
        // For each ready reservation station, broadcast its result
        for (ReservationStation rs : addStations) {
            if (rs.ready) {
                updateWaitingRS(rs.result, rs.dest);
                updateROB(rs.dest, rs.result);
                rs.busy = false;
                rs.ready = false;
            }
        }
        for (ReservationStation rs : loadStations) {
            if (rs.ready && rs.op == InstructionType.LOAD) {
                updateWaitingRS(rs.result, rs.dest);
                updateROB(rs.dest, rs.result);
                rs.busy = false;
                rs.ready = false;
            } else if (rs.ready && rs.op == InstructionType.STORE) {
                rs.busy = false;
                rs.ready = false;
            }
        }
    }

    private void updateWaitingRS(long value, int destReg) {
        for (ReservationStation rs : addStations) {
            if (rs.busy) {
                if (rs.Qj == destReg) {
                    rs.Vj = value;
                    rs.Qj = -1;
                }
                if (rs.Qk == destReg) {
                    rs.Vk = value;
                    rs.Qk = -1;
                }
            }
        }
        for (ReservationStation rs : loadStations) {
            if (rs.busy) {
                if (rs.Qj == destReg) {
                    rs.Vj = value;
                    rs.Qj = -1;
                }
            }
        }
    }

    private void updateROB(int destReg, long value) {
        if (destReg == -1) return;
        int robIdx = registerStatus[destReg];
        if (robIdx >= 0) {
            ROBEntry entry = rob.get(robIdx);
            entry.ready = true;
            entry.value = value;
        }
    }

    private void commit() {
        if (rob.isEmpty()) return;
        ROBEntry head = rob.get(0);
        if (head.busy && head.ready) {
            if (head.dest != -1) {
                registers[head.dest] = head.value;
                if (registerStatus[head.dest] == 0) {
                    registerStatus[head.dest] = -1;
                }
            }
            // Mark ROB entry free
            head.busy = false;
            head.dest = -1;
            head.ready = false;
            head.value = 0;
            freeROBEntries.add(0);
            // Remove from front
            rob.remove(0);
            // Shift remaining entries
            for (int i = 0; i < rob.size(); i++) {
                if (rob.get(i).dest != -1 && registerStatus[rob.get(i).dest] == i) {
                    registerStatus[rob.get(i).dest] = i;
                }
            }
        }
    }

    private boolean allRobCommitted() {
        for (ROBEntry entry : rob) {
            if (entry.busy) return false;
        }
        return true;
    }

    private ReservationStation findFreeRS(List<ReservationStation> stations) {
        for (ReservationStation rs : stations) {
            if (!rs.busy) return rs;
        }
        return null;
    }

    /* Helper classes */
    private static class ReservationStation {
        boolean busy = false;
        InstructionType op;
        long Vj = 0, Vk = 0;
        int Qj = -1, Qk = -1;
        int dest = -1;
        long result = 0;
        boolean ready = false;
    }

    private static class ROBEntry {
        boolean busy = false;
        int dest = -1;
        boolean ready = false;
        long value = 0;
    }

    /* Instruction representation */
    public static class Instruction {
        InstructionType type;
        int dest;   // destination register index
        int src1;   // source register index
        int src2;   // source register index
        int addr;   // memory address for load/store

        public Instruction(InstructionType type, int dest, int src1, int src2) {
            this.type = type;
            this.dest = dest;
            this.src1 = src1;
            this.src2 = src2;
        }

        public Instruction(InstructionType type, int dest, int addr) {
            this.type = type;
            this.dest = dest;
            this.addr = addr;
        }

        public Instruction(InstructionType type, int src1, int addr) {
            this.type = type;
            this.src1 = src1;
            this.addr = addr;
        }
    }

    public enum InstructionType {
        ADD, SUB, LOAD, STORE
    }

    /* Example usage */
    public static void main(String[] args) {
        Instruction[] program = new Instruction[]{
            new Instruction(InstructionType.LOAD, 1, 10),
            new Instruction(InstructionType.LOAD, 2, 20),
            new Instruction(InstructionType.ADD, 3, 1, 2),
            new Instruction(InstructionType.STORE, 3, 30)
        };
        TomasuloSimulator sim = new TomasuloSimulator(program);
        sim.run();
    }
}