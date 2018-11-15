package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class Label extends Quadruple {
    private static int labelCount = 0;
    public int num;
    public Label() {
        num = labelCount++;
    }

    public String print() {
        String ret = "%L" + ((Integer) num).toString();
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "L" + num + ":\n";
        return ret;
    }

    public void setLabelCount() {
        labelCount = 0;
    }

    @Override
    public void memoryAllocation(List<Quadruple> code) {
        code.add(this);
    }

    @Override
    public String toMIPS() {
        return "L" + num;
    }
}
