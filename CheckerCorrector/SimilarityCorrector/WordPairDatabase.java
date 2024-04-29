package SimilarityCorrector;

import java.io.*;
import java.sql.*;
import java.util.*;

import util.StringProcessor;

public class WordPairDatabase {
    private String url;

    private WordPairDatabase(String url) {
        this.url = "jdbc:sqlite:"+ url;
    }
    public static WordPairDatabase of(String url){
        return new WordPairDatabase(url);
    }

    public void createTable() {
        try (Connection conn = DriverManager.getConnection(url)) {
            String sql = "CREATE TABLE IF NOT EXISTS word_pairs (\n"
                    + "    word1 TEXT,\n"
                    + "    word2 TEXT,\n"
                    + "    count INTEGER,\n"
                    + "    PRIMARY KEY (word1, word2)\n"
                    + ");";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error creating table: " + e.getMessage());
        }
    }

    public void processSentences(String fileName) {
        try (Connection conn = DriverManager.getConnection(url);
             BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            String line;
            try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
                while ((line = reader.readLine()) != null) {
                    line = line.toLowerCase();
                    line = StringProcessor.handleApostrophe(line);
                    line = line.replaceAll("\\p{Punct}", " $0");
                    String[] words = line.split("\\s+");
                    for (int i = 0; i < words.length - 1; i++) {
                        String word1 = words[i];
                        String word2 = words[i + 1];
                        updateDatabase(conn, word1, word2);
                    }
                }
            } catch (IOException e) {
                System.err.println("Error reading file: " + e.getMessage());
            }
        } catch (SQLException | IOException e) {
            System.err.println("Error processing sentences: " + e.getMessage());
        }
    }

    private void updateDatabase(Connection conn, String word1, String word2) {
        String sqlSelect = "SELECT count FROM word_pairs WHERE word1 = ? AND word2 = ?";
        String sqlInsert = "INSERT INTO word_pairs (word1, word2, count) VALUES (?, ?, 1)";
        String sqlUpdate = "UPDATE word_pairs SET count = count + 1 WHERE word1 = ? AND word2 = ?";

        try (PreparedStatement selectStmt = conn.prepareStatement(sqlSelect)) {
            selectStmt.setString(1, word1);
            selectStmt.setString(2, word2);
            ResultSet resultSet = selectStmt.executeQuery();

            if (resultSet.next()) {
                try (PreparedStatement updateStmt = conn.prepareStatement(sqlUpdate)) {
                    updateStmt.setString(1, word1);
                    updateStmt.setString(2, word2);
                    updateStmt.executeUpdate();
                }
            } else {
                try (PreparedStatement insertStmt = conn.prepareStatement(sqlInsert)) {
                    insertStmt.setString(1, word1);
                    insertStmt.setString(2, word2);
                    insertStmt.executeUpdate();
                }
            }
        } catch (SQLException e) {
            System.err.println("Error updating database: " + e.getMessage());
        }
    }

    public void bfsAndGetWords(String startWord, int depth) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(startWord);
        //visited.add(startWord);

        try (Connection conn = DriverManager.getConnection(url)) {
            while (!queue.isEmpty() && depth > 0) {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    String word = queue.poll();
                    visited.add(word);
                    for (String nextWord : getNextWords(conn, word)) {
                        if (!visited.contains(nextWord)) {
                            queue.add(nextWord);
                        }
                    }
                }
                depth--;
            }
        } catch (SQLException e) {
            System.err.println("Error accessing database: " + e.getMessage());
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("similarity_words.txt"))) {
            for(String word: visited) {
                writer.write(word + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Set<String> getNextWords(Connection conn, String word) {
        Set<String> nextWords = new HashSet<>();
        String sql = "SELECT word2 FROM word_pairs WHERE word1 = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, word);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                nextWords.add(rs.getString("word2"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving next words: " + e.getMessage());
        }

        return nextWords;
    }

    public void bfsAndWriteCountsToFile(String startWord, int depth) {
        String outputFile = "similarity_words.txt";
        Map<String, Integer> wordCounts = new HashMap<>();
        Queue<String> queue = new ArrayDeque<>();
        queue.add(startWord);

        try (Connection conn = DriverManager.getConnection(url)) {
            while (!queue.isEmpty() && depth > 0) {
                int size = queue.size();
                for (int i = 0; i < size; i++) {
                    String word = queue.poll();
                    for (Map.Entry<String, Integer> entry : getCountsForWord(conn, word).entrySet()) {
                        //String pair = word + " " + entry.getKey();
                        wordCounts.put(entry.getKey(), entry.getValue());
                        queue.add(entry.getKey());  // Add next word to queue for BFS
                    }
                }
                depth--;
            }
        } catch (SQLException e) {
            System.err.println("Error accessing database: " + e.getMessage());
        }

        // Write word counts to the output file
        //System.out.print("WORDCOUNTS: ");
        //System.out.println(wordCounts);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            for (Map.Entry<String, Integer> entry : wordCounts.entrySet()) {
                writer.write(entry.getKey() + " " + entry.getValue() + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
        }
    }

    private Map<String, Integer> getCountsForWord(Connection conn, String word) {
        Map<String, Integer> counts = new HashMap<>();
        String sql = "SELECT word2, count FROM word_pairs WHERE word1 = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, word);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                counts.put(rs.getString("word2"), rs.getInt("count"));
            }
        } catch (SQLException e) {
            System.err.println("Error retrieving counts for word: " + e.getMessage());
        }

        return counts;
    }

    public static void main(String[] args) {
        String url = "word_pairs.db";
        String fileName = "sentences.txt";
        WordPairDatabase wordPairDb = WordPairDatabase.of(url);
        wordPairDb.createTable();
        wordPairDb.processSentences(fileName);

        wordPairDb.bfsAndGetWords("apple", 3);
    }
}
