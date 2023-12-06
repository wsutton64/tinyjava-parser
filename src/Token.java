public class Token {
   // A Token has a type and a value
    private TokenType type;
    private String value;

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }

    public TokenType getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return type + " " + value;
    }

    public enum TokenType {
        KEYWORD,
        IDENTIFIER,
        NUMBER,
        OPERATOR,
        PUNCTUATION,
        ERROR,
        EOF
   }
}