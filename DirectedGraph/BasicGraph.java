package DirectedGraph;
import StateMachine.State;

public class BasicGraph {
    public DirectedGraph<State> graph;
    public BasicGraph(){
        graph = new DirectedGraph<>();
        makeBasicGraph();
    }
    public void makeBasicGraph(){
        State cur = State.first();
        for(int i=0; i<State.values().length; i++){
            graph.addNode(cur);
            cur = cur.next();
        }

        cur = State.first();
        for(int i=1; i<State.values().length; i++){
            cur = cur.next();
            graph.addEdge(State.first(), cur);
        }

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
    }
    public DirectedGraph<State> getGraph() {
        return graph;
    }
}
