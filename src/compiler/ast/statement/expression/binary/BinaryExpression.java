package compiler.ast.statement.expression.binary;

import compiler.ast.Node;
import compiler.ast.SymbolTable;
import compiler.ast.declaration.FunctionDeclaration;
import compiler.ast.statement.expression.Expression;
import compiler.ast.statement.expression.suffix.ArrayAccessExpression;
import compiler.ast.statement.expression.suffix.FieldAccessExpression;
import compiler.ast.type.*;
import compiler.ir.*;

import java.util.LinkedList;
import java.util.Stack;

/**
 * Created by bluesnap on 16/3/31.
 */
public class BinaryExpression extends Expression {
    public Expression leftHandSide, rightHandSide;
    public BinaryOperator operator;

    public BinaryExpression() {
        leftHandSide = null;
        rightHandSide = null;
        operator = null;
    }

    public BinaryExpression(Expression leftHandSide, Expression rightHandSide, BinaryOperator operator) {
        this.leftHandSide = leftHandSide;
        this.rightHandSide = rightHandSide;
        this.operator = operator;
    }

    public void print(int d) {
        indent(d);
        System.out.println("BinaryExpression " + operator.name());

        leftHandSide.print(d + 1);
        rightHandSide.print(d + 1);
    }

    @Override
    public boolean third(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack) {
        if (!leftHandSide.third(current, functionState, forStack))
            return false;

        if (!rightHandSide.third(current, functionState, forStack))
            return false;

        Type tp1 = leftHandSide.expressionType;
        Type tp2 = rightHandSide.expressionType;
        BinaryOperator op = operator;

        if (!typeCompare(tp1, tp2)) {
            if (operator == BinaryOperator.ASSIGN &&
                    tp2 instanceof NullType &&
                    (tp1 instanceof ArrayType ||
                    tp1 instanceof ClassType)) {
                expressionType = new NullType();
                isLvalue = false;
                return true;
            } else if (operator == BinaryOperator.EQUAL ||
                    operator == BinaryOperator.NOT_EQUAL) {
                if (tp1 instanceof NullType &&
                        (tp2 instanceof ArrayType ||
                        tp2 instanceof ClassType)) {
                    expressionType = new BoolType();
                    isLvalue = false;
                    return true;
                } else if (tp2 instanceof NullType &&
                            (tp1 instanceof ArrayType ||
                                tp1 instanceof ClassType)) {
                    expressionType = new BoolType();
                    isLvalue = false;
                    return true;
                } else {
                    outputErrorInfomation(this);
                    return false;
                }
            } else{
                outputErrorInfomation(this);
                return false;
            }
        }

        if ((op == BinaryOperator.LOGICAL_AND) ||
                (op == BinaryOperator.LOGICAL_OR)) {
            if (tp1 instanceof BoolType) {
                expressionType = new BoolType();
                isLvalue = false;
            } else {
                outputErrorInfomation(this);
                return false;
            }
        }

        if ((op == BinaryOperator.BITWISE_AND) ||
                (op == BinaryOperator.BITWISE_EXCLUSIVE_OR) ||
                (op == BinaryOperator.BITWISE_INCLUSIVE_OR)) {
            if (tp1 instanceof IntType) {
                expressionType = tp1;
                isLvalue = false;
            } else {
                outputErrorInfomation(this);
                return false;
            }
        }

        if (op == BinaryOperator.EQUAL) {
            if ((tp1 instanceof VoidType) ||
                    (tp1 instanceof NullType)) {
                outputErrorInfomation(this);
                return false;
            } else {
                expressionType = new BoolType();
                isLvalue = false;
            }
        }

        if (op == BinaryOperator.NOT_EQUAL) {
            if ((tp1 instanceof VoidType) ||
                    (tp1 instanceof StringType) ||
                    (tp1 instanceof NullType)) {
                outputErrorInfomation(this);
                return false;
            } else {
                expressionType = new BoolType();
                isLvalue = false;
            }
        }

        if ((op == BinaryOperator.LESS) ||
                (op == BinaryOperator.LEQ) ||
                (op == BinaryOperator.GREAT) ||
                (op == BinaryOperator.GEQ)) {
            if ((tp1 instanceof VoidType) ||
                    (tp1 instanceof ArrayType) ||
                    (tp1 instanceof BoolType) ||
                    (tp1 instanceof ClassType)) {
                outputErrorInfomation(this);
                return false;
            } else {
                expressionType = new BoolType();
                isLvalue = false;
            }
        }

        if (op == BinaryOperator.ADD) {
            if (tp1 instanceof StringType) {
                expressionType = tp1;
                isLvalue = false;
            } else if (tp1 instanceof IntType) {
                expressionType = tp1;
                isLvalue = false;
            } else {
                outputErrorInfomation(this);
                return false;
            }
        }

        if (op == BinaryOperator.SUB ||
                op == BinaryOperator.MUL ||
                op == BinaryOperator.DIV ||
                op == BinaryOperator.MOD ||
                op == BinaryOperator.LEFT_SHIFT ||
                op == BinaryOperator.RIGHT_SHIFT) {
            if (tp1 instanceof IntType) {
                expressionType = tp1;
                isLvalue = false;
            } else {
                outputErrorInfomation(this);
                return false;
            }
        }

        if (op == BinaryOperator.ASSIGN) {
            if (!leftHandSide.isLvalue) {
                outputErrorInfomation(this);
                return false;
            }
            expressionType = tp1;
            isLvalue = false;
        }

        return true;
    }

