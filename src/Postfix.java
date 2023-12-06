import java.util.ArrayList;

public class Postfix {
    private ArrayList<String> postfixList;
    private String string;

    public Postfix() {
        string = "";
        postfixList = new ArrayList<>();
    }

    public void add(String str) {
        string += (str + " ");
    }

    public void submit() {
        if (string.equals("")) return;
        postfixList.add(string);
        string = "";
    }

    public ArrayList<String> getList() {
        return postfixList;
    }
}
