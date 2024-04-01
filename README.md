# Group6
Our java webcrawler and langauge checker project. 

Webcrawler by Alex Melnick 

Data Parser by Michael

Language checker and corrector by Reza Sajjadi

## Sources
https://docs.oracle.com/javase/tutorial/networking/urls/index.html

## Libraries
The only library used for the checker and corrector is https://github.com/xerial/sqlite-jdbc and its dependency https://www.slf4j.org. These are both provided in the repo, and there is no need to download them.

## Usage
To run the webcrawler, simply create a ScratchCrawler object and run the .crawl command with the seed URL as the arguement.

`ScratchCrawler crawler = new ScratchCrawler(); // Create a new ScratchCrawler object`

`crawler.crawl("https://archive.org/details/bostonpubliclibrary"); // Start off the crawl with the seed page`

To build the project for CheckerCorrector we are using a make file. running "make dev_corrector" and "make dev_checker" will compile and build the checker.jar and corrector.jar with the user interface requested.

## checker
Our checker is using two different methods to assign confidence points. The first method uses a State Machine and the second is using an n-Grams inspired implementation. The final score will be a weighted sum of these scores provided by two different methods.

### State Machine Checker
In order to have a good working State machine we need to first update the grammar and roles of each word manually. This step will be automatized to some extent in the next milestones. Tokens are provided in CheckerCorrector/SQLite/mydatabase.db, and also the basic graph provided by CheckerCorrector/DirectedGraph/BasicGraph.java. A sentence will first go through a typo checker and get updated if needed (it will also affect the confidence score). Then the sentence will be tokenized, and using the provided graph it will check whether the sentence is following the correct format or not, for each miss on any edge of the graph a penalty will be added to the confidence score.

### n-Grams checker
This checker used the crawled data and gave a score by summing up all the n_grams probabilities of phrases in a sentence.


## Corrector
The current corrector uses the typo corrector which was used in the checker and also the state machine to suggest possible corrections. Corrections of the typo corrector are based on the most similar path through the state machine. In order to find a similar path we are first doing a DFS on the graph starting from the first token and storing all the possible paths. Then, based on the most similar path we decide whether we should change/delete/add a token and suggest a token with a similar role.
The correction at this point is limited to the state machine complexity, but it will be improved for the next milestone.
