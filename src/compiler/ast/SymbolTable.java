package compiler.ast;

import java.util.HashMap;

/**
 * Created by bluesnap on 16/4/2.
 */
public class SymbolTable {
    private HashMap<Symbol, Node> map = new HashMap<>();
    public SymbolTable prev, next;

    public SymbolTable() {
        prev = null;
        next = null;
    }

    public SymbolTable(SymbolTable prev) {
         this.prev = prev;
    }

    public Node find(Symbol sym) {
        if (!map.containsKey(sym)) {
            if (prev == null) {
                return null;
            } else {
                return prev.find(sym);
            }
        } else {
            return map.get(sym);
        }
    }

    public SymbolTable getNext() {
        next = new SymbolTable(this);
        return next;
    }

    public boolean insert(Symbol sym, Node node) {
        if (map.containsKey(sym))
            return false;
        map.put(sym, node);
        return true;
    }
}
