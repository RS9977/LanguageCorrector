# Alex
- Removed stack traces from error messages for the scrawler
- Added displaying number of pages crawled thus far regardless of --stats argument
- Blacklisted assets.tumblr.com
- Added check for seed URLs not starting with "http"
- Added to INSTALL.md:
    - Added some common websites to try crawling
    - Added some instructions for seed URLS
    - Emphasized the reduced amount of URLs available when running without --xl


# Reza
- Update the usuage, workflow, limitations etc. in ReadMe.md and Install.md based on the given feadbacks and clarifying the confusions.
- Change the color range of Checker GUI and add an spectrum of colors using to show the confidence score in a legend.
- Update the accuracy of the checker in GUI and some other options.
- Fix bugs resulted in case of using a special charachter for both checker and corrector.
- Increase the speed of corrector's GUI and fix reported bug for running out of heap memory.
- Fix corrector bugs of not updating the typos in the final correction.
