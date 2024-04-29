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
- Updated the usuage, workflow, limitations etc. in ReadMe.md and Install.md based on the given feadbacks and clarified the confusions.
- Changed the color range of Checker GUI and add an spectrum of colors using to show the confidence score in a legend.
- Fixed bugs resulted in case of using a special charachter for both checker and corrector.
- Increased the speed of corrector's GUI and fix reported bug for running out of heap memory.
- Fixed corrector bugs of not updating the typos in the final correction.
- Increased accuracy of checker by adding the n-grams in everywhere, previously for some methods it was missed such as in the GUI.
- Increased accuracy of corrector by introducing new method on top of the previous mehtods.
