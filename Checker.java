import java.io.IOException;
import java.util.List;

import DirectedGraph.BasicGraph;
import DirectedGraph.DirectedGraph;
import StateMachine.*;
import DBinterface.DBinterface;
import util.*;

public class Checker {
     public static void main(String[] args) {
        //DirectedGraph<State> graph = new DirectedGraph<>();
        
        ArgumentParser argPars = ArgumentParser.of(args);
        BasicGraph basicGraphClass = new BasicGraph();
        DBinterface dbInterface = new DBinterface();
        DirectedGraph<State> graph = basicGraphClass.getGraph();
        JsonMaker jsonMaker = JsonMaker.create();

        if(argPars.isCheckFile()){
            SentenceExtractor extractor = SentenceExtractor.of(argPars.getFileName());
            List<String> extractedSentences = extractor.getSentences();  
            
            
            for (String sentence : extractedSentences) {
                System.out.println("Sentence: " + sentence);
                jsonMaker.addSentence(sentence, dbInterface.checkTokenInDatabase(sentence, graph));
                System.out.println("*********************************************************");
                PhraseExtractor extractorPhrase = PhraseExtractor.fromSentence(sentence);
                List<String> phrases = extractorPhrase.getPhrases();
                for (String phrase : phrases) {
                    System.out.println("Phrase: "+ phrase);
                    jsonMaker.addPhrase(phrase, dbInterface.checkTokenInDatabase(phrase, graph));
                    System.out.println("------------------------------------------------------------");
                }
                jsonMaker.toJson("data.json");
                System.out.println("##########################################################");
                
            }
        }else if(argPars.isCheckSentence()){

            System.out.println("Sentence: " + argPars.getSentence());
            jsonMaker.addSentence(argPars.getSentence(), dbInterface.checkTokenInDatabase(argPars.getSentence(), graph));
            System.out.println("*********************************************************");
            PhraseExtractor extractorPhrase = PhraseExtractor.fromSentence(argPars.getSentence());
            List<String> phrases = extractorPhrase.getPhrases();
            for (String phrase : phrases) {
                System.out.println("Phrase: " + phrase);
                jsonMaker.addPhrase(phrase, dbInterface.checkTokenInDatabase(phrase, graph));
                System.out.println("------------------------------------------------------------");
            }
            jsonMaker.toJson("data.json");
            System.out.println("##########################################################");
        }
          
    }
}

//javac -d bin Checker.java **/*.java
//java -cp bin:SQLite/sqlite-jdbc-3.45.2.0.jar:SQLite/slf4j-api-1.7.36.jar:SQLite/slf4j-jdk14-1.7.36.jar Checker 
//jar cvfm checker.jar manifest.txt -C bin . -C SQLite .
