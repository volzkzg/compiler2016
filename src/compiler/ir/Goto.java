package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class Goto extends Quadruple {
    public Label label;

    public Goto() {
        label = null;
    }

    public Goto(Label label) {
        this.label = label;
    }

    public String print() {
        String ret = "jump " + label.print();
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "";
        ret += "b L" + label.num + "\n";
        return ret;
    }

    @Override
    public void memoryAllocation(List<Quadruple> code) {
        Register.rollBack(code);
        code.add(new Goto(label));
    }

    @Override
    public String toMIPS() {
        String ret = "";
        ret += "b " + label.toMIPS() + "\n";
        return ret;
    }
}
