package Translator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DirectTranslator {

    private Map<String, String> wordMap;

    private DirectTranslator() {
        wordMap = new HashMap<>();
    }

    public static DirectTranslator make(){
        return new DirectTranslator();
    }

    public void loadWordMapFromFile(String filePath) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split("\\s+");
                if (words.length == 2) {
                    wordMap.put(words[0], words[1]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String replaceWordsInSentence(String sentence) {
        StringBuilder replacedSentence = new StringBuilder();
        String[] words = sentence.split("(?<=\\p{Punct}|\\s)|(?=\\p{Punct}|\\s)");
        for (String word : words) {
            if (wordMap.containsKey(word)) {
                replacedSentence.append(wordMap.get(word));
            } else {
                replacedSentence.append(word);
            }
        }
        // Remove extra space at the end and ensure no space before punctuation
        return replacedSentence.toString().trim().replaceAll("\\s(?=\\p{Punct})", "");
    }

    public static void main(String[] args) {
        DirectTranslator wordReplacement = new DirectTranslator();
        wordReplacement.loadWordMapFromFile("SQLite/word_map.txt");

        String sentence = "aa, bb! cc dd, ee? ff.";
        String replacedSentence = wordReplacement.replaceWordsInSentence(sentence);
        System.out.println("Original sentence: " + sentence);
        System.out.println("Replaced sentence: " + replacedSentence);
    }
}
