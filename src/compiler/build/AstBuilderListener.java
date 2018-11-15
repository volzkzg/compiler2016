package compiler.build;

import compiler.ast.AbstractSyntaxTree;
import compiler.ast.Node;
import compiler.ast.Symbol;
import compiler.ast.declaration.*;
import compiler.ast.statement.*;
import compiler.ast.statement.expression.Expression;
import compiler.ast.statement.expression.binary.BinaryExpression;
import compiler.ast.statement.expression.binary.BinaryOperator;
import compiler.ast.statement.expression.primary.*;
import compiler.ast.statement.expression.suffix.*;
import compiler.ast.statement.expression.unary.UnaryExpression;
import compiler.ast.statement.expression.unary.UnaryOperator;
import compiler.ast.type.*;
import compiler.parser.GrammarBaseListener;
import compiler.parser.GrammarParser;
import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.math.BigInteger;

/**
 * Created by bluesnap on 16/3/31.
 */
public class AstBuilderListener extends GrammarBaseListener {
    public GrammarParser parser;
    public static ParseTreeProperty<Node> property = new ParseTreeProperty<>();

    public AstBuilderListener(GrammarParser parser) {
        this.parser = parser;
    }

    @Override
    public void exitProgram(GrammarParser.ProgramContext ctx) {
        super.exitProgram(ctx);
        AbstractSyntaxTree treeNode = new AbstractSyntaxTree();
        property.put(ctx, treeNode);
        for (GrammarParser.DeclarationContext p : ctx.declaration()) {
            Node node = property.get(p);
            treeNode.declarations.add(((Declaration) node));
        }
        property.get(ctx).lineNumber = ctx.getStart().getLine();

    }

    @Override
    public void exitDeclaration(GrammarParser.DeclarationContext ctx) {
        super.exitDeclaration(ctx);
        if (ctx.classDeclaration() != null) {
            property.put(ctx, property.get(ctx.classDeclaration()));
        } else if (ctx.functionDeclaration() != null) {
            property.put(ctx, property.get(ctx.functionDeclaration()));
        } else if (ctx.globalVariableDeclaration() != null) {
            property.put(ctx, property.get(ctx.globalVariableDeclaration()));
        }
    }

    @Override
    public void exitClassDeclaration(GrammarParser.ClassDeclarationContext ctx) {
        super.exitClassDeclaration(ctx);
        ClassDeclaration treeNode = new ClassDeclaration();
        property.put(ctx, treeNode);

        treeNode.className = Symbol.getSymbol(ctx.Identifier().getText());
        for (GrammarParser.VariableDeclarationStatementContext p : ctx.variableDeclarationStatement()) {
            treeNode.classFields.add((VariableDeclarationStatement) (property.get(p)));
        }
        property.get(ctx).lineNumber = ctx.getStart().getLine();

    }

    @Override
    public void exitFunctionDeclaration(GrammarParser.FunctionDeclarationContext ctx) {
        super.exitFunctionDeclaration(ctx);
        FunctionDeclaration treeNode = new FunctionDeclaration();
        property.put(ctx, treeNode);

        treeNode.returnType = (Type) property.get(ctx.type());
        treeNode.functionName = Symbol.getSymbol(ctx.Identifier().getText());
        for (GrammarParser.VariableDeclarationContext p : ctx.variableDeclaration()) {
            Node node = property.get(p);
            treeNode.parameterList.add((VariableDeclaration) node);
        }
        //treeNode.parameterList =
        treeNode.functionBody = (CompoundStatement) property.get(ctx.compoundStatement());
        property.get(ctx).lineNumber = ctx.getStart().getLine();

    }

    @Override
    public void exitVariableDeclaration(GrammarParser.VariableDeclarationContext ctx) {
        super.exitVariableDeclaration(ctx);
        VariableDeclaration treeNode = new VariableDeclaration();
        property.put(ctx, treeNode);
        treeNode.variableType = (Type) property.get(ctx.type());
        treeNode.variableName = Symbol.getSymbol(ctx.Identifier().getText());
        if (ctx.expression() != null) {
            treeNode.expression = (Expression) property.get(ctx.expression());
        }
        property.get(ctx).lineNumber = ctx.getStart().getLine();

    }

