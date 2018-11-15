package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class MemoryWrite extends Quadruple {
    public Address dest, src;
    public IntegerConst offset;
    public int size;

    public MemoryWrite() {
        dest = null;
        src = null;
        offset = null;
        size = 4;
    }

    public MemoryWrite(Address src, Address dest, IntegerConst offset) {
        this.dest = dest;
        this.offset = offset;
        this.src = src;
        this.size = 4;
    }

    public MemoryWrite(Address src, MemoryAddress memoryAddress) {
        this.src = src;
        this.offset = memoryAddress.offset;
        this.dest = memoryAddress.start;
        this.size = 4;
    }

    public String print() {
        String ret = "store" + " " + "4" + " " + dest.print() + " " + src.print() + " " + offset.print();
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "";
        ret += "lw $t1, " + (((StackEntry) src).num * 4) + "($sp)\n";
        ret += "sw $t1, " + (offset.value + ((StackEntry) dest).num * 4) + "($sp)\n";
        return ret;
    }

    @Override
    public void memoryAllocation(List<Quadruple> code) {
        Address to = dest, from = src;

        if (to instanceof StackEntry) {
            to = Register.allocateRegister(code, (StackEntry) to, true);
        }
        if (from instanceof StackEntry) {
            from = Register.allocateRegister(code, (StackEntry) from, true);
        }

        code.add(new MemoryWrite(from, to, offset));
    }

    @Override
    public String toMIPS() {
        String ret = "";
        ret += "sw " + src.toMIPS() + ", ";

        if (dest instanceof StackEntry) {
            if (((StackEntry) dest).name != null)
                ret = ret + "v" + ((StackEntry) dest).name + "\n";
            else
                ret = ret + ((StackEntry) dest).num * 4 + "($sp)" + "\n";
        } else {
            ret += offset.value + "(" + dest.toMIPS() + ")\n";
        }

        return ret;
    }
}
