package util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JsonMaker {
    private Map<String, Integer> sentences;
    private Map<String, Integer> phrases;

    private JsonMaker() {
        sentences = new HashMap<>();
        phrases = new HashMap<>();
    }

    public void addSentence(String sentence, int value) {
        sentences.put(sentence, value);
    }

    public void addPhrase(String phrase, int value) {
        phrases.put(phrase, value);
    }

    public void toJson(String fileName) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n  \"sentences\": {\n");
        appendMapToJson(sentences, jsonBuilder);
        jsonBuilder.append("  },\n  \"phrases\": {\n");
        appendMapToJson(phrases, jsonBuilder);
        jsonBuilder.append("  }\n}");

        String jsonString = jsonBuilder.toString();

        // Write JSON string to file
        try (FileWriter writer = new FileWriter(fileName)) {
            writer.write(jsonString);
            System.out.println("JSON file created successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void appendMapToJson(Map<String, Integer> map, StringBuilder jsonBuilder) {
        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            jsonBuilder.append("    \"").append(entry.getKey()).append("\": ").append(entry.getValue()).append(",\n");
        }
        // Remove the last comma and newline character
        if (!map.isEmpty()) {
            jsonBuilder.delete(jsonBuilder.length() - 2, jsonBuilder.length());
        }
        jsonBuilder.append('\n');
    }

    public static JsonMaker create() {
        return new JsonMaker();
    }

    public static void main(String[] args) {
        JsonMaker jsonMaker = JsonMaker.create();
        jsonMaker.addSentence("This is strange so choice", 55);
        jsonMaker.addPhrase("strange so choice", 70);
        jsonMaker.addPhrase("so choice word", 80);
        jsonMaker.addPhrase("This is", 0);
        jsonMaker.addPhrase("This is strange", 0);
        jsonMaker.addPhrase("strange so", 10);

        jsonMaker.toJson("data.json");
    }
}
