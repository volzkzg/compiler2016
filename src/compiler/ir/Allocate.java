package compiler.ir;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bluesnap on 16/5/1.
 */
public class Allocate extends Quadruple {
    public Address dest, size;

    public boolean[] backUpParamReg = new boolean[4];

    public Allocate() {
        dest = null;
        size = null;
    }
    public Allocate(Address dest, Address size) {
        this.dest = dest;
        this.size = size;
    }

    public String print() {
        return dest.print() + " = alloc " + size.print();
    }

    @Override
    public String toCISC() {
        String ret = "";

        ret += "lw $a0, " + (((StackEntry) size).num * 4) + "($sp)\n";
        ret += "li $v0, 9\n";
        ret += "syscall\n";
        ret += "sw $v0, " + (((StackEntry) dest).num * 4) + "($sp)\n";

        return ret;
    }

    public void memoryAllocation(List<Quadruple> code) {
        LinkedList<Address> list = new LinkedList<>();
        list.add(size);
        Call call = new Call(dest, null, list, "new");
        call.memoryAllocation(code);
    }

    @Override
    public String toMIPS() {
        return "";
    }
}
