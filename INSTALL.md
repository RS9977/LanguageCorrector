# Pre-conditions
- Hardware, peripherals, and operating system restrictions for running your code ( i.e., can this code only run on lab machines, or can it run elsewhere?)
    - All code should run on our lab computers, unless you have an exemption from the instructor.
    - If some of your features work more efficiently on specific hardware, please explain this here.
## General Requirements
All CLI modules of this repo (Checker/Corrector/Crawler) requires Java 17 to run. These modules were developed with Unix in mind, and is not guaranteed to work on other OS systems such as Windows or MacOS.

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
- Examples of how to use your project
    - Several clear examples the illustrate the main features of your project.
## Testing Patterns
- Descriptions of testing patterns, and instructions on how to exercise them:
    - unit tests
    - system tests

# Execution

## Crawler
To run the webcrawler, run `./crawler` in the \Crawler\ directory. The usage is as follows:
> Usage: java ScratchCrawler [--file <file_path>] or [--seed <seed_url>] or [--help]
> --file <file_path>: Read URLs from a file and start crawling
> --seed <seed_url>: Start crawling from a seed URL
> --mp <number>: Set the maximum number of pages to crawl
> --timeout <seconds>: Set the timeout for each page in seconds
> --stats: Print statistics during crawling
> --social: Include crawling from Tumblr social media platform
> --dutchDict: Include crawling from Dutch to English dictionary website
> --dutchSeed: Include crawling from Dutch website
> --turkish: Include crawling from Turkish website
> --xl: Increase the storage size per page to 1 MB
> --help: Display this help message

## Checker/Corrector
To build the project for CheckerCorrector we are using a make file. Running `make dev_corrector` and `make dev_checker` will compile and build the `checker.jar` and `corrector.jar` with the user interface requested, and and bash script ready to be run them as `./checker --file [PATH]` and `./corrector --file [PATH]`.

## Android App
The Android App is designed to work on any android device that supports Android 10 and above. For simple installation, an apk file of the app is provided in the repo. Make sure to enable `Install from Unknown Sources` before installing the apk on your device as it may be blocked during the installation process. 

The following are the controls for the app once launched:
- `+` Button: Add a new sentence/Phrase to store in a visual list.
- Short Press on a Phrase: Brings up a dialog showing the output of the checker on the tapped phrase/sentence.
- Long Press on a Phrase: Brings up a dialog to delete the phrase from the stored list.
