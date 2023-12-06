public class TJParser {
    private TJChario chario;
    private TJScanner scanner;
    private Postfix postfix;
    private Token token;
    private SymbolTable table;

    public TJParser(TJScanner scanner, TJChario chario) {
        // Initialize variables
        this.chario = chario;
        this.scanner = scanner;
        this.token = scanner.getToken();
        this.table = new SymbolTable(chario);
        this.postfix = new Postfix();
        // Start the parsing process
        chario.putMessage("\nStart parsing...");
        program();
        chario.putMessage("Parsing Complete.\n\nPostfix:");
        for (String element : postfix.getList()) {
            chario.putMessage(element);
        }
    }

    /*
     * Program -> block*
     */
    private void program() {
        if (scanner.getToken().getType() == Token.TokenType.ERROR) {
            // This block will run if there are token errors found in the Scanner and prevent the parser from running.
            syntaxError("Error Token. Throw exception and prevent parser from running.");
        }
        // Keep parsing blocks until reaches EoF
        while(scanner.getToken().getType() != Token.TokenType.EOF) {
            block();
            scanner.nextToken();
            token = scanner.getToken();
        }
    }

    /*
     * block -> "{" statement* "}"
     */
    private void block() {
        expectedValue("{");
        table.enterScope();
        // Keep parsing statements until it reaches "}"
        while(scanner.getToken().getType() != Token.TokenType.PUNCTUATION) {
            statement();
        }
        expectedValue("}");
        table.exitScope();
    }

    /*
     * statement -> if-statement | 
     *              declaration-statement | 
     *              assignment-statement
     */
    private void statement() {
        // Check if the statement beginds with a Keyword or Identifier
        if (token.getType() == Token.TokenType.KEYWORD) {
            String keyword = token.getValue();
            // Call the method of the corresponding keywords
            switch (keyword) {
                case "if":
                    ifStatement();
                    break;
                case "int":
                    declarationStatement();
                    break;
                case "double":
                    declarationStatement();
                    break;
                default:
                    syntaxError("Invalid Keyword");
            }
        } else if(token.getType() == Token.TokenType.IDENTIFIER) {
            // If the statement begins with an identifier, run the assignment statement
            assignmentStatement();
        } else {
            if (token.getType() == Token.TokenType.EOF) {
                syntaxError("Unexpected End of File. Missing '}'");
            } else {
                syntaxError("Invalid Statement. Expected Keyword or Identifier. Got: " + token );
            }
        }
    }

    /*
     * if-statement -> "if" "(" equivalence ")" block
     */
    private void ifStatement() {
        expectedValue("if");
        expectedValue("(");
        //postfix.add("equivalence");
        equivalence();
        expectedValue(")");
        postfix.add("ifT");
        block();
        postfix.submit();
    }

    /*
     * declaration-statement -> datatype identifier ";" |
     *                          datatype assignment-statement ";"
     */
    private void declarationStatement() {
        datatype();
        //String identifier = token.getValue();
        identifier();
        //SymbolEntry entry = table.enterSymbol(identifier);
        if (token.getValue().equals("=")) {
            assignmentStatement();
        } else if(token.getType() == Token.TokenType.NUMBER) {
            syntaxError("Missing '='");
        } else {
            postfix.submit();
            expectedValue(";");
        }
    }

    /*
     * assignment-statement -> identifier "=" expression ";"
     */
    private void assignmentStatement() {
        if (!token.getValue().equals("=")) {
            identifier();
        }
        //expectedValue("=");
        if (!token.getValue().matches("=|\\+=|\\-=|\\*=|\\%=|\\/=")) {
            syntaxError("Expected assignment operator. Found: " + token.getValue());
        }
        String operator = token.getValue();
        scanner.nextToken();
        token = scanner.getToken();
        expression();
        postfix.add(operator);
        postfix.submit();
        expectedValue(";");
    }

    /*
     * datatype -> int | double
     */
    private void datatype() {
        if (!token.getValue().equals("int") && !token.getValue().equals("double")) {
            syntaxError("Invalid Datatype. Expected 'int' or 'double'. Got: " + token.getValue());
        }
        expectedType(Token.TokenType.KEYWORD);
    }

    /*
     * identifier -> (letter | "_") (letter | digit | "_")*
     */
    private void identifier() {
        String id = token.getValue();
        expectedType(Token.TokenType.IDENTIFIER);
        postfix.add(id);
    }

    /*
     * equivalence -> expression (">" | "<" | "==" | "<=" | ">=" | "!=") expression
     */
    private void equivalence() {
        expression();
        //expectedValue("");
        if (token.getValue().matches("<|>|<=|>=|==|!=")) {
            String operator = token.getValue();
            scanner.nextToken();
            token = scanner.getToken();
            expression();
            postfix.add(operator);
            postfix.submit();
        } else {
            syntaxError("Invalid equivalence operator. Got: " + token);
        }
        
    }

    /*
     * expression -> expression ("+" | "-") term | 
     *               term
     */
    private void expression() {
        term();
        //postfix.add(token.getValue());
        while (token.getValue().matches("\\+|\\-|\\+=|\\-=")) {
            String operator = token.getValue();
            //expectedValue("");
            scanner.nextToken();
            token = scanner.getToken();
            term();
            postfix.add(operator);
            postfix.submit();
        }
    }

    /*
     * term -> term ("*" | "/" | "%") factor | 
     *         factor
     */
    private void term() {
        factor();
        //postfix.add(token.getValue());
        while (token.getValue().matches("\\/|\\*|\\%|\\/=|\\*=|\\%=")) {
            String operator = token.getValue();
            //expectedValue("");
            scanner.nextToken();
            token = scanner.getToken();
            postfix.add(operator);
            factor();
        }
    }

    /*
     * factor -> "(" expression ")" | 
     *           number
     */
    private void factor() {
        if (token.getValue().equals("(")) {
            expectedValue("(");
            expression();
            expectedValue(")");
        } else if (token.getType() == Token.TokenType.NUMBER) {
            number();
        } else {
            identifier();
        }
    }

    /*
     * number -> number digit |
     *           number "." number |
     *           digit
     */
    private void number() {
        String num = token.getValue();
        expectedType(Token.TokenType.NUMBER);
        postfix.add(num);
    }

    // Check if the current token's type is equal to the expected type
    // If not, send a syntax error. Otherwise, move onto the next token.
    private void expectedType(Token.TokenType expectedType) {
        //System.out.println(token);
        if (token.getType() != expectedType) {
            syntaxError("Expected type: " + expectedType + ", Found: " + token);
        }
        scanner.nextToken();
        token = scanner.getToken();
    }

    // Check if the current token's value is equal to the expected value
    // If not, send a syntax error. Otherwise, move onto the next token.
    private void expectedValue(String expectedValue) {
        //System.out.println(token);
        if (!token.getValue().equals(expectedValue)) {
            syntaxError("Expected value: " + expectedValue + ", Found: " + token);
        }
        scanner.nextToken();
        token = scanner.getToken();
    }

    // Send an error message to chario and throw a RuntimeException
    private void syntaxError(String error) {
        chario.putError(error);
        throw new RuntimeException("Fatal Error");
    }
}
