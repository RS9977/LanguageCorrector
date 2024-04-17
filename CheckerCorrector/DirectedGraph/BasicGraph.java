package DirectedGraph;
import java.util.HashMap;
import java.sql.*;

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
        createTable();


        graph.addEdge(State.START,     State.PRONOUN);
        edgeList.put(State.START.toString(), State.PRONOUN.toString());

        graph.addEdge(State.PRONOUN,   State.VERB);
        edgeList.put(State.PRONOUN.toString(), State.VERB.toString());

        graph.addEdge(State.VERB,      State.ADVERB);
        edgeList.put(State.VERB.toString(), State.ADVERB.toString());

        graph.addEdge(State.ADVERB,    State.ADJECTIVE);
        edgeList.put(State.ADVERB.toString(), State.ADJECTIVE.toString());

        graph.addEdge(State.VERB,      State.ARTICLE);
        edgeList.put(State.VERB.toString(), State.ARTICLE.toString());

        graph.addEdge(State.ARTICLE,   State.ADVERB);
        edgeList.put(State.ARTICLE.toString(), State.ADVERB.toString());

        graph.addEdge(State.ARTICLE,   State.ADJECTIVE);
        edgeList.put(State.ARTICLE.toString(), State.ADJECTIVE.toString());

        graph.addEdge(State.ARTICLE,   State.NOUN);
        edgeList.put(State.ARTICLE.toString(), State.NOUN.toString());

        graph.addEdge(State.ADJECTIVE, State.DOT);
        edgeList.put(State.ADJECTIVE.toString(), State.DOT.toString());

        graph.addEdge(State.ADJECTIVE, State.NOUN);
        edgeList.put(State.ADJECTIVE.toString(), State.NOUN.toString());

        graph.addEdge(State.ADJECTIVE, State.COMMA);
        edgeList.put(State.ADJECTIVE.toString(), State.COMMA.toString());

        graph.addEdge(State.NOUN,      State.DOT);
        edgeList.put(State.NOUN.toString(), State.DOT.toString());

        graph.addEdge(State.NOUN,      State.COMMA);
        edgeList.put(State.NOUN.toString(), State.COMMA.toString());

        graph.addEdge(State.DOT,       State.END);
        edgeList.put(State.DOT.toString(), State.END.toString());

        //graph.addEdge(State.COMMA,     State.PRONOUN);
        graph.addEdge(State.COMMA,     State.CONJ);
        edgeList.put(State.COMMA.toString(), State.CONJ.toString());

        graph.addEdge(State.CONJ,      State.PRONOUN);
        edgeList.put(State.CONJ.toString(), State.PRONOUN.toString());

        graph.addEdge(State.PRONOUN,   State.MODAL);
        edgeList.put(State.PRONOUN.toString(), State.MODAL.toString());

        graph.addEdge(State.NOUN,   State.MODAL);
        edgeList.put(State.NOUN.toString(), State.MODAL.toString());

        graph.addEdge(State.MODAL,       State.VERB);
        edgeList.put(State.MODAL.toString(), State.VERB.toString());

        graph.addEdge(State.IF,        State.PRONOUN);
        edgeList.put(State.IF.toString(), State.PRONOUN.toString());

        graph.addEdge(State.THAT,      State.PRONOUN);
        edgeList.put(State.THAT.toString(), State.PRONOUN.toString());

        graph.addEdge(State.IF,        State.NOUN);
        edgeList.put(State.IF.toString(), State.NOUN.toString());

        graph.addEdge(State.THAT,      State.NOUN);
        edgeList.put(State.THAT.toString(), State.NOUN.toString());

        graph.addEdge(State.NOUN,      State.VERB);
        edgeList.put(State.NOUN.toString(), State.VERB.toString());

        graph.addEdge(State.NOUN,      State.MODAL);
        edgeList.put(State.NOUN.toString(), State.MODAL.toString());

        //graph.addEdge(State.VERB,      State.NOT);
        graph.addEdge(State.PRONOUN,   State.DOES);
        edgeList.put(State.PRONOUN.toString(), State.DOES.toString());

        graph.addEdge(State.NOUN,      State.DOES);
        edgeList.put(State.NOUN.toString(), State.DOES.toString());

        graph.addEdge(State.DOES,      State.NOT);
        edgeList.put(State.DOES.toString(), State.NOT.toString());

        graph.addEdge(State.NOT,       State.VERB);
        edgeList.put(State.NOT.toString(), State.VERB.toString());

        graph.addEdge(State.NOT,       State.ADVERB);
        edgeList.put(State.NOT.toString(), State.ADVERB.toString());

        graph.addEdge(State.NOT,       State.ADJECTIVE);
        edgeList.put(State.NOT.toString(), State.ADJECTIVE.toString());

        graph.addEdge(State.NOT,       State.ARTICLE);
        edgeList.put(State.NOT.toString(), State.ARTICLE.toString());

        graph.addEdge(State.NOT,       State.DOT);
        edgeList.put(State.NOT.toString(), State.DOT.toString());

        graph.addEdge(State.PREPOS,        State.PRONOUN);
        edgeList.put(State.PREPOS.toString(), State.PRONOUN.toString());

        graph.addEdge(State.NOUN,      State.IS);
        edgeList.put(State.NOUN.toString(), State.IS.toString());

        graph.addEdge(State.PRONOUN,   State.IS);
        edgeList.put(State.PRONOUN.toString(), State.IS.toString());

        graph.addEdge(State.IS,        State.ADJECTIVE);
        edgeList.put(State.IS.toString(), State.ADJECTIVE.toString());

        graph.addEdge(State.IS,        State.ADVERB);
        edgeList.put(State.IS.toString(), State.ADVERB.toString());

        graph.addEdge(State.IS,        State.ARTICLE);
        edgeList.put(State.IS.toString(), State.ARTICLE.toString());

        graph.addEdge(State.IS,        State.NOT);
        edgeList.put(State.IS.toString(), State.NOT.toString());

        graph.addEdge(State.THAT,      State.IF);
        edgeList.put(State.THAT.toString(), State.IF.toString());

        graph.addEdge(State.VERB,      State.PREPOS);
        edgeList.put(State.VERB.toString(), State.PREPOS.toString());

        graph.addEdge(State.IS,      State.PREPOS);
        edgeList.put(State.IS.toString(), State.PREPOS.toString());

        graph.addEdge(State.PREPOS,      State.NOUN);
        edgeList.put(State.PREPOS.toString(), State.NOUN.toString());

        graph.addEdge(State.PREPOS,      State.ARTICLE);
        edgeList.put(State.PREPOS.toString(), State.ARTICLE.toString());

        graph.addEdge(State.PREPOS,      State.ADJECTIVE);
        edgeList.put(State.PREPOS.toString(), State.ADJECTIVE.toString());

        graph.addEdge(State.ADVERB,      State.VERB);
        edgeList.put(State.ADVERB.toString(), State.VERB.toString());

        graph.addEdge(State.NOUN,      State.NOUN);
        edgeList.put(State.NOUN.toString(), State.NOUN.toString());

        graph.addEdge(State.ADJECTIVE,      State.ADJECTIVE);
        edgeList.put(State.ADJECTIVE.toString(), State.ADJECTIVE.toString());
    }
    public DirectedGraph<State> getGraph() {
        return graph;
    }


    private HashMap<String, String> edgeList;

    private static final String URL = "jdbc:sqlite:./SQLite/graphdatabase_name";

    // Method to create the table
    private void createTable() {
        edgeList = new HashMap<>();
        // SQL statement to create the table
        /*String sqlCreateTable = "CREATE TABLE nodeOne_nodeTwo ("
                + "nodeOne VARCHAR(50) PRIMARY KEY,"
                + "nodeTwo VARCHAR(50)"
                + ")";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            // Execute the SQL statement to create the table
            stmt.execute(sqlCreateTable);
        } catch (SQLException e) {
            e.printStackTrace();
        }*/
    }

    // Method to read data from SQLite database into HashMap
    public void readDataFromDatabase() {
        edgeList = new HashMap<>();
        String dbUrl = "jdbc:sqlite:./SQLite/graphdatabase.db";
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM nodeOne_nodeTwo");

            while (resultSet.next()) {
                String word = resultSet.getString("nodeOne");
                String role = resultSet.getString("nodeTwo");
                edgeList.put(word, role);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Method to update SQLite database with updated HashMap
    public void updateDatabase() {
        String dbUrl = "jdbc:sqlite:./SQLite/newdatabase.db";
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            // Clear existing data in the table
            Statement clearStatement = connection.createStatement();
            clearStatement.executeUpdate("DELETE FROM nodeOne_nodeTwo");

            // Insert updated data from HashMap into the table
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO word_roles (nodeOne, nodeTwo) VALUES (?, ?)");
            for (String word : edgeList.keySet()) {
                String role = edgeList.get(word);
                insertStatement.setString(1, word);
                insertStatement.setString(2, role);
                insertStatement.addBatch();
            }
            insertStatement.executeBatch();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Getter method for the HashMap
    public HashMap<String, String> getEdgeList() {
        return edgeList;
    }

    // Setter method for the HashMap
    public void setWordRolesMap(HashMap<String, String> edgeList) {
        this.edgeList = edgeList;
    }
}
