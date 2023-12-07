import java.util.*;

public class SymbolTable {
    private Stack<Hashtable<String, SymbolEntry>> symbolStack;
    private TJChario chario;

    public SymbolTable(TJChario chario) {
        // Creates an empty stack of tables, with a reference to a Chario object for the output of error messages.
        this.chario = chario;
        symbolStack = new Stack<>();
    }

    public void enterScope() {
        // Pushes a new table onto the stack.
        symbolStack.push(new Hashtable<>());
    }

    public void exitScope() {
        // Pops a table from the stack and prints its contents.
        Hashtable<String, SymbolEntry> currentTable = symbolStack.pop();
        chario.putMessage("Exiting Scope. Symbol Table Contents:");
        for (String name : currentTable.keySet()) {
            chario.putMessage(currentTable.get(name).getName());
        }
    }

    public SymbolEntry enterSymbol(String name) {
        // If name is not already present, inserts an entry for it into the table and returns that entry; otherwise prints an error message and returns an empty entry.
        if (!symbolStack.peek().containsKey(name)) {
            SymbolEntry entry = new SymbolEntry(name);
            symbolStack.peek().put(name, entry);
            return entry;
        } else {
            chario.putError("Error: " + name + " already declared");
            return new SymbolEntry("");
        }
    }

    public SymbolEntry findSymbol(String name) {
        // If name is already present, returns its entry; otherwise prints an error message and returns an empty entry.
        for (Hashtable<String, SymbolEntry> table : symbolStack) {
            if (table.containsKey(name)) {
                return table.get(name);
            }
        }
        chario.putError("Error: " + name + " not declared");
        return new SymbolEntry("");
    }
}
