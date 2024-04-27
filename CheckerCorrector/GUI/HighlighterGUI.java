package GUI;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;

import DBinterface.DBinterface;
import DirectedGraph.BasicGraph;
import HashTableMaker.HashTableMaker;

import java.sql.SQLException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HighlighterGUI extends JFrame {
    private JTextArea textArea;
    private JButton highlightButton;
    public boolean isDutch;
    public HighlighterGUI(boolean isDutch) {
        this.isDutch = isDutch;
        setTitle("Text Highlighter");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        textArea = new JTextArea(10, 30);
        highlightButton = new JButton("Highlight");
        highlightButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                highlightPhrases();
            }
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(highlightButton);

        add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void highlightPhrases() {
        String text = textArea.getText();
        List<String> phrases = extractPhrases(text, 3);
        highlightPhrases(phrases);
    }

    private List<String> extractPhrases(String text, int phraseLength) {
        List<String> phrases = new ArrayList<>();
        String[] words = text.split("\\s+");
        for (int i = 0; i <= words.length - phraseLength; i+=1) {
            StringBuilder phraseBuilder = new StringBuilder();
            for (int j = 0; j < phraseLength; j++) {
                phraseBuilder.append(words[i + j]);
                if (j < phraseLength - 1) {
                    phraseBuilder.append(" ");
                }
            }
            phrases.add(phraseBuilder.toString());
        }
        return phrases;
    }

    private void highlightPhrases(List<String> phrases) {
        try{
            DBinterface dbInterface;
            HashTableMaker manager;
            if(!this.isDutch){
                dbInterface = new DBinterface("SQLite/token_database_english.db", "SQLite/smallDic.txt");
                manager = new HashTableMaker("SQLite/hash_database_english.db");
            }else{
                dbInterface = new DBinterface("SQLite/token_database_dutch.db", "SQLite/DutchTranslation.txt");
                manager = new HashTableMaker("SQLite/hash_database_dutch.db");
            }
            BasicGraph basicGraphClass = new BasicGraph();
            for (String phrase : phrases) {
                double conf = dbInterface.checkTokenInDatabase(phrase, basicGraphClass.getGraph())*0.8;   
                conf += manager.nGram(phrase, 3)*0.2;
                //System.out.println(phrase + "| "+ conf);
                highlightPhrase(phrase, (int)conf);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Color getColorForNumber(int number) {
        float hue = ((float) number / 100); // Hue ranges from 0 to 1
        //System.out.println(hue);
        return Color.getHSBColor(0, hue, 1);
    }

    private void highlightPhrase(String phrase, int colorInd) {
        Color color = getColorForNumber(colorInd);
        String text = textArea.getText();
        int index = text.indexOf(phrase);
        while (index >= 0) {
            try {
                textArea.getHighlighter().addHighlight(index, index + phrase.length(), new DefaultHighlighter.DefaultHighlightPainter(color));
            } catch (Exception e) {
                // Handle exception
            }
            index = text.indexOf(phrase, index + 1);
        }
    }
}
