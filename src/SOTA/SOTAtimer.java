package SOTA;

public class SOTAtimer {
    public String handle(boolean right, boolean left, boolean jump) {
        String text = "a";

        if(right){
            text = "b";
        }

        if(left){
            text = "c";
        }

        if(jump){
            text = "d";
        }

        return text;
    }
}
