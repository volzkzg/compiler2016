package compiler.ir;

import java.util.List;
/**
 * Created by bluesnap on 16/4/29.
 */
public class Branch extends Quadruple {
    public Address src;
    public Label label1, label2;

    public Branch() {
        src = null;
        label1 = null;
        label2 = null;
    }

    public Branch(Address src, Label label1, Label label2) {
        this.src = src;
        this.label1 = label1;
        this.label2 = label2;
    }

    public String print() {
        String ret = "br " + src.print() + " " + label1.print() + " " + label2.print();
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "";
        if (src instanceof IntegerConst) {
            ret += "li $t0, " + ((IntegerConst) src).value + "\n";
        } else if (src instanceof StackEntry) {
            ret += "lw $t0, " + (((StackEntry) src).num * 4) + "($sp)" + "\n";
        }
        ret += "beq $t0, 1, L" + label1.num + "\n";
        ret += "bnq $t0, 1, L" + label2.num + "\n";
        return ret;
    }

    @Override
    public void memoryAllocation(List<Quadruple> code) {
        Address from = src;

        if (from instanceof StackEntry) {
            from = Register.allocateRegister(code, (StackEntry) from, true);
        }

        Register.rollBack(code);

        code.add(new Branch(from, label1, label2));
    }

    @Override
    public String toMIPS() {
        // won't come
        String ret = "";
        //ret += "bnez " + src.toMIPS() + ", " + label1.toMIPS() + "\n";
        ret += "beqz " + src.toMIPS() + ", " + label2.toMIPS() + "\n";
        return ret;
    }
}
