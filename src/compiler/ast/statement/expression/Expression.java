package compiler.ast.statement.expression;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.Statement;
import compiler.ast.type.Type;
import compiler.ir.Address;
import compiler.ir.Function;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/30.
 */
public class Expression extends Statement {
    public Type expressionType;
    public boolean isLvalue;

    public Address getAddress(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack,
                                            Function function) {
        return null;
    }

    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack,
                            Function function) {
        return null;
    }

    @Override
    public void generateIR(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        this.getValue(current, functionState, forStack, function);
    }
}
