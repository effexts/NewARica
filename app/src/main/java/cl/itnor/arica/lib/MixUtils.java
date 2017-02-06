package cl.itnor.arica.lib;

/**
 * Created by effexts on 1/21/17.
 */

public class MixUtils {
    public static String parseAction(String action){
        return (action.substring(action.indexOf(':') + 1, action.length())).trim();
    }

    public static float getAngle(float center_x, float center_y, float post_x, float post_y) {
        float tmpv_x = post_x - center_x;
        float tmpv_y = post_y - center_y;
        float d = (float) Math.sqrt(tmpv_x*tmpv_x + tmpv_y*tmpv_y);
        float cos = tmpv_x / d;
        float angle = (float) Math.toDegrees(Math.acos(cos));
        return (tmpv_x < 0) ? angle * -1 : angle;
    }

    public static String formatDist(float meters) {
        if (meters < 1000) return ((int) meters) + "m";
        else if (meters < 10000) return formatDec(meters/1000f, 1) + "km";
        else  return ((int) meters/1000f) + "km";
    }

    private static String formatDec(float value, int dec) {
        int factor = (int) Math.pow(10, dec);
        int front = (int) value;
        int back = (int) Math.abs(value * factor) % factor;

        return front + "" + back;
    }
}
