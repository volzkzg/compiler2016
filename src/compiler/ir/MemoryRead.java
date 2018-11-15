package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class MemoryRead extends Quadruple {
    public Address dest, src;
    public IntegerConst offset;
    public int size;

    public MemoryRead() {
        dest = null;
        src = null;
        offset = null;
        size = 4;
    }

    public MemoryRead(Address dest, Address src, IntegerConst offset) {
        this.dest = dest;
        this.offset = offset;
        this.src = src;
        this.size = 4;
    }

    public MemoryRead(Address dest, MemoryAddress memoryAddress) {
        this.dest = dest;
        this.offset = memoryAddress.offset;
        this.src = memoryAddress.start;
        this.size = 4;
    }

    public String print() {
        String ret = dest.print() + " = " + "load" + " " + "4" + " " + src.print() + " " + offset.print();
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "";

        ret += "lw $t1, " + (offset.value + ((StackEntry) src).num * 4) + "($sp)\n";
        ret += "sw $t1, " + (((StackEntry) dest).num * 4) + "($sp)\n";

        return ret;
    }

    @Override
    public void memoryAllocation(List<Quadruple> code) {
        Address to = dest, from = src;

        if (to instanceof StackEntry) {
            to = Register.allocateRegister(code, (StackEntry) to, false);
        }
        if (from instanceof StackEntry) {
            from = Register.allocateRegister(code, (StackEntry) from, true);
        }

        code.add(new MemoryRead(to, from, offset));
        if (to instanceof Register)
            Register.change(((Register) to).num);
    }

    @Override
    public String toMIPS() {
        String ret = "";

        ret += "lw " + dest.toMIPS() + ", ";

        if (src instanceof StackEntry) {
            if (((StackEntry) src).name != null) {
                ret += "v" + ((StackEntry) src).name + "\n";
            } else {
                ret += ((StackEntry) src).num * 4 + "($sp)\n";
            }
        } else {
            ret += offset.value + "(" + src.toMIPS() + ")\n";
        }

        return ret;
    }
}
