import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser {

    public static void main(String[] args) {
        // Read URLs from a text file
        List<String> urls = readUrlsFromFile("urls.txt");
        List<String> links = new ArrayList<String>();
        List<String> sentences = new ArrayList<String>();

        // Parse each webpage and gather information
        for (String url : urls) {
            try {
                // Fetch the webpage using JSoup
                Document doc = Jsoup.connect(url).get();

                // Extract all the text content from the page
                String allText = getAllText(doc);

                // Find all sentences in the text
                sentences = findSentences(allText);

                // Extract links from the page
               //links = extractLinks(doc);

                // Write sentences and links to a file
                writeToFile(sentences, links, "output_" + getFilenameFromUrl(url) + ".txt");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

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

    // Function to extract all text content from the page
    private static String getAllText(Document doc) {
        return doc.text();
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

    // Function to extract links from the page
    private static List<String> extractLinks(Document doc) {
        List<String> links = new ArrayList<>();
        Elements elements = doc.select("a[href]");
        for (Element element : elements) {
            links.add(element.attr("href"));
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
}