package StateMachine;

import java.util.List;

import DirectedGraph.DirectedGraph;
public class StateMachine{
    public int isStateMachineFollowed(DirectedGraph<State> graph, List<State> actions, State initialState, int initialConf) {
        int confidence = initialConf;
        State currentState = initialState;
        for (State action : actions) {
            List<State> transitions = graph.getAdjacentNodes(currentState);
            if (!transitions.contains(action)) {
                currentState = State.START;
                confidence += 10; // Action not allowed in current state
            }
            currentState = action; // Transition to the next state
        }
        return confidence;
    }
}