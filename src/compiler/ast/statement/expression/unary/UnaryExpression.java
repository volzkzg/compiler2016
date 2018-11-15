package compiler.ast.statement.expression.unary;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.BoolType;
import compiler.ast.type.IntType;
import compiler.ast.type.Type;
import compiler.ir.*;

import java.util.NavigableMap;
import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class UnaryExpression extends Expression {
    public Expression expression;
    public UnaryOperator operator;

    public UnaryExpression() {
        expression = null;
        operator = null;
    }

    public UnaryExpression(Expression expression, UnaryOperator operator) {
        this.expression = expression;
        this.operator = operator;
    }

    public void print(int d) {
        indent(d);
        System.out.println("UnaryExpression");
        expression.print(d + 1);
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (!expression.third(current, functionState, forStack)) {
            outputErrorInfomation(this);
            return false;
        }

        Type tp = expression.expressionType;
        UnaryOperator op = operator;

        if (op == UnaryOperator.INCREMENT ||
                op == UnaryOperator.DECREMENT) {
            if (!expression.isLvalue) {
                outputErrorInfomation(this);
                return false;
            }
            if (tp instanceof IntType) {
                isLvalue = false;
                expressionType = tp;
            } else {
                outputErrorInfomation(this);
                return false;
            }
        }

        if (op == UnaryOperator.PLUS ||
                op == UnaryOperator.MINUS ||
                op == UnaryOperator.TILDE) {
            if (tp instanceof IntType) {
                isLvalue = false;
                expressionType = tp;
            } else {
                outputErrorInfomation(this);
                return false;
            }
        }

        if (op == UnaryOperator.NOT) {
            if (tp instanceof BoolType) {
                isLvalue = false;
                expressionType = tp;
            } else {
                outputErrorInfomation(this);
                return false;
            }
        }

        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        StackEntry dest = new StackEntry(true);
        if (operator == UnaryOperator.MINUS) {
            Address hs = expression.getValue(current, functionState, forStack, function);
            if (hs instanceof IntegerConst) {
                return new IntegerConst(-((IntegerConst) hs).value);
            } else {
                ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, ArithmeticOp.MINUS, hs);
                function.body.add(arithmeticExpression);
                return dest;
            }
        } else if (operator == UnaryOperator.NOT) {
            Address hs = expression.getValue(current, functionState, forStack, function);
            if (hs instanceof IntegerConst) {
                if (((IntegerConst) hs).value != 0) {
                    return new IntegerConst(0);
                } else {
                    return new IntegerConst(1);
                }
            } else {
                ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, hs, ArithmeticOp.XOR, new IntegerConst(1));
                function.body.add(arithmeticExpression);
                return dest;
            }
        } else if (operator == UnaryOperator.TILDE) {
            Address hs = expression.getValue(current, functionState, forStack, function);
            if (hs instanceof IntegerConst) {
                return new IntegerConst(~((IntegerConst) hs).value);
            } else {
                ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, ArithmeticOp.TILDE, hs);
                function.body.add(arithmeticExpression);
                return dest;
            }
        } else if (operator == UnaryOperator.PLUS) {
            return expression.getValue(current, functionState, forStack, function);
        } else if (operator == UnaryOperator.INCREMENT) {
            Address address = expression.getAddress(current, functionState, forStack, function);
            if (address instanceof StackEntry) {
                function.body.add(new ArithmeticExpression(address, address, ArithmeticOp.ADD, new IntegerConst(1)));
                return address;
            } else if (address instanceof MemoryAddress) {
                StackEntry stackEntry = new StackEntry(true);
                MemoryRead memoryRead = new MemoryRead(stackEntry, (MemoryAddress) address);
                ArithmeticExpression arithmeticExpression = new ArithmeticExpression(stackEntry, stackEntry, ArithmeticOp.ADD, new IntegerConst(1));
                MemoryWrite memoryWrite = new MemoryWrite(stackEntry, (MemoryAddress) address);
                function.body.add(memoryRead);
                function.body.add(arithmeticExpression);
                function.body.add(memoryWrite);
                return stackEntry;
            }
        } else if (operator == UnaryOperator.DECREMENT) {
            Address address = expression.getAddress(current, functionState, forStack, function);
            if (address instanceof StackEntry) {
                function.body.add(new ArithmeticExpression(address, address, ArithmeticOp.SUB, new IntegerConst(1)));
                return address;
            } else if (address instanceof MemoryAddress) {
                StackEntry stackEntry = new StackEntry(true);
                MemoryRead memoryRead = new MemoryRead(stackEntry, (MemoryAddress) address);
                ArithmeticExpression arithmeticExpression = new ArithmeticExpression(stackEntry, stackEntry, ArithmeticOp.SUB, new IntegerConst(1));
                MemoryWrite memoryWrite = new MemoryWrite(stackEntry, (MemoryAddress) address);
                function.body.add(memoryRead);
                function.body.add(arithmeticExpression);
                function.body.add(memoryWrite);
                return stackEntry;
            }
        }
        return dest;
    }
}
