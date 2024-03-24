import java.io.FileWriter;
import java.io.IOException;

import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class OurCrawler extends WebCrawler {
    
    // Required by the WebCrawler class
    // Exludes the following file types from being crawled
    private final static Pattern EXCLUSIONS
      = Pattern.compile(".*(\\.(css|js|xml|gif|jpg|png|mp3|mp4|zip|gz|pdf))$");

    // Required by the WebCrawler class
    // Determines if the crawler should visit the page based on exclusions and the linked website
    // In this case, the crawler will only visit pages that are from wikipedia 
    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {
        String href = url.getURL().toLowerCase();
        return !EXCLUSIONS.matcher(href).matches()
            && href.startsWith("https://www.en.wikipedia.com/");
    }

    // Required by the WebCrawler class
    // This method is called when a page is visited
    @Override
    public void visit(Page page) {
        // Get the URL of the page
        String url = page.getWebURL().getURL();
        
        if(Debug.DEBUG)
            System.out.println("URL: " + url);

        // If the page is an HTML page, extract the text, html, and links
        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();      
            String text = htmlParseData.getText(); //extract text from page
            String html = htmlParseData.getHtml(); //extract html from page
            Set<WebURL> links = htmlParseData.getOutgoingUrls();

            if(Debug.DEBUG) {
                System.out.println("------------------------------------");
                System.out.println("URL: " + url);
                System.out.println("Text length: " + text.length());
                System.out.println("Html length: " + html.length());
                System.out.println("Number of outgoing links: " + links.size());
            }

            // // Log the text, html, and links
            // try (FileWriter writer = new FileWriter("crawledData.txt", true)) {
            //     writer.write("------------------------------------\n");
            //     writer.write("URL: " + url + "\n");
            //     writer.write("Text length: " + text.length() + "\n");
            //     writer.write("Html length: " + html.length() + "\n");
            //     writer.write("Number of outgoing links: " + links.size() + "\n");
            //     writer.write("Text: " + text + "\n");
            //     writer.write("Html: " + html + "\n");
            //     writer.write("Outgoing links: " + links + "\n");
            //     writer.close();

            //     if(Debug.DEBUG)
            //         System.out.println("Data successfully written to file");
            // } catch (IOException e) {
            //     System.out.println("An error occurred while writing to the file");
            //     e.printStackTrace();
            // }
        }
    }


}
