package DirectedGraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import StateMachine.State;
import util.ListToString;

public class DFS {

    private DFS() {
        allPaths = new HashSet<>();
    }

    public static  DFS of(){
        return new DFS();
    }

    public Set<String> dfs(DirectedGraph<State> graph, State currentState, int maxDepth){
        List<State> path = new ArrayList<>();
        path.add(currentState);
        dfsRecurssion(graph, currentState, 0, maxDepth, path);
        return allPaths;
    }
    private void dfsRecurssion(DirectedGraph<State> graph, State currentState, int depth, int maxDepth, List<State> path) {
        if (currentState.equals("dot") || depth >= maxDepth) {
            ListToString lTS =  ListToString.of();
            StringBuilder sb = new StringBuilder();
            for(State p: path){
                lTS.addString(p);
            }
            allPaths.add(lTS.getString());
            return;
        }
        List<State> transitions = graph.getAdjacentNodes(currentState);
        for (State nextState : transitions) {
            path.add(nextState);
            dfsRecurssion(graph, nextState, depth + 1, maxDepth, path);
            path.remove(path.size() - 1);
        }
    }
    Set<String> allPaths;
}
