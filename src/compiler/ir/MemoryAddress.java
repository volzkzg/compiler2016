package compiler.ir;

/**
 * Created by bluesnap on 16/4/30.
 */
public class MemoryAddress extends Address {
    public Address start;
    public IntegerConst offset;

    public MemoryAddress() {
        start = null;
        offset = null;
    }

    public MemoryAddress(Address start, IntegerConst offset) {
        this.start = start;
        this.offset = offset;
    }

    public String print() {
        return "";
    }

    @Override
    public String toMIPS() {
        return null;
    }
}
