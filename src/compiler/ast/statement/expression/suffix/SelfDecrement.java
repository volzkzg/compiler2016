package compiler.ast.statement.expression.suffix;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.IntType;
import compiler.ir.*;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class SelfDecrement extends Expression {
    public Expression expression;

    public SelfDecrement() {
        expression = null;
    }

    public SelfDecrement(Expression expression) {
        this.expression = expression;
    }

    public void print(int d) {
        indent(d);
        System.out.println("SelfDecrement");
        expression.print(d + 1);
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (!expression.third(current, functionState, forStack)) {
            outputErrorInfomation(this);
            return false;
        }

        if (!expression.isLvalue) {
            outputErrorInfomation(this);
            return false;
        } else {
            if (!(expression.expressionType instanceof IntType)) {
                outputErrorInfomation(this);
                return false;
            } else {
                expressionType = expression.expressionType;
                isLvalue = false;
            }
        }
        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        return getValue(current, functionState, forStack, function, false);
    }

    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack,
                            Function function, boolean isInForStep) {
        Address address = expression.getAddress(current, functionState, forStack, function);
        if (address instanceof StackEntry) {
            StackEntry ret = new StackEntry(true);
            function.body.add(new Assign(ret, address));
            function.body.add(new ArithmeticExpression(address, address, ArithmeticOp.SUB, new IntegerConst(1)));
            return ret;
        } else if (address instanceof MemoryAddress) {
            StackEntry ret = new StackEntry(true);
            StackEntry stackEntry = new StackEntry(true);
            MemoryRead memoryRead = new MemoryRead(stackEntry, (MemoryAddress) address);
            function.body.add(memoryRead);
            function.body.add(new Assign(ret, stackEntry));
            function.body.add(new ArithmeticExpression(stackEntry, stackEntry, ArithmeticOp.SUB, new IntegerConst(1)));
            function.body.add(new MemoryWrite(stackEntry, (MemoryAddress) address));
            return ret;
        }
        return null;
    }
}
