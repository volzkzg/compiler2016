package compiler.ast;

/**
 * Created by bluesnap on 16/3/30.
 */
import compiler.ast.declaration.ClassDeclaration;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.declaration.GlobalVariableDeclaration;
import compiler.ast.declaration.VariableDeclaration;
import compiler.ast.statement.VariableDeclarationStatement;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.ClassType;
import compiler.ir.Address;
import compiler.ir.Function;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Stack;

public class Symbol extends Expression {
    public String name;
    //public int lineNumber;
    private static Dictionary<String, Symbol> dictionary = new Hashtable<>();
    private Symbol(String text) {
        name = text;
    }

    public static Symbol getSymbol(String text) {
        String unique = text.intern();
        Symbol s = dictionary.get(unique);
        if (s == null) {
            s = new Symbol(unique);
            dictionary.put(unique, s);
        }
        return s;
    }

    public void print(int d) {
        indent(d);
        System.out.println("Symbol : " + name);
    }
    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        Node res = current.find(this);
        if (res == null) {
            outputErrorInfomation(this);
            return false;
        }
        if (res instanceof ClassDeclaration) {
            expressionType = new ClassType(((ClassDeclaration) res).className);
            isLvalue = false;
        }
        if (res instanceof FunctionDeclaration) {
            expressionType = ((FunctionDeclaration) res).returnType;
            isLvalue = false;
        }
        if (res instanceof VariableDeclaration) {
            expressionType = ((VariableDeclaration) res).variableType;
            isLvalue = true;
        }
        if (res instanceof VariableDeclarationStatement) {
            expressionType = ((VariableDeclarationStatement) res).variableType;
            isLvalue = true;
        }
        if (res instanceof GlobalVariableDeclaration) {
            expressionType = ((GlobalVariableDeclaration) res).variableType;
            isLvalue = true;
        }
        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        return current.find(this).reg;
    }

    @Override
    public Address getAddress(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        return current.find(this).reg;
    }
}
