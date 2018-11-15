package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class Assign extends Quadruple {
    public Address dest, src;

    public Assign() {
        dest = null;
        src = null;
    }

    public Assign(Address dest, Address src) {
        this.dest = dest;
        this.src = src;
    }

    public String print() {
        String ret = dest.print() + " = move " + src.print();
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "";
        if (src instanceof StackEntry) {
            int num1, num2;
            num1 = ((StackEntry) dest).num * 4;
            num2 = ((StackEntry) src).num * 4;
            ret += "lw $t0, " + num2 + "($sp)\n";
            ret += "sw " + num1 + "($sp), " + "$t0\n";
        } else if (src instanceof IntegerConst) {
            int num1 = ((StackEntry) dest).num * 4;
            int num2 = ((IntegerConst) src).value;
            ret += "li $t0, " + num2 + "\n";
            ret += "sw " + num1 + "($sp), $t0\n";
        } else if (src instanceof StringAddressConst) {

        }
        return ret;
    }

    public void memoryAllocation(List<Quadruple> code) {
        Address to = dest, from = src;

        if (from instanceof StackEntry)
            from = Register.allocateRegister(code, (StackEntry) from, true);
        if (to instanceof StackEntry)
            to = Register.allocateRegister(code, (StackEntry) to, false);

        code.add(new Assign(to, from));
        if (to instanceof Register)
            Register.change(((Register) to).num);
    }

    @Override
    public String toMIPS() {
        String ret = "";
        if (src instanceof StringAddressConst) {
            ret += "la " + dest.toMIPS() + ", str" + src.toMIPS() + "\n";
        } else if (src instanceof IntegerConst) {
            ret += "li " + dest.toMIPS() + ", " + src.toMIPS() + "\n";
        } else if (src instanceof Register) {
            ret += "move " + dest.toMIPS() + ", " + src.toMIPS() + "\n";
        }
        return ret;
    }
}
