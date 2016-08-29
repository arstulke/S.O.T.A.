package SOTA;

public class sotaHandler {
    public String handle(boolean right, boolean left, boolean jump) {
        String text = "";
        String a = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa\n";

        for (int i = 0; i < 9; i++) {
            text += a;
        }

        if (right) {
            text = text.replace("a", "b");
        }

        if (left) {
            text = text.replace("a", "c");
        }

        if (jump) {
            text = text.replace("a", "d");
        }

        return text;
    }
}
