package compiler.ast.statement.expression.suffix;

import compiler.ast.Node;
import compiler.ast.Symbol;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.ArrayType;
import compiler.ast.type.IntType;
import compiler.ast.type.StringType;
import compiler.ir.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by bluesnap on 16/4/4.
 */
public class EmbeddedFunctionCall extends Expression {
    public Expression expression;
    public Symbol functionName;
    public List<Expression> args;

    public EmbeddedFunctionCall() {
        functionName = null;
        args = new LinkedList<>();
    }

    public EmbeddedFunctionCall(Symbol functionName, List<Expression> args) {
        this.functionName = functionName;
        this.args = args;
    }

    public void print(int d) {
        indent(d);
        System.out.println("EmbeddedFunctionCall" + " " + functionName.name);
        for (Expression p : args) {
            p.print(d + 1);
        }
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (!expression.third(current, functionState, forStack)) {
            outputErrorInfomation(this);
            return false;
        }

        String s = functionName.name;
        if (s.equals("size")) {
            if (args.size() != 0) {
                outputErrorInfomation(this);
                return false;
            }
            if (!(expression.expressionType instanceof ArrayType)) {
                outputErrorInfomation(this);
                return false;
            }
            expressionType = new IntType();
            isLvalue = false;
        } else if (s.equals("length")) {
            if (args.size() != 0) {
                outputErrorInfomation(this);
                return false;
            }
            if (!(expression.expressionType instanceof StringType)) {
                outputErrorInfomation(this);
                return false;
            }
            expressionType = new IntType();
            isLvalue = false;
        } else if (s.equals("substring")) {
            if (args.size() != 2) {
                outputErrorInfomation(this);
                return false;
            }
            if (!(expression.expressionType instanceof StringType)) {
                outputErrorInfomation(this);
                return false;
            }
            Expression exp1, exp2;
            exp1 = args.get(0);
            exp2 = args.get(1);
            if (!exp1.third(current, functionState, forStack)) {
                outputErrorInfomation(this);
                return false;
            }
            if (!exp2.third(current, functionState, forStack)) {
                outputErrorInfomation(this);
                return false;
            }

            if (!(exp1.expressionType instanceof IntType) ||
                    !(exp2.expressionType instanceof  IntType)) {
                outputErrorInfomation(this);
                return false;
            }
            expressionType = new StringType();
            isLvalue = false;
        } else if (s.equals("parseInt")) {
            if (args.size() != 0) {
                outputErrorInfomation(this);
                return false;
            }
            if (!(expression.expressionType instanceof StringType)) {
                outputErrorInfomation(this);
                return false;
            }
            expressionType = new IntType();
            isLvalue = false;
        } else if (s.equals("ord")) {
            if (args.size() != 1) {
                outputErrorInfomation(this);
                return false;
            }
            if (!(expression.expressionType instanceof StringType)) {
                outputErrorInfomation(this);
                return false;
            }
            Expression exp;
            exp = args.get(0);
            if (!exp.third(current, functionState, forStack)) {
                outputErrorInfomation(this);
                return false;
            }
            if (!(exp.expressionType instanceof IntType)) {
                outputErrorInfomation(this);
                return false;
            }
            expressionType = new IntType();
            isLvalue = false;
        } else {
            outputErrorInfomation(this);
            return false;
        }
        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        Call ret = new Call();


        if (functionName.name.equals("size")) {
            ret.args.add(expression.getValue(current, functionState, forStack, function));
            ret.name = functionName.name;
            ret.returnValue = new StackEntry(true);
        } else if (functionName.name.equals("length")) {
            ret.args.add(expression.getValue(current, functionState, forStack, function));
            ret.name = functionName.name;
            ret.returnValue = new StackEntry(true);
        } else if (functionName.name.equals("substring")) {
            ret.args.add(expression.getValue(current, functionState, forStack, function));
            ret.name = functionName.name;
            ret.returnValue = new StackEntry(true);
        } else if (functionName.name.equals("parseInt")) {
            ret.args.add(expression.getValue(current, functionState, forStack, function));
            ret.name = functionName.name;
            ret.returnValue = new StackEntry(true);
        } else if (functionName.name.equals("ord")) {
            ret.args.add(expression.getValue(current, functionState, forStack, function));
            ret.name = functionName.name;
            ret.returnValue = new StackEntry(true);
        }

        for (Expression p : args) {
            Address address = p.getValue(current, functionState, forStack, function);
            if (address instanceof StackEntry) {
                ret.args.add(address);
            } else {
                //StackEntry dest = new StackEntry();
                //function.body.add(new Assign(dest, address));
                ret.args.add(address);
            }
        }
        function.body.add(ret); // 这句话必须放到最后
        return ret.returnValue;
    }
}
