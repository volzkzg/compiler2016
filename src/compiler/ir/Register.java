package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/5/5.
 */
public class Register extends Address {
    public int num;
    public final static int tempRegNum = 19;
    public final static int paramRegNum = 4;
    public static StackEntry[] inReg;
    public static int[] time;
    public static Boolean[] changed;

    public Register(int num) {
        this.num = num;
    }

    public static void init() {
        inReg = new StackEntry[tempRegNum + paramRegNum];
        time = new int[tempRegNum + paramRegNum];
        changed = new Boolean[tempRegNum + paramRegNum];
    }

    public static void writeGlobalToMemory(List<Quadruple> code, int pos) {
        // 判断寄存器中存的是一个全局变量
        if (inReg[pos] != null && inReg[pos].name != null) {
            if (changed[pos]) // 如果在寄存器中该元素被改变了, 才把他放回内存
                code.add(new MemoryWrite(new Register(pos), inReg[pos], new IntegerConst(0)));
            inReg[pos] = null;
            time[pos] = -1;
            changed[pos] = false;
        }
    }

    public static void writeToMemory(List<Quadruple> code, int pos) {
        if (inReg[pos] != null) {
            if (changed[pos])
                code.add(new MemoryWrite(new Register(pos), inReg[pos], new IntegerConst(0)));
            inReg[pos] = null;
            time[pos] = -1;
            changed[pos] = false;
        }
    }

    public static void writeToMemory(List<Quadruple> code, StackEntry stackEntry) {
        for (int i = 0; i < tempRegNum; ++i) {
            if (inReg[i] == null) continue;
            if (inReg[i].num == stackEntry.num) {
                writeToMemory(code, i);
            }
        }
    }

    public static void clean(StackEntry stackEntry) {
        for (int i = 0; i < tempRegNum; ++i) {
            if (inReg[i] == null) continue;
            if (stackEntry.num == inReg[i].num) {
                inReg[i] = null;
                time[i] = -1;
                changed[i] = false;
                return;
            }
        }
    }

    public static void rollBack(List<Quadruple> code) {
        for (int i = 0; i < Register.tempRegNum; ++i) {
            if (Register.inReg[i] != null){
                if (Register.inReg[i].redundancy) {
                    Register.clean(Register.inReg[i]);
                } else {
                    Register.writeToMemory(code, i);
                }
            }
        }
    }

    public static Register allocateRegister(List<Quadruple> code, StackEntry stackEntry, boolean readFromMemory) {
        int localTime = code.size();
        Register reg = find(stackEntry);
        if (reg != null) return reg;

        int pos = -1, val = 0x7FFFFFFF/2;
        for (int i = 0; i < tempRegNum; ++i) {
            if (inReg[i] != null) {
                if (time[i] < val && time[i] < localTime) { // use time locality
                    val = time[i];
                    pos = i;
                }
            } else if (inReg[i] == null) {
                inReg[i] = stackEntry;
                time[i] = code.size();
                changed[i] = false;
                if (readFromMemory) {
                    code.add(new MemoryRead(new Register(i), stackEntry, new IntegerConst(0)));
                }
                return new Register(i);
            }
        }

        writeToMemory(code, pos);
        if (readFromMemory) {
            code.add(new MemoryRead(new Register(pos), stackEntry, new IntegerConst(0)));
        }
        inReg[pos] = stackEntry;
        time[pos] = code.size();
        changed[pos] = false;
        return new Register(pos);
    }

    public static void change(int pos) {
        changed[pos] = true;
    }

    public static Register find(StackEntry stackEntry) {
        for (int i = 0; i < tempRegNum + paramRegNum; ++i) {
            if (inReg[i] == null) continue;
            if (stackEntry.num == inReg[i].num)
                return new Register(i);
        }
        return null;
    }

    public String print() {
        return "";
    }

    @Override
    public String toMIPS() {
        if (num < 10)
            return "$t" + num;
        else if (num < 18)
            return "$s" + (num - 10);
        else if (num < 19)
            return "$fp";
        else
            return "$a" + (num - 19);
    }
}
