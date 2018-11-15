package compiler.ast.statement;


import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.type.BoolType;
import compiler.ir.*;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class WhileLoopStatement extends Statement {
    public Expression condition;
    public Statement body;
    public Label label1, label2, label3;
    public WhileLoopStatement() {
        condition = null;
        body = null;
    }

    public WhileLoopStatement(Expression condition, Statement body) {
        this.condition = condition;
        this.body = body;
    }

    public void print(int d) {
        indent(d);
        System.out.println("WhileLoopStatement");
        if (condition != null)
            condition.print(d + 1);
        if (body != null)
            body.print(d + 1);
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (condition == null) { // while condition can't be null
            outputErrorInfomation(this);
            return false;
        }
        if (condition != null && !condition.third(current, functionState, forStack))
            return false;
        if (!(condition.expressionType instanceof BoolType)) {
            outputErrorInfomation(this);
            return false;
        }
        current = current.getNext();
        forStack.push(this);
        if (body != null && !body.third(current, functionState, forStack))
            return false;
        forStack.pop();
        current = current.prev;
        return true;
    }

    @Override
    public void generateIR(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        label1 = new Label();
        label2 = new Label();
        label3 = new Label();

        function.body.add(new Goto(label1));
        function.body.add(label1);

        Address conditionReg = condition.getValue(current, functionState, forStack, function);
        Branch br = new Branch();
        br.src = conditionReg;
        br.label1 = label2;
        br.label2 = label3;
        function.body.add(br);

        function.body.add(label2);

        current = current.getNext();
        forStack.push(this);
        body.generateIR(current, functionState, forStack, function);
        function.body.add(new Goto(label1));
        forStack.pop();
        current = current.prev;

        function.body.add(label3);
        /*
        Label1
        Expr
        Branch
        	Label2
	        body
        Label3
         */
    }
}
