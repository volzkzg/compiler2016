package compiler.ast.declaration;

import compiler.ast.Node;
import compiler.ast.Symbol;
import compiler.ast.SymbolTable;
import compiler.ast.statement.VariableDeclarationStatement;
import compiler.ir.Address;
import compiler.ir.Function;
import compiler.ir.IntegerConst;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by bluesnap on 16/3/30.
 */

public class ClassDeclaration extends Declaration {
    public Symbol className;
    public List<VariableDeclarationStatement> classFields;

    public ClassDeclaration() {
        className = null;
        classFields = new LinkedList<>();
    }

    public ClassDeclaration(Symbol className, List<VariableDeclarationStatement> classFields) {
        this.className = className;
        this.classFields = classFields;
    }

    public void print(int d) {
        indent(d);
        System.out.println("ClassDeclaration");
        for (VariableDeclarationStatement p : classFields) {
            p.print(d + 1);
        }
    }

    public boolean first(SymbolTable current) {
        ret = current.insert(className, this);
        if (ret == false) {
            outputErrorInfomation(this);
            return false;
        }
        return true;
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        current = current.getNext();
        for (Node p : classFields) {
            if (!p.third(current, functionState, forStack)) return false;
        }
        current = current.prev;
        return true;
    }

    public IntegerConst getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        return new IntegerConst(classFields.size());
    }
}
