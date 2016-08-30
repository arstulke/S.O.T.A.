package sota;

public class SotaHandler {

    public boolean right;
    public boolean left;
    public boolean jump;

    String[][][] map;

    public SotaHandler(){
        map =
    }

    public String handle(boolean[] keys) {
        updateKeysBooleans(keys);


        return right + ", " + left + ", " + jump;
    }

    private void updateKeysBooleans(boolean[] keys) {
        right = keys[0] || keys[1];
        left = keys[2] || keys[3];
        jump = keys[4] || keys[5] || keys[6];
    }
}
