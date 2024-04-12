dev_checker:
	javac -d bin Checker.java **/*.java
	jar cvfm checker.jar manifestChecker.txt -C bin . -C SQLite .
	echo '#!/bin/bash' > checker
	echo 'java -jar checker.jar "$$@"' >> checker
	chmod +x checker


dev_corrector:
	javac -d bin Corrector.java **/*.java
	jar cvfm corrector.jar manifestCorrector.txt -C bin . -C SQLite .
	echo '#!/bin/bash' > corrector
	echo 'java -jar corrector.jar "$$@"' >> corrector
	chmod +x corrector