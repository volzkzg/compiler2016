package compiler.ast.type;

/**
 * Created by bluesnap on 16/3/30.
 */
public class StringType extends BasicType {
    public void print(int d) {
        indent(d);
        System.out.println("StringType");
    }
}
