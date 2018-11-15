package compiler.ast.statement.expression.binary;

/**
 * Created by bluesnap on 16/3/31.
 */
public enum BinaryOperator {
    ASSIGN,

    LOGICAL_OR, LOGICAL_AND, BITWISE_INCLUSIVE_OR, BITWISE_EXCLUSIVE_OR, BITWISE_AND,

    EQUAL, NOT_EQUAL, LESS, LEQ, GREAT, GEQ, LEFT_SHIFT, RIGHT_SHIFT,

    ADD, SUB, MUL, DIV, MOD
}
