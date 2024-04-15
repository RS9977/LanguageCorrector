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
    private JLabel currentWordStatusLabel;
    private JButton acceptButton;
    private JButton rejectButton;
    private JButton acceptAllButton;
    private JButton rejectAllButton;
    private JButton backButton;
    private JButton nextButton;

    public WordRoleUpdater() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            loadWordRoles();
            initializeUI();
            updateLabels();
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
            changes.put(word, true);
        }

        statement.close();
    }

    private void initializeUI() {
        setTitle("Word Role Updater");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        progressLabel = new JLabel();
        progressLabel.setHorizontalAlignment(SwingConstants.CENTER);

        wordLabel = new JLabel();
        wordLabel.setHorizontalAlignment(SwingConstants.CENTER);

        roleLabel = new JLabel();
        roleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        currentWordStatusLabel = new JLabel();
        currentWordStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
       // updateCurrentStatus();

       updateLabels();
        JPanel labelPanel = new JPanel(new GridLayout(4, 1));
        labelPanel.add(progressLabel);
        labelPanel.add(wordLabel);
        labelPanel.add(roleLabel);
        labelPanel.add(currentWordStatusLabel);

        acceptButton = new JButton("Accept");
        rejectButton = new JButton("Reject");
        acceptAllButton = new JButton("Accept All");
        rejectAllButton = new JButton("Reject All");
        backButton = new JButton("Back");
        nextButton = new JButton("Next");

        JPanel buttonPanel = new JPanel(new GridLayout(3, 2));
        buttonPanel.add(acceptButton);
        buttonPanel.add(rejectButton);
        buttonPanel.add(acceptAllButton);
        buttonPanel.add(rejectAllButton);
        buttonPanel.add(backButton);
        buttonPanel.add(nextButton);


        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changes.put(getCurrentWord(), true);
                updateNextWord();
            }
        });

        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goNext();
            }
        });

        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goBack();
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changes.put(getCurrentWord(), false);
                //wordRoles.remove(getCurrentWord()); // Remove the current word
                //changes.remove(getCurrentWord()); // Remove the change for the current word
                updateNextWord(); // Move to the next word
            }
        });

        acceptAllButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
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

    private void goBack(){
        if(currentIndex>0){
            currentIndex--;
            updateLabels();
        }else
            JOptionPane.showMessageDialog(this, "Can not go back further");
    }

    private void goNext(){
        if (currentIndex < wordRoles.size()-1) {
            currentIndex++;
            updateLabels();
        } else {
            JOptionPane.showMessageDialog(this, "Can not go next further");
        }
    }

    private void updateNextWord() {
        currentIndex++;
        if (currentIndex >= wordRoles.size()) {
            updateDatabase();
            JOptionPane.showMessageDialog(this, "No more changes to review.");
            System.exit(0);
        } else {
            updateLabels();
        }
    }

    private void updateLabels(){
        progressLabel.setText((currentIndex + 1) + "/" + wordRoles.size());

        String word = getCurrentWord();
        wordLabel.setText("Word: " + word);

        String role = wordRoles.get(word);
        roleLabel.setText("Role: " + role);

        currentWordStatusLabel.setText("To be cleared? " + ((changes.get(word)? "No!": "Yes!")));
    }

    /*
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

   // private void updateCurrentStatus() {
  //      String word = getCurrentWord();
  //      currentWordStatusLabel.setText("To be cleared? " + ((changes.get(word)? "No!": "Yes!")));
  //  }
    */
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
