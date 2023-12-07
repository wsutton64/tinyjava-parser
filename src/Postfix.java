import java.util.ArrayList;

public class Postfix {
    private ArrayList<String> postfixList;
    private String string;

    public Postfix() {
        // Initialize the string and ArrayList
        string = "";
        postfixList = new ArrayList<>();
    }

    public void add(String str) {
        // When called, append string with the passed string. This builds the postfix
        string += (str + " ");
    }

    public void submit() {
        // When called, add the postfix string to postfixList. If the string is empty, skip it
        if (string.equals("")) return;
        postfixList.add(string);
        string = "";
    }

    public ArrayList<String> getList() {
        // Return the ArrayList of postfix notation strings
        return postfixList;
    }
}