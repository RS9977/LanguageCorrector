JAVAC=javac
JFLAGS=-Xlint:none -Xlint:-deprecation

default: ScratchCrawler.class Debug.class RobotsTXT.class RegexParser.class

ScratchCrawler.class: ScratchCrawler.java
	$(JAVAC) $(JFLAGS) ScratchCrawler.java

Debug.class: Debug.java
	$(JAVAC) $(JFLAGS) Debug.java

RobotsTXT.class: RobotsTXT.java
	$(JAVAC) $(JFLAGS) RobotsTXT.java

RegexParser.class: RegexParser.java
	$(JAVAC) $(JFLAGS) RegexParser.java

clean:
	rm -f *.class
	del *.class