package compiler.ast.statement.expression.suffix;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.ArrayType;
import compiler.ast.type.IntType;
import compiler.ast.type.Type;
import compiler.ir.*;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class ArrayAccessExpression extends Expression {
    public Expression expression;
    public Expression dimension;

    public ArrayAccessExpression() {
        expression = null;
        dimension = null;
    }

    public ArrayAccessExpression(Expression identifier, Expression dimension) {
        this.expression = identifier;
        this.dimension = dimension;
    }

    public void print(int d) {
        indent(d);
        System.out.println("ArrayAccessExpression");
        expression.print(d + 1);
        dimension.print(d + 1);
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (!expression.third(current, functionState, forStack)) {
            outputErrorInfomation(this);
            return false;
        }
        if (!dimension.third(current, functionState, forStack)) {
            outputErrorInfomation(this);
            return false;
        }

        Type tp1 = expression.expressionType;
        Type tp2 = dimension.expressionType;

        if (tp1 instanceof ArrayType) {
            if (tp2 instanceof IntType) {
                expressionType = ((ArrayType) tp1).baseType;
                isLvalue = true;
            } else {
                outputErrorInfomation(this);
                return false;
            }
        } else {
            outputErrorInfomation(this);
            return false;
        }

        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        MemoryAddress res = (MemoryAddress) this.getAddress(current, functionState, forStack, function);
        StackEntry tmp = new StackEntry(true);
        function.body.add(new ArithmeticExpression(tmp, res.start, ArithmeticOp.ADD, res.offset));
        function.body.add(new MemoryRead(tmp, tmp, new IntegerConst(0)));
        return tmp;
    }

    @Override
    public Address getAddress(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack,
                              Function function) {
        MemoryAddress memoryAddress = new MemoryAddress();
        Address exp = expression.getValue(current, functionState, forStack, function);
        Address dim = dimension.getValue(current, functionState, forStack, function);
        if (dim instanceof IntegerConst) {
            memoryAddress.start = exp;
            memoryAddress.offset = new IntegerConst(4 * (((IntegerConst) dim).value + 1));
            //System.out.println("JUST " + memoryAddress.offset.value);
            return memoryAddress;
        } else {
            StackEntry tmp = new StackEntry(true);
            //function.body.add(new ArithmeticExpression(tmp, dim, ArithmeticOp.ADD, new IntegerConst(1)));
            function.body.add(new ArithmeticExpression(tmp, dim, ArithmeticOp.MUL, new IntegerConst(4)));
            function.body.add(new ArithmeticExpression(tmp, exp, ArithmeticOp.ADD, tmp));
            memoryAddress.start = tmp;
            memoryAddress.offset = new IntegerConst(4);
            return memoryAddress;
        }
    }
}
