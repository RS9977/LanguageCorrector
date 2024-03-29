package StateMachine;

import java.util.ArrayList;
import java.util.List;

import DirectedGraph.DirectedGraph;

import util.TwoListStruct;

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
    public TwoListStruct suggestedStateMachine(DirectedGraph<State> graph, List<State> actions, State initialState) {
        
        //System.out.println("----------------------------------------------------");
        State currentState = initialState;
        boolean flag = false;
        List<State> suggestedAction = new ArrayList<>();
        List<Integer> flags         = new ArrayList<>();
        State tempState = currentState;
        //suggestedAction.add(currentState);
        int cnt = 0;
        for(int i=0; i<actions.size(); i++){
        //for (State action : actions) {
            State action = actions.get(i);
            System.out.print(currentState);
            
            List<State> transitions = graph.getAdjacentNodes(currentState);
            if(flag && cnt <2){
                List<State> tempTransitions = graph.getAdjacentNodes(tempState);
                for(State checkState: tempTransitions){
                    if(checkState != State.START){
                        List<State> checkTransitions = graph.getAdjacentNodes(checkState);
                        if(checkTransitions.contains(action)){
                            System.out.print("| updated to: "+ checkState);
                            suggestedAction.add(checkState);
                            flags.add(1);
                        }
                    }
                }
                flag = false;
                cnt = 0;
            }
            if (!transitions.contains(action)) {
                //System.out.print("? " + action);
                tempState = currentState;
                currentState = State.START;
                flag = true;
                cnt ++;
            }
            if(!flag){
                suggestedAction.add(currentState);
                System.out.print("| no update");
                flags.add(0);
                currentState = action; // Transition to the next state
            }else if(cnt <2){
                suggestedAction.add(currentState);
                flags.add(0);
                System.out.print("| no update");
                List<State> tempTransitions = graph.getAdjacentNodes(tempState);
                for(State checkState: tempTransitions){
                    if(checkState != State.START){
                        List<State> checkTransitions = graph.getAdjacentNodes(checkState);
                        if(checkTransitions.contains(action)){
                            suggestedAction.add(checkState);
                            System.out.print("| updated to with missing: "+ checkState + " =)");
                            flags.add(2);
                            currentState = checkState;
                            i--;
                        }
                    }
                }
                flag = false;
                cnt = 0;
            }else{
                currentState = action; // Transition to the next state
            }
            System.out.println();
            //System.out.println();
            
        }
        
        return TwoListStruct.of(suggestedAction, flags);
    }
}