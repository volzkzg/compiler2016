package compiler.ast.statement;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ir.Function;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class CompoundStatement extends Statement {
    public List<Statement> statements;

    public CompoundStatement() {
        statements = new LinkedList<>();
    }

    public CompoundStatement(List<Statement> statements) {
        this.statements = statements;
    }

    public void print(int d) {
        indent(d);
        System.out.println("CompoundStatement");
        for (Statement p : statements) {
            if (p != null)
                p.print(d + 1);
        }
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (functionState == null)
            current = current.getNext();
        for (Node p : statements) {
            if (p != null && !p.third(current, functionState, forStack))
                return false;
        }
        if (functionState == null)
            current = current.prev;
        return true;
    }

    @Override
    public void generateIR(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        if (functionState == null)
            current = current.getNext();

        for (Statement p : statements) {
            p.generateIR(current, functionState, forStack, function);
        }

        if (function == null)
            current = current.prev;
    }
}
