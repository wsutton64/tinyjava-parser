import java.util.ArrayList;

public class TJScanner {
    private char ch;
    private TJChario chario;
    private ArrayList<Token> tokenList;
    private int tokenIndex;
    private Token currentToken;

    public TJScanner(TJChario chario) {
        // Initialize variables and list
        this.chario = chario;
        this.tokenList = new ArrayList<>();
        this.tokenIndex = 0;
        // Tokenize the input
        chario.putMessage("\nStart tokenizing...");
        tokenize();

        // When complete, notify the user
        chario.putMessage("Tokenizing Complete.");
        if (!findErrors()) {
            // If there are no errors, print the token list
            chario.putMessage("Tokens:\n" + tokenList);
            // Set the first token
            nextToken();
        }
    }

    private void tokenize() {
        // Runs through the entire file and parses tokens
        // This method finds the start of a new token and runs the subsequent parser until a full token is derived. Add the token to the tokenList and move onto the next token
        while (chario.getChar() != 0) {
            // While loops until EoF (null char)
            ch = chario.getChar();
            if (Character.isWhitespace(ch)) {
                // If the current char is whitespace, skip
                chario.nextChar();
            } else if (Character.isLetter(ch) || ch == '_') {
                // If the current char is a letter or _, parse a keyword or indentifier
                tokenList.add(parseKeywordOrIdentifier());
            } else if (Character.isDigit(ch)) {
                // If the current char is a digit, parse a number
                tokenList.add(parseNumber());
            } else {
                // Otherwise it must be an operator, punctuation, or some unknown character
                tokenList.add(parseOperatorOrPunctuation());
            }
        }
        tokenList.add(new Token(Token.TokenType.EOF, "End of File"));
    }

    private boolean findErrors() {
        // Go through all tokens and check if there are any ERROR tokens and add them to errorList
        // If any are found, send an error message and errorList to chario. Set the current token to EoF so the Parser doesnt run.
        // Return true/false if any are found
        boolean errors = false;
        ArrayList<Token> errorList = new ArrayList<>();
        for (Token token : tokenList){
            if (token.getType() == Token.TokenType.ERROR) {
                errorList.add(token);
            }
        }
        if (errorList.size() > 0) {
            errors = true;
            chario.putError("Token errors found: " + errorList);
            currentToken = new Token(Token.TokenType.ERROR, "End of File due to Error");
        }
        return errors;
    }

    private Token parseKeywordOrIdentifier() {
        // Initialize an empty string and TokenType
        String token = "";
        Token.TokenType type;

        // Append the string with the current char and move onto the next char
        token += chario.getChar();
        chario.nextChar();

        // While the current char is a Letter/Digit/_, keep building the string and advancing to the next char
        while (Character.isLetterOrDigit(chario.getChar()) || chario.getChar() == '_') {
            token += chario.getChar();
            chario.nextChar();
        }

        // If the string is "if", "int", or "double", then it is a Keyword. Otherwise it must be an Identifier
        if (token.equals("if") || token.equals("int") || token.equals("double")) {
            type = Token.TokenType.KEYWORD;
        } else {
            type = Token.TokenType.IDENTIFIER;
        }

        // Return a token with the specified type and token string
        return new Token(type, token);
    }

    private Token parseNumber() {
        // Initialize an empty string, token type, and boolean
        String token = "";
        Token.TokenType type = Token.TokenType.NUMBER;
        boolean decimal = false;

        // While the current char is a digit, keep building the string and advancing to the next char
        // If the next char is '.', add it as it is a floating point number. Flag that a decimal has already been added.
        while (Character.isDigit(chario.getChar())) {
            token += chario.getChar();
            chario.nextChar();
            if (chario.getChar() == '.') {
                token += chario.getChar();
                chario.nextChar();
                if (decimal) {
                    type = Token.TokenType.ERROR;
                }
                decimal = true;
            }
        }
        // Return a token with the specified type and token string
        return new Token(type, token);
    }

    private Token parseOperatorOrPunctuation() {
        // Initialize an empty string, type, and get the current char
        String token = "";
        Token.TokenType type;
        char ch = chario.getChar();

        // Append the string with the current char and move onto the next char
        token += ch;
        chario.nextChar();

        // Check if the current char is one of the given operators, then check if it is followed by '='
        if (ch == '>' || ch == '<' || ch == '=' || ch == '!' || ch =='*' || ch =='/' || ch =='%' || ch =='+' || ch =='-') {
            type = Token.TokenType.OPERATOR;
            if (chario.getChar() == '=') {
                token += chario.getChar();
                chario.nextChar();
            }
        } else if (ch == ';' || ch == '(' || ch == ')' || ch == '{' || ch == '}') {
            type = Token.TokenType.PUNCTUATION;
        } else {
            // An unknown character has been found. Give an errored token
            type = Token.TokenType.ERROR;
        }
        
        // Return a token with the specified type and token string
        return new Token(type, token);
    }

    public Token getToken() {
        return currentToken;
    }

    public void nextToken() {
        // Get the next token in tokenList. If it reaches the end of the file, message End of File
        // If token stream ends before EoF, something went wrong.
        if (tokenIndex < tokenList.size()) {
            currentToken = tokenList.get(tokenIndex);
            tokenIndex++;
        } else if (currentToken.getType() == Token.TokenType.EOF) {
            chario.putMessage("End of File reached.");
        } else {
            chario.putError("Unexpected end of tokens");
            throw new RuntimeException("Fatal Error");
        }
    }
}
