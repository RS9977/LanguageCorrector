import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Iterator;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ScratchCrawler {
    public static int MAX_PAGES = 100; // Maximum pages to crawl
    public static int TIMEOUT = 30; // Timeout for each page in seconds

    public static long waitTime = 200; // Time to wait between requests in milliseconds
    public static int totalSize = 0; // Total size of pages visited in bytes
    public static double processingRatePages = 0; // Processing rate in pages per second (inverse waitTime)
    public static double processingRateLinks = 0; // Processing rate in links per second
    public static double processingRateSize = 0; // Processing rate in bytes per second
    public static boolean printStats = false; // Print stats flag
    public static int max_storage = 1000; // Maximum storage size per page in bytes

    // Using a HashSet to store visited pages and pages to visit. This is the best data structure 
    // for this use case because it has O(1) time complexity for add, remove, and contains 
    // operations and enforces uniqueness (we do not want to crawl pages more than once). 
    // Also we do not care about the order of the pages.
    public static Set<String> pagesVisited = new HashSet<String>(); // Set to store visited pages
    public static Set<String> pagesToVisit = new HashSet<String>(); // Set to store pages to visit
    public static Set<String> disallowedDomains = new HashSet<String>(); // Set to store disallowed domains
    public static Set<String> whitelist = new HashSet<String>(Arrays.asList( // Set to store whitelisted domains
        // Add whitelisted domains here   
        //"https://www.usenetarchives.com" // Our social media platform for crawling (approved by Prof. Trachtenberg)
    ));


    // In order to store the robots.txt restrictions, we are going to use a HashMap with the domain 
    // as the key and an object representing the restrictions as the value. This is the best data
    // structure for this use case because it allows us to quickly look up the restrictions for a
    // given domain.
    public static HashMap<String, RobotsTXT> visitedRobotsTXTs = new HashMap<String, RobotsTXT>(); // Map to store robots.txt restrictions

    public String getNextPage() {
        String nextPage = null; // Initialize nextPage to null
        Iterator<String> it = pagesToVisit.iterator(); // Create an iterator for pagesToVisit
        
        if (!pagesToVisit.isEmpty()) { // If there are pages to visit
            nextPage = it.next(); // Get the next page
            pagesToVisit.remove(nextPage); // Remove the page from pagesToVisit
            pagesVisited.add(nextPage); // Add the page to pagesVisited
        } else {
            if (printStats) System.out.println("No more pages to visit."); // Print message
        }

        return nextPage; // Return the next page
    }

    // https://docs.oracle.com/javase/tutorial/networking/urls/index.html
    public static void getPage(String url) {
        // Code to get the page

        // Provide real-time status and statistics feedback for the crawler
        if (printStats) {
            System.out.println("Processing URL: " + url); 
        }

        // REMOVE ME - fixing RegexParser.extractLinks() to handle URLs ending with ')'
        // if (url.endsWith(")")) {
        //     url = url.substring(0, url.length() - 1);
        // }
        
        try {
            URL pageURL = new URL(url); // Create a new URL object
            String domain = extractDomain(url); // Extract the domain from the URL

            HttpURLConnection connection = (HttpURLConnection) pageURL.openConnection();
            // Set the User-Agent header
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            int responseCode = connection.getResponseCode();

            if (responseCode != HttpURLConnection.HTTP_OK) {
                if (printStats) System.out.println("Error reading page. Response code: " + responseCode);

                // If the page is not found, add the domain to the disallowedDomains set
                disallowedDomains.add(domain);

                if (Debug.DEBUG)
                    System.out.println("Adding domain to disallowedDomains: " + domain); // Print message

                return;
            }
            
            // Check if url is allowed by robots.txt
            if (isInVisitedRobotsTxt(domain)) { // If the URL is in the visited robots.txt
                RobotsTXT robotsTXT = visitedRobotsTXTs.get(domain); // Get the RobotsTXT object for the URL
                if (robotsTXT.getDisallowedPaths().contains(url) || disallowedDomains.contains(domain)) { // If the URL is disallowed
                    if(printStats) System.out.println("URL is disallowed"); // Print message
                    return; // Exit the method
                } else {
                    // URL is allowed
                    // Update the wait time based on the crawl delay
                    waitTime = robotsTXT.getCrawlDelay() * 1000; // Update the wait time based on the crawl delay                       
                }
            } else {
                parseRobotsTXT(url); // Parse the robots.txt file

                // 
            }

            // Update the wait time based on the crawl delay
            if (visitedRobotsTXTs.containsKey(url)) { // If the URL is in the visited robots.txt
                RobotsTXT robotsTXT = visitedRobotsTXTs.get(url); // Get the RobotsTXT object for the URL
                waitTime = robotsTXT.getCrawlDelay() * 1000; // Update the wait time based on the crawl delay
            }
            
            // Code to read the page
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageURL.openStream())); // Create a new BufferReader object
            PrintWriter writer = new PrintWriter(new FileWriter("crawledData.txt",true)); // Create a new PrintWriter object
            
            String line; // Declare a string to store each line of the page
            StringBuilder pageContent = new StringBuilder(); // To store the page content

            int linksExtracted = 0; // Number of links extracted from the page
            while ((line = reader.readLine()) != null && pageContent.length() < max_storage) { // While there are lines to read
                pageContent.append(line); // Add the line to the page content
                
                // Extract links from the page
                List<String> links = RegexParser.extractLinks(line); // Extract the links from the line
                for (String link : links) { // For each link
                    if (!pagesVisited.contains(link)) { // If the link has not been visited
                        pagesToVisit.add(link); // Add the link to pagesToVisit
                        linksExtracted++; // Increment the number of links extracted
                    }
                }
            }
            // Write the page content to the file, up to storage limit
            writer.println(pageContent.toString().substring(0, Math.min(max_storage, pageContent.length())));      

            // Provide real-time status and statistics feedback for the crawler
            totalSize += pageContent.length();
            processingRatePages = 1000 / (double)waitTime; // Processing rate in pages per second (inverse waitTime)
            processingRateLinks = linksExtracted * processingRatePages; // Processing rate in links per second
            processingRateSize = pageContent.length() * processingRatePages; // Processing rate in bytes per second

            if (printStats) {
                System.out.println("Length of page processed [Bytes]: " + pageContent.length());
                System.out.println("Total size of pages visited [Bytes]: " + totalSize);
                System.out.println("Number of links extracted: " + linksExtracted);
                System.out.println("Number of pages crawled  (" + MAX_PAGES + " pages max): " + pagesVisited.size());
                System.out.println("URLs available to crawl: " +  pagesToVisit.size());
                System.out.println("Processing rate in pages per second: " + processingRatePages);
                System.out.println("Processing rate in links per second: " + processingRateLinks);
                System.out.println("Processing rate in bytes per second: " + processingRateSize);
            }
           

            writer.close(); // Close the writer
            reader.close(); // Close the reader

        } catch (MalformedURLException e) {
            if(printStats) {
                System.out.println("Error creating URL object.");
                e.printStackTrace();
            }
            return; // Exit the method
        } catch (IOException e) {
            if(printStats) {
                System.out.println("Error reading page.");
                e.printStackTrace();
            }
            return; // Exit the method
        } 

    }

    public static void parseRobotsTXT(String url) {
        // Code to parse robots.txt
        if (Debug.DEBUG)
            System.out.println("Parsing robots.txt for: " + url); // Print message

        // Extract the domain from the URL
        String domain = extractDomain(url);

        // Create the RobotsTXT object
        url = domain + "/robots.txt"; // Append /robots.txt to the domain

        RobotsTXT robotsTXT = new RobotsTXT(url); // Create a new RobotsTXT object
        if (Debug.DEBUG)
            System.out.println("RobotsTXT object created for: " + url); // Print message

        // Fetch the robots.txt file
        try {
            if (Debug.DEBUG)
                System.out.println("Fetching robots.txt file for: " + url); // Print message

            URL pageURL = new URL(url); // Create a new URL object
            
            // Code to read the page
            BufferedReader reader = new BufferedReader(new InputStreamReader(pageURL.openStream())); // Create a new BufferReader object
            //PrintWriter writer = new PrintWriter("src/main/resources/crawledData.txt"); // Create a new PrintWriter object
            String line; // Declare a string to store each line of the page
            while ((line = reader.readLine()) != null) { // While there are lines to read
                //writer.println(line); // Write the line to the file
                // if (Debug.DEBUG)
                //     System.out.println(line); // Print the line
                
                if (line.startsWith("User-agent: *")) {
                    // Read lines until next user agent
                    while ((line = reader.readLine()) != null && !line.startsWith("User-agent:")) {
                        if (line.startsWith("Disallow: ")) {
                            // parse Disallow
                            robotsTXT.addDisallowedPath(line.substring(10)); // Add the disallowed path to the RobotsTXT object 
                            
                            if (Debug.DEBUG_RobotsTXT)
                                System.out.println(line); // Print message
                        } else if (line.startsWith("Allow: ")) {
                            // parse Allow
                            robotsTXT.addAllowedPath(line.substring(7)); // Add the allowed path to the RobotsTXT object
                            
                            if (Debug.DEBUG_RobotsTXT)
                                System.out.println(line); // Print message
                        } else if (line.startsWith("Crawl-delay: ")) {
                            // parse Crawl-delay
                            int delay = Integer.parseInt(line.substring(13)); // Parse the crawl delay
                            robotsTXT.setCrawlDelay(delay); // Set the crawl delay

                            if (Debug.DEBUG_RobotsTXT)
                                System.out.println(line); // Print message
                        }
                    }
                }
            }   

            reader.close(); // Close the reader
        } catch (MalformedURLException e) {
            if(printStats) {
                System.out.println("Error creating URL object for robots.txt file.");
                e.printStackTrace();
            }
            return; // Exit the method
        } catch (IOException e) {
            if (Debug.DEBUG_RobotsTXT) {
                System.out.println("Error fetching robots.txt file.");
                e.printStackTrace();
            }
            
            // If the robots.txt file is not found, add the domain to the disallowedDomains set
            disallowedDomains.add(domain);
        }

        // Store the RobotsTXT object in the visitedRobotsTXTs map
        visitedRobotsTXTs.put(domain, robotsTXT); // Add the RobotsTXT object to the map
    }

    public static boolean isInVisitedRobotsTxt(String url) {
        // Code to check if URL is in visited robots.txt
        if (Debug.DEBUG)
            System.out.println("Checking if URL is in visited robots.txt: " + url); // Print message

        // Extract the domain from the URL
        String domain; 
        Pattern pattern = Pattern.compile("((http://|https://)?[^:/]+)"); // Create a pattern to match the domain
        Matcher matcher = pattern.matcher(url); // Create a matcher for the pattern
        if (matcher.find()) {
            domain = matcher.group(1); // Obtain the domain and TLD, including the protocol
            if (Debug.DEBUG)
                System.out.println("Domain: " + domain); // Print the domain
        } else {
            if (printStats) System.out.println("Error extracting domain from URL.");
            return false; // Exit the method
        }

        // Check if the domain is in the visitedRobotsTXTs map
        if (visitedRobotsTXTs.containsKey(domain)) { // If the domain is in the map
            if (Debug.DEBUG)
                System.out.println("Domain is in visited robots.txt: " + domain); // Print message
            return true; // Return true
        } else {
            if (Debug.DEBUG)
                System.out.println("Domain is not in visited robots.txt: " + domain); // Print message
            return false; // Return false
        }
    }

    public static boolean allowedToCrawl(String url) {
        // Code to check if allowed to crawl
        if (Debug.DEBUG)
            System.out.println("Checking if allowed to crawl: " + url); // Print message

        // Extract the domain from the URL
        String domain = extractDomain(url); // Set the domain to the URL for now

        // Check if the domain is in the whitelist
        if (whitelist.contains(domain)) {
            return true;
        }
        
        // Check if the domain is in the visitedRobotsTXTs map
        if (visitedRobotsTXTs.containsKey(domain)) { // If the domain is in the map
            RobotsTXT robotsTXT = visitedRobotsTXTs.get(domain); // Get the RobotsTXT object for the domain
            if (robotsTXT.getDisallowedPaths().contains(url.replaceFirst(domain, ""))) { // If the URL is disallowedif (robotsTXT.getDisallowedPaths().contains(domain - url)) { // If the URL is disallowed
                if (Debug.DEBUG)
                    System.out.println("URL is disallowed by robots.txt: " + url); // Print message
                return false; // Return false
            } else {
                // URL is allowed
                return true; // Return true
            }
        } 
        else {
            parseRobotsTXT(url); // Parse the robots.txt file
            return allowedToCrawl(url); // Rerun the method
        }
    }

    public static String extractDomain(String url) {
        // Extract the domain from the URL
        String domain; 
        Pattern pattern = Pattern.compile("((http://|https://)?[^:/]+)"); // Create a pattern to match the domain
        Matcher matcher = pattern.matcher(url); // Create a matcher for the pattern
        if (matcher.find()) {
            domain = matcher.group(1); // Obtain the domain and TLD, including the protocol
            if (Debug.DEBUG)
                System.out.println("Domain: " + domain); // Print the domain
        } else {
            if (printStats) System.out.println("Error extracting domain from URL.");
            return url; // Exit the method
        }

        return domain; // Return the domain
    }

    // Unused in this version of the crawler
    // public void crawl(String seed) {
    //     pagesToVisit.add(seed); // Add the seed page to pagesToVisit

    //     while (pagesVisited.size() < MAX_PAGES && !pagesToVisit.isEmpty()) { // While the number of visited pages is less than MAX_PAGES
    //         String nextPage = getNextPage(); // Get the next page
    //         try {
    //             Thread.sleep(waitTime); // Wait to be polite
    //             getPage(nextPage); // Get the page
    //         } catch (InterruptedException e) {
    //             System.out.println("Error waiting between crawling pages.");
    //             e.printStackTrace();
    //         } 
    //     }

    //     System.out.println("Crawling complete."); // Print message
    // }
    public void crawl() {
        while (pagesVisited.size() < MAX_PAGES && !pagesToVisit.isEmpty()) { // While the number of visited pages is less than MAX_PAGES
            String nextPage = getNextPage(); // Get the next page
            
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<?> future = executor.submit(() -> {
                try {
                    Thread.sleep(waitTime); // Wait to be polite
                    getPage(nextPage); // Get the page
                } catch (InterruptedException e) {
                    System.out.println("Error waiting between crawling pages.");
                    e.printStackTrace();
                }
            });
            try {
                future.get(TIMEOUT, java.util.concurrent.TimeUnit.SECONDS); // Set a timeout of 30 seconds for each page
            } catch (Exception e) {
                future.cancel(true); // Cancel the future
                if (printStats) System.out.println("Page read timed out. Read took longer than " + TIMEOUT + " seconds.");
            }
            executor.shutdownNow();
        }

        System.out.println("Crawling complete."); // Print message
    }

    public void readURLsFromFile(String filePath) {
        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {
            List<String> urls = stream.collect(Collectors.toList()); // Convert the stream to a list
            for (String url : urls) { // Iterate over the list
                pagesToVisit.add(url);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + filePath);
            e.printStackTrace();
        }
    }


    // For testing purposes
    public static void main(String[] args) {

        ScratchCrawler crawler = new ScratchCrawler(); // Create a new ScratchCrawler object
        //crawler.crawl("https://archive.org/details/bostonpubliclibrary"); // Start off the crawl with the seed page

        boolean startCrawl = false; // Flag to start the crawl
        String seed = ""; // Seed URL

        // Parse command-line arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--file":
                    if (i + 1 < args.length) {
                        String filePath = args[++i];
                        crawler.readURLsFromFile(filePath);
                        startCrawl = true;
                    } else {
                        System.out.println("Missing file path after --file");
                    }
                    break;
                case "--seed":
                    if (i + 1 < args.length) {
                        seed = args[++i];
                        crawler.pagesToVisit.add(seed);
                        startCrawl = true;
                    } else {
                        System.out.println("Missing seed URL after --seed");
                    }
                    break;
                case "--mp":
                    if (i + 1 < args.length) {
                        MAX_PAGES = Integer.parseInt(args[++i]);
                    } else {
                        System.out.println("Missing number of pages after --mp");
                    }
                    break;
                case "--timeout":
                    if (i + 1 < args.length) {
                        TIMEOUT = Integer.parseInt(args[++i]);
                    } else {
                        System.out.println("Missing timeout value after --timeout");
                    }
                    break;
                case "--stats":
                    printStats = true;
                    break;
                case "--social": 
                    // Extension of our crawler to crawling social media posts of some large network (in this case Tumblr)
                    crawler.pagesToVisit.add("https://www.tumblr.com/");
                    startCrawl = true;
                    break;
                case "--dutch":
                    // Extension of our crawler with an English to Dutch translation
                    crawler.pagesToVisit.add("https://travelwithlanguages.com/blog/most-common-dutch-words.html");
                    startCrawl = true;
                    break;
                case "--xl":
                    // Allows our crawler to save 1 MB of data per page instead of 1 KB
                    max_storage = 1000000; // 1 million bytes (1 MB)
                    break;
                case "--help":
                    System.out.println("Usage: java ScratchCrawler [--file <file_path>] or [--seed <seed_url>] or [--help]");
                    System.out.println("--file <file_path>: Read URLs from a file and start crawling");
                    System.out.println("--seed <seed_url>: Start crawling from a seed URL");
                    System.out.println("--mp <number>: Set the maximum number of pages to crawl");
                    System.out.println("--timeout <seconds>: Set the timeout for each page in seconds");
                    System.out.println("--stats: Print statistics during crawling");
                    System.out.println("--social: Include crawling from Tumblr social media platform");
                    System.out.println("--dutch: Include crawling from Dutch translation website");
                    System.out.println("--xl: Increase the storage size per page to 1 MB");
                    System.out.println("--help: Display this help message");
                    break;
                default:
                    System.out.println("Invalid argument: " + args[i] + ". Use --help for usage information.");
                    break;
            }
        }

        if (startCrawl) {
            crawler.crawl(); // Start the crawl
        } 
        else {
            System.out.println("No seed URL(s) provided. Use --help for usage information.");
        }

    }
}
