package compiler.ast;

import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.type.ArrayType;
import compiler.ast.type.ClassType;
import compiler.ast.type.Type;
import compiler.ir.Address;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class Node {
    public Address reg;
    public int lineNumber;
    public boolean ret;

    public Node() {
        reg = null;
    }

    public void print(int d) {}

    public boolean first(SymbolTable current) {
        return true;
    }
    public boolean second(SymbolTable current) {
        return true;
    }
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        return true;
    }
    public void indent(int d) {
        for (int i = 0; i < d; ++i) {
            System.out.print(" ");
        }
    }
    public void outputErrorInfomation(Node treeNode) {
        System.out.println(treeNode.lineNumber + " is fucked");
    }

    public Type getBaseType(Type type) {
        if (type instanceof ArrayType) {
            return getBaseType(((ArrayType) type).baseType);
        } else {
            return type;
        }
    }


    public boolean typeCompare(Type tp1, Type tp2) {
        if (tp1 instanceof ClassType && tp2 instanceof ClassType) {
            if (!((ClassType) tp1).className.equals(((ClassType) tp2).className)) {
                return false;
            }
        }
        if (!tp1.getClass().equals(tp2.getClass()))
            return false;
        if (tp1 instanceof ArrayType) {
            return typeCompare(((ArrayType) tp1).baseType, ((ArrayType) tp2).baseType);
        } else {
            return true;
        }
    }


}
