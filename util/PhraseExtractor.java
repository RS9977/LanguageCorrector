package util;
import java.util.ArrayList;
import java.util.List;

public class PhraseExtractor {
    private List<String> phrases;

    private PhraseExtractor(List<String> phrases) {
        this.phrases = phrases;
    }

    public static PhraseExtractor fromSentence(String sentence) {
        List<String> phrases = extractPhrases(sentence);
        return new PhraseExtractor(phrases);
    }

    private static List<String> extractPhrases(String sentence) {
        List<String> phrases = new ArrayList<>();
        String[] words = sentence.split("\\s+");
        for (int i = 0; i < words.length; i++) {
            for (int j = i + 2; (j <= 4 & j<=words.length); j++){//words.length; j++) {
                StringBuilder phraseBuilder = new StringBuilder();
                for (int k = i; k < j; k++) {
                    phraseBuilder.append(words[k]);
                    if (k < j - 1) {
                        phraseBuilder.append(" ");
                    }
                }
                phrases.add(phraseBuilder.toString());
            }
        }
        return phrases;
    }

    public List<String> getPhrases() {
        return phrases;
    }
}
