package compiler.ast.type;

/**
 * Created by bluesnap on 16/3/30.
 */
public class VoidType extends BasicType {
    public void print(int d) {
        indent(d);
        System.out.println("VoidType");
    }
}
