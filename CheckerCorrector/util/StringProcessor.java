package util;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringProcessor {
    public static String processString(String input) {

        input = input.replaceAll("\\[.*?\\]|\\{.*?\\}|\\(.*?\\)\\s*", "");

        input = input.replaceAll("[;:]", ",");

        input = input.replaceAll("\\s+", " ");

        input = input.replaceAll("\\b\\d+\\b", "NUM");

        if (input.matches("^[a-zA-Z, ]+$")) {
            return input;
        } else {
            return "";
        }
    }
}
