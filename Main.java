import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;




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
        graph.addNode(State.ARTICLE);
        graph.addNode(State.NOUN);
        graph.addNode(State.DOT);
        

        graph.addEdge(State.START,     State.PRONOUN);
        graph.addEdge(State.PRONOUN,   State.VERB);
        graph.addEdge(State.VERB,      State.ADJECTIVE);
        graph.addEdge(State.VERB,      State.ARTICLE);
        graph.addEdge(State.ARTICLE,   State.ADJECTIVE);
        graph.addEdge(State.ADJECTIVE, State.DOT);
        graph.addEdge(State.ADJECTIVE, State.NOUN);
        graph.addEdge(State.NOUN,      State.DOT);
        graph.addEdge(State.DOT,       State.END);

        
        // Sentence to tokenize
        String sentence = "they are a good.";
        // Tokenize the sentence
        DBinterface dbInterface = new DBinterface();

        dbInterface.checkTokenInDatabase(sentence, graph);
        
    }
}
