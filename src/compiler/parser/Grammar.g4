grammar Grammar;

program
    :   declaration+
    ;

declaration
    :   functionDeclaration
    |   classDeclaration
    |   globalVariableDeclaration
    ;

type
    :   Bool
    |   Int
    |   String
    |   Void
    |   Identifier
    |   type LeftBracket expression? RightBracket
    ;

classDeclaration
    :   Class Identifier '{' variableDeclarationStatement* '}'
    ;

globalVariableDeclaration
    :   type Identifier ('=' expression)? ';'
    ;

variableDeclaration
    :   (type Identifier ('=' expression)?)
    ;

functionDeclaration
    :   type Identifier '(' (variableDeclaration (',' variableDeclaration)*)? ')' compoundStatement
    |   Void Identifier '(' (variableDeclaration (',' variableDeclaration)*)? ')' compoundStatement
    ;

// 字面值
constant
    :   True
    |   False
    |   IntegerConstant
    |   StringConstant
    |   Null
    ;

primaryExpression
    :   constant
    |   Identifier
    |   LeftParen expression RightParen
    |   New type
    ;

suffixExpression
    :   primaryExpression
//    |   suffixExpression Dot Identifier LeftParen expression? RightParen
    |   suffixExpression Dot Identifier
    |   suffixExpression LeftBracket expression RightBracket
    |   suffixExpression LeftParen (expression (',' expression)*)? RightParen
    |   suffixExpression PlusPlus
    |   suffixExpression MinusMinus
    //|   suffixExpression '.size()'
    ;

unaryExpression
    :   suffixExpression
    |   PlusPlus suffixExpression
    |   MinusMinus suffixExpression
    |   Tilde suffixExpression
    |   Not suffixExpression
    |   Plus suffixExpression
    |   Minus suffixExpression
    ;

multiplicativeExpression
    :   unaryExpression
    |   multiplicativeExpression Star unaryExpression
    |   multiplicativeExpression Div unaryExpression
    |   multiplicativeExpression Mod unaryExpression
    ;

additiveExpression
    :   multiplicativeExpression
    |   additiveExpression Plus multiplicativeExpression
    |   additiveExpression Minus multiplicativeExpression
    ;

shiftExpression
    :   additiveExpression
    |   shiftExpression LeftShift additiveExpression
    |   shiftExpression RightShift additiveExpression
    ;

relationExpression
    :   shiftExpression
    |   relationExpression Less shiftExpression
    |   relationExpression LessEqual shiftExpression
    |   relationExpression Greater shiftExpression
    |   relationExpression GreaterEqual shiftExpression
    ;

equalityExpression
    :   relationExpression
    |   equalityExpression Equal relationExpression
    |   equalityExpression NotEqual relationExpression
    ;

bitwiseAndExpression
    :   equalityExpression
    |   bitwiseAndExpression '&' equalityExpression
    ;

bitwiseExclusiveOrExpression
    :   bitwiseAndExpression
    |   bitwiseExclusiveOrExpression '^' bitwiseAndExpression
    ;

bitwiseInclusiveOrExpression
    :   bitwiseExclusiveOrExpression
    |   bitwiseInclusiveOrExpression '|' bitwiseExclusiveOrExpression
    ;

logicalAndExpression
    :   bitwiseInclusiveOrExpression
    |   logicalAndExpression '&&' bitwiseInclusiveOrExpression
    ;

logicalOrExpression
    :   logicalAndExpression
    |   logicalOrExpression '||' logicalAndExpression
    ;

assignmentExpression
    :   logicalOrExpression
    |   unaryExpression '=' assignmentExpression
    ;

expression
    :   assignmentExpression
//    |   expression (Comma expression) +
    ;

statement
    :   compoundStatement
    |   expressionStatement
    |   selectionStatement
    |   iterationStatement
    |   jumpStatement
    |   variableDeclarationStatement
    ;

compoundStatement
    :   '{' statement* '}'
    ;

expressionStatement
    :   expression? ';'
    ;

selectionStatement
    :   If '('expression')' statement (Else statement)?
    ;

iterationStatement
    :   While '('expression')' statement    # whileLoop
    |   For '('init?';'condition?';'step?')' statement     # forLoop
    ;

init
    :   expression
    ;

condition
    :   expression
    ;

step
    :   expression
    ;

jumpStatement
    :   Break ';'               # breakStatement
    |   Continue ';'            # continueStatement
    |   Return expression? ';'  # returnStatement
    ;

variableDeclarationStatement
    :   type Identifier ('=' expression)? ';'
    ;


// reserved keywords
Bool :  'bool';
Int :   'int';
String :    'string';
Null :  'null';
Void :  'void';
True :  'true';
False :     'false';
If :    'if';
Else :  'else';
For :   'for';
While :     'while';
Break :     'break';
Continue :  'continue';
Return :    'return';
New :   'new';
Class :     'class';



// 标识符
Identifier
    :   Nondigit
                (   Digit
                |   Nondigit)*
    ;

fragment
Nondigit
    :   [a-zA-Z_]
    ;

fragment
Digit
    :   [0-9]
    ;



IntegerConstant     // always DecimalConstant
    :   NonzeroDigit Digit*
    |   '0'
    ;

fragment
NonzeroDigit
    :   [1-9]
    ;

StringConstant
    :   '"' Char* '"'
    ;

fragment
Char
    :   PrintableChar
    |   EscapeChar
    ;

fragment
PrintableChar
    :   ~["\\\r\n]
    ;

fragment
EscapeChar
    : '\\\''
	| '\\"'
	| '\\?'
	| '\\\\'
	| '\\a'
	| '\\b'
	| '\\f'
	| '\\n'
	| '\\r'
	| '\\t'
	| '\\v'
    ;


// 运算符

// 算术运算符
Plus :   '+';
Minus   :   '-';
Star    :   '*';
Div :   '/';
Mod :   '%';

// 关系运算符
Less : '<';
Greater : '>';
Equal : '==';
NotEqual : '!=';
GreaterEqual : '>=';
LessEqual : '<=';

// 逻辑运算符
AndAnd : '&&';
OrOr : '||';
Not : '!';

// 位运算符
LeftShift : '<<';
RightShift : '>>';
Tilde : '~';
Or : '|';
Caret : '^';
And : '&';

// 赋值运算符
Assign : '=';

// 自增运算符和自减运算符
PlusPlus : '++';
MinusMinus : '--';

// 分量运算符
Dot : '.';

// 下标运算符
LeftBracket : '[';
RightBracket : ']';

// 括号(可以用于 calling function & subexpression grouping)
LeftParen : '(';
RightParen : ')';

Question : '?';
Colon : ':';
Semi : ';';
Comma : ',';

// 空白字符的处理
WhiteSpace
    :   [ \t]+
        -> skip
    ;

NewLine
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

// 注释

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;