    @Override
    public void exitGlobalVariableDeclaration(GrammarParser.GlobalVariableDeclarationContext ctx) {
        super.exitGlobalVariableDeclaration(ctx);
        GlobalVariableDeclaration treeNode = new GlobalVariableDeclaration();
        property.put(ctx, treeNode);
        treeNode.variableType = (Type) property.get(ctx.type());
        treeNode.variableName = Symbol.getSymbol(ctx.Identifier().getText());
        if (ctx.expression() != null) {
            treeNode.expression = (Expression) property.get(ctx.expression());
        }
        property.get(ctx).lineNumber = ctx.getStart().getLine();

    }

    @Override
    public void exitVariableDeclarationStatement(GrammarParser.VariableDeclarationStatementContext ctx) {
        super.exitVariableDeclarationStatement(ctx);
        VariableDeclarationStatement treeNode = new VariableDeclarationStatement();
        property.put(ctx, treeNode);
        treeNode.variableType = (Type) property.get(ctx.type());
        treeNode.variableName = Symbol.getSymbol(ctx.Identifier().getText());
        if (ctx.expression() != null) {
            treeNode.expression = (Expression) property.get(ctx.expression());
        }
        property.get(ctx).lineNumber = ctx.getStart().getLine();

    }

    @Override
    public void exitStatement(GrammarParser.StatementContext ctx) {
        super.exitStatement(ctx);
        if (ctx.compoundStatement() != null) {
            property.put(ctx, property.get(ctx.compoundStatement()));
        } else if (ctx.expressionStatement() != null) {
            property.put(ctx, property.get(ctx.expressionStatement()));
        } else if (ctx.selectionStatement() != null) {
            property.put(ctx, property.get(ctx.selectionStatement()));
        } else if (ctx.iterationStatement() != null) {
            property.put(ctx, property.get(ctx.iterationStatement()));
        } else if (ctx.jumpStatement() != null) {
            property.put(ctx, property.get(ctx.jumpStatement()));
        } else if (ctx.variableDeclarationStatement() != null) {
            property.put(ctx, property.get(ctx.variableDeclarationStatement()));
        }
    }

    @Override
    public void exitCompoundStatement(GrammarParser.CompoundStatementContext ctx) {
        super.exitCompoundStatement(ctx);
        CompoundStatement treeNode = new CompoundStatement();
        property.put(ctx, treeNode);

        for (GrammarParser.StatementContext p : ctx.statement()) {
            treeNode.statements.add((Statement) property.get(p));
        }
        property.get(ctx).lineNumber = ctx.getStart().getLine();

    }

    @Override
    public void exitExpressionStatement(GrammarParser.ExpressionStatementContext ctx) {
        super.exitExpressionStatement(ctx);
        if (ctx.expression() != null) {
            property.put(ctx, property.get(ctx.expression()));
        }
    }

    @Override
    public void exitSelectionStatement(GrammarParser.SelectionStatementContext ctx) {
        super.exitSelectionStatement(ctx);
        IfStatement treeNode = new IfStatement();
        property.put(ctx, treeNode);
        treeNode.condition = (Expression) property.get(ctx.expression());
        treeNode.body = (Statement) property.get(ctx.statement(0));

        if (ctx.Else() != null) {
            treeNode.elseBody = (Statement) property.get(ctx.statement(1));
        }
        property.get(ctx).lineNumber = ctx.getStart().getLine();

    }

    @Override
    public void exitWhileLoop(GrammarParser.WhileLoopContext ctx) {
        super.exitWhileLoop(ctx);
        WhileLoopStatement treeNode = new WhileLoopStatement();
        property.put(ctx, treeNode);
        treeNode.condition = (Expression) (property.get(ctx.expression()));
        treeNode.body = (Statement) (property.get(ctx.statement()));
        property.get(ctx).lineNumber = ctx.getStart().getLine();

    }

