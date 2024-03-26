dev:
	javac -d bin Checker.java **/*.java
	jar cvfm checker.jar manifest.txt -C bin . -C SQLite .
