package compiler.ast.statement.expression.primary;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.IntType;
import compiler.ir.Address;
import compiler.ir.Function;
import compiler.ir.IntegerConst;

import java.math.BigInteger;
import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class IntegerConstant extends Expression {
    public BigInteger value;

    public IntegerConstant() {
        value = BigInteger.ZERO;
    }

    public IntegerConstant(BigInteger value) {
        this.value = value;
    }

    public void print(int d) {
        indent(d);
        System.out.println("IntegerConstant " + value);
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        isLvalue = false;
        expressionType = new IntType();
        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        IntegerConst ret = new IntegerConst(value.intValue());
        return ret;
    }
}

