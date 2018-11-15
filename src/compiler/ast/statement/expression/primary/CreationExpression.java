package compiler.ast.statement.expression.primary;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.ClassDeclaration;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.ArrayType;
import compiler.ast.type.ClassType;
import compiler.ast.type.Type;
import compiler.ir.*;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class CreationExpression extends Expression {
    public Type name;

    public CreationExpression() {
        name = null;
    }

    public CreationExpression(Type name) {
        this.name = name;
    }

    public void print(int d) {
        indent(d);
        System.out.println("CreationExpression");
        name.print(d + 1);
    }

    // 记得处理数组 括号中 的东西 数字出现的位置

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        Type baseType = getBaseType(name);
        if (name instanceof ArrayType) {
            if (baseType instanceof ClassType) {
                if (current.find(((ClassType) baseType).className) == null) {
                    outputErrorInfomation(this);
                    return false;
                }
            }
            int num = 0;
            Type tmp = name;
            while (tmp instanceof ArrayType) {

                if (((ArrayType) tmp).dimension != null &&
                        !((ArrayType) tmp).dimension.third(current, functionState, forStack)) {
                    outputErrorInfomation(this);
                    return false;
                }

                if (((ArrayType) tmp).dimension != null) {
                   // System.out.println(((ArrayType) tmp).baseType.getClass().getTypeName());
                    if (!(((ArrayType) tmp).baseType instanceof ArrayType)) {
                        num += 10000;
                    } else {
                        num++;
                    }
                }
                tmp = ((ArrayType) tmp).baseType;
            }
            //System.out.println("GO through" + " " + num);
            if (num != 10000) {
                outputErrorInfomation(this);
                return false;
            }
        } else if (name instanceof ClassType) {
            if (current.find(((ClassType) name).className) == null) {
                outputErrorInfomation(this);
                return false;
            }
        }

        expressionType = name;
        isLvalue = false;
        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        if (name instanceof ArrayType) {
            // find expression
            Expression expression = null;
            Type type = name;
            while (expression == null) {
                expression = ((ArrayType) type).dimension;
                type = ((ArrayType) type).baseType;
            }

            // calculate expression to get a number
            Address size = expression.getValue(current, functionState, forStack, function);
            StackEntry dest = new StackEntry(true);

            if (size instanceof IntegerConst) {
                StackEntry length = new StackEntry(true);
                function.body.add(new Assign(length, new IntegerConst((((IntegerConst) size).value + 1) * 4)));
                function.body.add(new Allocate(dest, length));
                function.body.add(new Assign(length, size));
                function.body.add(new MemoryWrite(length, dest, new IntegerConst(0)));
                // 5.6 20:46 做修改的原因是因为我的大小应该记的是正确的大小,而不是+1乘4后的大小
            } else {
                StackEntry length = new StackEntry(true); // to let size not change
                function.body.add(new ArithmeticExpression(length, size, ArithmeticOp.ADD, new IntegerConst(1)));
                function.body.add(new ArithmeticExpression(length, length, ArithmeticOp.MUL, new IntegerConst(4)));
                function.body.add(new Allocate(dest, length));
                function.body.add(new MemoryWrite(size, dest, new IntegerConst(0)));
            }
            return dest;
        } else if (name instanceof ClassType) {
            IntegerConst classSize = ((ClassDeclaration)current.find(((ClassType) name).className)).getValue(current, functionState, forStack, function);
            IntegerConst size = new IntegerConst(classSize.value * 4);
            StackEntry length = new StackEntry(true);
            StackEntry dest = new StackEntry(true);
            function.body.add(new Assign(length, size));
            function.body.add(new Allocate(dest, length));
            return dest;
        }
        return null;
    }
}
