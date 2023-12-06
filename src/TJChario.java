import java.io.*;

public class TJChario {
    private InputStream input;
    private char currentChar;

    public TJChario(InputStream input) {
        this.input = input;
        nextChar(); // Advance to get the first char
    }

    public char getChar() {
        // Returns the current char
        return currentChar;
    }

    public void nextChar() {
        // When called, advance to the next char. If the next char is EoF/returns -1, set currentChar to null character
        try {
            int nextChar = input.read();
            if (nextChar != -1) {
                currentChar = (char) nextChar;
            } else {
                currentChar = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void putMessage(String message) {
        System.out.println(message);
    }
    public void putError(String error) {
        System.err.println(error);
    }
}