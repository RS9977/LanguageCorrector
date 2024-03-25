package StateMachine;

import java.util.List;

import DirectedGraph.DirectedGraph;
public class StateMachine<NodeClass>{
    public boolean isStateMachineFollowed(DirectedGraph<NodeClass> graph, List<NodeClass> actions, NodeClass initialState) {
        NodeClass currentState = initialState;
        for (NodeClass action : actions) {
            List<NodeClass> transitions = graph.getAdjacentNodes(currentState);
            if (!transitions.contains(action)) {
                return false; // Action not allowed in current state
            }
            currentState = action; // Transition to the next state
        }
        return true;
    }
}