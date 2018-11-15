package compiler.ast.statement;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ir.Function;
import compiler.ir.Goto;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class BreakStatement extends Statement {
    public void print(int d) {
        indent(d);
        System.out.println("BreakStatement");
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (forStack.empty()) {
            outputErrorInfomation(this);
            return false;
        }
        return true;
    }

    public void generateIR(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        Node top = forStack.peek();
        if (top instanceof ForLoopStatement) {
            function.body.add(new Goto((((ForLoopStatement) top).label4)));
        } else if (top instanceof WhileLoopStatement) {
            function.body.add(new Goto(((WhileLoopStatement) top).label3));
        }
    }
}
