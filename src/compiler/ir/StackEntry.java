package compiler.ir;

/**
 * Created by bluesnap on 16/4/28.
 */
public class StackEntry extends Address {
    private static int stackCount = 0;
    public int num;
    public String name;
    public boolean redundancy;

    public StackEntry() {
        redundancy = false;
        name = null;
        num = stackCount++;
    }

    public StackEntry(boolean redundancy) {
        this.redundancy = redundancy;
        name = null;
        num = stackCount++;
    }

    public String print() {
        String ret = "$R" + ((Integer) num).toString();
        return ret;
    }

    public static int getStackCount() {
        return stackCount;
    }
    public static void setStackCount() {
        stackCount = 0;
    }

    public String toCISC() {
        String ret = "";
        ret += name + ":\n";
        ret += "\t.word 0\n";
        return ret;
    }

    @Override
    public String toMIPS() {
        return "v" + name + ":\n .word 0\n";
    }
}
