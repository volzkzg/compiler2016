package compiler.ir;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by bluesnap on 16/4/28.
 */
public class IR {
    public List<Function> parts;
    public static List<StackEntry> stackEntryList;
    public static List<Quadruple> quadrupleList;
    public static List<StringAddressConst> stringAddressConstList;

    public static List<Quadruple> code;

    public static void init() {
        stackEntryList = new LinkedList<>();
        quadrupleList = new LinkedList<>();
        stringAddressConstList = new LinkedList<>();
    }

    public IR() {
        parts = new LinkedList<>();
    }

    public IR(List<Function> parts) {
        this.parts = parts;
    }

    public String print() {
        String ret = "";
        for (Function p : parts) {
            ret += p.print() + "\n\n";
        }
        return ret;
    }

    public String toCISC() {
        String ret = "";

        ret += ".data\n";

        // stackEntryList to .data
        for (int i = 0; i < stackEntryList.size(); ++i) {
            ret += stackEntryList.get(i).toCISC();
        }
        // stringAddressList to .data
        for (int i = 0; i < stringAddressConstList.size(); ++i) {
            ret += stringAddressConstList.get(i).toCISC();
        }

        ret += ".text\n";
        for (int i = 0; i < parts.size(); ++i) {
            if (parts.get(i).name.equals("main")) {
                ret += "main:\n";
                // quadruple in global
                for (int j = 0; j < quadrupleList.size(); ++j) {
                    ret += quadrupleList.get(j).toCISC();
                }
                ret += "b L" + parts.get(i).label.num + "\n";
            }
            ret += parts.get(i).toCISC();
        }

        // embedded functions
//        ret +=
        return ret;
    }

    public void memoryAllocation() {
        Register.init();
        code = new LinkedList<>();

        for (int i = 0; i < quadrupleList.size(); ++i) {
            quadrupleList.get(i).memoryAllocation(code);
        }
        for (int i = 0; i < Register.tempRegNum; ++i) {
            Register.writeGlobalToMemory(code, i);
        }

        for (int i = 0; i < parts.size(); ++i) {
            parts.get(i).memoryAllocation();
        }
    }

