package compiler.build;

/**
 * Created by bluesnap on 16/3/31.
 */

import compiler.ast.AbstractSyntaxTree;
import compiler.ast.SymbolTable;
import compiler.ir.IR;
import compiler.parser.GrammarLexer;
import compiler.parser.GrammarParser;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import java.io.*;
import java.util.Stack;

public class Build {
    public static void main(String[] args) throws Exception {
//        String inputFile = "/Users/bluesnap/Documents/Project/IntelliJ Idea Project/Mx/src/compiler/testfile/compile_error/incop-1-5120309049-liaochao.mx";
//        String inputFile = "/Users/bluesnap/Documents/Project/IntelliJ Idea Project/Mx/testfile/test.mx";
//        String inputFile = "/Users/bluesnap/Documents/Project/IntelliJ Idea Project/Mx/testfile/passed/queens-5100379110-daibo.mx";
//        InputStream is = new FileInputStream(inputFile);
//        OutputStream os = new FileOutputStream("/Users/bluesnap/res.s");
        ANTLRInputStream input = new ANTLRInputStream(System.in);
        GrammarLexer lexer = new GrammarLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        GrammarParser parser = new GrammarParser(tokens);
        ParseTree tree = parser.program(); // parse;

        ParseTreeWalker walker = new ParseTreeWalker();
        AstBuilderListener astBuilder = new AstBuilderListener(parser);
        walker.walk(astBuilder, tree);

        //AstBuilderListener.property.get(tree).print(0);

        boolean ret = true;
        SymbolTable table = new SymbolTable();
        ret = ret & (AstBuilderListener.property.get(tree).first(table));
        ret = ret & (AstBuilderListener.property.get(tree).second(table));
        ret = ret & (AstBuilderListener.property.get(tree).third(table, null, new Stack<>()));

        /*
        if (ret)
            System.out.println("Sematic Check YES");
        else
            System.out.println("Sematic Check NO");
           */

        IR.init();
        IR ir = ((AbstractSyntaxTree) AstBuilderListener.property.get(tree)).generateIR(table, null, new Stack<>());
        ir.memoryAllocation();

        String content = ir.toMIPS();
        System.out.println(content);
        /*
        File file = new File("/Users/bluesnap/res.s");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);
        bw.write(content);
        bw.close();
        */
        //System.out.println(ir.toMIPS());
        //System.out.println(ir.print());
        //System.out.println(ir.toCISC());
    }
}
