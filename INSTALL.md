# Pre-conditions
- Hardware, peripherals, and operating system restrictions for running your code ( i.e., can this code only run on lab machines, or can it run elsewhere?)
    - All code should run on our lab computers, unless you have an exemption from the instructor.
    - If some of your features work more efficiently on specific hardware, please explain this here.
## General Requirements
All CLI modules of this repo (Checker/Corrector/Crawler) requires Java 17 to run. These modules were developed with Unix in mind, and is not guaranteed to work on other OS systems such as Windows.

On lab machines you are required to load the new version of Java using `source /ad/eng/opt/java/add_jdk17.sh`

## Android App 
When running the android app, the minimum required OS needed to start the app is Android 10.

# Supporting files
## Non-Standard Libraries
- A list of non-standard libraries needed for your project to run, including:
    - Clear and simple instructions for how to freely (and legally!) acquire and install them from source code with minimal effort.
    - You may additionally link to a binary version of the libraries, if you wish.
### SQLite JDBC Driver (sqlite-jdbc)
This is provided in the repo, and there is no need to download the library externally. This is used for communicating with SQLite databases.

### Simple Logging Facade for Java (SLF4J)
This is provided in the repo, and there is no need to download the library externally. This is used for communicating with SQLite databases.

## Example Usage
- Examples of how to use our project
### Checker/Corrector
```
    Corrector:
        `./corrector --file "filename.txt"` -> This will dump the corrected sentence for English of the specidied file to `corrected.txt`
        `./corrector  --sentence "I am a book." -> This will dump the corrected sentence for English of the specidied sentence to `corrected.txt`
        `./corrector --correctorGUI --file filename.txt` -> This will run a GUI where you can see what are the suggestions and choose between them. The file is where it is looking for the text to be corrected.
        `./corrector --correctorGUI --dutch --file filename.txt` -> This will do the same thing as the previous one, but with Dutch. Specifying dutch for the previous ones will also lead to usage of dutch. The file is where it is looking for the text to be corrected.
        `./corrector --translateToDutch --file "filename.txt"` -> This will translate the english text in the file to dutch and put it in `corrected.txt`

    Checker:
        `./checker --file "filename.txt"` -> This will dump the confidence score for English of the specidied file to `confidence_ourChecker.json`
        `./checker --sentence "I am a book." -> This will dump the confidence score for the specidied file to `confidence_ourChecker.json`
        `./checker --checkerGUI ` -> This will run an interactive GUI where you can type and by pushing the buttom it will tell you the confidence.
        `./checker --checkerGUI --dutch` -> This will do the same thing as the previous one, but with Dutch. Specifying dutch for the previous ones will also lead to usage of dutch.
        `./checker --updateToken --file "SQLite/output_Boston.txt"` This will run update the database for tokens which takes a long time.
        `./checker --updateTokenFromDic --file "SQLite/DutchTranslation.txt"` This will run update the database for tokens directly from a parsed dictionary from the crawler. The format should be as it can be seen for any other file.
        `./checker --updateHashTable --file "SQLite/output_Boston.txt"` This will run update the database for n-grams weights.
        `./checker --validateUpdates --file "SQLite/token_database_english_updated.db"` This will run a GUI which will go through the database and check with the user if the updated tokens have a correct role.
```
    
## Testing Patterns
- Descriptions of testing patterns, and instructions on how to exercise them:
    - unit tests
    - system tests

# Execution

## Crawler
To run the webcrawler, run `./crawler` in the \Crawler\ directory. The usage is as follows. Any number of arguments are allowed. 
```
Usage: java ScratchCrawler [--file <file_path>] or [--seed <seed_url>] or [--help]
    --file <file_path>: Read URLs from a file and start crawling
    --seed <seed_url>: Start crawling from a seed URL
    --mp <number>: Set the maximum number of pages to crawl
    --timeout <seconds>: Set the timeout for each page in seconds
    --stats: Print statistics during crawling
    --social: Include crawling from Tumblr social media platform
    --dutchDict: Include crawling from Dutch to English dictionary website
    --dutchSeed: Include crawling from Dutch website
    --turkish: Include crawling from Turkish website
    --xl: Increase the storage size per page to 1 MB
    --help: Display this help message
```


## Checker/Corrector
To build the project for CheckerCorrector we are using a make file. Running `make dev_corrector` and `make dev_checker` will compile and build the `checker.jar` and `corrector.jar` with the user interface requested, and and bash script ready to be run them as `./checker --file [PATH]` and `./corrector --file [PATH]`.
```
Usage:
    Corrector Options:
        --file <filename>: this option should be used if you want to pass your input as file.
        --sentence <sentence>: this option should be used if you want to pass your input as a small sentence.
        --correctorGUI: this option can be used if you want a GUI for the corrector to select between possible suggestions.
        --translateToDutch: this option should be used if you want to translate from English to Dutch. Specify the the filename in txt format or sentence option for this method.
        --translateToEnglish: this option should be used if you want to translate from Dutch to English. Specify the the filename in txt format or sentence option for this method.
        --dutch: this option should be used if you want to use dutch language alongside other options. The default is English.
    Checker Options:
        --file <filename>: this option should be used if you want to pass your input as file.
        -sentence <sentence>: this option should be used if you want to pass your input as a small sentence.
        --checkerGUI: this option can be used if you want a GUI for the checker to see the highlighted sentences. This option is interactive mode.
        --updateToken: this option should be used alongside a file as input to update new tokens for the database from the crawled data. This option may take hours based on the size of crawled file. You must use --file <filename.txt> which has the crawled data for this option as well. The crawled data consists of sentences that is recieved from the crawler. 
        --updateTokenFromDic: this option should be used alongside a file as input to update new tokens for the database from the crawled data from a dictionary website. This option may take hours based on the size of crawled file. You must use --file <filename.txt> which has the crawled data for this option as well. The crawled data consists of words and their roles and their translation as it is crawled by crawler.
        --updateHashTable: this option should be used alongside a file as input to update n-grams weights for the database. This option may take a few minutes. You must use --file <filename.txt> which has the crawled data for this option as well. The crawled data consists of sentences that is recieved from the crawler. 
        --validateUpdates: this option can be used to check the correctness of the database for tokens. This will pops up a window. You must use --file <dbname.db> for this option as well.
        --dutch: this option should be used if you want to use dutch language alongside other options. The default is English.
```

## Android App
The Android App is designed to work on any android device that supports Android 10 and above. For simple installation, an apk file of the app is provided in the repo. Make sure to enable `Install from Unknown Sources` before installing the apk on your device as it may be blocked during the installation process. 

The following are the controls for the app once launched:
- `+` Button: Add a new sentence/Phrase to store in a visual list.
- Short Press on a Phrase: Brings up a dialog showing the output of the checker on the tapped phrase/sentence.
- Long Press on a Phrase: Brings up a dialog to delete the phrase from the stored list.
checkerGUI