import java.io.IOException;

import DirectedGraph.BasicGraph;
import DirectedGraph.DirectedGraph;
import StateMachine.*;
import DBinterface.DBinterface;
import util.*;

public class Main {
     public static void main(String[] args) {
        //DirectedGraph<State> graph = new DirectedGraph<>();
        
        String sentence;
        ArgumentParser argPars = ArgumentParser.of(args);;
        
        if(argPars.isCheckSentence()){
            BasicGraph basicGraphClass = new BasicGraph();
            DirectedGraph<State> graph = basicGraphClass.getGraph();

        
        // Sentence to tokenize
        //String sentence = "it is a very good book, but it is small book.";
        // Tokenize the sentence
            DBinterface dbInterface = new DBinterface();

            dbInterface.checkTokenInDatabase(argPars.getSentence(), graph);
        }
          
    }
}

//javac -d bin Main.java **/*.java
//java -cp bin:SQLite/sqlite-jdbc-3.45.2.0.jar:SQLite/slf4j-api-1.7.36.jar:SQLite/slf4j-jdk14-1.7.36.jar Main 
//jar cvfm checker.jar manifest.txt -C bin . -C SQLite .
