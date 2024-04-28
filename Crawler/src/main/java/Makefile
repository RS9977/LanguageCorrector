JAVAC=javac
JFLAGS=-Xlint:none -Xlint:-deprecation
LIBS=tika-app-2.9.2.jar

default: ScratchCrawler.class Debug.class RobotsTXT.class RegexParser.class

ScratchCrawler.class: ScratchCrawler.java
	$(JAVAC) $(JFLAGS) -cp $(LIBS) ScratchCrawler.java

Debug.class: Debug.java
	$(JAVAC) $(JFLAGS) -cp $(LIBS) Debug.java

RobotsTXT.class: RobotsTXT.java
	$(JAVAC) $(JFLAGS) -cp $(LIBS) RobotsTXT.java

RegexParser.class: RegexParser.java
	$(JAVAC) $(JFLAGS) -cp $(LIBS) RegexParser.java

clean:
	rm -f *.class
	del *.class
