import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.HashMap;
import java.util.List;

import HashTableMaker.HashTableMaker;
import util.PhraseExtractor;
import util.SentenceExtractor;

public class test {
    private Connection connection;

    public test(String databaseUrl) throws SQLException {
        connection = DriverManager.getConnection(databaseUrl);
        createTableIfNotExists();
    }

    private void createTableIfNotExists() throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS hashes (hash TEXT PRIMARY KEY, count INTEGER)");
        }
    }

    public HashMap<String, Integer> loadHashedSentences() throws SQLException {
        HashMap<String, Integer> hashedSentencesMap = new HashMap<>();

        String query = "SELECT hash, count FROM hashes";
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {
            while (resultSet.next()) {
                String hash = resultSet.getString("hash");
                int count = resultSet.getInt("count");
                hashedSentencesMap.put(hash, count);
            }
        }

        return hashedSentencesMap;
    }

    public void updateDatabase(String phrase) throws SQLException, NoSuchAlgorithmException {
        String hash = generateHash(phrase);

        PreparedStatement selectStatement = connection.prepareStatement("SELECT count FROM hashes WHERE hash = ?");
        selectStatement.setString(1, hash);
        ResultSet resultSet = selectStatement.executeQuery();

        if (resultSet.next()) {
            int count = resultSet.getInt("count");
            PreparedStatement updateStatement = connection.prepareStatement("UPDATE hashes SET count = ? WHERE hash = ?");
            updateStatement.setInt(1, count + 1);
            updateStatement.setString(2, hash);
            updateStatement.executeUpdate();
        } else {
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO hashes (hash, count) VALUES (?, ?)");
            insertStatement.setString(1, hash);
            insertStatement.setInt(2, 1);
            insertStatement.executeUpdate();
        }
    }

    private String generateHash(String phrase) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hashBytes = digest.digest(phrase.getBytes());

        StringBuilder hexString = new StringBuilder();
        for (byte b : hashBytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }

        return hexString.toString();
    }

    public void closeConnection() throws SQLException {
        if (connection != null) {
            connection.close();
        }
    }

    public static void main(String[] args) {
        try {
            SentenceExtractor extractor = SentenceExtractor.of(argPars.getFileName());
                List<String> extractedSentences = extractor.getSentences();  
                
                try {
                    HashTableMaker manager = new HashTableMaker("jdbc:sqlite:SQLite/hash_database.db");

                    // Loading hashed sentences from the database into a HashMap
                    manager.updateDatabase("This is a test phrase.");
                    manager.closeConnection();
                
                    for (String sentence : extractedSentences) {
                        manager.updateDatabase(sentence);
                        PhraseExtractor extractorPhrase = PhraseExtractor.fromSentence(sentence);
                        List<String> phrases = extractorPhrase.getPhrases();
                        for (String phrase : phrases) {
                            manager.updateDatabase(phrase);
                        }                        
                    }
                    manager.closeConnection();
                } catch (SQLException | NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}
