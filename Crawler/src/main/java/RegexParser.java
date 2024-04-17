import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser {
    // Function to read URLs from a text file
    private static List<String> readUrlsFromFile(String filename) {
        List<String> urls = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                urls.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return urls;
    }

    // Function to find all sentences in the given text
    private static List<String> findSentences(String text) {
        List<String> sentences = new ArrayList<>();
        Pattern pattern = Pattern.compile("([^?.!]+[?.!])");
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            sentences.add(matcher.group());
        }
        return sentences;
    }

    public static List<String> extractLinks(String text) {
        List<String> links = new ArrayList<>();
        // Regular expression pattern to match URLs
        String urlPattern = "\\b(https?://|-//|www\\.)[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(text);
    
        // Find all matches of URLs in the text
        while (matcher.find()) {
            String url = matcher.group();
            // Trim trailing quotation mark if present
            if (url.endsWith("\"")) {
                url = url.substring(0, url.length() - 1);
            }
            links.add(url);
        }
        return links;
    }

    // Function to write sentences and links to a file
    private static void writeToFile(List<String> sentences, List<String> links, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            writer.write("Sentences found:\n");
            for (String sentence : sentences) {
                writer.write(sentence + "\n");
            }
            writer.write("\nLinks on the page:\n");
            for (String link : links) {
                writer.write(link + "\n");
            }
            System.out.println("Results written to " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Function to get a filename from the URL
    private static String getFilenameFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[parts.length - 1];
    }

    // Parse the Dutch dictionary
    public static String parseDutchDict(String line, int curPart) {
        String entry = null;
    
        // Regular expression pattern to match dictionary entries
        Pattern patternEntry = Pattern.compile("<div class=\"vocab\">(.*?)</div>");
        Matcher matcherEntry = patternEntry.matcher(line);
        if (matcherEntry.find() && curPart == 0) {
            entry = matcherEntry.group(1);
            return entry;
        } else if (matcherEntry.find() && curPart != 0) {
            return "$BAD";
        }
    
        // Regular expression pattern to match parts of speech
        Pattern patternPos = Pattern.compile("<div class=\"pos\">\\[(.*?)\\]</div>");
        Matcher matcherPos = patternPos.matcher(line);
        if (matcherPos.find() && curPart == 1) {
            entry = matcherPos.group(1);
            return entry;
        } else if (matcherPos.find() && curPart != 1) {
            return "$BAD";
        }
    
        // Regular expression pattern to match definitions
        Pattern patternDefinition = Pattern.compile("<div class=\"definition\">\\((.*?)\\)</div>");
        Matcher matcherDefinition = patternDefinition.matcher(line);
        if (matcherDefinition.find() && curPart == 2) {
            entry = matcherDefinition.group(1);
            return entry;
        } else if (matcherDefinition.find() && curPart != 2) {
            return "$BAD";
        }
    
        // Did not find a Duct Word, Part of Speech, or Definition
        return null;
    }
}