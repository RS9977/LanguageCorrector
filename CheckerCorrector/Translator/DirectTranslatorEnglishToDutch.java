package Translator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DirectTranslatorEnglishToDutch {

    private Map<String, String> wordMap;

    private DirectTranslatorEnglishToDutch() {
        wordMap = new HashMap<>();
    }

    public static DirectTranslatorEnglishToDutch make(){
        return new DirectTranslatorEnglishToDutch();
    }

    public void loadWordMapFromFile(String filePath) {
        //System.out.println("filePath");
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            
            String line;
            while ((line = br.readLine()) != null) {
                String[] words = line.split("\\s+");
                if (words.length > 2) {
                    if(words[2].equals("to") && words.length>3){
                        wordMap.put(words[3], words[0]);
                    }else{
                        wordMap.put(words[2], words[0]);
                    }
                }
            }
           // System.out.println(wordMap.size());
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
}
