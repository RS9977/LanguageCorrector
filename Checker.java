import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import HashTableMaker.HashTableMaker;
import DirectedGraph.BasicGraph;
import DirectedGraph.DirectedGraph;
import HashTableMaker.HashTableMaker;
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
        if(argPars.isUpdateHashTable()){
            if(argPars.isCheckFile()){
                SentenceExtractor extractor = SentenceExtractor.of(argPars.getFileName());
                List<String> extractedSentences = extractor.getSentences();  
                
                try {
                    HashTableMaker manager = new HashTableMaker();
                    for (String sentence : extractedSentences) {
                        manager.updateDatabase(sentence.toLowerCase());
                        PhraseExtractor extractorPhrase = PhraseExtractor.fromSentence(sentence);
                        List<String> phrases = extractorPhrase.getPhrases();
                        for (String phrase : phrases) {
                            manager.updateDatabase(phrase.toLowerCase());
                        }                        
                    }
                    manager.closeConnection();
                } catch (SQLException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }else if(argPars.isCheckSentence()){
                try {
                    HashTableMaker manager = new HashTableMaker();
                    manager.updateDatabase(argPars.getSentence().toLowerCase());
                    for (String phrase : PhraseExtractor.fromSentence(argPars.getSentence()).getPhrases()) {
                        manager.updateDatabase(phrase.toLowerCase());                      
                    }
                    manager.closeConnection();
                } catch (SQLException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }else if(argPars.isCheckFile()){
            SentenceExtractor extractor = SentenceExtractor.of(argPars.getFileName());
            List<String> extractedSentences = extractor.getSentences();  
            try {
                HashTableMaker manager = new HashTableMaker();
                for (String sentence : extractedSentences) {
                    System.out.println("Sentence: " + sentence);
                    
                    System.out.println("*********************************************************");
                    PhraseExtractor extractorPhrase = PhraseExtractor.fromSentence(sentence);
                    List<String> phrases = extractorPhrase.getPhrases();
                    int score = 0;
                    jsonMaker.addSentence(sentence.toLowerCase(), (dbInterface.checkTokenInDatabase(sentence.toLowerCase(), graph)+manager.nGram(sentence, 3))/2);
                    for (String phrase : phrases) {
                        System.out.println("Phrase: "+ phrase);
                        jsonMaker.addPhrase(phrase.toLowerCase(), (dbInterface.checkTokenInDatabase(phrase.toLowerCase(), graph)+manager.nGram(phrase.toLowerCase(),3))/2);
                        System.out.println("------------------------------------------------------------");
                        
                    }
                    
                    jsonMaker.toJson("data.json");
                    System.out.println("##########################################################");
                    
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(argPars.isCheckSentence()){

            System.out.println("Sentence: " + argPars.getSentence());
            jsonMaker.addSentence(argPars.getSentence().toLowerCase(), dbInterface.checkTokenInDatabase(argPars.getSentence().toLowerCase(), graph));
            System.out.println("*********************************************************");
            PhraseExtractor extractorPhrase = PhraseExtractor.fromSentence(argPars.getSentence().toLowerCase());
            List<String> phrases = extractorPhrase.getPhrases();
            for (String phrase : phrases) {
                System.out.println("Phrase: " + phrase);
                jsonMaker.addPhrase(phrase, dbInterface.checkTokenInDatabase(phrase.toLowerCase(), graph));
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
