package compiler.ast.type;


/**
 * Created by bluesnap on 16/3/30.
 */
public class BoolType extends BasicType {
    public void print(int d) {
        indent(d);
        System.out.println("BoolType");
    }
}
