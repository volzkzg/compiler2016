package compiler.ast.declaration;

import compiler.ast.Node;
import compiler.ast.Symbol;
import compiler.ast.SymbolTable;
import compiler.ast.statement.CompoundStatement;
import compiler.ast.type.ClassType;
import compiler.ast.type.IntType;
import compiler.ast.type.Type;
import compiler.ir.Function;
import compiler.ir.Label;
import compiler.ir.StackEntry;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Created by bluesnap on 16/3/30.
 */
public class FunctionDeclaration extends Declaration {
    public Type returnType;
    public Symbol functionName;
    public List<VariableDeclaration> parameterList;
    public CompoundStatement functionBody;
    public Function save;

    public FunctionDeclaration() {
        returnType = null;
        functionName = null;
        parameterList = new LinkedList<>();
        functionBody = null;
        save = null;
    }

    public FunctionDeclaration(Type returnType, Symbol functionName,
                               List<VariableDeclaration> parameterList, CompoundStatement functionBody) {
        this.returnType = returnType;
        this.functionName = functionName;
        this.parameterList = parameterList;
        this.functionBody = functionBody;
    }

    public void print(int d) {
        indent(d);
        System.out.println("FunctionDeclaration");
        for (VariableDeclaration p : parameterList) {
            p.print(d + 1);
        }
        functionBody.print(d + 1);
    }

    @Override
    public boolean second(SymbolTable current) {
        if (functionName.name.equals("main")) {
            if (!(returnType instanceof IntType)) {
                outputErrorInfomation(this);
                return false;
            }
            if (parameterList.size() != 0) {
                outputErrorInfomation(this);
                return false;
            }
        }
        if (returnType instanceof ClassType) {
            if (current.find(((ClassType) returnType).className) == null) {
                outputErrorInfomation(this);
                return false;
            }
        }
        ret = current.insert(functionName, this);
        if (!ret) {
            outputErrorInfomation(this);
            return false;
        } else {
            current = new SymbolTable(current);
            for (Node p : parameterList) {
                if (!p.second(current)) return false;
            }
            current = current.prev;
        }
        return true;
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        current = current.getNext();
        for (Node p : parameterList) {
            p.second(current);
        }
        if (!functionBody.third(current, this, forStack))
            return false;
        current = current.prev;
        return true;
    }

    public Function generateIR(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        current = current.getNext();

        Function ret = new Function();
        save = ret;

        ret.name = functionName.name;

        // 将参数列表翻译成 IR
        for (Node p : parameterList) {
            ((VariableDeclaration) p).generateIR(current, this, forStack, ret);
        }

        // 将函数主体翻译成 IR
        ((CompoundStatement) functionBody).generateIR(current, this, forStack, ret);

        current = current.prev;

        ret.label = new Label();
        ret.before = StackEntry.getStackCount();
        //System.out.println(ret.before);
        return ret;
    }
}
