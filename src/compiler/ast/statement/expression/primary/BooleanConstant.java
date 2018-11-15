package compiler.ast.statement.expression.primary;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.BoolType;
import compiler.ir.Address;
import compiler.ir.Function;
import compiler.ir.IntegerConst;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class BooleanConstant extends Expression {
    public Boolean value;

    public BooleanConstant() {
        value = null;
    }

    public BooleanConstant(Boolean value) {
        this.value = value;
    }

    public void print(int d) {
        indent(d);
        System.out.println("BooleanConstant");
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        isLvalue = false;
        expressionType = new BoolType();
        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        IntegerConst ret = new IntegerConst();
        if (value == Boolean.TRUE) {
            ret.value = 1;
        } else {
            ret.value = 0;
        }
        return ret;
    }
}
