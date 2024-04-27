dev_crawler:
	javac -cp tika-app-2.92.jar -d bin src/main/java/*.java
	jar cvfm ScratchCrawler.jar manifestCrawler.txt -C bin .
	echo '#!/bin/bash' > crawler
	echo 'java -jar ScratchCrawler.jar "$$@"' >> crawler
	chmod +x crawler