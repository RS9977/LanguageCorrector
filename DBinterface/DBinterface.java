package DBinterface;

import java.util.ArrayList;
import java.util.List;

import DirectedGraph.DirectedGraph;

import java.sql.*;
import StateMachine.*;

public class DBinterface {
    public void checkTokenInDatabase(String sentence, DirectedGraph<State> graph){
        StateMachine<State> SM = new StateMachine<>();
        sentence = sentence.replaceAll("\\p{Punct}", " $0");
        String[] tokens = sentence.split("\\s+");
        String url = "jdbc:sqlite:./SQLite/mydatabase.db";
        try (Connection connection = DriverManager.getConnection(url)) {

            // Lookup each token in the database and categorize it
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                
                try (Statement statement = connection.createStatement()) {
                    String query = "SELECT role FROM word_roles WHERE word = '" + token + "';";
                    ResultSet resultSet = statement.executeQuery(query);
                    if (resultSet.next()) {
                        String role = resultSet.getString("role");
                        // Replace the token with its role
                        tokens[i] = role;
                    }
                }
            }

            List<State> actions = new ArrayList<>();

            for(String token: tokens){
                actions.add(State.fromString(token));
            }
            // Define the initial state
            State initialState = State.START;

            // Check if the sequence of actions follows the state machine

            boolean isFollowed = SM.isStateMachineFollowed(graph, actions, initialState);
            if (isFollowed) {
                System.out.println("Sequence of actions follows the state machine.");
            } else {
                System.out.println("Sequence of actions does not follow the state machine.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
