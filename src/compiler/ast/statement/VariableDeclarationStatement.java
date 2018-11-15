package compiler.ast.statement;

import compiler.ast.Node;
import compiler.ast.Symbol;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.ClassDeclaration;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.*;
import compiler.ir.Assign;
import compiler.ir.Function;
import compiler.ir.StackEntry;

import java.util.Stack;

/**
 * Created by bluesnap on 16/4/2.
 */
public class VariableDeclarationStatement extends Statement {
    public Type variableType;
    public Symbol variableName;
    public Expression expression;

    public VariableDeclarationStatement() {
        variableType = null;
        variableName = null;
        expression = null;
    }

    public VariableDeclarationStatement(Type type, Symbol name, Expression expression) {
        this.variableType = type;
        this.variableName = name;
        this.expression = expression;
    }

    public void print(int d) {
        indent(d);
        System.out.println("VariableDeclarationStatement");
        if (variableType != null)
            variableType.print(d + 1);
        if (expression != null)
            expression.print(d + 1);
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (variableType instanceof ClassType) {
            // 如果是自定义的类,去检查该类是否存在
            Node p = current.find(((ClassType) variableType).className);

            if (p == null || !(p instanceof ClassDeclaration)) {
                outputErrorInfomation(this);
                return false;
            }

        } else if (variableType instanceof ArrayType) {
            // 如果是数组类型,找基类是否存在
            Type baseType = getBaseType(variableType);
            if (baseType instanceof ClassType) {
                if (current.find(((ClassType) baseType).className) == null) {
                    outputErrorInfomation(this);
                    return false;
                }
            }
            int num = 0;
            Type tmp = variableType;
            while (tmp instanceof ArrayType) {
                if (((ArrayType) tmp).dimension != null)
                    num++;
                tmp = ((ArrayType) tmp).baseType;
            }
            if (num != 0) {
                outputErrorInfomation(this);
                return false;
            }
        }
        if (variableType instanceof VoidType) {
            outputErrorInfomation(this);
            return false;
        }
        this.ret = current.insert(variableName, this);
        if (!this.ret) {
            outputErrorInfomation(this);
            return false;
        }
        if (expression != null && !expression.third(current, functionState, forStack)) {
            outputErrorInfomation(this);
            return false;
        }
        if (expression != null && expression.expressionType instanceof NullType &&
                !(variableType instanceof ClassType ||
                variableType instanceof ArrayType)) {
            outputErrorInfomation(this);
            return false;
        }

        return true;
    }

    public void generateIR(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack,
                           Function function) {
        // 插入 Symbol Table
        current.insert(variableName, this);

        // 创建一个新的寄存器来保存当前这个变量
        StackEntry register = new StackEntry();
        this.reg = register;

        // 做初始化
        if (expression != null) {
            function.body.add(new Assign(register, expression.getValue(current, functionState, forStack, function)));
        }
    }
}
