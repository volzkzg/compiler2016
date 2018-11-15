package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class ArithmeticExpression extends Quadruple {
    public ArithmeticOp op;
    public Address dest, src1, src2; // src1 must be StackEntry or src2 may be IntegerConst

    public ArithmeticExpression() {
        op = null;
        dest = null;
        src1 = null;
        src2 = null;
    }

    public ArithmeticExpression(Address dest, Address src1, ArithmeticOp op, Address src2) {
        this.dest = dest;
        this.src1 = src1;
        this.op = op;
        this.src2 = src2;
    }

    public ArithmeticExpression(Address dest, ArithmeticOp op, Address src1) {
        this.dest = dest;
        this.op = op;
        this.src1 = src1;
        this.src2 = null;
    }

    public String print() {
        String ret = "";
        ret += dest.print() + " = ";
        switch (op) {
            case ADD:ret += "add"; break;
            case SUB:ret += "sub"; break;
            case MUL:ret += "mul"; break;
            case DIV:ret += "div"; break;
            case MOD:ret += "rem"; break;
            case SHL:ret += "shl"; break;
            case SHR:ret += "shr"; break;
            case XOR:ret += "xor"; break;
            case OR:ret += "or"; break;
            case AND:ret += "and"; break;
            case MINUS:ret += "neg"; break;
            case TILDE:ret += "not"; break;
        }
        ret += " " + src1.print();
        if (src2 != null) {
            ret += " " + src2.print();
        }
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "";
        int num0, num1, num2;
        num1 = ((StackEntry) src1).num * 4;
        num0 = ((StackEntry) dest).num * 4;
        ret += "lw $t1," + " " + num1 + "($sp)\n";
        ret += "lw $t0," + " " + num0 + "($sp)\n";
        if (src2 != null) {
            if (src2 instanceof StackEntry) {
                num2 = (((StackEntry) src2).num) * 4;
                ret += "lw $t2," + " " + num2 + "($sp)\n";
            }
            switch (op) {
                case ADD:
                    ret += "add";
                    break;
                case SUB:
                    ret += "sub";
                    break;
                case MUL:
                    ret += "mul";
                    break;
                case DIV:
                    ret += "div";
                    break;
                case MOD:
                    ret += "rem";
                    break;
                case SHL:
                    ret += "srl";
                    break;
                case SHR:
                    ret += "shr";
                    break;
                case AND:
                    ret += "and";
                    break;
                case OR:
                    ret += "or";
                    break;
                case XOR:
                    ret += "xor";
                    break;
            }
            if (src2 instanceof StackEntry)
                ret += " $t0, $t1, $t2\n";
            else if (src2 instanceof IntegerConst) {
                ret += " $t0, $t1, " + ((IntegerConst) src2).value + "\n";
            }
            ret += "sw $t0, " + num0 + "($sp)\n";
        } else {
            switch (op) {
                case MINUS:
                    ret += "neg";
                    break;
                case TILDE:
                    ret += "not";
                    break;
            }
            ret += " $t0 $t1\n";
            ret += "sw $t0, " + num0 + "($sp)\n";
        }
        return ret;
    }

    public void memoryAllocation(List<Quadruple> code) {
        Address to = dest, from1 = src1, from2 = src2;

        if (from1 instanceof StackEntry)
            from1 = Register.allocateRegister(code, (StackEntry) from1, true);
        if (from2 != null && src2 instanceof StackEntry)
            from2 = Register.allocateRegister(code, (StackEntry) from2, true);
        if (to instanceof StackEntry)
            to = Register.allocateRegister(code, (StackEntry) to, false);

        if (from2 != null)
            code.add(new ArithmeticExpression(to, from1, op, from2));
        else
            code.add(new ArithmeticExpression(to, op, from1));
        if (to instanceof Register) {
            Register.change(((Register) to).num);
        }
    }

    @Override
    public String toMIPS() {
        String ret = "";
        switch (op) {
            case ADD: ret += "add"; break;
            case SUB: ret += "sub"; break;
            case MUL: ret += "mul"; break;
            case DIV: ret += "div"; break;
            case MOD: ret += "rem"; break;
            case SHL: ret += "srl"; break;
            case SHR: ret += "shr"; break;
            case AND: ret += "and"; break;
            case OR: ret += "or"; break;
            case XOR: ret += "xor"; break;
        }
        if (src2 == null)
            ret = ret + " " + dest.toMIPS() + ", " + src1.toMIPS() + "\n";
        else {
            ret = ret + " " + dest.toMIPS() + ", " + src1.toMIPS() + ", " + src2.toMIPS() + "\n";
        }
        return ret;
    }
}
