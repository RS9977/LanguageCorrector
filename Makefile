dev:
	javac -d bin Main.java **/*.java
	jar cvfm checker.jar manifest.txt -C bin . -C SQLite .
