package TypoCorrector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class WordCountSorter {
    private String filename;

    private WordCountSorter(String filename) {
        this.filename = filename;
    }

    public static WordCountSorter of(String filename){
        return new WordCountSorter(filename);
    }

    public List<String> getSortedWordsByCount() {
        Map<String, Integer> wordCountMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\s+");
                if (parts.length == 2) {
                    String word = parts[0];
                    int count = Integer.parseInt(parts[1]);
                    wordCountMap.put(word, count);
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        // Sort the map by value (count) in descending order
        List<Map.Entry<String, Integer>> sortedEntries = new ArrayList<>(wordCountMap.entrySet());
        sortedEntries.sort((e1, e2) -> e2.getValue().compareTo(e1.getValue()));

        // Extract sorted words from sorted entries
        List<String> sortedWords = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : sortedEntries) {
            sortedWords.add(entry.getKey());
        }

        return sortedWords;
    }
}
