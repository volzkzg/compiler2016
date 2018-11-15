package compiler.ast.type;

/**
 * Created by bluesnap on 16/4/5.
 */
public class NullType extends BasicType {
    public void print(int d) {
        indent(d);
        System.out.println("NullType");
    }
}