    public String toMIPS() {
        String ret = "";

        ret += ".data\n";

        // stackEntryList to .data
        for (int i = 0; i < stackEntryList.size(); ++i)
            ret += stackEntryList.get(i).toMIPS();
        // stringAddressConst to .data
        ret += "strnewline:\n .asciiz " + "\"\\n\"" + "\n";
        for (int i = 0; i < stringAddressConstList.size(); ++i) {
            ret += "str" + stringAddressConstList.get(i).num + ":\n .word " +
                    stringAddressConstList.get(i).len +
                    "\n .asciiz " + stringAddressConstList.get(i).value + "\n";
        }

        ret += ".text\n.globl main\n";

        // add global information
        ret += "main:\n";
        int size = 100 + 32;
        ret += "sub $sp, $sp, " + size * 4 + "\n";
        ret += "sw $ra, " + ((size + 31) * 4) + "($sp)" + "\n";
        // quadruple in global
        for (int i = 0; i < code.size(); ++i) {
            ret += code.get(i).toMIPS();
            if (code.get(i) instanceof Label) {
                ret += ":\n";
            }
        }
        // find main function
        for (int i = 0; i < parts.size(); ++i) {
            if (parts.get(i).name.equals("main")) {
                ret += "subu $sp, $sp, " + (parts.get(i).before + 32) * 4 + "\n";
                ret += "jal main_Entry\n";
                ret += "addu $sp, $sp, " + (parts.get(i).before + 32) * 4 + "\n";
            }
        }
        ret += "lw $ra, " + ((size + 31) * 4) + "($sp)" + "\n";
        ret += "add $sp, $sp, " + size * 4 + "\n";
        ret += "jr $ra\n";

        for (int i = 0; i < parts.size(); ++i)
            ret += parts.get(i).toMIPS();

        /*
            在这里添加全局函数
         */
        // size
        ret += "size:\n" +
                "lw $v0, 0($a0)\n" +
                "jr $ra\n";

        // length
        ret += "length:\n" +
                "lw $v0, 0($a0)\n" +
                "jr $ra\n";

        // ord
        ret += "ord:\n" +
                "addu $a1, $a1, 4\n" +
                "addu $a1, $a1, $a0\n" +
                "move $v0, $a1\n" +
                "jr $ra\n";

        // new
        ret = ret + "new:\n" +
                "li $v0, 9\n" +
                "syscall\n" +
                "jr $ra\n";

        // print
        ret = ret + "print:\n" +
                "li $v0, 4\n" +
                "addu $a0, $a0, 4\n" + // 跳过记录字符串长度的 word
                "syscall\n" +
                "jr $ra\n";

        // (println)
        ret = ret + "println:\n" +
                "li $v0, 4\n" +
                "addu $a0, $a0, 4\n" + // 跳过记录字符串长度的 word
                "syscall\n" +
                "la $a0, strnewline\n" +
                "syscall\n" +
                "jr $ra\n";

        // getString
        ret = ret + "getString:\n" +
                "sw $ra, 124($sp)\n" +
                "li $v0, 9\n" +
                "li $a0, 8192\n" +
                "syscall\n" +
                "addu $a0, $v0, 4\n" +
                "li $a1, 8188\n" +
                "li $v0, 8\n" +
                "syscall\n" +
                "subu $v0, $a0, 4\n" +
                "getString_label:\n" +
                "lb $a1, 0($a0)\n" +
                "addu $a0, $a0, 1\n" +
                "bnez $a1, getString_label\n" +
                "subu $a0, $a0, $v0\n" +
                "subu $a0, $a0, 5\n" +
                "sw $a0, 0($v0)\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        // getInt
        ret = ret + "getInt:\n" +
                "sw $ra, 124($sp)\n" +
                "li $a1, 0\n" +
                "getInt_label1:\n" +
                "li $v0, 12\n" +
                "syscall\n" +
                "beq $v0, 45, getInt_label2\n" +
                "bge $v0, 48, getInt_label3\n" +
                "j getInt_label1\n" +
                "getInt_label2:\n" +
                "li $v0, 12\n" +
                "syscall\n" +
                "li $a1, 1\n" +
                "getInt_label3:\n" +
                "sub $a0, $v0, 48\n" +
                "getInt_label6:\n" +
                "li $v0, 12\n" +
                "syscall\n" +
                "blt $v0, 48, getInt_label4\n" +
                "sub $v0, $v0, 48\n" +
                "mul $a0, $a0, 10\n" +
                "add $a0, $a0, $v0\n" +
                "j getInt_label6\n" +
                "getInt_label4:\n" +
                "move $v0, $a0\n" +
                "beq $a1, 0, getInt_label5\n" +
                "neg $v0, $v0\n" +
                "getInt_label5:\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        // toString
        ret = ret + "toString:\n" +
                "sw $ra, 124($sp)\n" +
                "move $a1, $a0\n" +
                "li $a2, 0\n" +
                "toString_label1:\n" +
                "div $a1, $a1, 10\n" +
                "addu $a2, $a2, 1\n" +
                "bnez $a1, toString_label1\n" +
                "move $a1, $a0\n" +
                "move $a0, $a2\n" +
                "addu $a0, $a0, 9\n" +
                "divu $a0, $a0, 4\n" +
                "mulou $a0, $a0, 4\n" +
                "li $v0, 9\n" +
                "syscall\n" +
                "bltz $a1, toString_label2\n" +
                "sw $a2, 0($v0)\n" +
                "addu $a0, $v0, 4\n" +
                "addu $a0, $a0, $a2\n" +
                "j toString_label3\n" +
                "toString_label2:\n" +
                "abs $a1, $a1\n" +
                "addu $a2, $a2, 1\n" +
                "sw $a2, 0($v0)\n" +
                "addu $a0, $v0, 4\n" +
                "li $a3, 45\n" +
                "sb $a3, 0($a0)\n" +
                "addu $a0, $a0, $a2\n" +
                "toString_label3:\n" +
                "li $a2, 0\n" +
                "sb $a2, 0($a0)\n" +
                "toString_label4:\n" +
                "subu $a0, $a0, 1\n" +
                "rem $a3, $a1, 10\n" +
                "addu $a3, $a3, 48\n" +
                "sb $a3, 0($a0)\n" +
                "div $a1, $a1, 10\n" +
                "bnez $a1, toString_label4\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        // subString
        ret = ret + "substring:\n" +
                "sw $ra, 124($sp)\n" +
                "addu $a1, $a1, 4\n" +
                "addu $a1, $a1, $a0\n" +
                "addu $a2, $a2, 4\n" +
                "addu $a2, $a2, $a0\n" +
                "li $v0, 9\n" +
                "subu $a0, $a2, $a1\n" +
                "addu $a0, $a0, 9\n" +
                "divu $a0, $a0, 4\n" +
                "mulou $a0, $a0, 4\n" +
                "syscall\n" +
                "subu $a3, $a2, $a1\n" +
                "addu $a3, $a3, 1\n" +
                "sw $a3, 0($v0)\n" +
                "addu $a0, $v0, 4\n" +
                "substring_label1:\n" +
                "bgt $a1, $a2, substring_label2\n" +
                "lb $a3, 0($a1)\n" +
                "sb $a3, 0($a0)\n" +
                "addu $a1, $a1, 1\n" +
                "addu $a0, $a0, 1\n" +
                "j substring_label1\n" +
                "substring_label2:\n" +
                "li $a3, 0\n" +
                "sb $a3, 0($a0)\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        // parseInt
        ret = ret + "parseInt:\n" +
                "sw $ra, 124($sp)\n" +
                "li $a1, 0\n" +
                "addu $a2, $a0, 4\n" +
                "lb $a3, 0($a2)\n" +
                "bge $a3, 48, parseInt_label1\n" +
                "addu $a2, $a2, 1\n" +
                "lb $a3, 0($a2)\n" +
                "li $a1, 1\n" +
                "parseInt_label1:\n" +
                "sub $a0, $a3, 48\n" +
                "parseInt_label2:\n" +
                "addu $a2, $a2, 1\n" +
                "lb $a3, 0($a2)\n" +
                "blt $a3, 48, parseInt_label3\n" +
                "bgt $a3, 57, parseInt_label3\n" +
                "sub $a3, $a3, 48\n" +
                "mul $a0, $a0, 10\n" +
                "add $a0, $a0, $a3\n" +
                "j parseInt_label2\n" +
                "parseInt_label3:\n" +
                "move $v0, $a0\n" +
                "beq $a1, 0, parseInt_label4\n" +
                "neg $v0, $v0\n" +
                "parseInt_label4:\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        // stringAdd
        ret = ret + "stringAdd:\n" +
                "sw $ra, 124($sp)\n" +
                "lw $a2, 0($a0)\n" +
                "lw $a3, 0($a1)\n" +
                "add $a3, $a2, $a3\n" +
                "move $a2, $a0\n" +
                "move $a0, $a3\n" +
                "add $a0, $a0, 8\n" +
                "div $a0, $a0, 4\n" +
                "mul $a0, $a0, 4\n" +
                "li $v0, 9\n" +
                "syscall\n" +
                "sw $a3, 0($v0)\n" +
                "addu $a0, $v0, 4\n" +
                "addu $a2, $a2, 4\n" +
                "stringAdd_label1:\n" +
                "lb $a3, 0($a2)\n" +
                "beqz $a3, stringAdd_label2\n" +
                "sb $a3, 0($a0)\n" +
                "addu $a2, $a2, 1\n" +
                "addu $a0, $a0, 1\n" +
                "j stringAdd_label1\n" +
                "stringAdd_label2:\n" +
                "addu $a1, $a1, 4\n" +
                "stringAdd_label3:\n" +
                "lb $a3, 0($a1)\n" +
                "beqz $a3, stringAdd_label4\n" +
                "sb $a3, 0($a0)\n" +
                "addu $a1, $a1, 1\n" +
                "addu $a0, $a0, 1\n" +
                "j stringAdd_label3\n" +
                "stringAdd_label4:\n" +
                "li $a3, 0\n" +
                "sb $a3, 0($a0)\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        // stringEqual
        ret = ret + "stringEqual:\n" +
                "sw $ra, 124($sp)\n" +
                "lw $a2, 0($a0)\n" +
                "lw $a3, 0($a1)\n" +
                "bne $a2, $a3, stringEquals_neq\n" +
                "addu $a0, $a0, 4\n" +
                "addu $a1, $a1, 4\n" +
                "stringEquals_start:\n" +
                "lb $a2, 0($a0)\n" +
                "lb $a3, 0($a1)\n" +
                "addu $a0, $a0, 1\n" +
                "addu $a1, $a1, 1\n" +
                "bne $a2, $a3, stringEquals_neq\n" +
                "beq $a2, 0, stringEquals_eq\n" +
                "j stringEquals_start\n" +
                "stringEquals_eq:\n" +
                "li $v0, 1\n" +
                "j stringEquals_end\n" +
                "stringEquals_neq:\n" +
                "li $v0, 0\n" +
                "stringEquals_end:\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        // stringLessThan
        ret = ret + "stringLessThan:\n" +
                "sw $ra, 124($sp)\n" +
                "addu $a0, $a0, 4\n" +
                "addu $a1, $a1, 4\n" +
                "stringLessThan_start:\n" +
                "lb $a2, 0($a0)\n" +
                "lb $a3, 0($a1)\n" +
                "addu $a0, $a0, 1\n" +
                "addu $a1, $a1, 1\n" +
                "add $v0, $a2, $a3\n" +
                "beq $v0, 0, stringLessThan_no\n" +
                "beq $a2, 0, stringLessThan_yes\n" +
                "beq $a3, 0, stringLessThan_no\n" +
                "blt $a2, $a3, stringLessThan_yes\n" +
                "bgt $a2, $a3, stringLessThan_no\n" +
                "j stringLessThan_start\n" +
                "stringLessThan_yes:\n" +
                "li $v0, 1\n" +
                "j stringLessThan_end\n" +
                "stringLessThan_no:\n" +
                "li $v0, 0\n" +
                "stringLessThan_end:\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        // stringLessThanOrEquals
        ret = ret + "stringLessThanOrEquals:\n" +
                "sw $ra, 124($sp)\n" +
                "addu $a0, $a0, 4\n" +
                "addu $a1, $a1, 4\n" +
                "stringLessThanOrEquals_start:\n" +
                "lb $a2, 0($a0)\n" +
                "lb $a3, 0($a1)\n" +
                "addu $a0, $a0, 1\n" +
                "addu $a1, $a1, 1\n" +
                "add $v0, $a2, $a3\n" +
                "beq $v0, 0, stringLessThanOrEquals_yes\n" +
                "beq $a2, 0, stringLessThanOrEquals_yes\n" +
                "beq $a3, 0, stringLessThanOrEquals_no\n" +
                "blt $a2, $a3, stringLessThanOrEquals_yes\n" +
                "bgt $a2, $a3, stringLessThanOrEquals_no\n" +
                "j stringLessThanOrEquals_start\n" +
                "stringLessThanOrEquals_yes:\n" +
                "li $v0, 1\n" +
                "j stringLessThanOrEquals_end\n" +
                "stringLessThanOrEquals_no:\n" +
                "li $v0, 0\n" +
                "stringLessThanOrEquals_end:\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        // stringGreatThan
        ret = ret + "stringGreatThan:\n" +
                "sw $ra, 124($sp)\n" +
                "addu $a0, $a0, 4\n" +
                "addu $a1, $a1, 4\n" +
                "stringGreatThan_start:\n" +
                "lb $a2, 0($a0)\n" +
                "lb $a3, 0($a1)\n" +
                "addu $a0, $a0, 1\n" +
                "addu $a1, $a1, 1\n" +
                "add $v0, $a2, $a3\n" +
                "beq $v0, 0, stringGreatThan_no\n" +
                "beq $a2, 0, stringGreatThan_yes\n" +
                "beq $a3, 0, stringGreatThan_no\n" +
                "bgt $a2, $a3, stringGreatThan_yes\n" +
                "blt $a2, $a3, stringGreatThan_no\n" +
                "j stringGreatThan_start\n" +
                "stringGreatThan_yes:\n" +
                "li $v0, 1\n" +
                "j stringGreatThan_end\n" +
                "stringGreatThan_no:\n" +
                "li $v0, 0\n" +
                "stringGreatThan_end:\n" +
                "lw $ra, 124($sp)\n" +
                "jr $ra\n";

        return ret;
    }
}
