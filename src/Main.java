import java.io.FileInputStream;
import java.util.Scanner;

public class Main {
    private TJScanner scanner;
    private TJParser parser;
    private TJChario chario;

    public Main(String[] args) {
        String filePath;
        // Check the arguments
        if (args.length > 1) {
            // User submitted more than just a filepath
            System.out.println("Too many arguments.");
            return;
        } else if (args.length == 1) {
            // User has submitted a filepath
            filePath = args[0];
        } else {
            // User has not submitted a filepath. Prompt user for one.
            Scanner javaScanner = new Scanner(System.in);
            System.out.println("Enter the file location: ");
            filePath = javaScanner.nextLine();
            javaScanner.close();
        }

        FileInputStream input;
        try {
            // Check the filetype and create a new fileinputstream
            checkType(filePath);
            input = new FileInputStream(filePath);
        } catch (Exception e) {
            // If either throw an error, notify the user and exit the program
            System.err.println(e.getMessage());
            return;
        }

        // Initialize chario, scanner, and parser
        chario = new TJChario(input);
        scanner = new TJScanner(chario);
        parser = new TJParser(scanner, chario);
    }

    private void checkType(String filePath) throws Exception {
        if (!filePath.toLowerCase().endsWith(".txt") && !filePath.toLowerCase().endsWith(".java")) {
            // Checks if the filepath DOES NOT end in .txt or .java
            throw new Exception("File does not end in .txt or .java");
        }
    }

    public static void main(String[] args) {
        // Create new Main app object and pass arguments
        new Main(args);
    }
}
