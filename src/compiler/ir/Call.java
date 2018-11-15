package compiler.ir;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class Call extends Quadruple {
    public Address returnValue;
    public Function callee, caller; // 改成 function
    public LinkedList<Address> args;
    public int before;
    public String name;

    // 用来判断 a0-a3 中的元素是否需要写回内存备份
    public boolean[] backUpParamReg = new boolean[4];

    public Call() {
        before = 0;
        returnValue = null;
        callee = null;
        args = new LinkedList<>();
        name = null;
    }

    public Call(Address returnValue, Function callee, Address arg, String name) {
        this.returnValue = returnValue;
        this.callee = callee;
        this.args = new LinkedList<>();
        this.args.add(arg);
        this.name = name;
    }

    public Call(Address returnValue, Function callee, LinkedList<Address> args, String name) {
        this.returnValue = returnValue;
        this.callee = callee;
        this.args = args;
        this.name = name;
    }

    public String print() {
        String ret = "";

        for (int i = 0; i < args.size(); ++i) {
            ret += " " + args.get(i).print();
        }
        ret = "call" + " " + callee.name  + ret;
        if (returnValue != null) {
            ret = returnValue.print() + " = " + ret;
        }
        return ret;
    }

    @Override
    public String toCISC() {
        String ret = "";
        int delta = (callee.before + 1) * 4;

        // 调整 sp
        ret += "subu " + "$sp, $sp, " + delta + "\n";

        // 传参数
        for (int i = 0; i < args.size(); ++i) {
            // lw $t0, (num1 + delta)($sp)
            // sw num2($sp), $t0
            int num1 = ((StackEntry) args.get(i)).num * 4; // 保存当前调用的参数
            int num2 = ((StackEntry) callee.args.get(i)).num * 4; // 保存被调函数的参数
            ret += "lw $t0, " + (num1 + delta) + "($sp)\n";
            ret += "sw " + "$t0, " + num2 + "($sp)\n";
        }

        // 保存 ra
        // sw (caller.before + 1 + delta)*4($sp), $ra
        ret += "sw " + "$ra, " + ((caller.before + 1) * 4 + delta) + "($sp)\n";

        // jal
        ret += "jal L" + callee.label.num + "\n";

        // 存返回值
        if (returnValue != null)
            ret += "sw $v0, " + (((StackEntry) returnValue).num * 4 + delta) + "($sp)\n";

        // 调整回 ra
        ret += "lw " + "$ra, " + ((caller.before + 1) * 4 + delta) + "($sp)\n";

        // 调整回 sp
        ret += "addu " + "$sp, $sp, " + delta + "\n";

        return ret;
    }

    @Override
    public void memoryAllocation(List<Quadruple> code) {
        if (name == null) {
            // call 之前要把寄存器中的东西都写回内存
            for (int i = 0; i < Register.tempRegNum; ++i)
                Register.writeToMemory(code, i);

            Call call;
            Address to = returnValue;

            if (to instanceof StackEntry)
                to = Register.allocateRegister(code, (StackEntry) to, false);
            call = new Call(to, callee, args, name);

            for (int i = 0; i < Register.paramRegNum; ++i)
                call.backUpParamReg[i] = false;
            for (int i = 0; i < Register.paramRegNum; ++i) {
                if (Register.inReg[Register.tempRegNum + i] != null)
                    call.backUpParamReg[i] = true;
            }

            code.add(call);
            if (to instanceof Register)
                Register.change(((Register) to).num);
        } else { // embedded call
            Address to = returnValue;

            // 把所有的参数的 StackEntry 所包含的寄存器都写回内存
            for (int i= 0; i < args.size(); ++i) {
                if (args.get(i) instanceof StackEntry) {
                    Register.writeToMemory(code, (StackEntry) args.get(i));
                }
            }

            if (to instanceof StackEntry)
                to = Register.allocateRegister(code, (StackEntry) to, false);

            Call call = new Call(to, null, args, name);

            for (int i = 0; i < 4; ++i)
                call.backUpParamReg[i] = false;
            for (int i = 0; i < 4; ++i) {
                if (Register.inReg[Register.tempRegNum + i] != null)
                    call.backUpParamReg[i] = true;
            }

            code.add(call);
            if (to instanceof Register) {
                Register.change(((Register) to).num);
            }
        }
    }

    @Override
    public String toMIPS() {
        String ret = "";
        if (name == null) {
            int spChangeByte = callee.before + 32;

            for (int i = 0; i < 4; ++i)
                if (backUpParamReg[i])
                    ret += "sw $a" + i + ", " + i * 4 + "($sp)\n";

            ret += "sub $sp, $sp, " + spChangeByte * 4 + "\n";

            // 把 参数 设置好, 传参

            int k = args.size();
            if (k > 4) k = 4;
            for (int i = 0; i < k; ++i) {
                Address arg = args.get(i);
                if (arg instanceof IntegerConst) {
                    ret += "li $a" + i + ", " + arg.toMIPS() + "\n";
                } else if (arg instanceof StringAddressConst) {
                    ret += "la $a" + i + ", " + "str" + arg.toMIPS() + "\n";
                } else if (arg instanceof StackEntry) {
                    if (((StackEntry) arg).name == null) { // 不是全局变量
                        ret += "lw $a" + i + ", " + (spChangeByte + ((StackEntry) arg).num) * 4 + "($sp)\n";
                    } else { // 是全局变量
                        ret += "lw $a" + i + ", " + "v" + ((StackEntry) arg).name + "\n";
                    }
                }
            }

            for (int i = 4; i < args.size(); ++i) {
                Address arg = args.get(i);
                if (arg instanceof IntegerConst) {
                    ret += "li $t0, " + arg.toMIPS() + "\n";
                } else if (arg instanceof StringAddressConst) {
                    ret += "la $t0, str" + arg.toMIPS() + "\n";
                } else if (arg instanceof StackEntry) {
                    if (((StackEntry) arg).name == null) {
                        ret += "lw $t0, " + (spChangeByte + ((StackEntry) arg).num) * 4 + "($sp)\n";
                    } else {
                        ret += "lw $t0, v" + (((StackEntry) arg).name) + "\n";
                    }
                }
                ret += "sw $t0, " + i * 4 + "($sp)\n";
            }

            // 跳转过去
            ret += "jal " + callee.name + "_Entry\n";
            ret += "add $sp, $sp, " + spChangeByte * 4 + "\n";

            for (int i = 0; i < 4; ++i)
                if (backUpParamReg[i])
                    ret += "lw $a" + i + ", " + i * 4 + "($sp)\n";
            if (returnValue != null) {
                ret += "move " + returnValue.toMIPS() + ", $v0\n";
            }
        } else { // embedded functions
            int len = 32;
            for (int i = 0; i < 4; ++i)
                if (backUpParamReg[i])
                    ret += "sw " + "$a" + i + ", " + i * 4 + "($sp)" + "\n";

            ret +=  "subu $sp, $sp, " + len * 4 + "\n";
            for (int i = 0; i < args.size(); ++i) {
                if (args.get(i) instanceof IntegerConst) {
                    ret += "li " + "$a" + i + ", " + args.get(i).toMIPS() + "\n";
                } else {
                    if (((StackEntry) args.get(i)).name != null)
                            ret += "lw " + "$a" + i + ", " + "v" + ((StackEntry) args.get(i)).name + "\n";
                        else
                            ret += "lw " + "$a" + i + ", " + (((StackEntry) args.get(i)).num + len) * 4 + "($sp)" + "\n";
                }
            }
            ret += "jal " + name + "\n";
            ret += "addu $sp, $sp, " + len * 4 + "\n";

            for (int i = 0; i < 4; ++i)
                if (backUpParamReg[i])
                    ret += "lw" + "$a" + i + ", " + i * 4 + "($sp)" + "\n";

            if (returnValue != null)
                ret += "move " + returnValue.toMIPS() + ", $v0\n";

        }
        return ret;
    }
}