    @Override
    public Address getValue(SymbolTable current, FunctionDeclaration functionState, Stack<Node> forStack, Function function) {
        if (operator == BinaryOperator.LOGICAL_AND) {
            LinkedList<Expression> list = new LinkedList<>();
            LinkedList<Expression> expr = new LinkedList<>();
            Expression p = this;
            while (p instanceof BinaryExpression && ((BinaryExpression) p).operator == BinaryOperator.LOGICAL_AND) {
                list.add(p);
                p = ((BinaryExpression) p).leftHandSide;
            }
            if (list.size() > 10) {
                expr.add(p);
                for (int i = list.size() - 1; i >= 0; --i) {
                    expr.add(((BinaryExpression) list.get(i)).rightHandSide);
                }

                Expression u, v = expr.get(expr.size() - 1);
                BinaryExpression x = new BinaryExpression();

                for (int i = expr.size() - 2; i >= 0; --i) {
                    u = expr.get(i);
                    x = new BinaryExpression();
                    x.operator = BinaryOperator.LOGICAL_AND;
                    x.leftHandSide = u;
                    x.rightHandSide = v;
                    v = x;
                }

                StackEntry dest = new StackEntry();
                Address lhs = x.leftHandSide.getValue(current, functionState, forStack, function);
                if (lhs instanceof IntegerConst) {
                    if (((IntegerConst) lhs).value == 0) {
                        return (new IntegerConst(0));
                    } else {
                        return x.rightHandSide.getValue(current, functionState, forStack, function);
                    }
                } else if (lhs instanceof StackEntry) {
                    Label next = new Label();
                    Label succ = new Label();
                    Label fail = new Label();
                    Label exit = new Label();
                    function.body.add(new Branch(lhs, next, fail));
                    function.body.add(next);
                    Address rhs = x.rightHandSide.getValue(current, functionState, forStack, function);
                    function.body.add(new Branch(rhs, succ, fail));
                    function.body.add(succ);
                    function.body.add(new Assign(dest, new IntegerConst(1)));
                    function.body.add(new Goto(exit));
                    function.body.add(fail);
                    function.body.add(new Assign(dest, new IntegerConst(0)));
                    function.body.add(new Goto(exit));
                    function.body.add(exit);
                    return dest;
                }
            }
        }

        // 短路求值
        if (operator == BinaryOperator.LOGICAL_AND) {
            StackEntry dest = new StackEntry();
            Address lhs = leftHandSide.getValue(current, functionState, forStack, function);
            if (lhs instanceof IntegerConst) {
                if (((IntegerConst) lhs).value == 0) {
                    return (new IntegerConst(0));
                } else {
                    return rightHandSide.getValue(current, functionState, forStack, function);
                }
            } else if (lhs instanceof StackEntry) {
                Label next = new Label();
                Label succ = new Label();
                Label fail = new Label();
                Label exit = new Label();
                function.body.add(new Branch(lhs, next, fail));
                function.body.add(next);
                Address rhs = rightHandSide.getValue(current, functionState, forStack, function);
                function.body.add(new Branch(rhs, succ, fail));
                function.body.add(succ);
                function.body.add(new Assign(dest, new IntegerConst(1)));
                function.body.add(new Goto(exit));
                function.body.add(fail);
                function.body.add(new Assign(dest, new IntegerConst(0)));
                function.body.add(new Goto(exit));
                function.body.add(exit);
                return dest;
            }
        } else if (operator == BinaryOperator.LOGICAL_OR) {
            Address lhs = leftHandSide.getValue(current, functionState, forStack, function);
            if (lhs instanceof IntegerConst) {
                if (((IntegerConst) lhs).value == 1) {
                    return (new IntegerConst(1));
                } else {
                    return rightHandSide.getValue(current, functionState, forStack, function);
                }
            } else if (lhs instanceof StackEntry) {
                StackEntry dest = new StackEntry();
                Label next = new Label();
                Label succ = new Label();
                Label fail = new Label();
                Label exit = new Label();
                function.body.add(new Branch(lhs, succ, next));
                function.body.add(next);
                Address rhs = rightHandSide.getValue(current, functionState, forStack, function);
                function.body.add(new Branch(rhs, succ, fail));
                function.body.add(succ);
                function.body.add(new Assign(dest, new IntegerConst(1)));
                function.body.add(new Goto(exit));
                function.body.add(fail);
                function.body.add(new Assign(dest, new IntegerConst(0)));
                function.body.add(new Goto(exit));
                function.body.add(exit);
                return dest;
            }
        }

        Address lhs, rhs;
        StackEntry dest = new StackEntry(true);

        if (operator == BinaryOperator.ASSIGN) {
            if (leftHandSide instanceof ArrayAccessExpression ||
                    leftHandSide instanceof FieldAccessExpression) {
                MemoryAddress memoryAddress = (MemoryAddress) leftHandSide.getAddress(current, functionState, forStack, function);
                rhs = rightHandSide.getValue(current, functionState, forStack, function);
                StackEntry tmp = new StackEntry(true);
                if (rhs instanceof IntegerConst) {
                    function.body.add(new Assign(tmp, rhs));
                    function.body.add(new MemoryWrite(tmp, memoryAddress));
                } else if (rhs instanceof StackEntry) {
                    function.body.add(new MemoryWrite(rhs, memoryAddress));
                }
                /*
                function.body.add(new MemoryRead(dest, memoryAddress));
                return dest;
                */
                return rhs;
            } else { // IntegerConst or StringAddressConst
                lhs = leftHandSide.getValue(current, functionState, forStack, function);
                if (rightHandSide instanceof BinaryExpression) {
                    if (((BinaryExpression) rightHandSide).operator == BinaryOperator.ADD) {
                        Address a, b;
                        a = ((BinaryExpression) rightHandSide).leftHandSide.getValue(current, functionState, forStack, function);
                        b = ((BinaryExpression) rightHandSide).rightHandSide.getValue(current, functionState, forStack, function);
                        function.body.add(new ArithmeticExpression(lhs, a, ArithmeticOp.ADD, b));
                    } else {
                        rhs = rightHandSide.getValue(current, functionState, forStack, function);
                        function.body.add(new Assign(lhs, rhs));
                    }
                } else {
                    rhs = rightHandSide.getValue(current, functionState, forStack, function);
                    function.body.add(new Assign(lhs, rhs));
                }
                return lhs;
            }
        } // return TEMP

        lhs = leftHandSide.getValue(current, functionState, forStack, function);
        rhs = rightHandSide.getValue(current, functionState, forStack, function);

        if (leftHandSide.expressionType instanceof StringType &&
                rightHandSide.expressionType instanceof StringType) {
            Call call = new Call();
            call.returnValue = dest;
            call.args.add(lhs);
            call.args.add(rhs);

            switch (operator) {
                case ADD:
                    call.name = "stringAdd";
                    break;
                case EQUAL:
                    call.name = "stringEqual";
                    break;
                case LESS:
                    call.name = "stringLessThan";
                    break;
                case LEQ:
                    call.name = "stringLessThanOrEqualTo";
                    break;
                case GREAT:
                    call.name = "stringGreatThan";
                    break;
            }
            function.body.add(call);
            return dest;
        }

        Address mid;
        StackEntry newLhs;

        if (lhs instanceof IntegerConst &&
                rhs instanceof StackEntry) {
            switch (operator) {
                case ADD:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    break;
                case SUB:
                    newLhs = new StackEntry(true);
                    function.body.add(new Assign(newLhs, lhs));
                    lhs = newLhs;
                    break;
                case MUL:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    break;
                case DIV:
                    newLhs = new StackEntry(true);
                    function.body.add(new Assign(newLhs, lhs));
                    lhs = newLhs;
                    break;
                case MOD:
                    newLhs = new StackEntry(true);
                    function.body.add(new Assign(newLhs, lhs));
                    lhs = newLhs;
                    break;
                case BITWISE_AND:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    break;
                case BITWISE_EXCLUSIVE_OR:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    break;
                case BITWISE_INCLUSIVE_OR:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    break;
                case LOGICAL_AND:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    break;
                case LOGICAL_OR:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    break;
                case EQUAL:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    break;
                case NOT_EQUAL:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    break;
                case LESS:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    operator = BinaryOperator.GEQ;
                    break;
                case LEQ:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    operator = BinaryOperator.GREAT;
                    break;
                case GREAT:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    operator = BinaryOperator.LEQ;
                    break;
                case GEQ:
                    mid = lhs;
                    lhs = rhs;
                    rhs = mid;
                    operator = BinaryOperator.LESS;
                    break;
                case LEFT_SHIFT:
                    newLhs = new StackEntry(true);
                    function.body.add(new Assign(newLhs, lhs));
                    lhs = newLhs;
                    break;
                case RIGHT_SHIFT:
                    newLhs = new StackEntry(true);
                    function.body.add(new Assign(newLhs, lhs));
                    lhs = newLhs;
                    break;
            }
        }

        if (lhs instanceof IntegerConst) {
            if (rhs instanceof IntegerConst) {
                IntegerConst result;
                switch (operator) {
                    case ADD:
                        result = new IntegerConst(((IntegerConst) lhs).value + ((IntegerConst) rhs).value);
                        break;
                    case SUB:
                        result = new IntegerConst(((IntegerConst) lhs).value - ((IntegerConst) rhs).value);
                        break;
                    case MUL:
                        result = new IntegerConst(((IntegerConst) lhs).value * ((IntegerConst) rhs).value);
                        break;
                    case DIV:
                        result = new IntegerConst(((IntegerConst) lhs).value / ((IntegerConst) rhs).value);
                        break;
                    case MOD:
                        result = new IntegerConst(((IntegerConst) lhs).value % ((IntegerConst) rhs).value);
                        break;
                    case BITWISE_AND:
                        result = new IntegerConst(((IntegerConst) lhs).value & ((IntegerConst) rhs).value);
                        break;
                    case BITWISE_EXCLUSIVE_OR:
                        result = new IntegerConst(((IntegerConst) lhs).value ^ ((IntegerConst) rhs).value);
                        break;
                    case BITWISE_INCLUSIVE_OR:
                        result = new IntegerConst(((IntegerConst) lhs).value | ((IntegerConst) rhs).value);
                        break;
                    case LOGICAL_AND:
                        result = new IntegerConst(((IntegerConst) lhs).value & ((IntegerConst) rhs).value);
                        break;
                    case LOGICAL_OR:
                        result = new IntegerConst(((IntegerConst) lhs).value | ((IntegerConst) rhs).value);
                        break;
                    case EQUAL:
                        result = new IntegerConst(1 - (((IntegerConst) lhs).value - ((IntegerConst) rhs).value));
                        break;
                    case NOT_EQUAL:
                        result = new IntegerConst(((IntegerConst) lhs).value - ((IntegerConst) rhs).value);
                        break;
                    case LESS:
                        if (((IntegerConst) lhs).value < ((IntegerConst) rhs).value) {
                            result = new IntegerConst(1);
                        } else {
                            result = new IntegerConst(0);
                        }
                        break;
                    case LEQ:
                        if (((IntegerConst) lhs).value <= ((IntegerConst) rhs).value) {
                            result = new IntegerConst(1);
                        } else {
                            result = new IntegerConst(0);
                        }
                        break;
                    case GREAT:
                        if (((IntegerConst) lhs).value > ((IntegerConst) rhs).value) {
                            result = new IntegerConst(1);
                        } else {
                            result = new IntegerConst(0);
                        }
                        break;
                    case GEQ:
                        if (((IntegerConst) lhs).value > ((IntegerConst) rhs).value) {
                            result = new IntegerConst(1);
                        } else {
                            result = new IntegerConst(0);
                        }
                        break;
                    case LEFT_SHIFT:
                        result = new IntegerConst(((IntegerConst) lhs).value << ((IntegerConst) rhs).value);
                        break;
                    case RIGHT_SHIFT:
                        result = new IntegerConst(((IntegerConst) lhs).value >> ((IntegerConst) rhs).value);
                        break;
                    default:
                        result = null;
                }
                return result;
            }
        } // return IntegerConst

        // 处理字符串运算

        if (operator == BinaryOperator.ADD) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.ADD, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.SUB) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.SUB, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.MUL) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.MUL, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.DIV) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.DIV, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.MOD) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.MOD, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.BITWISE_AND) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.AND, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.BITWISE_EXCLUSIVE_OR) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.XOR, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.BITWISE_INCLUSIVE_OR) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.OR, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.LOGICAL_AND) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.AND, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.LOGICAL_OR) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.OR, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.EQUAL) {
            RelationExpression relationExpression = new RelationExpression(dest, lhs, RelationOp.EQ, rhs);
            function.body.add(relationExpression);
        } else if (operator == BinaryOperator.NOT_EQUAL) {
            RelationExpression relationExpression = new RelationExpression(dest, lhs, RelationOp.NE, rhs);
            function.body.add(relationExpression);
        } else if (operator == BinaryOperator.LESS) {
            RelationExpression relationExpression = new RelationExpression(dest, lhs, RelationOp.LT, rhs);
            function.body.add(relationExpression);
        } else if (operator == BinaryOperator.LEQ) {
            RelationExpression relationExpression = new RelationExpression(dest, lhs, RelationOp.LE, rhs);
            function.body.add(relationExpression);
        } else if (operator == BinaryOperator.GREAT) {
            RelationExpression relationExpression = new RelationExpression(dest, lhs, RelationOp.GT, rhs);
            function.body.add(relationExpression);
        } else if (operator == BinaryOperator.GEQ) {
            RelationExpression relationExpression = new RelationExpression(dest, lhs, RelationOp.GE, rhs);
            function.body.add(relationExpression);
        } else if (operator == BinaryOperator.LEFT_SHIFT) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.SHL, rhs);
            function.body.add(arithmeticExpression);
        } else if (operator == BinaryOperator.RIGHT_SHIFT) {
            ArithmeticExpression arithmeticExpression = new ArithmeticExpression(dest, lhs, ArithmeticOp.SHR, rhs);
            function.body.add(arithmeticExpression);
        }

        return dest;
    }
}
