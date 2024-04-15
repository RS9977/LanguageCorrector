package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class WordRoleUpdater extends JFrame {
    private static final String DB_URL = "jdbc:sqlite:./SQLite/newdatabase.db";
    private static final String TABLE_NAME = "word_roles";
    private static final int POPUP_WIDTH = 300;
    private static final int POPUP_HEIGHT = 200;

    private Connection connection;
    private Map<String, String> wordRoles;
    private Map<String, Boolean> changes;
    private int currentIndex;
    private JLabel progressLabel;
    private JLabel wordLabel;
    private JLabel roleLabel;
    private JButton acceptButton;
    private JButton rejectButton;
    private JButton acceptAllButton;
    private JButton rejectAllButton;

    public WordRoleUpdater() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            loadWordRoles();
            initializeUI();
            updateWordLabel(); // Show the first word and its role
            updateRoleLabel();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadWordRoles() throws SQLException {
        wordRoles = new HashMap<>();
        changes = new HashMap<>();

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM " + TABLE_NAME);

        while (resultSet.next()) {
            String word = resultSet.getString("word");
            String role = resultSet.getString("role");
            wordRoles.put(word, role);
            changes.put(word, false);
        }

        statement.close();
    }

    private void initializeUI() {
        setTitle("Word Role Updater");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        progressLabel = new JLabel();
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateProgressLabel();

        wordLabel = new JLabel();
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateWordLabel();

        roleLabel = new JLabel();
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateRoleLabel();

        JPanel labelPanel = new JPanel(new GridLayout(3, 1));
        labelPanel.add(progressLabel);
        labelPanel.add(wordLabel);
        labelPanel.add(roleLabel);

        acceptButton = new JButton("Accept");
        rejectButton = new JButton("Reject");
        acceptAllButton = new JButton("Accept All");
        rejectAllButton = new JButton("Reject All");

        JPanel buttonPanel = new JPanel(new GridLayout(2, 2));
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(acceptAllButton);
        buttonPanel.add(rejectAllButton);

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changes.put(getCurrentWord(), true);
                updateNextWord();
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //wordRoles.remove(getCurrentWord()); // Remove the current word
                //changes.remove(getCurrentWord()); // Remove the change for the current word
                updateNextWord(); // Move to the next word
            }
        });

        acceptAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (String word : wordRoles.keySet()) {
                    changes.put(word, true);
                }
                updateDatabase(); // Update database before ending
                JOptionPane.showMessageDialog(WordRoleUpdater.this, "All changes accepted. Database updated.");
                System.exit(0);
            }
        });

        rejectAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                wordRoles.clear(); // Clear all word roles
                changes.clear(); // Clear all changes
                updateDatabase(); // Update database (clear the table)
                JOptionPane.showMessageDialog(WordRoleUpdater.this, "All changes rejected. Table cleared.");
                System.exit(0);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(POPUP_WIDTH, POPUP_HEIGHT));
        mainPanel.add(labelPanel, BorderLayout.NORTH);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);

        pack();
        setLocationRelativeTo(null); // Center the window
        setVisible(true);
    }

    private String getCurrentWord() {
        return (String) wordRoles.keySet().toArray()[currentIndex];
    }

    private void updateNextWord() {
        currentIndex++;
        if (currentIndex >= wordRoles.size()) {
            updateDatabase();
            JOptionPane.showMessageDialog(this, "No more changes to review.");
            System.exit(0);
        } else {
            updateWordLabel();
            updateRoleLabel(); 
            updateProgressLabel();
        }
    }

    private void updateProgressLabel() {
        progressLabel.setText((currentIndex + 1) + "/" + wordRoles.size());
    }

    private void updateWordLabel() {
        String word = getCurrentWord();
        wordLabel.setText("Word: " + word);
    }

    private void updateRoleLabel() {
        String word = getCurrentWord();
        String role = wordRoles.get(word);
        roleLabel.setText("Role: " + role);
    }

    private void updateDatabase() {
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate("DELETE FROM " + TABLE_NAME);
            PreparedStatement insertStatement = connection.prepareStatement("INSERT INTO " + TABLE_NAME + " (word, role) VALUES (?, ?)");

            for (Map.Entry<String, Boolean> entry : changes.entrySet()) {
                String word = entry.getKey();
                boolean accept = entry.getValue();

                if (accept) {
                    String role = wordRoles.get(word);
                    insertStatement.setString(1, word);
                    insertStatement.setString(2, role);
                    insertStatement.executeUpdate();
                }
            }

            statement.close();
            insertStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
