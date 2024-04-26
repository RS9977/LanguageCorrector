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
    public static String handleApostrophe(String text) {
        String[][] contractions = {
            {"won't", "will not"},
            {"can't", "cannot"},
            {"isn't", "is not"},
            {"aren't", "are not"},
            {"wasn't", "was not"},
            {"weren't", "were not"},
            {"haven't", "have not"},
            {"hasn't", "has not"},
            {"hadn't", "had not"},
            {"doesn't", "does not"},
            {"don't", "do not"},
            {"didn't", "did not"},
            {"shouldn't", "should not"},
            {"couldn't", "could not"},
            {"mustn't", "must not"},
            {"mightn't", "might not"},
            {"wouldn't", "would not"},
            {"it's", "it is"},
            {"he's", "he is"},
            {"she's", "she is"},
            {"that's", "that is"},
            {"who's", "who is"},
            {"what's", "what is"},
            {"there's", "there is"},
            {"here's", "here is"},
            {"let's", "let us"},
            {"they're", "they are"},
            {"we're", "we are"},
            {"you're", "you are"}

        };
        for (String[] contraction : contractions) {
            text = text.replaceAll("\\b" + contraction[0] + "\\b", contraction[1]);
        }
        String regex = "(\\w+)'s\\s+(\\w+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        StringBuilder result = new StringBuilder();
        int lastEnd = 0;
        while (matcher.find()) {
            result.append(text, lastEnd, matcher.start());
            result.append(matcher.group(2)).append(" of ").append(matcher.group(1));
            lastEnd = matcher.end();
        }
        result.append(text.substring(lastEnd));
        text = result.toString();
        text = text.replaceAll("[\\'’]", "");

        return text;
    }
}
