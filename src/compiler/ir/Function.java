package compiler.ir;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class Function {
    public String name;
    public int before;
    public List<Quadruple> body;
    public List<Address> args;
    public Label label;

    public List<Quadruple> code;

    public Function() {
        name = null;
        before = 0;
        body = new LinkedList<Quadruple>();
        args = new LinkedList<Address>();
        label = null;
    }

    public Function(String name, int size, List<Quadruple> body, List<Address> args) {
        this.name = name;
        this.before = size;
        this.body = body;
        this.args = args;
    }

    public String print() {
        String ret = "func" + " " + name;
        for (int i = 0; i < args.size(); ++i) {
            ret += " " + args.get(i).print();
        }

        ret += " " + "{\n" + "%" + name + ":\n";
        for (Quadruple p : body) {
            if (p instanceof Label) {
                ret += "\n" + p.print() + ":\n";
            } else {
                ret += "\t" + p.print() + "\n";
            }
        }
        ret += "}";
        return ret;
    }

    public String toCISC() {
        String ret = "";
        ret += label.toCISC();
        for (int i = 0; i < body.size(); ++i) {
            ret += body.get(i).toCISC();
        }
        return ret;
    }

    public void memoryAllocation() {
        Register.init();
        code = new LinkedList<>();

        int bnd = args.size();
        if (Register.paramRegNum < bnd) bnd = Register.paramRegNum;
        for (int i = 0; i < bnd; ++i) {
            Register.inReg[Register.tempRegNum + i] = (StackEntry) args.get(i);
            Register.time[Register.tempRegNum + i] = 0;
            Register.changed[Register.tempRegNum + i] = false;
        }

        for (int i = 0; i < body.size(); ++i) {
            body.get(i).memoryAllocation(code);
        }
        for (int i = 0; i < Register.tempRegNum; ++i) {
            Register.writeGlobalToMemory(code, i);
        }
    }

    public String toMIPS() {
        boolean isLeafFunction = true;
        for (int i = 0; i < code.size(); ++i)
            if (code.get(i) instanceof Call)
                isLeafFunction = false;
        String ret = "";
        ret += name + "_Entry:\n";
        if (!isLeafFunction)
            ret += "sw $ra, " + (before + 31) * 4 + "($sp)" + "\n";

        for (int i = 0; i < code.size(); ++i) {
            Quadruple p = code.get(i);
            // 删除不必要的 GOTO
            if (p instanceof Goto && (i + 1) < code.size() && code.get(i + 1) instanceof Label &&
                    ((Goto) p).label.num == ((Label) code.get(i + 1)).num)
                continue;

            // 对于 Branch 进行优化, 进行 Fall Through
            if (i + 1 < code.size()) {
                Quadruple q = code.get(i + 1);
                if (p instanceof Branch && q instanceof Label) {
                        if (((Branch) p).label1.num == ((Label) q).num) {
                            ret += "beqz " + ((Branch) p).src.toMIPS() + ", " + ((Branch) p).label2.toMIPS() + "\n";
                        } else {
                            ret += "bnez " + ((Branch) p).src.toMIPS() + ", " + ((Branch) p).label1.toMIPS() + "\n";
                            ret += "b " + ((Branch) p).label2.toMIPS() + "\n";
                        }
                } else {
                    ret += p.toMIPS();
                }
            } else {
                ret += p.toMIPS();
            }
            if (p instanceof Label)
                ret += ":\n";
        }

        ret += name + "_Exit:\n";
        if (!isLeafFunction)
            ret += "lw $ra, " + (before + 31) * 4 + "($sp)" + "\n";
        ret += "jr $ra\n";

        return ret;
    }
}
