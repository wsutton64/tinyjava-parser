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
        // If program gets to this point, Parsing is complete
        chario.putMessage("Parsing Complete.\n\nPostfix:");
        // Print out all of the postfix elements for the user to see
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
        //table.enterScope();
        // Keep parsing statements until it reaches "}"
        while(scanner.getToken().getType() != Token.TokenType.PUNCTUATION) {
            statement();
        }
        expectedValue("}");
        //table.exitScope();
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
        // Make sure all of the elements of the if-statement are in order
        expectedValue("if");
        expectedValue("(");
        equivalence();
        expectedValue(")");
        // Equivalence will get the operands. After, add "ifT" for the block
        postfix.add("ifT");
        block();
        postfix.submit();
    }

    /*
     * declaration-statement -> datatype identifier ";" |
     *                          datatype assignment-statement ";"
     */
    private void declarationStatement() {
        // Make sure all elements of the declaration statement are in order
        datatype();
        identifier();
        if (token.getValue().equals("=")) {
            // If the current token is '=', it must be a declaration statement that assigns a value.
            assignmentStatement();
        } else if(token.getType() == Token.TokenType.NUMBER) {
            // If the token is a number, there is a missing '='
            syntaxError("Missing '='");
        } else {
            // Submit as it will just be the identifier and expect ';'
            postfix.submit();
            expectedValue(";");
        }
    }

    /*
     * assignment-statement -> identifier "=" expression ";"
     */
    private void assignmentStatement() {
        if (!token.getValue().equals("=")) {
            // If the token is not '=', then the method must not have been called from declarationStatement()
            // Get the identifier
            identifier();
        }
        if (!token.getValue().matches("=|\\+=|\\-=|\\*=|\\%=|\\/=")) {
            // Make sure the token is a recognized operator.
            syntaxError("Expected assignment operator. Found: " + token.getValue());
        }
        // Save the operator and move onto the next token
        String operator = token.getValue();
        scanner.nextToken();
        token = scanner.getToken();
        // Get the following expression
        expression();
        // Add the operator to the postfix line and submit
        postfix.add(operator);
        postfix.submit();
        // Expect the statement to end with ';'
        expectedValue(";");
    }

    /*
     * datatype -> int | double
     */
    private void datatype() {
        // Just check if datatype is 'int' or 'double', then check to make sure token has a KEYWORD tokentype
        if (!token.getValue().equals("int") && !token.getValue().equals("double")) {
            syntaxError("Invalid Datatype. Expected 'int' or 'double'. Got: " + token.getValue());
        }
        expectedType(Token.TokenType.KEYWORD);
    }

    /*
     * identifier -> (letter | "_") (letter | digit | "_")*
     */
    private void identifier() {
        // Save the identifier, make sure the token is valid, then add it to the postfix string
        String id = token.getValue();
        expectedType(Token.TokenType.IDENTIFIER);
        postfix.add(id);
    }

    /*
     * equivalence -> expression (">" | "<" | "==" | "<=" | ">=" | "!=") expression
     */
    private void equivalence() {
        // Parse the first expression
        expression();
        // Make sure equivlance uses an appropriate equivalence operator
        if (token.getValue().matches("<|>|<=|>=|==|!=")) {
            // Save the operator and move onto the next token
            String operator = token.getValue();
            scanner.nextToken();
            token = scanner.getToken();
            // Parse the next expression
            expression();
            // Add the operator to the postfix string and submit
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
        // Parse the first term of the expression
        term();
        if (token.getValue().matches("\\+|\\-|\\+=|\\-=")) {
            // If the next token is a recognized operator, save it and move onto the next token
            String operator = token.getValue();
            scanner.nextToken();
            token = scanner.getToken();
            // Parse the next term
            term();
            // Add the operator to the postfix string and submit
            postfix.add(operator);
            postfix.submit();
        } // No else because an expression may boil down to just a number
    }

    /*
     * term -> term ("*" | "/" | "%") factor | 
     *         factor
     */
    private void term() {
        // Parse the first factor of the term
        factor();
        if (token.getValue().matches("\\/|\\*|\\%|\\/=|\\*=|\\%=")) {
            // if the next token is a recognized operator, save it and move onto the next token
            String operator = token.getValue();
            scanner.nextToken();
            token = scanner.getToken();
            // Parse the next factor
            factor();
            // Add the operator to the postfix string
            postfix.add(operator);
            // There is no submit here as it will be in expression()
        }
    }

    /*
     * factor -> "(" expression ")" | 
     *           number
     */
    private void factor() {
        // Check if the factor is a parenthesised expression, a number, or an identifier
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
        // Save the number, check the token, then add it to the postfix string
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
