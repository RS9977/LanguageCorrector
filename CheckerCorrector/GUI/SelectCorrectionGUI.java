
package GUI;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SelectCorrectionGUI extends JFrame {
    private JPanel panel;
    private JPanel labelPanel;
    private JPanel applyPanel;
    private JPanel  mainPanel;
    private JButton applyButton;
    private JButton nextButton;
    private List<JButton> buttons;
    private JLabel counterLabel;
    private GUIListener listener;
    private static final int POPUP_WIDTH = 400;
    private static final int POPUP_HEIGHT = 200;
    

    public SelectCorrectionGUI(GUIListener listener, String label) {
        this.listener = listener;
        setTitle("Correction Suggestions");
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        panel = new JPanel();
        labelPanel = new JPanel(new GridLayout(1, 1));
        applyPanel = new JPanel(new GridLayout(1, 2));
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(POPUP_WIDTH, POPUP_HEIGHT));
        panel.setLayout(new FlowLayout());

        buttons = new ArrayList<>();
        updateCounter(label);
        try {
            File file = new File("correction_details.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                JButton button = new JButton(line);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton btn = (JButton) e.getSource();
                        btn.setBackground(Color.GREEN);
                    }
                });
                buttons.add(button);
                panel.add(button);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        applyButton = new JButton("Apply Corrections");
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                List<Boolean> flags = new ArrayList<>();
                for (JButton button : buttons) {
                    flags.add(button.getBackground().equals(Color.GREEN));
                }
                // Pass the flags and counter to Main using the interface method
                updateCounter(listener.updateFlagsAndLabel(flags));
                // Update buttons based on file changes
                updatePanel();
                
            }
        });


        nextButton = new JButton("Next Sentence");
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                
                String sentence = listener.loadNextSentece();
                if(sentence.equals("")){
                    JOptionPane.showMessageDialog(SelectCorrectionGUI.this, "All senteces have been reviewed and the corrected version has been written to corrected.txt!");
                    System.exit(0);
                }

                updateCounter(listener.loadNextSentece());
                // Update buttons based on file changes
                updatePanel();
                
            }
        });


        updatePanel();
        labelPanel.add(counterLabel);    
        applyPanel.add(applyButton);
        applyPanel.add(nextButton);
        mainPanel.add(labelPanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(applyPanel, BorderLayout.SOUTH);

        add(mainPanel);

        pack();
        setLocationRelativeTo(null); // Center the window

        //add(mainPanel);
        setVisible(true);
    }
    private void updateCounter(String label){
        counterLabel = new JLabel(label);
        counterLabel.setHorizontalAlignment(SwingConstants.CENTER);
    }

    private void updatePanel() {
        // Here you should update the buttons based on changes in the text file
        // For simplicity, I'm just removing all buttons and adding new ones
        panel.removeAll();
        labelPanel.removeAll();
        applyPanel.removeAll();
        mainPanel.removeAll();

        buttons.clear();
        try {
            File file = new File("correction_details.txt");
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                JButton button = new JButton(line);
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        JButton btn = (JButton) e.getSource();
                        btn.setBackground(Color.GREEN);
                    }
                });
                buttons.add(button);
                panel.add(button);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //panel.add(applyButton);
        //panel.add(counterLabel);
        panel.revalidate();
        panel.repaint();

        labelPanel.add(counterLabel);
        labelPanel.revalidate();
        labelPanel.repaint();
        
        applyPanel.add(applyButton);
        applyPanel.add(nextButton);
        applyPanel.revalidate();
        applyPanel.repaint();
        
        mainPanel.add(labelPanel, BorderLayout.NORTH);
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.add(applyPanel, BorderLayout.SOUTH);
        mainPanel.revalidate();
        mainPanel.repaint();
    }
}
