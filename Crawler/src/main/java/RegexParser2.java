import org.apache.tika.io.IOUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParser2 {
    // Function to read URLs from a text file
    public static void main(String[] args) throws IOException {
        String file = "Absolute\\Path";
        List<String> links = new ArrayList<String>();
        List<String> sentences = new ArrayList<String>();
        FileInputStream inputStream = new FileInputStream(file);
            String all_text = IOUtils.toString(inputStream);
            sentences = findSentences(all_text);
            //links = extractLinks(all_text);
            writeToFile(sentences,links,"output_"+"Dutch2.txt");
    }

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
        String regex = "(?<=<[^>]>)([^<>.!?]*)(?=[.!?])|(?<=^|(?<=[.!?]))\\s+";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(text);
        while (matcher.find()) {
            sentences.add(matcher.group().trim());
        }
        return sentences;
    }

    // Function to extract links from the page
    public static List<String> extractLinks(String text) {
        List<String> links = new ArrayList<>();
        // Regular expression pattern to match URLs
        String urlPattern = "\\b(https?://|-//|www\\.)[-A-Za-z0-9+&@#/%?=~_|!:,.;]*[-A-Za-z0-9+&@#/%=~_|]";
        Pattern pattern = Pattern.compile(urlPattern);
        Matcher matcher = pattern.matcher(text);

        // Find all matches of URLs in the text
        while (matcher.find()) {
            links.add(matcher.group());
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
            Scanner file;
            PrintWriter writes;
            file = new Scanner(new File(filename));
            writes = new PrintWriter("2-" + filename);
            while (file.hasNext()){
                String line = file.nextLine();
                if(!line.isEmpty()){
                    writes.write(line);
                    writes.write("\n");
                }
            }
            file.close();
            writer.close();

            System.out.println("Results written to " + "2-" + filename);
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
