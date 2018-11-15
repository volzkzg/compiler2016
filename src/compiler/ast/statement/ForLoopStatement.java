package compiler.ast.statement;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.statement.expression.suffix.SelfDecrement;
import compiler.ast.statement.expression.suffix.SelfIncrement;
import compiler.ast.type.BoolType;
import compiler.ir.*;

import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class ForLoopStatement extends Statement {
    public Expression initExpression;
    public Expression step, condition;
    public Statement body;
    public Label label1, label2, label3, label4;

    public ForLoopStatement() {
        initExpression = null;
        step = null;
        condition = null;
        body = null;
    }

    public ForLoopStatement(Expression initExpression, Expression step,
                            Expression condition, Statement body) {
        this.initExpression = initExpression;
        this.step = step;
        this.condition = condition;
        this.body = body;
    }

    public void print(int d) {
        indent(d);
        System.out.println("ForLoopStatement");
        if (initExpression != null)
            initExpression.print(d + 1);
        if (condition != null)
            condition.print(d + 1);
        if (step != null)
            step.print(d + 1);
        if (body != null)
            body.print(d + 1);
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        forStack.push(this);
        if (initExpression != null && !initExpression.third(current, functionState, forStack))
            return false;
        if (condition != null && !condition.third(current, functionState, forStack))
            return false;
        if (condition != null && !(condition.expressionType instanceof BoolType)) {
            outputErrorInfomation(this);
            return false;
        }
        if (step != null && !step.third(current, functionState, forStack))
            return false;
        current = current.getNext();
        if (body != null && !body.third(current, functionState, forStack))
            return false;
        current = current.prev;
        forStack.pop();
        return true;
    }

    @Override
    public void generateIR(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        label1 = new Label();
        label2 = new Label();
        label3 = new Label();
        label4 = new Label();

        forStack.push(this);
        // Expr1
        if (initExpression != null) {
            initExpression.getValue(current, functionState, forStack, function);
        }
        // Goto label1
        function.body.add(new Goto(label1));
        // label1
        function.body.add(label1);

        if (condition != null) {
            Address conditionReg = condition.getValue(current, functionState, forStack, function);
            Branch br = new Branch();
            br.label1 = label2;
            br.label2 = label4;
            br.src = conditionReg;
            function.body.add(br);
        } else {
            Goto gt = new Goto();
            gt.label = label2;
            function.body.add(gt);
        }
        // label2
        function.body.add(label2);

        current = current.getNext();
        body.generateIR(current, functionState, forStack, function);
        function.body.add(new Goto(label3));
        function.body.add(label3);
        if (step != null) {
            if (step instanceof SelfDecrement)
                ((SelfDecrement) step).getValue(current.prev, functionState, forStack, function, true);
            else if (step instanceof SelfIncrement)
                ((SelfIncrement) step).getValue(current.prev, functionState, forStack, function, true);
            else
                step.getValue(current.prev, functionState, forStack, function);
        }
        function.body.add(new Goto(label1));
        function.body.add(label4);
        current = current.prev;

        forStack.pop();

        /*
        Expr1
        Label1
        Expr2
        Branch
        Label2
        Body
        Label3
        Expr3 goto label1
        Label 4
         */
    }
}
