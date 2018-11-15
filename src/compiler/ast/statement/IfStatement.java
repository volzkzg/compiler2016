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
public class IfStatement extends Statement {
    public Expression condition;
    public Statement body, elseBody;

    public IfStatement() {
        condition = null;
        body = null;
        elseBody = null;
    }

    public IfStatement(Expression condition, Statement body, Statement elseBody) {
        this.condition = condition;
        this.body = body;
        this.elseBody = elseBody;
    }

    public void print(int d) {
        indent(d);
        System.out.println("IfStatement");
        if (condition != null)
            condition.print(d + 1);
        if (body != null)
            body.print(d + 1);
        if (elseBody != null)
            elseBody.print(d + 1);
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (condition == null) { // if condition can't be null
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
        if (body != null && !body.third(current, functionState, forStack))
            return false;
        current = current.prev;
        current = current.getNext();
        if (elseBody != null && !elseBody.third(current, functionState, forStack))
            return false;
        current = current.prev;
        return true;
    }

    @Override
    public void generateIR(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        Label label1, label2, label3;

        if (elseBody != null) {
            label1 = new Label();
            label2 = new Label();
            label3 = new Label();

            Address conditionReg = condition.getValue(current, functionState, forStack, function);
            Branch br = new Branch();
            br.src = conditionReg;
            br.label1 = label1;
            br.label2 = label2;
            function.body.add(br);

            function.body.add(label1);
            current = current.getNext();
            body.generateIR(current, functionState, forStack, function);
            function.body.add(new Goto(label3));
            current = current.prev;

            function.body.add(label2);
            current = current.getNext();
            elseBody.generateIR(current, functionState, forStack, function);
            function.body.add(new Goto(label3));
            current = current.prev;

            function.body.add(label3);
        } else {
            label1 = new Label();
            label2 = new Label();

            Address conditionReg = condition.getValue(current, functionState, forStack, function);
            Branch br = new Branch();
            br.src = conditionReg;
            br.label1 = label1;
            br.label2 = label2;
            function.body.add(br);

            function.body.add(label1);
            current = current.getNext();
            body.generateIR(current, functionState, forStack, function);
            function.body.add(new Goto(label2));
            current = current.prev;

            function.body.add(label2);
        }
        /*
        Expr
        Branch
        Label1
            then body goto label3
        Label2
            else body
        Label3
        */

        /*
        Expr
        Branch
        Label1
            then body
        Label2
         */
    }
}