    @Override
    public void exitForLoop(GrammarParser.ForLoopContext ctx) {
        super.exitForLoop(ctx);
        ForLoopStatement treeNode = new ForLoopStatement();
        property.put(ctx, treeNode);

        if (ctx.init() != null)
            treeNode.initExpression = (Expression) (property.get(ctx.init()));
        if (ctx.condition() != null)
            treeNode.condition = (Expression) (property.get(ctx.condition()));
        if (ctx.step() != null)
            treeNode.step = (Expression) (property.get(ctx.step()));
        treeNode.body = (Statement) (property.get(ctx.statement()));
        property.get(ctx).lineNumber = ctx.getStart().getLine();
    }

    @Override
    public void exitInit(GrammarParser.InitContext ctx) {
        super.exitInit(ctx);
        property.put(ctx, property.get(ctx.expression()));
    }

    @Override
    public void exitCondition(GrammarParser.ConditionContext ctx) {
        super.exitCondition(ctx);
        property.put(ctx, property.get(ctx.expression()));
    }

    @Override
    public void exitStep(GrammarParser.StepContext ctx) {
        super.exitStep(ctx);
        property.put(ctx, property.get(ctx.expression()));
    }

    @Override
    public void exitBreakStatement(GrammarParser.BreakStatementContext ctx) {
        super.exitBreakStatement(ctx);
        BreakStatement treeNode = new BreakStatement();
        property.put(ctx, treeNode);
        property.get(ctx).lineNumber = ctx.getStart().getLine();
    }

    @Override
    public void exitContinueStatement(GrammarParser.ContinueStatementContext ctx) {
        super.exitContinueStatement(ctx);
        ContinueStatement treeNode = new ContinueStatement();
        property.put(ctx, treeNode);
        property.get(ctx).lineNumber = ctx.getStart().getLine();
    }

    @Override
    public void exitReturnStatement(GrammarParser.ReturnStatementContext ctx) {
        super.exitReturnStatement(ctx);
        ReturnStatement treeNode = new ReturnStatement();
        property.put(ctx, treeNode);
        treeNode.returnExpression = (Expression) (property.get(ctx.expression()));
        property.get(ctx).lineNumber = ctx.getStart().getLine();
    }

    @Override
    public void exitExpression(GrammarParser.ExpressionContext ctx) {
        super.exitExpression(ctx);
        property.put(ctx, property.get(ctx.assignmentExpression()));
    }

