package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class Return extends Quadruple {
    public Address value;
    public String name;
    public Return() {
        value = null;
    }
    public Return(Address value, String name) {
        this.value = value;
        this.name = name;
    }

    public String print() {
        String ret = "ret " + value.print();
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "";
        if (value instanceof IntegerConst) {
            ret += "li $v0, " + ((IntegerConst) value).value + "\n";
        } else if (value instanceof StackEntry) {
            ret += "lw $v0, " + ((((StackEntry) value).num) * 4) + "($sp)\n";
        }
        ret += "jr $ra\n";
        return ret;
    }

    @Override
    public void memoryAllocation(List<Quadruple> code) {
        Address val = value;
        int localTime = code.size();

        if (val instanceof StackEntry) {
            val = Register.allocateRegister(code, (StackEntry) val, true);
        }
        for (int i = 0; i < Register.tempRegNum; ++i)
            Register.writeGlobalToMemory(code, i);
        if (val instanceof Register)
            Register.change(((Register) val).num);
        code.add(new Return(val, name));
    }

    @Override
    public String toMIPS() {
        String ret = "";
        if (value != null) {
            if (value instanceof IntegerConst) {
                ret += "li $v0, " + ((IntegerConst) value).value + "\n";
            } else if (value instanceof StringAddressConst) {
                ret += "la $v0, str" + ((StringAddressConst) value).toMIPS() + "\n";
            } else {
                ret += "move $v0, " + value.toMIPS() + "\n";
            }
        }
        ret += "b " + name + "_Exit\n";
        return ret;
    }
}
