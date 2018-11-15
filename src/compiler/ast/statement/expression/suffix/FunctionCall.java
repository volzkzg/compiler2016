package compiler.ast.statement.expression.suffix;

import compiler.ast.Node;
import compiler.ast.Symbol;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.declaration.VariableDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.statement.expression.binary.BinaryExpression;
import compiler.ast.type.IntType;
import compiler.ast.type.StringType;
import compiler.ast.type.Type;
import compiler.ast.type.VoidType;
import compiler.ir.*;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class FunctionCall extends Expression {
    public Symbol functionName;
    public List<Expression> args;

    public FunctionCall() {
        functionName = null;
        args = new LinkedList<>();
    }

    public FunctionCall(Symbol functionName, List<Expression> args) {
        this.functionName = functionName;
        this.args = args;
    }

    public void print(int d) {
        indent(d);
       System.out.println("FunctionCall : " + functionName.name);
        for (Expression p : args) {
            p.print(d + 1);
        }
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (functionName == null) {
            outputErrorInfomation(this);
            return false;
        }

        for (Expression p : args) {
            if (!p.third(current, functionState, forStack)) {
                outputErrorInfomation(this);
                return false;
            }
        }

        // 特判 内置函数

        if (current.find(functionName) == null) {
            String s = functionName.name;
            if (s.equals("print")) {
                if (args.size() != 1) {
                    outputErrorInfomation(this);
                    return false;
                }
                Expression exp = args.get(0);
                if (exp.expressionType instanceof StringType) {
                    expressionType = new VoidType();
                    isLvalue = false;
                } else {
                    outputErrorInfomation(this);
                    return false;
                }
                return true;
            } else if (s.equals("println")) {
                if (args.size() != 1) {
                    outputErrorInfomation(this);
                    return false;
                }
                Expression exp = args.get(0);
                if (exp.expressionType instanceof StringType) {
                    expressionType = new VoidType();
                    isLvalue = false;
                } else {
                    outputErrorInfomation(this);
                    return false;
                }
                return true;
//            } else if (s.equals("length")) {
//                if (args.size() != 1) {
//                    outputErrorInfomation(this);
//                    return false;
//                }
//                Expression exp = args.get(0);
//                if (exp.expressionType instanceof StringType) {
//                    expressionType = new VoidType();
//                    isLvalue = false;
//                } else {
//                    outputErrorInfomation(this);
//                    return false;
//                }
//                return true;
            } else if (s.equals("getString")) {
                if (args.size() != 0) {
                    outputErrorInfomation(this);
                    return false;
                }
                expressionType = new StringType();
                isLvalue = true;
                return true;
            } else if (s.equals("getInt")) {
                if (args.size() != 0) {
                    outputErrorInfomation(this);
                    return false;
                }
                expressionType = new IntType();
                isLvalue = false;
                return true;
            } else if (s.equals("toString")) {
                if (args.size() != 1) {
                    outputErrorInfomation(this);
                    return false;
                }
                Expression exp = args.get(0);
                if (exp.expressionType instanceof IntType) {
                    expressionType = new StringType();
                    isLvalue = true;
                } else {
                    outputErrorInfomation(this);
                    return false;
                }
                return true;
            } else {
                outputErrorInfomation(this);
                return false;
            }
        }
        //System.out.println(functionName.name);
        if (!(current.find(functionName) instanceof FunctionDeclaration)) {
            outputErrorInfomation(this);
            return false;
        }

        FunctionDeclaration func = (FunctionDeclaration) current.find(functionName);

        List<VariableDeclaration> lst1 = func.parameterList;
        List<Expression> lst2 = args;

        // 参数个数是否对等
        if (lst1.size() != lst2.size()) {
            outputErrorInfomation(this);
            return false;
        }

        // 每个参数对应的类型是否相等
        for (int i = 0; i < lst1.size(); ++i) {
            Type tp1 = lst1.get(i).variableType;
            Type tp2 = lst2.get(i).expressionType;
            if (!typeCompare(tp1, tp2)) {
                outputErrorInfomation(this);
                return false;
            }
        }

        expressionType = func.returnType;
        isLvalue = false;

        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        Call call = new Call();

        if (functionName.name.equals("println") && args.get(0).expressionType instanceof StringType) {
            Expression p = args.get(0);
            if (p instanceof BinaryExpression) {
                Expression a, b, c;
                a = ((BinaryExpression) p).leftHandSide;
                b = ((BinaryExpression) p).rightHandSide;
                function.body.add(new Call(null, null, a.getValue(current, functionState, forStack, function), "print"));
                function.body.add(new Call(null, null, b.getValue(current, functionState, forStack, function), "println"));
                return null;
            } else {
                for (Expression w : args) {
                    Address address = w.getValue(current, functionState, forStack, function);
                    if (address instanceof StackEntry) {
                        call.args.add(address);
                    } else {
                        call.args.add(address);
                    }
                }
            }
        } else {
            // 插入所有的 call 的参数
            for (Expression p : args) {
                Address address = p.getValue(current, functionState, forStack, function);

                if (address instanceof StackEntry) {
                    call.args.add(address);
                } else {
                    //StackEntry dest = new StackEntry();
                    //function.body.add(new Assign(dest, address));
                    //call.args.add(dest);
                    call.args.add(address);
                }
            }
        }
        function.body.add(call);

        // 内建函数
        if (functionName.name.equals("print")) {
            call.name = functionName.name;
            call.returnValue = null;
        } else if (functionName.name.equals("println")) {
            call.name = functionName.name;
            call.returnValue = null;
        } else if (functionName.name.equals("getString")) {
            call.name = functionName.name;
            call.returnValue = new StackEntry(true);
        } else if (functionName.name.equals("getInt")) {
            call.name = functionName.name;
            call.returnValue = new StackEntry(true);
        } else if (functionName.name.equals("toString")) {
            call.name = functionName.name;
            call.returnValue = new StackEntry(true);
        } else {
            FunctionDeclaration functionDeclaration = (FunctionDeclaration) current.find(functionName);
            if (functionDeclaration.returnType instanceof VoidType) {
                call.returnValue = null;
            } else {
                call.returnValue = new StackEntry(true);
            }
            call.before = StackEntry.getStackCount();
            call.callee = functionDeclaration.save;
            call.caller = functionState.save;
        }
        return call.returnValue;
    }
}
