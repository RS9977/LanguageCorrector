package DBinterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;

import DirectedGraph.DirectedGraph;

import java.sql.*;
import StateMachine.*;
import TypoCorrector.TypoCorrector;
import util.TwoListStruct;
import util.StringFileWriter;
import util.StringProcessor;

import GUI.SelectCorrectionHandler;

public class DBinterface {
    public int checkTokenInDatabase(String sentence, DirectedGraph<State> graph){
        StateMachine SM = new StateMachine();
        sentence = sentence.replaceAll("\\p{Punct}", " $0");
        String[] tokens = sentence.split("\\s+");
        String url = "jdbc:sqlite:./SQLite/mydatabase.db";
        String dicFileName = "./SQLite/smallDic.txt";
        TypoCorrector typoChecker =  TypoCorrector.of(dicFileName);
        int initialConf = 0;
        try (Connection connection = DriverManager.getConnection(url)) {

            // Lookup each token in the database and categorize it
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                
                try (Statement statement = connection.createStatement()) {
                    
                    String query = "SELECT role FROM word_roles WHERE word = '" + token + "';";
                    String role = new String();
                    
                    ResultSet resultSet = statement.executeQuery(query);
                    if (resultSet.next()) {
                        role = resultSet.getString("role");
                        ////System.out.print("first try: " + token + " -> " + role);
                        tokens[i] = role;
                    }else{
                        String tokenCorrected = new String();
                        if(role.isEmpty()){
                            tokenCorrected = typoChecker.closestWord(token);
                            if(!tokenCorrected.equals(token))
                                initialConf += 5;
                            ////System.out.print("Corrected token: " + token + " -> " + tokenCorrected);
                            query = "SELECT role FROM word_roles WHERE word = '" + tokenCorrected + "';";
                            // Replace the token with its role
                            resultSet = statement.executeQuery(query);
                            if (resultSet.next()) {
                                role = resultSet.getString("role");
                                ////System.out.print("| Second try: "+ token + " -> " + role);
                                tokens[i] = role;
                            }
                        }

                    } 
                    }
                    ////System.out.println();
            }

            List<State> actions = new ArrayList<>();

            for(String token: tokens){
                actions.add(State.fromString(token));
            }
            // Define the initial state
            State initialState = State.START;

            // Check if the sequence of actions follows the state machine

            int confidence = SM.isStateMachineFollowed(graph, actions, initialState, initialConf);
            //System.out.print("The confidence score is: "+ confidence + "\n");
            return confidence;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public String correctTokenInDatabase(String sentence, DirectedGraph<State> graph, int cnt, boolean isNotGUI){
        List<Boolean> flagsCorrection = new ArrayList<>();
        for(int i=0; i<cnt; i++){ 
            sentence = new String(correctTokenInDatabaseInnerloop(sentence, graph, flagsCorrection, isNotGUI));
            if(checkTokenInDatabase(sentence, graph)<10)
                break;
        }
        return sentence;
    }

    public String correctTokenInDatabaseGUI(String sentence, DirectedGraph<State> graph, List<Boolean> flagsCorrection){
        return  correctTokenInDatabaseInnerloop(sentence, graph, flagsCorrection, false);
    }

    private String correctTokenInDatabaseInnerloop(String sentence, DirectedGraph<State> graph, List<Boolean> flagsCorrection, boolean isNotGUI){
        StateMachine SM = new StateMachine();
        sentence = sentence.replaceAll("\\p{Punct}", " $0");
        String[] tokens = sentence.split("\\s+");
        String[] tokensCopy = tokens.clone();
        List<String> tokenList = new ArrayList<>(Arrays.asList(tokensCopy));
        String url = "jdbc:sqlite:./SQLite/mydatabase.db";
        String dicFileName = "./SQLite/smallDic.txt";
        TypoCorrector typoChecker =  TypoCorrector.of(dicFileName);
        int initialConf = 0;
        try (Connection connection = DriverManager.getConnection(url)) {

            // Lookup each token in the database and categorize it
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                
                try (Statement statement = connection.createStatement()) {
                    
                    String query = "SELECT role FROM word_roles WHERE word = '" + token + "';";
                    String role = new String();
                    
                    ResultSet resultSet = statement.executeQuery(query);
                    if (resultSet.next()) {
                        role = resultSet.getString("role");
                        //////System.out.print("first try: " + token + " -> " + role);
                        tokens[i] = role;
                    }else{
                        String tokenCorrected = new String();
                        if(role.isEmpty()){
                            tokenCorrected = typoChecker.closestWord(token);
                            if(!tokenCorrected.equals(token))
                                initialConf += 5;
                           // ////System.out.print("Corrected token: " + token + " -> " + tokenCorrected);

                            query = "SELECT role FROM word_roles WHERE word = '" + tokenCorrected + "';";
                            // Replace the token with its role
                            resultSet = statement.executeQuery(query);
                            if (resultSet.next()) {
                                tokenList.set(i,tokenCorrected);
                                role = resultSet.getString("role");
                              //  ////System.out.print("| Second try: "+ token + " -> " + role);
                                tokens[i] = role;
                            }
                        }

                    } 
                    }
                    //////System.out.println();
            }

            List<State> actions = new ArrayList<>();

            for(String token: tokens){
                actions.add(State.fromString(token));
            }
            // Define the initial state
            State initialState = State.START;

            // Check if the sequence of actions follows the state machine

            TwoListStruct<State, Integer> output = SM.suggestedStateMachine(graph, actions, initialState);
           // output.displayArrays();
            List<State> suggested = output.getOutputList();
            List<Integer> flags   = output.getChangesList();
            int delCnt = 0;
            boolean seenDot = false;
            int     indDotseen = Math.max(suggested.size()+1, flags.size()+1);
            StringFileWriter sfw = StringFileWriter.of("correction_details.txt", "\n", isNotGUI);
            int flagsCorrectioncnt = 0;
            for(int i=0; i<suggested.size(); i++){
                if(seenDot){
                    //indDotseen = i;
                   // break;
                }
                if(suggested.get(i) == State.DOT)
                    seenDot = true;
                if(flags.get(i+delCnt)==1){
                    try (Statement statement = connection.createStatement()) {
                        String query = "SELECT word FROM word_roles WHERE role = '" + suggested.get(i) + "';";
                        String word = new String();
                        ResultSet resultSet = statement.executeQuery(query);
                        
                        ////System.out.print("!!! 1: " + resultSet + "| ");
                        if (resultSet.next()) {
                            word = resultSet.getString("word");
                            ////System.out.println("Here I am: "+ word);
                            if(i<tokenList.size()){
                                
                                if(flagsCorrection.isEmpty()){
                                    tokenList.set(i,word);
                                    sfw.appendString(tokenList.get(i) + " -> "+ word);
                                }else if(flagsCorrection.get(flagsCorrectioncnt)){
                                    tokenList.set(i,word);
                                    flagsCorrectioncnt++;
                                }
                                
                                
                            }else{
                                if(flagsCorrection.isEmpty()){
                                    sfw.appendString("IND: "+ i + " -> "+ word);
                                    tokenList.add(word);
                                }else if(flagsCorrection.get(flagsCorrectioncnt)){
                                    tokenList.add(word);
                                    flagsCorrectioncnt++;
                                }
                            }
                        }
                    }
                }else if(flags.get(i+delCnt)==2){
                    delCnt++;
                    if(flagsCorrection.isEmpty()){
                        sfw.appendString(tokenList.get(i) + " -> X");
                        tokenList.remove(i);
                    }else if(flagsCorrection.get(flagsCorrectioncnt)){
                        tokenList.remove(i);
                        flagsCorrectioncnt++;
                    }
                }else if(flags.get(i+delCnt)==3){
                    try (Statement statement = connection.createStatement()) {
                        ////System.out.println(suggested.get(i));
                        String query = "SELECT word FROM word_roles WHERE role = '" + suggested.get(i) + "';";
                        String word = new String();
                        ResultSet resultSet = statement.executeQuery(query);
                        if (resultSet.next()) {
                            word = resultSet.getString("word");
                            ////System.out.println("Here I am: "+ word);
                            if(flagsCorrection.isEmpty()){
                                sfw.appendString("IND: "+ i + " -> "+ word);
                                if(i<tokenList.size()){
                                    tokenList.add(i,word);
                                }else{
                                    tokenList.add(word);
                                }
                            }else if(flagsCorrection.get(flagsCorrectioncnt)){
                                if(i<tokenList.size()){
                                    tokenList.add(i,word);
                                }else{
                                    tokenList.add(word);
                                }
                                flagsCorrectioncnt++;
                            }
                        }
                    }
                }
            }   
            try {
                if(isNotGUI)
                    sfw.appendString("-----------------------------------------");
                if(flagsCorrection.isEmpty()){
                    sfw.writeToFile();
                }
            } catch (IOException e) {
                System.err.println("An error occurred while writing to the file: " + e.getMessage());
            }
            StringBuilder result = new StringBuilder();
            boolean flagStart = false;
            int i = 0;
            for (String token : tokenList) {
                if(i==indDotseen)
                    break;
                if(flagStart && !token.equals(".") && !token.equals(","))
                    result.append(" ");
                result.append(token);
                flagStart = true;  
                i++;
            }
            return result.toString();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new String();
    }

    public int updateTokenInDatabase(String sentence, DirectedGraph<State> graph){
        //System.out.print(sentence + " |");
        sentence = StringProcessor.processString(sentence);
        
        if(sentence.equals(""))
            return 0;
        StateMachine SM = new StateMachine();
        sentence = sentence.replaceAll("\\p{Punct}", " $0");
        //System.out.println(sentence);
        String[] tokens = sentence.split("\\s+");
        String[] tokensCopy = tokens.clone();
        List<Boolean> missFlag = new ArrayList();
        List<String> tokenList = new ArrayList<>(Arrays.asList(tokensCopy));
        //String url       = "jdbc:sqlite:./SQLite/mydatabase.db";
        //String urlinsert = "jdbc:sqlite:./SQLite/newdatabase.db";
        String dicFileName = "./SQLite/smallDic.txt";
        //String url = "jdbc:sqlite:./SQLite/newdatabase.db";
        TypoCorrector typoChecker =  TypoCorrector.of(dicFileName);
        int initialConf = 0;
        int cntMiss = 0;
            // Lookup each token in the database and categorize it
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i];
                String role;
            //    System.out.print("\nBefor token: "+tokens[i]+"| ");                    
                if (wordRolesMap.containsKey(token)) {
                    role = wordRolesMap.get(token);
                        //////System.out.print("first try: " + token + " -> " + role);
                    //    System.out.println("role: " + role);
                    tokens[i] = role;
                    missFlag.add(false);
                }else{
                    String tokenCorrected = new String();
                        tokenCorrected = typoChecker.closestWord(token);
                        if(!tokenCorrected.equals(token))
                            initialConf += 5;
                           // ////System.out.print("Corrected token: " + token + " -> " + tokenCorrected);

                        if (wordRolesMap.containsKey(tokenCorrected)) {
                            tokenList.set(i,tokenCorrected);
                            role = wordRolesMap.get(tokenCorrected);
                               //  ////System.out.print("| Second try: "+ token + " -> " + role);
                              //  System.out.println("role: " + role);
                            tokens[i] = role;
                            missFlag.add(false);
                        }else{
                            missFlag.add(true);
                            cntMiss ++;
                        }
                }
                if(cntMiss>1)
                    return 0;
                    //System.out.print("After token: "+tokens[i]+"| ");
                    //System.out.println();
            }
            
