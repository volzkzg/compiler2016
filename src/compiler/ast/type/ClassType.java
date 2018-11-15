package compiler.ast.type;

import compiler.ast.Symbol;

/**
 * Created by bluesnap on 16/3/30.
 */
public class ClassType extends BasicType {
    public Symbol className;

    public ClassType() {

    }

    public ClassType(Symbol className) {
        this.className = className;
    }

    public void print(int d) {
        indent(d);
        System.out.println("ClassType");
    }
}
