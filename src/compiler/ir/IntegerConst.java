package compiler.ir;

/**
 * Created by bluesnap on 16/4/28.
 */
public class IntegerConst extends Const {
    public int value;
    public IntegerConst() {}
    public IntegerConst(int value) {
        this.value = value;
    }
    public String print() {
        String ret = ((Integer) value).toString();
        return ret;
    }

    @Override
    public String toMIPS() {
        return ((Integer) value).toString();
    }
}
