import java.sql.*;

import java.sql.*;

public class StateMachineChecker {

    // Define the states of the state machine
    private enum State {
        START,      // Initial state
        PRONOUN,    // State for processing a pronoun token
        ARTICLE,    // State for article
        VERB,       // State for processing a verb token
        ADJECTIVE,   // State for processing an adjective token
        NOUN,   // State for processing a noun
        DOT,   // State for processing a noun
    }

    // Define the transitions of the state machine
    private static State nextState(State currentState, String token) {
        
        switch (currentState) {
            case START:
                if (token.equalsIgnoreCase("Pronoun")) {
                    return State.PRONOUN;
                }
                break;
            case PRONOUN:
                if (token.equalsIgnoreCase("Verb")) {
                    return State.VERB;
                }
                break;
            case ARTICLE:
                if (token.equalsIgnoreCase("Adjective")) {
                    return State.ADJECTIVE;
                }
                break;
            case VERB:
                if (token.equalsIgnoreCase("Article")) {
                    return State.ARTICLE;
                }
                break;
            case ADJECTIVE:
                if (token.equalsIgnoreCase("Noun")) {
                    return State.NOUN;
                }
                break;
            case NOUN:
                if (token.equalsIgnoreCase("Dot")) {
                    return State.DOT;
                }
                break;
        }
        return null; // No valid transition
    }

    // Check if the tokens follow the state machine
    private static boolean followsStateMachine(String[] tokens) {
        State currentState = State.START;
        for (String token : tokens) {

            System.out.println(currentState + " -> " + token + "\n");
            State nextState = nextState(currentState, token);
            if (nextState == null) {
                return false; // Invalid transition
            }
            currentState = nextState;
        }

        // Check if the final state is acceptable
        return currentState == State.DOT;
    }

    public static void main(String[] args) {
        // JDBC URL for SQLite database (replace "database.db" with your database file)
        String url = "jdbc:sqlite:./SQLite/mydatabase.db";

        // Sentence to tokenize
        String sentence = "it is a good book.";

        try (Connection connection = DriverManager.getConnection(url)) {

            // Tokenize the sentence
            sentence = sentence.replaceAll("\\p{Punct}", " $0");
            String[] tokens = sentence.split("\\s+");
            
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

            // Check if the tokens follow the state machine
            boolean followsStateMachine = followsStateMachine(tokens);

            // Output the result
            if (followsStateMachine) {
                System.out.println("The tokens follow the state machine.");
            } else {
                System.out.println("The tokens do not follow the state machine.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}


//javac -d bin -cp .:sqlite-jdbc-3.45.2.0.jar StateMachineChecker.java