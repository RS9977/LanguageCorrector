// This is the main class of the project
// TODO: instantiate the crawler and run it

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class main {
    public static void main(String[] args) {
        // Confiure the crawler
        CrawlConfig config = new CrawlConfig(); // Create a new CrawlConfig object
        config.setCrawlStorageFolder("/src/main/resources/"); // Set the storage folder for the crawler
        config.setPolitenessDelay(1000); // Set the delay between requests to 1000 milliseconds (1 second)
        config.setMaxDepthOfCrawling(100); // Set the maximum depth of crawling to 100
        config.setMaxPagesToFetch(1000); // Set the maximum number of pages to fetch to 1000
        config.setIncludeBinaryContentInCrawling(false); // Set to false to exclude binary content from crawling
        config.setIncludeHttpsPages(true); // Set to true to include HTTPS pages in crawling
        config.setResumableCrawling(false); // Set to false to disable resumable crawling (we can change this later)

        // Instantiate the controller
        PageFetcher pageFetcher = new PageFetcher(config); // Create a new PageFetcher object
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig(); // Create a new RobotstxtConfig object
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher); // Create a new RobotstxtServer object
        CrawlController controller = null; // Declare controller before the try block to make it accessible outside the try block
        try {
            controller = new CrawlController(config, pageFetcher, robotstxtServer); // Initialize controller inside the try block
        } catch (Exception e) {
            System.out.println("Error initializing controller");
            e.printStackTrace();
        }

        // Add seed URLs
        controller.addSeed("https://www.en.wikipedia.com/"); // Add the seed URL to the controller

        int numberOfCrawlers = 7; // Set the number of crawlers to 7

        // Start the crawler
        CrawlController.WebCrawlerFactory<OurCrawler> factory = () -> new OurCrawler(); // Create a new WebCrawlerFactory object
        controller.start(factory, numberOfCrawlers); // Start the controller with the factory and number of crawlers
    }
}
