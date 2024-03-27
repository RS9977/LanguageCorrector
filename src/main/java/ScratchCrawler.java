import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class ScratchCrawler {
    public static final int MAX_PAGES = 100; // Maximum pages to crawl
    public static final long waitTime = 200; // Time to wait between requests in milliseconds

    // Using a HashSet to store visited pages and pages to visit. This is the best data structure 
    // for this use case because it has O(1) time complexity for add, remove, and contains 
    // operations and enforces uniqueness (we do not want to crawl pages more than once). 
    // Also we do not care about the order of the pages.
    public Set<String> pagesVisited = new HashSet<String>(); // Set to store visited pages
    public Set<String> pagesToVisit = new HashSet<String>(); // Set to store pages to visit

    public String getNextPage() {
        String nextPage = null; // Initialize nextPage to null
        Iterator<String> it = pagesToVisit.iterator(); // Create an iterator for pagesToVisit
        
        if (!pagesToVisit.isEmpty()) { // If there are pages to visit
            nextPage = it.next(); // Get the next page
            pagesToVisit.remove(nextPage); // Remove the page from pagesToVisit
            pagesVisited.add(nextPage); // Add the page to pagesVisited
        } else {
            System.out.println("No more pages to visit."); // Print message
        }

        return nextPage; // Return the next page
    }

    // https://docs.oracle.com/javase/tutorial/networking/urls/index.html
    public void getPage(String url) {
        // Code to get the page
        if(Debug.DEBUG)
            System.out.println("Getting page: " + url); // Print message

        try {
            URL pageURL = new URL(url); // Create a new URL object
            
            // Code to read the page
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageURL.openStream())); // Create a new BufferReader object
            PrintWriter writer = new PrintWriter("src/main/resources/crawledData.txt"); // Create a new PrintWriter object
            String line; // Declare a string to store each line of the page
            
            while ((line = reader.readLine()) != null) { // While there are lines to read
                writer.println(line); // Write the line to the file

                // Code to extract links from the page
                //line.extractURLs(); // Extract URLs from the line
                // Should extract URLS from the line, add them to pagesToVisit, and print them to a file
            }
            writer.close(); // Close the writer
            reader.close(); // Close the reader

        } catch (MalformedURLException e) {
            System.out.println("Error creating URL object.");
            e.printStackTrace();
            return; // Exit the method
        } catch (IOException e) {
            System.out.println("Error reading page.");
            e.printStackTrace();
            return; // Exit the method
        } 

    }

    public void crawl(String seed) {
        pagesToVisit.add(seed); // Add the seed page to pagesToVisit

        while (pagesVisited.size() < MAX_PAGES && !pagesToVisit.isEmpty()) { // While the number of visited pages is less than MAX_PAGES
            String nextPage = getNextPage(); // Get the next page
            try {
                Thread.sleep(waitTime); // Wait to be polite
                getPage(nextPage); // Get the page
            } catch (InterruptedException e) {
                System.out.println("Error waiting between crawling pages.");
                e.printStackTrace();
            } // 
        }

        System.out.println("Crawling complete."); // Print message
    }


    // For testing purposes
    public static void main(String[] args) {
        // Test the getNextPage method
        ScratchCrawler crawler = new ScratchCrawler(); // Create a new ScratchCrawler object
        crawler.crawl("https://en.wikipedia.com/"); // Start off the crawl with the seed page
    }
}