    @Override
    public void exitAssignmentExpression(GrammarParser.AssignmentExpressionContext ctx) {
        super.exitAssignmentExpression(ctx);

        if (ctx.unaryExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.operator = BinaryOperator.ASSIGN;
            treeNode.leftHandSide = (Expression) (property.get(ctx.unaryExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.assignmentExpression()));
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.logicalOrExpression()));
        }
    }

    @Override
    public void exitLogicalOrExpression(GrammarParser.LogicalOrExpressionContext ctx) {
        super.exitLogicalOrExpression(ctx);
        if (ctx.logicalOrExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.operator = BinaryOperator.LOGICAL_OR;
            treeNode.leftHandSide = (Expression) (property.get(ctx.logicalOrExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.logicalAndExpression()));
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.logicalAndExpression()));
        }
    }

    @Override
    public void exitLogicalAndExpression(GrammarParser.LogicalAndExpressionContext ctx) {
        super.exitLogicalAndExpression(ctx);
        if (ctx.logicalAndExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.operator = BinaryOperator.LOGICAL_AND;
            treeNode.leftHandSide = (Expression) (property.get(ctx.logicalAndExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.bitwiseInclusiveOrExpression()));
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.bitwiseInclusiveOrExpression()));
        }
    }

    @Override
    public void exitBitwiseInclusiveOrExpression(GrammarParser.BitwiseInclusiveOrExpressionContext ctx) {
        super.exitBitwiseInclusiveOrExpression(ctx);
        if (ctx.bitwiseInclusiveOrExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.operator = BinaryOperator.BITWISE_INCLUSIVE_OR;
            treeNode.leftHandSide = (Expression) (property.get(ctx.bitwiseInclusiveOrExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.bitwiseExclusiveOrExpression()));
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.bitwiseExclusiveOrExpression()));
        }
    }

    @Override
    public void exitBitwiseExclusiveOrExpression(GrammarParser.BitwiseExclusiveOrExpressionContext ctx) {
        super.exitBitwiseExclusiveOrExpression(ctx);
        if (ctx.bitwiseExclusiveOrExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.operator = BinaryOperator.BITWISE_EXCLUSIVE_OR;
            treeNode.leftHandSide = (Expression) (property.get(ctx.bitwiseExclusiveOrExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.bitwiseAndExpression()));
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.bitwiseAndExpression()));
        }
    }

    @Override
    public void exitBitwiseAndExpression(GrammarParser.BitwiseAndExpressionContext ctx) {
        super.exitBitwiseAndExpression(ctx);
        if (ctx.bitwiseAndExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.operator = BinaryOperator.BITWISE_AND;
            treeNode.leftHandSide = (Expression) (property.get(ctx.bitwiseAndExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.equalityExpression()));
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.equalityExpression()));
        }
    }

    @Override
    public void exitEqualityExpression(GrammarParser.EqualityExpressionContext ctx) {
        super.exitEqualityExpression(ctx);
        if (ctx.equalityExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.leftHandSide = (Expression) (property.get(ctx.equalityExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.relationExpression()));
            if (ctx.Equal() != null) {
                treeNode.operator = BinaryOperator.EQUAL;
            } else {
                treeNode.operator = BinaryOperator.NOT_EQUAL;
            }
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.relationExpression()));
        }
    }

    @Override
    public void exitRelationExpression(GrammarParser.RelationExpressionContext ctx) {
        super.exitRelationExpression(ctx);
        if (ctx.relationExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.leftHandSide = (Expression) (property.get(ctx.relationExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.shiftExpression()));
            if (ctx.Less() != null) {
                treeNode.operator = BinaryOperator.LESS;
            } else if (ctx.LessEqual() != null) {
                treeNode.operator = BinaryOperator.LEQ;
            } else if (ctx.Greater() != null) {
                treeNode.operator = BinaryOperator.GREAT;
            } else if (ctx.GreaterEqual() != null) {
                treeNode.operator = BinaryOperator.GEQ;
            }
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.shiftExpression()));
        }
    }

    @Override
    public void exitShiftExpression(GrammarParser.ShiftExpressionContext ctx) {
        super.exitShiftExpression(ctx);
        if (ctx.shiftExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.leftHandSide = (Expression) (property.get(ctx.shiftExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.additiveExpression()));
            if (ctx.LeftShift() != null) {
                treeNode.operator = BinaryOperator.LEFT_SHIFT;
            } else {
                treeNode.operator = BinaryOperator.RIGHT_SHIFT;
            }
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.additiveExpression()));
        }
    }

    @Override
    public void exitAdditiveExpression(GrammarParser.AdditiveExpressionContext ctx) {
        super.exitAdditiveExpression(ctx);
        if (ctx.additiveExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.leftHandSide = (Expression) (property.get(ctx.additiveExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.multiplicativeExpression()));
            if (ctx.Plus() != null) {
                treeNode.operator = BinaryOperator.ADD;
            } else if (ctx.Minus() != null) {
                treeNode.operator = BinaryOperator.SUB;
            }
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.multiplicativeExpression()));
        }
    }

    @Override
    public void exitMultiplicativeExpression(GrammarParser.MultiplicativeExpressionContext ctx) {
        super.enterMultiplicativeExpression(ctx);
        if (ctx.multiplicativeExpression() != null) {
            BinaryExpression treeNode = new BinaryExpression();
            property.put(ctx, treeNode);
            treeNode.leftHandSide = (Expression) (property.get(ctx.multiplicativeExpression()));
            treeNode.rightHandSide = (Expression) (property.get(ctx.unaryExpression()));
            if (ctx.Star() != null) {
                treeNode.operator = BinaryOperator.MUL;
            } else if (ctx.Div() != null) {
                treeNode.operator = BinaryOperator.DIV;
            } else if (ctx.Mod() != null) {
                treeNode.operator = BinaryOperator.MOD;
            }
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.unaryExpression()));
        }
    }

    @Override
    public void exitUnaryExpression(GrammarParser.UnaryExpressionContext ctx) {
        super.enterUnaryExpression(ctx);

        if (ctx.Plus() != null) {
            UnaryExpression treeNode = new UnaryExpression();
            property.put(ctx, treeNode);
            treeNode.expression = (Expression) (property.get(ctx.suffixExpression()));
            treeNode.operator = UnaryOperator.PLUS;
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.Minus() != null) {
            UnaryExpression treeNode = new UnaryExpression();
            property.put(ctx, treeNode);
            treeNode.expression = (Expression) (property.get(ctx.suffixExpression()));
            treeNode.operator = UnaryOperator.MINUS;
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.PlusPlus() != null) {
            UnaryExpression treeNode = new UnaryExpression();
            property.put(ctx, treeNode);
            treeNode.expression = (Expression) (property.get(ctx.suffixExpression()));
            treeNode.operator = UnaryOperator.INCREMENT;
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.MinusMinus() != null) {
            UnaryExpression treeNode = new UnaryExpression();
            property.put(ctx, treeNode);
            treeNode.expression = (Expression) (property.get(ctx.suffixExpression()));
            treeNode.operator = UnaryOperator.DECREMENT;
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.Not() != null) {
            UnaryExpression treeNode = new UnaryExpression();
            property.put(ctx, treeNode);
            treeNode.expression = (Expression) (property.get(ctx.suffixExpression()));
            treeNode.operator = UnaryOperator.NOT;
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.Tilde() != null) {
            UnaryExpression treeNode = new UnaryExpression();
            property.put(ctx, treeNode);
            treeNode.expression = (Expression) (property.get(ctx.suffixExpression()));
            treeNode.operator = UnaryOperator.TILDE;
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else {
            property.put(ctx, property.get(ctx.suffixExpression()));
        }


    }

    @Override
    public void exitSuffixExpression(GrammarParser.SuffixExpressionContext ctx) {
        super.exitSuffixExpression(ctx);
        if (ctx.primaryExpression() == null) {
            if (ctx.Dot() != null) {
                FieldAccessExpression treeNode = new FieldAccessExpression();
                property.put(ctx, treeNode);
                treeNode.object = (Expression) (property.get(ctx.suffixExpression()));
                treeNode.field = Symbol.getSymbol(ctx.Identifier().getText());
                property.get(ctx).lineNumber = ctx.getStart().getLine();
            } else if (ctx.LeftBracket() != null) {
                ArrayAccessExpression treeNode = new ArrayAccessExpression();
                property.put(ctx, treeNode);
                treeNode.expression = (Expression) (property.get(ctx.suffixExpression()));
                treeNode.dimension = (Expression) (property.get(ctx.expression(0)));
                property.get(ctx).lineNumber = ctx.getStart().getLine();
            } else if (ctx.LeftParen() != null) {

                if (property.get(ctx.suffixExpression()) instanceof Symbol) {
                    // Non-embedded function
                    FunctionCall treeNode = new FunctionCall();
                    property.put(ctx, treeNode);

                    Node q = property.get(ctx.suffixExpression());
                    treeNode.functionName = (Symbol) q;

                    for (GrammarParser.ExpressionContext p : ctx.expression()) {
                        treeNode.args.add(((Expression) property.get(p)));
                    }
                    property.get(ctx).lineNumber = ctx.getStart().getLine();
                } else {
                    // Embedded function
                    EmbeddedFunctionCall treeNode = new EmbeddedFunctionCall();
                    property.put(ctx, treeNode);
                    treeNode.expression = ((FieldAccessExpression) property.get(ctx.suffixExpression())).object;
                    treeNode.functionName = ((FieldAccessExpression) property.get(ctx.suffixExpression())).field;
                    for (GrammarParser.ExpressionContext p: ctx.expression()) {
                        treeNode.args.add(((Expression) property.get(p)));
                    }
                    property.get(ctx).lineNumber = ctx.getStart().getLine();
                }
            } else if (ctx.PlusPlus() != null) {
                SelfIncrement treeNode = new SelfIncrement();
                property.put(ctx, treeNode);
                treeNode.expression = (Expression) (property.get(ctx.suffixExpression()));
                property.get(ctx).lineNumber = ctx.getStart().getLine();
            } else if (ctx.MinusMinus() != null) {
                SelfDecrement treeNode = new SelfDecrement();
                property.put(ctx, treeNode);
                treeNode.expression = (Expression) (property.get(ctx.suffixExpression()));
                property.get(ctx).lineNumber = ctx.getStart().getLine();
            }
        } else {
            property.put(ctx, property.get(ctx.primaryExpression()));
        }


    }

    @Override
    public void exitPrimaryExpression(GrammarParser.PrimaryExpressionContext ctx) {
        super.exitPrimaryExpression(ctx);
        if (ctx.constant() != null) {
            property.put(ctx, property.get(ctx.constant()));
        } else if (ctx.Identifier() != null) {
            Symbol sym;
            sym = Symbol.getSymbol(ctx.Identifier().getText());
            property.put(ctx, sym);
            sym.lineNumber = ctx.getStart().getLine();
        } else if (ctx.LeftParen() != null) {
            property.put(ctx, property.get(ctx.expression()));
        } else if (ctx.New() != null) {
            CreationExpression treeNode = new CreationExpression();
            property.put(ctx, treeNode);
            treeNode.name = (Type) (property.get(ctx.type()));
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        }
    }

    @Override
    public void exitConstant(GrammarParser.ConstantContext ctx) {
        super.exitConstant(ctx);
        if (ctx.True() != null) {
            BooleanConstant treeNode = new BooleanConstant();
            treeNode.value = Boolean.TRUE;
            property.put(ctx, treeNode);
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.False() != null) {
            BooleanConstant treeNode = new BooleanConstant();
            treeNode.value = Boolean.FALSE;
            property.put(ctx, treeNode);
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.IntegerConstant() != null) {
            IntegerConstant treeNode = new IntegerConstant();
            treeNode.value = new BigInteger(ctx.getText());
            property.put(ctx, treeNode);
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.StringConstant() != null) {
            StringLiteral treeNode = new StringLiteral();
            treeNode.value = ctx.getText();
            property.put(ctx, treeNode);
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.Null() != null) {
            Null treeNode = new Null();
            property.put(ctx, treeNode);
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        }

    }

    @Override
    public void exitType(GrammarParser.TypeContext ctx) {
        super.exitType(ctx);
        if (ctx.Bool() != null) {
            BoolType treeNode = new BoolType();
            property.put(ctx, treeNode);
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.Int() != null) {
            IntType treeNode = new IntType();
            property.put(ctx, treeNode);
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.String() != null) {
            StringType treeNode = new StringType();
            property.put(ctx, treeNode);
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.Void() != null) {
            VoidType treeNode = new VoidType();
            property.put(ctx, treeNode);
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.Identifier() != null) {
            ClassType treeNode = new ClassType();
            property.put(ctx, treeNode);
            treeNode.className = Symbol.getSymbol(ctx.Identifier().getText());
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        } else if (ctx.type() != null) {
            ArrayType treeNode = new ArrayType();
            property.put(ctx, treeNode);
            treeNode.baseType = (Type) (property.get(ctx.type()));
            treeNode.dimension = (Expression) (property.get(ctx.expression()));
            property.get(ctx).lineNumber = ctx.getStart().getLine();
        }
    }
}
