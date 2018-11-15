package compiler.ir;

import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public abstract class Quadruple {
    abstract public String print();
    abstract public String toCISC();
    abstract public void memoryAllocation(List<Quadruple> code);
    abstract public String toMIPS();
}
