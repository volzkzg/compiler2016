package compiler.ir;

/**
 * Created by bluesnap on 16/4/28.
 */
public class StringAddressConst extends Const {
    public String value, noQuote;
    public int num, len;
    public static int count = 0;
    public static StringAddressConst newline;
    public StringAddressConst() {
        value = null;
        num = count++;
    }

    public StringAddressConst(String value) {
        this.value = value;
        len = 0;
        for (int i = 0; i < value.length(); ++i) {
            if (value.charAt(i) == '\\') {
                ++i;
            }
            len++;
        }
        len -= 2;
        num = count++;
    }


    public String print() {
        return value;
    }

    public static void setCount() {
        count = 0;
    }

    public String toCISC() {
        String ret = "";
        ret += "str" + count + ":\n" + "\t.word" + " " + value.getBytes().length + "\n";
        ret += "\talign 2\n";
        ret += "\tasciiz " + value + "\n";
        return ret;
    }

    @Override
    public String toMIPS() {
        return ((Integer) num).toString();
    }
}
