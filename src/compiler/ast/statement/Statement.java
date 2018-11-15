package compiler.ast.statement;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ir.Function;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/30.
 */
public abstract class Statement extends Node {
    public void generateIR(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {

    }
}
