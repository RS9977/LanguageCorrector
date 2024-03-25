import DirectedGraph.DirectedGraph;
import StateMachine.*;
import DBinterface.DBinterface;

public class Main {
     public static void main(String[] args) {
        DirectedGraph<State> graph = new DirectedGraph<>();
        

        // Build the state machine graph
        graph.addNode(State.START);
        graph.addNode(State.PRONOUN);
        graph.addNode(State.VERB);
        graph.addNode(State.ADJECTIVE);
        graph.addNode(State.ADVERB);
        graph.addNode(State.ARTICLE);
        graph.addNode(State.CONJ);
        graph.addNode(State.COMMA);
        graph.addNode(State.NOUN);
        graph.addNode(State.DOT);
        

        graph.addEdge(State.START,     State.PRONOUN);
        graph.addEdge(State.PRONOUN,   State.VERB);
        graph.addEdge(State.VERB,      State.ADJECTIVE);
        graph.addEdge(State.VERB,      State.ADVERB);
        graph.addEdge(State.ADVERB,    State.ADJECTIVE);
        graph.addEdge(State.VERB,      State.ARTICLE);
        graph.addEdge(State.ARTICLE,   State.ADVERB);
        graph.addEdge(State.ARTICLE,   State.ADJECTIVE);
        graph.addEdge(State.ADJECTIVE, State.DOT);
        graph.addEdge(State.ADJECTIVE, State.NOUN);
        graph.addEdge(State.NOUN,      State.DOT);
        graph.addEdge(State.NOUN,      State.COMMA);
        graph.addEdge(State.DOT,       State.END);
        graph.addEdge(State.COMMA,     State.PRONOUN);
        graph.addEdge(State.COMMA,     State.CONJ);
        graph.addEdge(State.CONJ,      State.PRONOUN);
        

        
        // Sentence to tokenize
        String sentence = "it is a very good book, and it is very small.";
        // Tokenize the sentence
        DBinterface dbInterface = new DBinterface();

        dbInterface.checkTokenInDatabase(sentence, graph);
        
    }
}

//javac -d bin Main.java **/*.java
//java -cp bin:SQLite/sqlite-jdbc-3.45.2.0.jar:SQLite/slf4j-api-1.7.36.jar Main