            //System.out.println("\nMISS: "+cntMiss);
            if(cntMiss>0 && !missFlag.get(missFlag.size()-1)){
                List<State> actions = new ArrayList<>();

                for(String token: tokens){
                    actions.add(State.fromString(token));
                }
                // Define the initial state
                State initialState = State.START;
                
                // Check if the sequence of actions follows the state machine
                List<State> suggested = SM.updateDB(graph, actions, initialState);
               // System.out.println(actions);
              //  System.out.println(suggested);
              //  System.out.println("---------------------------");
                
                for(int i=0; i<suggested.size(); i++){
                    if(!suggested.get(i).toString().equals(tokens[i])){
                        if(!missFlag.get(i))
                            return 0;
                    }
                        
                }
                int cntUpdate = 0;
                for(int i=0; i<tokens.length; i++){
                    if(missFlag.get(i)){
                        if(!suggested.get(i).toString().equals(tokens[i]) && !suggested.get(i).toString().equals("nan") && State.validSuggestedState(suggested.get(i))){
                            System.out.println("\n"+sentence);
                            System.out.println(actions);
                            System.out.println(suggested);
                            System.out.println(missFlag);
                            System.out.println(tokens[i] + " -> " + suggested.get(i));
                            System.out.println("--------------------------------");
                            wordRolesMap.put(tokens[i], suggested.get(i).toString()); 
                            cntUpdate++;
                        }
                    }
                }
                return cntUpdate;
                
            }
            return 0;
    }


    private HashMap<String, String> wordRolesMap;

    // Method to read data from SQLite database into HashMap
    public void readDataFromDatabase() {
        wordRolesMap = new HashMap<>();
        String dbUrl = "jdbc:sqlite:./SQLite/newdatabase.db";
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM word_roles");

            while (resultSet.next()) {
                String word = resultSet.getString("word");
                String role = resultSet.getString("role");
                wordRolesMap.put(word, role);
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
            clearStatement.executeUpdate("DELETE FROM word_roles");

            // Insert updated data from HashMap into the table
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO word_roles (word, role) VALUES (?, ?)");
            for (String word : wordRolesMap.keySet()) {
                String role = wordRolesMap.get(word);
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
    public HashMap<String, String> getWordRolesMap() {
        return wordRolesMap;
    }

    // Setter method for the HashMap
    public void setWordRolesMap(HashMap<String, String> wordRolesMap) {
        this.wordRolesMap = wordRolesMap;
    }

}
