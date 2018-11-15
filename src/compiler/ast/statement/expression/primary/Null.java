package compiler.ast.statement.expression.primary;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.NullType;
import compiler.ir.Address;
import compiler.ir.Function;
import compiler.ir.IntegerConst;

import java.util.Stack;

/**
 * Created by bluesnap on 16/4/2.
 */
public class Null extends Expression {
    public void print(int d) {
        indent(d);
        System.out.println("Null");
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        isLvalue = true;
        expressionType = new NullType();
        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        return new IntegerConst(0);
    }
}
