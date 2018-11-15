package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/4/30.
 */
public class RelationExpression extends Quadruple {
    public RelationOp op;
    public Address dest, src1, src2;

    public RelationExpression() {
        op = null;
        dest = null;
        src1 = null;
        src2 = null;
    }

    public RelationExpression(Address dest, Address src1, RelationOp op, Address src2) {
        this.dest = dest;
        this.src1 = src1;
        this.op = op;
        this.src2 = src2;
    }

    public RelationExpression(Address dest, RelationOp op, Address src1) {
        this.dest = dest;
        this.op = op;
        this.src1 = src1;
        this.src2 = null;
    }

    public String print() {
        String ret = dest.print() + " " + "=" + " ";
        switch (op) {
            case EQ:ret += "seq "; break;
            case NE:ret += "sne "; break;
            case LE:ret += "sle "; break;
            case LT:ret += "slt "; break;
            case GE:ret += "sge "; break;
            case GT:ret += "sgt "; break;
        }
        ret += src1.print() + " " + src2.print();
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "";

        int destNum = (((StackEntry) dest).num * 4);
        int src1Num = (((StackEntry) src1).num * 4);
        ret += "lw $t1, " + src1Num + "($sp)\n";

        switch (op) {
            case EQ: ret += "seq "; break;
            case NE: ret += "sne "; break;
            case LE: ret += "sle "; break;
            case LT: ret += "slt "; break;
            case GE: ret += "sge "; break;
            case GT: ret += "sgt "; break;
        }

        ret += "$t0" + " " + "$t1" + " ";
        if (src2 instanceof IntegerConst) {
            ret += ((IntegerConst) src2).value + "\n";
        } else if (src2 instanceof StackEntry) {
            ret += (((StackEntry) src2).num * 4) + "($sp)\n";
        }
        ret += "lw $t0, " + destNum + "($sp)\n";
        return ret;
    }

    @Override
    public void memoryAllocation(List<Quadruple> code) {
        Address to = dest, from1 = src1, from2 = src2;
        int localTime = code.size();

        if (from1 instanceof StackEntry) {
            from1 = Register.allocateRegister(code, (StackEntry) from1, true);
        }
        if (from2 instanceof StackEntry) {
            from2 = Register.allocateRegister(code, (StackEntry) from2, true);
        }
        if (to instanceof StackEntry) {
            to = Register.allocateRegister(code, (StackEntry) to, false);
        }

        code.add(new RelationExpression(to, from1, op, from2));
        if (to instanceof Register) {
            Register.change(((Register) to).num);
        }
    }

    @Override
    public String toMIPS() {
        String ret = "";
        switch (op) {
            case EQ:
                ret += "seq ";
                break;
            case NE:
                ret += "sne ";
                break;
            case LE:
                ret += "sle ";
                break;
            case LT:
                ret += "slt ";
                break;
            case GE:
                ret += "sge ";
                break;
            case GT:
                ret += "sgt ";
                break;
        }
        ret += dest.toMIPS() + ", " + src1.toMIPS() + ", " + src2.toMIPS() + "\n";
        return ret;
    }
}
