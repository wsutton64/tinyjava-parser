public class SymbolTable {
    public SymbolTable(TJChario chario) {
        // Creates an empty stack of tables, with a reference to a Chario object for the output of error messages.
    }

    public void enterScope() {
        // Pushes a new table onto the stack.
    }

    public void exitScope() {
        // Pops a table from the stack and prints its contents.
    }

    public SymbolEntry enterSymbol(String string) {
        // If name is not already present, inserts an antry for it into the table and returns that entry; otherwise prints an error message and returns an empty entry.
        return null;
    }

    public SymbolEntry findSymbol(String name) {
        // If name is already present, returns its entry; otherwise prints an error message and returns an empty entry.
        return null;
    }
}
