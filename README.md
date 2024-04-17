# Group 6 - Language Correction
## Documentation
### Summary
Our group aims to develop a Language Correction software that efficiently analyzes text for correct language usage by crawling through pages of the target language(s) on the Internet. The evaluator will compile common usage patterns of words and phrases from crawled texts and use them as a reference to identify words or phrases that are consistent or inconsistent with the gathered data. This project will assist users in improving their language skills and ensuring the accuracy of their written content.

### Credits
Alex Melnick 

Michael Harkess

Reza Sajjadi

### Implementation Description
A high-level description of the implementation, with particular emphasis on the design decisions related to data structures and algorithms

### Implemented Features
- Provide real-time status and statistics ( e.g., what URL is being processed, rate of processing, size of storage, etc.) feedback for the crawler. [10%]
    - Implemented by calculating statistics as the crawler crawls each page. We report the length of the page processed in bytes, the total amount of data crawled thus far, the number of links extracted from the page, the number of pages crawled, the number of URLs left available to crawl, the current crawling rate in pages per second, the current crawling rate in links extracted per second, and the current crawling rate in bytes per second. Usage: use "--stats" argument for the crawler.  
- Provide a list of reasonable corrections to a suspicious text, ranked in order of how different they are from the original text. [15%]
    - Description of how it was implemented with an emphasis on data structures and algorithms used
- Graphical User Interface that highlights suspicious and non-suspicious textual elements in a given text. [15%]
    - Description of how it was implemented with an emphasis on data structures and algorithms used
- Extend your crawler to crawling social media posts of some large network ( e.g., Twitter, Facebook, LinkedIn, Truth, MeWe…). [15%]
    - Implemented by adding a social media platform as a seed to our crawler. We are using Tumblr as our social media platform. It is considered a major platform with more than 500 million monthly users. Choosing a social media platform was difficult since most platforms no longer allow crawling as a safeguard against their data being used to train LLMs. After careful examination of possible sites, we decided that Tumblr was the best choice that still allowed crawlers. Usage: use "--social" argument for the crawler.
- Provide a graphical human feedback system for deciding among possible phrase corrections, with feedback into the suspicion levels reported by the system. [15%]
    - Description of how it was implemented with an emphasis on data structures and algorithms used
- Develop and Android client for your checker. [15%]
    - For the most part, the Android client of the checker is interfacing with the Checker CLI tool to produce its output in a pop-up window on the app. To do this, the Checker tool was modified so that it did not write the checked result to a JSON file (and instead kept it as a JSON string) as well as not require CLI arguments for the script to function. In addition, helper class functions were used to display the output to the user in the app.
- Extend your system to a language in which none of the team members have fluency. [15% per language, up to 3] - 1x
    - We added the ability to crawl Dutch websites by adding a Dutch webpage seed to our crawler. Usage: use "--dutchSeed" argument for the crawler. Additional Dutch websites to crawl can be specified by adding their URLs to a file and using the "--file [filename.txt]" argument for the crawler. 
- Provide a reasonable translation from English to another language based on common language structures. [30%] 
    - We added the ability to translation from Dutch to English and from English to Dutch. We accomplished this by crawling a Dutch to English online dictionary and translating the words literally. Usage: use ""--dutchDict" argument with crawler to re-crawl the dictionary. 

#### Changed Features from Initial Defense Report

##### Added
- Feature 1

##### Removed
- Feature 1

### References
https://docs.oracle.com/javase/tutorial/networking/urls/index.html

## Code
- Most Updated (Stable) Branch: Master 
- Folder with All data needed to run repo:
- Folder with All testing Code:

## Work Breakdown
Webcrawler, Webcrawler stats, Webcrawler social media, Extend system to other language (partial), Provide a reasonable translation (partial) by Alex Melnick 

Data Parser, Android App by Michael Harkess

Language checker and corrector by Reza Sajjadi

## Advanced Technical Information

### How it Works
#### Checker
Our checker is using two different methods to assign confidence points. The first method uses a State Machine and the second is using an n-Grams inspired implementation. The final score will be a weighted sum of these scores provided by two different methods.

##### State Machine Checker
In order to have a good working State machine we need to first update the grammar and roles of each word manually. This step will be automatized to some extent in the next milestones. Tokens are provided in `CheckerCorrector/SQLite/mydatabase.db`, and also the basic graph provided by `CheckerCorrector/DirectedGraph/BasicGraph.java`. A sentence will first go through a typo checker and get updated if needed (it will also affect the confidence score). Then the sentence will be tokenized, and using the provided graph it will check whether the sentence is following the correct format or not, for each miss on any edge of the graph a penalty will be added to the confidence score.
The typo corrector is calculating the distance of a word to dictionary(supposly provided by crawler). Here we used the small dictionary of homewrok2, and replace the misspelled word with closest distant word with some condition.

##### n-Grams checker
This checker used the crawled data and gave a score by summing up all the n_grams probabilities of phrases in a sentence. In order to store the data of the crawled data we used SHA-256 hash and store the result in CheckerCorrector/SQLite/hash_database.db.

#### Proof of Effictiveness
In order to show the effictiveness of our tool we used ChatGBT to write a script that is using a third-party Python library to make the exact same score for each sentence. These can be found in CheckerCorrector/samples/ directory. Both Json generated by our tool and the third party are available.

#### Corrector
The current corrector uses the typo corrector which was used in the checker and also the state machine to suggest possible corrections. Corrections of the typo corrector are based on the most similar path through the state machine. In order to find a similar path we are first doing a DFS on the graph starting from the first token and storing all the possible paths. Then, based on the most similar path we decide whether we should change/delete/add a token and suggest a token with a similar role.
The correction at this point is limited to the state machine complexity, but it will be improved for the next milestone.

### Known Issues/Bugs
#### Android App
- If a dialog box pops up and the device/screen is rotated, then the app will unexpectedly crash
- The confidence score of every word in a given sentence/phrase is 0
