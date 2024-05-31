package me.gravityio.multiline_mastery.helper;

public class Helper {

    public static Integer toInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
