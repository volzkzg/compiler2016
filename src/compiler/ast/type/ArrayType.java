package compiler.ast.type;

import compiler.ast.statement.expression.Expression;

/**
 * Created by bluesnap on 16/3/30.
 */
public class ArrayType extends BasicType {
    public Type baseType;
    public Expression dimension;

    public ArrayType() {
        baseType = null;
        dimension = null;
    }

    public ArrayType(Type arrayType, Expression dimension) {
        this.baseType = arrayType;
        this.dimension = dimension;
    }

    public void print(int d) {
        indent(d);
        System.out.println("ArrayType");
        if (baseType != null)
            baseType.print(d + 1);
        if (dimension != null)
            dimension.print(d + 1);
    }
}
