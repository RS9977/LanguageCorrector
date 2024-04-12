dev_crawler:
	javac -d bin src/main/java/*.java
	jar cvfm ScratchCrawler.jar manifestCrawler.txt -C bin .
	echo '#!/bin/bash' > crawler
	echo 'java -jar ScratchCrawler.jar "$$@"' >> crawler
	chmod +x crawler