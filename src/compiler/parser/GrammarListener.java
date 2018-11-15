// Generated from /Users/bluesnap/Documents/Project/IntelliJ Idea Project/Mx/src/compiler/parser/Grammar.g4 by ANTLR 4.5.1
package compiler.parser;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link GrammarParser}.
 */
public interface GrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link GrammarParser#program}.
	 * @param ctx the parse tree
	 */
	void enterProgram(GrammarParser.ProgramContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#program}.
	 * @param ctx the parse tree
	 */
	void exitProgram(GrammarParser.ProgramContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void enterDeclaration(GrammarParser.DeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#declaration}.
	 * @param ctx the parse tree
	 */
	void exitDeclaration(GrammarParser.DeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void enterType(GrammarParser.TypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#type}.
	 * @param ctx the parse tree
	 */
	void exitType(GrammarParser.TypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterClassDeclaration(GrammarParser.ClassDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#classDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitClassDeclaration(GrammarParser.ClassDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#globalVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterGlobalVariableDeclaration(GrammarParser.GlobalVariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#globalVariableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitGlobalVariableDeclaration(GrammarParser.GlobalVariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclaration(GrammarParser.VariableDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#variableDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclaration(GrammarParser.VariableDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void enterFunctionDeclaration(GrammarParser.FunctionDeclarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#functionDeclaration}.
	 * @param ctx the parse tree
	 */
	void exitFunctionDeclaration(GrammarParser.FunctionDeclarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#constant}.
	 * @param ctx the parse tree
	 */
	void enterConstant(GrammarParser.ConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#constant}.
	 * @param ctx the parse tree
	 */
	void exitConstant(GrammarParser.ConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpression(GrammarParser.PrimaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpression(GrammarParser.PrimaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#suffixExpression}.
	 * @param ctx the parse tree
	 */
	void enterSuffixExpression(GrammarParser.SuffixExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#suffixExpression}.
	 * @param ctx the parse tree
	 */
	void exitSuffixExpression(GrammarParser.SuffixExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterUnaryExpression(GrammarParser.UnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#unaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitUnaryExpression(GrammarParser.UnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(GrammarParser.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(GrammarParser.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(GrammarParser.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(GrammarParser.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void enterShiftExpression(GrammarParser.ShiftExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#shiftExpression}.
	 * @param ctx the parse tree
	 */
	void exitShiftExpression(GrammarParser.ShiftExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#relationExpression}.
	 * @param ctx the parse tree
	 */
	void enterRelationExpression(GrammarParser.RelationExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#relationExpression}.
	 * @param ctx the parse tree
	 */
	void exitRelationExpression(GrammarParser.RelationExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void enterEqualityExpression(GrammarParser.EqualityExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#equalityExpression}.
	 * @param ctx the parse tree
	 */
	void exitEqualityExpression(GrammarParser.EqualityExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#bitwiseAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitwiseAndExpression(GrammarParser.BitwiseAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#bitwiseAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitwiseAndExpression(GrammarParser.BitwiseAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#bitwiseExclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitwiseExclusiveOrExpression(GrammarParser.BitwiseExclusiveOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#bitwiseExclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitwiseExclusiveOrExpression(GrammarParser.BitwiseExclusiveOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#bitwiseInclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterBitwiseInclusiveOrExpression(GrammarParser.BitwiseInclusiveOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#bitwiseInclusiveOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitBitwiseInclusiveOrExpression(GrammarParser.BitwiseInclusiveOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalAndExpression(GrammarParser.LogicalAndExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#logicalAndExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalAndExpression(GrammarParser.LogicalAndExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void enterLogicalOrExpression(GrammarParser.LogicalOrExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#logicalOrExpression}.
	 * @param ctx the parse tree
	 */
	void exitLogicalOrExpression(GrammarParser.LogicalOrExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentExpression(GrammarParser.AssignmentExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#assignmentExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentExpression(GrammarParser.AssignmentExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(GrammarParser.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(GrammarParser.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(GrammarParser.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(GrammarParser.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void enterCompoundStatement(GrammarParser.CompoundStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#compoundStatement}.
	 * @param ctx the parse tree
	 */
	void exitCompoundStatement(GrammarParser.CompoundStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void enterExpressionStatement(GrammarParser.ExpressionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#expressionStatement}.
	 * @param ctx the parse tree
	 */
	void exitExpressionStatement(GrammarParser.ExpressionStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void enterSelectionStatement(GrammarParser.SelectionStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#selectionStatement}.
	 * @param ctx the parse tree
	 */
	void exitSelectionStatement(GrammarParser.SelectionStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code whileLoop}
	 * labeled alternative in {@link GrammarParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterWhileLoop(GrammarParser.WhileLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code whileLoop}
	 * labeled alternative in {@link GrammarParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitWhileLoop(GrammarParser.WhileLoopContext ctx);
	/**
	 * Enter a parse tree produced by the {@code forLoop}
	 * labeled alternative in {@link GrammarParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void enterForLoop(GrammarParser.ForLoopContext ctx);
	/**
	 * Exit a parse tree produced by the {@code forLoop}
	 * labeled alternative in {@link GrammarParser#iterationStatement}.
	 * @param ctx the parse tree
	 */
	void exitForLoop(GrammarParser.ForLoopContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#init}.
	 * @param ctx the parse tree
	 */
	void enterInit(GrammarParser.InitContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#init}.
	 * @param ctx the parse tree
	 */
	void exitInit(GrammarParser.InitContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#condition}.
	 * @param ctx the parse tree
	 */
	void enterCondition(GrammarParser.ConditionContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#condition}.
	 * @param ctx the parse tree
	 */
	void exitCondition(GrammarParser.ConditionContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#step}.
	 * @param ctx the parse tree
	 */
	void enterStep(GrammarParser.StepContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#step}.
	 * @param ctx the parse tree
	 */
	void exitStep(GrammarParser.StepContext ctx);
	/**
	 * Enter a parse tree produced by the {@code breakStatement}
	 * labeled alternative in {@link GrammarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterBreakStatement(GrammarParser.BreakStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code breakStatement}
	 * labeled alternative in {@link GrammarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitBreakStatement(GrammarParser.BreakStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code continueStatement}
	 * labeled alternative in {@link GrammarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterContinueStatement(GrammarParser.ContinueStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code continueStatement}
	 * labeled alternative in {@link GrammarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitContinueStatement(GrammarParser.ContinueStatementContext ctx);
	/**
	 * Enter a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link GrammarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void enterReturnStatement(GrammarParser.ReturnStatementContext ctx);
	/**
	 * Exit a parse tree produced by the {@code returnStatement}
	 * labeled alternative in {@link GrammarParser#jumpStatement}.
	 * @param ctx the parse tree
	 */
	void exitReturnStatement(GrammarParser.ReturnStatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link GrammarParser#variableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void enterVariableDeclarationStatement(GrammarParser.VariableDeclarationStatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link GrammarParser#variableDeclarationStatement}.
	 * @param ctx the parse tree
	 */
	void exitVariableDeclarationStatement(GrammarParser.VariableDeclarationStatementContext ctx);
}