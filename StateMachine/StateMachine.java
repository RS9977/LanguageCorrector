package StateMachine;

import java.util.List;

import DirectedGraph.DirectedGraph;
public class StateMachine{
    public int isStateMachineFollowed(DirectedGraph<State> graph, List<State> actions, State initialState, int initialConf) {
        int confidence = initialConf;
        //System.out.println("----------------------------------------------------");
        State currentState = initialState;
        for (State action : actions) {
            //System.out.print(currentState);
            List<State> transitions = graph.getAdjacentNodes(currentState);
            if (!transitions.contains(action)) {
                //System.out.print("? " + action);
                currentState = State.START;
                confidence += 10; // Action not allowed in current state
                
            }
            //System.out.println();
            currentState = action; // Transition to the next state
        }
        return confidence;
    }
}