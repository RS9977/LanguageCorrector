package DBinterface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.io.IOException;
import java.util.Map;
import java.io.BufferedReader;
import java.io.FileReader;

import DirectedGraph.DirectedGraph;

import java.sql.*;
import StateMachine.*;
import TypoCorrector.FilePrefixComparator;
import TypoCorrector.TypoCorrector;
import TypoCorrector.WordCountSorter;
import util.TwoListStruct;
import util.StringFileWriter;
import util.StringProcessor;

import GUI.SelectCorrectionHandler;
import SimilarityCorrector.WordPairDatabase;

public class DBinterface {
    String url;
    String dicFileName;
    WordPairDatabase wordPairDatabase;
    public DBinterface(String url, String dicFileName){
        this.url = "jdbc:sqlite:./"+url;
        this.dicFileName = dicFileName;
    }
    public DBinterface(String url, String dicFileName, WordPairDatabase wordPairDatabase){
        this.url = "jdbc:sqlite:./"+url;
        this.dicFileName = dicFileName;
        this.wordPairDatabase = wordPairDatabase;
    }
    public int checkTokenInDatabase(String sentence, DirectedGraph<State> graph){
        StateMachine SM = new StateMachine();
        sentence = sentence.replaceAll("\\p{Punct}", " $0");
        String[] tokens = sentence.split("\\s+");
        //String url = "jdbc:sqlite:./SQLite/mydatabase.db";
        //String dicFileName = "./SQLite/smallDic.txt";
        TypoCorrector typoChecker =  TypoCorrector.of(this.dicFileName);
        int initialConf = 0;
        try (Connection connection = DriverManager.getConnection(this.url)) {

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
            return ((int)((double)confidence*100.0/(actions.size()*15)));
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

    public String correctTokenInDatabase(String sentence, DirectedGraph<State> graph, int cnt, boolean isNotGUI, List<Boolean> flagsCorrection){
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
        //String url = "jdbc:sqlite:./SQLite/mydatabase.db";
        //this.dicFileName = "./SQLite/smallDic.txt";
        TypoCorrector typoChecker =  TypoCorrector.of(this.dicFileName);
        int initialConf = 0;
        int flagsCorrectioncnt = 0;
        boolean flagTypoCorrectionAccepted = true;
        StringFileWriter sfw = StringFileWriter.of("correction_details.txt", "\n", isNotGUI);

        //System.out.println(flagsCorrection);

        try (Connection connection = DriverManager.getConnection(this.url)) {

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
                            if(!tokenCorrected.equals(token)){
                                
                                initialConf += 5;
                                if(flagsCorrection.isEmpty()){
                                    sfw.appendString(token + " (REPLACE WITH) -> "+ tokenCorrected + "**");
                                }else if(!flagsCorrection.get(flagsCorrectioncnt) && isNotGUI){
                                    flagTypoCorrectionAccepted = false;
                                    //tokenList.set(i, "nan");
                                    break;
                                }else if(!flagsCorrection.get(flagsCorrectioncnt)){
                                    tokenCorrected = token;
                                }
                                flagsCorrectioncnt++;
                            }
                           //System.out.println("Corrected token: " + token + " -> " + tokenCorrected);

                            query = "SELECT role FROM word_roles WHERE word = '" + tokenCorrected + "';";
                            // Replace the token with its role
                            resultSet = statement.executeQuery(query);
                            if (resultSet.next()) {
                                tokenList.set(i,tokenCorrected);
                                role = resultSet.getString("role");
                              //  ////System.out.print("| Second try: "+ token + " -> " + role);
                                tokens[i] = role;
                            }else{
                                tokenList.set(i,tokenCorrected);
                            }
                        }

                    } 
                    }
                    //////System.out.println();
            }
            int     indDotseen = tokenList.size()+1;
            boolean flagStructureCorrection = true;
            if(flagTypoCorrectionAccepted){
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
                indDotseen = Math.max(suggested.size()+1, flags.size()+1);
                
                
                int biasToken = 0;
                
                //if(!isNotGUI)
                //System.out.println(flags);
                //System.out.println(suggested);
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
                                if(i+biasToken>0){
                                    String nextToken = word;
                                    String queryy = "SELECT role FROM word_roles WHERE word = '" + nextToken + "';";
                                    ResultSet resultSett = statement.executeQuery(queryy);
                                    if (resultSett.next()) {
                                        String roleNext = resultSett.getString("role");
                                        wordPairDatabase.bfsAndWriteCountsToFile(tokenList.get(i+biasToken-1), 2);
                                        WordCountSorter wordCountSorter = WordCountSorter.of("similarity_words.txt");
                                        List<String> possibleWords = wordCountSorter.getSortedWordsByCount();
                                        //System.out.println(possibleWords);
                                        for(String tokenCorrected: possibleWords){
                                            if(!nextToken.equals(tokenCorrected)){
                                                String queryNext = "SELECT role FROM word_roles WHERE word = '" + tokenCorrected + "';";
                                                ResultSet resultSetNext = statement.executeQuery(queryNext);
                                                if(resultSetNext.next()){    
                                                    String roleNextNext = resultSett.getString("role");
                                                    if(roleNextNext.equals(roleNext)){
                                                        word = tokenCorrected;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if(i+biasToken<tokenList.size()){
                                    
                                    if(flagsCorrection.isEmpty()){
                                        sfw.appendString(tokenList.get(i) + " (REPLACE WITH) -> "+ word + "*");
                                        tokenList.set(i+biasToken,word);
                                    }else if(flagsCorrection.get(flagsCorrectioncnt)){
                                        
                                        tokenList.set(i+biasToken,word);
                                    }else{
                                        flagStructureCorrection = false;
                                    }
                                    
                                    
                                }else{
                                    if(flagsCorrection.isEmpty()){
                                        sfw.appendString("(INSERTION INTO THE END) -> "+ word + "*");
                                        tokenList.add(word);
                                    }else if(flagsCorrection.get(flagsCorrectioncnt)){
                                        tokenList.add(word);
                                    }else{
                                        flagStructureCorrection = false;
                                    }
                                }
                                flagsCorrectioncnt++;
                                //System.out.println(biasToken);
                            }
                        }
                    }else if(flags.get(i+delCnt)==2){
                        delCnt++;
                        if(flagsCorrection.isEmpty()){
                            sfw.appendString(tokenList.get(i) + "-> (REMOVE)" + "*");
                            tokenList.remove(i+biasToken);
                        }else if(flagsCorrection.get(flagsCorrectioncnt)){
                            tokenList.remove(i+biasToken);
                        }else{
                            biasToken++;
                            flagStructureCorrection = false;
                        }
                        flagsCorrectioncnt++;
                    // System.out.println(biasToken);
                    }else if(flags.get(i+delCnt)==3){
                        try (Statement statement = connection.createStatement()) {
                            ////System.out.println(suggested.get(i));
                            String query = "SELECT word FROM word_roles WHERE role = '" + suggested.get(i) + "';";
                            String word = new String();
                            ResultSet resultSet = statement.executeQuery(query);
                            if (resultSet.next()) {
                                word = resultSet.getString("word");
                                ////System.out.println("Here I am: "+ word);
                                if(i+biasToken>0){
                                    String nextToken = word;
                                    String queryy = "SELECT role FROM word_roles WHERE word = '" + nextToken + "';";
                                    ResultSet resultSett = statement.executeQuery(queryy);
                                    if (resultSett.next()) {
                                        String roleNext = resultSett.getString("role");
                                        wordPairDatabase.bfsAndWriteCountsToFile(tokenList.get(i+biasToken-1), 2);
                                        WordCountSorter wordCountSorter = WordCountSorter.of("similarity_words.txt");
                                        List<String> possibleWords = wordCountSorter.getSortedWordsByCount();
                                       // System.out.println(possibleWords);
                                        for(String tokenCorrected: possibleWords){
                                            if(!nextToken.equals(tokenCorrected)){
                                                String queryNext = "SELECT role FROM word_roles WHERE word = '" + tokenCorrected + "';";
                                                ResultSet resultSetNext = statement.executeQuery(queryNext);
                                                if(resultSetNext.next()){    
                                                    String roleNextNext = resultSett.getString("role");
                                                    if(roleNextNext.equals(roleNext)){
                                                        word = tokenCorrected;
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                if(flagsCorrection.isEmpty()){
                                    
                                    if(i<tokenList.size()){
                                        sfw.appendString("(INSERTION INTO INDEX): "+ i + " -> "+ word + "*");
                                        tokenList.add(i+biasToken,word);
                                    }else{
                                        sfw.appendString("(INSERTION INTO THE END) -> "+ word + "*");
                                        tokenList.add(word);
                                    }
                                }else if(flagsCorrection.get(flagsCorrectioncnt)){
                                    if(i<tokenList.size()){
                                    // System.out.println("here!");
                                        tokenList.add(i+biasToken,word);
                                    }else{
                                        //sfw.appendString("(INSERTION INTO THE END) -> "+ word + "*");
                                        tokenList.add(word);
                                    }
                                }else{
                                    flagStructureCorrection = false;
                                    biasToken--;
                                }
                                flagsCorrectioncnt++;
                            // System.out.println(biasToken);
                            }
                        }
                    }
                }
                if(flagStructureCorrection){

                    for(int i=0; i<tokenList.size()-1; i++){
                        String nextToken = tokenList.get(i+1);
                        try (Statement statement = connection.createStatement()) {
                            String query = "SELECT role FROM word_roles WHERE word = '" + nextToken + "';";
                            ResultSet resultSet = statement.executeQuery(query);
                            if (resultSet.next()) {
                                String roleNext = resultSet.getString("role");
                                if(roleNext.equals("verb")){// || roleNext.equals("noun") || roleNext.equals("adjective") || roleNext.equals("adverb")){                 
                                    wordPairDatabase.bfsAndGetWords(tokenList.get(i), 2);
                                    FilePrefixComparator findSimilarity =  FilePrefixComparator.of("similarity_words.txt");
                                    String tokenCorrected = findSimilarity.findBestMatchingPrefix(nextToken);
                                    if(!nextToken.equals(tokenCorrected)){
                                        String queryNext = "SELECT role FROM word_roles WHERE word = '" + tokenCorrected + "';";
                                        ResultSet resultSetNext = statement.executeQuery(queryNext);
                                        if(resultSetNext.next()){    
                                            String roleNextNext = resultSet.getString("role");
                                            if(roleNextNext.equals("verb")){
                                                if(flagsCorrection.isEmpty()){
                                                    sfw.appendString(nextToken + " (REPLACE WITH) -> "+ tokenCorrected);
                                                    tokenList.set(i+1, tokenCorrected);
                                                }else if(flagsCorrection.get(flagsCorrectioncnt)){
                                                    tokenList.set(i+1, tokenCorrected);
                                                }
                                                flagsCorrectioncnt++;
                                            }
                                        }
                                    }
                                }
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
           //System.out.println(tokenList);
            StringBuilder result = new StringBuilder();
            boolean flagStart = false;
            int i = 0;
            //System.out.println(tokenList);
            for (String token : tokenList) {
                if(i==indDotseen)
                    break;
                if(flagStart && !token.equals(".") && !token.equals(","))
                    result.append(" ");
                result.append(token);
                flagStart = true;  
                i++;
            }
            if(!flagTypoCorrectionAccepted){
                result.append("|");
                result.append(flagsCorrectioncnt);
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
        //String dicFileName = "./SQLite/smallDic.txt";
        //String url = "jdbc:sqlite:./SQLite/newdatabase.db";
        TypoCorrector typoChecker =  TypoCorrector.of(this.dicFileName);
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

    public void updateTokenTableFromDic(String urlS, String filePath, Boolean isDutch) {
        // System.out.println("filePath");
         HashMap<String, String> wordMap = new HashMap<>();
         try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
             
             String line;
             if(isDutch){
                while ((line = br.readLine()) != null) {
                    String[] words = line.split("\\s+");
                    if (words.length > 2) {
                        wordMap.put(words[0], words[1]);
                    }
                }
             }else{
                while ((line = br.readLine()) != null) {
                    String[] words = line.split("\\s+");
                    if (words.length > 2) {
                        if(words[2].equals("to") && words.length>3){
                            wordMap.put(words[3], words[1]);
                        }else{
                            wordMap.put(words[2], words[1]);
                        }
                    }
                }
             }
             wordRolesMap = mixHashMaps(wordMap, wordRolesMap);
             updateDatabase(urlS);
         //    System.out.println(wordMap.size());
         } catch (IOException e) {
             e.printStackTrace();
         }
     }
     public HashMap<String, String> mixHashMaps(HashMap<String, String> map1, HashMap<String, String> map2) {
        HashMap<String, String> mergedMap = new HashMap<>();
        for (Map.Entry<String, String> entry : map1.entrySet()) {
            mergedMap.put(entry.getKey(), entry.getValue());
        }
        for (Map.Entry<String, String> entry : map2.entrySet()) {
            if (mergedMap.containsKey(entry.getKey())) {
                mergedMap.put(entry.getKey(), entry.getValue());
            } else {
                mergedMap.put(entry.getKey(), entry.getValue());
            }
        }

        return mergedMap;
    }
    // Method to read data from SQLite database into HashMap
    public void readDataFromDatabase() {
        wordRolesMap = new HashMap<>();
        String dbUrl = this.url;//SQLite/newdatabase.db";
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
    public void updateDatabase(String urlS) {
        String dbUrl = "jdbc:sqlite:./"+urlS;
        //String dbUrl = "jdbc:sqlite:./SQLite/newdatabase.db";
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
