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

    public Set<String> dfs(DirectedGraph<State> graph, State currentState, State end, int maxDepth, int minDepth){
        List<State> path = new ArrayList<>();
        path.add(currentState);
        dfsRecurssion(graph, currentState, end, 0, maxDepth, path, minDepth);
        return allPaths;
    }
    private void dfsRecurssion(DirectedGraph<State> graph, State currentState, State end, int depth, int maxDepth, List<State> path, int minDepth) {
        if ((currentState == end)) {
            ListToString lTS =  ListToString.of();
            StringBuilder sb = new StringBuilder();
            int cnt=0;
            for(State p: path){
                lTS.addString(p);
                cnt++;
            }
            
            if(cnt>(maxDepth-minDepth)){
                allPaths.add(lTS.getString());
            }
            return;
        }else if(depth >= maxDepth){
            return;
        }
        List<State> transitions = graph.getAdjacentNodes(currentState);
        for (State nextState : transitions) {
            path.add(nextState);
            dfsRecurssion(graph, nextState, end, depth + 1, maxDepth, path, minDepth);
            path.remove(path.size() - 1);
        }
    }
    Set<String> allPaths;
}
