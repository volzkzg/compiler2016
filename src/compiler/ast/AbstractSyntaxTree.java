package compiler.ast;

/**
 * Created by bluesnap on 16/3/30.
 */

import compiler.ast.declaration.ClassDeclaration;
import compiler.ast.declaration.Declaration;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.declaration.GlobalVariableDeclaration;
import compiler.ir.IR;
import compiler.ir.StackEntry;
import compiler.ir.StringAddressConst;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class AbstractSyntaxTree extends Node {
    public List<Declaration> declarations;

    public AbstractSyntaxTree() {
        declarations = new LinkedList<>();
    }

    public AbstractSyntaxTree(List<Declaration> declarations) {
        this.declarations = declarations;
    }

    public void print(int d) {
        System.out.println("AbstractSyntaxTree");
        for (Declaration p : declarations) {
            p.print(d);
        }
    }

    public boolean first(SymbolTable current) {
        for (Node p : declarations) {
            if (!p.first(current)) return false;
        }
        return true;
    }

    @Override
    public boolean second(SymbolTable current) {
        for (Node p : declarations) {
            if (!p.second(current)) return false;
        }
        return true;
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        boolean check = false;
        for (Node p : declarations) {
            if (!p.third(current, functionState, forStack)) return false;
            if (p instanceof FunctionDeclaration) {
                if (((FunctionDeclaration) p).functionName.name.equals("main"))
                    check = true;
            }
        }
        if (!check) return false;
        return true;
    }

    public IR generateIR(SymbolTable table, FunctionDeclaration functionState, Stack<Node> forStack) {
        StringAddressConst.setCount();
        StringAddressConst.newline = new StringAddressConst();
        StringAddressConst.newline.value = "\n";

        IR ret = new IR();
        IR.init();
        StackEntry.setStackCount();

        for (Node p : declarations) {
            if (p instanceof FunctionDeclaration) {
                StackEntry.setStackCount();
                ret.parts.add(((FunctionDeclaration) p).generateIR(table, functionState, forStack));
            } else if (p instanceof ClassDeclaration) {

            } else if (p instanceof GlobalVariableDeclaration) {
                ((GlobalVariableDeclaration) p).generateIR(table, functionState, forStack);
            }
        }

        return ret;
        
    }
}
